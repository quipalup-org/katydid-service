package com.quipalup.katydid.child.search.application

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.right
import com.quipalup.katydid.child.common.domain.Child
import com.quipalup.katydid.child.common.domain.ChildRepository
import com.quipalup.katydid.child.search.domain.FindChildByIdError
import com.quipalup.katydid.common.id.Id
import java.util.UUID
import javax.inject.Named

@Named
class FindChildByIdQueryHandler(private val childRepository: ChildRepository) {
    fun execute(query: FindChildByIdQuery): Either<FindChildByIdError, ChildResult> =
        query.toId()
            .flatMap { childRepository.findById(it) }
            .flatMap { it.toResult() }

    private fun FindChildByIdQuery.toId(): Either<FindChildByIdError, Id> = Id(UUID.fromString(this.id)).right()

    private fun Child.toResult(): Either<FindChildByIdError, ChildResult> =
        ChildResult(
            id = id.value.toString(),
            name = name.value,
            portraitURL = portraitURL.value.toString(),
            isPresent = isPresent.value
        ).right()
}

data class FindChildByIdQuery(val id: String)

data class ChildResult(
    val id: String,
    val name: String,
    val portraitURL: String,
    val isPresent: Boolean
)
