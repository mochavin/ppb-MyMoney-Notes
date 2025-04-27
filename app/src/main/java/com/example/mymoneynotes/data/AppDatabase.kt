package com.example.mymoneynotes.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Transaction::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile // Ensures visibility of changes across threads
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // Return instance if exists, otherwise create and return
            return INSTANCE ?: synchronized(this) { // Lock to prevent concurrent creation
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "money_notes_database" // Database file name
                )
                    // .fallbackToDestructiveMigration() // Use migrations in production!
                    .build()
                INSTANCE = instance
                instance // Return the newly created instance
            }
        }
    }
}