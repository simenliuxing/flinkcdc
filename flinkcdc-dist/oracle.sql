-- 定义配置(并行度、容错、状态后段等相关配置)，配置可以参考下面链接：
-- https://nightlies.apache.org/flink/flink-docs-master/docs/dev/table/config/
-- https://nightlies.apache.org/flink/flink-docs-master/docs/deployment/config/
set
pipeline.name = oracle-kafka;
set
table.exec.resource.default-parallelism = 1;

-- source端配置和数据类型 参考上面
CREATE TABLE source
(
    MONEY   NUMERIC,
    ID  NUMERIC
) WITH (
--       'debezium.database.connection.adpter' = 'xstream',
      'connector' = 'oracle-cdc',
      'hostname' = '172.16.100.243',
      'port' = '1521',
      'username' = 'developer',
      'password' = 'oracle123',
      'database-name' = 'ORCL',
      'schema-name' = 'DEVELOPER',
      'table-name' = 'HIVE'
      );

-- sink端配置和数据类型 参考上面
CREATE TABLE sink
(
    MONEY   NUMERIC,
    ID  NUMERIC
) WITH (
      'connector' = 'print'
--       'connector' = 'kafka',
--       'topic' = 'chuixue',
--       'properties.bootstrap.servers' = 'localhost:9092',
--       'format' = 'debezium-json'
      );

-- 执行sql
insert into sink
select *
from source;
