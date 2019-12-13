package entries;

import net.jini.core.entry.Entry;

public class LotIndexEntry implements Entry {

    public Integer index;

    public LotIndexEntry(){
        // no arg constructor
    }

    public LotIndexEntry(int index){
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
        return "LotIndexEntry{" +
                "index=" + index +
                '}';
    }
}
