package com.example.musicalgames.games.mental_intervals

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.musicalgames.components.interval_palette.IntervalPaletteListener
import com.example.musicalgames.components.interval_palette.IntervalPaletteView
import com.example.musicalgames.components.key_palette.KeyPaletteListener
import com.example.musicalgames.components.key_palette.KeyPaletteView
import com.example.musicalgames.game.games.mental_intervals.MentalViewmodelListener
import com.example.musicalgames.utils.ChromaticNote
import com.example.musicalgames.utils.Interval
import kotlin.math.roundToInt

class MentalView(context: Context) : ViewGroup(context), MentalViewmodelListener {

    private var viewModel: MentalViewModel? = null
    private val notePaletteRatio :Float = 2f/3f
    private val keyPaletteView: KeyPaletteView
    private val intervalPaletteView: IntervalPaletteView
    private val messageTextView: TextView
    init {
        keyPaletteView = KeyPaletteView(context)
        intervalPaletteView = IntervalPaletteView(context)
        messageTextView = TextView(context).apply {
            textSize = 20f
            gravity = Gravity.CENTER_HORIZONTAL
            text = ""
        }

        addView(messageTextView)
        addView(keyPaletteView)
        addView(intervalPaletteView)
    }
    fun setViewModel(viewModel: MentalViewModel) {
        this.viewModel =viewModel
        redraw()
    }
    fun setKeyboardListener(listener: KeyPaletteListener) {
        keyPaletteView.registerListener(listener)
    }
    fun setIntervalListener(listener: IntervalPaletteListener) {
        intervalPaletteView.registerListener(listener)
    }

    private fun redraw() {
        if(viewModel!!.type == Type.INTERVAL_NOTE || viewModel!!.type == Type.DEGREE_NOTE)
            intervalPaletteView.visibility = View.GONE
        else
            keyPaletteView.visibility = View.GONE
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

        val messageHeight = (height * 1) / 10
        messageTextView.measure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(messageHeight, MeasureSpec.EXACTLY)
        )

        val paletteHeight :Int = (notePaletteRatio * height.toFloat()).roundToInt()

        keyPaletteView.measure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(paletteHeight, MeasureSpec.EXACTLY)
        )

        intervalPaletteView.measure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(paletteHeight, MeasureSpec.EXACTLY)
        )
        setMeasuredDimension(width, height)
    }

    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        val keyPaletteHeight = (height.toFloat() * notePaletteRatio).roundToInt()
        val messageHeight = (height * 1) / 10

        messageTextView.layout(0, 0, width, messageHeight)
        keyPaletteView.layout(0, height - keyPaletteHeight, width, height)
        intervalPaletteView.layout(0, height - keyPaletteHeight, width, height)
    }

    override fun onNewProblem(interval: Interval, questionNote: ChromaticNote) {
        messageTextView.text = "What is the note positioned at $interval from $questionNote?"
    }

    override fun onNewProblem(questionNote: ChromaticNote, note: ChromaticNote) {
       messageTextView.text = "What is the interval between $questionNote and $note"
    }

    override fun onRightAnswer() {
        messageTextView.text = "Good!"
    }

    override fun onWrongAnswer(correctAns: Interval) {
        messageTextView.text = "The right answer is : ${correctAns.name}"
    }

    override fun onWrongAnswer(correctAns: ChromaticNote) {
        messageTextView.text = "The right answer is : $correctAns"
    }
}
