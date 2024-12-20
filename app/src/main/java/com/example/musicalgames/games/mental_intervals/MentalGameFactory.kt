package com.example.musicalgames.games.mental_intervals

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.musicalgames.game_activity.GameController
import com.example.musicalgames.game_activity.GameIntentMaker
import com.example.musicalgames.game_activity.GameListener
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.games.CustomGameCreator
import com.example.musicalgames.games.GameDatabase
import com.example.musicalgames.games.GameFactory
import com.example.musicalgames.games.GamePackage
import com.example.musicalgames.games.flappy.FlappyLevelDao

class MentalGameFactory : GameFactory {

    override suspend fun getLevels(pack: GamePackage, context: Context): List<Level> {
        val mentalLevelDao: MentalLevelDao = GameDatabase.getDatabase(context).mentalLevelDao()

        return when (pack) {
            GamePackage.PREDEFINED -> mentalLevelDao.getBaseLevels()
            GamePackage.CUSTOM -> mentalLevelDao.getCustomLevels()
            else -> mentalLevelDao.getFavouriteLevels()
        }
    }

    override fun getPermissions(): Array<String> {
        return arrayOf()
    }

    override fun getViewModelType(): Class<out ViewModel> {
        return MentalViewModel::class.java
    }

    override fun getIntentMaker(): GameIntentMaker {
       return MentalViewModel.Companion
    }

    override fun getCustomCreator(context: Context, attrs: AttributeSet): CustomGameCreator {
        TODO("Not yet implemented")
    }

    override fun createGame(
        context: Context,
        activity: FragmentActivity,
        gameContainer: ViewGroup,
        gameListener: GameListener
    ): GameController {
        val viewModel = ViewModelProvider(activity)[MentalViewModel::class.java]
        val gameView = MentalView(context)
        gameView.setViewModel(viewModel)
        gameContainer.addView(gameView)

        val gameController = MentalController(gameView)
        gameController.setViewModel(viewModel)
        gameController.initGame(context, gameListener)
        return gameController
    }
}