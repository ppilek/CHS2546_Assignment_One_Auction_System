package models;

public class User {

    public int index;
    public String username, password;
    public boolean isSingIn;

    public User() {

    }

    public User(int index, String username, String password, boolean isSingIn) {
        this.index = index;
        this.username = username;
        this.password = password;
        this.isSingIn = isSingIn;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
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

    public boolean isSingIn() {
        return isSingIn;
    }

    public void setSingIn(boolean singIn) {
        isSingIn = singIn;
    }

    @Override
    public String toString() {
        return "User{" +
                "index=" + index +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", isSingIn=" + isSingIn +
                '}';
    }
}
