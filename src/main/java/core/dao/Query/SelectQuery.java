package core.dao.Query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SelectQuery<T> extends AbstractQuery<List<T>> {
    private final RowMapper<T> rowMapper;

    public SelectQuery(String sql, RowMapper<T> rowMapper, Object... parameters) {
        super(sql, parameters);
        this.rowMapper = rowMapper;
    }

    @Override
    public List<T> execute(Connection connection) throws SQLException {
        if (rowMapper == null) {
            throw new IllegalArgumentException("RowMapper is required for select queries");
        }

        List<T> rows = new ArrayList<>();
        try (PreparedStatement statement = createStatement(connection);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                rows.add(rowMapper.map(resultSet));
            }
        }
        return rows;
    }
}

