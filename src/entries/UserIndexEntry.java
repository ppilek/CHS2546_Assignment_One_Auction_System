package entries;

import net.jini.core.entry.Entry;

public class UserIndexEntry implements Entry {

    public Integer index;

    public UserIndexEntry() {
        // no arg constructor
    }

    public UserIndexEntry(Integer index) {
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
        return "UserIndexEntry{" +
                "index=" + index +
                '}';
    }
}
