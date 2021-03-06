package com.quipalup.katydid.logentry.primaryadapter.api

data class SearchLogEntriesDocument(val data: List<SearchLogEntryDocument> = listOf())

sealed class SearchLogEntryDocument {
    data class MealLogEntryDocument(
        val id: String,
        val type: String = "meal",
        val attributes: MealLogEntryDocumentAttributes,
        val links: LogEntryLinksAttribute = LogEntryLinksAttribute("https://katydid-web-client.s3.us-east-2.amazonaws.com/icons/meal-icon.svg")
    ) : SearchLogEntryDocument()

    data class NapLogEntryDocument(
        val id: String,
        val type: String = "nap",
        val attributes: NapLogEntryDocumentAttributes,
        val links: LogEntryLinksAttribute = LogEntryLinksAttribute("https://katydid-web-client.s3.us-east-2.amazonaws.com/icons/nap-icon.svg")
    ) : SearchLogEntryDocument()
}

data class MealLogEntryDocumentAttributes(
    val childId: String,
    val time: Long,
    val description: String,
    val amount: Int,
    val unit: String
)

data class NapLogEntryDocumentAttributes(
    val childId: String,
    val time: Long,
    val duration: Long
)

data class LogEntryLinksAttribute(val iconURL: String)
