package com.wendev.kolas.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.wendev.kolas.data.chat.ChatMessageDao
import com.wendev.kolas.data.detection.local.DetectionDao
import com.wendev.kolas.data.detection.local.KolasDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideKolasDatabase(@ApplicationContext context: Context): KolasDatabase =
        Room.databaseBuilder(context, KolasDatabase::class.java, "kolas.db")
            .addMigrations(MIGRATION_1_2)
            .build()

    @Provides
    fun provideDetectionDao(database: KolasDatabase): DetectionDao = database.detectionDao()

    @Provides
    fun provideChatMessageDao(database: KolasDatabase): ChatMessageDao = database.chatMessageDao()

    /** Adds the `chat_messages` table introduced in schema version 2. */
    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS `chat_messages` (" +
                    "`id` TEXT NOT NULL, " +
                    "`detectionId` TEXT NOT NULL, " +
                    "`author` TEXT NOT NULL, " +
                    "`text` TEXT NOT NULL, " +
                    "`createdAt` INTEGER NOT NULL, " +
                    "PRIMARY KEY(`id`))"
            )
        }
    }
}
