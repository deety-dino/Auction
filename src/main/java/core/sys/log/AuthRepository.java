package core.sys.log;

import core.dao.Query.QueryExecutor;
import core.dao.Query.QueryFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AuthRepository {
    private final QueryExecutor queryExecutor;

    public AuthRepository(QueryExecutor queryExecutor) {
        this.queryExecutor = queryExecutor;
    }

    public Optional<AuthUser> findByUsername(String username) throws SQLException {
        List<AuthUser> rows = queryExecutor.execute(QueryFactory.select(
                "SELECT user_id, username, email, password FROM users WHERE username = ? LIMIT 1",
                resultSet -> new AuthUser(
                        resultSet.getInt("user_id"),
                        resultSet.getString("username"),
                        resultSet.getString("email"),
                        resultSet.getString("password")
                ),
                username
        ));

        if (rows.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(rows.get(0));
    }

    public boolean existsByUsernameOrEmail(String username, String email) throws SQLException {
        List<Integer> rows = queryExecutor.execute(QueryFactory.select(
                "SELECT user_id FROM users WHERE username = ? OR email = ? LIMIT 1",
                resultSet -> resultSet.getInt("user_id"),
                username,
                email
        ));
        return !rows.isEmpty();
    }

    public int createUser(String username, String email, String password) throws SQLException {
        return queryExecutor.execute(QueryFactory.insert(
                "INSERT INTO users(username, email, password) VALUES (?, ?, ?)",
                username,
                email,
                password
        ));
    }
}

