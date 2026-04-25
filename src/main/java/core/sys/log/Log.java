package core.sys.log;

import core.dao.DBUser;
import core.dao.JDBC;
import core.dao.Query.QueryExecutor;

import java.sql.SQLException;

public class Log {
    private final Login login;
    private final Signup signup;

    public Log(String databaseName) {
        JDBC jdbc = JDBC.getInstance();
        try {
            jdbc.connect(databaseName);
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to connect to database: username: "+ DBUser.getInstance().getUsername()+ " password: " + DBUser.getInstance().getPassword(), exception);
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

