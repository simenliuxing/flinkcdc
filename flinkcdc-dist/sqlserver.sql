-- 定义配置(并行度、容错、状态后段等相关配置)，配置可以参考下面链接：
-- https://nightlies.apache.org/flink/flink-docs-master/docs/dev/table/config/
-- https://nightlies.apache.org/flink/flink-docs-master/docs/deployment/config/
set
pipeline.name = sqlserver-kafka;
set
table.exec.resource.default-parallelism = 1;

-- source端配置和数据类型 参考上面
CREATE TABLE source
(
    id   INT,
    name STRING,
    PRIMARY KEY (id) NOT ENFORCED
) WITH (
      'connector' = 'sqlserver-cdc',
      'hostname' = '172.16.100.76',
      'port' = '1433',
      'username' = 'developer',
      'password' = 'SQLserver123',
      'database-name' = 'developer',
      'schema-name' = 'dbo',
      'table-name' = 'persion');

-- sink端配置和数据类型 参考上面
CREATE TABLE sink
(
    id   INT,
    name STRING
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
from source
