package bolt;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import comparator.IntegerValueComparator;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.IBasicBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.*;

public class ObjectsCountBolt implements IBasicBolt {
    private Map<String, Integer> counts = new HashMap<>();
    private List<String> mostRelevantNodes = Arrays.asList("amenity=bench", "amenity=drinking_water", "amenity=parking", "amenity=restaurant", "highway=crossing", "information=guidepost", "natural=peak", "natural=tree", "tourism=information", "tourism=picnic_site");
    private List<String> mostRelevantWays = Arrays.asList("building=house", "building=residential", "building=yes", "highway=footway", "highway=path", "highway=residential", "highway=service", "highway=track", "landuse=forest", "waterway=stream");

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
                    //"tag": [{"@k": "name", "@v": "Capanne-Prato-Cinquale"}, {"@k": "railway", "@v": "halt"}]}},
                    if (action.has("node")) {
                        JsonObject node = action.getAsJsonObject("node");
                        if (node.has("tag")) {
                            JsonElement jsonTag = node.get("tag");
                            if (jsonTag.isJsonArray()) {
                                JsonArray tags = jsonTag.getAsJsonArray();
                                for (JsonElement tagElement : tags) {
                                    if (tags.isJsonObject()) {
                                        JsonObject tag = tagElement.getAsJsonObject();
                                        countByTag(tag);
                                    }
                                }
                            } else {
                                if (jsonTag.isJsonObject()) {
                                    countByTag(jsonTag.getAsJsonObject());
                                } else {
                                    System.out.print(jsonTag.toString());
                                }
                            }

                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Something went wrong!");
            System.out.println(e);
        }
    }

    private void countByTag(JsonObject tag) {
        if (tag.has("@k") && tag.has("@v")) {
            JsonPrimitive key = tag.getAsJsonPrimitive("@k");
            JsonPrimitive value = tag.getAsJsonPrimitive("@v");
            count(key.getAsString() + "=" + value.getAsString());
        }
    }

    private void count(String text) {
        Integer count = counts.get(text);
        if (count == null)
            count = 0;
        count++;
        counts.put(text, count);
    }


    @Override
    public void cleanup() {
        List<Map.Entry<String, Integer>> list = new LinkedList<>(counts.entrySet());
        Collections.sort(list, new IntegerValueComparator<>());
        for (Map.Entry<String, Integer> entry : list) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            System.out.println(key + ": " + value.toString());
        }
        printMostRelevanted(list, mostRelevantNodes, "Most relevant nodes: ");
        printMostRelevanted(list, mostRelevantWays, "Most relevant ways: ");


    }

    private void printMostRelevanted(List<Map.Entry<String, Integer>> list, List<String> relevant, String text) {
        System.out.println();
        System.out.println(text);
        System.out.println();

        for (Map.Entry<String, Integer> entry : list) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            if (relevant.contains(key)) {
                System.out.println(key + ": " + value.toString());
            }
        }
        System.out.println();

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
