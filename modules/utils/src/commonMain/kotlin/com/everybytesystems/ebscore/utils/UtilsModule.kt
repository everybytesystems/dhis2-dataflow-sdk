package com.everybytesystems.ebscore.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.math.*
import kotlin.random.Random

/**
 * EBSCore Utils Module
 * Provides utility functions and extensions for the EBSCore SDK
 */

// ============================================================================
// 📅 DATE & TIME UTILITIES
// ============================================================================

object DateTimeUtils {
    
    fun now(): Instant = Clock.System.now()
    
    fun today(): LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
    
    fun formatDate(date: LocalDate, format: DateFormat = DateFormat.ISO): String {
        return when (format) {
            DateFormat.ISO -> date.toString()
            DateFormat.US -> "${date.monthNumber}/${date.dayOfMonth}/${date.year}"
            DateFormat.EU -> "${date.dayOfMonth}/${date.monthNumber}/${date.year}"
            DateFormat.READABLE -> "${date.month.name} ${date.dayOfMonth}, ${date.year}"
        }
    }
    
    fun formatDateTime(instant: Instant, timeZone: TimeZone = TimeZone.currentSystemDefault()): String {
        val localDateTime = instant.toLocalDateTime(timeZone)
        return "${formatDate(localDateTime.date)} ${localDateTime.time}"
    }
    
    fun parseDate(dateString: String): LocalDate? {
        return try {
            LocalDate.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }
    
    fun isToday(date: LocalDate): Boolean = date == today()
    
    fun isYesterday(date: LocalDate): Boolean = date == today().minus(DatePeriod(days = 1))
    
    fun isTomorrow(date: LocalDate): Boolean = date == today().plus(DatePeriod(days = 1))
    
    fun daysBetween(start: LocalDate, end: LocalDate): Int {
        return start.daysUntil(end)
    }
    
    fun addDays(date: LocalDate, days: Int): LocalDate {
        return date.plus(DatePeriod(days = days))
    }
    
    fun addMonths(date: LocalDate, months: Int): LocalDate {
        return date.plus(DatePeriod(months = months))
    }
    
    fun addYears(date: LocalDate, years: Int): LocalDate {
        return date.plus(DatePeriod(years = years))
    }
    
    fun getWeekOfYear(date: LocalDate): Int {
        // Simplified week calculation
        val dayOfYear = date.dayOfYear
        return (dayOfYear - 1) / 7 + 1
    }
    
    fun getQuarter(date: LocalDate): Int {
        return (date.monthNumber - 1) / 3 + 1
    }
}

enum class DateFormat { ISO, US, EU, READABLE }

// ============================================================================
// 🔢 MATH & STATISTICS UTILITIES
// ============================================================================

object MathUtils {
    
    fun mean(values: List<Double>): Double {
        return if (values.isEmpty()) 0.0 else values.sum() / values.size
    }
    
    fun median(values: List<Double>): Double {
        if (values.isEmpty()) return 0.0
        val sorted = values.sorted()
        val size = sorted.size
        return if (size % 2 == 0) {
            (sorted[size / 2 - 1] + sorted[size / 2]) / 2.0
        } else {
            sorted[size / 2]
        }
    }
    
    fun mode(values: List<Double>): Double? {
        return values.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key
    }
    
    fun standardDeviation(values: List<Double>): Double {
        if (values.isEmpty()) return 0.0
        val mean = mean(values)
        val variance = values.map { (it - mean).pow(2) }.sum() / values.size
        return sqrt(variance)
    }
    
    fun variance(values: List<Double>): Double {
        if (values.isEmpty()) return 0.0
        val mean = mean(values)
        return values.map { (it - mean).pow(2) }.sum() / values.size
    }
    
    fun percentile(values: List<Double>, percentile: Double): Double {
        if (values.isEmpty()) return 0.0
        val sorted = values.sorted()
        val index = (percentile / 100.0) * (sorted.size - 1)
        val lower = floor(index).toInt()
        val upper = ceil(index).toInt()
        
        return if (lower == upper) {
            sorted[lower]
        } else {
            val weight = index - lower
            sorted[lower] * (1 - weight) + sorted[upper] * weight
        }
    }
    
