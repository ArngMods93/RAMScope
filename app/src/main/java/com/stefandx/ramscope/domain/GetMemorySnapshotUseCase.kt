package com.stefandx.ramscope.domain

import com.stefandx.ramscope.data.MemoryInfoProvider
import com.stefandx.ramscope.model.MemoryDetails

/**
 * Thin use-case wrapper around [MemoryInfoProvider]. Keeping this
 * indirection means the UI layer never touches Android memory APIs
 * directly, which makes the ViewModels easier to test and keeps
 * responsibilities separated as the app grows.
 */
class GetMemorySnapshotUseCase(private val provider: MemoryInfoProvider) {
    operator fun invoke(): MemoryDetails = provider.getMemoryDetails()
}
