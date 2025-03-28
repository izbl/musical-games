package com.example.musicalgames.games.play_by_ear

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.example.musicalgames.game_activity.GameController
import com.example.musicalgames.game_activity.GameListener

class EarController(private val view: EarView) : GameController {
    private var viewModel: EarViewModel? = null
    override fun setViewModel(viewModel: ViewModel) {
        view.setViewModel(viewModel as EarViewModel)
        this.viewModel = viewModel
    }

    override fun initGame(context: Context, listener: GameListener) {
        view.registerEndListener(listener)
    }

    override fun startGame(owner: LifecycleOwner) {
        viewModel!!.playRoot()
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            viewModel!!.newProblem()
        },2000)
    }

    override fun pauseGame() {
        //TODO("Not yet implemented")
    }

    override fun endGame() {
        //TODO("Not yet implemented")
    }

    override fun getScore(): Int {
        return view.getScore()
    }

    override fun getEndDescription(): String {
        return "The correct note was ${viewModel!!.getCorrectNote()}"
    }
}