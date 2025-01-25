package com.vocabri.data.test

import androidx.annotation.VisibleForTesting
import com.vocabri.utils.IdGenerator

class FakeIdGenerator : IdGenerator {
    @VisibleForTesting
    var id = "1234"

    override fun generateStringId(): String = id
}
