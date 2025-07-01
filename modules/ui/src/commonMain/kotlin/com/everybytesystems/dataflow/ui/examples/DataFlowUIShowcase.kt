package com.everybytesystems.dataflow.ui.examples

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import com.everybytesystems.dataflow.ui.components.*
import com.everybytesystems.dataflow.ui.charts.*
import com.everybytesystems.dataflow.ui.dashboard.*
import com.everybytesystems.dataflow.ui.forms.*
import com.everybytesystems.dataflow.ui.theme.*

/**
 * DataFlow UI Showcase
 * Comprehensive example demonstrating all UI components
 */

@Composable
fun DataFlowUIShowcase() {
    DataFlowTheme {
        var selectedTab by remember { mutableStateOf(0) }
        
        Column(modifier = Modifier.fillMaxSize()) {
            // Tab navigation
            TabRow(selectedTabIndex = selectedTab) {
                val tabs = listOf("Dashboard", "Charts", "Tables", "Forms", "Components")
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }
            
            // Tab content
            when (selectedTab) {
                0 -> DashboardShowcase()
                1 -> ChartsShowcase()
                2 -> TablesShowcase()
                3 -> FormsShowcase()
                4 -> ComponentsShowcase()
            }
        }
    }
}

// ============================================================================
// 📊 DASHBOARD SHOWCASE
// ============================================================================

@Composable
private fun DashboardShowcase() {
    val widgets = remember {
        listOf(
            DashboardWidget(
                id = "sales",
                title = "Total Sales",
                type = WidgetType.METRIC_CARD,
                size = WidgetSize.MEDIUM
            ),
            DashboardWidget(
                id = "revenue",
                title = "Revenue Trend",
                type = WidgetType.LINE_CHART,
                size = WidgetSize.LARGE
            ),
            DashboardWidget(
                id = "regions",
                title = "Sales by Region",
                type = WidgetType.BAR_CHART,
                size = WidgetSize.MEDIUM
            ),
            DashboardWidget(
                id = "completion",
                title = "Project Completion",
                type = WidgetType.GAUGE_CHART,
                size = WidgetSize.SMALL
            ),
            DashboardWidget(
                id = "status",
                title = "Status Overview",
                type = WidgetType.PIE_CHART,
                size = WidgetSize.MEDIUM
            ),
            DashboardWidget(
                id = "progress",
                title = "Progress Tracking",
                type = WidgetType.PROGRESS_INDICATOR,
                size = WidgetSize.WIDE
            )
        )
    }
    
    val config = DashboardConfig(
        title = "Executive Dashboard",
        subtitle = "Real-time business metrics and analytics",
        columns = 3,
        allowEdit = true
    )
    
    DataFlowExecutiveDashboard(
        widgets = widgets,
        config = config,
        onWidgetClick = { widget ->
            println("Clicked widget: ${widget.title}")
        },
        onRefresh = {
            println("Dashboard refreshed")
        }
    )
}

// ============================================================================
// 📈 CHARTS SHOWCASE
// ============================================================================

