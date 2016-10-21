package comparator;

import java.util.Comparator;
import java.util.Map;

public class IntegerValueComparator<K> implements Comparator<Map.Entry<K, Integer>> {
    @Override
    public int compare(Map.Entry<K, Integer> entry1, Map.Entry<K, Integer> entry2) {
        return entry1.getValue().compareTo(entry2.getValue());
    }
}
