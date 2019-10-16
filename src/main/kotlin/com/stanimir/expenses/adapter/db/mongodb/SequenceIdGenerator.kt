package com.stanimir.expenses.adapter.db.mongodb

import com.mongodb.client.MongoDatabase
import com.stanimir.expenses.core.IdGenerator
import org.bson.Document

/**
 * @author Stanimir Iliev <stambeto2197@gmail.com />
 */
 
class SequenceIdGenerator(private val db: MongoDatabase): IdGenerator {
    private val collection = "Ids"

    override fun generate(): String {
        val col = db.getCollection(collection)
        val newId = col.countDocuments() + 1L

        col.insertOne(Document("value", newId))

        return newId.toString()
    }
}