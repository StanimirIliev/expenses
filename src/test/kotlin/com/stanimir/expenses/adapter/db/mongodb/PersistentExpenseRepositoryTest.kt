package com.stanimir.expenses.adapter.db.mongodb

import com.mongodb.client.MongoClients
import com.mongodb.client.MongoDatabase
import com.stanimir.expenses.core.ExpenseNotFoundException
import com.stanimir.expenses.core.ExpenseRepository
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.util.Optional

/**
 * @author Stanimir Iliev (stanimir.iliev@clouway.com)
 */

class PersistentExpenseRepositoryTest {

    private val db: MongoDatabase
    private val repository: ExpenseRepository

    init {

        val dbClient = MongoClients.create("mongodb://127.0.0.1")
        db = dbClient.getDatabase("local")

        repository = PersistentExpenseRepository(db)
    }

    @Before
    fun setUp() {
        db.getCollection("Expenses").drop()
    }

    @Test
    fun getExpenseThatWasRegistered() {
        val expense = repository.register(
                "::id::",
                "::kind::",
                "::description::",
                0.49,
                LocalDate.of(2018, 5, 18)
        )

        assertThat(repository.getById("::id::").get(), `is`(equalTo(expense)))
    }

    @Test
    fun getExpenseForSpecificDate() {
        val expense = repository.register(
                "::id::",
                "::other_kind::",
                "::description::",
                4.6,
                LocalDate.of(2018, 5, 16)
        )
        repository.register(
                "::id-2::",
                "::kind::",
                "::description::",
                5.1,
                LocalDate.of(2018, 5, 18)
        )

        assertThat(repository.getByDate(
                LocalDate.of(2018, 5, 16),
                LocalDate.of(2018, 5, 17)
        ), `is`(equalTo(listOf(expense))))
    }

    @Test
    fun getExpensesForSpecifiedKinds() {
        val expense1 = repository.register(
                "::id::",
                "::kind::",
                "::description::",
                5.1,
                LocalDate.of(2018, 5, 18)
        )
        val expense2 = repository.register(
                "::id-2::",
                "::other_kind::",
                "::description::",
                4.6,
                LocalDate.of(2018, 5, 16)
        )
        repository.register(
                "::id-3::",
                "::third_kind::",
                "::description::",
                3.5,
                LocalDate.of(2018, 5, 15)
        )

        assertThat(repository.getByKinds(listOf("::kind::", "::other_kind::")), `is`(equalTo(listOf(expense2, expense1))))
    }

    @Test
    fun getExpensesForSpecifiedKindsAndDates() {
        val expense1 = repository.register(
                "::id::",
                "::kind::",
                "::description::",
                5.1,
                LocalDate.of(2018, 5, 18)
        )
        val expense2 = repository.register(
                "::id-2::",
                "::other_kind::",
                "::description::",
                4.6,
                LocalDate.of(2018, 5, 16)
        )
        repository.register(
                "::id-3::",
                "::kind::",
                "::description::",
                3.5,
                LocalDate.of(2018, 5, 15)
        )

        assertThat(repository.getByKinds(
                listOf("::kind::", "::other_kind::"),
                Optional.of(LocalDate.of(2018, 5, 16)),
                Optional.of(LocalDate.of(2018, 5, 18))
        ), `is`(equalTo(listOf(expense2, expense1))))
    }

    @Test
    fun tryToGetExpensesForSpecifiedKindsWithOneDateProvided() {
        val expense1 = repository.register(
                "::id::",
                "::kind::",
                "::description::",
                5.1,
                LocalDate.of(2018, 5, 18)
        )
        val expense2 = repository.register(
                "::id-2::",
                "::other_kind::",
                "::description::",
                4.6,
                LocalDate.of(2018, 5, 16)
        )
        val expense3 = repository.register(
                "::id-3::",
                "::kind::",
                "::description::",
                3.5,
                LocalDate.of(2018, 5, 15)
        )

        assertThat(repository.getByKinds(
                listOf("::kind::", "::other_kind::"),
                Optional.of(LocalDate.of(2018, 5, 16))
        ), `is`(equalTo(listOf(expense3, expense2, expense1))))
    }

