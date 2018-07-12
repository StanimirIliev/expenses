package com.stanimir.expenses.core

import java.time.LocalDate

/**
 * @author Stanimir Iliev <stanimir.iliev@clouway.com />
 */

data class Expense(
        val id: Int,
        val kind: String,
        val description: String,
        val amount: Double,
        val date: LocalDate,
        val deletedOn: LocalDate? = null
)