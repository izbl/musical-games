package com.example.musicalgames
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.musicalgames.controllers.BirdController
import com.example.musicalgames.controllers.GameController
import com.example.musicalgames.models.PitchRecogniser
import com.example.musicalgames.viewmodels.FlappyViewModel
import com.example.musicalgames.viewmodels.MultiplayerViewModel
import com.example.musicalgames.views.FloppyGameView
import com.example.musicalgames.views.GameEndListener

class FlappyGameFragment : Fragment(), GameEndListener {
    private lateinit var gameView: FloppyGameView
    private lateinit var gameController: GameController
    private lateinit var pitchRecogniser: PitchRecogniser

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_flappy, container, false)

        pitchRecogniser = PitchRecogniser(requireContext())
        val birdController = BirdController(pitchRecogniser)

        gameView = rootView.findViewById(R.id.gameView)
        gameView.setEndListener(this)
        gameController = GameController(gameView, birdController)

        val startGameButton = rootView.findViewById<Button>(R.id.startGameButton)
        startGameButton.setOnClickListener {
            if (checkPermissions()) {
                gameController.startGame()
                pitchRecogniser.start()
                startGameButton.visibility = View.GONE
            } else {
                requestMultiplePermissions.launch(arrayOf(Manifest.permission.RECORD_AUDIO))
            }
        }

        if (!checkPermissions())
            requestMultiplePermissions.launch(arrayOf(Manifest.permission.RECORD_AUDIO))

        return rootView
    }

    private fun checkPermissions(): Boolean {
        val permissionResult = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.RECORD_AUDIO
        )
        return permissionResult == PackageManager.PERMISSION_GRANTED
    }

    private val requestMultiplePermissions =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permission ->
            // Handle permission request result if needed
        }

    override fun onEndGame() {
        val viewModel = ViewModelProvider(requireActivity()).get(FlappyViewModel::class.java)
        viewModel.score= gameView.getScore()
        gameController.stopGame()
        findNavController().navigate(R.id.gameEndedFragment)
    }

    override fun onDestroy() {
        super.onDestroy()
        pitchRecogniser.release()
        gameController.stopGame()
    }
}
