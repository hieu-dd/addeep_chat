package net.itanchi.addeep.android.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import net.itanchi.addeep.android.ui.navigation.NavigationManager
import net.itanchi.addeep.core.data.AppDataManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class BaseViewModel : ViewModel(), KoinComponent {
    protected val dataManager: AppDataManager by inject()
    protected val navigationManager: NavigationManager by inject()
    protected val scope: CoroutineScope = viewModelScope
}