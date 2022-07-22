package com.example.whac_a_mole.presentation.game_field.model

import com.example.whac_a_mole.GameField

data class GameFieldState(
    val gameField: GameField = GameField(),
    val playerScore: Int = 0,
    val currentTime: Long = 0
)