    fun correlation(x: List<Double>, y: List<Double>): Double {
        if (x.size != y.size || x.isEmpty()) return 0.0
        
        val meanX = mean(x)
        val meanY = mean(y)
        
        val numerator = x.zip(y) { xi, yi -> (xi - meanX) * (yi - meanY) }.sum()
        val denominatorX = x.map { (it - meanX).pow(2) }.sum()
        val denominatorY = y.map { (it - meanY).pow(2) }.sum()
        
        return if (denominatorX == 0.0 || denominatorY == 0.0) {
            0.0
        } else {
            numerator / sqrt(denominatorX * denominatorY)
        }
    }
    
    fun linearRegression(x: List<Double>, y: List<Double>): LinearRegressionResult {
        if (x.size != y.size || x.isEmpty()) {
            return LinearRegressionResult(0.0, 0.0, 0.0)
        }
        
        val n = x.size
        val meanX = mean(x)
        val meanY = mean(y)
        
        val numerator = x.zip(y) { xi, yi -> (xi - meanX) * (yi - meanY) }.sum()
        val denominator = x.map { (it - meanX).pow(2) }.sum()
        
        val slope = if (denominator == 0.0) 0.0 else numerator / denominator
        val intercept = meanY - slope * meanX
        val rSquared = correlation(x, y).pow(2)
        
        return LinearRegressionResult(slope, intercept, rSquared)
    }
    
    fun roundToDecimalPlaces(value: Double, places: Int): Double {
        val factor = 10.0.pow(places)
        return round(value * factor) / factor
    }
    
    fun clamp(value: Double, min: Double, max: Double): Double {
        return when {
            value < min -> min
            value > max -> max
            else -> value
        }
    }
    
    fun normalize(values: List<Double>): List<Double> {
        val min = values.minOrNull() ?: 0.0
        val max = values.maxOrNull() ?: 1.0
        val range = max - min
        
        return if (range == 0.0) {
            values.map { 0.0 }
        } else {
            values.map { (it - min) / range }
        }
    }
}

@Serializable
data class LinearRegressionResult(
    val slope: Double,
    val intercept: Double,
    val rSquared: Double
)

// ============================================================================
// 🔤 STRING UTILITIES
// ============================================================================

object StringUtils {
    
    fun isNullOrEmpty(str: String?): Boolean = str.isNullOrEmpty()
    
    fun isNullOrBlank(str: String?): Boolean = str.isNullOrBlank()
    
    fun truncate(str: String, maxLength: Int, suffix: String = "..."): String {
        return if (str.length <= maxLength) {
            str
        } else {
            str.take(maxLength - suffix.length) + suffix
        }
    }
    
    fun capitalize(str: String): String {
        return str.lowercase().replaceFirstChar { it.uppercase() }
    }
    
    fun camelCase(str: String): String {
        return str.split(" ", "_", "-")
            .mapIndexed { index, word ->
                if (index == 0) word.lowercase()
                else word.lowercase().replaceFirstChar { it.uppercase() }
            }
            .joinToString("")
    }
    
    fun kebabCase(str: String): String {
        return str.replace(Regex("([a-z])([A-Z])"), "$1-$2")
            .replace(" ", "-")
            .replace("_", "-")
            .lowercase()
    }
    
    fun snakeCase(str: String): String {
        return str.replace(Regex("([a-z])([A-Z])"), "$1_$2")
            .replace(" ", "_")
            .replace("-", "_")
            .lowercase()
    }
    
    fun removeAccents(str: String): String {
        // Simplified accent removal
        return str.replace(Regex("[àáâãäå]"), "a")
            .replace(Regex("[èéêë]"), "e")
            .replace(Regex("[ìíîï]"), "i")
            .replace(Regex("[òóôõö]"), "o")
            .replace(Regex("[ùúûü]"), "u")
            .replace(Regex("[ñ]"), "n")
            .replace(Regex("[ç]"), "c")
    }
    
