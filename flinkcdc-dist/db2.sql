set pipeline.name = db2-kafka;
set table.exec.resource.default-parallelism = 1;

CREATE TABLE source (
     ID INT ,
     NAME STRING,
     primary key (ID) not enforced
) WITH (
     'connector' = 'db2-cdc',
     'hostname' = '172.16.83.99',
     'port' = '50000',
     'username' = 'db2inst1',
     'password' = 'Abc!@135',
     'database-name' = 'test',
     'schema-name' = 'DB2INST1',
     'table-name' = 'TIEZHU_ONE'
);

-- sink端配置和数据类型 参考上面
CREATE TABLE sink
(
    id   INT,
    name STRING
) WITH (
        'connector' = 'print'

--      'connector' = 'kafka',
--      'topic' = 'chuixue',
--      'properties.bootstrap.servers' = 'localhost:9092',
--      'format' = 'ogg-json'
);

-- 执行sql
insert into sink
select *
from source;