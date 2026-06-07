package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.ExpenseRecord
import com.example.data.MilkRecord
import com.example.viewmodel.DairyAnalytics
import com.example.viewmodel.DairyViewModel
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

// Forest Organic Theme Colors
val ForestDeep = Color(0xFF1E5631)
val SageGreen = Color(0xFF4F7942)
val GoldYellow = Color(0xFFD4AF37)
val RedTerracotta = Color(0xFFC25A41)
val CreamBackground = Color(0xFFFAF9F6)
val SoftSandCard = Color(0xFFF2EFE9)
val CharcoalGray = Color(0xFF2E332F)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DairyDashboard(
    viewModel: DairyViewModel,
    modifier: Modifier = Modifier
) {
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
    val milkRecords by viewModel.milkRecords.collectAsStateWithLifecycle()
    val expenseRecords by viewModel.expenseRecords.collectAsStateWithLifecycle()
    val analytics by viewModel.analyticsState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(CreamBackground),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ForestDeep)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Dairy Record Book",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Analyze Production & Expenses",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                    
                    // Demo Prefill button if database is empty
                    if (milkRecords.isEmpty()) {
                        Button(
                            onClick = { viewModel.prefillDemoData() },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldYellow, contentColor = CharcoalGray),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier
                                .testTag("prefill_button")
                                .height(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Prefill",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Demo Data", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "App info",
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = SoftSandCard,
                tonalElevation = 8.dp,
                windowInsets = WindowInsets.navigationBars,
                modifier = Modifier.height(80.dp)
            ) {
                NavigationBarItem(
                    selected = activeTab == 0,
                    onClick = { viewModel.activeTab.value = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
                    label = { Text("Dashboard", fontSize = 12.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ForestDeep,
                        selectedTextColor = ForestDeep,
                        indicatorColor = ForestDeep.copy(alpha = 0.15f),
                        unselectedIconColor = CharcoalGray.copy(alpha = 0.6f),
                        unselectedTextColor = CharcoalGray.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.testTag("nav_dashboard")
                )
                NavigationBarItem(
                    selected = activeTab == 1,
                    onClick = { viewModel.activeTab.value = 1 },
                    icon = { Icon(Icons.Default.Add, contentDescription = "Add Records") },
                    label = { Text("Add Entry", fontSize = 12.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ForestDeep,
                        selectedTextColor = ForestDeep,
                        indicatorColor = ForestDeep.copy(alpha = 0.15f),
                        unselectedIconColor = CharcoalGray.copy(alpha = 0.6f),
                        unselectedTextColor = CharcoalGray.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.testTag("nav_add_record")
                )
                NavigationBarItem(
                    selected = activeTab == 2,
                    onClick = { viewModel.activeTab.value = 2 },
                    icon = { Icon(Icons.Default.List, contentDescription = "Records Log") },
                    label = { Text("Records Log", fontSize = 12.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ForestDeep,
                        selectedTextColor = ForestDeep,
                        indicatorColor = ForestDeep.copy(alpha = 0.15f),
                        unselectedIconColor = CharcoalGray.copy(alpha = 0.6f),
                        unselectedTextColor = CharcoalGray.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.testTag("nav_logs")
                )
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = CreamBackground
        ) {
            AnimatedContent(
                targetState = activeTab,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "MainContentTransition"
            ) { tab ->
                when (tab) {
                    0 -> DashboardTab(analytics, viewModel)
                    1 -> AddRecordTab(viewModel)
                    2 -> RecordsLogTab(milkRecords, expenseRecords, viewModel)
                }
            }
        }
    }
}

// FORMATTERS
val qtyFormat = DecimalFormat("0.0L")
val pctFormat = DecimalFormat("0.00'%'")
val currencyFormat = DecimalFormat("$#,##0.00")

@Composable
fun DashboardTab(analytics: DairyAnalytics, viewModel: DairyViewModel) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (analytics.totalMilkLiters == 0.0) {
            EmptyDashboardCard(onClickPrefill = { viewModel.prefillDemoData() })
            return@Column
        }

        // Quick Yield Summary
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(130.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SoftSandCard)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(ForestDeep.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Milk Production",
                            tint = ForestDeep,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = qtyFormat.format(analytics.totalMilkLiters),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalGray
                        )
                        Text(
                            text = "Total Milk Logged",
                            fontSize = 11.sp,
                            color = CharcoalGray.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(130.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SoftSandCard)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(GoldYellow.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Avg Fat/SNF",
                            tint = SageGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Fat: " + pctFormat.format(analytics.averageFatPct),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = CharcoalGray
                            )
                        }
                        Text(
                            text = "SNF: " + pctFormat.format(analytics.averageSNFPct),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CharcoalGray.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "Weighted Components",
                            fontSize = 10.sp,
                            color = CharcoalGray.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }

        // Financial Performance Banner Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = ForestDeep)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Financial Snapshot",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = currencyFormat.format(analytics.netProfit),
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Net Operating Income",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 11.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (analytics.netProfit >= 0) Color.White.copy(alpha = 0.2f) else RedTerracotta)
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (analytics.netProfit >= 0) "+ PROFITS" else "DEFICIT",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = currencyFormat.format(analytics.totalRevenue),
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Milk Revenue",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 10.sp
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = currencyFormat.format(analytics.totalExpenses),
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Total Expenses",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // Production Trend Custom Chart
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SoftSandCard)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Milk Yield Trend",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalGray
                        )
                        Text(
                            text = "Daily production history (L)",
                            fontSize = 11.sp,
                            color = CharcoalGray.copy(alpha = 0.6f)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(ForestDeep.copy(alpha = 0.08f))
                            .padding(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Trend icon",
                            tint = ForestDeep,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                
                // Render custom chart using Canvas
                MilkProductionChart(
                    data = analytics.dailyProduction,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            }
        }

        // Sessions breakout and expenses category breakdowns
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier
                    .weight(1.1f)
                    .height(220.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SoftSandCard)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Session Split",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalGray
                        )
                        Text(
                            text = "Avg Morning vs Evening",
                            fontSize = 10.sp,
                            color = CharcoalGray.copy(alpha = 0.6f)
                        )
                    }

                    // Bar Comparison Display
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val maxVal = maxOf(analytics.averageMorningQty, analytics.averageEveningQty, 1.0)
                        
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Morning", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CharcoalGray)
                                Text(qtyFormat.format(analytics.averageMorningQty), fontSize = 11.sp, color = SageGreen)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(12.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CharcoalGray.copy(alpha = 0.08f))
                            ) {
                                val ratio = (analytics.averageMorningQty / maxVal).toFloat()
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(ratio)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(SageGreen, ForestDeep)
                                            )
                                        )
                                )
                            }
                        }

                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Evening", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CharcoalGray)
                                Text(qtyFormat.format(analytics.averageEveningQty), fontSize = 11.sp, color = GoldYellow)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(12.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CharcoalGray.copy(alpha = 0.08f))
                            ) {
                                val ratio = (analytics.averageEveningQty / maxVal).toFloat()
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(ratio)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(GoldYellow, Color(0xFFE5C158))
                                            )
                                        )
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(2.dp))
                }
            }

            Card(
                modifier = Modifier
                    .weight(0.9f)
                    .height(220.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SoftSandCard)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Expenses Cat.",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalGray
                    )

                    if (analytics.expenseCategories.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No expenses yet",
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                color = CharcoalGray.copy(alpha = 0.5f)
                            )
                        }
                    } else {
                        val totalExp = analytics.totalExpenses.coerceAtLeast(1.0)
                        
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.verticalScroll(rememberScrollState())
                        ) {
                            analytics.expenseCategories.forEach { (cat, amt) ->
                                val pct = (amt / totalExp * 100).toInt()
                                val color = when(cat) {
                                    "Feed" -> ForestDeep
                                    "Healthcare" -> RedTerracotta
                                    "Labor" -> SageGreen
                                    "Utility" -> GoldYellow
                                    else -> Color.Gray
                                }
                                
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(cat, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = CharcoalGray)
                                        Text("${pct}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    LinearProgressIndicator(
                                        progress = (amt / totalExp).toFloat(),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp)),
                                        color = color,
                                        trackColor = CharcoalGray.copy(alpha = 0.08f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MilkProductionChart(
    data: List<Pair<String, Double>>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text("Insufficient data to plot chart", color = CharcoalGray.copy(alpha = 0.5f))
        }
        return
    }

    Box(
        modifier = modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            
            val maxProduction = data.maxOf { it.second }.coerceAtLeast(1.0) * 1.2
            val minProduction = 0.0
            
            val scaleX = width / (data.size - 1).coerceAtLeast(1)
            val scaleY = height / (maxProduction - minProduction).toFloat()

            // Path for Line
            val linePath = Path()
            val fillPath = Path()

            data.forEachIndexed { index, pair ->
                val x = index * scaleX
                val y = height - ((pair.second - minProduction).toFloat() * scaleY)

                if (index == 0) {
                    linePath.moveTo(x, y)
                    fillPath.moveTo(x, height)
                    fillPath.lineTo(x, y)
                } else {
                    linePath.lineTo(x, y)
                    fillPath.lineTo(x, y)
                }

                if (index == data.size - 1) {
                    fillPath.lineTo(x, height)
                    fillPath.close()
                }
            }

            // Draw clean vertical helper axis
            drawLine(
                color = CharcoalGray.copy(alpha = 0.15f),
                start = Offset(0f, 0f),
                end = Offset(0f, height),
                strokeWidth = 1.dp.toPx()
            )

            // Draw clean horizontal helper axis
            drawLine(
                color = CharcoalGray.copy(alpha = 0.15f),
                start = Offset(0f, height),
                end = Offset(width, height),
                strokeWidth = 1.dp.toPx()
            )

            // Draw background fill gradient
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        SageGreen.copy(alpha = 0.35f),
                        SageGreen.copy(alpha = 0.01f)
                    )
                )
            )

            // Draw line
            drawPath(
                path = linePath,
                color = ForestDeep,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            // Draw points & labels
            data.forEachIndexed { index, pair ->
                val x = index * scaleX
                val y = height - ((pair.second - minProduction).toFloat() * scaleY)
                
                // Node point outer ring
                drawCircle(
                    color = ForestDeep,
                    radius = 5.dp.toPx(),
                    center = Offset(x, y)
                )
                // Node point inner solid
                drawCircle(
                    color = CreamBackground,
                    radius = 2.5.dp.toPx(),
                    center = Offset(x, y)
                )
            }
        }
        
        // Simple Overlay labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .offset(y = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            data.forEach { pair ->
                Text(
                    text = pair.first,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = CharcoalGray.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun EmptyDashboardCard(onClickPrefill: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        colors = CardDefaults.cardColors(containerColor = SoftSandCard),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(40.dp))
                    .background(ForestDeep.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Empty cow icon",
                    tint = SageGreen,
                    modifier = Modifier.size(42.dp)
                )
            }
            Text(
                "Welcome to Dairy records!",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = CharcoalGray,
                textAlign = TextAlign.Center
            )
            Text(
                "Easily log daily milk yield session-wise, record farm feed/healthcare expenses, and instantly visualize trends & profits.",
                fontSize = 13.sp,
                color = CharcoalGray.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
            
            Button(
                onClick = onClickPrefill,
                colors = ButtonDefaults.buttonColors(containerColor = ForestDeep),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .testTag("onboard_demo_data")
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Demo",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Prepopulate 10 Days Demo Data", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Text(
                "Or manually head over to 'Add Entry' tab to record your first session.",
                fontSize = 11.sp,
                color = CharcoalGray.copy(alpha = 0.5f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun AddRecordTab(viewModel: DairyViewModel) {
    var recordType by remember { mutableStateOf(0) } // 0: Milk Yield, 1: Expenses
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Selector bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(SoftSandCard)
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (recordType == 0) ForestDeep else Color.Transparent)
                    .clickable { recordType = 0 }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Milk Production",
                    color = if (recordType == 0) Color.White else CharcoalGray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (recordType == 1) ForestDeep else Color.Transparent)
                    .clickable { recordType = 1 }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Farm Expense",
                    color = if (recordType == 1) Color.White else CharcoalGray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        if (recordType == 0) {
            // MILK FORM
            var quantityStr by remember { mutableStateOf("") }
            var session by remember { mutableStateOf("Morning") }
            var fatStr by remember { mutableStateOf("4.0") }
            var snfStr by remember { mutableStateOf("8.5") }
            var priceStr by remember { mutableStateOf("1.30") }
            var notes by remember { mutableStateOf("") }
            var isSubmittedSuccess by remember { mutableStateOf(false) }

            Text("Log Milk Yield", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = CharcoalGray)

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Session
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1.0f)
                            .border(
                                1.dp,
                                if (session == "Morning") ForestDeep else CharcoalGray.copy(alpha = 0.12f),
                                RoundedCornerShape(10.dp)
                            )
                            .background(if (session == "Morning") ForestDeep.copy(alpha = 0.05f) else Color.Transparent)
                            .clickable { session = "Morning" }
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Morning session",
                                tint = if (session == "Morning") ForestDeep else CharcoalGray,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Morning", color = CharcoalGray, fontWeight = FontWeight.Bold)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .weight(1.0f)
                            .border(
                                1.dp,
                                if (session == "Evening") ForestDeep else CharcoalGray.copy(alpha = 0.12f),
                                RoundedCornerShape(10.dp)
                            )
                            .background(if (session == "Evening") ForestDeep.copy(alpha = 0.05f) else Color.Transparent)
                            .clickable { session = "Evening" }
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Evening session",
                                tint = if (session == "Evening") ForestDeep else CharcoalGray,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Evening", color = CharcoalGray, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                OutlinedTextField(
                    value = quantityStr,
                    onValueChange = { quantityStr = it },
                    label = { Text("Milk Quantity (Liters)", color = CharcoalGray.copy(alpha = 0.8f)) },
                    placeholder = { Text("e.g. 18.5") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_milk_quantity")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = fatStr,
                        onValueChange = { fatStr = it },
                        label = { Text("Fat %", color = CharcoalGray.copy(alpha = 0.8f)) },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_milk_fat")
                    )

                    OutlinedTextField(
                        value = snfStr,
                        onValueChange = { snfStr = it },
                        label = { Text("SNF %", color = CharcoalGray.copy(alpha = 0.8f)) },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_milk_snf")
                    )
                }

                OutlinedTextField(
                    value = priceStr,
                    onValueChange = { priceStr = it },
                    label = { Text("Price per Liter", color = CharcoalGray.copy(alpha = 0.8f)) },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_milk_price")
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (Optional)", color = CharcoalGray.copy(alpha = 0.8f)) },
                    placeholder = { Text("Animal health, feed conditions, etc.") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val qty = quantityStr.toDoubleOrNull()
                        val fat = fatStr.toDoubleOrNull() ?: 4.0
                        val snf = snfStr.toDoubleOrNull() ?: 8.5
                        val price = priceStr.toDoubleOrNull() ?: 1.30
                        
                        if (qty != null && qty > 0.0) {
                            viewModel.addMilkRecord(
                                date = System.currentTimeMillis(),
                                session = session,
                                quantity = qty,
                                fat = fat,
                                snf = snf,
                                price = price,
                                notes = notes
                            )
                            quantityStr = ""
                            notes = ""
                            focusManager.clearFocus()
                            isSubmittedSuccess = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_milk_record"),
                    colors = ButtonDefaults.buttonColors(containerColor = ForestDeep),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save Milk Record", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                if (isSubmittedSuccess) {
                    Text(
                        "Record saved successfully!",
                        color = SageGreen,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                }
            }
        } else {
            // EXPENSE FORM
            var catType by remember { mutableStateOf("Feed") }
            val categories = listOf("Feed", "Healthcare", "Labor", "Utility", "Other")
            
            var amountStr by remember { mutableStateOf("") }
            var expNotes by remember { mutableStateOf("") }
            var isSubmittedSuccess by remember { mutableStateOf(false) }

            Text("Record Farm Expense", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = CharcoalGray)

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Large Category Selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = catType == cat,
                            onClick = { catType = cat },
                            label = { Text(cat) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ForestDeep,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Expense Amount", color = CharcoalGray.copy(alpha = 0.8f)) },
                    placeholder = { Text("e.g. 50.00") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_expense_amount")
                )

                OutlinedTextField(
                    value = expNotes,
                    onValueChange = { expNotes = it },
                    label = { Text("Expense Details / Notes", color = CharcoalGray.copy(alpha = 0.8f)) },
                    placeholder = { Text("Feed mix, Vet medication, labor name etc.") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val amount = amountStr.toDoubleOrNull()
                        if (amount != null && amount > 0.0) {
                            viewModel.addExpenseRecord(
                                date = System.currentTimeMillis(),
                                category = catType,
                                amount = amount,
                                notes = expNotes
                            )
                            amountStr = ""
                            expNotes = ""
                            focusManager.clearFocus()
                            isSubmittedSuccess = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_expense_record"),
                    colors = ButtonDefaults.buttonColors(containerColor = ForestDeep),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save Expense Record", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                if (isSubmittedSuccess) {
                    Text(
                        "Expense recorded successfully!",
                        color = SageGreen,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun RecordsLogTab(
    milkRecords: List<MilkRecord>,
    expenseRecords: List<ExpenseRecord>,
    viewModel: DairyViewModel
) {
    var logType by remember { mutableStateOf(0) } // 0: Milk Records, 1: Expense Records
    val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Log selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(SoftSandCard)
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (logType == 0) ForestDeep else Color.Transparent)
                    .clickable { logType = 0 }
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Milk Yield Logs",
                    color = if (logType == 0) Color.White else CharcoalGray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (logType == 1) ForestDeep else Color.Transparent)
                    .clickable { logType = 1 }
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Expense Logs",
                    color = if (logType == 1) Color.White else CharcoalGray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }

        if (logType == 0) {
            if (milkRecords.isEmpty()) {
                EmptyStateView("No milk logs recorded yet. Head over to Add Entry.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(milkRecords) { record ->
                        MilkRecordItem(record, dateFormatter) {
                            viewModel.deleteMilkRecord(record.id)
                        }
                    }
                }
            }
        } else {
            if (expenseRecords.isEmpty()) {
                EmptyStateView("No expenses recorded yet. Head over to Add Entry.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(expenseRecords) { record ->
                        ExpenseRecordItem(record, dateFormatter) {
                            viewModel.deleteExpenseRecord(record.id)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MilkRecordItem(
    record: MilkRecord,
    dateFormatter: SimpleDateFormat,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SoftSandCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (record.session == "Morning") SageGreen.copy(alpha = 0.15f) else GoldYellow.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (record.session == "Morning") Icons.Default.Star else Icons.Default.Check,
                    contentDescription = record.session,
                    tint = if (record.session == "Morning") SageGreen else ForestDeep,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = dateFormatter.format(Date(record.date)),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalGray
                    )
                    Text(
                        text = qtyFormat.format(record.quantity),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ForestDeep
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${record.session} Session",
                        fontSize = 11.sp,
                        color = CharcoalGray.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "Fat: ${record.fat}% | SNF: ${record.snf}%",
                        fontSize = 11.sp,
                        color = CharcoalGray.copy(alpha = 0.7f)
                    )
                }

                if (record.notes.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "\"${record.notes}\"",
                        fontSize = 11.sp,
                        color = CharcoalGray.copy(alpha = 0.5f),
                        style = androidx.compose.ui.text.TextStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onDelete,
                modifier = Modifier.testTag("delete_milk_${record.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = RedTerracotta,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun ExpenseRecordItem(
    record: ExpenseRecord,
    dateFormatter: SimpleDateFormat,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SoftSandCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val color = when(record.category) {
                "Feed" -> ForestDeep
                "Healthcare" -> RedTerracotta
                "Labor" -> SageGreen
                "Utility" -> GoldYellow
                else -> Color.Gray
            }
            
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when(record.category) {
                        "Feed" -> Icons.Default.Home
                        "Healthcare" -> Icons.Default.Warning
                        "Labor" -> Icons.Default.Person
                        "Utility" -> Icons.Default.Settings
                        else -> Icons.Default.Info
                    },
                    contentDescription = record.category,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = dateFormatter.format(Date(record.date)),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalGray
                    )
                    Text(
                        text = currencyFormat.format(record.amount),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = RedTerracotta
                    )
                }

                Text(
                    text = "Category: ${record.category}",
                    fontSize = 11.sp,
                    color = color,
                    fontWeight = FontWeight.Bold
                )

                if (record.notes.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "\"${record.notes}\"",
                        fontSize = 11.sp,
                        color = CharcoalGray.copy(alpha = 0.5f),
                        style = androidx.compose.ui.text.TextStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onDelete,
                modifier = Modifier.testTag("delete_expense_${record.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = RedTerracotta,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyStateView(message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(ForestDeep.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Information Icon",
                tint = SageGreen,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            fontSize = 14.sp,
            color = CharcoalGray.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
    }
}
