package com.quipalup.katydid.child.search.domain

sealed class SearchChildrenError {
    object Unknown : SearchChildrenError()
}

sealed class FindChildByIdError {
    object DoesNotExist : FindChildByIdError()
}
