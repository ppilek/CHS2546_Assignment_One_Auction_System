package entries;

import net.jini.core.entry.Entry;

public class BuyNowEntry implements Entry {

    public Integer index;
    public Boolean isResponse;
    public String sellerUserName;
    public Integer lotIndex;
    public String buyerUserName;
    public Boolean isAccepted;

    public BuyNowEntry() {
        // no arg constructor
    }

    public BuyNowEntry(Integer index) {
        this.index = index;
    }

    public BuyNowEntry(Boolean isResponse) {
        this.isResponse = isResponse;
    }

    public BuyNowEntry(Boolean isResponse, String sellerUserName) {
        this.isResponse = isResponse;
        this.sellerUserName = sellerUserName;
    }

    public BuyNowEntry(Integer index, Boolean isResponse, String sellerUserName, Integer lotIndex, String buyerUserName, Boolean isAccepted) {
        this.index = index;
        this.isResponse = isResponse;
        this.sellerUserName = sellerUserName;
        this.lotIndex = lotIndex;
        this.buyerUserName = buyerUserName;
        this.isAccepted = isAccepted;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Boolean getResponse() {
        return isResponse;
    }

    public void setResponse(Boolean response) {
        isResponse = response;
    }

    public String getSellerUserName() {
        return sellerUserName;
    }

    public void setSellerUserName(String sellerUserName) {
        this.sellerUserName = sellerUserName;
    }

    public Integer getLotIndex() {
        return lotIndex;
    }

    public void setLotIndex(Integer lotIndex) {
        this.lotIndex = lotIndex;
    }

    public String getBuyerUserName() {
        return buyerUserName;
    }

    public void setBuyerUserName(String buyerUserName) {
        this.buyerUserName = buyerUserName;
    }

    public Boolean getAccepted() {
        return isAccepted;
    }

    public void setAccepted(Boolean accepted) {
        isAccepted = accepted;
    }

    @Override
    public String toString() {
        return "BuyNowEntry{" +
                "index=" + index +
                ", isResponse=" + isResponse +
                ", sellerUserName='" + sellerUserName + '\'' +
                ", lotIndex=" + lotIndex +
                ", buyerUserName='" + buyerUserName + '\'' +
                ", isAccepted=" + isAccepted +
                '}';
    }
}
