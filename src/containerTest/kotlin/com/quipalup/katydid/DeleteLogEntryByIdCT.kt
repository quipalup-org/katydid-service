package com.quipalup.katydid

import com.quipalup.katydid.common.id.Id
import com.quipalup.katydid.logentry.domain.LogEntry
import com.quipalup.katydid.logentry.domain.LogEntryRepository
import io.restassured.RestAssured
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import java.util.UUID
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort

@SpringBootTest(
    classes = [Katydid::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)

class DeleteLogEntryByIdCT {

    @Autowired
    private lateinit var repository: LogEntryRepository

    @LocalServerPort
    private val port: Int = 0

    @BeforeEach
    fun setUp() {
        RestAssured.port = port
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
    }

    private val uuid: String = "e903d71b-234b-4129-996e-a6b411f2862d"

    @Test
    fun `delete log entry by id`() {
        repository.save(
            LogEntry(
                id = Id(UUID.fromString(uuid)),
                time = 123345534,
                description = "Yogurt with strawberries",
                amount = 100,
                unit = "percentage"
            )
        )
        Given {
            contentType("application/vnd.api+json")
            pathParam("id", uuid)
        } When {
            delete("/log-entries/{id}")
        } Then {
            statusCode(204)
        }
    }
}
