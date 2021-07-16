package com.quipalup.katydid.logentry.primaryadapter.api

import arrow.core.Either
import com.quipalup.katydid.child.search.application.ChildResult
import com.quipalup.katydid.child.search.application.FindChildByIdQuery
import com.quipalup.katydid.child.search.application.FindChildByIdQueryHandler
import com.quipalup.katydid.child.search.domain.FindChildByIdError
import com.quipalup.katydid.logentry.application.SearchLogEntriesByChildIdCommand
import com.quipalup.katydid.logentry.application.SearchLogEntriesCommandHandler
import com.quipalup.katydid.logentry.domain.LogEntry_
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class SearchLogEntriesEndpoint(
    private val searchLogEntriesCommandHandler: SearchLogEntriesCommandHandler,
    private val findChildByIdQueryHandler: FindChildByIdQueryHandler
) {

    @GetMapping("log-entries")
    fun matchingCriteria(
        @RequestParam(required = false) parameters: Map<String, String>?
    ): SearchLogEntriesDocument {

        val x = mapOf<String, String>(
            "filter" to parameters!!.get("filter[childId]").toString()!!,
            "included" to parameters!!.get("included").toString()
        )
        return x.run {
            this.get("filter")!!.let {
                SearchLogEntriesByChildIdCommand(it).let {
                    val child = findChildByIdQueryHandler.execute(FindChildByIdQuery(it.childId))
                    val logEntries = searchLogEntriesCommandHandler.execute(it)
                    Pair(child, logEntries)
                }.let {
                    val toSearchLogEntriesDocument = it.toSearchLogEntriesDocument()
                    toSearchLogEntriesDocument
                }
            }
        }
    }
}

fun Pair<Either<FindChildByIdError, ChildResult>, List<LogEntry_>>.toSearchLogEntriesDocument(): SearchLogEntriesDocument {
    return when {
        this.second.isEmpty() -> SearchLogEntriesDocument()
        else -> SearchLogEntriesDocument(
            data = this.second.map {
                when (it) {
                    is LogEntry_.Meal -> SearchLogEntryDocument.MealLogEntryDocument(
                        id = it.id.value.toString(),
                        attributes = MealLogEntryDocumentAttributes(
                            childId = it.childId.value().toString(),
                            time = it.time,
                            description = it.description,
                            amount = it.amount,
                            unit = it.unit
                        ),
                        relationships = LogEntryChildRelationship(
                            LogEntryChild(
                                id = it.childId.value().toString()
                            )
                        )
                    )
                    is LogEntry_.Nap -> SearchLogEntryDocument.NapLogEntryDocument(
                        id = it.id.value.toString(),
                        attributes = NapLogEntryDocumentAttributes(
                            childId = it.childId.value().toString(),
                            time = it.time,
                            duration = it.duration
                        ),
                        relationships = LogEntryChildRelationship(
                            LogEntryChild(
                                id = it.childId.value().toString()
                            )
                        )
                    )
                }
            },
            included = this.first.fold(
                ifLeft = { listOf<IncludedChildDocument>() },
                ifRight = {
                    listOf(it.let {
                        IncludedChildDocument.ChildIncluded(
                            id = it.id,
                            attributes = ChildAttributes(
                                name = it.name,
                                portraitURL = it.portraitURL,
                                isPresent = it.isPresent
                            )
                        )
                    }
                    )
                }
            )
        )
    }
}
