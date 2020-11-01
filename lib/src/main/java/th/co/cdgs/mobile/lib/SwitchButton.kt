package th.co.cdgs.mobile.lib

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.annotation.TargetApi
import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.Checkable
import androidx.core.content.res.ResourcesCompat.getFont
import androidx.core.view.doOnLayout


class SwitchButton : View, Checkable {

    private val ANIMATE_STATE_NONE: Int = 0
    private val ANIMATE_STATE_PENDING_DRAG: Int = 1
    private val ANIMATE_STATE_DRAGING: Int = 2
    private val ANIMATE_STATE_PENDING_RESET: Int = 3
    private val ANIMATE_STATE_PENDING_SETTLE: Int = 4
    private val ANIMATE_STATE_SWITCH: Int = 5

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs)
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        super.setPadding(0, 0, 0, 0)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        var typedArray: TypedArray? = null
        if (attrs != null) {
            typedArray = context.obtainStyledAttributes(attrs, R.styleable.SwitchButton)
        }
        shadowEffect = optBoolean(
            typedArray,
            R.styleable.SwitchButton_sb_shadow_effect,
            true
        )
        uncheckCircleColor = optColor(
            typedArray,
            R.styleable.SwitchButton_sb_uncheckcircle_color,
            -0x555556
        ) //0XffAAAAAA;
        uncheckCircleWidth = optPixelSize(
            typedArray,
            R.styleable.SwitchButton_sb_uncheckcircle_width,
            dp2pxInt(1.5f)
        ) //dp2pxInt(1.5f);
        uncheckCircleOffsetX = dp2px(10f)
        uncheckCircleRadius = optPixelSize(
            typedArray,
            R.styleable.SwitchButton_sb_uncheckcircle_radius,
            dp2px(4f)
        ) //dp2px(4);
        checkedLineOffsetX = dp2px(4f)
        checkedLineOffsetY = dp2px(4f)
        shadowRadius = optPixelSize(
            typedArray,
            R.styleable.SwitchButton_sb_shadow_radius,
            dp2pxInt(2.5f)
        ) //dp2pxInt(2.5f);
        shadowOffset = optPixelSize(
            typedArray,
            R.styleable.SwitchButton_sb_shadow_offset,
            dp2pxInt(1.5f)
        ) //dp2pxInt(1.5f);
        shadowColor = optColor(
            typedArray,
            R.styleable.SwitchButton_sb_shadow_color,
            0X33000000
        ) //0X33000000;
        uncheckColor = optColor(
            typedArray,
            R.styleable.SwitchButton_sb_uncheck_color,
            -0x222223
        ) //0XffDDDDDD;
        checkedColor = optColor(
            typedArray,
            R.styleable.SwitchButton_sb_checked_color,
            -0xae2c99
        ) //0Xff51d367;
        borderWidth = optPixelSize(
            typedArray,
            R.styleable.SwitchButton_sb_border_width,
            dp2pxInt(1f)
        ) //dp2pxInt(1);
        checkLineColor = optColor(
            typedArray,
            R.styleable.SwitchButton_sb_checkline_color,
            Color.WHITE
        ) //Color.WHITE;
        checkLineWidth = optPixelSize(
            typedArray,
            R.styleable.SwitchButton_sb_checkline_width,
            dp2pxInt(1f)
        ) //dp2pxInt(1.0f);
        checkLineLength = dp2px(6f)
        val buttonColor: Int = optColor(
            typedArray,
            R.styleable.SwitchButton_sb_button_color,
            Color.WHITE
        ) //Color.WHITE;
        uncheckButtonColor = optColor(
            typedArray,
            R.styleable.SwitchButton_sb_uncheckbutton_color,
            buttonColor
        )
        checkedButtonColor = optColor(
            typedArray,
            R.styleable.SwitchButton_sb_checkedbutton_color,
            buttonColor
        )
        val effectDuration: Int = optInt(
            typedArray,
            R.styleable.SwitchButton_sb_effect_duration,
            300
        ) //300;
        isChecked = optBoolean(
            typedArray,
            R.styleable.SwitchButton_sb_checked,
            false
        )
        showIndicator = optBoolean(
            typedArray,
            R.styleable.SwitchButton_sb_show_indicator,
            false
        )
        background = optColor(
            typedArray,
            R.styleable.SwitchButton_sb_background,
            Color.WHITE
        ) //Color.WHITE;
        enableEffect = optBoolean(
            typedArray,
            R.styleable.SwitchButton_sb_enable_effect,
            true
        )
        textOn = optString(typedArray, R.styleable.SwitchButton_sb_text_on, null)
        textOnColor = optColor(typedArray, R.styleable.SwitchButton_sb_text_on_color, Color.BLACK)
        textOnSize = optPixelSize(typedArray, R.styleable.SwitchButton_sb_text_on_size, dp2px(15F))
        textOnFont = optResource(typedArray, R.styleable.SwitchButton_sb_text_on_font, NO_ID)
        textOff = optString(typedArray, R.styleable.SwitchButton_sb_text_off, null)
        textOffColor = optColor(typedArray, R.styleable.SwitchButton_sb_text_off_color, Color.BLACK)
        textOffSize =
            optPixelSize(typedArray, R.styleable.SwitchButton_sb_text_off_size, dp2px(15F))
        textOffFont = optResource(typedArray, R.styleable.SwitchButton_sb_text_off_font, NO_ID)
        gradientColor1 = optColor(
            typedArray,
            R.styleable.SwitchButton_sb_gradient_i,
            NO_ID
        )
        gradientColor2 = optColor(
            typedArray,
            R.styleable.SwitchButton_sb_gradient_ii,
            NO_ID
        )
        gradientColor3 = optColor(
            typedArray,
            R.styleable.SwitchButton_sb_gradient_iii,
            NO_ID
        )
        gradientPath = optInt(
            typedArray,
            R.styleable.SwitchButton_sb_gradient_path,
            NO_ID
        )
        typedArray?.recycle()
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        buttonPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        textPaint = Paint(Paint.ANTI_ALIAS_FLAG)

        if (gradientColor1 != NO_ID || gradientColor2 != NO_ID || gradientColor3 != NO_ID) {
            val colors = mutableListOf<Int>()
            if (gradientColor1 != NO_ID) {
                colors.add(gradientColor1)
            }
            if (gradientColor2 != NO_ID) {
                colors.add(gradientColor2)
            }
            if (gradientColor3 != NO_ID) {
                colors.add(gradientColor3)
            }
            gradientDrawable = GradientDrawable(
                when (gradientPath) {
                    1 -> GradientDrawable.Orientation.TOP_BOTTOM
                    2 -> GradientDrawable.Orientation.TR_BL
                    3 -> GradientDrawable.Orientation.RIGHT_LEFT
                    4 -> GradientDrawable.Orientation.BR_TL
                    5 -> GradientDrawable.Orientation.BOTTOM_TOP
                    6 -> GradientDrawable.Orientation.BL_TR
                    7 -> GradientDrawable.Orientation.LEFT_RIGHT
                    8 -> GradientDrawable.Orientation.TL_BR
                    else -> GradientDrawable.Orientation.TOP_BOTTOM
                }, colors.toIntArray()
            )
            this.doOnLayout {
                multiBitmap = Bitmap.createBitmap(
                    it.width,
                    it.height,
                    Bitmap.Config.ARGB_8888
                )
                multiBitmap?.let { cv = Canvas(it) }
                gradientDrawable?.setBounds(0, 0, it.width, it.height)
                cv?.let { c -> gradientDrawable?.draw(c) }
                multiBitmap?.let { b ->
                    bitmapShader =
                        BitmapShader(b, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
                }
            }
        }

        buttonPaint?.color = buttonColor
        if (shadowEffect) {
            buttonPaint?.setShadowLayer(
                shadowRadius.toFloat(),
                0F, shadowOffset.toFloat(),
                shadowColor
            )
        }
        viewState = ViewState()
        beforeState = ViewState()
        afterState = ViewState()
        valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator?.duration = effectDuration.toLong()
        valueAnimator?.repeatCount = 0
        valueAnimator?.addUpdateListener(animatorUpdateListener)
        valueAnimator?.addListener(animatorListener)
        super.setClickable(true)
        setPadding(0, 0, 0, 0)
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthMeasureSpec: Int = widthMeasureSpec
        var heightMeasureSpec: Int = heightMeasureSpec
        val widthMode: Int = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode: Int = MeasureSpec.getMode(heightMeasureSpec)
        if ((widthMode == MeasureSpec.UNSPECIFIED
                    || widthMode == MeasureSpec.AT_MOST)
        ) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(DEFAULT_WIDTH, MeasureSpec.EXACTLY)
        }
        if ((heightMode == MeasureSpec.UNSPECIFIED
                    || heightMode == MeasureSpec.AT_MOST)
        ) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(DEFAULT_HEIGHT, MeasureSpec.EXACTLY)
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val viewPadding: Float = (shadowRadius + shadowOffset).coerceAtLeast(borderWidth).toFloat()
        height = h - viewPadding - viewPadding
        width = w - viewPadding - viewPadding
        viewRadius = height * .5f
        buttonRadius = viewRadius - borderWidth
        left = viewPadding
        top = viewPadding
        right = w - viewPadding
        bottom = h - viewPadding
        centerX = (left + right) * .5f
        centerY = (top + bottom) * .5f
        buttonMinX = left + viewRadius
        buttonMaxX = right - viewRadius
        if (isChecked()) {
            setCheckedViewState(viewState)
        } else {
            setUncheckViewState(viewState)
        }
        isUiInited = true
        postInvalidate()
    }

    private fun setUncheckViewState(viewState: ViewState?) {
        viewState!!.radius = 0f
        viewState.checkStateColor = uncheckColor
        viewState.checkedLineColor = Color.TRANSPARENT
        viewState.buttonX = buttonMinX
        buttonPaint?.color = uncheckButtonColor
    }

    private fun setCheckedViewState(viewState: ViewState?) {
        viewState!!.radius = viewRadius
        viewState.checkStateColor = checkedColor
        viewState.checkedLineColor = checkLineColor
        viewState.buttonX = buttonMaxX
        buttonPaint?.color = checkedButtonColor
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint?.strokeWidth = borderWidth.toFloat()
        paint?.style = Paint.Style.FILL
        paint?.color = background
        drawRoundRect(
            canvas,
            left, top, right, bottom,
            viewRadius, paint
        )
        paint?.style = Paint.Style.STROKE
        if (gradientColor1 != NO_ID || gradientColor2 != NO_ID || gradientColor3 != NO_ID) {
            paint?.shader = bitmapShader
        } else {
            paint?.color = uncheckColor
        }
        drawRoundRect(
            canvas,
            left, top, right, bottom,
            viewRadius, paint
        )

        if (showIndicator) {
            drawUncheckIndicator(canvas)
        }

        val des: Float = viewState!!.radius * .5f
        paint?.style = Paint.Style.STROKE
        paint?.color = viewState!!.checkStateColor
        paint?.strokeWidth = borderWidth + des * 2f
        drawRoundRect(
            canvas,
            left + des, top + des, right - des, bottom - des,
            viewRadius, paint
        )

        paint?.style = Paint.Style.FILL
        paint?.strokeWidth = 1F
        drawArc(
            canvas,
            left, top,
            left + 2 * viewRadius, top + 2 * viewRadius, 90f, 180f, paint
        )
        paint?.let {
            canvas.drawRect(
                left + viewRadius, top,
                viewState!!.buttonX, top + 2 * viewRadius,
                it
            )
        }

        if (isChecked) {
            if (!textOn.isNullOrBlank() && !textOn.isNullOrEmpty()) {
                drawText(canvas, textPaint, textOn!!)
            }
        } else {
            if (!textOff.isNullOrBlank() && !textOff.isNullOrEmpty()) {
                drawText(canvas, textPaint, textOff!!)
            }
        }

        if (showIndicator) {
            drawCheckedIndicator(canvas)
        }

        drawButton(canvas, viewState!!.buttonX, centerY)
    }

    private fun drawCheckedIndicator(canvas: Canvas) {
        drawCheckedIndicator(
            canvas,
            viewState!!.checkedLineColor,
            checkLineWidth.toFloat(),
            left + viewRadius - checkedLineOffsetX, centerY - checkLineLength,
            left + viewRadius - checkedLineOffsetY, centerY + checkLineLength,
            paint
        )
    }

    private fun drawCheckedIndicator(
        canvas: Canvas,
        color: Int,
        lineWidth: Float,
        sx: Float, sy: Float, ex: Float, ey: Float,
        paint: Paint?
    ) {
        paint?.style = Paint.Style.STROKE
        paint?.color = color
        paint?.strokeWidth = lineWidth
        if (paint != null) {
            canvas.drawLine(
                sx, sy, ex, ey,
                paint
            )
        }
    }

    private fun drawUncheckIndicator(canvas: Canvas) {
        drawUncheckIndicator(
            canvas,
            uncheckCircleColor,
            uncheckCircleWidth.toFloat(),
            right - uncheckCircleOffsetX, centerY,
            uncheckCircleRadius,
            paint
        )
    }

    private fun drawUncheckIndicator(
        canvas: Canvas,
        color: Int,
        lineWidth: Float,
        centerX: Float, centerY: Float,
        radius: Float,
        paint: Paint?
    ) {
        paint?.style = Paint.Style.STROKE
        paint?.color = color
        paint?.strokeWidth = lineWidth
        if (paint != null) {
            canvas.drawCircle(centerX, centerY, radius, paint)
        }
    }

    private fun drawArc(
        canvas: Canvas,
        left: Float, top: Float,
        right: Float, bottom: Float,
        startAngle: Float, sweepAngle: Float,
        paint: Paint?
    ) {
        if (paint != null) {
            canvas.drawArc(
                left, top, right, bottom,
                startAngle, sweepAngle, true, paint
            )
        }
    }

    private fun drawRoundRect(
        canvas: Canvas,
        left: Float, top: Float,
        right: Float, bottom: Float,
        backgroundRadius: Float,
        paint: Paint?
    ) {
        if (paint != null) {
            canvas.drawRoundRect(
                left, top, right, bottom,
                backgroundRadius, backgroundRadius, paint
            )
        }
    }

    private fun drawButton(canvas: Canvas, x: Float, y: Float) {
        buttonPaint?.let { canvas.drawCircle(x, y, buttonRadius, it) }
        paint?.style = Paint.Style.STROKE
        paint?.strokeWidth = 1F
        paint?.color = -0x222223
        paint?.let { canvas.drawCircle(x, y, buttonRadius, it) }
    }

    private fun drawText(canvas: Canvas, paint: Paint?, text: String) {
        paint?.textAlign = Paint.Align.CENTER
        paint?.textSize = dp2sp(
            if (isChecked) {
                textOnSize
            } else {
                textOffSize
            }
        )
        paint?.color = if (isChecked) {
            textOnColor
        } else {
            textOffColor
        }
        if (isChecked) {
            if (textOnFont != NO_ID) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    paint?.typeface = resources.getFont(textOnFont)
                } else {
                    paint?.typeface = getFont(context, textOnFont)
                }
            } else {
                paint?.typeface = Typeface.DEFAULT_BOLD
            }
        } else {
            if (textOffFont != NO_ID) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    paint?.typeface = resources.getFont(textOffFont)
                } else {
                    paint?.typeface = getFont(context, textOffFont)
                }
            } else {
                paint?.typeface = Typeface.DEFAULT_BOLD
            }
        }
        val xPos = canvas.width / 2
        val yPos = (canvas.height / 2 - (paint?.descent()?.plus(paint.ascent())?.div(2)!!))
        canvas.drawText(text, xPos.toFloat(), yPos, paint)
    }

    override fun setChecked(checked: Boolean) {
        if (checked == isChecked()) {
            postInvalidate()
            return
        }
        toggle(enableEffect, false)
    }

    override fun isChecked(): Boolean {
        return isChecked
    }

    override fun toggle() {
        toggle(true)
    }

    fun toggle(animate: Boolean) {
        toggle(animate, true)
    }

    private fun toggle(animate: Boolean, broadcast: Boolean) {
        if (!isEnabled) {
            return
        }
        if (isEventBroadcast) {
            throw RuntimeException("should NOT switch the state in method: [onCheckedChanged]!")
        }
        if (!isUiInited) {
            isChecked = !isChecked
            if (broadcast) {
                broadcastEvent()
            }
            return
        }
        if (valueAnimator!!.isRunning) {
            valueAnimator!!.cancel()
        }
        if (!enableEffect || !animate) {
            isChecked = !isChecked
            if (isChecked()) {
                setCheckedViewState(viewState)
            } else {
                setUncheckViewState(viewState)
            }
            postInvalidate()
            if (broadcast) {
                broadcastEvent()
            }
            return
        }
        animateState = ANIMATE_STATE_SWITCH
        beforeState!!.copy(viewState)
        if (isChecked()) {
            setUncheckViewState(afterState)
        } else {
            setCheckedViewState(afterState)
        }
        valueAnimator!!.start()
    }

    private fun broadcastEvent() {
        if (onCheckedChangeListener != null) {
            isEventBroadcast = true
            onCheckedChangeListener!!.onCheckedChanged(this, isChecked())
        }
        isEventBroadcast = false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) {
            return false
        }
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                isTouchingDown = true
                touchDownTime = System.currentTimeMillis()
                removeCallbacks(postPendingDrag)
                postDelayed(postPendingDrag, 100)
            }
            MotionEvent.ACTION_MOVE -> {
                val eventX: Float = event.x
                if (isPendingDragState) {
                    var fraction: Float = eventX / getWidth()
                    fraction = 0f.coerceAtLeast(1f.coerceAtMost(fraction))
                    viewState!!.buttonX = (buttonMinX
                            + (buttonMaxX - buttonMinX)
                            * fraction)
                } else if (isDragState) {
                    var fraction: Float = eventX / getWidth()
                    fraction = 0f.coerceAtLeast(1f.coerceAtMost(fraction))
                    viewState!!.buttonX = (buttonMinX
                            + (buttonMaxX - buttonMinX)
                            * fraction)
                    viewState!!.checkStateColor = argbEvaluator.evaluate(
                        fraction,
                        uncheckColor,
                        checkedColor
                    ) as Int
                    postInvalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                isTouchingDown = false
                removeCallbacks(postPendingDrag)
                if (System.currentTimeMillis() - touchDownTime <= 300) {
                    toggle()
                } else if (isDragState) {
                    val eventX: Float = event.x
                    var fraction: Float = eventX / getWidth()
                    fraction = 0f.coerceAtLeast(1f.coerceAtMost(fraction))
                    val newCheck: Boolean = fraction > .5f
                    if (newCheck == isChecked()) {
                        pendingCancelDragState()
                    } else {
                        isChecked = newCheck
                        pendingSettleState()
                    }
                } else if (isPendingDragState) {
                    pendingCancelDragState()
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                isTouchingDown = false
                removeCallbacks(postPendingDrag)
                if ((isPendingDragState
                            || isDragState)
                ) {
                    pendingCancelDragState()
                }
            }
        }
        return true
    }

    private val isInAnimating: Boolean
        get() = animateState != ANIMATE_STATE_NONE

    private val isPendingDragState: Boolean
        get() = (animateState == ANIMATE_STATE_PENDING_DRAG
                || animateState == ANIMATE_STATE_PENDING_RESET)

    private val isDragState: Boolean
        get() {
            return animateState == ANIMATE_STATE_DRAGING
        }

    fun setShadowEffect(shadowEffect: Boolean) {
        if (this.shadowEffect == shadowEffect) {
            return
        }
        this.shadowEffect = shadowEffect
        if (this.shadowEffect) {
            buttonPaint?.setShadowLayer(
                shadowRadius.toFloat(),
                0F, shadowOffset.toFloat(),
                shadowColor
            )
        } else {
            buttonPaint?.setShadowLayer(
                0F,
                0F, 0F,
                0
            )
        }
    }

    fun setEnableEffect(enable: Boolean) {
        enableEffect = enable
    }

    private fun pendingDragState() {
        if (isInAnimating) {
            return
        }
        if (!isTouchingDown) {
            return
        }
        if (valueAnimator!!.isRunning) {
            valueAnimator!!.cancel()
        }
        animateState = ANIMATE_STATE_PENDING_DRAG
        beforeState!!.copy(viewState)
        afterState!!.copy(viewState)
        if (isChecked()) {
            afterState!!.checkStateColor = checkedColor
            afterState!!.buttonX = buttonMaxX
            afterState!!.checkedLineColor = checkedColor
        } else {
            afterState!!.checkStateColor = uncheckColor
            afterState!!.buttonX = buttonMinX
            afterState!!.radius = viewRadius
        }
        valueAnimator!!.start()
    }

    private fun pendingCancelDragState() {
        if (isDragState || isPendingDragState) {
            if (valueAnimator!!.isRunning) {
                valueAnimator!!.cancel()
            }
            animateState = ANIMATE_STATE_PENDING_RESET
            beforeState!!.copy(viewState)
            if (isChecked()) {
                setCheckedViewState(afterState)
            } else {
                setUncheckViewState(afterState)
            }
            valueAnimator!!.start()
        }
    }

    private fun pendingSettleState() {
        if (valueAnimator!!.isRunning) {
            valueAnimator!!.cancel()
        }
        animateState = ANIMATE_STATE_PENDING_SETTLE
        beforeState!!.copy(viewState)
        if (isChecked()) {
            setCheckedViewState(afterState)
        } else {
            setUncheckViewState(afterState)
        }
        valueAnimator!!.start()
    }

    override fun setOnClickListener(l: OnClickListener?) {}
    override fun setOnLongClickListener(l: OnLongClickListener?) {}
    fun setOnCheckedChangeListener(l: OnCheckedChangeListener?) {
        onCheckedChangeListener = l
    }

    interface OnCheckedChangeListener {
        fun onCheckedChanged(view: SwitchButton?, isChecked: Boolean)
    }

    private var shadowRadius: Int = 0
    private var shadowOffset: Int = 0
    private var shadowColor: Int = 0
    private var viewRadius: Float = 0f
    private var buttonRadius: Float = 0f
    private var height: Float = 0f
    private var width: Float = 0f
    private var left: Float = 0f
    private var top: Float = 0f
    private var right: Float = 0f
    private var bottom: Float = 0f
    private var centerX: Float = 0f
    private var centerY: Float = 0f
    private var background: Int = 0
    private var uncheckColor: Int = 0
    private var checkedColor: Int = 0
    private var borderWidth: Int = 0
    private var checkLineColor: Int = 0
    private var checkLineWidth: Int = 0
    private var checkLineLength: Float = 0f
    private var uncheckCircleColor: Int = 0
    private var uncheckCircleWidth: Int = 0
    private var uncheckCircleOffsetX: Float = 0f
    private var uncheckCircleRadius: Float = 0f
    private var checkedLineOffsetX: Float = 0f
    private var checkedLineOffsetY: Float = 0f
    private var uncheckButtonColor: Int = 0
    private var checkedButtonColor: Int = 0
    private var buttonMinX: Float = 0f
    private var buttonMaxX: Float = 0f
    private var buttonPaint: Paint? = null
    private var paint: Paint? = null
    private var textPaint: Paint? = null
    private var viewState: ViewState? = null
    private var beforeState: ViewState? = null
    private var afterState: ViewState? = null
    private val rect: RectF = RectF()
    private var animateState: Int = ANIMATE_STATE_NONE
    private var valueAnimator: ValueAnimator? = null
    private val argbEvaluator: ArgbEvaluator = ArgbEvaluator()
    private var isChecked: Boolean = false
    private var enableEffect: Boolean = false
    private var shadowEffect: Boolean = false
    private var showIndicator: Boolean = false
    private var isTouchingDown: Boolean = false
    private var isUiInited: Boolean = false
    private var isEventBroadcast: Boolean = false
    private var onCheckedChangeListener: OnCheckedChangeListener? = null
    private var touchDownTime: Long = 0
    private var textOn: String? = "On"
    private var textOnColor: Int = Color.BLACK
    private var textOnSize: Float = 15F
    private var textOnFont: Int = 0
    private var textOff: String? = "Off"
    private var textOffColor: Int = Color.BLACK
    private var textOffSize: Float = 15F
    private var textOffFont: Int = 0
    private var gradientDrawable: GradientDrawable? = null
    private var multiBitmap: Bitmap? = null
    private var cv: Canvas? = null
    private var bitmapShader: BitmapShader? = null
    private var gradientColor1: Int = 0
    private var gradientColor2: Int = 0
    private var gradientColor3: Int = 0
    private var gradientPath: Int = 0
    private val postPendingDrag: Runnable = Runnable {
        if (!isInAnimating) {
            pendingDragState()
        }
    }
    private val animatorUpdateListener: AnimatorUpdateListener = object : AnimatorUpdateListener {
        override fun onAnimationUpdate(animation: ValueAnimator) {
            val value: Float = animation.animatedValue as Float
            when (animateState) {
                ANIMATE_STATE_PENDING_SETTLE -> {
                    run {}
                    run {}
                    run {
                        viewState!!.checkedLineColor = argbEvaluator.evaluate(
                            value,
                            beforeState!!.checkedLineColor,
                            afterState!!.checkedLineColor
                        ) as Int
                        viewState!!.radius = (beforeState!!.radius
                                + (afterState!!.radius - beforeState!!.radius) * value)
                        if (animateState != ANIMATE_STATE_PENDING_DRAG) {
                            viewState!!.buttonX = (beforeState!!.buttonX
                                    + (afterState!!.buttonX - beforeState!!.buttonX) * value)
                        }
                        viewState!!.checkStateColor = argbEvaluator.evaluate(
                            value,
                            beforeState!!.checkStateColor,
                            afterState!!.checkStateColor
                        ) as Int
                    }
                }
                ANIMATE_STATE_PENDING_RESET -> {
                    run {}
                    run {
                        viewState!!.checkedLineColor = argbEvaluator.evaluate(
                            value,
                            beforeState!!.checkedLineColor,
                            afterState!!.checkedLineColor
                        ) as Int
                        viewState!!.radius = (beforeState!!.radius
                                + (afterState!!.radius - beforeState!!.radius) * value)
                        if (animateState != ANIMATE_STATE_PENDING_DRAG) {
                            viewState!!.buttonX = (beforeState!!.buttonX
                                    + (afterState!!.buttonX - beforeState!!.buttonX) * value)
                        }
                        viewState!!.checkStateColor = argbEvaluator.evaluate(
                            value,
                            beforeState!!.checkStateColor,
                            afterState!!.checkStateColor
                        ) as Int
                    }
                }
                ANIMATE_STATE_PENDING_DRAG -> {
                    viewState!!.checkedLineColor = argbEvaluator.evaluate(
                        value,
                        beforeState!!.checkedLineColor,
                        afterState!!.checkedLineColor
                    ) as Int
                    viewState!!.radius = (beforeState!!.radius
                            + (afterState!!.radius - beforeState!!.radius) * value)
                    if (animateState != ANIMATE_STATE_PENDING_DRAG) {
                        viewState!!.buttonX = (beforeState!!.buttonX
                                + (afterState!!.buttonX - beforeState!!.buttonX) * value)
                    }
                    viewState!!.checkStateColor = argbEvaluator.evaluate(
                        value,
                        beforeState!!.checkStateColor,
                        afterState!!.checkStateColor
                    ) as Int
                }
                ANIMATE_STATE_SWITCH -> {
                    viewState!!.buttonX = (beforeState!!.buttonX
                            + (afterState!!.buttonX - beforeState!!.buttonX) * value)
                    val fraction: Float =
                        (viewState!!.buttonX - buttonMinX) / (buttonMaxX - buttonMinX)
                    viewState!!.checkStateColor = argbEvaluator.evaluate(
                        fraction,
                        uncheckColor,
                        checkedColor
                    ) as Int
                    viewState!!.radius = fraction * viewRadius
                    viewState!!.checkedLineColor = argbEvaluator.evaluate(
                        fraction,
                        Color.TRANSPARENT,
                        checkLineColor
                    ) as Int
                }
                ANIMATE_STATE_DRAGING -> {
                    run {}
                    run {}
                }
                ANIMATE_STATE_NONE -> {
                }
                else -> {
                    run {}
                    run {}
                }
            }
            postInvalidate()
        }
    }
    private val animatorListener: Animator.AnimatorListener = object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator) {}
        override fun onAnimationEnd(animation: Animator) {
            when (animateState) {
                ANIMATE_STATE_DRAGING -> {
                }
                ANIMATE_STATE_PENDING_DRAG -> {
                    animateState = ANIMATE_STATE_DRAGING
                    viewState!!.checkedLineColor = Color.TRANSPARENT
                    viewState!!.radius = viewRadius
                    postInvalidate()
                }
                ANIMATE_STATE_PENDING_RESET -> {
                    animateState = ANIMATE_STATE_NONE
                    postInvalidate()
                }
                ANIMATE_STATE_PENDING_SETTLE -> {
                    animateState = ANIMATE_STATE_NONE
                    postInvalidate()
                    broadcastEvent()
                }
                ANIMATE_STATE_SWITCH -> {
                    isChecked = !isChecked
                    animateState = ANIMATE_STATE_NONE
                    postInvalidate()
                    broadcastEvent()
                }
                ANIMATE_STATE_NONE -> {
                }
                else -> {
                }
            }
        }

        override fun onAnimationCancel(animation: Animator) {}
        override fun onAnimationRepeat(animation: Animator) {}
    }

    private class ViewState {
        var buttonX: Float = 0f
        var checkStateColor: Int = 0
        var checkedLineColor: Int = 0
        var radius: Float = 0f
        fun copy(source: ViewState?) {
            buttonX = source!!.buttonX
            checkStateColor = source.checkStateColor
            checkedLineColor = source.checkedLineColor
            radius = source.radius
        }
    }

    companion object {
        private val DEFAULT_WIDTH: Int = dp2pxInt(58f)
        private val DEFAULT_HEIGHT: Int = dp2pxInt(36f)

        private fun dp2px(dp: Float): Float {
            val r: Resources = Resources.getSystem()
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.displayMetrics)
        }

        private fun dp2sp(dp: Float): Float {
            val r: Resources = Resources.getSystem()
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, dp, r.displayMetrics)
        }

        private fun dp2pxInt(dp: Float): Int {
            return dp2px(dp).toInt()
        }

        private fun optInt(
            typedArray: TypedArray?,
            index: Int,
            def: Int
        ): Int {
            if (typedArray == null) {
                return def
            }
            return typedArray.getInt(index, def)
        }

        private fun optPixelSize(
            typedArray: TypedArray?,
            index: Int,
            def: Float
        ): Float {
            if (typedArray == null) {
                return def
            }
            return typedArray.getDimension(index, def)
        }

        private fun optPixelSize(
            typedArray: TypedArray?,
            index: Int,
            def: Int
        ): Int {
            if (typedArray == null) {
                return def
            }
            return typedArray.getDimensionPixelOffset(index, def)
        }

        private fun optColor(
            typedArray: TypedArray?,
            index: Int,
            def: Int
        ): Int {
            if (typedArray == null) {
                return def
            }
            return typedArray.getColor(index, def)
        }

        private fun optBoolean(
            typedArray: TypedArray?,
            index: Int,
            def: Boolean
        ): Boolean {
            if (typedArray == null) {
                return def
            }
            return typedArray.getBoolean(index, def)
        }

        private fun optString(
            typedArray: TypedArray?,
            index: Int,
            def: String?
        ): String? {
            if (typedArray == null) {
                return def
            }
            return typedArray.getString(index)
        }

        private fun optResource(
            typedArray: TypedArray?,
            index: Int,
            def: Int
        ): Int {
            if (typedArray == null) {
                return def
            }
            return typedArray.getResourceId(index, def)
        }
    }
}