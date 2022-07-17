package com.example.whac_a_mole.di

import android.content.Context
import com.example.whac_a_mole.data.local.PlayerScoreStorage
import com.example.whac_a_mole.data.local.PlayerScoreStorageImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Singleton
    @Provides
    fun providePlayerScoreStorage(@ApplicationContext context: Context): PlayerScoreStorage {
        return PlayerScoreStorageImpl(context)
    }
}