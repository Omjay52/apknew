package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "milk_records")
data class MilkRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: Long, // timestamp
    val session: String, // "Morning" or "Evening"
    val quantity: Double, // in Liters
    val fat: Double, // Fat %
    val snf: Double, // SNF %
    val pricePerLiter: Double, // Price per unit
    val notes: String = ""
) {
    val totalRevenue: Double
        get() = quantity * pricePerLiter
}
