package core.dao;

public class DBUser {
    private String username;
    private String password;
    private static DBUser dbUser;

    private DBUser() {}
    public static DBUser getInstance() {
        if (dbUser == null) {
            dbUser = new DBUser();
        }
        return dbUser;
    }
    public String getUsername() {
        return this.username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
