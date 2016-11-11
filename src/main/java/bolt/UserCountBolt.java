package bolt;

import com.google.gson.*;

import comparator.IntegerValueComparator;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.IBasicBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.*;
import java.util.stream.Collectors;

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
            JsonParser jsonParser = new JsonParser();
            List<Map.Entry<String, Integer>> list = new LinkedList<>(counts.entrySet());
            Collections.sort(list, new IntegerValueComparator<>());

            JsonArray results = new JsonArray();
            for (Map.Entry<String, Integer> entry : getResultList()) {
                String key = entry.getKey();
                Integer value = entry.getValue();
                JsonElement result = (JsonElement) jsonParser.parse("{\""+key+"\":"+value.toString()+"}");
                results.add(result);
            }
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


    private void printKeys(JsonObject jsonObject) {
        List<String> keys = jsonObject.entrySet()
                .stream()
                .map(i -> i.getKey())
                .collect(Collectors.toCollection(ArrayList::new));
        keys.forEach(System.out::println);

    }

    private List<Map.Entry<String, Integer>> getResultList(){
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

    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
