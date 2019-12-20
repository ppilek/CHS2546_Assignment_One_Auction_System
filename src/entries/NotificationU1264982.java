package entries;

import net.jini.core.entry.Entry;

public class NotificationU1264982 implements Entry {

    public Integer lotIndex;
    public String lotSeller;
    public String lotTitle;
    public String lotDescription;
    public Double lotPrice;
    public Boolean isBidAccepted;
    public String lotBuyer;

    public NotificationU1264982() {
        // no arg constructor
    }

    public NotificationU1264982(Integer lotIndex, String lotSeller, String lotTitle, String lotDescription, Double lotPrice, Boolean isBidAccepted, String lotBuyer) {
        this.lotIndex = lotIndex;
        this.lotSeller = lotSeller;
        this.lotTitle = lotTitle;
        this.lotDescription = lotDescription;
        this.lotPrice = lotPrice;
        this.isBidAccepted = isBidAccepted;
        this.lotBuyer = lotBuyer;
    }

    public Integer getLotIndex() {
        return lotIndex;
    }

    public void setLotIndex(Integer lotIndex) {
        this.lotIndex = lotIndex;
    }

    public String getLotSeller() {
        return lotSeller;
    }

    public void setLotSeller(String lotSeller) {
        this.lotSeller = lotSeller;
    }

    public String getLotTitle() {
        return lotTitle;
    }

    public void setLotTitle(String lotTitle) {
        this.lotTitle = lotTitle;
    }

    public String getLotDescription() {
        return lotDescription;
    }

    public void setLotDescription(String lotDescription) {
        this.lotDescription = lotDescription;
    }

    public Double getLotPrice() {
        return lotPrice;
    }

    public void setLotPrice(Double lotPrice) {
        this.lotPrice = lotPrice;
    }

    public Boolean getBidAccepted() {
        return isBidAccepted;
    }

    public void setBidAccepted(Boolean bidAccepted) {
        isBidAccepted = bidAccepted;
    }

    public String getLotBuyer() {
        return lotBuyer;
    }

    public void setLotBuyer(String lotBuyer) {
        this.lotBuyer = lotBuyer;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "lotIndex=" + lotIndex +
                ", lotSeller='" + lotSeller + '\'' +
                ", lotTitle='" + lotTitle + '\'' +
                ", lotDescription='" + lotDescription + '\'' +
                ", lotPrice=" + lotPrice +
                ", isBidAccepted=" + isBidAccepted +
                ", lotBuyer='" + lotBuyer + '\'' +
                '}';
    }
}
