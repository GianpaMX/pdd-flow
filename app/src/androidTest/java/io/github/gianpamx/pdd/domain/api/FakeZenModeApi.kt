package io.github.gianpamx.pdd.domain.api

import io.github.gianpamx.pdd.domain.entity.ZenMode

class FakeZenModeApi : ZenModeApi {
    override var mode: ZenMode = ZenMode.Off
}
