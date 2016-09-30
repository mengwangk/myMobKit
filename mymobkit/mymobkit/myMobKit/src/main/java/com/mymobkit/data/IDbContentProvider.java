package com.mymobkit.data;

public interface IDbContentProvider {

	public enum Field {

		ID("id"),
		NAME("name"),
		VALUE("value"),
		MODULE("module"),
		DESCRIPTION("description"),
		CONFIGURABLE("configurable"),
		DATE_CREATED("date_created"),
		DATE_MODIFIED("date_modified"),
		TIMESTAMP("timestamp"),
		ACTION("action");


		private final String id;

		private Field(String id) {

			this.id = id;
		}

		public String getId() {

			return id;
		}
	}
}
