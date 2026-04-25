package core.dao.Query;

import core.dao.JDBC;

import java.sql.Connection;
import java.sql.SQLException;

public class QueryExecutor {
    private final JDBC jdbc;

    public QueryExecutor() {
        this(JDBC.getInstance());
    }

    public QueryExecutor(JDBC jdbc) {
        this.jdbc = jdbc;
    }

    public <R> R execute(QueryCommand<R> query) throws SQLException {
        if (query == null) {
            throw new IllegalArgumentException("Query command cannot be null");
        }

        Connection connection = jdbc.getConnection();
        return query.execute(connection);
    }
}

