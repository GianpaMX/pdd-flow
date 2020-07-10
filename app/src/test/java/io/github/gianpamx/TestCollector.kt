package io.github.gianpamx

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch

@InternalCoroutinesApi
class TestCollector<T> {
    val values = mutableListOf<T>()

    fun test(scope: CoroutineScope, flow: Flow<T>): Job {
        return scope.launch {
            flow.collect(object : FlowCollector<T> {
                override suspend fun emit(value: T) {
                    values.add(value)
                }
            })
        }
    }
}