    fun levenshteinDistance(str1: String, str2: String): Int {
        val len1 = str1.length
        val len2 = str2.length
        
        val dp = Array(len1 + 1) { IntArray(len2 + 1) }
        
        for (i in 0..len1) dp[i][0] = i
        for (j in 0..len2) dp[0][j] = j
        
        for (i in 1..len1) {
            for (j in 1..len2) {
                val cost = if (str1[i - 1] == str2[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,      // deletion
                    dp[i][j - 1] + 1,      // insertion
                    dp[i - 1][j - 1] + cost // substitution
                )
            }
        }
        
        return dp[len1][len2]
    }
    
    fun similarity(str1: String, str2: String): Double {
        val maxLen = maxOf(str1.length, str2.length)
        return if (maxLen == 0) 1.0
        else 1.0 - levenshteinDistance(str1, str2).toDouble() / maxLen
    }
    
    fun extractNumbers(str: String): List<Double> {
        val regex = Regex("-?\\d+(\\.\\d+)?")
        return regex.findAll(str).map { it.value.toDouble() }.toList()
    }
    
    fun maskEmail(email: String): String {
        val parts = email.split("@")
        if (parts.size != 2) return email
        
        val username = parts[0]
        val domain = parts[1]
        
        val maskedUsername = if (username.length <= 2) {
            username
        } else {
            username.first() + "*".repeat(username.length - 2) + username.last()
        }
        
        return "$maskedUsername@$domain"
    }
    
    fun maskPhone(phone: String): String {
        val digits = phone.filter { it.isDigit() }
        return if (digits.length >= 4) {
            "*".repeat(digits.length - 4) + digits.takeLast(4)
        } else {
            phone
        }
    }
}

// ============================================================================
// 📋 COLLECTION UTILITIES
// ============================================================================

object CollectionUtils {
    
    fun <T> List<T>.chunkedBy(predicate: (T) -> Boolean): List<List<T>> {
        val result = mutableListOf<List<T>>()
        var currentChunk = mutableListOf<T>()
        
        for (item in this) {
            if (predicate(item) && currentChunk.isNotEmpty()) {
                result.add(currentChunk.toList())
                currentChunk = mutableListOf()
            }
            currentChunk.add(item)
        }
        
        if (currentChunk.isNotEmpty()) {
            result.add(currentChunk)
        }
        
        return result
    }
    
    fun <T> List<T>.groupByConsecutive(keySelector: (T) -> Any): List<List<T>> {
        if (isEmpty()) return emptyList()
        
        val result = mutableListOf<List<T>>()
        var currentGroup = mutableListOf<T>()
        var currentKey = keySelector(first())
        
        for (item in this) {
            val key = keySelector(item)
            if (key != currentKey) {
                result.add(currentGroup.toList())
                currentGroup = mutableListOf()
                currentKey = key
            }
            currentGroup.add(item)
        }
        
        if (currentGroup.isNotEmpty()) {
            result.add(currentGroup)
        }
        
        return result
    }
    
    fun <T> List<T>.takeRandom(count: Int): List<T> {
        return if (count >= size) this else shuffled().take(count)
    }
    
    fun <T> List<T>.sample(sampleSize: Int): List<T> {
        return takeRandom(sampleSize)
    }
    
    fun <T> List<T>.mode(): T? {
        return groupingBy { it }.eachCount().maxByOrNull { it.value }?.key
    }
    
    fun <T> List<T>.frequencies(): Map<T, Int> {
        return groupingBy { it }.eachCount()
    }
    
    fun <T> List<T>.duplicates(): List<T> {
        return frequencies().filter { it.value > 1 }.keys.toList()
    }
    
    fun <T> List<T>.unique(): List<T> {
        return distinct()
    }
    
    fun <T> List<T>.rotateLeft(positions: Int): List<T> {
        if (isEmpty()) return this
        val normalizedPositions = positions % size
        return drop(normalizedPositions) + take(normalizedPositions)
    }
    
    fun <T> List<T>.rotateRight(positions: Int): List<T> {
        return rotateLeft(-positions)
    }
}

// ============================================================================
// 🔐 VALIDATION UTILITIES
// ============================================================================

object ValidationUtils {
    
    fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        return emailRegex.matches(email)
    }
    
    fun isValidPhone(phone: String): Boolean {
        val phoneRegex = Regex("^[+]?[0-9]{10,15}$")
        return phoneRegex.matches(phone.replace(Regex("[\\s()-]"), ""))
    }
    
    fun isValidUrl(url: String): Boolean {
        val urlRegex = Regex("^https?://[\\w\\-]+(\\.[\\w\\-]+)+([\\w\\-\\.,@?^=%&:/~\\+#]*[\\w\\-\\@?^=%&/~\\+#])?$")
        return urlRegex.matches(url)
    }
    
    fun isValidIPAddress(ip: String): Boolean {
        val ipRegex = Regex("^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$")
        return ipRegex.matches(ip)
    }
    
    fun isStrongPassword(password: String): Boolean {
        return password.length >= 8 &&
                password.any { it.isUpperCase() } &&
                password.any { it.isLowerCase() } &&
                password.any { it.isDigit() } &&
                password.any { !it.isLetterOrDigit() }
    }
    
    fun validateRequired(value: String?, fieldName: String): ValidationResult {
        return if (value.isNullOrBlank()) {
            ValidationResult.Invalid("$fieldName is required")
        } else {
            ValidationResult.Valid
        }
    }
    
    fun validateLength(value: String?, minLength: Int, maxLength: Int, fieldName: String): ValidationResult {
        return when {
            value == null -> ValidationResult.Invalid("$fieldName is required")
            value.length < minLength -> ValidationResult.Invalid("$fieldName must be at least $minLength characters")
            value.length > maxLength -> ValidationResult.Invalid("$fieldName must be at most $maxLength characters")
            else -> ValidationResult.Valid
        }
    }
    
    fun validateRange(value: Double, min: Double, max: Double, fieldName: String): ValidationResult {
        return when {
            value < min -> ValidationResult.Invalid("$fieldName must be at least $min")
            value > max -> ValidationResult.Invalid("$fieldName must be at most $max")
            else -> ValidationResult.Valid
        }
    }
}

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val message: String) : ValidationResult()
}

// ============================================================================
// 🎲 RANDOM UTILITIES
// ============================================================================

object RandomUtils {
    
    fun randomString(length: Int, charset: String = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"): String {
        return (1..length)
            .map { charset.random() }
            .joinToString("")
    }
    
