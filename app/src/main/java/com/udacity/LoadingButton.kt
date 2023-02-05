package com.udacity

import android.animation.AnimatorInflater
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var bgColor = Color.BLACK
    private var tColor = Color.BLACK
    private var animationProgress = 0.0
    private var valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { p, old, new ->

    }
    private val updateAnimation = ValueAnimator.AnimatorUpdateListener {
        animationProgress = (it.animatedValue as Float).toDouble()
        invalidate()
        requestLayout()
    }

    init {
        isClickable = true
        valueAnimator = AnimatorInflater.loadAnimator(
            context, R.animator.loading_animation
        ) as ValueAnimator

        valueAnimator.addUpdateListener(updateAnimation)

        val attr = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingButton,
            0,
            0
        )
        try {
            bgColor = attr.getColor(
                R.styleable.LoadingButton_bgColor,
                ContextCompat.getColor(context, R.color.colorPrimary)
            )

            tColor = attr.getColor(
                R.styleable.LoadingButton_textColor,
                ContextCompat.getColor(context, R.color.white)
            )
        } finally {
            attr.recycle()
        }

    }

    private val rect = RectF(
        740f,
        50f,
        810f,
        110f
    )

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.strokeWidth = 0f
        paint.color = bgColor
        canvas?.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        if(buttonState != ButtonState.Loading) {
            paint.color = Color.WHITE
            canvas?.drawText(
                resources.getString(R.string.button_name),
                (width / 2).toFloat(),
                ((height + 30) / 2).toFloat(),
                paint
            )
        }
        if (buttonState == ButtonState.Loading) {
            paint.color = Color.parseColor("#004349")
            canvas?.drawRect(
                0f, 0f,
                (width * (animationProgress / 100)).toFloat(), height.toFloat(), paint
            )
            paint.color = Color.parseColor("#F9A825")
            canvas?.drawArc(rect,0f, (360 * (animationProgress / 100)).toFloat(), true, paint)
            //canvas?.drawArc((widthSize -40 ).toFloat(), (heightSize - 50).toFloat(), (widthSize-120).toFloat(), (heightSize - 100).toFloat(),0f, (360 * (animationProgress / 100)).toFloat(), true, paint)
            paint.color = tColor
            canvas?.drawText(
                resources.getString(R.string.button_loading), (width / 2).toFloat(), ((height + 30) / 2).toFloat(),
                paint
            )
        }
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }

    override fun performClick(): Boolean {
        super.performClick()
        if (buttonState == ButtonState.Completed) buttonState = ButtonState.Loading
        valueAnimator.start()
        return true
    }

    fun cancelAnimation() {
        valueAnimator.cancel()

        buttonState = ButtonState.Completed
        invalidate()
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}