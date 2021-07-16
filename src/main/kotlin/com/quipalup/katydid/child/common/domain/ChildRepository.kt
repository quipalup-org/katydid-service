package com.quipalup.katydid.child.common.domain

import arrow.core.Either
import com.quipalup.katydid.child.search.domain.ChildField
import com.quipalup.katydid.child.search.domain.FindChildByIdError
import com.quipalup.katydid.child.search.domain.SearchChildrenError
import com.quipalup.katydid.common.genericsearch.PageResult
import com.quipalup.katydid.common.genericsearch.SearchRequest
import com.quipalup.katydid.common.id.Id

interface ChildRepository {
    fun search(searchRequest: SearchRequest<ChildField>): Either<SearchChildrenError, PageResult<Child>>
    fun findById(id: Id): Either<FindChildByIdError, Child>
}
