package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DairyDao {
    // Milk Records
    @Query("SELECT * FROM milk_records ORDER BY date DESC, id DESC")
    fun getAllMilkRecords(): Flow<List<MilkRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilkRecord(record: MilkRecord)

    @Query("DELETE FROM milk_records WHERE id = :id")
    suspend fun deleteMilkRecordById(id: Int)

    // Expense Records
    @Query("SELECT * FROM expense_records ORDER BY date DESC, id DESC")
    fun getAllExpenseRecords(): Flow<List<ExpenseRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpenseRecord(record: ExpenseRecord)

    @Query("DELETE FROM expense_records WHERE id = :id")
    suspend fun deleteExpenseRecordById(id: Int)
}
