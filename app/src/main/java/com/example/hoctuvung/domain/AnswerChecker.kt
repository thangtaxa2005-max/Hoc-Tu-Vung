package com.example.hoctuvung.domain

object AnswerChecker {

    private val spaces = Regex("\\s+")

    private fun normalize(s: String): String =
        s.trim().lowercase().replace(spaces, " ")

    // Dung khi khop hoan toan sau khi chuan hoa (khong phan biet hoa thuong, gop khoang trang)
    fun isCorrect(answer: String, expected: String): Boolean {
        val a = normalize(answer)
        return a.isNotEmpty() && a == normalize(expected)
    }
}