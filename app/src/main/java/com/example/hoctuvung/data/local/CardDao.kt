package com.example.hoctuvung.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(card: CardEntity): Long

    @Update
    suspend fun update(card: CardEntity)

    @Delete
    suspend fun delete(card: CardEntity)

    @Query("SELECT * FROM cards ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<CardEntity>>

    @Query("SELECT * FROM cards WHERE id = :id")
    suspend fun getById(id: Long): CardEntity?

    @Query("SELECT * FROM cards WHERE intervalDays > 0 AND dueDay <= :today ORDER BY dueDay ASC, id ASC")
    suspend fun getDueCards(today: Long): List<CardEntity>

    @Query("SELECT * FROM cards WHERE intervalDays = 0 ORDER BY createdAt ASC, id ASC LIMIT :limit")
    suspend fun getNewCards(limit: Int): List<CardEntity>

    @Query("SELECT COUNT(*) FROM cards WHERE intervalDays > 0 AND dueDay <= :today")
    fun observeDueCount(today: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM cards WHERE intervalDays = 0")
    fun observeNewCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM cards WHERE intervalDays >= 21")
    fun observeMasteredCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM cards WHERE intervalDays > 0 AND intervalDays < 21")
    fun observeLearningCount(): Flow<Int>

    @Query("SELECT * FROM cards WHERE lapses > 0 ORDER BY lapses DESC LIMIT 5")
    fun getMostFailedCards(): Flow<List<CardEntity>>
}
