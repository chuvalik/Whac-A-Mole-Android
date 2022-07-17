package com.example.whac_a_mole

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GameApplication : Application() {

    override fun onCreate() {
        super.onCreate()

    }
}