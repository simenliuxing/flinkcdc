# 基于apache flink和flink-cdc-connectors工程的采集案例

该工程的目的是可以通过sql化的方式，方便的全量、增量一体化采集mysql、oracle数据库中的数据，而不用安装其他的一些类canal、ogg等采集工具.
由于目前数栈flink版本(version:flink1.12.7)原因，该工程打包后，不可在数栈上使用.

## 支持的数据库
### source端配置和数据类型
| Connector                                                 | Database                                                                                                                                                                                                                                                                                                                                                                                               | Driver                  |
|-----------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------|
| [mysql-cdc](https://github.com/ververica/flink-cdc-connectors/blob/release-2.2/docs/content/connectors/mysql-cdc.md)         | <li> [MySQL](https://dev.mysql.com/doc): 5.6, 5.7, 8.0.x <li> [RDS MySQL](https://www.aliyun.com/product/rds/mysql): 5.6, 5.7, 8.0.x <li> [PolarDB MySQL](https://www.aliyun.com/product/polardb): 5.6, 5.7, 8.0.x <li> [Aurora MySQL](https://aws.amazon.com/cn/rds/aurora): 5.6, 5.7, 8.0.x <li> [MariaDB](https://mariadb.org): 10.x <li> [PolarDB X](https://github.com/ApsaraDB/galaxysql): 2.0.1 | JDBC Driver: 8.0.21     |
| [oracle-cdc](https://github.com/ververica/flink-cdc-connectors/blob/release-2.2/docs/content/connectors/oracle-cdc.md)       | <li> [Oracle](https://www.oracle.com/index.html): 11, 12, 19                                                                                                                                                                                                                                                                                                                                           | Oracle Driver: 19.3.0.0 |

### sink端配置和数据类型(目前只支持写kafka)
| Connector                                                 | Format                                                                                                                                                                                                                                                                                                                                                                                               | Data                  |
|-----------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------|
| kafka        |[debezium-json](https://nightlies.apache.org/flink/flink-docs-master/docs/connectors/table/formats/debezium/) |  [debezium-json 数据格式](https://nightlies.apache.org/flink/flink-docs-master/docs/connectors/table/formats/debezium/#how-to-use-debezium-format) |
| kafka        |[canal-json](https://nightlies.apache.org/flink/flink-docs-master/docs/connectors/table/formats/canal/) | [canal-json 数据格式](https://nightlies.apache.org/flink/flink-docs-master/docs/connectors/table/formats/canal/#how-to-use-canal-format) |
| kafka        |[maxwell-json](https://nightlies.apache.org/flink/flink-docs-master/docs/connectors/table/formats/maxwell/) |  [maxwell-json 数据格式](https://nightlies.apache.org/flink/flink-docs-master/docs/connectors/table/formats/maxwell/#how-to-use-maxwell-format)|
| kafka        |[changelog-json](https://ververica.github.io/flink-cdc-connectors/release-2.1/content/formats/changelog-json.html) | changelog-json 数据格式：<br> {"data":{},"op":"+I"} <br> {"data":{},"op":"-U"} <br> {"data":{},"op":"+U"} <br> {"data":{},"op":"-D"} |
| kafka        |[ogg-json(目前不支持)](https://nightlies.apache.org/flink/flink-docs-master/docs/connectors/table/formats/ogg/) | [ogg-json 数据格式](https://nightlies.apache.org/flink/flink-docs-master/docs/connectors/table/formats/ogg/#how-to-use-ogg-format) |

## 使用步骤

1.代码内容如下：
```
        // source端配置和数据类型 参考上面
        String source = "CREATE TABLE source\n" +
                "(\n" +
                "    id      INT,\n" +
                "    name STRING,\n" +
                "    PRIMARY KEY (id) NOT ENFORCED\n" +
                ") WITH (\n" +
                "      'connector' = 'mysql-cdc',\n" +
                "      'hostname' = 'localhost',\n" +
                "      'port' = '3306',\n" +
                "      'username' = 'root',\n" +
                "      'password' = 'root',\n" +
                "      'database-name' = 'test',\n" +
                "      'table-name' = 'out_cdc')";
        // sink端配置和数据类型 参考上面
        String sink = "CREATE TABLE sink \n" +
                " (\n" +
                "     id      INT, \n" +
                "     name STRING \n" +
                " ) WITH ( \n" +
                "        'connector' = 'kafka',\n" +
                "        'topic' = 'chuixue',\n" +
                "        'properties.bootstrap.servers' = 'localhost:9092', \n" +
                "        'format' = 'debezium-json')";

        String insert = "insert into sink select * from source";



        EnvironmentSettings settings = EnvironmentSettings
                .newInstance()
                .build();
        TableEnvironment tableEnv = TableEnvironment.create(settings);

        // 并行度、容错、状态后段等相关配置，参考下面链接
        // https://nightlies.apache.org/flink/flink-docs-master/docs/dev/table/config/
        // https://nightlies.apache.org/flink/flink-docs-master/docs/deployment/config/
        Configuration configuration = tableEnv.getConfig().getConfiguration();
        configuration.setString(PipelineOptions.NAME, "mysql-kafka");
        configuration.setString("table.exec.resource.default-parallelism", "1");

        tableEnv.executeSql(source).print();
        tableEnv.executeSql(sink).print();
        StatementSet statementSet = tableEnv.createStatementSet();
        statementSet.addInsertSql(insert);

        statementSet.execute();
```
2.将工程下载到本地，编译、打包   
3.在服务器上执行 

## 后续计划
1.支持更多数据库采集   
2.将上面的sql代码和配置单独放到一个文件中，做到不用修改代码即可支持采集