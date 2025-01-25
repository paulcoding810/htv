package com.paulcoding.htv

import android.content.Context
import androidx.lifecycle.ViewModel
import com.paulcoding.htv.utils.readFile
import com.paulcoding.htv.utils.readJSONFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class HTVViewModel : ViewModel() {
    private var _stateFlow = MutableStateFlow(UiState())
    val stateFlow = _stateFlow.asStateFlow()

    data class UiState(
        val config: Config = Config(),
        val adsBlackList: List<String> = emptyList(),
        val selectedSite: Site? = null
    )

    fun readConfigs(context: Context) {
        try {
            val config =
                context.readJSONFile<Config>("$DIR/config.json")
            val adsBlackList = config.blackList.let {
                context.readFile("$DIR/$it").split("\n")
            }.filter(String::isNotEmpty)
            _stateFlow.update {
                UiState(config = config, adsBlackList = adsBlackList)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setSite(site: Site) {
        _stateFlow.update {
            it.copy(selectedSite = site)
        }
    }
}