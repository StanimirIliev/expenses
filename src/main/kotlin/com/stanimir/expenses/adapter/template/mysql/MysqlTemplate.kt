package com.stanimir.expenses.adapter.template.mysql

import com.stanimir.expenses.core.JdbcTemplate
import com.stanimir.expenses.core.RowMapper
import java.sql.SQLException
import java.sql.Statement
import java.util.*
import javax.sql.DataSource

/**
 * @author Stanimir Iliev <stanimir.iliev@clouway.com />
 */

class MysqlTemplate(private val dataSource: DataSource) : JdbcTemplate {

    private var lastId = -1

    override fun execute(query: String): Int {
        val connection = dataSource.connection
        var statement: Statement? = null

        return try {
            statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)
            statement.execute()

            val generatedKeys = statement.generatedKeys
            if(generatedKeys.next()) {
                lastId = generatedKeys.getInt(1)
            }

            statement.updateCount
        } catch (e: SQLException) {
            -1
        } finally {
            if (statement != null) {
                statement.close()
            }
            connection.close()
        }
    }

    override fun getLastId() = lastId

    override fun <T> fetch(query: String, mapper: RowMapper<T>): List<T> {
        val connection = dataSource.connection
        var statement: Statement? = null

        try {
            statement = connection.prepareStatement(query)
            val result = statement.executeQuery()

            val list = LinkedList<T>()
            while (result.next()) {
                list.add(mapper.adapt(result))
            }

            return list
        } finally {
            if (statement != null) {
                statement.close()
            }
            connection.close()
        }
    }
}