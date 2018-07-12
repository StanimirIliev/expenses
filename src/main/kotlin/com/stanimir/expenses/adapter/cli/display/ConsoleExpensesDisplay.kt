package com.stanimir.expenses.adapter.cli.display

import com.stanimir.expenses.core.Expense
import com.stanimir.expenses.core.ExpenseRepository
import com.stanimir.expenses.core.ExpensesDisplay
import com.stanimir.table.adapter.cli.SimpleTable
import com.stanimir.table.core.Cell
import com.stanimir.table.core.Header
import com.stanimir.table.core.Table
import java.text.DecimalFormat
import java.time.LocalDate
import java.util.*

/**
 * @author Stanimir Iliev <stanimir.iliev@clouway.com />
 */

class ConsoleExpensesDisplay(private val repository: ExpenseRepository) : ExpensesDisplay {

    private val headers = listOf(
            Header(5, "ID", Table.Align.CENTER),
            Header(10, "DATE", Table.Align.CENTER),
            Header(9, "DAY", Table.Align.CENTER),
            Header(16, "KIND", Table.Align.CENTER),
            Header(20, "DESCRIPTION", Table.Align.CENTER),
            Header(10, "AMOUNT", Table.Align.CENTER)
    )

    override fun printAll() = printExpensesWithMonthSummary(repository.getAll())

    override fun printByDate(startDate: LocalDate, endDate: LocalDate) {
        printExpensesWithPeriodSummary(repository.getByDate(startDate, endDate), startDate, endDate)
    }

    override fun printByKinds(kinds: List<String>, startDate: Optional<LocalDate>, endDate: Optional<LocalDate>) {
        if(startDate.isPresent && endDate.isPresent) {
            printExpensesWithPeriodSummary(repository.getByKinds(kinds, startDate, endDate), startDate.get(), endDate.get())
        } else {
            printExpensesWithMonthSummary(repository.getByKinds(kinds, startDate, endDate))
        }
    }

    override fun printAllKinds() {
        val kinds = repository.getAll().map { it.kind }.distinct()
        val table = SimpleTable(Table.LineWeight.BOLD, true)

        table.setHeader(listOf(Header(40, "ALL KINDS", Table.Align.CENTER)))

        kinds.forEach { kind ->
            table.addRow(listOf(Cell(kind, Table.Align.CENTER))).addSeparatingLine(Table.LineWeight.LIGHT)
        }

        print(table.build())

    }

    override fun printMonthsAmount() {
        val table = SimpleTable(Table.LineWeight.BOLD, true)

        table.setHeader(listOf(
                Header(15, "MONTH", Table.Align.CENTER),
                Header(15, "TOTAL AMOUNT", Table.Align.CENTER)
        ))

        repository.getAll().groupBy { it.date.month }.forEach {
            table.addRow(listOf(
                    Cell(it.key.name, Table.Align.CENTER),
                    Cell(formatAmount(it.value.map { it.amount }.sum()), Table.Align.CENTER)
            )).addSeparatingLine(Table.LineWeight.LIGHT)
        }

        print(table.build())
    }

    private fun printExpensesWithPeriodSummary(expenses: List<Expense>, startDate: LocalDate, endDate: LocalDate) {
        print(SimpleTable(Table.LineWeight.BOLD)
                .setHeader(listOf(Header(75, "FROM $startDate, TO $endDate", Table.Align.CENTER)))
                .build())

        val table = SimpleTable(Table.LineWeight.BOLD, true)
        table.setHeader(headers)

        expenses.forEach { expense ->
            table.addRow(listOf(
                    Cell(expense.id.toString(), Table.Align.CENTER),
                    Cell(expense.date.toString(), Table.Align.CENTER),
                    Cell(expense.date.dayOfWeek.toString(), Table.Align.CENTER),
                    Cell(expense.kind, Table.Align.CENTER),
                    Cell(expense.description, Table.Align.CENTER),
                    Cell(formatAmount(expense.amount), Table.Align.CENTER)
            )).addSeparatingLine(Table.LineWeight.LIGHT)
        }
        print(table.build())

        print(SimpleTable(Table.LineWeight.BOLD, true)
                .setHeader(listOf(Header(
                        75,
                        "TOTAL AMOUNT FROM $startDate, TO $endDate = ${formatAmount(expenses.map { it.amount }.sum())}",
                        Table.Align.CENTER
                )))
                .build())
    }

    private fun printExpensesWithMonthSummary(expenses: List<Expense>) {
        val table = SimpleTable(Table.LineWeight.BOLD, true)

        expenses.groupBy { it.date.month }.forEach {
            print(table
                    .setHeader(listOf(Header(75, "Expenses for ${it.key.name}", Table.Align.CENTER)))
                    .build())

            table.setHeader(headers)

            val monthExpenses = it.value
            monthExpenses.groupBy { it.date }

            it.value.forEach { expense ->
                table.addRow(listOf(
                        Cell(expense.id.toString(), Table.Align.CENTER),
                        Cell(expense.date.toString(), Table.Align.CENTER),
                        Cell(expense.date.dayOfWeek.toString(), Table.Align.CENTER),
                        Cell(expense.kind, Table.Align.CENTER),
                        Cell(expense.description, Table.Align.CENTER),
                        Cell(formatAmount(expense.amount), Table.Align.CENTER)
                )).addSeparatingLine(Table.LineWeight.LIGHT)
            }

            print(table.build())

            print(table
                    .setHeader(listOf(Header(75, "Total for ${it.key.name}: ${formatAmount(it.value.map { it.amount }.sum())}", Table.Align.CENTER)))
                    .build())
        }
    }

    private fun formatAmount(value: Double) = DecimalFormat("00.00").format(value)
}