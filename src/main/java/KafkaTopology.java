import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import bolt.*;
import com.google.gson.JsonArray;
import helper.BoltHelper;
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
        builder.setSpout("kafkaSpout", kafkaSpout);

        //builder.setBolt("benchmarkBolt", new BenchmarkBolt()).shuffleGrouping("kafkaSpout");

        builder.setBolt("stringToJsonBolt", new StringToJsonBolt()).shuffleGrouping("kafkaSpout");
        //builder.setBolt("printer", new JsonPrinterBolt()).shuffleGrouping("stringToJsonBolt");
        builder.setBolt("userCountBolt", new UserCountBolt()).allGrouping("stringToJsonBolt");
        builder.setBolt("objectCountBolt", new ObjectsCountBolt()).allGrouping("stringToJsonBolt");

        KafkaBolt kafkaBoltUserCount = new KafkaBolt();
        kafkaBoltUserCount.withProducerProperties(getProperties(host));
        kafkaBoltUserCount.withTopicSelector(new DefaultTopicSelector("UserCount"));
        kafkaBoltUserCount.withTupleToKafkaMapper(new FieldNameBasedTupleToKafkaMapper());
        builder.setBolt("kafkaBoltUserCount", kafkaBoltUserCount).shuffleGrouping("userCountBolt");

        KafkaBolt kafkaBoltObjectCount = new KafkaBolt();
        kafkaBoltObjectCount.withProducerProperties(getProperties(host));
        kafkaBoltObjectCount.withTopicSelector(new DefaultTopicSelector("ObjectCount"));
        kafkaBoltObjectCount.withTupleToKafkaMapper(new FieldNameBasedTupleToKafkaMapper());
        builder.setBolt("kafkaBoltObjectCount", kafkaBoltObjectCount).shuffleGrouping("objectCountBolt");


        Config config = new Config();


        config.registerSerialization(JsonObject.class);
        config.registerSerialization(JsonArray.class);
        //config.setDebug(true);
        //config.setNumWorkers(3);
        //config.setMaxTaskParallelism(3);
        LocalCluster cluster = new LocalCluster();

        cluster.submitTopology("kafka", config, builder.createTopology());
        Thread.sleep(30 * 60 * 1000);
        cluster.shutdown();

    }

    private static Properties getProperties(String host) {
        Properties props = new Properties();
        props.put("bootstrap.servers", host + ":9092");
        props.put("metadata.broker.list", host + ":9092");
        props.put("request.required.acks", "1");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return props;
    }


}