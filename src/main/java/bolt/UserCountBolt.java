package bolt;

import com.google.gson.*;

import helper.BoltHelper;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.IBasicBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.*;

public class UserCountBolt implements IBasicBolt {

    private BoltHelper boltHelper = new BoltHelper();

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
                            boltHelper.count(user.getAsString());
                            String result = boltHelper.getReturnMessage(10);
                            basicOutputCollector.emit(new Values(result));
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("UserCountBolt: Something went wrong!");
            System.out.println(e);
        }

    }

    @Override
    public void cleanup() {
        boltHelper.print();
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
