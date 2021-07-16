package com.quipalup.katydid.child

import arrow.core.Either
import arrow.core.right
import com.quipalup.katydid.child.ChildMother.BLANCA
import com.quipalup.katydid.child.common.domain.Child
import com.quipalup.katydid.child.common.domain.ChildRepository
import com.quipalup.katydid.child.search.application.ChildResult
import com.quipalup.katydid.child.search.application.FindChildByIdQuery
import com.quipalup.katydid.child.search.application.FindChildByIdQueryHandler
import com.quipalup.katydid.child.search.domain.FindChildByIdError
import com.quipalup.katydid.common.id.Id
import io.mockk.every
import io.mockk.mockk
import java.net.URL
import java.util.UUID
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class FindChildByIdQueryHandlerTest {
    @Test
    fun `find child by id`() {

        val childRepository = mockk<ChildRepository>()

        `will return child for the given id`(childRepository, Id(UUID.fromString(id)), child)
        FindChildByIdQueryHandler(childRepository).execute(findChildByIdQuery).fold(
            { Assertions.fail(it.toString()) },
            { assertThat(it).isEqualTo(expectedResult) }
        )
    }

    private fun `will return child for the given id`(
        repository: ChildRepository,
        id: Id,
        child: Child
    ) {
        every {
            repository.findById(id)
        } returns child.toStubResult()
    }

    companion object {

        private val id = "5ee62461-adb8-4618-a110-06290a787223"

        private val name = Child.Name("Blanca")
        private val portraitURL =
            Child.PortraitURL(URL("https://katydid-web-client.s3.us-east-2.amazonaws.com/img/profile/$id.png"))

        private val childId = Id(UUID.fromString(id))
        private val isPresent = Child.IsPresent(true)
        private val findChildByIdQuery = FindChildByIdQuery(
            id = id
        )

        private fun Child.toStubResult(): Either<FindChildByIdError, Child> =
            Child(
                id = id,
                name = name,
                portraitURL = portraitURL,
                isPresent = isPresent
            ).right()

        private fun Child.toResult(): ChildResult =
            ChildResult(
                id = id.value.toString(),
                name = name.value,
                portraitURL = portraitURL.value.toString(),
                isPresent = isPresent.value
            )

        private val expectedResult: ChildResult = BLANCA.toResult()

        val child = Child(
            id = childId,
            name = name,
            portraitURL = portraitURL,
            isPresent = isPresent
        )
    }
}
