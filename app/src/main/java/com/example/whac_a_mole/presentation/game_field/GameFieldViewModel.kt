package com.example.whac_a_mole.presentation.game_field

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.*
import com.example.whac_a_mole.presentation.game_field.model.Cell
import com.example.whac_a_mole.presentation.game_field.model.GameFieldState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class GameFieldViewModel @Inject constructor() : ViewModel() {

    private val gameField = MutableList(GAME_FIELD_SIZE) { Cell.EMPTY }
    private var currentPlayerScore = 0

    private val _gameState = MutableLiveData(GameFieldState())
    val gameState: LiveData<GameFieldState> get() = _gameState

    private val _uiChannel = Channel<GameUiEffect>()
    val gameUiEffect get() = _uiChannel.receiveAsFlow()

    init {
        initGameState()
        initTimer()
    }

    private fun initTimer() {
        object : CountDownTimer(GAME_TIME, GAME_TIME_INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {
                processGameTick(millisUntilFinished)
            }

            override fun onFinish() {
                navigateToGameResult()
            }
        }.start()
    }

    private fun initGameState() {
        _gameState.value = _gameState.value?.copy(
            gameField = gameField,
            playerScore = currentPlayerScore
        )
    }

    private fun navigateToGameResult() {
        viewModelScope.launch {
            _uiChannel.send(GameUiEffect.NavigateToGameResult(_gameState.value?.playerScore!!))
        }
    }

    fun clickedOnTheMole() {
        currentPlayerScore += PLAYER_SCORE_PER_CLICK
        _gameState.value = _gameState.value?.copy(playerScore = currentPlayerScore)
    }

    private fun processGameTick(millisUntilFinished: Long) {
        setTimeUntilGameFinished(millisUntilFinished)
        setEmptyCellToPreviousMolePosition()
        setNewMolePosition()
    }

    private fun setTimeUntilGameFinished(millisUntilFinished: Long) {
        _gameState.value = _gameState.value?.copy(currentTime = millisUntilFinished / 1000)
    }

    private fun setNewMolePosition() {
        val randomIndex = Random.nextInt(until = GAME_FIELD_SIZE)
        gameField[randomIndex] = Cell.MOLE
        _gameState.value = _gameState.value?.copy(gameField = gameField)
    }

    private fun setEmptyCellToPreviousMolePosition() {
        val moleIndex = gameField.indexOf(Cell.MOLE)
        if (moleIndex >= 0) gameField[moleIndex] = Cell.EMPTY
        _gameState.value = _gameState.value?.copy(gameField = gameField)
    }

    sealed class GameUiEffect {
        data class NavigateToGameResult(val score: Int) : GameUiEffect()
    }

    private companion object {
        const val GAME_TIME = 30_000L
        const val GAME_TIME_INTERVAL = 500L

        const val GAME_FIELD_SIZE = 9

        const val PLAYER_SCORE_PER_CLICK = 1
    }
}