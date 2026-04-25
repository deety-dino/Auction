package core.dao.Query;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface QueryCommand<R> {
    R execute(Connection connection) throws SQLException;
}

