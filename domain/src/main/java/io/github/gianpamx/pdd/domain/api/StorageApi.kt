package io.github.gianpamx.pdd.domain.api

import io.github.gianpamx.pdd.domain.entity.ZenMode

interface StorageApi {
    var originalMode: ZenMode?
}
