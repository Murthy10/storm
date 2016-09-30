import java.util.UUID;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.kafka.*;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.starter.bolt.PrinterBolt;
import org.apache.storm.topology.TopologyBuilder;

public class KafkaTopology {

    public static void main(String[] args) throws Exception {
        String brokerConnection = "localhost:2181";
        BrokerHosts hosts = new ZkHosts(brokerConnection);

        String topicName = "osm";
        SpoutConfig kafkaConf = new SpoutConfig(hosts, topicName, "/" + topicName, UUID.randomUUID().toString());

        kafkaConf.scheme = new SchemeAsMultiScheme(new StringScheme());

        KafkaSpout kafkaSpout = new KafkaSpout(kafkaConf);

        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("spout", kafkaSpout, 2);
        builder.setBolt("printer", new PrinterBolt())
                .shuffleGrouping("spout");

        Config config = new Config();
        config.setDebug(true);

        if (args != null && args.length > 0) {
            config.setNumWorkers(3);

            StormSubmitter.submitTopology(args[0], config, builder.createTopology());
        } else {
            config.setMaxTaskParallelism(3);

            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("kafka", config, builder.createTopology());

            Thread.sleep(10000);

            cluster.shutdown();
        }

    }

}