package com.wendev.kolas.data.detection.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.wendev.kolas.data.chat.ChatMessageDao
import com.wendev.kolas.data.chat.ChatMessageEntity

@Database(
    entities = [DetectionEntity::class, ChatMessageEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(ScoreConverters::class)
abstract class KolasDatabase : RoomDatabase() {
    abstract fun detectionDao(): DetectionDao
    abstract fun chatMessageDao(): ChatMessageDao
}
