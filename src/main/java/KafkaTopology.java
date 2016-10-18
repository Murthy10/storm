import java.util.UUID;

import bolt.UserCountBolt;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.kafka.*;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.topology.TopologyBuilder;
import com.google.gson.JsonObject;

import bolt.JsonPrinterBolt;
import bolt.StringToJsonBolt;

public class KafkaTopology {

    public static void main(String[] args) throws Exception {
        String brokerConnection = "localhost:2181";
        BrokerHosts hosts = new ZkHosts(brokerConnection);

        String topicName = "osm";
        SpoutConfig kafkaConf = new SpoutConfig(hosts, topicName, "/" + topicName, UUID.randomUUID().toString());

        kafkaConf.scheme = new SchemeAsMultiScheme(new StringScheme());

        KafkaSpout kafkaSpout = new KafkaSpout(kafkaConf);

        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("kafkaSpout", kafkaSpout, 1);
        builder.setBolt("stringToJsonBolt", new StringToJsonBolt()).shuffleGrouping("kafkaSpout");
        //builder.setBolt("printer", new JsonPrinterBolt()).shuffleGrouping("stringToJsonBolt");
        builder.setBolt("printer", new UserCountBolt()).shuffleGrouping("stringToJsonBolt");

        Config config = new Config();
        config.registerSerialization(JsonObject.class);
        //config.setDebug(true);
        //config.setNumWorkers(3);
        //config.setMaxTaskParallelism(3);
        LocalCluster cluster = new LocalCluster();

        cluster.submitTopology("kafka", config, builder.createTopology());
        Thread.sleep(30000);
        cluster.shutdown();

    }


}