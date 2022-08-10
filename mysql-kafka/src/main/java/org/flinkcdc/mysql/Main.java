/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.flinkcdc.mysql;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.PipelineOptions;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.StatementSet;
import org.apache.flink.table.api.TableEnvironment;

/**
 * @author chuixue
 * @create 2022-08-05 14:39
 * @description
 **/
public class Main {
    public static void main(String[] args) {
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

        Configuration configuration = tableEnv.getConfig().getConfiguration();
        configuration.setString(PipelineOptions.NAME, "mysql-kafka");
        configuration.setString("table.exec.resource.default-parallelism", "1");

        tableEnv.executeSql(source).print();
        tableEnv.executeSql(sink).print();
        StatementSet statementSet = tableEnv.createStatementSet();
        statementSet.addInsertSql(insert);

        statementSet.execute();
    }
}