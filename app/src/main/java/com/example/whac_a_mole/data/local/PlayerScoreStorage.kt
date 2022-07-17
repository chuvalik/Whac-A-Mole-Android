package com.example.whac_a_mole.data.local

interface PlayerScoreStorage {

    fun saveUserScore(score: Int)

    fun getUserScore(): Int
}