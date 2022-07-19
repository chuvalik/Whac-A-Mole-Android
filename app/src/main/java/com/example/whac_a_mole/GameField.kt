package com.example.whac_a_mole

import com.example.whac_a_mole.presentation.game_field.model.Cell

typealias OnFieldChangedListener = (field: GameField) -> Unit

class GameField {

    val rows: Int = 3
    val columns: Int = 3

    private var currentMolePositionRow = -1
    private var currentMolePositionColumn = -1

    private val field = Array(rows) { Array(columns) { Cell.EMPTY } }

    val listener = mutableSetOf<OnFieldChangedListener>()

    fun setMolePosition(row: Int, column: Int) {

        setEmptyCell()
        field[row][column] = Cell.MOLE
        updateCurrentMolePosition(row, column)

        listener.forEach { it.invoke(this) }
    }

    private fun setEmptyCell() {
        if (currentMolePositionRow == -1 || currentMolePositionColumn == -1) return

        field[currentMolePositionRow][currentMolePositionColumn] = Cell.EMPTY
    }

    private fun updateCurrentMolePosition(row: Int, column: Int) {
        currentMolePositionRow = row
        currentMolePositionColumn = column
    }

    fun getCell(row: Int, column: Int): Cell {
        return field[row][column]
    }


}