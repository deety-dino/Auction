package core.sys.obj;

import java.util.ArrayList;

public class User extends SystemObject{
    String email;
    ArrayList<Item> items;

    public User(String name, Integer id, String email) {
        super(name, id);
        this.email = email;
        this.items = new ArrayList<Item>();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }
}
