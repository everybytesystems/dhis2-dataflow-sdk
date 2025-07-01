package com.everybytesystems.dataflow.ui.calendar

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import kotlinx.coroutines.*
import kotlinx.datetime.*
import kotlin.math.*

/**
 * Calendar & Agenda Components
 * Event management, scheduling, and calendar views with agenda integration
 */

// ============================================================================
// 📅 CALENDAR DATA MODELS
// ============================================================================

data class CalendarState(
    val currentDate: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
    val selectedDate: LocalDate? = null,
    val viewMode: CalendarViewMode = CalendarViewMode.MONTH,
    val events: List<CalendarEvent> = emptyList(),
    val selectedEvent: CalendarEvent? = null,
    val isEventDialogOpen: Boolean = false,
    val isEventCreating: Boolean = false,
    val eventFilter: EventFilter = EventFilter(),
    val weekStartDay: DayOfWeek = DayOfWeek.MONDAY,
    val showWeekNumbers: Boolean = false,
    val highlightToday: Boolean = true,
    val timeZone: TimeZone = TimeZone.currentSystemDefault()
)

data class CalendarEvent(
    val id: String,
    val title: String,
    val description: String = "",
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val isAllDay: Boolean = false,
    val location: String = "",
    val attendees: List<EventAttendee> = emptyList(),
    val category: EventCategory = EventCategory.PERSONAL,
    val priority: EventPriority = EventPriority.MEDIUM,
    val status: EventStatus = EventStatus.CONFIRMED,
    val recurrence: EventRecurrence? = null,
    val reminders: List<EventReminder> = emptyList(),
    val color: Color = Color.Blue,
    val isPrivate: Boolean = false,
    val url: String = "",
    val attachments: List<String> = emptyList(),
    val createdAt: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
    val updatedAt: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
    val metadata: Map<String, Any> = emptyMap()
)

data class EventAttendee(
    val id: String,
    val name: String,
    val email: String,
    val status: AttendeeStatus = AttendeeStatus.PENDING,
    val isOrganizer: Boolean = false,
    val isOptional: Boolean = false
)

data class EventReminder(
    val id: String,
    val type: ReminderType,
    val minutesBefore: Int,
    val isEnabled: Boolean = true
)

data class EventRecurrence(
    val type: RecurrenceType,
    val interval: Int = 1,
    val endDate: LocalDate? = null,
    val occurrences: Int? = null,
    val daysOfWeek: Set<DayOfWeek> = emptySet(),
    val dayOfMonth: Int? = null,
    val weekOfMonth: Int? = null
)

data class EventFilter(
    val categories: Set<EventCategory> = EventCategory.values().toSet(),
    val priorities: Set<EventPriority> = EventPriority.values().toSet(),
    val statuses: Set<EventStatus> = EventStatus.values().toSet(),
    val searchQuery: String = "",
    val dateRange: ClosedRange<LocalDate>? = null,
    val showPrivate: Boolean = true
)

enum class CalendarViewMode(val displayName: String) {
    DAY("Day"),
    WEEK("Week"),
    MONTH("Month"),
    YEAR("Year"),
    AGENDA("Agenda"),
    TIMELINE("Timeline")
}

enum class EventCategory(val displayName: String, val color: Color) {
    PERSONAL("Personal", Color(0xFF2196F3)),
    WORK("Work", Color(0xFF4CAF50)),
    MEETING("Meeting", Color(0xFFFF9800)),
    APPOINTMENT("Appointment", Color(0xFF9C27B0)),
    HOLIDAY("Holiday", Color(0xFFF44336)),
    BIRTHDAY("Birthday", Color(0xFFE91E63)),
    REMINDER("Reminder", Color(0xFF607D8B)),
    TRAVEL("Travel", Color(0xFF00BCD4)),
    HEALTH("Health", Color(0xFF8BC34A)),
    EDUCATION("Education", Color(0xFF3F51B5))
}

enum class EventPriority(val displayName: String, val color: Color) {
    LOW("Low", Color(0xFF4CAF50)),
    MEDIUM("Medium", Color(0xFFFF9800)),
    HIGH("High", Color(0xFFF44336)),
    URGENT("Urgent", Color(0xFF9C27B0))
}

enum class EventStatus(val displayName: String) {
    CONFIRMED("Confirmed"),
    TENTATIVE("Tentative"),
    CANCELLED("Cancelled"),
    COMPLETED("Completed")
}

