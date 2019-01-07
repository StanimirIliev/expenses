package com.stanimir.expenses.core

import java.time.LocalDate
import java.util.*

/**
 * @author Stanimir Iliev <stanimir.iliev@clouway.com />
 */

interface ExpenseRepository {
    /**
     * Save expense to persistent layer
     *
     * @param id the id of the expense
     * @param kind the kind of the expense
     * @param description the description of the expense
     * @param amount the amount of the expense
     * @param date the date of the expense
     * @return the registered expense
     */
    fun register(id: String, kind: String, description: String, amount: Double, date: LocalDate): Expense

    /**
     * Get expenses filtered by kind. If both start and end dates are provided
     * then results are filtered by their date.
     *
     * @param kinds the allowed kinds
     * @param startDate start date, including
     * @param endDate end date, including
     */
    fun getByKinds(kinds: List<String>, startDate: Optional<LocalDate> = Optional.empty(), endDate: Optional<LocalDate> = Optional.empty()): List<Expense>

    /**
     * Get expenses from given period of time and for specific kind
     *
     * @param startDate the date after which expenses will show, including
     * @param endDate the date before which expenses will show, including
     * @return list of [Expense]
     */
    fun getByDate(startDate: LocalDate, endDate: LocalDate): List<Expense>

    /**
     * Get expense by its id
     *
     * @param id the id of the desired expense
     * @return optional expense
     */
    fun getById(id: String): Optional<Expense>

    /**
     * Gets all expenses
     *
     * @return list of [Expense]
     */
    fun getAll(): List<Expense>

    /**
     * Updates expense
     *
     * @param id the id of the expense to update
     * @param kind the new kind
     * @param description the new description
     * @param amount the new amount
     * @param date the new date
     * @throws [ExpenseNotFoundException]
     */
    fun update(id: String, kind: String, description: String, amount: Double, date: LocalDate)

    /**
     * Marks expense as deleted
     *
     * @param id the id of the expense to delete
     * @throws [ExpenseNotFoundException]
     */
    fun delete(id: String)
}