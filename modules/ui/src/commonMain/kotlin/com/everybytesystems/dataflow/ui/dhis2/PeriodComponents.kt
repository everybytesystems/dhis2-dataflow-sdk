package com.everybytesystems.dataflow.ui.dhis2

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import kotlinx.datetime.*

/**
 * DHIS2 Period Components
 * Comprehensive period selection and management
 */

// ============================================================================
// 📅 PERIOD MODELS
// ============================================================================

data class Period(
    val id: String,
    val name: String,
    val displayName: String = name,
    val periodType: PeriodType,
    val startDate: String,
    val endDate: String,
    val isoDate: String? = null
)

enum class PeriodType(val displayName: String, val code: String) {
    DAILY("Daily", "Daily"),
    WEEKLY("Weekly", "Weekly"),
    WEEKLY_WEDNESDAY("Weekly (Wednesday)", "WeeklyWednesday"),
    WEEKLY_THURSDAY("Weekly (Thursday)", "WeeklyThursday"),
    WEEKLY_SATURDAY("Weekly (Saturday)", "WeeklySaturday"),
    WEEKLY_SUNDAY("Weekly (Sunday)", "WeeklySunday"),
    BI_WEEKLY("Bi-weekly", "BiWeekly"),
    MONTHLY("Monthly", "Monthly"),
    BI_MONTHLY("Bi-monthly", "BiMonthly"),
    QUARTERLY("Quarterly", "Quarterly"),
    SIX_MONTHLY("Six-monthly", "SixMonthly"),
    SIX_MONTHLY_APRIL("Six-monthly April", "SixMonthlyApril"),
    SIX_MONTHLY_NOV("Six-monthly November", "SixMonthlyNov"),
    YEARLY("Yearly", "Yearly"),
    FINANCIAL_APRIL("Financial Year (April)", "FinancialApril"),
    FINANCIAL_JULY("Financial Year (July)", "FinancialJuly"),
    FINANCIAL_OCT("Financial Year (October)", "FinancialOct"),
    FINANCIAL_NOV("Financial Year (November)", "FinancialNov")
}

data class RelativePeriod(
    val id: String,
    val name: String,
    val displayName: String = name,
    val periodType: PeriodType? = null,
    val offset: Int = 0
)

enum class PeriodSelectionMode {
    SINGLE,
    MULTIPLE,
    RANGE
}

data class PeriodRange(
    val startPeriod: Period,
    val endPeriod: Period
)

// ============================================================================
// 📅 PERIOD SELECTOR
// ============================================================================

@Composable
fun PeriodSelector(
    selectedPeriods: Set<String>,
    onSelectionChange: (Set<String>) -> Unit,
    modifier: Modifier = Modifier,
    periodType: PeriodType = PeriodType.MONTHLY,
    selectionMode: PeriodSelectionMode = PeriodSelectionMode.MULTIPLE,
    placeholder: String = "Select periods...",
    enabled: Boolean = true,
    isError: Boolean = false,
    maxSelections: Int? = null,
    availablePeriods: List<Period> = generatePeriods(periodType)
) {
    var showDialog by remember { mutableStateOf(false) }
    
    val selectedPeriodNames = remember(selectedPeriods, availablePeriods) {
        availablePeriods.filter { it.id in selectedPeriods }.map { it.displayName }
    }
    
    val displayText = when {
        selectedPeriodNames.isEmpty() -> placeholder
        selectedPeriodNames.size == 1 -> selectedPeriodNames.first()
        else -> "${selectedPeriodNames.size} periods selected"
    }
    
    OutlinedTextField(
        value = displayText,
        onValueChange = { },
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        readOnly = true,
        isError = isError,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Calendar"
            )
        },
        trailingIcon = {
            IconButton(
                onClick = { if (enabled) showDialog = true }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select periods"
                )
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = MaterialTheme.colorScheme.onSurface
        )
    )
    
    if (showDialog) {
        PeriodSelectionDialog(
            selectedPeriods = selectedPeriods,
            onSelectionChange = onSelectionChange,
            onDismiss = { showDialog = false },
            periodType = periodType,
            selectionMode = selectionMode,
            maxSelections = maxSelections,
            availablePeriods = availablePeriods
        )
    }
}