enum class AttendeeStatus(val displayName: String) {
    PENDING("Pending"),
    ACCEPTED("Accepted"),
    DECLINED("Declined"),
    TENTATIVE("Tentative")
}

enum class ReminderType(val displayName: String) {
    NOTIFICATION("Notification"),
    EMAIL("Email"),
    SMS("SMS"),
    POPUP("Popup")
}

enum class RecurrenceType(val displayName: String) {
    NONE("None"),
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly"),
    YEARLY("Yearly"),
    CUSTOM("Custom")
}

// ============================================================================
// 📅 MAIN CALENDAR COMPONENT
// ============================================================================

@Composable
fun CalendarView(
    state: CalendarState,
    onStateChange: (CalendarState) -> Unit,
    onEventClick: (CalendarEvent) -> Unit,
    onDateClick: (LocalDate) -> Unit,
    onEventCreate: (CalendarEvent) -> Unit,
    onEventUpdate: (CalendarEvent) -> Unit,
    onEventDelete: (CalendarEvent) -> Unit,
    modifier: Modifier = Modifier,
    showMiniCalendar: Boolean = true,
    showAgenda: Boolean = true,
    allowEventCreation: Boolean = true
) {
    Row(modifier = modifier.fillMaxSize()) {
        // Left sidebar with mini calendar and agenda
        if (showMiniCalendar || showAgenda) {
            Card(
                modifier = Modifier
                    .width(320.dp)
                    .fillMaxHeight(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (showMiniCalendar) {
                        MiniCalendar(
                            state = state,
                            onStateChange = onStateChange,
                            onDateClick = onDateClick
                        )
                        
                        if (showAgenda) {
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                    
                    if (showAgenda) {
                        AgendaView(
                            state = state,
                            onEventClick = onEventClick,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
        }
        
        // Main calendar area
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column {
                // Calendar header with controls
                CalendarHeader(
                    state = state,
                    onStateChange = onStateChange,
                    onEventCreate = if (allowEventCreation) {
                        { onStateChange(state.copy(isEventDialogOpen = true, isEventCreating = true)) }
                    } else null
                )
                
                // Calendar content based on view mode
                Box(modifier = Modifier.weight(1f)) {
                    when (state.viewMode) {
                        CalendarViewMode.MONTH -> MonthView(
                            state = state,
                            onDateClick = onDateClick,
                            onEventClick = onEventClick
                        )
                        CalendarViewMode.WEEK -> WeekView(
                            state = state,
                            onDateClick = onDateClick,
                            onEventClick = onEventClick
                        )
                        CalendarViewMode.DAY -> DayView(
                            state = state,
                            onEventClick = onEventClick
                        )
                        CalendarViewMode.YEAR -> YearView(
                            state = state,
                            onDateClick = onDateClick
                        )
                        CalendarViewMode.AGENDA -> FullAgendaView(
                            state = state,
                            onEventClick = onEventClick
                        )
                        CalendarViewMode.TIMELINE -> TimelineView(
                            state = state,
                            onEventClick = onEventClick
                        )
                    }
                }
            }
        }
    }
    
    // Event dialog
    if (state.isEventDialogOpen) {
        EventDialog(
            event = state.selectedEvent,
            isCreating = state.isEventCreating,
            onDismiss = {
                onStateChange(
                    state.copy(
                        isEventDialogOpen = false,
                        selectedEvent = null,
                        isEventCreating = false
                    )
                )
            },
            onSave = { event ->
                if (state.isEventCreating) {
                    onEventCreate(event)
                } else {
                    onEventUpdate(event)
                }
                onStateChange(
                    state.copy(
                        isEventDialogOpen = false,
                        selectedEvent = null,
                        isEventCreating = false
                    )
                )
            },
            onDelete = if (!state.isEventCreating) {
                { event ->
                    onEventDelete(event)
                    onStateChange(
                        state.copy(
                            isEventDialogOpen = false,
                            selectedEvent = null,
                            isEventCreating = false
                        )
                    )
                }
            } else null
        )
    }
}

// ============================================================================
// 📅 CALENDAR HEADER
// ============================================================================

@Composable
fun CalendarHeader(
    state: CalendarState,
    onStateChange: (CalendarState) -> Unit,
    onEventCreate: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Navigation and title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Previous/Next navigation
                IconButton(
                    onClick = {
                        val newDate = when (state.viewMode) {
                            CalendarViewMode.DAY -> state.currentDate.minus(1, DateTimeUnit.DAY)
                            CalendarViewMode.WEEK -> state.currentDate.minus(1, DateTimeUnit.WEEK)
                            CalendarViewMode.MONTH -> state.currentDate.minus(1, DateTimeUnit.MONTH)
                            CalendarViewMode.YEAR -> state.currentDate.minus(1, DateTimeUnit.YEAR)
                            else -> state.currentDate.minus(1, DateTimeUnit.MONTH)
                        }
                        onStateChange(state.copy(currentDate = newDate))
                    }
                ) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Previous")
                }
                
                IconButton(
                    onClick = {
                        val newDate = when (state.viewMode) {
                            CalendarViewMode.DAY -> state.currentDate.plus(1, DateTimeUnit.DAY)
                            CalendarViewMode.WEEK -> state.currentDate.plus(1, DateTimeUnit.WEEK)
                            CalendarViewMode.MONTH -> state.currentDate.plus(1, DateTimeUnit.MONTH)
                            CalendarViewMode.YEAR -> state.currentDate.plus(1, DateTimeUnit.YEAR)
                            else -> state.currentDate.plus(1, DateTimeUnit.MONTH)
                        }
                        onStateChange(state.copy(currentDate = newDate))
                    }
                ) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Next")
                }
                
                // Current period title
                Text(
                    text = formatCalendarTitle(state.currentDate, state.viewMode),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                // Today button
                TextButton(
                    onClick = {
                        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
                        onStateChange(state.copy(currentDate = today, selectedDate = today))
                    }
                ) {
                    Text("Today")
                }
            }
            
            // View mode and actions
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // View mode selector
                var viewModeExpanded by remember { mutableStateOf(false) }
                
                ExposedDropdownMenuBox(
                    expanded = viewModeExpanded,
                    onExpandedChange = { viewModeExpanded = it }
                ) {
                    OutlinedButton(
                        onClick = { viewModeExpanded = true },
                        modifier = Modifier.menuAnchor()
                    ) {
                        Text(state.viewMode.displayName)
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    
                    ExposedDropdownMenu(
                        expanded = viewModeExpanded,
                        onDismissRequest = { viewModeExpanded = false }
                    ) {
                        CalendarViewMode.values().forEach { mode ->
                            DropdownMenuItem(
                                text = { Text(mode.displayName) },
                                onClick = {
                                    onStateChange(state.copy(viewMode = mode))
                                    viewModeExpanded = false
                                }
                            )
                        }
                    }
                }
                
                // Create event button
                if (onEventCreate != null) {
                    Button(onClick = onEventCreate) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("New Event")
                    }
                }
                
                // Filter button
                IconButton(
                    onClick = {
                        // Open filter dialog
                        println("Open filter dialog")
                    }
                ) {
                    Icon(Icons.Default.FilterList, contentDescription = "Filter")
                }
            }
        }
    }
}

