package entries;

import net.jini.core.entry.Entry;

public class LotEntry implements Entry {

    public Integer index;
    public String userSeller;
    public String title;
    public String description;
    public Double originalPrice;
    public Double soldPrice;
    public String status;
    public String userBuyer;

    public LotEntry() {
        // no arg constructor
    }

    public LotEntry(Integer index) {
        this.index = index;
    }

    public LotEntry(Integer index, String userSeller, String title, String description, Double originalPrice, Boolean isBid, Double soldPrice, String status, String userBuyer) {
        this.index = index;
        this.userSeller = userSeller;
        this.title = title;
        this.description = description;
        this.originalPrice = originalPrice;
        this.soldPrice = soldPrice;
        this.status = status;
        this.userBuyer = userBuyer;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
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

    public Double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public Double getSoldPrice() {
        return soldPrice;
    }

    public void setSoldPrice(Double soldPrice) {
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
        return "LotEntry{" +
                "index=" + index +
                ", userSeller='" + userSeller + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", originalPrice=" + originalPrice +
                ", soldPrice=" + soldPrice +
                ", status='" + status + '\'' +
                ", userBuyer='" + userBuyer + '\'' +
                '}';
    }
}
