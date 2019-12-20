package entries;

import net.jini.core.entry.Entry;

public class UserIndexU1264982 implements Entry {

    public Integer index;

    public UserIndexU1264982() {
        // no arg constructor
    }

    public UserIndexU1264982(Integer index) {
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
        return "UserIndex{" +
                "index=" + index +
                '}';
    }
}
