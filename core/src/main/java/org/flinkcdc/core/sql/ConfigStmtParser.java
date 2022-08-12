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

package org.flinkcdc.core.sql;

import org.apache.commons.lang3.StringUtils;
import org.apache.flink.table.api.StatementSet;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.operations.command.SetOperation;
import org.apache.flink.table.planner.parse.ExtendedParser;

import java.util.Optional;

/**
 * @author chuixue
 * @create 2021-09-23 17:34
 * @description
 **/
public class ConfigStmtParser extends AbstractStmtParser {
    @Override
    public boolean canHandle(String stmt) {
        return StringUtils.isNotBlank(stmt) && stmt.trim().toLowerCase().startsWith("set");
    }

    @Override
    public void execStmt(String stmt, TableEnvironment tableEnvironment, StatementSet statementSet) {
        Optional<Operation> command = ExtendedParser.INSTANCE.parse(stmt);
        if (command.isPresent()) {
            SetOperation setOperation = (SetOperation) command.get();
            if (setOperation.getKey().isPresent() && setOperation.getValue().isPresent()) {
                String key = setOperation.getKey().get().trim();
                String value = setOperation.getValue().get().trim();
                tableEnvironment.getConfig().getConfiguration().setString(key, value);
            }
        }
    }
}
