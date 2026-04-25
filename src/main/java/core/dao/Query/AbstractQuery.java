package core.dao.Query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class AbstractQuery<R> implements QueryCommand<R> {
    private final String sql;
    private final Object[] parameters;

    protected AbstractQuery(String sql, Object... parameters) {
        if (sql == null || sql.isBlank()) {
            throw new IllegalArgumentException("SQL cannot be null or blank");
        }
        this.sql = sql;
        this.parameters = parameters == null ? new Object[0] : parameters;
    }

    protected PreparedStatement createStatement(Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(sql);
        for (int i = 0; i < parameters.length; i++) {
            statement.setObject(i + 1, parameters[i]);
        }
        return statement;
    }

    protected String getSql() {
        return sql;
    }
}

