package com.example.whac_a_mole.presentation.main_menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whac_a_mole.data.local.PlayerScoreStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainMenuViewModel @Inject constructor(
    playerScoreStorage: PlayerScoreStorage
) : ViewModel() {

    val bestScore = playerScoreStorage.getUserScore()

    private val _uiChannel = Channel<MenuUiEffect>()
    val menuResultEffect = _uiChannel.receiveAsFlow()

    fun startGameButtonClicked() {
        viewModelScope.launch {
            _uiChannel.send(MenuUiEffect.NavigateToGameField)
        }
    }

    sealed class MenuUiEffect {
        object NavigateToGameField : MenuUiEffect()
    }
}