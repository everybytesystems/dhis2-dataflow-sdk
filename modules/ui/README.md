# 🎨 EBSCore UI SDK

A comprehensive, production-ready multiplatform UI toolkit for Android, iOS, Desktop, and Web with Kotlin Multiplatform and Compose.

## ✨ Features

### 🎨 **Modern Design System**
- Material Design 3 implementation
- Dark/Light theme support
- Consistent color palette and typography
- Accessible design patterns

### 📊 **Comprehensive Chart Library**
- **Line Charts** - Multi-series with animations and interactions
- **Bar Charts** - Horizontal/vertical with gradient fills
- **Pie Charts** - Donut mode with interactive legends
- **Gauge Charts** - KPI displays with progress indicators
- **Sparklines** - Compact trend visualizations

### 📋 **Advanced Data Tables**
- Virtual scrolling for large datasets
- Sorting and filtering capabilities
- Row selection (single/multiple)
- Search functionality
- Custom cell renderers
- Responsive column widths

### 📱 **Executive Dashboards**
- Grid, masonry, and flow layouts
- Real-time data updates
- Drag-and-drop widget arrangement
- Multiple widget types
- Edit mode for customization

### 📝 **Form Components**
- Complete form validation
- Multiple input types (text, email, password, etc.)
- Dropdown and multi-select
- Checkboxes, radio buttons, switches
- Sliders and date pickers
- Custom validation rules

### 🎯 **UI Components**
- Metric cards with trend indicators
- Status chips and progress bars
- Loading states and error handling
- Search bars with suggestions
- Skeleton loaders
- Animated transitions

## 🚀 Quick Start

### Installation

Add to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.everybytesystems:dhis2-ebscore-sdk-ui:1.0.0")
}
```

### Basic Usage

```kotlin
import com.everybytesystems.ebscore.ui.theme.EBSCoreTheme
import com.everybytesystems.ebscore.ui.components.*
import com.everybytesystems.ebscore.ui.charts.*

@Composable
fun MyApp() {
    EBSCoreTheme {
        Column {
            // Metric Card
            EBSCoreMetricCard(
                title = "Total Sales",
                value = "$125,430",
                trend = MetricTrend(12.5f, TrendDirection.UP),
                icon = Icons.Default.TrendingUp
            )
            
            // Line Chart
            EBSCoreLineChart(
                series = listOf(
                    ChartSeries(
                        name = "Revenue",
                        data = chartData,
                        color = EBSCoreColors.Blue500
                    )
                ),
                config = ChartConfig(
                    title = "Monthly Revenue",
                    showLegend = true
                )
            )
            
            // Data Table
            EBSCoreDataTable(
                columns = tableColumns,
                data = tableData,
                selectable = true,
                searchable = true
            )
        }
    }
}
```

## 📊 Chart Examples

### Line Chart
```kotlin
val series = listOf(
    ChartSeries(
        name = "Sales",
        data = listOf(
            ChartDataPoint(1f, 100f, "Jan", "$100K"),
            ChartDataPoint(2f, 150f, "Feb", "$150K"),
            ChartDataPoint(3f, 120f, "Mar", "$120K")
        ),
        color = EBSCoreColors.Blue500
    )
)

EBSCoreLineChart(
    series = series,
    config = ChartConfig(
        title = "Sales Trend",
        animated = true,
        showGrid = true
    )
)
```

### Bar Chart
```kotlin
val data = listOf(
    ChartDataPoint(1f, 80f, "Q1", "80%"),
    ChartDataPoint(2f, 120f, "Q2", "120%"),
    ChartDataPoint(3f, 95f, "Q3", "95%")
)

EBSCoreBarChart(
    data = data,
    horizontal = true,
    config = ChartConfig(title = "Quarterly Performance")
)
```

### Pie Chart
```kotlin
val data = listOf(
    ChartDataPoint(1f, 40f, "Desktop", "40%", EBSCoreColors.Blue500),
    ChartDataPoint(2f, 35f, "Mobile", "35%", EBSCoreColors.Green500),
    ChartDataPoint(3f, 25f, "Tablet", "25%", EBSCoreColors.Orange500)
)

EBSCorePieChart(
    data = data,
    donutMode = true,
    config = ChartConfig(title = "Traffic Sources")
)
```

## 📋 Data Table Example

```kotlin
val columns = listOf(
    TableColumn("name", "Product Name"),
    TableColumn("price", "Price", alignment = Alignment.End),
    TableColumn("status", "Status")
)

