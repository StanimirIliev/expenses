package com.stanimir.expenses.adapter.cli.apache

import org.apache.commons.cli.CommandLine
import java.io.File
import java.time.LocalDate

/**
 * @author Stanimir Iliev <stanimir.iliev@clouway.com />
 */

@Suppress("UNCHECKED_CAST")
fun <T> CommandLine.parseOption(option: String, type: Class<T>): T {
    try {
        val value = this.getOptionValue(option) ?: throw OptionValueNotFoundException(option)

        return cast(value, type)
    } catch (e: Exception) {
        if (e is OptionValueNotFoundException) throw e
        throw CannotParseOptionException(option)
    }
}

fun <T> CommandLine.parseOptionOrNull(option: String, type: Class<T>): T? {
    return try {
        parseOption(option, type)
    } catch (e: OptionValueNotFoundException) {
        null
    }
}

fun <T> CommandLine.parseOptionList(option: String, type: Class<T>): List<T> {
    return try {
        val value = this.getOptionValue(option) ?: throw OptionValueNotFoundException(option)

        val result = mutableListOf<T>()

        value.split(",").map { it.trim() }.filter { it.isNotEmpty() }.forEach {
            result.add(cast(it, type))
        }

        result
    } catch (e: Exception) {
        if (e is OptionValueNotFoundException) throw e
        throw CannotParseOptionException(option)
    }
}

@Suppress("UNCHECKED_CAST")
private fun <T> cast(value: String, clazz: Class<T>): T {
    return when (clazz) {
        Int::class.java -> value.toInt() as T
        Double::class.java -> value.replace(',', '.').toDouble() as T
        File::class.java -> File(value) as T
        LocalDate::class.java -> {
            val date = value.replace('.', '-').replace('/', '-')
            val splittedDate = date.split('-')
            val year = if (splittedDate[0].length == 2) "20${splittedDate[0]}" else splittedDate[0]
            val month = if (splittedDate[1].length == 1) "0${splittedDate[1]}" else splittedDate[1]
            val day = if (splittedDate[2].length == 1) "0${splittedDate[2]}" else splittedDate[2]

            LocalDate.parse("$year-$month-$day") as T
        }
        else -> value as T
    }
}

class CannotParseOptionException(val option: String) : RuntimeException()
class OptionValueNotFoundException(val option: String) : RuntimeException()