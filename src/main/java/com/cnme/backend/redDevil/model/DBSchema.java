package com.cnme.backend.redDevil.model;

public class DBSchema {
	public final String Name;
	public final String[] ColumnKeys;
	public final String[] PrimaryKeys;
	public final boolean IsStoredProcedures;
	public String[] QuerySets;
	public String FilterCondition;

	public DBSchema(Builder builder) {
		this.Name = builder.Name;
		this.ColumnKeys = builder.ColumnKeys;
		this.PrimaryKeys = builder.PrimaryKeys;
		this.IsStoredProcedures = builder.IsStoredProcedures;
		this.QuerySets = builder.QuerySets == null ? new String[] { "*" }
				: builder.QuerySets;
		this.FilterCondition = builder.FilterCondition;
	}

	public void updateQuerySets(String[] querySets) {
		this.QuerySets = querySets;
	}

	public void updateFilterCondition(String condition) {
		this.FilterCondition = condition;
	}

	public static class Builder {
		private final String Name;
		private final String[] ColumnKeys;
		private final String[] PrimaryKeys;
		private final boolean IsStoredProcedures;
		private String[] QuerySets;
		private String FilterCondition;

		public Builder(String name, String[] columnKeys, String[] primaryKeys,
				boolean isStoredProcedures) {
			this.Name = name;
			this.ColumnKeys = columnKeys;
			this.PrimaryKeys = primaryKeys;
			this.IsStoredProcedures = isStoredProcedures;
		}

		public Builder QuerySets(String[] querySets) {
			this.QuerySets = querySets;
			return this;
		}

		public Builder FilterCondition(String condition) {
			this.FilterCondition = condition;
			return this;
		}

		public DBSchema build() {
			return new DBSchema(this);
		}

	}
}