@Composable
private fun ChartsShowcase() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Line Chart
        item {
            val series = listOf(
                ChartSeries(
                    name = "Sales",
                    data = listOf(
                        ChartDataPoint(1f, 100f, "Jan", "$100K"),
                        ChartDataPoint(2f, 150f, "Feb", "$150K"),
                        ChartDataPoint(3f, 120f, "Mar", "$120K"),
                        ChartDataPoint(4f, 180f, "Apr", "$180K"),
                        ChartDataPoint(5f, 200f, "May", "$200K"),
                        ChartDataPoint(6f, 170f, "Jun", "$170K")
                    ),
                    color = DataFlowColors.Blue500
                ),
                ChartSeries(
                    name = "Profit",
                    data = listOf(
                        ChartDataPoint(1f, 80f, "Jan", "$80K"),
                        ChartDataPoint(2f, 120f, "Feb", "$120K"),
                        ChartDataPoint(3f, 100f, "Mar", "$100K"),
                        ChartDataPoint(4f, 140f, "Apr", "$140K"),
                        ChartDataPoint(5f, 160f, "May", "$160K"),
                        ChartDataPoint(6f, 130f, "Jun", "$130K")
                    ),
                    color = DataFlowColors.Green500
                )
            )
            
            DataFlowLineChart(
                series = series,
                config = ChartConfig(
                    title = "Sales & Profit Trend",
                    subtitle = "Monthly performance over 6 months"
                )
            )
        }
        
        // Bar Chart
        item {
            val data = listOf(
                ChartDataPoint(1f, 85f, "North", "85%"),
                ChartDataPoint(2f, 92f, "South", "92%"),
                ChartDataPoint(3f, 78f, "East", "78%"),
                ChartDataPoint(4f, 96f, "West", "96%"),
                ChartDataPoint(5f, 88f, "Central", "88%")
            )
            
            DataFlowBarChart(
                data = data,
                config = ChartConfig(
                    title = "Regional Performance",
                    subtitle = "Sales performance by region"
                ),
                horizontal = true
            )
        }
        
        // Pie Chart
        item {
            val data = listOf(
                ChartDataPoint(1f, 40f, "Desktop", "40%", DataFlowColors.Blue500),
                ChartDataPoint(2f, 35f, "Mobile", "35%", DataFlowColors.Green500),
                ChartDataPoint(3f, 15f, "Tablet", "15%", DataFlowColors.Orange500),
                ChartDataPoint(4f, 10f, "Other", "10%", DataFlowColors.Purple500)
            )
            
            DataFlowPieChart(
                data = data,
                config = ChartConfig(
                    title = "Traffic Sources",
                    subtitle = "Website traffic by device type"
                )
            )
        }
        
        // Gauge Chart
        item {
            DataFlowGaugeChart(
                value = 75f,
                maxValue = 100f,
                title = "System Performance",
                subtitle = "Overall system health score"
            )
        }
    }
}

// ============================================================================
// 📋 TABLES SHOWCASE
// ============================================================================

@Composable
private fun TablesShowcase() {
    val columns = listOf(
        TableColumn("id", "ID", TableColumnWidth.Fixed(60.dp)),
        TableColumn("name", "Product Name", TableColumnWidth.Flexible(2f)),
        TableColumn("category", "Category", TableColumnWidth.Flexible(1f)),
        TableColumn("price", "Price", TableColumnWidth.Fixed(100.dp), Alignment.End),
        TableColumn("stock", "Stock", TableColumnWidth.Fixed(80.dp), Alignment.CenterHorizontally),
        TableColumn("status", "Status", TableColumnWidth.Fixed(120.dp))
    )
    
    val data = listOf(
        TableRow("1", listOf(
            TableCell("1"),
            TableCell("MacBook Pro"),
            TableCell("Laptops"),
            TableCell("$2,499", "$2,499", CellType.NUMBER),
            TableCell("15", "15", CellType.NUMBER),
            TableCell("In Stock", "In Stock", CellType.STATUS)
        )),
        TableRow("2", listOf(
            TableCell("2"),
            TableCell("iPhone 15"),
            TableCell("Phones"),
            TableCell("$999", "$999", CellType.NUMBER),
            TableCell("32", "32", CellType.NUMBER),
            TableCell("In Stock", "In Stock", CellType.STATUS)
        )),
        TableRow("3", listOf(
            TableCell("3"),
            TableCell("iPad Air"),
            TableCell("Tablets"),
            TableCell("$599", "$599", CellType.NUMBER),
            TableCell("8", "8", CellType.NUMBER),
            TableCell("Low Stock", "Low Stock", CellType.STATUS)
        )),
        TableRow("4", listOf(
            TableCell("4"),
            TableCell("Apple Watch"),
            TableCell("Wearables"),
            TableCell("$399", "$399", CellType.NUMBER),
            TableCell("0", "0", CellType.NUMBER),
            TableCell("Out of Stock", "Out of Stock", CellType.STATUS)
        )),
        TableRow("5", listOf(
            TableCell("5"),
            TableCell("AirPods Pro"),
            TableCell("Audio"),
            TableCell("$249", "$249", CellType.NUMBER),
            TableCell("25", "25", CellType.NUMBER),
            TableCell("In Stock", "In Stock", CellType.STATUS)
        ))
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        DataFlowDataTable(
            columns = columns,
            data = data,
            selectable = true,
            onRowClick = { row ->
                println("Clicked row: ${row.id}")
            },
            onSelectionChange = { selectedRows ->
                println("Selected ${selectedRows.size} rows")
            }
        )
    }
}

// ============================================================================
// 📝 FORMS SHOWCASE
// ============================================================================

