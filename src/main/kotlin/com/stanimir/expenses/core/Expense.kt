package com.stanimir.expenses.core

import java.time.LocalDate

/**
 * @author Stanimir Iliev <stanimir.iliev@clouway.com />
 */

data class Expense(
        val id: String = "",
        val kind: String = "",
        val description: String = "",
        val amount: Double = 0.0,
        val date: LocalDate = LocalDate.now(),
        val deletedOn: LocalDate? = null
)