/* Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.reactive.pool.impl;

import org.hibernate.engine.jdbc.spi.SqlStatementLogger;

import io.vertx.sqlclient.Pool;

/**
 * A pool of reactive connections backed by a Vert.x {@link Pool}.
 * <p>
 * This is an alternative to
 * {@link org.hibernate.reactive.pool.impl.DefaultSqlClientPool},
 * for use when one doesn't want Hibernate Reactive to manage the
 * lifecycle of the underlying {@code Pool}.
 * <p>
 * This implementation is meant to be used in Quarkus.
 */
public final class ExternalSqlClientPool extends SqlClientPool {

	private final Pool pool;
	private final SqlStatementLogger sqlStatementLogger;
	private final boolean usePostgresStyleParameters;

	public ExternalSqlClientPool(Pool pool, SqlStatementLogger sqlStatementLogger, boolean usePostgresStyleParameters) {
		this.pool = pool;
		this.sqlStatementLogger = sqlStatementLogger;
		this.usePostgresStyleParameters = usePostgresStyleParameters;
	}

	@Override
	protected Pool getPool() {
		return pool;
	}

	@Override
	protected SqlStatementLogger getSqlStatementLogger() {
		return sqlStatementLogger;
	}

	@Override
	protected boolean usePostgresStyleParameters() {
		return usePostgresStyleParameters;
	}

}
