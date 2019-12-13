package entries;

import net.jini.core.entry.Entry;

public class BidIndexEntry implements Entry {

    public Integer index;

    public BidIndexEntry() {
        // no arg constructor
    }

    public BidIndexEntry(Integer index) {
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
        return "BidIndexEntry{" +
                "index=" + index +
                '}';
    }
}
