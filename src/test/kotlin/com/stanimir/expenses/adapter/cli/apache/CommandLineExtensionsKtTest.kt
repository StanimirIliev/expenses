package com.stanimir.expenses.adapter.cli.apache

import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Option
import org.apache.commons.cli.Options
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import java.io.File
import java.time.LocalDate

/**
 * @author Stanimir Iliev (stanimir.iliev@clouway.com)
 */

class CommandLineExtensionsKtTest {

    private val options = Options().addOption(Option("t", "test", true, "Test"))
    private val parser = DefaultParser()

    @Test
    fun parseOptionToDoubleWithComma() {
        val args = arrayOf("-t", "2,5")
        val cmd = parser.parse(options, args)

        assertThat(cmd.parseOption("t", Double::class.java), `is`(equalTo(2.5)))
    }

    @Test
    fun parseOptionToDoubleWithPoint() {
        val args = arrayOf("-t", "2.5")
        val cmd = parser.parse(options, args)

        assertThat(cmd.parseOption("t", Double::class.java), `is`(equalTo(2.5)))
    }

    @Test
    fun parseOptionToLocalDateWithDashes() {
        val args = arrayOf("-t", "2018-05-06")
        val cmd = parser.parse(options, args)

        assertThat(cmd.parseOption("t", LocalDate::class.java), `is`(equalTo(LocalDate.of(2018, 5, 6))))
    }

    @Test
    fun parseOptionToLocalDateWithDots() {
        val args = arrayOf("-t", "2018.05.06")
        val cmd = parser.parse(options, args)

        assertThat(cmd.parseOption("t", LocalDate::class.java), `is`(equalTo(LocalDate.of(2018, 5, 6))))
    }

    @Test
    fun parseOptionToLocalDateWithForwardSlashes() {
        val args = arrayOf("-t", "2018/05/06")
        val cmd = parser.parse(options, args)

        assertThat(cmd.parseOption("t", LocalDate::class.java), `is`(equalTo(LocalDate.of(2018, 5, 6))))
    }

    @Test
    fun parseShortedDatesWithDots() {
        val args = arrayOf("-t", "18.5.6")
        val cmd = parser.parse(options, args)

        assertThat(cmd.parseOption("t", LocalDate::class.java), `is`(equalTo(LocalDate.of(2018, 5, 6))))
    }

    @Test
    fun parseShortedDatesWithSlashes() {
        val args = arrayOf("-t", "18/5/6")
        val cmd = parser.parse(options, args)

        assertThat(cmd.parseOption("t", LocalDate::class.java), `is`(equalTo(LocalDate.of(2018, 5, 6))))
    }

    @Test
    fun parseShortedDatesWithDashes() {
        val args = arrayOf("-t", "18-5-6")
        val cmd = parser.parse(options, args)

        assertThat(cmd.parseOption("t", LocalDate::class.java), `is`(equalTo(LocalDate.of(2018, 5, 6))))
    }

    @Test(expected = CannotParseOptionException::class)
    fun tryToParseOptionToLocalDateWithInvalidFormat() {
        val args = arrayOf("-t", "2018.50.60")
        val cmd = parser.parse(options, args)

        cmd.parseOption("t", LocalDate::class.java)
    }

    @Test
    fun parseOptionToString() {
        val args = arrayOf("-t", "example of string")
        val cmd = parser.parse(options, args)

        assertThat(cmd.parseOption("t", String::class.java), `is`(equalTo("example of string")))
    }

    @Test
    fun parseOptionToFile() {
        val args = arrayOf("-t", "/someFile")
        val cmd = parser.parse(options, args)

        assertThat(cmd.parseOption("t", File::class.java), `is`(equalTo(File("/someFile"))))
    }

    @Test
    fun parseOptionOrNullWithPersistingOption() {
        val args = arrayOf("-t", "5")
        val cmd = parser.parse(options, args)

        assertThat(cmd.parseOptionOrNull("t", Int::class.java), `is`(equalTo(5)))
    }

    @Test
    fun parseOptionOrNullWithAbsentOption() {
        val cmd = parser.parse(options, emptyArray())

        assertThat(cmd.parseOptionOrNull("t", Int::class.java), `is`(nullValue()))
    }

    @Test
    fun parseListOfStrings() {
        val args = arrayOf("-t", "string1,string2")
        val cmd = parser.parse(options, args)

        assertThat(cmd.parseOptionList("t", String::class.java), `is`(equalTo(listOf("string1", "string2"))))
    }

    @Test
    fun parseListOfStringsWithCommasAndSpaces() {
        val args = arrayOf("-t", "string1  ,  string2")
        val cmd = parser.parse(options, args)

        assertThat(cmd.parseOptionList("t", String::class.java), `is`(equalTo(listOf("string1", "string2"))))
    }

    @Test
    fun parseListOfIntegers() {
        val args = arrayOf("-t", "3,6, 8  , 2")
        val cmd = parser.parse(options, args)

        assertThat(cmd.parseOptionList("t", Int::class.java), `is`(equalTo(listOf(3, 6, 8, 2))))
    }

    @Test
    fun tryToParseListWithoutCommas() {
        val args = arrayOf("-t", "5")
        val cmd = parser.parse(options, args)

        assertThat(cmd.parseOptionList("t", Int::class.java), `is`(equalTo(listOf(5))))
    }
}