package com.stanimir.expenses.core

/**
 * @author Stanimir Iliev <stanimir.iliev@clouway.com />
 */

data class ExpenseNotFoundException(val id: Int): RuntimeException()