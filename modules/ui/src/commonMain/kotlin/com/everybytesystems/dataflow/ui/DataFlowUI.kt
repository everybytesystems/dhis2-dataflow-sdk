package com.everybytesystems.dataflow.ui

/**
 * DataFlow UI SDK
 * Complete UI toolkit for modern data applications
 * 
 * Features:
 * - 🎨 Modern Material Design 3 theme system
 * - 📊 Comprehensive chart library (Line, Bar, Pie, Gauge)
 * - 📋 Advanced data tables with sorting, filtering, and selection
 * - 📱 Executive dashboards with real-time updates
 * - 📝 Form components with validation
 * - 🔄 Loading states and error handling
 * - 🎯 Status indicators and progress components
 * - 🔍 Search and filtering capabilities
 * - 📈 Metric cards with trend indicators
 * - 🎪 Animations and transitions
 * 
 * @version 1.0.0
 * @author DataFlow Team
 */

// Re-export all public APIs for easy access

// Theme System
export { DataFlowTheme, DataFlowColors, DataFlowTypography } from './theme/DataFlowTheme'
export { getChartColors, getStatusColor } from './theme/DataFlowTheme'

// Core Components
export {
    DataFlowMetricCard,
    MetricTrend,
    TrendDirection,
    DataFlowDataTable,
    TableColumn,
    TableRow,
    TableCell,
    CellType,
    TableColumnWidth,
    DataFlowSearchBar,
    DataFlowStatusChip,
    StatusType,
    DataFlowProgressIndicator,
    DataFlowLoadingIndicator,
    DataFlowSkeletonLoader,
    DataFlowErrorState,
    DataFlowAnimatedVisibility
} from './components/DataFlowComponents'

// Chart Components
export {
    DataFlowLineChart,
    DataFlowBarChart,
    DataFlowPieChart,
    DataFlowGaugeChart,
    DataFlowChartContainer,
    ChartDataPoint,
    ChartSeries,
    ChartConfig
} from './charts/DataFlowCharts'

// Dashboard Components
export {
    DataFlowExecutiveDashboard,
    DashboardWidget,
    WidgetType,
    WidgetSize,
    DashboardConfig,
    DashboardLayout
} from './dashboard/DataFlowDashboard'

// Form Components
export {
    DataFlowForm,
    DataFlowTextField,
    DataFlowPasswordField,
    DataFlowTextArea,
    DataFlowDropdown,
    DataFlowCheckbox,
    DataFlowRadioGroup,
    DataFlowSwitch,
    DataFlowSlider,
    FormField,
    FieldType,
    FieldValidation,
    FieldOption,
    FormState
} from './forms/DataFlowForms'

/**
 * DataFlow UI Version
 */
const val DATAFLOW_UI_VERSION = "1.0.0"

/**
 * DataFlow UI Build Info
 */
object DataFlowUIInfo {
    const val VERSION = DATAFLOW_UI_VERSION
    const val NAME = "DataFlow UI SDK"
    const val DESCRIPTION = "Complete UI toolkit for modern data applications"
    
    val FEATURES = listOf(
        "Modern Material Design 3 theme system",
        "Comprehensive chart library",
        "Advanced data tables",
        "Executive dashboards",
        "Form components with validation",
        "Loading states and error handling",
        "Status indicators and progress components",
        "Search and filtering capabilities",
        "Metric cards with trend indicators",
        "Smooth animations and transitions"
    )
    
    fun printInfo() {
        println("=".repeat(50))
        println("$NAME v$VERSION")
        println("=".repeat(50))
        println(DESCRIPTION)
        println()
        println("Features:")
        FEATURES.forEach { feature ->
            println("  ✓ $feature")
        }
        println("=".repeat(50))
    }
}