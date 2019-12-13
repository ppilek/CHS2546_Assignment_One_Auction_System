package entries;

import net.jini.core.entry.Entry;

public class LotEntry implements Entry {

    public Integer index;
    public String userSeller;
    public String title;
    public String description;
    public Double price;

    public LotEntry() {
        // no arg constructor
    }

    public LotEntry(Integer index, String userSeller, String title, String description, Double price) {
        this.index = index;
        this.userSeller = userSeller;
        this.title = title;
        this.description = description;
        this.price = price;
    }

    public LotEntry(Integer index) {
        this.index = index;
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "LotEntry{" +
                "index=" + index +
                ", userSeller='" + userSeller + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                '}';
    }
}
