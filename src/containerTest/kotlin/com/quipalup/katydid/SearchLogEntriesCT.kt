package com.quipalup.katydid

import com.quipalup.katydid.common.id.toChildId
import com.quipalup.katydid.common.id.toId
import com.quipalup.katydid.logentry.domain.ChildId
import com.quipalup.katydid.logentry.domain.LogEntryRepositoryPC
import com.quipalup.katydid.logentry.domain.LogEntry_
import io.restassured.RestAssured
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.annotation.DirtiesContext

@SpringBootTest(
    classes = [Katydid::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class SearchLogEntriesCT {
    @Autowired
    private lateinit var repository: LogEntryRepositoryPC

    @LocalServerPort
    private val port: Int = 0

    @BeforeEach
    fun setUp() {
        RestAssured.port = port
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
    }

    @Test
    fun `matching filter criteria for child id`() {
        repository.save(thisChildsNapLogEntry)
        repository.save(thisChildsMealLogEntry)
        repository.save(notThisChildsNapLogEntry)

        Given {
            param("filter[childId]", childId.value.toString())
            param("included", child)
        } When {
            get("/log-entries")
        } Then {
            statusCode(200)
        } Extract {
            `assert that response body is equal to`(body().asString(), expectedResponseBody)
        }
    }

    companion object {
        private val childId = UUID.fromString("5ee62461-adb8-4618-a110-06290a787223").toId()
        private val mealId = UUID.fromString("93acd93f-cc03-49ab-b761-b2b2810ad505").toId()
        private val napId = UUID.fromString("7a49cfd6-6a8a-404c-ad97-c51763341fe1").toId()
        private val child = "child"

        private val time = ZonedDateTime
            .of(2021, 6, 23, 20, 30, 50, 4, ZoneId.of("UTC"))
            .toEpochSecond()
        private const val description = "Spaghetti bolognese"
        private const val amount = 4

        private const val duration: Long = 3
        private const val unit = "grams"

        private val childName = "Blanca"
        private val isPresent = true

        private val thisChildsMealLogEntry = LogEntry_.Meal(
            id = mealId,
            childId = ChildId(childId),
            time = time,
            description = description,
            amount = amount,
            unit = unit
        )
        private val thisChildsNapLogEntry = LogEntry_.Nap(
            id = napId,
            childId = ChildId(childId),
            time = time,
            duration = duration
        )

        private val notThisChildsNapLogEntry = LogEntry_.Nap(
            id = UUID.randomUUID().toId(),
            childId = UUID.randomUUID().toChildId(),
            time = time,
            duration = duration
        )
        private val expectedResponseBody = """
            {
                "data": [
                    {
                        "id": "${thisChildsMealLogEntry.id.value}",
                        "type": "meal",
                        "attributes": {
                            "childId": "${childId.value}",
                            "time": $time,
                            "description": "$description",
                            "amount": $amount,
                            "unit": "$unit"
                        },
                        links: {
                            iconURL: "https://katydid-web-client.s3.us-east-2.amazonaws.com/icons/meal-icon.svg"
                        },
                          relationships: {
                            "child": {
                                "id": "${childId.value}",
                                "type": "child"
                            }
                        }
                    },
                                        {
                        "id": "${thisChildsNapLogEntry.id.value}",
                        "type": "nap",
                        "attributes": {
                            "childId": "${childId.value}",
                            "time": $time,
                            "duration": $duration
                        },
                        links: {
                            iconURL: "https://katydid-web-client.s3.us-east-2.amazonaws.com/icons/nap-icon.svg"
                        },
                         relationships: {
                            "child": {
                                "id": "${childId.value}",
                                "type": "child"
                            }
                        }
                    }
                ],
                 "included": [
                    {
                        "id": "${childId.value}",
                        "type": "child",
                        "attributes": {
                            "name": "$childName",
                            "portraitURL": "https://katydid-web-client.s3.us-east-2.amazonaws.com/img/profile/${childId.value}.png",
                            "isPresent": $isPresent
                        }
                    }
                ]
            }
        """
    }
}
