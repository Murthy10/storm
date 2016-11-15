package helper;

import comparator.IntegerValueComparator;

import java.io.Serializable;
import java.util.*;

public class BoltHelper implements Serializable {
    private Map<String, Integer> counts;

    public BoltHelper() {
        counts = new HashMap<>();
    }

    public List<Map.Entry<String, Integer>> getResultList() {
        List<Map.Entry<String, Integer>> entries = new LinkedList<>(counts.entrySet());
        Collections.sort(entries, new IntegerValueComparator<>());
        return entries;
    }

    public void print() {
        for (Map.Entry<String, Integer> entry : getResultList()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            System.out.println(key + ": " + value.toString());
        }

    }

    public void count(String text) {
        Integer count = counts.get(text);
        if (count == null)
            count = 0;
        count++;
        counts.put(text, count);
    }

    public String getReturnMessage(int numberOfElements) {
        List<Map.Entry<String, Integer>> entries = getResultList();
        if (numberOfElements > entries.size()) {
            numberOfElements = entries.size();
        }
        String result = "[";
        for (int i = entries.size() - 1; i >= entries.size() - numberOfElements; i--) {
            String key = entries.get(i).getKey();
            Integer value = entries.get(i).getValue();
            result += "{\"key\":\"" + key + "\", \"value\":" + value.toString() + "},";
        }
        result = result.substring(0, result.length() - 1);
        result += "]";
        return result;
    }

}
