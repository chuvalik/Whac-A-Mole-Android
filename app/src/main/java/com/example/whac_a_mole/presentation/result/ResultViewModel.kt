package com.example.whac_a_mole.presentation.result

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whac_a_mole.data.local.PlayerScoreStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val playerScoreStorage: PlayerScoreStorage,
    state: SavedStateHandle
): ViewModel() {

    val bestScore get() = playerScoreStorage.getUserScore()
    val playerScore = state.get<Int>(KEY_PLAYER_SCORE) ?: 0

    private val _uiChannel = Channel<ResultUiEffect>()
    val resultUiEffect get() = _uiChannel.receiveAsFlow()

    init {
        processPlayerScore()
    }

    fun playAgainButtonPressed() {
        viewModelScope.launch {
            _uiChannel.send(ResultUiEffect.NavigateToGameField)
        }
    }

    private fun processPlayerScore() {
        if (playerScore > playerScoreStorage.getUserScore()) {
            playerScoreStorage.saveUserScore(playerScore)
        }
    }

    fun menuButtonPressed() {
        viewModelScope.launch {
            _uiChannel.send(ResultUiEffect.NavigateToMainMenu)
        }
    }

    sealed class ResultUiEffect {
        object NavigateToGameField : ResultUiEffect()
        object NavigateToMainMenu : ResultUiEffect()
    }

    private companion object {
        const val KEY_PLAYER_SCORE = "playerScore"
    }
}