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

import org.apache.flink.table.api.StatementSet;
import org.apache.flink.table.api.TableEnvironment;

/**
 * @author chuixue
 * @create 2022-08-05 14:39
 * @description
 **/
public abstract class AbstractStmtParser {

    private AbstractStmtParser nextStmtParser;

    public void setNextStmtParser(AbstractStmtParser nextStmtParser) {
        this.nextStmtParser = nextStmtParser;
    }

    public abstract boolean canHandle(String stmt);

    public final void handleStmt(
            String stmt,
            TableEnvironment tEnv,
            StatementSet statementSet) {
        if (canHandle(stmt)) {
            execStmt(stmt, tEnv, statementSet);
        } else if (nextStmtParser != null) {
            nextStmtParser.handleStmt(stmt, tEnv, statementSet);
        } else {
            // Iff all StmtParser can not handle.
            tEnv.executeSql(stmt).print();
        }
    }

    /**
     * execute sql statement
     *
     * @param stmt
     * @param tableEnvironment
     * @param statementSet
     */
    public abstract void execStmt(
            String stmt,
            TableEnvironment tableEnvironment,
            StatementSet statementSet);
}
