package com.wendev.kolas.data.detection.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DetectionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: DetectionEntity)

    @Query("SELECT * FROM detections ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<DetectionEntity>>

    @Query("SELECT * FROM detections WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): DetectionEntity?
}
