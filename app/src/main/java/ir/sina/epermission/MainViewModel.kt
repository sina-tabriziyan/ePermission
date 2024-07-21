package ir.sina.epermission

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel() {
    private val _showDialog = MutableStateFlow(false)
    val showDialog = _showDialog.asStateFlow()

    private val _launchAppSetting = MutableStateFlow(false)
    val launchAppSetting = _launchAppSetting.asStateFlow()


    fun updateShowDialog(show: Boolean) = _showDialog.update { show }

    fun updateLaunchAppSetting(launch: Boolean) = _launchAppSetting.update { launch }
}