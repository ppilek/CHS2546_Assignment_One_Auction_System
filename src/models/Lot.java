package models;

public class Lot {

    String index, userSeller, title, description, originalPrice, soldPrice, status, userBuyer;

    public Lot(String index, String userSeller, String title, String description, String originalPrice, String soldPrice, String status, String userBuyer) {
        this.index = index;
        this.userSeller = userSeller;
        this.title = title;
        this.description = description;
        this.originalPrice = originalPrice;
        this.soldPrice = soldPrice;
        this.status = status;
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

    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getSoldPrice() {
        return soldPrice;
    }

    public void setSoldPrice(String soldPrice) {
        this.soldPrice = soldPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
                ", originalPrice='" + originalPrice + '\'' +
                ", soldPrice='" + soldPrice + '\'' +
                ", status='" + status + '\'' +
                ", userBuyer='" + userBuyer + '\'' +
                '}';
    }
}