    fun randomAlphanumeric(length: Int): String {
        return randomString(length, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789")
    }
    
    fun randomNumeric(length: Int): String {
        return randomString(length, "0123456789")
    }
    
    fun randomAlphabetic(length: Int): String {
        return randomString(length, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz")
    }
    
    fun randomUUID(): String {
        return "${randomAlphanumeric(8)}-${randomAlphanumeric(4)}-${randomAlphanumeric(4)}-${randomAlphanumeric(4)}-${randomAlphanumeric(12)}"
    }
    
    fun randomDouble(min: Double = 0.0, max: Double = 1.0): Double {
        return Random.nextDouble(min, max)
    }
    
    fun randomInt(min: Int = 0, max: Int = Int.MAX_VALUE): Int {
        return Random.nextInt(min, max)
    }
    
    fun randomBoolean(): Boolean {
        return Random.nextBoolean()
    }
    
    fun <T> randomChoice(items: List<T>): T? {
        return if (items.isEmpty()) null else items[Random.nextInt(items.size)]
    }
    
    fun <T> randomChoices(items: List<T>, count: Int): List<T> {
        return (1..count).mapNotNull { randomChoice(items) }
    }
}

// ============================================================================
// 🔄 RETRY UTILITIES
// ============================================================================

object RetryUtils {
    
    suspend fun <T> retry(
        times: Int = 3,
        delay: Long = 1000,
        backoffMultiplier: Double = 2.0,
        predicate: (Throwable) -> Boolean = { true },
        block: suspend () -> T
    ): T {
        var currentDelay = delay
        repeat(times - 1) { attempt ->
            try {
                return block()
            } catch (e: Exception) {
                if (!predicate(e)) throw e
                kotlinx.coroutines.delay(currentDelay)
                currentDelay = (currentDelay * backoffMultiplier).toLong()
            }
        }
        return block() // Last attempt
    }
    
    suspend fun <T> retryWithExponentialBackoff(
        maxAttempts: Int = 3,
        initialDelay: Long = 1000,
        maxDelay: Long = 30000,
        factor: Double = 2.0,
        block: suspend () -> T
    ): T {
        var currentDelay = initialDelay
        repeat(maxAttempts - 1) {
            try {
                return block()
            } catch (e: Exception) {
                kotlinx.coroutines.delay(currentDelay)
                currentDelay = minOf((currentDelay * factor).toLong(), maxDelay)
            }
        }
        return block() // Last attempt
    }
}

// ============================================================================
// 📊 PERFORMANCE UTILITIES
// ============================================================================

// Platform-specific time function
expect fun getCurrentTimeMillis(): Long

object PerformanceUtils {
    
    suspend fun <T> measureTime(block: suspend () -> T): Pair<T, Long> {
        val startTime = getCurrentTimeMillis()
        val result = block()
        val endTime = getCurrentTimeMillis()
        return result to (endTime - startTime)
    }
    
    suspend fun <T> benchmark(
        iterations: Int = 1000,
        warmupIterations: Int = 100,
        block: suspend () -> T
    ): BenchmarkResult {
        // Warmup
        repeat(warmupIterations) { block() }
        
        val times = mutableListOf<Long>()
        repeat(iterations) {
            val (_, time) = measureTime { block() }
            times.add(time)
        }
        
        return BenchmarkResult(
            iterations = iterations,
            totalTime = times.sum(),
            averageTime = times.average(),
            minTime = times.minOrNull() ?: 0L,
            maxTime = times.maxOrNull() ?: 0L,
            medianTime = MathUtils.median(times.map { it.toDouble() }).toLong()
        )
    }
}

@Serializable
data class BenchmarkResult(
    val iterations: Int,
    val totalTime: Long,
    val averageTime: Double,
    val minTime: Long,
    val maxTime: Long,
    val medianTime: Long
)

// ============================================================================
// 🔧 EXTENSION FUNCTIONS
// ============================================================================

// String Extensions
fun String.isEmail(): Boolean = ValidationUtils.isValidEmail(this)
fun String.isPhone(): Boolean = ValidationUtils.isValidPhone(this)
fun String.isUrl(): Boolean = ValidationUtils.isValidUrl(this)
fun String.isStrongPassword(): Boolean = ValidationUtils.isStrongPassword(this)
fun String.truncate(maxLength: Int, suffix: String = "..."): String = StringUtils.truncate(this, maxLength, suffix)
fun String.toCamelCase(): String = StringUtils.camelCase(this)
fun String.toKebabCase(): String = StringUtils.kebabCase(this)
fun String.toSnakeCase(): String = StringUtils.snakeCase(this)

// List Extensions
fun <T> List<T>.takeRandomElements(count: Int): List<T> = with(CollectionUtils) { takeRandom(count) }
fun <T> List<T>.sampleElements(sampleSize: Int): List<T> = with(CollectionUtils) { sample(sampleSize) }
fun <T> List<T>.findMode(): T? = with(CollectionUtils) { mode() }
fun <T> List<T>.getFrequencies(): Map<T, Int> = with(CollectionUtils) { frequencies() }

// Double Extensions
fun Double.roundTo(places: Int): Double = MathUtils.roundToDecimalPlaces(this, places)
fun Double.clamp(min: Double, max: Double): Double = MathUtils.clamp(this, min, max)

// Date Extensions
fun LocalDate.isToday(): Boolean = DateTimeUtils.isToday(this)
fun LocalDate.isYesterday(): Boolean = DateTimeUtils.isYesterday(this)
fun LocalDate.isTomorrow(): Boolean = DateTimeUtils.isTomorrow(this)
fun LocalDate.format(format: DateFormat = DateFormat.ISO): String = DateTimeUtils.formatDate(this, format)