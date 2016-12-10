package bolt;

import helper.BoltHelper;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;

import java.util.Map;

public class BenchmarkBolt implements IRichBolt {
    private long startTime = 0;
    private long endTime = 0;
    private long counter = 0;

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {

    }

    @Override
    public void execute(Tuple tuple) {
        if (startTime == 0) {
            startTime = System.currentTimeMillis();

        }
        counter++;
        endTime = System.currentTimeMillis();

    }

    @Override
    public void cleanup() {
        long totalTime = endTime - startTime;
        System.out.println("Processing time:" + Long.toString(totalTime) + "ms");
        System.out.println("Counter : " + counter);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