val data = listOf(
    TableRow("1", listOf(
        TableCell("MacBook Pro"),
        TableCell("$2,499", type = CellType.NUMBER),
        TableCell("In Stock", type = CellType.STATUS)
    ))
)

EBSCoreDataTable(
    columns = columns,
    data = data,
    selectable = true,
    sortable = true,
    searchable = true,
    onRowClick = { row -> println("Clicked: ${row.id}") }
)
```

## 📱 Dashboard Example

```kotlin
val widgets = listOf(
    DashboardWidget(
        id = "sales",
        title = "Total Sales",
        type = WidgetType.METRIC_CARD,
        size = WidgetSize.MEDIUM
    ),
    DashboardWidget(
        id = "chart",
        title = "Revenue Trend",
        type = WidgetType.LINE_CHART,
        size = WidgetSize.LARGE
    )
)

EBSCoreExecutiveDashboard(
    widgets = widgets,
    config = DashboardConfig(
        title = "Executive Dashboard",
        columns = 3,
        allowEdit = true
    ),
    onWidgetClick = { widget -> /* Handle click */ },
    onRefresh = { /* Refresh data */ }
)
```

## 📝 Form Example

```kotlin
val fields = listOf(
    FormField(
        key = "name",
        label = "Full Name",
        type = FieldType.TEXT,
        required = true
    ),
    FormField(
        key = "email",
        label = "Email",
        type = FieldType.EMAIL,
        required = true
    ),
    FormField(
        key = "company",
        label = "Company",
        type = FieldType.SELECT,
        options = listOf(
            FieldOption("tech", "Technology"),
            FieldOption("finance", "Finance")
        )
    )
)

EBSCoreForm(
    fields = fields,
    title = "Contact Form",
    onSubmit = { values -> 
        println("Form submitted: $values")
    }
)
```

## 🎨 Theming

### Custom Colors
```kotlin
EBSCoreTheme(
    darkTheme = false
) {
    // Your app content
}
```

### Accessing Theme Colors
```kotlin
@Composable
fun MyComponent() {
    val colors = EBSCoreThemeColors.current
    val chartColors = getChartColors()
    
    // Use theme colors
    Box(
        modifier = Modifier.background(colors.primary)
    )
}
```

## 🔧 Advanced Usage

### Custom Chart Colors
```kotlin
val customConfig = ChartConfig(
    colors = listOf(
        Color(0xFF1976D2),
        Color(0xFF388E3C),
        Color(0xFFF57C00)
    )
)
```

### Table Cell Renderers
```kotlin
TableCell(
    value = "75",
    displayValue = "75%",
    type = CellType.PROGRESS
)
```

### Form Validation
```kotlin
FormField(
    key = "password",
    label = "Password",
    type = FieldType.PASSWORD,
    validation = FieldValidation(
        minLength = 8,
        pattern = Regex("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$"),
        customValidator = { value ->
            if (!value.contains("@")) "Must contain @" else null
        }
    )
)
```

## 📱 Platform Support

- ✅ **Android** - Native Android with Material Design 3
- ✅ **JVM/Desktop** - Desktop applications
- 🚧 **iOS** - Coming soon
- 🚧 **Web** - Kotlin/JS support planned

## 🎯 Performance

- **Virtual Scrolling** - Handle millions of table rows
- **Canvas Rendering** - Smooth 60fps chart animations
- **Lazy Loading** - Efficient memory usage
- **State Management** - Optimized recomposition

## 🔍 Accessibility

- **Screen Reader Support** - Full accessibility compliance
- **High Contrast Mode** - Enhanced visibility options
- **Keyboard Navigation** - Complete keyboard support
- **Touch Targets** - Minimum 48dp touch areas

## 📚 Examples

Check out the comprehensive showcase:

```kotlin
import com.everybytesystems.ebscore.ui.examples.EBSCoreUIShowcase

@Composable
fun App() {
    EBSCoreUIShowcase()
}
```

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guide](../../CONTRIBUTING.md) for details.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](../../LICENSE) file for details.

## 🆘 Support

- 📖 [Documentation](../../docs/)
- 🐛 [Issue Tracker](../../issues)
- 💬 [Discussions](../../discussions)

---

**EBSCore UI SDK** - Building beautiful data applications, one component at a time! 🚀