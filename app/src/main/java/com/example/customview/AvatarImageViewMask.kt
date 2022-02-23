package com.example.customview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.toRectF
import com.example.customview.extensions.dpToPx

class AvatarImageViewMask @JvmOverloads constructor(
    context: Context,
    attrs:AttributeSet? = null,
    defStyleAttr: Int = 0
): AppCompatImageView(context, attrs, defStyleAttr){

    @Px
    var borderWidth: Float = context.dpToPx(DEFAULT_BORDER_WIDTH)
    @ColorInt
    var borderColor: Int = DEFAULT_BORDER_COLOR
    var inials = DEFAULT_INITIALS
    private val maskPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val viewRect = Rect()
    private lateinit var resultBm: Bitmap
    private lateinit var maskBm: Bitmap
    private lateinit var srcBm: Bitmap

    init {
        if(attrs != null ){
            val attrsArray = context.obtainStyledAttributes(attrs, R.styleable.AvatarImageViewMask)
            borderWidth = attrsArray.getDimension(
                R.styleable.AvatarImageViewMask_aiv_borderWidth,
                context.dpToPx(DEFAULT_BORDER_WIDTH)
            )

            borderColor = attrsArray.getColor(
                R.styleable.AvatarImageViewMask_aiv_borderColor,
                DEFAULT_BORDER_COLOR
            )

            inials = attrsArray.getString(R.styleable.AvatarImageViewMask_aiv_initials) ?: DEFAULT_INITIALS
        }

        scaleType = ScaleType.CENTER_CROP
        setup()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val initSize = resolveDefaultSize(widthMeasureSpec)
        setMeasuredDimension(initSize, initSize)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawBitmap(maskBm, viewRect, viewRect, null)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if(w == 0) return
        with(viewRect){
            left = 0
            top = 0
            right = w
            bottom = h
        }

        prepareBitmaps(w, h)
    }

    private fun resolveDefaultSize(spec: Int): Int = when(MeasureSpec.getMode(spec)){
        MeasureSpec.AT_MOST -> MeasureSpec.getSize(spec)
        MeasureSpec.EXACTLY -> MeasureSpec.getSize(spec)
        MeasureSpec.UNSPECIFIED -> context.dpToPx(DEFAULT_SIZE).toInt()
        else -> MeasureSpec.getSize(spec)
    }

    private fun setup() {
        with(maskPaint){
            color = Color.GREEN
            style = Paint.Style.FILL
        }
    }

    private fun prepareBitmaps(width: Int, height: Int){
        maskBm = Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8)
        resultBm = maskBm.copy(Bitmap.Config.ARGB_8888, true)
        val maskCanvas = Canvas(maskBm)
        maskCanvas.drawOval(viewRect.toRectF(), maskPaint)
        maskPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        srcBm = drawable.toBitmap(width,height, Bitmap.Config.ARGB_8888)

        val resultCanvas = Canvas(resultBm)
        resultCanvas.drawBitmap(maskBm, viewRect, viewRect, null)
        resultCanvas.drawBitmap(srcBm, viewRect, viewRect, maskPaint)
    }

    companion object {
        private const val DEFAULT_SIZE = 50
        private const val DEFAULT_BORDER_WIDTH = 2
        private const val DEFAULT_BORDER_COLOR = Color.BLUE
        private const val DEFAULT_INITIALS = "?.?."
    }
}