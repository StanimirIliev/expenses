package com.stanimir.expenses.adapter.db.mysql

import com.stanimir.expenses.core.*
import java.io.FileReader
import java.sql.ResultSet
import java.time.LocalDate
import java.util.*

/**
 * @author Stanimir Iliev <stanimir.iliev@clouway.com />
 */

class PersistentExpenseRepository(private val template: JdbcTemplate) : ExpenseRepository {
    private val table = "Expenses"
    private val mapper = object : RowMapper<Expense> {
        override fun adapt(result: ResultSet): Expense {
            return Expense(
                    result.getInt("id"),
                    result.getString("kind"),
                    result.getString("description"),
                    result.getDouble("amount"),
                    result.getDate("date").toLocalDate()
            )
        }
    }

    init {// create table if not exists
        template.execute(FileReader("schema/expenses.sql").readText())
    }

    override fun register(kind: String, description: String, amount: Double, date: LocalDate): Expense {
        val query = "insert into $table(kind, description, amount, date) " +
                "values('$kind', '$description', $amount, '$date')"
        template.execute(query)

        return Expense(template.getLastId(), kind, description, amount, date)
    }

    override fun getByKinds(kinds: List<String>, startDate: Optional<LocalDate>, endDate: Optional<LocalDate>): List<Expense> {
        return if (kinds.isEmpty()) {
            emptyList()
        } else {
            var query = "select * from $table where kind in" +
                    " (${kinds.map { "'$it'" }.toString().trimStart('[').trimEnd(']')}) " +
                    "and deletedOn is null"
            if (startDate.isPresent && endDate.isPresent) {
                query += " and date >= '${startDate.get()}' and date <= '${endDate.get()}'"
            }
            template.fetch("$query order by date", mapper)
        }
    }

    override fun getByDate(startDate: LocalDate, endDate: LocalDate): List<Expense> {
        return template.fetch("select * from $table where date >= '$startDate' and date <= '$endDate' and deletedOn is null order by date", mapper)
    }

    override fun getById(id: Int): Optional<Expense> {
        val result = template.fetch("select * from $table where id=$id and deletedOn is null", mapper)

        return if (result.isEmpty()) {
            Optional.empty()
        } else {
            Optional.of(result.first())
        }
    }

    override fun getAll(): List<Expense> {
        return template.fetch("select * from $table where deletedOn is null order by date", mapper)
    }

    override fun update(id: Int, kind: String, description: String, amount: Double, date: LocalDate) {
        val command = "update $table set kind='$kind',description='$description',amount=$amount,date='$date' where id=$id and deletedOn is null"
        if (template.execute(command) == 0) throw ExpenseNotFoundException(id)
    }

    override fun delete(id: Int) {
        val command = "update $table set deletedOn='${LocalDate.now()}' where id=$id and deletedOn is null"
        if (template.execute(command) == 0) throw ExpenseNotFoundException(id)
    }
}