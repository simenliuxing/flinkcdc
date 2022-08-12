set
pipeline.name = mysql-kafka;
set
table.exec.resource.default-parallelism = 1;

CREATE TABLE source
(
    id   INT,
    name STRING,
    PRIMARY KEY (id) NOT ENFORCED
) WITH (
      'connector' = 'mysql-cdc',
      'hostname' = 'localhost',
      'port' = '3306',
      'username' = 'root',
      'password' = 'root',
      'database-name' = 'test',
      'table-name' = 'out_cdc');

CREATE TABLE sink
(
    id   INT,
    name STRING
) WITH (
      'connector' = 'kafka',
      'topic' = 'chuixue',
      'properties.bootstrap.servers' = 'localhost:9092',
      'format' = 'debezium-json');

insert into sink
select *
from source