-- 定义配置(并行度、容错、状态后段等相关配置)，配置可以参考下面链接：
-- https://nightlies.apache.org/flink/flink-docs-master/docs/dev/table/config/
-- https://nightlies.apache.org/flink/flink-docs-master/docs/deployment/config/
set pipeline.name = kafka-print;
set table.exec.resource.default-parallelism = 1;

-- source端配置和数据类型 参考上面
CREATE TABLE source
(
    id   INT,
    name STRING
) WITH (
      'connector' = 'kafka',
      'topic' = 'chuixue',
      'properties.bootstrap.servers' = 'localhost:9092',
      'properties.group.id' = 'testGroup',
      'scan.startup.mode' = 'earliest-offset',
      'format' = 'ogg-json'
      );

-- sink端配置和数据类型 参考上面
CREATE TABLE sink
(
    id   BIGINT,
    name STRING
) WITH (
      'connector' = 'print');

-- 执行sql
insert into sink
select
    count(*) as id,
    name
from source
group by name;
