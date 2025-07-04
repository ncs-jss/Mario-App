package com.ncs.marioapp.Domain.HelperClasses

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.ncs.marioapp.R

interface IRevealListener {
    fun onRevealed(scratchView: ScratchView)
    fun onRevealPercentChangedListener(scratchView: ScratchView, percent: Float)
}

class ScratchView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private var path = Path()
    private val paint = Paint().apply {
        isAntiAlias = true
        color = Color.TRANSPARENT
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 60f
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    private lateinit var overlayBitmap: Bitmap
    private lateinit var overlayCanvas: Canvas
    private var overlayDrawable: Bitmap? = null

    private var listener: IRevealListener? = null
    private var revealed = false

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.ScratchView, 0, 0).apply {
            try {
                val drawableId = getResourceId(R.styleable.ScratchView_overlay_image, -1)
                if (drawableId != -1) {
                    overlayDrawable = BitmapFactory.decodeResource(resources, drawableId)
                }
            } finally {
                recycle()
            }
        }
    }

    fun setStrokeWidth(width: Int) {
        paint.strokeWidth = width.toFloat()
    }

    fun setRevealListener(listener: IRevealListener) {
        this.listener = listener
    }

    fun reveal() {
        if (!revealed) {
            revealed = true
            listener?.onRevealed(this)
            invalidate()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        overlayBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        overlayCanvas = Canvas(overlayBitmap)
        if (overlayDrawable != null) {
            val scaled = Bitmap.createScaledBitmap(overlayDrawable!!, w, h, true)
            overlayCanvas.drawBitmap(scaled, 0f, 0f, null)
        } else {
            overlayCanvas.drawColor(Color.GRAY)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(overlayBitmap, 0f, 0f, null)
        canvas.drawPath(path, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (revealed) return false

        when (event.action) {
            MotionEvent.ACTION_DOWN -> path.moveTo(event.x, event.y)
            MotionEvent.ACTION_MOVE -> {
                path.lineTo(event.x, event.y)
                drawPath()
                invalidate()
                listener?.onRevealPercentChangedListener(this, getScratchedPercentage())
            }
            MotionEvent.ACTION_UP -> drawPath()
        }
        return true
    }

    private fun drawPath() {
        overlayCanvas.drawPath(path, paint)
    }

    private fun getScratchedPercentage(): Float {
        val totalPixels = overlayBitmap.width * overlayBitmap.height
        val pixels = IntArray(totalPixels)
        overlayBitmap.getPixels(pixels, 0, overlayBitmap.width, 0, 0, overlayBitmap.width, overlayBitmap.height)
        val scratched = pixels.count { it == 0 } // Transparent
        return scratched.toFloat() / totalPixels
    }
}
