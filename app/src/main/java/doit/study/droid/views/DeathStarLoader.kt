package doit.study.droid.views

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import doit.study.droid.R
import kotlin.math.roundToInt
import kotlin.math.sin

class DeathStarLoader @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var amplitudeRatio: Float = 0f
    private var waveColor: Int = 0

    private var waterLevelRatio = 1f
    private var waveShiftRatio = DEFAULT_WAVE_SHIFT_RATIO
    private var defaultWaterLevel: Float = 0f

    private val paint = Paint()
    private val wavePaint = Paint()
    private var waveShader: BitmapShader? = null
    private val waveShaderMatrix = Matrix()

    private var starPaint = Paint()
    private var starPath = Path()

    // Animation
    private var animatorSetWave: AnimatorSet? = null

    init {
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        paint.isAntiAlias = true

        // Init Wave
        wavePaint.isAntiAlias = true

        // Init Star
        starPaint.apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            color = Color.RED
            strokeWidth = 20f
            strokeCap = Paint.Cap.ROUND
        }

        initAnimation()

        // Load the styled attributes and set their properties
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.DeathStarLoader, defStyleAttr, 0)

        // Init Wave
        waveColor = attributes.getColor(R.styleable.DeathStarLoader_ds_wave_color, DEFAULT_WAVE_COLOR)
        val amplitudeRatioAttr = attributes.getFloat(R.styleable.DeathStarLoader_ds_wave_amplitude, DEFAULT_AMPLITUDE_RATIO)
        amplitudeRatio = if (amplitudeRatioAttr > DEFAULT_AMPLITUDE_RATIO) DEFAULT_AMPLITUDE_RATIO else amplitudeRatioAttr
        setProgress(attributes.getInteger(R.styleable.DeathStarLoader_ds_progress, 0))

        attributes.recycle()
    }

    public override fun onDraw(canvas: Canvas) {
        updateWaveShader()

        // scale wave shader according to waveLengthRatio and amplitudeRatio
        // (waveLengthRatio, amplitudeRatio) ~ (width, height)
        waveShaderMatrix.setScale(
                1f,
                amplitudeRatio / DEFAULT_AMPLITUDE_RATIO,
                0f,
                defaultWaterLevel
        )
        // translate shader according to waveShiftRatio and waterLevelRatio
        // this decides the start position(waveShiftRatio for x, waterLevelRatio for y) of waves
        waveShaderMatrix.postTranslate(
                waveShiftRatio * width,
                (DEFAULT_WATER_LEVEL_RATIO - waterLevelRatio) * height
        )

        // assign matrix to invalidate the shader
        waveShader?.setLocalMatrix(waveShaderMatrix)

        // Draw Wave
        val radius = width / 2f
        canvas.drawCircle(width / 2f, height / 2f, radius, wavePaint)

        drawStar(canvas)
    }

    private fun drawStar(canvas: Canvas) {
        starPath.addCircle(width / 2f, height / 2f, width / 2f, Path.Direction.CW)
        canvas.clipPath(starPath)

        starPath.reset()
        starPath.addCircle(width / 2f, height / 2f, width / 2f - 5f, Path.Direction.CW)
        starPath.addCircle(width / 2.9f, height / 3.4f, width / 8f, Path.Direction.CW)
        starPath.addCircle(width / 2.9f, height / 3.4f, width / 20f, Path.Direction.CW)

        // 10
        starPath.moveTo(width * 0.55f, height * 0.9f)
        starPath.lineTo(width * 0.6f, height * 0.9f)
        // 20
        starPath.moveTo(width * 0.65f, height * 0.8f)
        starPath.lineTo(width.toFloat(), height * 0.8f)
        // 30
        starPath.moveTo(width * 0.7f, height * 0.7f)
        starPath.lineTo(width * 0.75f, height * 0.7f)
        starPath.moveTo(width * 0.8f, height * 0.7f)
        starPath.lineTo(width.toFloat(), height * 0.7f)
        // 40
        starPath.moveTo(width * 0.85f, height * 0.6f)
        starPath.lineTo(width.toFloat(), height * 0.6f)
        // 50 %
        starPath.moveTo(0f, height / 2f)
        starPath.lineTo(width.toFloat(), height / 2f)
        // 60
        starPath.moveTo(width * 0.75f, height * 0.4f)
        starPath.lineTo(width.toFloat(), height * 0.4f)
        // 70
        starPath.moveTo(width * 0.65f, height * 0.3f)
        starPath.lineTo(width * 0.7f, height * 0.3f)
        starPath.moveTo(width * 0.75f, height * 0.3f)
        starPath.lineTo(width.toFloat(), height * 0.3f)
        // 80
        starPath.moveTo(width * 0.6f, height * 0.2f)
        starPath.lineTo(width.toFloat(), height * 0.2f)
        // 90
        starPath.moveTo(width * 0.55f, height * 0.1f)
        starPath.lineTo(width * 0.65f, height * 0.1f)

        canvas.drawPath(starPath, starPaint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateWaveShader()
    }

    private fun updateWaveShader() {
        val defaultAngularFrequency = 2.0f * Math.PI / DEFAULT_WAVE_LENGTH_RATIO / width.toFloat()
        val defaultAmplitude = height * DEFAULT_AMPLITUDE_RATIO
        defaultWaterLevel = height * DEFAULT_WATER_LEVEL_RATIO

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val wavePaint = Paint()
        wavePaint.strokeWidth = 2f
        wavePaint.isAntiAlias = true

        // y = A * sin(ωx+φ) + h
        val endX = width + 1
        val endY = height + 1

        val waveY = FloatArray(endX)

        // background wave
        wavePaint.color = adjustAlpha(waveColor, 0.3f)
        for (beginX in 0 until endX) {
            val wx = beginX * defaultAngularFrequency
            val beginY = (defaultWaterLevel + defaultAmplitude * sin(wx)).toFloat()
            canvas.drawLine(beginX.toFloat(), beginY, beginX.toFloat(), endY.toFloat(), wavePaint)
            // store table for later shift without recalculation of sinus
            waveY[beginX] = beginY
        }
        // foreground wave
        wavePaint.color = waveColor
        val wave2Shift = width / 4
        for (beginX in 0 until endX) {
            canvas.drawLine(beginX.toFloat(), waveY[(beginX + wave2Shift) % endX], beginX.toFloat(), endY.toFloat(), wavePaint)
        }

        waveShader = BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP)
        this.wavePaint.shader = waveShader
    }

    fun setColor(color: Int) {
        waveColor = color
        updateWaveShader()
        invalidate()
    }

    /**
     * Set vertical size of wave according to `amplitudeRatio`
     *
     * @param amplitudeRatio Default to be 0.05. Result of amplitudeRatio + waterLevelRatio should be less than 1.
     */
    fun setAmplitudeRatio(amplitudeRatio: Float) {
        if (this.amplitudeRatio != amplitudeRatio) {
            this.amplitudeRatio = amplitudeRatio
            invalidate()
        }
    }

    @JvmOverloads
    fun setProgress(progress: Int, milliseconds: Long = 1000L) {
        // vertical animation.
        val waterLevelAnim = ObjectAnimator.ofFloat(this, "waterLevelRatio", waterLevelRatio, progress.toFloat() / 100)
        waterLevelAnim.duration = milliseconds
        waterLevelAnim.interpolator = DecelerateInterpolator()
        val animatorSetProgress = AnimatorSet()
        animatorSetProgress.play(waterLevelAnim)
        animatorSetProgress.start()
    }

    private fun startAnimation() {
            animatorSetWave?.start()
    }

    private fun initAnimation() {
        // horizontal animation.
        val waveShiftAnim = ObjectAnimator.ofFloat(this, "waveShiftRatio", 0f, 1f)
        waveShiftAnim.repeatCount = ValueAnimator.INFINITE
        waveShiftAnim.duration = 1000
        waveShiftAnim.interpolator = LinearInterpolator()

        animatorSetWave = AnimatorSet()
        animatorSetWave?.play(waveShiftAnim)
    }

    /**
     * Shift the wave horizontally according to `waveShiftRatio`.
     *
     * @param waveShiftRatio Should be 0 ~ 1. Default to be 0.
     */
    private fun setWaveShiftRatio(waveShiftRatio: Float) {
        if (this.waveShiftRatio != waveShiftRatio) {
            this.waveShiftRatio = waveShiftRatio
            invalidate()
        }
    }

    /**
     * Set water level according to `waterLevelRatio`.
     *
     * @param waterLevelRatio Should be 0 ~ 1. Default to be 0.5.
     */
    private fun setWaterLevelRatio(waterLevelRatio: Float) {
        if (this.waterLevelRatio != waterLevelRatio) {
            this.waterLevelRatio = waterLevelRatio
            invalidate()
        }
    }

    private fun cancel() {
        animatorSetWave?.end()
    }

    override fun onDetachedFromWindow() {
        cancel()
        super.onDetachedFromWindow()
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (visibility == VISIBLE) {
            startAnimation()
        } else {
            cancel()
        }
    }

    override fun onWindowVisibilityChanged(visibility: Int) {
        super.onWindowVisibilityChanged(visibility)
        if (getVisibility() != VISIBLE) {
            return
        }

        if (visibility == VISIBLE) {
            startAnimation()
        } else {
            cancel()
        }
    }

    /**
     * Transparent the given color by the factor
     * The more the factor closer to zero the more the color gets transparent
     *
     * @param color The color to transparent
     * @param factor 1.0f to 0.0f
     * @return int - A transplanted color
     */
    private fun adjustAlpha(color: Int, factor: Float): Int {
        val alpha = (Color.alpha(color) * factor).roundToInt()
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        return Color.argb(alpha, red, green, blue)
    }

    companion object {
        private const val DEFAULT_AMPLITUDE_RATIO = 0.05f
        private const val DEFAULT_WATER_LEVEL_RATIO = 0.5f
        private const val DEFAULT_WAVE_LENGTH_RATIO = 1.0f
        private const val DEFAULT_WAVE_SHIFT_RATIO = 0.0f
        private const val DEFAULT_WAVE_COLOR = Color.BLACK
    }
}
