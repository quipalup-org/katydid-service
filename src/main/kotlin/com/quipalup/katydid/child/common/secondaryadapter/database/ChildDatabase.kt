package com.quipalup.katydid.child.common.secondaryadapter.database

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.quipalup.katydid.child.common.domain.Child
import com.quipalup.katydid.child.common.domain.ChildRepository
import com.quipalup.katydid.child.common.secondaryadapter.database.StaticYoungHumans.BLANCA
import com.quipalup.katydid.child.common.secondaryadapter.database.StaticYoungHumans.CRISTINA
import com.quipalup.katydid.child.common.secondaryadapter.database.StaticYoungHumans.DAVID
import com.quipalup.katydid.child.common.secondaryadapter.database.StaticYoungHumans.JOHN
import com.quipalup.katydid.child.common.secondaryadapter.database.StaticYoungHumans.MARIA
import com.quipalup.katydid.child.common.secondaryadapter.database.StaticYoungHumans.MONICA
import com.quipalup.katydid.child.common.secondaryadapter.database.StaticYoungHumans.VICTOR
import com.quipalup.katydid.child.search.domain.ChildField
import com.quipalup.katydid.child.search.domain.FindChildByIdError
import com.quipalup.katydid.child.search.domain.SearchChildrenError
import com.quipalup.katydid.common.genericsearch.Filter
import com.quipalup.katydid.common.genericsearch.PageResult
import com.quipalup.katydid.common.genericsearch.SearchOperation
import com.quipalup.katydid.common.genericsearch.SearchRequest
import com.quipalup.katydid.common.id.Id
import java.net.URL
import java.util.UUID
import javax.inject.Named

@Named
class ChildDatabase : ChildRepository {
    override fun search(searchRequest: SearchRequest<ChildField>): Either<SearchChildrenError, PageResult<Child>> {

        if (searchRequest.filters.any { filter: Filter<ChildField> -> filter.operation is SearchOperation.UnarySearchOperation.IsTrue && filter.field == ChildField.IS_PRESENT })
            return listOf(BLANCA, CRISTINA, VICTOR, MONICA, DAVID).let { PageResult(it.size.toLong(), it) }.right()

        if (searchRequest.filters.any { filter: Filter<ChildField> -> filter.operation is SearchOperation.UnarySearchOperation.IsFalse && filter.field == ChildField.IS_PRESENT })
            return listOf(JOHN, MARIA).let { PageResult(it.size.toLong(), it) }.right()

        return SearchChildrenError.Unknown.left()
    }

    override fun findById(id: Id): Either<FindChildByIdError, Child> = id.value.let { uuid ->
        childrenList.filter {
            it.id.value == uuid
        }.let {
            when {
                it.isEmpty() -> FindChildByIdError.DoesNotExist.left()
                else -> it.first().right()
            }
        }
    }
}

private object StaticYoungHumans {
    private fun sample(
        id: Id = Id(),
        name: Child.Name = Child.Name("sample"),
        portraitURL: Child.PortraitURL = Child.PortraitURL(URL("http://sample.com")),
        isPresent: Child.IsPresent = Child.IsPresent(true)
    ): Child = Child(
        id = id,
        name = name,
        portraitURL = portraitURL,
        isPresent = isPresent
    )

    private fun present(): Child = sample(isPresent = Child.IsPresent(true))
    private fun notPresent(): Child = sample(isPresent = Child.IsPresent(false))

    val BLANCA: Child = present().copy(
        id = Id(UUID.fromString("5ee62461-adb8-4618-a110-06290a787223")),
        name = Child.Name("Blanca")
    ).let { child ->
        child.attachPortraitURL()
    }

    val CRISTINA: Child = present().copy(
        id = Id(UUID.fromString("86a93463-e7e1-4fc0-b12c-981f1eea16e8")),
        name = Child.Name("Cristina")
    ).let { child ->
        child.attachPortraitURL()
    }

    val VICTOR: Child = present().copy(
        id = Id(UUID.fromString("b9c2380f-0b4c-4871-aefc-a6ed2c3a2408")),
        name = Child.Name("Victor")
    ).let { child ->
        child.attachPortraitURL()
    }

    val MONICA: Child = present().copy(
        id = Id(UUID.fromString("a5edf2fa-30b1-45e4-a39b-96243fa60caa")),
        name = Child.Name("Monica")
    ).let { child ->
        child.attachPortraitURL()
    }

    val DAVID: Child = present().copy(
        id = Id(UUID.fromString("666cf327-09da-46ad-a01c-d3ae6e8ebc9d")),
        name = Child.Name("David")
    ).let { child ->
        child.attachPortraitURL()
    }

    val JOHN: Child = notPresent().copy(
        id = Id(UUID.fromString("bb7e288d-5c6a-43c5-83a1-551491a72002")),
        name = Child.Name("John")
    ).let { child ->
        child.attachPortraitURL()
    }

    val MARIA: Child = notPresent().copy(
        id = Id(UUID.fromString("2635d7f4-4761-410f-af6c-fd77a0f338cb")),
        name = Child.Name("Maria")
    ).let { child ->
        child.attachPortraitURL()
    }

    private fun Child.attachPortraitURL() = this.copy(
        portraitURL = Child.PortraitURL(
            URL(
                "https://katydid-web-client.s3.us-east-2.amazonaws.com/img/profile/${this.id.asString()}.png"
            )
        )
    )
}

val childrenList = listOf<Child>(BLANCA, CRISTINA, DAVID, MARIA, MONICA, JOHN, VICTOR)
