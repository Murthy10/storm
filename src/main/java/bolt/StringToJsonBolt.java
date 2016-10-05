package bolt;

import org.apache.storm.shade.org.json.simple.JSONObject;
import org.apache.storm.shade.org.json.simple.parser.JSONParser;
import org.apache.storm.shade.org.json.simple.parser.ParseException;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.IBasicBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.Map;

public class StringToJsonBolt implements IBasicBolt {
    public void prepare(Map map, TopologyContext topologyContext) {

    }

    public void execute(Tuple tuple, BasicOutputCollector basicOutputCollector) {
        String diff = tuple.getString(0);
        JSONParser jsonParser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(diff);
            basicOutputCollector.emit(new Values(jsonObject));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void cleanup() {

    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("json"));
    }

    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