    @Test
    fun getExpensesWithEmptyKindsListProvided() {
        repository.register(
                "::id::",
                "::kind::",
                "::description::",
                5.1,
                LocalDate.of(2018, 5, 18)
        )

        assertThat(repository.getByKinds(listOf()), `is`(equalTo(emptyList())))
    }

    @Test
    fun getAllExpenses() {
        val expense1 = repository.register(
                "::id::",
                "::kind::",
                "::description::",
                5.1,
                LocalDate.of(2018, 5, 18)
        )
        val expense2 = repository.register(
                "::id-2::",
                "::other_kind::",
                "::description::",
                4.6,
                LocalDate.of(2018, 5, 16)
        )

        assertThat(repository.getAll(), `is`(equalTo(listOf(expense2, expense1))))
    }

    @Test
    fun tryToGetExpenseForDateOutOfRange() {
        repository.register(
                "::id::",
                "::other_kind::",
                "::description::",
                4.6,
                LocalDate.of(2018, 4, 18)
        )

        assertThat(repository.getByDate(
                LocalDate.of(2018, 5, 1),
                LocalDate.of(2018, 5, 30)
        ), `is`(equalTo(emptyList())))
    }

    @Test
    fun getExpensesKindNoCaseSensitive() {
        val expense = repository.register(
                "::id::",
                "::Kind::",
                "::description::",
                5.1,
                LocalDate.of(2018, 5, 18)
        )
        repository.register(
                "::id-2::",
                "::other_kind::",
                "::description::",
                4.6,
                LocalDate.of(2018, 5, 16)
        )

        assertThat(repository.getByKinds(listOf("::KiNd::")), `is`(equalTo(listOf(expense))))
    }

    @Test
    fun updateExpense() {
        val expense = repository.register(
                "::id::",
                "::kind::",
                "::description::",
                5.0,
                LocalDate.of(2018, 5, 3)
        )
        val updatedExpense = expense.copy(
                kind = "::new_kind::",
                description = "::new_description::",
                date = LocalDate.of(2018, 6, 3)
        )

        repository.update(
                updatedExpense.id,
                updatedExpense.kind,
                updatedExpense.description,
                updatedExpense.amount,
                updatedExpense.date
        )
        assertThat(repository.getById(expense.id).get(), `is`(equalTo(updatedExpense)))
    }

    @Test(expected = ExpenseNotFoundException::class)
    fun tryToUpdateExpenseWithUnknownId() {
        repository.update("wrong id", "", "", 0.0, LocalDate.now())
    }

    @Test(expected = ExpenseNotFoundException::class)
    fun deleteExpense() {
        val date = LocalDate.now()
        repository.register(
                "::id::",
                "::kind::",
                "::description::",
                1.0,
                date
        )

        repository.delete("::id::")

        assertThat(repository.getById("::id::").isPresent, `is`(equalTo(false)))
        assertThat(repository.getAll(), `is`(equalTo(emptyList())))
        assertThat(repository.getByKinds(listOf("::kind::")), `is`(equalTo(emptyList())))
        assertThat(repository.getByDate(date, date), `is`(equalTo(emptyList())))
        repository.update("::id::", "", "", 0.0, date)// throws exception
    }

    @Test(expected = ExpenseNotFoundException::class)
    fun tryToDeleteUnknownExpense() {
        repository.delete("wrong id")
    }

    @Test
    fun orderExpensesByDate() {
        val expense1 = repository.register(
                "::id::",
                "::kind::",
                "",
                1.0,
                LocalDate.of(2018, 5, 10)
        )
        val expense2 = repository.register(
                "::id-2::",
                "::kind::",
                "",
                1.0,
                LocalDate.of(2018, 5, 9)
        )

        assertThat(repository.getAll(), `is`(equalTo(listOf(expense2, expense1))))
        assertThat(repository.getByKinds(listOf("::kind::")), `is`(equalTo(listOf(expense2, expense1))))
        assertThat(repository.getByKinds(
                listOf("::kind::"),
                Optional.of(LocalDate.of(2018, 5, 9)),
                Optional.of(LocalDate.of(2018, 5, 10))
        ), `is`(equalTo(listOf(expense2, expense1))))
        assertThat(repository.getByDate(
                LocalDate.of(2018, 5, 9),
                LocalDate.of(2018, 5, 10)
        ), `is`(equalTo(listOf(expense2, expense1))))
    }
}