package dmanlancers.com.flightplanner.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Login extends RealmObject {

    @PrimaryKey
    private int id;
    private String username;
    private String password;
    private String email;

    public Login() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
