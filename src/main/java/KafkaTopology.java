import java.util.Properties;
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
        String host = "172.17.0.2";
        String brokerConnection = host + ":2181";
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
        builder.setBolt("userCountBolt", new UserCountBolt()).shuffleGrouping("stringToJsonBolt");
        //builder.setBolt("objects", new ObjectsCountBolt()).shuffleGrouping("stringToJsonBolt");

        KafkaBolt kafkaBolt = new KafkaBolt().withTopicSelector(new DefaultTopicSelector("results")).withTupleToKafkaMapper(new FieldNameBasedTupleToKafkaMapper());
        builder.setBolt("kafkaBolt", kafkaBolt).shuffleGrouping("userCountBolt");

        Config config = new Config();

        //set producer properties.
        Properties props = new Properties();
        props.put("metadata.broker.list", host+ ":9092");
        props.put("request.required.acks", "1");
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        config.put("kafka.broker.properties", props);

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