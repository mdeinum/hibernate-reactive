/* Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.reactive.provider.service;

import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.reactive.pool.ReactiveConnection;
import org.hibernate.reactive.pool.ReactiveConnectionPool;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.schema.internal.exec.GenerationTarget;
import org.hibernate.tool.schema.internal.exec.GenerationTargetToDatabase;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletionStage;

/**
 * Adaptor that redirects DDL generated by the schema export
 * tool to the reactive connection.
 *
 * @author Gavin King
 */
public class ReactiveGenerationTarget implements GenerationTarget {
	private final ServiceRegistry registry;
	private CompletionStage<ReactiveConnection> commands;
	private Set<String> statements;

	CoreMessageLogger log = CoreLogging.messageLogger( GenerationTargetToDatabase.class );

	public ReactiveGenerationTarget(ServiceRegistry registry) {
		this.registry = registry;
	}

	@Override
	public void prepare() {
		commands = registry.getService( ReactiveConnectionPool.class ).getConnection();
		statements = new HashSet<>();
	}

	@Override
	public void accept(String command) {
		// avoid executing duplicate DDL statements
		// (hack specifically to avoid multiple
		// inserts into a sequence emulation table)
		if ( statements.add( command ) ) {
			commands = commands.thenCompose(
					connection -> connection.execute( command )
							.handle( (r, e) -> {
								if ( e != null ) {
									log.warnf("HRX000021: DDL command failed [%s]", e.getMessage() );
								}
								return null;
							} )
							.thenApply( v -> connection )
			);
		}
	}

	@Override
	public void release() {
		statements = null;
		if ( commands != null ) {
			commands.whenComplete( (c, e) -> c.close() )
					.toCompletableFuture()
					.join();
		}
	}
}
