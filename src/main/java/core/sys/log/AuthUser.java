package core.sys.log;

import core.sys.obj.User;

public class AuthUser {
    private final String id;
    private final String username;
    private final String email;
    private final String passwordHash;

    public AuthUser(String id, String username, String email, String passwordHash) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public User toSystemUser() {
        return new User(username, id, email);
    }
}

