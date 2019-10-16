package com.stanimir.expenses.adapter.db.mongodb

import com.mongodb.client.MongoClients
import com.mongodb.client.MongoDatabase
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

/**
 * @author Stanimir Iliev (stanimir.iliev@clouway.com)
 */

class SequenceIdGeneratorTest {

    private val db: MongoDatabase
    private val idGenerator: SequenceIdGenerator

    init {

        val dbClient = MongoClients.create("mongodb://127.0.0.1")
        db = dbClient.getDatabase("local")

        idGenerator = SequenceIdGenerator(db)
    }

    @Before
    fun setUp() {
        db.getCollection("Ids").drop()
        db.createCollection("Ids")
    }

    @Test
    fun generateSequenceIds() {
        val firstIdGenerated = idGenerator.generate()
        val secondIdGenerated = idGenerator.generate()
        val thirdIdGenerated = idGenerator.generate()

        assertThat(firstIdGenerated, `is`("1"))
        assertThat(secondIdGenerated, `is`("2"))
        assertThat(thirdIdGenerated, `is`("3"))
    }
}