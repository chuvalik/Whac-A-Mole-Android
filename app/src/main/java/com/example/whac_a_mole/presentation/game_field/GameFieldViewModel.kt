package com.example.whac_a_mole.presentation.game_field

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whac_a_mole.presentation.game_field.model.GameFieldState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random


@HiltViewModel
class GameFieldViewModel @Inject constructor() : ViewModel() {

    private val _gameState = MutableLiveData<GameFieldState>(GameFieldState())
    val gameState: LiveData<GameFieldState> get() = _gameState

    private var currentPlayerScore = 0

    private val _uiChannel = Channel<GameUiEffect>()
    val gameUiEffect get() = _uiChannel.receiveAsFlow()

    init {
        initGame()
    }

    private fun initGame() {
        object : CountDownTimer(GAME_TIME, GAME_TIME_INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {
                processGameTick(millisUntilFinished)
            }

            override fun onFinish() {
                navigateToGameResult()
            }
        }.start()
    }

    private fun processGameTick(millisUntilFinished: Long) {
        setTimeUntilGameFinished(millisUntilFinished)
        setNewMolePosition()
    }

    private fun setNewMolePosition() {
        val randomRow = Random.nextInt(until = _gameState.value?.gameField?.rows!!)
        val randomColumn = Random.nextInt(until = _gameState.value?.gameField?.columns!!)
        _gameState.value?.gameField?.setMolePosition(randomRow, randomColumn)
    }

    private fun setTimeUntilGameFinished(millisUntilFinished: Long) {
        _gameState.value = _gameState.value?.copy(currentTime = millisUntilFinished / 1000)
    }

    fun clickedOnTheMole() {
        currentPlayerScore += 1
        _gameState.value= _gameState.value?.copy(playerScore = currentPlayerScore)
    }

    private fun navigateToGameResult() {
        viewModelScope.launch {
            _uiChannel.send(GameUiEffect.NavigateToGameResult(_gameState.value?.playerScore!!))
        }
    }

    sealed class GameUiEffect {
        data class NavigateToGameResult(val score: Int) : GameUiEffect()
    }

    private companion object {
        const val GAME_TIME = 30_000L
        const val GAME_TIME_INTERVAL = 500L
    }
}