package core.dao.Query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class ModifyQuery extends AbstractQuery<Integer> {
    protected ModifyQuery(String sql, Object... parameters) {
        super(sql, parameters);
    }

    @Override
    public Integer execute(Connection connection) throws SQLException {
        try (PreparedStatement statement = createStatement(connection)) {
            return statement.executeUpdate();
        }
    }
}

