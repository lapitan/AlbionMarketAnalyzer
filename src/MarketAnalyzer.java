import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class MarketAnalyzer {

    private TreeMap<Float,Item> itemTop;

    public MarketAnalyzer() {
        itemTop=new TreeMap<>(Collections.reverseOrder());
    }

    void put(Item item){
        itemTop.put(item.getRentability(),item);
    }

    public TreeMap<Float, Item> getItemTop() {
        return itemTop;
    }

}
