package com.example.hoctuvung.domain.srs

import kotlin.math.roundToInt

// Trang thai lap lai cua mot the (khop voi cac cot trong CardEntity)
data class SrsState(
    val repetitions: Int = 0,
    val intervalDays: Int = 0,
    val easeFactor: Double = 2.5,
    val lapses: Int = 0
)

data class SrsResult(
    val state: SrsState,
    val dueDay: Long
)

object Sm2Scheduler {

    const val MIN_EASE = 1.3

    // quality: 0..5 (5 = nho ngay, 1 = sai). today: epoch day cua hom nay.
    fun review(state: SrsState, quality: Int, today: Long): SrsResult {
        require(quality in 0..5) { "quality phai tu 0 den 5" }

        val newEase = maxOf(
            MIN_EASE,
            state.easeFactor + 0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02)
        )

        val newState = if (quality < 3) {
            // Sai: quay lai tu dau, ngay mai hoi lai
            SrsState(
                repetitions = 0,
                intervalDays = 1,
                easeFactor = newEase,
                lapses = if (state.repetitions > 0) state.lapses + 1 else state.lapses
            )
        } else {
            val newReps = state.repetitions + 1
            val newInterval = when (newReps) {
                1 -> 1
                2 -> 6
                else -> (state.intervalDays * newEase).roundToInt()
            }
            SrsState(newReps, newInterval, newEase, state.lapses)
        }

        return SrsResult(newState, today + newState.intervalDays)
    }
}