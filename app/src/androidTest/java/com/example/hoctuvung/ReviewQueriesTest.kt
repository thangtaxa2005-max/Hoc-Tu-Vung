package com.example.hoctuvung

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.hoctuvung.data.local.AppDatabase
import com.example.hoctuvung.data.local.CardDao
import com.example.hoctuvung.data.local.CardEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReviewQueriesTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: CardDao

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        dao = db.cardDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun dueCards_onlyReviewedAndDue_oldestDueFirst() {
        runBlocking {
            dao.insert(CardEntity(word = "new1", meaningVi = "x"))
            dao.insert(CardEntity(word = "dueToday", meaningVi = "x", intervalDays = 1, dueDay = 100))
            dao.insert(CardEntity(word = "dueLongAgo", meaningVi = "x", intervalDays = 1, dueDay = 50))
            dao.insert(CardEntity(word = "notDueYet", meaningVi = "x", intervalDays = 6, dueDay = 200))

            val due = dao.getDueCards(today = 100)

            assertEquals(listOf("dueLongAgo", "dueToday"), due.map { it.word })
        }
    }

    @Test
    fun newCards_respectLimit_oldestFirst() {
        runBlocking {
            dao.insert(CardEntity(word = "b", meaningVi = "x", createdAt = 2000))
            dao.insert(CardEntity(word = "a", meaningVi = "x", createdAt = 1000))
            dao.insert(CardEntity(word = "c", meaningVi = "x", createdAt = 3000))
            dao.insert(CardEntity(word = "reviewed", meaningVi = "x", intervalDays = 1, dueDay = 100))

            val fresh = dao.getNewCards(limit = 2)

            assertEquals(listOf("a", "b"), fresh.map { it.word })
        }
    }

    @Test
    fun updatedCard_leavesNewList_andJoinsDueList() {
        runBlocking {
            val id = dao.insert(CardEntity(word = "w", meaningVi = "x"))
            val card = dao.getById(id)!!

            dao.update(card.copy(intervalDays = 1, repetitions = 1, dueDay = 101))

            assertEquals(0, dao.getNewCards(limit = 10).size)
            assertEquals(0, dao.getDueCards(today = 100).size)
            assertEquals(1, dao.getDueCards(today = 101).size)
        }
    }
}