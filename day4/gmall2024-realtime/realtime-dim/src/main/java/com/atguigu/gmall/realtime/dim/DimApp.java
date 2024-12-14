package com.atguigu.gmall.realtime.dim;

import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class DimApp {
    public static void main(String[] args) {
        //黄精准备
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //设置并行度
        env.setParallelism(4);
        //开启检查点
        env.enableCheckpointing(5000L, CheckpointingMode.EXACTLY_ONCE);
        //设置检查点的超时时间
        env.getCheckpointConfig().setCheckpointTimeout(60000L);
        //设置job取消后检查点是否保留
        env.getCheckpointConfig()

    }
}
