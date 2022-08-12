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
import org.apache.flink.calcite.shaded.com.google.common.base.Strings;
import org.apache.flink.table.api.StatementSet;
import org.apache.flink.table.api.TableEnvironment;
import org.flinkcdc.core.throwable.DtSqlParserException;
import org.flinkcdc.core.util.DtStringUtil;
import org.flinkcdc.core.util.Splitter;

import java.util.List;

/**
 * @author chuixue
 * @create 2022-08-05 14:39
 * @description
 **/
public class SqlParser {

    private static final char SQL_DELIMITER = ';';

    /**
     * flink support sql syntax CREATE TABLE sls_stream() with (); CREATE (TABLE|SCALA) FUNCTION
     * fcnName WITH com.dtstack.com; insert into tb1 select * from tb2;
     *
     * @param
     */
    public static StatementSet parseSql(
            String sql, TableEnvironment tableEnvironment) {

        if (StringUtils.isBlank(sql)) {
            throw new IllegalArgumentException("SQL must be not empty!");
        }

        sql = DtStringUtil.dealSqlComment(sql);
        StatementSet statement = tableEnvironment.createStatementSet();
        Splitter splitter = new Splitter(SQL_DELIMITER);
        List<String> stmts = splitter.splitEscaped(sql);
        AbstractStmtParser stmtParser = createParserChain();

        stmts.stream()
                .filter(stmt -> !Strings.isNullOrEmpty(stmt.trim()))
                .forEach(
                        stmt -> {
                            try {
                                stmtParser.handleStmt(stmt, tableEnvironment, statement);
                            } catch (Exception e) {
                                throw new DtSqlParserException(stmt, e.getMessage(), e);
                            }
                        });

        return statement;
    }

    private static AbstractStmtParser createParserChain() {
        AbstractStmtParser insertStmtParser = new InsertStmtParser();
        AbstractStmtParser configStmtParser = new ConfigStmtParser();

        insertStmtParser.setNextStmtParser(configStmtParser);

        return insertStmtParser;
    }
}
