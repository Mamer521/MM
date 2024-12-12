import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import com.alibaba.ververica.cdc.debezium.StringDebeziumDeserializationSchema;
import com.alibaba.ververica.cdc.connectors.mysql.MySQLSource;
public class flink_CDC {
    public static void main(String[] args) throws Exception {


        SourceFunction<String> sourceFunction = MySQLSource.<String>builder()
                .hostname("hadoop101")
                .port(3306)
                .databaseList("gmall") // monitor all tables under inventory database
                .tableList("gmall.activity_info")
                .username("root")
                .password("root")
                .deserializer(new StringDebeziumDeserializationSchema()) // converts SourceRecord to String
                .build();

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env
                .addSource(sourceFunction)
                .print().setParallelism(1); // use parallelism 1 for sink to keep message ordering

        env.execute();
    }
}
