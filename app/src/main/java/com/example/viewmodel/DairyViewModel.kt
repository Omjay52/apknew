package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DairyViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: DairyRepository
    
    init {
        val database = DairyDatabase.getDatabase(application)
        repository = DairyRepository(database.dairyDao())
    }

    // Records Flows
    val milkRecords: StateFlow<List<MilkRecord>> = repository.allMilkRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expenseRecords: StateFlow<List<ExpenseRecord>> = repository.allExpenseRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Form states
    var activeTab = MutableStateFlow(0) // 0: Dashboard, 1: Add Record, 2: Records List

    // Live analytics calculated from Flows
    val analyticsState: StateFlow<DairyAnalytics> = combine(milkRecords, expenseRecords) { milk, expenses ->
        computeAnalytics(milk, expenses)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DairyAnalytics())

    // Actions
    fun addMilkRecord(date: Long, session: String, quantity: Double, fat: Double, snf: Double, price: Double, notes: String) {
        viewModelScope.launch {
            repository.insertMilkRecord(
                MilkRecord(
                    date = date,
                    session = session,
                    quantity = quantity,
                    fat = fat,
                    snf = snf,
                    pricePerLiter = price,
                    notes = notes
                )
            )
        }
    }

    fun deleteMilkRecord(id: Int) {
        viewModelScope.launch {
            repository.deleteMilkRecord(id)
        }
    }

    fun addExpenseRecord(date: Long, category: String, amount: Double, notes: String) {
        viewModelScope.launch {
            repository.insertExpenseRecord(
                ExpenseRecord(
                    date = date,
                    category = category,
                    amount = amount,
                    notes = notes
                )
            )
        }
    }

    fun deleteExpenseRecord(id: Int) {
        viewModelScope.launch {
            repository.deleteExpenseRecord(id)
        }
    }

    fun prefillDemoData() {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            
            // Generate records for the last 10 days
            for (i in 9 downTo 0) {
                calendar.time = Date()
                calendar.add(Calendar.DAY_OF_YEAR, -i)
                val dateMillis = calendar.timeInMillis
                
                // Keep dates aligned to avoid duplicates/messy sorting
                calendar.set(Calendar.HOUR_OF_DAY, 6)
                calendar.set(Calendar.MINUTE, 0)
                val morningTime = calendar.timeInMillis
                
                calendar.set(Calendar.HOUR_OF_DAY, 17)
                calendar.set(Calendar.MINUTE, 0)
                val eveningTime = calendar.timeInMillis

                // Morning Milking
                val morningQty = 15.0 + (Math.random() * 8.0) // 15 to 23 Liters
                val morningFat = 3.8 + (Math.random() * 0.8)   // 3.8% to 4.6%
                val morningSNF = 8.4 + (Math.random() * 0.6)   // 8.4% to 9.0%
                repository.insertMilkRecord(
                    MilkRecord(
                        date = morningTime,
                        session = "Morning",
                        quantity = morningQty,
                        fat = morningFat,
                        snf = morningSNF,
                        pricePerLiter = 1.30,
                        notes = "Demo Morning yield"
                    )
                )

                // Evening Milking
                val eveningQty = 12.0 + (Math.random() * 6.0) // 12 to 18 Liters
                val eveningFat = 4.0 + (Math.random() * 0.9)   // 4.0% to 4.9%
                val eveningSNF = 8.5 + (Math.random() * 0.5)   // 8.5% to 9.0%
                repository.insertMilkRecord(
                    MilkRecord(
                        date = eveningTime,
                        session = "Evening",
                        quantity = eveningQty,
                        fat = eveningFat,
                        snf = eveningSNF,
                        pricePerLiter = 1.35,
                        notes = "Demo Evening yield"
                    )
                )

                // Occasional Expenses
                if (i % 3 == 0) {
                    repository.insertExpenseRecord(
                        ExpenseRecord(
                            date = morningTime,
                            category = "Feed",
                            amount = 25.0 + (Math.random() * 10),
                            notes = "Concentrates & Hay"
                        )
                    )
                }
                if (i == 5) {
                    repository.insertExpenseRecord(
                        ExpenseRecord(
                            date = morningTime,
                            category = "Healthcare",
                            amount = 45.0,
                            notes = "Deworming vet visit"
                        )
                    )
                }
                if (i == 2) {
                    repository.insertExpenseRecord(
                        ExpenseRecord(
                            date = morningTime,
                            category = "Utility",
                            amount = 18.5,
                            notes = "Water & milking system cleaning"
                        )
                    )
                }
            }
        }
    }

    private fun computeAnalytics(milks: List<MilkRecord>, expenses: List<ExpenseRecord>): DairyAnalytics {
        if (milks.isEmpty()) return DairyAnalytics()

        val totalQty = milks.sumOf { it.quantity }
        val avgFat = if (milks.isNotEmpty()) milks.sumOf { it.fat * it.quantity } / totalQty else 0.0
        val avgSnf = if (milks.isNotEmpty()) milks.sumOf { it.snf * it.quantity } / totalQty else 0.0
        val totalRev = milks.sumOf { it.totalRevenue }
        val totalExp = expenses.sumOf { it.amount }
        val profit = totalRev - totalExp

        // Group Milk Production by Date (excluding hours/minutes)
        val dayFormatter = SimpleDateFormat("MMM dd", Locale.getDefault())
        val groupedProduction = milks.groupBy {
            val cal = Calendar.getInstance().apply { timeInMillis = it.date }
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            cal.timeInMillis
        }.mapValues { entry -> entry.value.sumOf { it.quantity } }
         .toList()
         .sortedBy { it.first }
         .takeLast(7)
         .map { dayFormatter.format(Date(it.first)) to it.second }

        val morningMilks = milks.filter { it.session.equals("Morning", ignoreCase = true) }
        val eveningMilks = milks.filter { it.session.equals("Evening", ignoreCase = true) }

        val avgMorningQty = if (morningMilks.isNotEmpty()) morningMilks.map { it.quantity }.average() else 0.0
        val avgEveningQty = if (eveningMilks.isNotEmpty()) eveningMilks.map { it.quantity }.average() else 0.0

        // Expenses Category Breakdown
        val expenseBreakdown = expenses.groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        return DairyAnalytics(
            totalMilkLiters = totalQty,
            averageFatPct = avgFat,
            averageSNFPct = avgSnf,
            totalRevenue = totalRev,
            totalExpenses = totalExp,
            netProfit = profit,
            dailyProduction = groupedProduction,
            averageMorningQty = avgMorningQty,
            averageEveningQty = avgEveningQty,
            expenseCategories = expenseBreakdown
        )
    }
}

data class DairyAnalytics(
    val totalMilkLiters: Double = 0.0,
    val averageFatPct: Double = 0.0,
    val averageSNFPct: Double = 0.0,
    val totalRevenue: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val netProfit: Double = 0.0,
    val dailyProduction: List<Pair<String, Double>> = emptyList(), // Pair of "MMM dd" to Liters
    val averageMorningQty: Double = 0.0,
    val averageEveningQty: Double = 0.0,
    val expenseCategories: Map<String, Double> = emptyMap()
)
