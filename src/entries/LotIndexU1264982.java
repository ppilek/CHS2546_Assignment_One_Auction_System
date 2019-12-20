package entries;

import net.jini.core.entry.Entry;

public class LotIndexU1264982 implements Entry {

    public Integer index;

    public LotIndexU1264982(){
        // no arg constructor
    }

    public LotIndexU1264982(int index){
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
        return "LotIndex{" +
                "index=" + index +
                '}';
    }
}
