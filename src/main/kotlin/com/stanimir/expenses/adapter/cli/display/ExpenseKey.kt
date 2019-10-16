package com.stanimir.expenses.adapter.cli.display

import java.time.LocalDate
import java.time.Month

/**
 * @author stanimir.i (stanimir.iliev.1997@gmail.com)
 */

data class ExpenseKey(val month: Month, val year: Int) {
    constructor(date: LocalDate) : this(date.month, date.year)
}