package core.sys.log;

import core.dao.JDBC;
import core.dao.Query.QueryExecutor;

import java.sql.SQLException;

public class Log {
    private final Login login;
    private final Signup signup;

    public Log(String databasePath) {
        JDBC jdbc = JDBC.getInstance();
        try {
            jdbc.connect(databasePath);
        } catch (SQLException exception) {
            throw new IllegalStateException(
                "Unable to connect to SQLite database at: " + jdbc.getResolvedDatabasePath(),
                exception
            );
        }

        AuthRepository repository = new AuthRepository(new QueryExecutor(jdbc));
        this.login = new Login(repository);
        this.signup = new Signup(repository);
    }

    public AuthResult login(String username, String password) {
        return login.execute(new LoginRequest(username, password));
    }

    public AuthResult signup(String username, String email, String password) {
        return signup.execute(new SignupRequest(username, email, password));
    }
}

