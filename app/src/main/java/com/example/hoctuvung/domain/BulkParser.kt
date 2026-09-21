package com.example.hoctuvung.domain

data class ParsedCard(
    val word: String,
    val meaningVi: String,
    val exampleSentence: String?
)

data class BulkParseResult(
    val cards: List<ParsedCard>,
    val invalidLines: List<Int>
)

object BulkParser {

    // Moi dong: word | nghia | cau vi du (khong bat buoc)
    fun parse(text: String): BulkParseResult {
        val cards = mutableListOf<ParsedCard>()
        val invalid = mutableListOf<Int>()

        text.lines().forEachIndexed { index, raw ->
            val line = raw.trim()
            if (line.isEmpty()) return@forEachIndexed

            val parts = line.split("|", limit = 3).map { it.trim() }
            val word = parts.getOrNull(0).orEmpty()
            val meaning = parts.getOrNull(1).orEmpty()
            val example = parts.getOrNull(2)?.ifEmpty { null }

            if (word.isEmpty() || meaning.isEmpty()) {
                invalid.add(index + 1)
            } else {
                cards.add(ParsedCard(word, meaning, example))
            }
        }
        return BulkParseResult(cards, invalid)
    }
}