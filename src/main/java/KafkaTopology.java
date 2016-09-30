import java.util.UUID;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.kafka.*;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.starter.bolt.PrinterBolt;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

public class KafkaTopology {

    public static void main(String[] args) throws Exception {
        String brokerConnection = "localhost:2181";
        BrokerHosts hosts = new ZkHosts(brokerConnection);

        String topicName = "osm";
        SpoutConfig kafkaConf = new SpoutConfig(hosts, topicName, "/" + topicName, UUID.randomUUID().toString());

        kafkaConf.scheme = new SchemeAsMultiScheme(new StringScheme());

        KafkaSpout kafkaSpout = new KafkaSpout(kafkaConf);

        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("spout", kafkaSpout, 1);
        builder.setBolt("splitter", new SplitSentence()).shuffleGrouping("spout");
        //builder.setBolt("printer", new PrinterBolt()).shuffleGrouping("splitter");

        Config config = new Config();
        //config.setDebug(true);
        //config.setNumWorkers(3);
        //config.setMaxTaskParallelism(3);
        LocalCluster cluster = new LocalCluster();

        cluster.submitTopology("kafka", config, builder.createTopology());
        Thread.sleep(30000);
        cluster.shutdown();

    }


    public static class SplitSentence extends BaseBasicBolt {

        public void execute(Tuple tuple, BasicOutputCollector collector) {
            String sentence = tuple.getString(0);
            String[] words = sentence.split("\\s+");
            System.out.println("Length:" + words.length);
            Values values = new Values(words);
            collector.emit(values);
        }

        public void declareOutputFields(OutputFieldsDeclarer declarer) {
            declarer.declare(new Fields(new String[]{"number", "word"}));
        }
    }

}