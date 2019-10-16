package com.stanimir.expenses.adapter.db.mongodb

import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters.`in`
import com.mongodb.client.model.Filters.and
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Filters.gte
import com.mongodb.client.model.Filters.lte
import com.mongodb.client.model.Sorts.ascending
import com.mongodb.client.model.Updates.combine
import com.mongodb.client.model.Updates.set
import com.stanimir.expenses.core.Expense
import com.stanimir.expenses.core.ExpenseNotFoundException
import com.stanimir.expenses.core.ExpenseRepository
import org.bson.Document
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.Date
import java.util.Optional

/**
 * @author Stanimir Iliev <stanimir.iliev@clouway.com />
 */

class PersistentExpenseRepository(private val db: MongoDatabase) : ExpenseRepository {
    private val collection = "Expenses"

    override fun register(id: String, kind: String, description: String, amount: Double, date: LocalDate): Expense {

        val document = Document(mapOf(
                "_id" to id,
                "kind" to kind,
                "description" to description,
                "amount" to amount,
                "date" to Date.from(date.atStartOfDay().toInstant(ZoneOffset.UTC))
        ))

        db.getCollection(collection).insertOne(document)

        return adapt(document)
    }

    override fun getByKinds(kinds: List<String>, startDate: Optional<LocalDate>, endDate: Optional<LocalDate>): List<Expense> {
        return if (kinds.isEmpty()) {
            emptyList()
        } else {
            val filters = mutableListOf(
                    `in`("kind", kinds),
                    eq("deletedOn", null)
            )

            if (startDate.isPresent && endDate.isPresent) {
                filters.add(gte("date", startDate.get()))
                filters.add(lte("date", endDate.get()))
            }

            db.getCollection(collection)
                    .find(and(*filters.toTypedArray()))
                    .sort(ascending("date"))
                    .map { adapt(it) }.toList()
        }
    }

    override fun getByDate(startDate: LocalDate, endDate: LocalDate): List<Expense> {
        return db.getCollection(collection)
                .find(and(
                        gte("date", startDate),
                        lte("date", endDate),
                        eq("deletedOn", null)
                ))
                .sort(ascending("date"))
                .map { adapt(it) }.toList()
    }

    override fun getById(id: String): Optional<Expense> {
        val foundDoc = db.getCollection(collection)
                .find(eq("_id", id))
                .firstOrNull()

        return if (foundDoc == null) {
            Optional.empty()
        } else {
            Optional.of(adapt(foundDoc))
        }
    }

    override fun getAll(): List<Expense> {
        return db.getCollection(collection)
                .find(eq("deletedOn", null))
                .sort(ascending("date"))
                .map { adapt(it) }.toList()
    }

    override fun update(id: String, kind: String, description: String, amount: Double, date: LocalDate) {
        db.getCollection(collection)
                .findOneAndUpdate(and(
                        eq("_id", id),
                        eq("deletedOn", null)
                ), combine(
                        set("kind", kind),
                        set("description", description),
                        set("amount", amount),
                        set("date", Date.from(date.atStartOfDay().toInstant(ZoneOffset.UTC)))
                )) ?: throw ExpenseNotFoundException(id)
    }

    override fun delete(id: String) {
        db.getCollection(collection)
                .findOneAndDelete(
                        and(
                                eq("_id", id),
                                eq("deletedOn", null)
                        )
                )
                ?: throw ExpenseNotFoundException(id)
    }

    private fun adapt(document: Document): Expense {
        return Expense(
                document.getString("_id").toString(),
                document.getString("kind"),
                document.getString("description"),
                document.getDouble("amount"),
                document.getDate("date").toInstant().atZone(ZoneId.of("UTC")).toLocalDate()
        )
    }
}