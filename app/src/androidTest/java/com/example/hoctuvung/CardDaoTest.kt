package com.example.hoctuvung

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.hoctuvung.data.local.AppDatabase
import com.example.hoctuvung.data.local.CardDao
import com.example.hoctuvung.data.local.CardEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CardDaoTest {

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
    fun insert_thenReadBack() {
        runBlocking {
            val id = dao.insert(CardEntity(word = "resign", meaningVi = "tu chuc"))
            val card = dao.getById(id)
            assertNotNull(card)
            assertEquals("resign", card!!.word)
            assertEquals(2.5, card.easeFactor, 0.0)
            assertEquals(0, card.repetitions)
        }
    }

    @Test
    fun duplicateWord_isIgnored_caseInsensitive() {
        runBlocking {
            val first = dao.insert(CardEntity(word = "Resign", meaningVi = "a"))
            val second = dao.insert(CardEntity(word = "resign", meaningVi = "b"))
            assertTrue(first > 0)
            assertEquals(-1L, second)
            assertEquals(1, dao.observeAll().first().size)
        }
    }

    @Test
    fun observeAll_returnsNewestFirst() {
        runBlocking {
            dao.insert(CardEntity(word = "old", meaningVi = "x", createdAt = 1000))
            dao.insert(CardEntity(word = "new", meaningVi = "y", createdAt = 2000))
            val list = dao.observeAll().first()
            assertEquals("new", list[0].word)
            assertEquals("old", list[1].word)
        }
    }
}