@Composable
private fun FormsShowcase() {
    val fields = listOf(
        FormField(
            key = "firstName",
            label = "First Name",
            type = FieldType.TEXT,
            required = true,
            placeholder = "Enter your first name"
        ),
        FormField(
            key = "lastName",
            label = "Last Name",
            type = FieldType.TEXT,
            required = true,
            placeholder = "Enter your last name"
        ),
        FormField(
            key = "email",
            label = "Email Address",
            type = FieldType.EMAIL,
            required = true,
            placeholder = "Enter your email address"
        ),
        FormField(
            key = "phone",
            label = "Phone Number",
            type = FieldType.PHONE,
            placeholder = "Enter your phone number"
        ),
        FormField(
            key = "company",
            label = "Company",
            type = FieldType.SELECT,
            options = listOf(
                FieldOption("tech", "Technology"),
                FieldOption("finance", "Finance"),
                FieldOption("healthcare", "Healthcare"),
                FieldOption("education", "Education"),
                FieldOption("other", "Other")
            ),
            placeholder = "Select your company type"
        ),
        FormField(
            key = "experience",
            label = "Years of Experience",
            type = FieldType.SLIDER,
            defaultValue = 5f
        ),
        FormField(
            key = "newsletter",
            label = "Subscribe to Newsletter",
            type = FieldType.SWITCH,
            defaultValue = true
        ),
        FormField(
            key = "comments",
            label = "Additional Comments",
            type = FieldType.TEXTAREA,
            placeholder = "Any additional information..."
        )
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        DataFlowForm(
            fields = fields,
            title = "Contact Information",
            subtitle = "Please fill out the form below",
            submitButtonText = "Submit Form",
            onSubmit = { values ->
                println("Form submitted with values: $values")
            }
        )
    }
}

// ============================================================================
// 🎨 COMPONENTS SHOWCASE
// ============================================================================

@Composable
private fun ComponentsShowcase() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Metric Cards
        item {
            Text(
                text = "Metric Cards",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DataFlowMetricCard(
                    title = "Total Users",
                    value = "12,345",
                    subtitle = "Active users this month",
                    trend = MetricTrend(15.2f, TrendDirection.UP, "vs last month"),
                    icon = Icons.Default.People,
                    modifier = Modifier.weight(1f)
                )
                
                DataFlowMetricCard(
                    title = "Revenue",
                    value = "$98,765",
                    subtitle = "Monthly recurring revenue",
                    trend = MetricTrend(-3.1f, TrendDirection.DOWN, "vs last month"),
                    icon = Icons.Default.AttachMoney,
                    color = DataFlowColors.Green500,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        // Status Components
        item {
            Text(
                text = "Status Components",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DataFlowStatusChip("Success", StatusType.SUCCESS)
                DataFlowStatusChip("Warning", StatusType.WARNING)
                DataFlowStatusChip("Error", StatusType.ERROR)
                DataFlowStatusChip("Info", StatusType.INFO)
            }
        }
        
        // Progress Indicators
        item {
            Text(
                text = "Progress Indicators",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DataFlowProgressIndicator(
                    progress = 0.75f,
                    label = "Project Alpha",
                    color = DataFlowColors.Blue500
                )
                
                DataFlowProgressIndicator(
                    progress = 0.45f,
                    label = "Project Beta",
                    color = DataFlowColors.Green500
                )
                
                DataFlowProgressIndicator(
                    progress = 0.90f,
                    label = "Project Gamma",
                    color = DataFlowColors.Orange500
                )
            }
        }
        
        // Search Bar
        item {
            Text(
                text = "Search Components",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            var searchQuery by remember { mutableStateOf("") }
            
            DataFlowSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "Search anything...",
                suggestions = listOf("Apple", "Android", "Windows", "Linux", "macOS")
            )
        }
        
        // Loading States
        item {
            Text(
                text = "Loading States",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DataFlowLoadingIndicator()
                
                Column {
                    DataFlowSkeletonLoader(height = 20.dp, width = 200.dp)
                    Spacer(modifier = Modifier.height(8.dp))
                    DataFlowSkeletonLoader(height = 16.dp, width = 150.dp)
                    Spacer(modifier = Modifier.height(8.dp))
                    DataFlowSkeletonLoader(height = 16.dp, width = 100.dp)
                }
            }
        }
        
        // Error State
        item {
            Text(
                text = "Error States",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                DataFlowErrorState(
                    message = "Failed to load data. Please check your connection and try again.",
                    onRetry = {
                        println("Retry clicked")
                    },
                    modifier = Modifier.padding(32.dp)
                )
            }
        }
    }
}