package core.sys.obj;

import java.util.ArrayList;

public class User extends SystemObject{
    String email;

    public User(String name, String id, String email) {
        super(name, id);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

}
