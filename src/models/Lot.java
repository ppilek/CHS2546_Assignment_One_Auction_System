package models;

public class Lot {

    String index, userSeller, title, description, price, userBuyer;

    public Lot(String index, String userSeller, String title, String description, String price, String userBuyer) {
        this.index = index;
        this.userSeller = userSeller;
        this.title = title;
        this.description = description;
        this.price = price;
        this.userBuyer = userBuyer;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getUserSeller() {
        return userSeller;
    }

    public void setUserSeller(String userSeller) {
        this.userSeller = userSeller;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUserBuyer() {
        return userBuyer;
    }

    public void setUserBuyer(String userBuyer) {
        this.userBuyer = userBuyer;
    }

    @Override
    public String toString() {
        return "Lot{" +
                "index='" + index + '\'' +
                ", userSeller='" + userSeller + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", price='" + price + '\'' +
                ", userBuyer='" + userBuyer + '\'' +
                '}';
    }
}
