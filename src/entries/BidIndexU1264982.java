package entries;

import net.jini.core.entry.Entry;

public class BidIndexU1264982 implements Entry {

    public Integer index;

    public BidIndexU1264982() {
        // no arg constructor
    }

    public BidIndexU1264982(Integer index) {
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

    @Override
    public String toString() {
        return "BidIndex{" +
                "index=" + index +
                '}';
    }
}
