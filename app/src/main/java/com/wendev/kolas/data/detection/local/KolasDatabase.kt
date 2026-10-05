package com.wendev.kolas.data.detection.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [DetectionEntity::class], version = 1, exportSchema = false)
@TypeConverters(ScoreConverters::class)
abstract class KolasDatabase : RoomDatabase() {
    abstract fun detectionDao(): DetectionDao
}
