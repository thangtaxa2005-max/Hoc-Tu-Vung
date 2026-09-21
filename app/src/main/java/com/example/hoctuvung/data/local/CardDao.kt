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

    // Tra ve id cua the moi, hoac -1 neu tu da ton tai (bi trung)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(card: CardEntity): Long

    @Update
    suspend fun update(card: CardEntity)

    @Delete
    suspend fun delete(card: CardEntity)

    // Danh sach tu moi nhat truoc; tu dong cap nhat khi du lieu doi
    @Query("SELECT * FROM cards ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<CardEntity>>

    @Query("SELECT * FROM cards WHERE id = :id")
    suspend fun getById(id: Long): CardEntity?

    // The da tung on va den han (tre nhat truoc)
    @Query("SELECT * FROM cards WHERE intervalDays > 0 AND dueDay <= :today ORDER BY dueDay ASC, id ASC")
    suspend fun getDueCards(today: Long): List<CardEntity>

    // The moi (chua on lan nao), cu nhat truoc, gioi han so luong
    @Query("SELECT * FROM cards WHERE intervalDays = 0 ORDER BY createdAt ASC, id ASC LIMIT :limit")
    suspend fun getNewCards(limit: Int): List<CardEntity>
}