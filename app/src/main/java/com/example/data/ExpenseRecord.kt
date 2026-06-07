package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expense_records")
data class ExpenseRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: Long, // timestamp
    val category: String, // "Feed", "Healthcare", "Labor", "Equipment", "Utility", "Other"
    val amount: Double,
    val notes: String = ""
)
