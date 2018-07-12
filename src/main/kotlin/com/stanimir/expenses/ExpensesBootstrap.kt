package com.stanimir.expenses

import com.mysql.cj.jdbc.MysqlDataSource
import com.stanimir.expenses.adapter.cli.apache.*
import com.stanimir.expenses.adapter.cli.display.ConsoleExpensesDisplay
import com.stanimir.expenses.adapter.db.mysql.PersistentExpenseRepository
import com.stanimir.expenses.adapter.template.mysql.MysqlTemplate
import org.apache.commons.cli.*
import java.nio.charset.Charset
import java.time.LocalDate
import java.util.*

/**
 * @author Stanimir Iliev <stanimir.iliev@clouway.com />
 */

class ExpensesBootstrap(val args: Array<String>) {
    private val options = Options()
    private val helpMessage = ExpensesBootstrap::class.java
            .getResourceAsStream("helpMessage")
            .reader(Charset.defaultCharset())
            .readText()

    init {
        // sets options groups
        options.addOptionGroup(
                OptionGroup()
                        .addOption(Option("n", "new", false, "Register new expense"))
                        .addOption(Option("p", "print", false, "Print expenses"))
                        .addOption(Option("printKindsOnly", "printKindsOnly", false, "Print kinds only"))
                        .addOption(Option("printMonthsAmount", "printMonthsAmount", false, "Print amount for every month"))
                        .addOption(Option("e", "edit", true, "Edit expense"))
                        .addOption(Option("delete", "delete", true, "Delete expense"))
        )
        // sets the rest options
        val predefinedOptions = listOf(
                Option("k", "kind", true, "Used with 'n' and 'p'"),
                Option("d", "description", true, "Used with 'n'"),
                Option("a", "amount", true, "Used with 'n'"),
                Option("D", "date", true, "Used with 'n' and 'p'"),
                Option("startDate", "startDate", true, "Used with 'p'"),
                Option("endDate", "endDate", true, "Used with 'p'"),
                Option("?", "help", false, "Help message")
        )

        predefinedOptions.forEach { options.addOption(it) }
    }

    fun start() {
        val mySqlDataSource = MysqlDataSource()
        mySqlDataSource.setUrl("jdbc:mysql://${System.getenv("DB_HOST")}/${System.getenv("DB_DATABASE")}" +
                "?useUnicode=true&" +
                "useJDBCCompliantTimezoneShift=true&" +
                "useLegacyDatetimeCode=false&" +
                "serverTimezone=UTC&" +
                "characterEncoding=utf8&" +
                "useSSL=false")
        mySqlDataSource.user = System.getenv("DB_USER")
        mySqlDataSource.setPassword(System.getenv("DB_PASS"))
        val jdbcTemplate = MysqlTemplate(mySqlDataSource)
        val repository = PersistentExpenseRepository(jdbcTemplate)
        val parser = DefaultParser()
        val display = ConsoleExpensesDisplay(repository)

        try {
            val cmd = parser.parse(options, args)

            when {
                cmd.hasOption("n") -> {
                    if (!cmd.hasOption('k') || !cmd.hasOption('a')) {
                        println("Mandatory options when using 'n' are not found")
                        return
                    }
                    val expense = repository.register(
                            cmd.getOptionValue('k'),
                            cmd.parseOptionOrNull("d", String::class.java) ?: "",
                            cmd.parseOption("a", Double::class.java),
                            cmd.parseOptionOrNull("D", LocalDate::class.java) ?: LocalDate.now()
                    )
                    println("New expense registered -> $expense")
                }
                cmd.hasOption("printKindsOnly") -> display.printAllKinds()
                cmd.hasOption("printMonthsAmount") -> display.printMonthsAmount()
                cmd.hasOption("p") -> {
                    when {
                        cmd.hasOption("k") -> {
                            if(cmd.hasOption("startDate") && cmd.hasOption("endDate")) {
                                display.printByKinds(
                                        cmd.parseOptionList("k", String::class.java),
                                        Optional.of(cmd.parseOption("startDate", LocalDate::class.java)),
                                        Optional.of(cmd.parseOption("endDate", LocalDate::class.java))
                                )
                            } else {
                                display.printByKinds(cmd.parseOptionList("k", String::class.java))
                            }
                        }
                        cmd.hasOption("startDate") && cmd.hasOption("endDate") -> {
                            display.printByDate(
                                    cmd.parseOption("startDate", LocalDate::class.java),
                                    cmd.parseOption("endDate", LocalDate::class.java)
                            )
                        }
                        else -> display.printAll()
                    }
                }
                cmd.hasOption("e") -> {
                    val id = cmd.parseOptionOrNull("e", Int::class.java)
                    if (id == null) {
                        println("option e takes argument id of the expenses which to update")
                        return
                    }

                    val oldExpense = repository.getById(id)
                    if (!oldExpense.isPresent) {
                        println("Cannot find expense with id $id")
                        return
                    }

                    val kind = cmd.parseOptionOrNull("k", String::class.java) ?: oldExpense.get().kind
                    val description = cmd.parseOptionOrNull("d", String::class.java) ?: oldExpense.get().description
                    val amount = cmd.parseOptionOrNull("a", Double::class.java) ?: oldExpense.get().amount
                    val date = cmd.parseOptionOrNull("D", LocalDate::class.java) ?: oldExpense.get().date

                    repository.update(id, kind, description, amount, date)
                    println("Expense with id $id updated successfully.")
                }
                cmd.hasOption("delete") -> {
                    val id = cmd.parseOptionOrNull("delete", Int::class.java)
                    if (id == null) {
                        println("option delete takes argument id of the expense which to delete")
                        return
                    }

                    repository.delete(id)
                    println("Expense with id $id deleted successfully.")
                }
                cmd.hasOption("?") -> println(helpMessage)
                else -> println("No main option selected. Use -? to view example usage.")
            }
        } catch (e: Exception) {
            when (e) {
                is ParseException -> println("Cannot parse options. Use -? to view example usage.")
                is CannotParseOptionException -> println("Cannot parse option ${e.option}")
                is OptionValueNotFoundException -> println("Option ${e.option} does not have value")
                else -> println(e)
            }

        }
    }
}

fun main(args: Array<String>) {
    ExpensesBootstrap(args).start()
}