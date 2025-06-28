package com.everybytesystems.dataflow.core.test

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

@Serializable
data class SimpleTestModel(
    val id: String,
    val name: String,
    val timestamp: Long = Clock.System.now().toEpochMilliseconds()
)

class SimpleTestClass {
    fun test(): String {
        return "Hello from DHIS2 DataFlow SDK Core!"
    }
}