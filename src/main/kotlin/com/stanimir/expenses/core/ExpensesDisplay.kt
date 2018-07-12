package com.stanimir.expenses.core

import java.time.LocalDate
import java.util.*

/**
 * Format given list with expenses in string
 *
 * @author Stanimir Iliev <stanimir.iliev@clouway.com />
 */

interface ExpensesDisplay {
    fun printAll()
    fun printByDate(startDate: LocalDate, endDate: LocalDate)
    fun printByKinds(kinds: List<String>, startDate: Optional<LocalDate> = Optional.empty(), endDate: Optional<LocalDate> = Optional.empty())
    fun printAllKinds()
    fun printMonthsAmount()
}