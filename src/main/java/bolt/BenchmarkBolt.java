package bolt;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;

import java.util.Map;

public class BenchmarkBolt implements IRichBolt {
    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {

    }

    @Override
    public void execute(Tuple tuple) {
        long startTime = System.currentTimeMillis();

        String book = tuple.getString(0);
        int numberOfWords = wordCount(book);

        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        System.out.println("Processing time:" + Long.toString(totalTime) +"ms Number of Words: " + Integer.toString(numberOfWords));
    }

    private int wordCount(String s){
        if (s == null)
            return 0;
        return s.trim().split("\\s+").length;
    }

    @Override
    public void cleanup() {

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