// ============================================================================
// 📅 MONTH VIEW
// ============================================================================

@Composable
fun MonthView(
    state: CalendarState,
    onDateClick: (LocalDate) -> Unit,
    onEventClick: (CalendarEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {
        // Days of week header
        Row(modifier = Modifier.fillMaxWidth()) {
            val daysOfWeek = generateDaysOfWeek(state.weekStartDay)
            daysOfWeek.forEach { dayOfWeek ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = dayOfWeek.name.take(3),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Calendar grid
        val monthDays = generateMonthDays(state.currentDate, state.weekStartDay)
        val weeks = monthDays.chunked(7)
        
        weeks.forEach { week ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
            ) {
                week.forEach { dayInfo ->
                    CalendarDayCell(
                        dayInfo = dayInfo,
                        state = state,
                        onDateClick = onDateClick,
                        onEventClick = onEventClick,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun CalendarDayCell(
    dayInfo: DayInfo,
    state: CalendarState,
    onDateClick: (LocalDate) -> Unit,
    onEventClick: (CalendarEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val isSelected = state.selectedDate == dayInfo.date
    val isToday = dayInfo.isToday && state.highlightToday
    val dayEvents = state.events.filter { event ->
        event.startDateTime.date == dayInfo.date || 
        (event.isAllDay && event.startDateTime.date <= dayInfo.date && event.endDateTime.date >= dayInfo.date)
    }.take(3) // Show max 3 events
    
    Card(
        modifier = modifier
            .fillMaxSize()
            .padding(2.dp)
            .clickable { onDateClick(dayInfo.date) },
        colors = CardDefaults.cardColors(
            containerColor = when {
                isSelected -> MaterialTheme.colorScheme.primaryContainer
                isToday -> MaterialTheme.colorScheme.secondaryContainer
                !dayInfo.isCurrentMonth -> MaterialTheme.colorScheme.surfaceVariant
                else -> MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected || isToday) 4.dp else 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
        ) {
            // Day number
            Text(
                text = dayInfo.date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                color = when {
                    !dayInfo.isCurrentMonth -> MaterialTheme.colorScheme.onSurfaceVariant
                    isToday -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )
            
            // Event indicators
            dayEvents.forEach { event ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .padding(vertical = 1.dp)
                        .background(
                            event.color.copy(alpha = 0.8f),
                            RoundedCornerShape(2.dp)
                        )
                        .clickable { onEventClick(event) }
                ) {
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        maxLines = 1,
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )
                }
            }
            
            // More events indicator
            if (state.events.count { it.startDateTime.date == dayInfo.date } > 3) {
                Text(
                    text = "+${state.events.count { it.startDateTime.date == dayInfo.date } - 3} more",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ============================================================================
// 📅 WEEK VIEW
// ============================================================================

@Composable
fun WeekView(
    state: CalendarState,
    onDateClick: (LocalDate) -> Unit,
    onEventClick: (CalendarEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {
        // Week header with dates
        Row(modifier = Modifier.fillMaxWidth()) {
            // Time column spacer
            Box(modifier = Modifier.width(60.dp))
            
            val weekDays = generateWeekDays(state.currentDate, state.weekStartDay)
            weekDays.forEach { dayInfo ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onDateClick(dayInfo.date) }
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = dayInfo.date.dayOfWeek.name.take(3),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = dayInfo.date.dayOfMonth.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (dayInfo.isToday) FontWeight.Bold else FontWeight.Normal,
                        color = if (dayInfo.isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
        
        HorizontalDivider()
        
        // Time slots with events
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(24) { hour ->
                WeekTimeSlot(
                    hour = hour,
                    weekDays = generateWeekDays(state.currentDate, state.weekStartDay),
                    events = state.events,
                    onEventClick = onEventClick
                )
            }
        }
    }
}

@Composable
fun WeekTimeSlot(
    hour: Int,
    weekDays: List<DayInfo>,
    events: List<CalendarEvent>,
    onEventClick: (CalendarEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
    ) {
        // Time label
        Box(
            modifier = Modifier
                .width(60.dp)
                .fillMaxHeight(),
            contentAlignment = Alignment.TopCenter
        ) {
            Text(
                text = formatHour(hour),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Day columns
        weekDays.forEach { dayInfo ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .border(
                        0.5.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                    .padding(2.dp)
            ) {
                // Events in this time slot
                val slotEvents = events.filter { event ->
                    event.startDateTime.date == dayInfo.date &&
                    event.startDateTime.hour <= hour &&
                    event.endDateTime.hour > hour &&
                    !event.isAllDay
                }
                
                slotEvents.forEach { event ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .clickable { onEventClick(event) },
                        colors = CardDefaults.cardColors(
                            containerColor = event.color.copy(alpha = 0.8f)
                        )
                    ) {
                        Text(
                            text = event.title,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            maxLines = 2,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }
        }
    }
}

// ============================================================================
// 📅 DAY VIEW
// ============================================================================

@Composable
fun DayView(
    state: CalendarState,
    onEventClick: (CalendarEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {
        // Day header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = state.currentDate.dayOfWeek.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = "${state.currentDate.month.name} ${state.currentDate.dayOfMonth}, ${state.currentDate.year}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // All-day events
        val allDayEvents = state.events.filter { 
            it.isAllDay && it.startDateTime.date == state.currentDate 
        }
        
        if (allDayEvents.isNotEmpty()) {
            Text(
                text = "All Day",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            allDayEvents.forEach { event ->
                EventCard(
                    event = event,
                    onClick = { onEventClick(event) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Timed events
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(24) { hour ->
                DayTimeSlot(
                    hour = hour,
                    date = state.currentDate,
                    events = state.events.filter { !it.isAllDay },
                    onEventClick = onEventClick
                )
            }
        }
    }
}

@Composable
fun DayTimeSlot(
    hour: Int,
    date: LocalDate,
    events: List<CalendarEvent>,
    onEventClick: (CalendarEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
        // Time label
        Box(
            modifier = Modifier
                .width(80.dp)
                .fillMaxHeight(),
            contentAlignment = Alignment.TopStart
        ) {
            Text(
                text = formatHour(hour),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Events column
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .border(
                    0.5.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
                .padding(4.dp)
        ) {
            val hourEvents = events.filter { event ->
                event.startDateTime.date == date &&
                event.startDateTime.hour <= hour &&
                event.endDateTime.hour > hour
            }
            
            hourEvents.forEachIndexed { index, event ->
                EventCard(
                    event = event,
                    onClick = { onEventClick(event) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (index * 4).dp)
                )
            }
        }
    }
}

// ============================================================================
// 📅 YEAR VIEW
// ============================================================================

@Composable
fun YearView(
    state: CalendarState,
    onDateClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(12) { monthIndex ->
            val month = Month.values()[monthIndex]
            val monthDate = LocalDate(state.currentDate.year, month, 1)
            
            MiniMonthView(
                date = monthDate,
                events = state.events,
                selectedDate = state.selectedDate,
                onDateClick = onDateClick,
                weekStartDay = state.weekStartDay
            )
        }
    }
}

@Composable
fun MiniMonthView(
    date: LocalDate,
    events: List<CalendarEvent>,
    selectedDate: LocalDate?,
    onDateClick: (LocalDate) -> Unit,
    weekStartDay: DayOfWeek,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            // Month header
            Text(
                text = "${date.month.name} ${date.year}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // Days grid
            val monthDays = generateMonthDays(date, weekStartDay)
            val weeks = monthDays.chunked(7)
            
            weeks.forEach { week ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    week.forEach { dayInfo ->
                        val hasEvents = events.any { it.startDateTime.date == dayInfo.date }
                        val isSelected = selectedDate == dayInfo.date
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clickable { onDateClick(dayInfo.date) }
                                .background(
                                    when {
                                        isSelected -> MaterialTheme.colorScheme.primary
                                        dayInfo.isToday -> MaterialTheme.colorScheme.primaryContainer
                                        else -> Color.Transparent
                                    },
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dayInfo.date.dayOfMonth.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                color = when {
                                    isSelected -> MaterialTheme.colorScheme.onPrimary
                                    !dayInfo.isCurrentMonth -> MaterialTheme.colorScheme.onSurfaceVariant
                                    dayInfo.isToday -> MaterialTheme.colorScheme.primary
                                    else -> MaterialTheme.colorScheme.onSurface
                                },
                                fontWeight = if (hasEvents) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// 📅 MINI CALENDAR
// ============================================================================

@Composable
fun MiniCalendar(
    state: CalendarState,
    onStateChange: (CalendarState) -> Unit,
    onDateClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Mini calendar header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    val newDate = state.currentDate.minus(1, DateTimeUnit.MONTH)
                    onStateChange(state.copy(currentDate = newDate))
                }
            ) {
                Icon(
                    Icons.Default.ChevronLeft,
                    contentDescription = "Previous month",
                    modifier = Modifier.size(16.dp)
                )
            }
            
            Text(
                text = "${state.currentDate.month.name} ${state.currentDate.year}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            
            IconButton(
                onClick = {
                    val newDate = state.currentDate.plus(1, DateTimeUnit.MONTH)
                    onStateChange(state.copy(currentDate = newDate))
                }
            ) {
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = "Next month",
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Days of week
        Row(modifier = Modifier.fillMaxWidth()) {
            val daysOfWeek = generateDaysOfWeek(state.weekStartDay)
            daysOfWeek.forEach { dayOfWeek ->
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = dayOfWeek.name.take(1),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // Calendar grid
        val monthDays = generateMonthDays(state.currentDate, state.weekStartDay)
        val weeks = monthDays.chunked(7)
        
        weeks.forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { dayInfo ->
                    val isSelected = state.selectedDate == dayInfo.date
                    val hasEvents = state.events.any { it.startDateTime.date == dayInfo.date }
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clickable { onDateClick(dayInfo.date) }
                            .background(
                                when {
                                    isSelected -> MaterialTheme.colorScheme.primary
                                    dayInfo.isToday -> MaterialTheme.colorScheme.primaryContainer
                                    else -> Color.Transparent
                                },
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = dayInfo.date.dayOfMonth.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = when {
                                isSelected -> MaterialTheme.colorScheme.onPrimary
                                !dayInfo.isCurrentMonth -> MaterialTheme.colorScheme.onSurfaceVariant
                                dayInfo.isToday -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.onSurface
                            },
                            fontWeight = if (hasEvents) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

// ============================================================================
// 📋 AGENDA VIEW
// ============================================================================

@Composable
fun AgendaView(
    state: CalendarState,
    onEventClick: (CalendarEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Agenda",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        val selectedDate = state.selectedDate ?: state.currentDate
        val dayEvents = state.events.filter { event ->
            event.startDateTime.date == selectedDate
        }.sortedBy { it.startDateTime }
        
        if (dayEvents.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.EventNote,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "No events",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatDate(selectedDate),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(dayEvents) { event ->
                    EventCard(
                        event = event,
                        onClick = { onEventClick(event) },
                        showDate = false
                    )
                }
            }
        }
    }
}

@Composable
fun FullAgendaView(
    state: CalendarState,
    onEventClick: (CalendarEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val groupedEvents = state.events
            .filter { event ->
                state.eventFilter.categories.contains(event.category) &&
                state.eventFilter.priorities.contains(event.priority) &&
                state.eventFilter.statuses.contains(event.status) &&
                (state.eventFilter.searchQuery.isEmpty() || 
                 event.title.contains(state.eventFilter.searchQuery, ignoreCase = true))
            }
            .sortedBy { it.startDateTime }
            .groupBy { it.startDateTime.date }
        
        groupedEvents.forEach { (date, events) ->
            item {
                Column {
                    // Date header
                    Text(
                        text = formatDate(date),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    // Events for this date
                    events.forEach { event ->
                        EventCard(
                            event = event,
                            onClick = { onEventClick(event) },
                            showDate = false,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

// ============================================================================
// 📊 TIMELINE VIEW
// ============================================================================

@Composable
fun TimelineView(
    state: CalendarState,
    onEventClick: (CalendarEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val sortedEvents = state.events
            .sortedBy { it.startDateTime }
            .filter { event ->
                state.eventFilter.categories.contains(event.category) &&
                state.eventFilter.priorities.contains(event.priority)
            }
        
        items(sortedEvents) { event ->
            TimelineEventCard(
                event = event,
                onClick = { onEventClick(event) }
            )
        }
    }
}

@Composable
fun TimelineEventCard(
    event: CalendarEvent,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Timeline indicator
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(60.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(event.color, CircleShape)
            )
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(40.dp)
                    .background(event.color.copy(alpha = 0.3f))
            )
        }
        
        // Event content
        EventCard(
            event = event,
            onClick = onClick,
            modifier = Modifier.weight(1f)
        )
    }
}

// ============================================================================
// 🎫 EVENT CARD
// ============================================================================

@Composable
fun EventCard(
    event: CalendarEvent,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showDate: Boolean = true
) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = event.color.copy(alpha = 0.1f)
        ),
        border = BorderStroke(1.dp, event.color.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Event title and priority
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                
                if (event.priority != EventPriority.MEDIUM) {
                    PriorityIndicator(priority = event.priority)
                }
            }
            
            // Time and date
            if (showDate) {
                Text(
                    text = formatEventDateTime(event),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = formatEventTime(event),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Location
            if (event.location.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = event.location,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Category and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CategoryChip(category = event.category)
                StatusIndicator(status = event.status)
            }
        }
    }
}

@Composable
fun PriorityIndicator(
    priority: EventPriority,
    modifier: Modifier = Modifier
) {
    val icon = when (priority) {
        EventPriority.LOW -> Icons.Default.KeyboardArrowDown
        EventPriority.MEDIUM -> Icons.Default.Remove
        EventPriority.HIGH -> Icons.Default.KeyboardArrowUp
        EventPriority.URGENT -> Icons.Default.PriorityHigh
    }
    
    Icon(
        icon,
        contentDescription = priority.displayName,
        modifier = modifier.size(16.dp),
        tint = priority.color
    )
}

@Composable
fun CategoryChip(
    category: EventCategory,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = category.color.copy(alpha = 0.2f)
    ) {
        Text(
            text = category.displayName,
            style = MaterialTheme.typography.labelSmall,
            color = category.color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun StatusIndicator(
    status: EventStatus,
    modifier: Modifier = Modifier
) {
    val (icon, color) = when (status) {
        EventStatus.CONFIRMED -> Icons.Default.CheckCircle to Color(0xFF4CAF50)
        EventStatus.TENTATIVE -> Icons.Default.Schedule to Color(0xFFFF9800)
        EventStatus.CANCELLED -> Icons.Default.Cancel to Color(0xFFF44336)
        EventStatus.COMPLETED -> Icons.Default.Done to Color(0xFF2196F3)
    }
    
    Icon(
        icon,
        contentDescription = status.displayName,
        modifier = modifier.size(16.dp),
        tint = color
    )
}

// ============================================================================
// 📝 EVENT DIALOG
// ============================================================================

@Composable
fun EventDialog(
    event: CalendarEvent?,
    isCreating: Boolean,
    onDismiss: () -> Unit,
    onSave: (CalendarEvent) -> Unit,
    onDelete: ((CalendarEvent) -> Unit)?,
    modifier: Modifier = Modifier
) {
    var title by remember(event) { mutableStateOf(event?.title ?: "") }
    var description by remember(event) { mutableStateOf(event?.description ?: "") }
    var location by remember(event) { mutableStateOf(event?.location ?: "") }
    var category by remember(event) { mutableStateOf(event?.category ?: EventCategory.PERSONAL) }
    var priority by remember(event) { mutableStateOf(event?.priority ?: EventPriority.MEDIUM) }
    var isAllDay by remember(event) { mutableStateOf(event?.isAllDay ?: false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (isCreating) "Create Event" else "Edit Event")
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.height(400.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                
                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
                
                item {
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Location") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Default.LocationOn, contentDescription = null)
                        }
                    )
                }
                
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isAllDay,
                            onCheckedChange = { isAllDay = it }
                        )
                        Text("All day event")
                    }
                }
                
                item {
                    var categoryExpanded by remember { mutableStateOf(false) }
                    
                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = { categoryExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = category.displayName,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("Category") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        
                        ExposedDropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false }
                        ) {
                            EventCategory.values().forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat.displayName) },
                                    onClick = {
                                        category = cat
                                        categoryExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                
                item {
                    var priorityExpanded by remember { mutableStateOf(false) }
                    
                    ExposedDropdownMenuBox(
                        expanded = priorityExpanded,
                        onExpandedChange = { priorityExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = priority.displayName,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("Priority") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = priorityExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        
                        ExposedDropdownMenu(
                            expanded = priorityExpanded,
                            onDismissRequest = { priorityExpanded = false }
                        ) {
                            EventPriority.values().forEach { pri ->
                                DropdownMenuItem(
                                    text = { Text(pri.displayName) },
                                    onClick = {
                                        priority = pri
                                        priorityExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                    val newEvent = CalendarEvent(
                        id = event?.id ?: "event_${System.currentTimeMillis()}",
                        title = title,
                        description = description,
                        location = location,
                        category = category,
                        priority = priority,
                        isAllDay = isAllDay,
                        startDateTime = event?.startDateTime ?: now,
                        endDateTime = event?.endDateTime ?: now.plus(1, DateTimeUnit.HOUR),
                        color = category.color,
                        createdAt = event?.createdAt ?: now,
                        updatedAt = now
                    )
                    onSave(newEvent)
                },
                enabled = title.isNotBlank()
            ) {
                Text(if (isCreating) "Create" else "Save")
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (!isCreating && onDelete != null && event != null) {
                    TextButton(
                        onClick = { onDelete(event) },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Delete")
                    }
                }
                
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        },
        modifier = modifier
    )
}

// ============================================================================
// 🛠️ UTILITY FUNCTIONS
// ============================================================================

data class DayInfo(
    val date: LocalDate,
    val isCurrentMonth: Boolean,
    val isToday: Boolean
)

private fun generateDaysOfWeek(startDay: DayOfWeek): List<DayOfWeek> {
    val days = DayOfWeek.values().toList()
    val startIndex = days.indexOf(startDay)
    return days.drop(startIndex) + days.take(startIndex)
}

private fun generateMonthDays(date: LocalDate, weekStartDay: DayOfWeek): List<DayInfo> {
    val firstDayOfMonth = LocalDate(date.year, date.month, 1)
    val lastDayOfMonth = firstDayOfMonth.plus(1, DateTimeUnit.MONTH).minus(1, DateTimeUnit.DAY)
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    
    val daysOfWeek = generateDaysOfWeek(weekStartDay)
    val firstDayIndex = daysOfWeek.indexOf(firstDayOfMonth.dayOfWeek)
    
    val days = mutableListOf<DayInfo>()
    
    // Previous month days
    val prevMonth = firstDayOfMonth.minus(1, DateTimeUnit.MONTH)
    val prevMonthLastDay = prevMonth.plus(1, DateTimeUnit.MONTH).minus(1, DateTimeUnit.DAY)
    for (i in firstDayIndex - 1 downTo 0) {
        val day = prevMonthLastDay.minus(i, DateTimeUnit.DAY)
        days.add(DayInfo(day, false, day == today))
    }
    
    // Current month days
    var currentDay = firstDayOfMonth
    while (currentDay <= lastDayOfMonth) {
        days.add(DayInfo(currentDay, true, currentDay == today))
        currentDay = currentDay.plus(1, DateTimeUnit.DAY)
    }
    
    // Next month days
    val nextMonth = lastDayOfMonth.plus(1, DateTimeUnit.DAY)
    var nextDay = nextMonth
    while (days.size < 42) { // 6 weeks * 7 days
        days.add(DayInfo(nextDay, false, nextDay == today))
        nextDay = nextDay.plus(1, DateTimeUnit.DAY)
    }
    
    return days
}

private fun generateWeekDays(date: LocalDate, weekStartDay: DayOfWeek): List<DayInfo> {
    val daysOfWeek = generateDaysOfWeek(weekStartDay)
    val currentDayIndex = daysOfWeek.indexOf(date.dayOfWeek)
    val weekStart = date.minus(currentDayIndex, DateTimeUnit.DAY)
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    
    return (0..6).map { dayOffset ->
        val day = weekStart.plus(dayOffset, DateTimeUnit.DAY)
        DayInfo(day, true, day == today)
    }
}

private fun formatCalendarTitle(date: LocalDate, viewMode: CalendarViewMode): String {
    return when (viewMode) {
        CalendarViewMode.DAY -> "${date.month.name} ${date.dayOfMonth}, ${date.year}"
        CalendarViewMode.WEEK -> {
            val weekDays = generateWeekDays(date, DayOfWeek.MONDAY)
            val start = weekDays.first().date
            val end = weekDays.last().date
            if (start.month == end.month) {
                "${start.month.name} ${start.dayOfMonth}-${end.dayOfMonth}, ${start.year}"
            } else {
                "${start.month.name} ${start.dayOfMonth} - ${end.month.name} ${end.dayOfMonth}, ${start.year}"
            }
        }
        CalendarViewMode.MONTH -> "${date.month.name} ${date.year}"
        CalendarViewMode.YEAR -> date.year.toString()
        else -> "${date.month.name} ${date.year}"
    }
}

private fun formatHour(hour: Int): String {
    return when (hour) {
        0 -> "12 AM"
        in 1..11 -> "$hour AM"
        12 -> "12 PM"
        else -> "${hour - 12} PM"
    }
}

private fun formatDate(date: LocalDate): String {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val tomorrow = today.plus(1, DateTimeUnit.DAY)
    val yesterday = today.minus(1, DateTimeUnit.DAY)
    
    return when (date) {
        today -> "Today"
        tomorrow -> "Tomorrow"
        yesterday -> "Yesterday"
        else -> "${date.month.name} ${date.dayOfMonth}, ${date.year}"
    }
}

private fun formatEventDateTime(event: CalendarEvent): String {
    return if (event.isAllDay) {
        "All day"
    } else {
        "${formatTime(event.startDateTime)} - ${formatTime(event.endDateTime)}"
    }
}

private fun formatEventTime(event: CalendarEvent): String {
    return if (event.isAllDay) {
        "All day"
    } else {
        "${formatTime(event.startDateTime)} - ${formatTime(event.endDateTime)}"
    }
}

private fun formatTime(dateTime: LocalDateTime): String {
    val hour = if (dateTime.hour == 0) 12 else if (dateTime.hour > 12) dateTime.hour - 12 else dateTime.hour
    val minute = dateTime.minute.toString().padStart(2, '0')
    val amPm = if (dateTime.hour < 12) "AM" else "PM"
    return "$hour:$minute $amPm"
}