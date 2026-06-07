package com.example.data

import kotlinx.coroutines.flow.Flow

class DairyRepository(private val dairyDao: DairyDao) {
    val allMilkRecords: Flow<List<MilkRecord>> = dairyDao.getAllMilkRecords()
    val allExpenseRecords: Flow<List<ExpenseRecord>> = dairyDao.getAllExpenseRecords()

    suspend fun insertMilkRecord(record: MilkRecord) {
        dairyDao.insertMilkRecord(record)
    }

    suspend fun deleteMilkRecord(id: Int) {
        dairyDao.deleteMilkRecordById(id)
    }

    suspend fun insertExpenseRecord(record: ExpenseRecord) {
        dairyDao.insertExpenseRecord(record)
    }

    suspend fun deleteExpenseRecord(id: Int) {
        dairyDao.deleteExpenseRecordById(id)
    }
}
