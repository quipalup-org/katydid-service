package com.quipalup.katydid.logentry.logentry.primaryadapter.httprest

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.quipalup.katydid.logentry.application.CreateLogEntryCommand
import com.quipalup.katydid.logentry.application.CreateLogEntryCommandHandler
import com.quipalup.katydid.logentry.domain.CreateLogEntryError
import com.quipalup.katydid.logentry.domain.LogEntry
import jdk.internal.net.http.HttpResponseImpl
import org.apache.coyote.Response
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.net.http.HttpResponse

@RestController
internal class CreateLogEntryEndpoint(private val createLogEntryCommandHandler: CreateLogEntryCommandHandler) {
    @PostMapping("/log-entries")
    @ResponseStatus(HttpStatus.CREATED)
    fun execute(logEntryRequestDocument: LogEntryRequestDocument): ResponseEntity<LogEntryResponseDocument>  {
        return logEntryRequestDocument.let {
            CreateLogEntryCommand(
                time = it.data.attributes.time,
                description = it.data.attributes.description,
                amount = it.data.attributes.time,
                unit = it.data.attributes.unit
            ).let {
                createLogEntryCommandHandler.execute(it).fold(
                    ifLeft = { ResponseEntity(LogEntryResponseDocument(), HttpStatus.CONFLICT) },
                    ifRight = {
                        ResponseEntity(LogEntryResponseDocument(
                            data =
                        ), HttpStatus.OK)
                    }
                )
            }
        }
    }

    private fun errorHandler(): (CreateLogEntryError) -> LogEntry = { throw RuntimeException() }

    private fun buildCommand(document: LogEntry): Either<CreateLogEntryError, CreateLogEntryCommand> = CreateLogEntryCommand(
            time = 1234,
            description = "Yogurt",
            amount = 12,
            unit = "percentage"
    ).right()
}

private fun LogEntry.toLogEntryResponseDocument(): LogEntryResponseDocument {
    return LogEntryResponseDocument(
            data = LogEntryResource(
                id = this.id.id.value.toString(),
                type = JsonApiTypes.MEAL_LOG_ENTRY,
                attributes = LogEntryResourceAttributes(
                    time = this.time,
                    amount = this.amount.toInt(),
                    unit = this.unit,
                    description = this.description
                )
            )
    )
}
