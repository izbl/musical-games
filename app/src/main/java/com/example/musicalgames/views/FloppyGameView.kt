package com.example.musicalgames.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.example.musicalgames.models.Bird
import com.example.musicalgames.models.Pipe
import kotlin.random.Random
interface GameEndListener {
    fun onEndGame()
}

class FloppyGameView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val pipes = mutableListOf<Pipe>()
    val bird: Bird = Bird()
    private val paint = Paint()
    private var targetY: Float?=null
    private var endListener: GameEndListener? =null
    fun setEndListener(listener: GameEndListener) {
        endListener=listener
    }

    companion object {
        const val PIPE_GAP = 200
        const val PIPE_DISTANCE = 400
    }
    fun getRandomPipe(lastPipeX: Int): Pipe {
        return Pipe(
            lastPipeX + PIPE_DISTANCE,
            generateRandomHeight(),
            PIPE_GAP
        )
    }

    fun addPipes() {
        viewTreeObserver.addOnGlobalLayoutListener {
            pipes.add(getRandomPipe(screenWidth))
        }
    }
    fun toast(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.color = Color.RED
        canvas.drawCircle(bird.x, bird.y, bird.radius, paint)

        paint.color = Color.GREEN

        for (pipe in pipes) {
            canvas.drawRect(pipe.x.toFloat(), 0f, (pipe.x + Pipe.WIDTH).toFloat(), pipe.topHeight.toFloat(), paint)
            canvas.drawRect(pipe.x.toFloat(), pipe.bottomY.toFloat(), (pipe.x + Pipe.WIDTH).toFloat(), height.toFloat(), paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action) {
            MotionEvent.ACTION_DOWN -> {
                targetY = event.y
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    fun update() {

        for (pipe in pipes) {
            pipe.move()
            val birdRect = RectF(
                bird.x - bird.radius,
                bird.y - bird.radius,
                bird.x + bird.radius,
                bird.y+bird.radius
            )
            val topPipeRect = RectF(
                pipe.x.toFloat(),
                0f,
                pipe.x.toFloat() + Pipe.WIDTH,
                pipe.topHeight.toFloat()
            )
            val bottomPipeRect = RectF(
                pipe.x.toFloat(),
                pipe.bottomY.toFloat(),
                pipe.x.toFloat() + Pipe.WIDTH,
                height.toFloat()  // Use the height of the GameView as the bottom boundary
            )
            if(birdRect.intersect(topPipeRect) || birdRect.intersect(bottomPipeRect)) {
                endListener?.onEndGame()
            }
        }
        pipes.removeAll { it.x+Pipe.WIDTH<0 }
        if(pipes.size!=0 && pipes.last().isVisible(width))
            pipes.add(getRandomPipe(pipes.last().x))
        invalidate()

    }

    private fun generateRandomHeight(): Int {
        return Random.nextInt(200, height - PIPE_GAP - 200)
    }

    private val screenWidth: Int
        get() = resources.displayMetrics.widthPixels
}