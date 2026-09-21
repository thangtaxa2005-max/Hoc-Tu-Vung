package com.example.hoctuvung.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BulkParserTest {

    @Test
    fun threeFields_parsed() {
        val r = BulkParser.parse("supervisor | giam sat | The supervisor approved the budget.")
        assertEquals(1, r.cards.size)
        assertEquals("supervisor", r.cards[0].word)
        assertEquals("giam sat", r.cards[0].meaningVi)
        assertEquals("The supervisor approved the budget.", r.cards[0].exampleSentence)
        assertEquals(0, r.invalidLines.size)
    }

    @Test
    fun twoFields_exampleIsNull() {
        val r = BulkParser.parse("resign | tu chuc")
        assertEquals(1, r.cards.size)
        assertNull(r.cards[0].exampleSentence)
    }

    @Test
    fun spacesAreTrimmed() {
        val r = BulkParser.parse("   resign   |   quit   ")
        assertEquals("resign", r.cards[0].word)
        assertEquals("quit", r.cards[0].meaningVi)
    }

    @Test
    fun blankLinesAreSkipped() {
        val r = BulkParser.parse("a|b\n\n   \nc|d")
        assertEquals(2, r.cards.size)
        assertEquals(0, r.invalidLines.size)
    }

    @Test
    fun lineWithoutSeparator_isInvalid() {
        val r = BulkParser.parse("a|b\nbroken line\nc|d")
        assertEquals(2, r.cards.size)
        assertEquals(listOf(2), r.invalidLines)
    }

    @Test
    fun emptyWordOrMeaning_isInvalid() {
        val r = BulkParser.parse("|meaning\nword|\nword2|   ")
        assertEquals(0, r.cards.size)
        assertEquals(listOf(1, 2, 3), r.invalidLines)
    }

    @Test
    fun invalidLineNumber_countsBlankLines() {
        val r = BulkParser.parse("a|b\n\nbroken")
        assertEquals(listOf(3), r.invalidLines)
    }

    @Test
    fun windowsLineEndings_work() {
        val r = BulkParser.parse("a|b\r\nc|d")
        assertEquals(2, r.cards.size)
    }

    @Test
    fun extraSeparators_stayInExample() {
        val r = BulkParser.parse("a|b|c|d")
        assertEquals("c|d", r.cards[0].exampleSentence)
    }

    @Test
    fun emptyInput_givesNothing() {
        val r = BulkParser.parse("")
        assertEquals(0, r.cards.size)
        assertEquals(0, r.invalidLines.size)
    }
}