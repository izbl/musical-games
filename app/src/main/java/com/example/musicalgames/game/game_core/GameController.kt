package com.example.musicalgames.game_activity

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel

interface GameListener {
    fun onGameEnded()
}

interface GameController {
    fun setViewModel(viewModel: ViewModel)
    fun initGame(context: Context, listener: GameListener)
    fun startGame(owner: LifecycleOwner)
    fun pauseGame()
    fun endGame()
    fun getScore(): Int
    fun getEndDescription(): String
}