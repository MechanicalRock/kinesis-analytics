package com.amazonaws.services.kinesisanalytics;

import com.amazonaws.services.kinesisanalytics.runtime.KinesisAnalyticsRuntime;
import com.amazonaws.services.kinesisanalytics.flink.connectors.producer.FlinkKinesisFirehoseProducer;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonNode;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kinesis.FlinkKinesisConsumer;
import org.apache.flink.streaming.connectors.kinesis.FlinkKinesisProducer;
import org.apache.flink.streaming.connectors.kinesis.config.ConsumerConfigConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * A basic Kinesis Data Analytics Java Flink application with Kinesis data
 * streams as source and sink.
 */
public class BasicStreamingJob {

    private static Logger LOG = LoggerFactory.getLogger(BasicStreamingJob.class);
    private static Properties appProperties = null;
    private static final ObjectMapper jsonParser = new ObjectMapper();

    public static void main(String... args) throws Exception {

        // set up the streaming execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        appProperties = getRuntimeConfigProperties();

        DataStream<String> input = getSourceFromKinesisDataStream(env);
        input.map(value -> { // Parse the JSON
            JsonNode jsonNode = jsonParser.readValue(value, JsonNode.class);
            return new Tuple3<>(
                jsonNode.get("EVENT_TIME").asText(),
                jsonNode.get("CAR").asText(),
                jsonNode.get("VELOCITY").asDouble());
        })
        .returns(Types.TUPLE(Types.STRING, Types.STRING, Types.DOUBLE))
        .keyBy(1) // Logically partition the stream per car model
        .timeWindow(Time.seconds(10), Time.seconds(5)) // Sliding window definition
        .max(2) // Calculate the maximum value over the window
        .map(value -> value.f0 + " ==> " + value.f1 + " : Terminal Velocity: " + value.f2.toString() + "\n")
        .addSink(createKinesisDataStreamSink());

        env.execute("Flink data stream processing for racing at Nurburgring");
    }
    // Consume from kinesis datastream
    private static DataStream<String> getSourceFromKinesisDataStream(StreamExecutionEnvironment env) {
        LOG.info("Starting to consume events from stream {}", appProperties.getProperty("inputStreamName"));

        String region = getAppProperty("aws.region", "");
        String inputStreamName = getAppProperty("inputStreamName", "");

        Properties inputProperties = new Properties();
        inputProperties.setProperty(ConsumerConfigConstants.AWS_REGION, region);
        inputProperties.setProperty(ConsumerConfigConstants.STREAM_INITIAL_POSITION, "LATEST");

        return env.addSource(new FlinkKinesisConsumer<>(inputStreamName, new SimpleStringSchema(), inputProperties));
    }

    // Output transformed data to kinesis datastream
    private static FlinkKinesisProducer<String> createKinesisDataStreamSink() {
        String region = getAppProperty("aws.region", "");
        String outputStreamName = getAppProperty("outputStreamName", "");
        String aggregation = getAppProperty("AggregationEnabled", "");
        String DefPartition = getAppProperty("Partition", "");

        Properties outputProperties = new Properties();
        outputProperties.setProperty(ConsumerConfigConstants.AWS_REGION, region);
        outputProperties.setProperty("AggregationEnabled", aggregation);

        FlinkKinesisProducer<String> sink = new FlinkKinesisProducer<>(new SimpleStringSchema(), outputProperties);
        sink.setDefaultStream(outputStreamName);
        sink.setDefaultPartition(DefPartition);
        return sink;
    }

    // helper method to return runtime properties for Property Group RacingDataConfigProperties defined in Cloudformation Template
    private static Properties getRuntimeConfigProperties() {
        try {
            Map<String, Properties> runConfigurations = KinesisAnalyticsRuntime.getApplicationProperties();
            return (Properties) runConfigurations.get("RacingDataConfigProperties");
        } catch (IOException var1) {
            LOG.error("Could not retrieve the runtime config properties for {}, exception {}", "RacingDataConfigProperties", var1);
            return null;
        }
    }

    private static String getAppProperty(String name, final String defaultValue) {
        return appProperties.getProperty(name, defaultValue);
    }

    private static int getAppPropertyInt(String name, final int defaultIntValue) {

        final String stringValue = getAppProperty(name,null);

        if(stringValue == null){
            return defaultIntValue;
        }

        return Integer.parseInt(stringValue);
    }

}
