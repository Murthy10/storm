package bolt;

import com.google.gson.*;

import comparator.IntegerValueComparator;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.IBasicBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.*;

public class UserCountBolt implements IBasicBolt {

    private Map<String, Integer> counts = new HashMap<>();

    @Override
    public void prepare(Map map, TopologyContext topologyContext) {

    }

    public void execute(Tuple tuple, BasicOutputCollector basicOutputCollector) {
        JsonObject json = (JsonObject) tuple.getValue(0);
        try {
            if (json.has("osm")) {
                JsonObject osm = json.getAsJsonObject("osm");
                JsonArray actions = osm.getAsJsonArray("action");
                for (JsonElement actionElement : actions) {
                    JsonObject action = actionElement.getAsJsonObject();
                    if (action.has("node")) {
                        JsonObject node = action.getAsJsonObject("node");
                        if (node.has("@user")) {
                            JsonPrimitive user = node.getAsJsonPrimitive("@user");
                            count(user.getAsString());
                        }
                    }
                }
            }
            List<Map.Entry<String, Integer>> list = new LinkedList<>(counts.entrySet());
            Collections.sort(list, new IntegerValueComparator<>());
            String results = "[";
            for (Map.Entry<String, Integer> entry : getResultList()) {
                String key = entry.getKey();
                Integer value = entry.getValue();
                results += "{\"name\":\"" + key + "\" \"count\":" + value.toString() + "},";
            }
            results = results.substring(0, results.length() - 1);
            results += "]";
            basicOutputCollector.emit(new Values(results));
        } catch (Exception e) {
            System.out.println("Something went wrong!");
            System.out.println(e);
        }

    }

    private void count(String text) {
        Integer count = counts.get(text);
        if (count == null)
            count = 0;
        count++;
        counts.put(text, count);
    }


    private List<Map.Entry<String, Integer>> getResultList() {
        List<Map.Entry<String, Integer>> list = new LinkedList<>(counts.entrySet());
        Collections.sort(list, new IntegerValueComparator<>());
        return list;
    }

    @Override
    public void cleanup() {
        for (Map.Entry<String, Integer> entry : getResultList()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            System.out.println(key + ": " + value.toString());
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("message"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
