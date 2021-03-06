package com.quipalup.katydid.logentry.domain

import com.quipalup.katydid.common.id.Id
import java.util.UUID

data class LogEntry(
    val id: Id,
    val time: Long,
    val description: String,
    val amount: Int,
    val unit: String
)

// parallel change
sealed class LogEntry_ {
    abstract val childId: ChildId

    class Meal(
        val id: Id,
        override val childId: ChildId,
        val time: Long,
        val description: String,
        val amount: Int,
        val unit: String
    ) : LogEntry_()

    class Nap(
        val id: Id,
        override val childId: ChildId,
        val time: Long,
        val duration: Long
    ) : LogEntry_()
}

data class ChildId(val value: Id) {
    fun value(): UUID = value.value
}
