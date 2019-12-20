package entries;

import net.jini.core.entry.Entry;

public class BidU1264982 implements Entry {

    public Integer index;
    public Integer lotIndex;
    public Double bidValue;
    public String userBuyer;

    public BidU1264982() {
        // no arg constructor
    }

    public BidU1264982(Integer lotIndex) {
        this.lotIndex = lotIndex;
    }

    public BidU1264982(Integer index, Integer lotIndex, Double bidValue, String userBuyer) {
        this.index = index;
        this.lotIndex = lotIndex;
        this.bidValue = bidValue;
        this.userBuyer = userBuyer;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Integer getLotIndex() {
        return lotIndex;
    }

    public void setLotIndex(Integer lotIndex) {
        this.lotIndex = lotIndex;
    }

    public Double getBidValue() {
        return bidValue;
    }

    public void setBidValue(Double bidValue) {
        this.bidValue = bidValue;
    }

    public String getUserBuyer() {
        return userBuyer;
    }

    public void setUserBuyer(String userBuyer) {
        this.userBuyer = userBuyer;
    }

    @Override
    public String toString() {
        return "BidEntry{" +
                "index=" + index +
                ", lotIndex=" + lotIndex +
                ", bidValue=" + bidValue +
                ", userBuyer='" + userBuyer + '\'' +
                '}';
    }
}