// ============================================================================
// 📱 PERIOD SELECTION DIALOG
// ============================================================================

@Composable
fun PeriodSelectionDialog(
    selectedPeriods: Set<String>,
    onSelectionChange: (Set<String>) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    periodType: PeriodType = PeriodType.MONTHLY,
    selectionMode: PeriodSelectionMode = PeriodSelectionMode.MULTIPLE,
    maxSelections: Int? = null,
    availablePeriods: List<Period> = generatePeriods(periodType),
    title: String = "Select Periods"
) {
    var selectedTab by remember { mutableStateOf(0) }
    var currentPeriodType by remember { mutableStateOf(periodType) }
    var currentYear by remember { mutableStateOf(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year) }
    
    val tabs = listOf("Periods", "Relative")
    
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier.width(500.dp),
        title = {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                // Tab row
                TabRow(
                    selectedTabIndex = selectedTab,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    tabs.forEachIndexed { index, tab ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(tab) }
                        )
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.height(400.dp)
            ) {
                when (selectedTab) {
                    0 -> {
                        // Fixed periods tab
                        PeriodSelectionContent(
                            selectedPeriods = selectedPeriods,
                            onSelectionChange = onSelectionChange,
                            periodType = currentPeriodType,
                            onPeriodTypeChange = { currentPeriodType = it },
                            selectionMode = selectionMode,
                            maxSelections = maxSelections,
                            currentYear = currentYear,
                            onYearChange = { currentYear = it },
                            availablePeriods = availablePeriods
                        )
                    }
                    1 -> {
                        // Relative periods tab
                        RelativePeriodSelectionContent(
                            selectedPeriods = selectedPeriods,
                            onSelectionChange = onSelectionChange,
                            selectionMode = selectionMode,
                            maxSelections = maxSelections
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Done")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun PeriodSelectionContent(
    selectedPeriods: Set<String>,
    onSelectionChange: (Set<String>) -> Unit,
    periodType: PeriodType,
    onPeriodTypeChange: (PeriodType) -> Unit,
    selectionMode: PeriodSelectionMode,
    maxSelections: Int?,
    currentYear: Int,
    onYearChange: (Int) -> Unit,
    availablePeriods: List<Period>
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Period type selector
        var periodTypeExpanded by remember { mutableStateOf(false) }
        
        ExposedDropdownMenuBox(
            expanded = periodTypeExpanded,
            onExpandedChange = { periodTypeExpanded = it }
        ) {
            OutlinedTextField(
                value = periodType.displayName,
                onValueChange = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                readOnly = true,
                label = { Text("Period Type") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = periodTypeExpanded)
                }
            )
            
            ExposedDropdownMenu(
                expanded = periodTypeExpanded,
                onDismissRequest = { periodTypeExpanded = false }
            ) {
                PeriodType.values().forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type.displayName) },
                        onClick = {
                            onPeriodTypeChange(type)
                            periodTypeExpanded = false
                        }
                    )
                }
            }
        }
        
        // Year selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onYearChange(currentYear - 1) }
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Previous year"
                )
            }
            
            Text(
                text = currentYear.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            IconButton(
                onClick = { onYearChange(currentYear + 1) }
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Next year"
                )
            }
        }
        
        // Period grid
        val periodsForYear = remember(availablePeriods, currentYear, periodType) {
            generatePeriodsForYear(currentYear, periodType)
        }
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(getGridColumns(periodType)),
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(periodsForYear) { period ->
                PeriodItem(
                    period = period,
                    isSelected = period.id in selectedPeriods,
                    onSelectionChange = { isSelected ->
                        handlePeriodSelection(
                            periodId = period.id,
                            isSelected = isSelected,
                            currentSelection = selectedPeriods,
                            selectionMode = selectionMode,
                            maxSelections = maxSelections,
                            onSelectionChange = onSelectionChange
                        )
                    },
                    selectionMode = selectionMode
                )
            }
        }
    }
}

