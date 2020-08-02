package io.github.gianpamx.pdd.domain.api

import io.github.gianpamx.pdd.domain.entity.ZenMode

interface ZenModeApi {
    var mode: ZenMode

    class AccessDeniedException(e: SecurityException) : SecurityException(e) {
        override fun equals(other: Any?) = other is AccessDeniedException
        override fun hashCode() = "AccessDeniedException".hashCode()
    }
}
