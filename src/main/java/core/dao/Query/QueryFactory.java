package core.dao.Query;

public final class QueryFactory {
    private QueryFactory() {
    }

    public static <T> SelectQuery<T> select(String sql, RowMapper<T> mapper, Object... parameters) {
        return new SelectQuery<>(sql, mapper, parameters);
    }

    public static InsertQuery insert(String sql, Object... parameters) {
        return new InsertQuery(sql, parameters);
    }

    public static UpdateQuery update(String sql, Object... parameters) {
        return new UpdateQuery(sql, parameters);
    }

    public static DeleteQuery delete(String sql, Object... parameters) {
        return new DeleteQuery(sql, parameters);
    }
}

