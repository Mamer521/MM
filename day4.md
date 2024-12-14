Flink**断点续传**:是基于offset,flink的checkpoint机制是实现断点续传的关键,Checkpoint 是一种分布式快照，它可以捕获作业的状态，包括数据源（如 Kafka）的 Offset。例如我们下载软件软件期间暂停操作就需要一个断点续传,直接从断点处继续续传

**dim层在hbase建表**,和数据存入hbase:
	建表期间报错一,是hbase中的连接报错,再执行list或者list_namespace的时候会报一个错误keepererrorcode:![8eaf056bc8e69421aac10ce6760792aa](E:\聊天信息\Tencent Files\2732158083\nt_qq\nt_data\Pic\2024-12\Ori\8eaf056bc8e69421aac10ce6760792aa.png)

解决:进入zookeeper中目录,执行./zkClish.sh,在执行deleteall /hbase,即可

​	2.上面报错解决后,接着又报一个:

​	![748a54bfd132b16d9ae7f7019436ac51](E:\聊天信息\Tencent Files\2732158083\nt_qq\nt_data\Pic\2024-12\Ori\748a54bfd132b16d9ae7f7019436ac51.png)

解决:报这个错是因为,hbase没有创建空间
1:cd /opt/module/hbase/bin
2:./start-hbase.sh
3:进入hbase,hbase shell
4:创建一个空间,create_namespace "空间名";
5:list_namespace查看空间是否有创建的空间;

**1.业务过程**:

```flink中的业务过程
//  1.ETL清洗主流数据
        SingleOutputStreamOperator<JSONObject> etlStream = etl(dataStreamSource);
        // 2.通过CDC读取配置表,并行度只能是1
        DataStreamSource<String> processStream = env.fromSource(FlinkSourceUtil.getMysqlSource(Constant.PROCESS_DATABASE, Constant.PROCESS_DIM_TABLE_NAME), WatermarkStrategy.noWatermarks(), "cdc_stream").setParallelism(1);
        // 3.在Hbase建表
        SingleOutputStreamOperator<TableProcessDim> createTableStream = createTable(processStream);
        // 4.主流数据和广播进行连接处理
        MapStateDescriptor<String,TableProcessDim> mapDescriptor = new MapStateDescriptor<String,TableProcessDim>("broadcast_state",String.class,TableProcessDim.class);
        BroadcastStream<TableProcessDim> broadcastStream = createTableStream.broadcast(mapDescriptor);
        SingleOutputStreamOperator<Tuple2<JSONObject, TableProcessDim>> processBroadCastStream = etlStream.connect(broadcastStream).process(new DimProcessFunction(mapDescriptor));
        // 5.过滤字段
        SingleOutputStreamOperator<Tuple2<JSONObject, TableProcessDim>> filterStream = getFilterStream(processBroadCastStream);
        // 6.写入Hbase
        filterStream.addSink(new DimSinkFunction());
```

