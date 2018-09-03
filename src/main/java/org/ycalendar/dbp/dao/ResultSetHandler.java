package org.ycalendar.dbp.dao;

import java.sql.ResultSet;

public interface ResultSetHandler<T> {
	T handle(ResultSet rs);
}