@Composable
private fun RelativePeriodSelectionContent(
    selectedPeriods: Set<String>,
    onSelectionChange: (Set<String>) -> Unit,
    selectionMode: PeriodSelectionMode,
    maxSelections: Int?
) {
    val relativePeriods = remember {
        listOf(
            RelativePeriod("THIS_MONTH", "This month"),
            RelativePeriod("LAST_MONTH", "Last month"),
            RelativePeriod("LAST_3_MONTHS", "Last 3 months"),
            RelativePeriod("LAST_6_MONTHS", "Last 6 months"),
            RelativePeriod("LAST_12_MONTHS", "Last 12 months"),
            RelativePeriod("THIS_QUARTER", "This quarter"),
            RelativePeriod("LAST_QUARTER", "Last quarter"),
            RelativePeriod("LAST_4_QUARTERS", "Last 4 quarters"),
            RelativePeriod("THIS_YEAR", "This year"),
            RelativePeriod("LAST_YEAR", "Last year"),
            RelativePeriod("LAST_5_YEARS", "Last 5 years"),
            RelativePeriod("THIS_FINANCIAL_YEAR", "This financial year"),
            RelativePeriod("LAST_FINANCIAL_YEAR", "Last financial year"),
            RelativePeriod("LAST_5_FINANCIAL_YEARS", "Last 5 financial years")
        )
    }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(relativePeriods) { relativePeriod ->
            RelativePeriodItem(
                relativePeriod = relativePeriod,
                isSelected = relativePeriod.id in selectedPeriods,
                onSelectionChange = { isSelected ->
                    handlePeriodSelection(
                        periodId = relativePeriod.id,
                        isSelected = isSelected,
                        currentSelection = selectedPeriods,
                        selectionMode = selectionMode,
                        maxSelections = maxSelections,
                        onSelectionChange = onSelectionChange
                    )
                },
                selectionMode = selectionMode
            )
        }
    }
}

@Composable
private fun PeriodItem(
    period: Period,
    isSelected: Boolean,
    onSelectionChange: (Boolean) -> Unit,
    selectionMode: PeriodSelectionMode
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelectionChange(!isSelected) },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = if (isSelected) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else null
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = period.displayName,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}

@Composable
private fun RelativePeriodItem(
    relativePeriod: RelativePeriod,
    isSelected: Boolean,
    onSelectionChange: (Boolean) -> Unit,
    selectionMode: PeriodSelectionMode
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelectionChange(!isSelected) },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Selection indicator
            when (selectionMode) {
                PeriodSelectionMode.SINGLE -> {
                    RadioButton(
                        selected = isSelected,
                        onClick = { onSelectionChange(true) }
                    )
                }
                else -> {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = onSelectionChange
                    )
                }
            }
            
            // Period info
            Text(
                text = relativePeriod.displayName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier.weight(1f)
            )
            
            // Relative period icon
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = "Relative period",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// ============================================================================
// 📅 PERIOD CALENDAR
// ============================================================================

