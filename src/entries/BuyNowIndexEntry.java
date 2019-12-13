package entries;

import net.jini.core.entry.Entry;

public class BuyNowIndexEntry implements Entry {

    public Integer index;

    public BuyNowIndexEntry() {
        // no arg constructor
    }

    public BuyNowIndexEntry(Integer index) {
        this.index = index;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public void increment(){
        index++;
    }

    public void decrement() {
        index--;
    }

    @Override
    public String toString() {
        return "BuyNowIndexEntry{" +
                "index=" + index +
                '}';
    }
}
