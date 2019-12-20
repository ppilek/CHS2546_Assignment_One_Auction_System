package models;

public class Bid {

    public String index, bidValue, user;

    public Bid(String index, String bidValue, String user) {
        this.index = index;
        this.bidValue = bidValue;
        this.user = user;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getBidValue() {
        return bidValue;
    }

    public void setBidValue(String bidValue) {
        this.bidValue = bidValue;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Bid{" +
                "index='" + index + '\'' +
                ", bidValue='" + bidValue + '\'' +
                ", user='" + user + '\'' +
                '}';
    }
}
