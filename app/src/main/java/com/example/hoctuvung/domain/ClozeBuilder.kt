package com.example.hoctuvung.domain

object ClozeBuilder {
    fun build(word: String, exampleSentence: String?): String? {
        if (exampleSentence.isNullOrBlank()) return null
        
        // Dùng Regex với \b để đảm bảo chỉ khớp từ độc lập 
        // (ví dụ: tìm "cat" sẽ không thay thế chữ "cat" trong "category")
        val regex = Regex("\\b${Regex.escape(word)}\\b", RegexOption.IGNORE_CASE)
        
        val result = regex.replace(exampleSentence, "_____")
        
        // Nếu câu không chứa từ đó (có thể do lỗi nhập liệu), trả về nguyên gốc để dự phòng
        return if (result == exampleSentence) exampleSentence else result
    }
}
