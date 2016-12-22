package bolt;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.apache.commons.lang.StringUtils;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.IBasicBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.Map;

public class SuspiciousBolt implements IBasicBolt {
    private int suspiciousCounter = 0;

    @Override
    public void prepare(Map map, TopologyContext topologyContext) {

    }

    @Override
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
                                        checkForSuspicious(tag, node, basicOutputCollector);
                                    }
                                }
                            } else {
                                if (jsonTag.isJsonObject()) {
                                    checkForSuspicious(jsonTag.getAsJsonObject(), node, basicOutputCollector);
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

/*
  where ( -- key
    length(key) <= 2
    or
    (tagcount = 4 and key ilike 'name')
  ) or (  -- value
    (length(value) = 1 and not (value ~ '^[0-9]+$' or value in ('N','S','E','W','F') or key='ref')
    )
  )
 */

    private void checkForSuspicious(JsonObject tag, JsonObject node, BasicOutputCollector basicOutputCollector) {
        if (tag.has("@k") && tag.has("@v")) {
            JsonPrimitive key = tag.getAsJsonPrimitive("@k");
            JsonPrimitive value = tag.getAsJsonPrimitive("@v");
            if (key.getAsString().length() <= 2 || key.getAsString().toLowerCase().equals("name") || key.getAsString().toLowerCase().equals("ref")) {
                basicOutputCollector.emit(new Values(node.getAsString()));
                suspiciousCounter++;
            }
            if (value.getAsString().length() == 1 && !(StringUtils.containsAny(value.getAsString(), "0123456789NSEWF"))) {
                basicOutputCollector.emit(new Values(node.getAsString()));
                suspiciousCounter++;
            }
        }
    }

    @Override
    public void cleanup() {
        System.out.println("Number of Suspicious: " + Integer.toString(suspiciousCounter));

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
