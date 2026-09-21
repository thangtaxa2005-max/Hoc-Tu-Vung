package com.example.hoctuvung.domain

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AnswerCheckerTest {

    @Test
    fun exactMatch_isCorrect() {
        assertTrue(AnswerChecker.isCorrect("resign", "resign"))
    }

    @Test
    fun caseIsIgnored() {
        assertTrue(AnswerChecker.isCorrect("RESIGN", "resign"))
        assertTrue(AnswerChecker.isCorrect("resign", "Resign"))
    }

    @Test
    fun surroundingSpacesAreIgnored() {
        assertTrue(AnswerChecker.isCorrect("   resign  ", "resign"))
    }

    @Test
    fun multipleInnerSpaces_areCollapsed() {
        assertTrue(AnswerChecker.isCorrect("look   forward  to", "look forward to"))
    }

    @Test
    fun differentWord_isWrong() {
        assertFalse(AnswerChecker.isCorrect("resing", "resign"))
    }

    @Test
    fun emptyAnswer_isWrong() {
        assertFalse(AnswerChecker.isCorrect("", "resign"))
        assertFalse(AnswerChecker.isCorrect("    ", "resign"))
    }

    @Test
    fun missingWordInPhrase_isWrong() {
        assertFalse(AnswerChecker.isCorrect("look forward", "look forward to"))
    }
}