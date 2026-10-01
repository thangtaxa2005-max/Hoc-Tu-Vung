package com.example.hoctuvung.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

data class DailyReviewCount(val dateStr: String, val count: Int)

@Dao
interface ReviewLogDao {
    @Insert
    suspend fun insertLog(log: ReviewLogEntity)

    @Query("SELECT date(reviewedAt / 1000, 'unixepoch', 'localtime') as dateStr, COUNT(*) as count FROM review_logs GROUP BY dateStr ORDER BY dateStr DESC LIMIT 7")
    fun getDailyReviewCounts(): Flow<List<DailyReviewCount>>
}
