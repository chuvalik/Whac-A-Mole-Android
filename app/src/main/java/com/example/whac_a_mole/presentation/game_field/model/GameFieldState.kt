package com.example.whac_a_mole.presentation.game_field.model

data class GameFieldState(
    val gameField: List<Cell> = emptyList(),
    val playerScore: Int = 0,
    val currentTime: Long = 0
)