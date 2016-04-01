import java.util.Map;
import java.util.LinkedHashMap;

public class Answer {

    private LinkedHashMap<String, Double> items;

    public Answer() {
        items = new LinkedHashMap();
    }

    public void addItem(String key, double value) {
        items.put(key, value);
    }

    public String toString(boolean integer) {
        String string = "";

        for (Map.Entry<String, Double> item : items.entrySet()) {
            string += item.getKey() + " = ";
            if (integer)
                string += (int) Math.floor(item.getValue());
            else
                string += item.getValue();
            string += "\n";
        }

        return string;
    }

    public String toString() {
        return toString(false);
    }

}
