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

package org.flinkcdc.core;

import org.apache.flink.core.execution.JobClient;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.StatementSet;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.TableResult;
import org.flinkcdc.core.sql.SqlParser;
import org.flinkcdc.core.util.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * @author chuixue
 * @create 2022-08-05 14:39
 * @description
 **/
public class RunJob {
    public static Logger LOG = LoggerFactory.getLogger(RunJob.class);

    /**
     * run
     *
     * @param sqlPath
     */
    public static void runJob(String sqlPath) {
        exeSqlJob(createTableEnvironment(), readFile(sqlPath));
    }

    /**
     * exec sql
     *
     * @param tableEnv
     * @param job
     */
    private static void exeSqlJob(
            TableEnvironment tableEnv,
            String job) {
        try {
            StatementSet statementSet = SqlParser.parseSql(job, tableEnv);
            TableResult execute = statementSet.execute();
            Optional<JobClient> jobClient = execute.getJobClient();
            if (jobClient.isPresent()) {
                System.out.println(jobClient.get().getAccumulators().get());
            }
        } catch (Exception e) {
            LOG.error(ExceptionUtil.getErrorMessage(e));
        }
    }

    /**
     * get env
     *
     * @return
     */
    private static TableEnvironment createTableEnvironment() {
        EnvironmentSettings settings = EnvironmentSettings
                .newInstance()
                .build();
        return TableEnvironment.create(settings);
    }

    /**
     * get sql context
     *
     * @param sqlPath
     * @return
     */
    private static String readFile(String sqlPath) {
        try {
            byte[] array = Files.readAllBytes(Paths.get(sqlPath));
            return new String(array, StandardCharsets.UTF_8);
        } catch (IOException ioe) {
            LOG.error("Can not get the job info !!!", ioe);
            throw new RuntimeException(ioe);
        }
    }
}