@Composable
fun PeriodCalendar(
    selectedPeriods: Set<String>,
    onSelectionChange: (Set<String>) -> Unit,
    modifier: Modifier = Modifier,
    periodType: PeriodType = PeriodType.MONTHLY,
    selectionMode: PeriodSelectionMode = PeriodSelectionMode.MULTIPLE,
    currentDate: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
) {
    var currentMonth by remember { mutableStateOf(currentDate.month) }
    var currentYear by remember { mutableStateOf(currentDate.year) }
    
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Calendar header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (currentMonth == Month.JANUARY) {
                        currentMonth = Month.DECEMBER
                        currentYear -= 1
                    } else {
                        currentMonth = Month(currentMonth.value - 1)
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Previous month"
                )
            }
            
            Text(
                text = "${currentMonth.name.lowercase().replaceFirstChar { it.uppercase() }} $currentYear",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            IconButton(
                onClick = {
                    if (currentMonth == Month.DECEMBER) {
                        currentMonth = Month.JANUARY
                        currentYear += 1
                    } else {
                        currentMonth = Month(currentMonth.value + 1)
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Next month"
                )
            }
        }
        
        // Calendar grid would be implemented here
        // This is a simplified representation
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Calendar view for $periodType periods",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

// ============================================================================
// 🔧 UTILITY FUNCTIONS
// ============================================================================

private fun handlePeriodSelection(
    periodId: String,
    isSelected: Boolean,
    currentSelection: Set<String>,
    selectionMode: PeriodSelectionMode,
    maxSelections: Int?,
    onSelectionChange: (Set<String>) -> Unit
) {
    val newSelection = when {
        !isSelected -> currentSelection - periodId
        selectionMode == PeriodSelectionMode.SINGLE -> setOf(periodId)
        maxSelections != null && currentSelection.size >= maxSelections -> currentSelection
        else -> currentSelection + periodId
    }
    
    onSelectionChange(newSelection)
}

private fun getGridColumns(periodType: PeriodType): Int {
    return when (periodType) {
        PeriodType.DAILY -> 7
        PeriodType.WEEKLY, PeriodType.WEEKLY_WEDNESDAY, PeriodType.WEEKLY_THURSDAY,
        PeriodType.WEEKLY_SATURDAY, PeriodType.WEEKLY_SUNDAY, PeriodType.BI_WEEKLY -> 4
        PeriodType.MONTHLY, PeriodType.BI_MONTHLY -> 3
        PeriodType.QUARTERLY, PeriodType.SIX_MONTHLY, PeriodType.SIX_MONTHLY_APRIL,
        PeriodType.SIX_MONTHLY_NOV -> 2
        else -> 1
    }
}

private fun generatePeriods(periodType: PeriodType): List<Period> {
    // This would generate actual periods based on the period type
    // For now, return a simplified list
    return (1..12).map { month ->
        Period(
            id = "2024${month.toString().padStart(2, '0')}",
            name = "2024-${month.toString().padStart(2, '0')}",
            displayName = "2024-${month.toString().padStart(2, '0')}",
            periodType = periodType,
            startDate = "2024-${month.toString().padStart(2, '0')}-01",
            endDate = "2024-${month.toString().padStart(2, '0')}-28"
        )
    }
}

private fun generatePeriodsForYear(year: Int, periodType: PeriodType): List<Period> {
    // This would generate actual periods for the specific year and type
    // For now, return a simplified list
    return when (periodType) {
        PeriodType.MONTHLY -> (1..12).map { month ->
            Period(
                id = "$year${month.toString().padStart(2, '0')}",
                name = "$year-${month.toString().padStart(2, '0')}",
                displayName = Month(month).name.take(3),
                periodType = periodType,
                startDate = "$year-${month.toString().padStart(2, '0')}-01",
                endDate = "$year-${month.toString().padStart(2, '0')}-28"
            )
        }
        PeriodType.QUARTERLY -> (1..4).map { quarter ->
            Period(
                id = "${year}Q$quarter",
                name = "${year}Q$quarter",
                displayName = "Q$quarter",
                periodType = periodType,
                startDate = "$year-${(quarter - 1) * 3 + 1}-01",
                endDate = "$year-${quarter * 3}-28"
            )
        }
        PeriodType.YEARLY -> listOf(
            Period(
                id = year.toString(),
                name = year.toString(),
                displayName = year.toString(),
                periodType = periodType,
                startDate = "$year-01-01",
                endDate = "$year-12-31"
            )
        )
        else -> generatePeriods(periodType)
    }
}