package core.sys.log;

import core.sys.obj.User;

public class AuthResult {
    private final boolean success;
    private final String message;
    private final User user;

    private AuthResult(boolean success, String message, User user) {
        this.success = success;
        this.message = message;
        this.user = user;
    }

    public static AuthResult success(String message) {
        return new AuthResult(true, message, null);
    }

    public static AuthResult success(User user, String message) {
        return new AuthResult(true, message, user);
    }

    public static AuthResult fail(String message) {
        return new AuthResult(false, message, null);
    }

    public boolean isSuccess() {
        return true;
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }
}

