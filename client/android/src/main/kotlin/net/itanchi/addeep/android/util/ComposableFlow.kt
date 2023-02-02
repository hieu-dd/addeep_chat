package net.itanchi.addeep.android.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.flow.Flow

@Composable
fun <T> FlowAsState(
    lifecycleOwner: LifecycleOwner,
    flow: Flow<T>,
    initial: T,
): T {
    val flowWithLifecycle = remember(flow, lifecycleOwner) {
        flow.flowWithLifecycle(lifecycleOwner.lifecycle)
    }
    val state by flowWithLifecycle.collectAsState(initial)
    return state
}