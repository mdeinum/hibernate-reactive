/* Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.reactive.testing;


import org.hibernate.reactive.containers.DatabaseConfiguration;
import org.hibernate.reactive.containers.DatabaseConfiguration.DBType;

import org.junit.Rule;
import org.junit.Test;

import org.assertj.core.api.Assertions;

public class DatabaseSelectionRuleTests {

	public static class SkipDBTest {
		@Rule
		public DatabaseSelectionRule rule = DatabaseSelectionRule.skipTestsFor( DBType.POSTGRESQL );

		@Test
		public void shouldSkipPostgres() {
			Assertions.assertThat( DatabaseConfiguration.dbType() ).isNotEqualTo( DBType.POSTGRESQL );
		}
	}

	public static class SkipMultipleDBsTest {
		@Rule
		public DatabaseSelectionRule rule = DatabaseSelectionRule.skipTestsFor( DBType.POSTGRESQL, DBType.MYSQL );

		@Test
		public void shouldSkipPostgresAndMySQL() {
			Assertions.assertThat( DatabaseConfiguration.dbType() )
					.isNotIn( DBType.POSTGRESQL, DBType.MYSQL );
		}
	}

	public static class RunOnlyOnDBTest {
		@Rule
		public DatabaseSelectionRule rule = DatabaseSelectionRule.runOnlyFor( DBType.POSTGRESQL );

		@Test
		public void shouldOnlyRunForPostgres() {
			Assertions.assertThat( DatabaseConfiguration.dbType() ).isEqualTo( DBType.POSTGRESQL );
		}
	}

	public static class RunOnlyOnDMultipleDBsTest {
		@Rule
		public DatabaseSelectionRule rule = DatabaseSelectionRule.runOnlyFor( DBType.POSTGRESQL, DBType.MYSQL );

		@Test
		public void shouldOnlyRunForPostgresOrMySql() {
			Assertions.assertThat( DatabaseConfiguration.dbType() ).isIn( DBType.POSTGRESQL, DBType.MYSQL );
		}
	}
}
