package com.stanimir.expenses.core

import java.sql.ResultSet

/**
 * @author Stanimir Iliev <stanimir.iliev@clouway.com />
 */

interface JdbcTemplate {

    /**
     * Executes a query to the DB
     *
     * @param query the query which to execute
     * @return the number of updated rows, -1 if an error has occurred
     */
    fun execute(query: String): Int

    /**
     * Gets last inserted Id
     */
    fun getLastId(): Int

    /**
     * Fetches result from the DB
     *
     * @param query the query for DB
     * @param mapper parse the result from DB
     */
    fun <T> fetch(query: String, mapper: RowMapper<T>): List<T>
}

interface RowMapper <T>{
    fun adapt(result: ResultSet): T
}