package core.sys.obj;

public class Item extends SystemObject{
    String name;
    User user;

    public Item(String name, Integer id, User user) {
        super(name, id);
        this.user = user;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
}
