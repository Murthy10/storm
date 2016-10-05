package bolt;

import org.apache.storm.shade.org.json.simple.JSONObject;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.IBasicBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;

import java.util.Map;

public class JsonPrinterBolt implements IBasicBolt {
    public void prepare(Map map, TopologyContext topologyContext) {

    }

    public void execute(Tuple tuple, BasicOutputCollector basicOutputCollector) {
        JSONObject json = (JSONObject) tuple.getValue(0);
        System.out.println(json.toString());
    }

    public void cleanup() {

    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }

    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
