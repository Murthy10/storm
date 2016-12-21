package bolt;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.IBasicBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.Map;

public class AreaYesBolt implements IBasicBolt {
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
                                        checkForAreYes(tag, node, basicOutputCollector);
                                    }
                                }
                            } else {
                                if (jsonTag.isJsonObject()) {
                                    checkForAreYes(jsonTag.getAsJsonObject(), node, basicOutputCollector);
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

    private void checkForAreYes(JsonObject tag, JsonObject node, BasicOutputCollector basicOutputCollector) {
        if (tag.has("@k") && tag.has("@v")) {
            JsonPrimitive key = tag.getAsJsonPrimitive("@k");
            JsonPrimitive value = tag.getAsJsonPrimitive("@v");
            if (key.getAsString().equals("area") && value.getAsString().equals("yes")) {
                basicOutputCollector.emit(new Values(node.getAsString()));
            }
        }
    }

    @Override
    public void cleanup() {

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
