package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [MilkRecord::class, ExpenseRecord::class], version = 1, exportSchema = false)
abstract class DairyDatabase : RoomDatabase() {
    abstract fun dairyDao(): DairyDao

    companion object {
        @Volatile
        private var INSTANCE: DairyDatabase? = null

        fun getDatabase(context: Context): DairyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DairyDatabase::class.java,
                    "dairy_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                @Suppress("UpdateOfTo_0")
                INSTANCE = instance
                instance
            }
        }
    }
}
