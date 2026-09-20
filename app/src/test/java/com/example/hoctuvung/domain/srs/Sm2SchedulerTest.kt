package com.example.hoctuvung.domain.srs

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class Sm2SchedulerTest {

    private val today = 20000L

    private fun answer(state: SrsState, quality: Int) =
        Sm2Scheduler.review(state, quality, today)

    @Test
    fun firstCorrect_scheduledTomorrow() {
        val r = answer(SrsState(), 5)
        assertEquals(1, r.state.repetitions)
        assertEquals(1, r.state.intervalDays)
        assertEquals(today + 1, r.dueDay)
        assertEquals(2.6, r.state.easeFactor, 1e-9)
    }

    @Test
    fun secondCorrect_scheduledInSixDays() {
        val s1 = answer(SrsState(), 5).state
        val r = answer(s1, 5)
        assertEquals(2, r.state.repetitions)
        assertEquals(6, r.state.intervalDays)
        assertEquals(today + 6, r.dueDay)
        assertEquals(2.7, r.state.easeFactor, 1e-9)
    }

    @Test
    fun thirdCorrect_intervalGrowsByEase() {
        val s1 = answer(SrsState(), 5).state
        val s2 = answer(s1, 5).state
        val r = answer(s2, 5)
        assertEquals(3, r.state.repetitions)
        assertEquals(17, r.state.intervalDays)
        assertEquals(today + 17, r.dueDay)
        assertEquals(2.8, r.state.easeFactor, 1e-9)
    }

    @Test
    fun wrongAfterLearning_resetsAndCountsLapse() {
        val learned = SrsState(repetitions = 3, intervalDays = 17, easeFactor = 2.8, lapses = 0)
        val r = answer(learned, 1)
        assertEquals(0, r.state.repetitions)
        assertEquals(1, r.state.intervalDays)
        assertEquals(1, r.state.lapses)
        assertEquals(2.26, r.state.easeFactor, 1e-9)
        assertEquals(today + 1, r.dueDay)
    }

    @Test
    fun wrongOnNewCard_doesNotCountLapse() {
        val r = answer(SrsState(), 1)
        assertEquals(0, r.state.lapses)
        assertEquals(1, r.state.intervalDays)
    }

    @Test
    fun quality4_keepsEaseUnchanged() {
        val r = answer(SrsState(), 4)
        assertEquals(1, r.state.repetitions)
        assertEquals(2.5, r.state.easeFactor, 1e-9)
    }

    @Test
    fun easeFactor_neverBelowMinimum() {
        val hard = SrsState(repetitions = 2, intervalDays = 6, easeFactor = 1.3, lapses = 0)
        val r = answer(hard, 1)
        assertEquals(1.3, r.state.easeFactor, 1e-9)
    }

    @Test
    fun invalidQuality_throws() {
        assertThrows(IllegalArgumentException::class.java) { answer(SrsState(), 6) }
    }
}