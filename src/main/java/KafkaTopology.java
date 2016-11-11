import java.util.UUID;

import bolt.*;
import com.google.gson.JsonArray;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.kafka.*;
import org.apache.storm.kafka.bolt.KafkaBolt;
import org.apache.storm.kafka.bolt.mapper.FieldNameBasedTupleToKafkaMapper;
import org.apache.storm.kafka.bolt.selector.DefaultTopicSelector;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.topology.TopologyBuilder;
import com.google.gson.JsonObject;

public class KafkaTopology {

    public static void main(String[] args) throws Exception {
        //String brokerConnection = "localhost:2181";
        String brokerConnection = "172.17.0.2:2181";
        BrokerHosts hosts = new ZkHosts(brokerConnection);

        String topicNameOsm = "osm";
        String topicNameBenchmark = "benchmark";
        SpoutConfig kafkaConf = new SpoutConfig(hosts, topicNameOsm, "/" + topicNameOsm, UUID.randomUUID().toString());
        //SpoutConfig kafkaConf = new SpoutConfig(hosts, topicNameBenchmark, "/" + topicNameOsm, UUID.randomUUID().toString());

        kafkaConf.scheme = new SchemeAsMultiScheme(new StringScheme());
        kafkaConf.fetchSizeBytes = 100000000;

        KafkaSpout kafkaSpout = new KafkaSpout(kafkaConf);

        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("kafkaSpout", kafkaSpout, 1);

        //builder.setBolt("benchmarkBolt", new BenchmarkBolt()).shuffleGrouping("kafkaSpout");

        builder.setBolt("stringToJsonBolt", new StringToJsonBolt()).shuffleGrouping("kafkaSpout");
        //builder.setBolt("printer", new JsonPrinterBolt()).shuffleGrouping("stringToJsonBolt");
        builder.setBolt("user", new UserCountBolt()).shuffleGrouping("stringToJsonBolt");
        //builder.setBolt("objects", new ObjectsCountBolt()).shuffleGrouping("stringToJsonBolt");

        KafkaBolt kafkaBolt = new KafkaBolt().withTopicSelector(new DefaultTopicSelector("ObjectResults")).withTupleToKafkaMapper(new FieldNameBasedTupleToKafkaMapper());
        builder.setBolt("toKafka", kafkaBolt).shuffleGrouping("user");

        Config config = new Config();
        config.registerSerialization(JsonObject.class);
        config.registerSerialization(JsonArray.class);
        //config.setDebug(true);
        //config.setNumWorkers(3);
        //config.setMaxTaskParallelism(3);
        LocalCluster cluster = new LocalCluster();

        cluster.submitTopology("kafka", config, builder.createTopology());
        Thread.sleep(30000);
        cluster.shutdown();

    }


}