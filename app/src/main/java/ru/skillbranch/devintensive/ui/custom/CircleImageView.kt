package ru.skillbranch.devintensive.ui.custom


import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import androidx.annotation.Dimension
import androidx.core.graphics.toColor
import ru.skillbranch.devintensive.R
import java.security.AccessControlContext
import android.graphics.*
import android.os.Build
import android.R.attr.inset
import android.R.attr.theme
import android.opengl.ETC1.getWidth
import android.opengl.ETC1.getHeight
import android.graphics.Shader
import android.graphics.BitmapShader
import android.graphics.RectF
import androidx.annotation.DrawableRes
import android.graphics.drawable.Drawable
import android.graphics.Bitmap
import android.net.Uri
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.graphics.Outline
import android.util.TypedValue
import android.view.ViewOutlineProvider
import androidx.annotation.RequiresApi
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import kotlinx.android.synthetic.main.activity_profile.view.*
import ru.skillbranch.devintensive.utils.Utils


class CircleImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {
    companion object {
        private const val DEFAULT_BORDER_COLOR = Color.WHITE
        private const val DEFAULT_BORDER_WIDTH = 2f // R.dimen.spacing_small_8

        private val SCALE_TYPE = ScaleType.CENTER_CROP

        private val BITMAP_CONFIG = Bitmap.Config.ARGB_8888
        private val COLORDRAWABLE_DIMENSION = 2

        private val DEFAULT_CIRCLE_BACKGROUND_COLOR = Color.TRANSPARENT
        private val DEFAULT_BORDER_OVERLAY = false
    }

    private var cv_borderColor = DEFAULT_BORDER_COLOR
    private var cv_borderWidth = DEFAULT_BORDER_WIDTH

    private var mDrawableRect = RectF()
    private var mBorderRect = RectF()

    private var mShaderMatrix = Matrix()
    private var mBitmapPaint = Paint()
    private var mBorderPaint = Paint()
    private var mCircleBackgroundPaint = Paint()

    private var mCircleBackgroundColor = DEFAULT_CIRCLE_BACKGROUND_COLOR

    private var mBitmap: Bitmap? = null
    private var mBitmapShader: BitmapShader? = null
    private var mBitmapWidth: Int = 0
    private var mBitmapHeight: Int = 0

    private var mDrawableRadius: Float = 0.toFloat()
    private var mBorderRadius: Float = 0.toFloat()

    private var mColorFilter: ColorFilter? = null

    private var mReady: Boolean = false
    private var mSetupPending: Boolean = false
    private var mBorderOverlay: Boolean = false
    private val mDisableCircularTransformation: Boolean = false

    init {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView)

            mBorderOverlay =
                DEFAULT_BORDER_OVERLAY //a.getBoolean(R.styleable.CircleImageView_civ_border_overlay, DEFAULT_BORDER_OVERLAY);
            mCircleBackgroundColor =
                DEFAULT_CIRCLE_BACKGROUND_COLOR //a.getColor(R.styleable.CircleImageView_civ_circle_background_color, DEFAULT_CIRCLE_BACKGROUND_COLOR);

            cv_borderColor = a.getColor(R.styleable.CircleImageView_cv_borderColor, DEFAULT_BORDER_COLOR)
            cv_borderWidth = a.getDimension(R.styleable.CircleImageView_cv_borderWidth, DEFAULT_BORDER_WIDTH)
            a.recycle()

            super.setScaleType(SCALE_TYPE)
            mReady = true

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                outlineProvider = OutlineProvider()
            }
            Log.d("M_CircleImageView", "init circle")
            if (mSetupPending) {
                setup()
                Log.d("M_CircleImageView", "init if (mSetupPending)")
                mSetupPending = false
            }

        }
    }

    private fun setup() {
        if (!mReady) {
            mSetupPending = true
            Log.d("M_CircleImageView", "!mReady")
            return
        }

        if (width == 0 && height == 0) {
            Log.d("M_CircleImageView", "width == 0 && height == 0")
            return
        }

        if (mBitmap == null) {
            Log.d("M_CircleImageView", "mBitmap == null")
            invalidate()
            return
        }
        Log.d("M_CircleImageView", "setup()")

        mBitmapShader = BitmapShader(mBitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

        Log.d("M_CircleImageView", "1")
        mBitmapPaint.isAntiAlias = true
        mBitmapPaint.shader = mBitmapShader
        Log.d("M_CircleImageView", "2")
        mBorderPaint.style = Paint.Style.STROKE
        mBorderPaint.isAntiAlias = true
        mBorderPaint.color = cv_borderColor
        mBorderPaint.strokeWidth = cv_borderWidth
        Log.d("M_CircleImageView", "3")
        mCircleBackgroundPaint.style = Paint.Style.FILL
        mCircleBackgroundPaint.isAntiAlias = true
        mCircleBackgroundPaint.color = mCircleBackgroundColor
        Log.d("M_CircleImageView", "4")
        mBitmapHeight = mBitmap!!.height
        mBitmapWidth = mBitmap!!.width
        Log.d("M_CircleImageView", "5")
        mBorderRect.set(calculateBounds())
        mBorderRadius =
            Math.min((mBorderRect.height() - cv_borderWidth) / 2.0f, (mBorderRect.width() - cv_borderWidth) / 2.0f)
        Log.d("M_CircleImageView", "6")
        mDrawableRect.set(mBorderRect)
        if (!mBorderOverlay && cv_borderWidth > 0) {
            mDrawableRect.inset(cv_borderWidth - 1.0f, cv_borderWidth - 1.0f)
        }
        mDrawableRadius = Math.min(mDrawableRect.height() / 2.0f, mDrawableRect.width() / 2.0f)
        Log.d("M_CircleImageView", "7")
        //       applyColorFilter()
        updateShaderMatrix()
        Log.d("M_CircleImageView", "8")
        invalidate()
        Log.d("M_CircleImageView", "9")
    }

    private fun updateShaderMatrix() {
        val scale: Float
        var dx = 0f
        var dy = 0f

        Log.d("M_CircleImageView", "updateShaderMatrix 1")
        mShaderMatrix.set(null)
        Log.d("M_CircleImageView", "updateShaderMatrix 2")

        if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width() * mBitmapHeight) {
            Log.d("M_CircleImageView", "updateShaderMatrix 31")
            scale = mDrawableRect.height() / mBitmapHeight //as Float
            dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f
        } else {
            Log.d("M_CircleImageView", "updateShaderMatrix 32")
            scale = mDrawableRect.width() / mBitmapWidth //as Float
            Log.d("M_CircleImageView", "updateShaderMatrix 321")
            dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f
            Log.d("M_CircleImageView", "updateShaderMatrix 322")
        }

        Log.d("M_CircleImageView", "updateShaderMatrix 4")
        mShaderMatrix.setScale(scale, scale)
        Log.d("M_CircleImageView", "updateShaderMatrix 5")
        mShaderMatrix.postTranslate((dx + 0.5f).toInt() + mDrawableRect.left, (dy + 0.5f).toInt() + mDrawableRect.top)

        Log.d("M_CircleImageView", "updateShaderMatrix 6")
        mBitmapShader?.setLocalMatrix(mShaderMatrix)
        Log.d("M_CircleImageView", "updateShaderMatrix 7")
    }


    private fun calculateBounds(): RectF {
        val availableWidth = width - paddingLeft - paddingRight
        val availableHeight = height - paddingTop - paddingBottom

        val sideLength = Math.min(availableWidth, availableHeight)

        val left = paddingLeft + (availableWidth - sideLength) / 2f
        val top = paddingTop + (availableHeight - sideLength) / 2f

        return RectF(left, top, left + sideLength, top + sideLength)
    }

    override fun onDraw(canvas: Canvas) {
        if (mDisableCircularTransformation) {
            super.onDraw(canvas);
            return;
        }

        if (mBitmap == null) {
            return;
        }

        if (mCircleBackgroundColor != Color.TRANSPARENT) {
            canvas.drawCircle(
                mDrawableRect.centerX(),
                mDrawableRect.centerY(),
                mDrawableRadius,
                mCircleBackgroundPaint
            );
        }
        canvas.drawCircle(mDrawableRect.centerX(), mDrawableRect.centerY(), mDrawableRadius, mBitmapPaint);
        if (cv_borderWidth > 0) {
            canvas.drawCircle(mBorderRect.centerX(), mBorderRect.centerY(), mBorderRadius, mBorderPaint);
        }
//        canvas.drawCircle(width/2f,height/2f,width/3f, Paint())
    }

    override fun setImageBitmap(bm: Bitmap) {
        super.setImageBitmap(bm)
        initializeBitmap()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        initializeBitmap()
    }

    override fun setImageResource(@DrawableRes resId: Int) {
        super.setImageResource(resId)
        initializeBitmap()
    }

    //     fun setImageURI(uri: Uri) {
//        super.setImageURI(uri)
//        initializeBitmap()
//    }
    private fun getBitmapFromDrawable(drawable: Drawable?): Bitmap? {
        if (drawable == null) {
            return null
        }

        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        try {
            val bitmap: Bitmap

            if (drawable is ColorDrawable) {
                bitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMENSION, COLORDRAWABLE_DIMENSION, BITMAP_CONFIG)
            } else {
                bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, BITMAP_CONFIG)
            }

            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setup()
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        super.setPadding(left, top, right, bottom)
        setup()
    }

    override fun setPaddingRelative(start: Int, top: Int, end: Int, bottom: Int) {
        super.setPaddingRelative(start, top, end, bottom)
        setup()
    }

    private fun initializeBitmap() {
        if (mDisableCircularTransformation) {
            mBitmap = null
            Log.d("M_CircleImageView", "initializeBitmap  mBitmap = null")
        } else {
            mBitmap = getBitmapFromDrawable(drawable)
            Log.d("M_CircleImageView", "initializeBitmap getBitmapFromDrawable(drawable)")
        }
        setup()
    }

    override fun getScaleType(): ImageView.ScaleType {
        return SCALE_TYPE
    }

    override fun setScaleType(scaleType: ImageView.ScaleType) {
        if (scaleType != SCALE_TYPE) {
            throw IllegalArgumentException(String.format("ScaleType %s not supported.", scaleType))
        }
    }

    override fun setAdjustViewBounds(adjustViewBounds: Boolean) {
        if (adjustViewBounds) {
            throw IllegalArgumentException("adjustViewBounds not supported.")
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private inner class OutlineProvider : ViewOutlineProvider() {

        override fun getOutline(view: View, outline: Outline) {
            val bounds = Rect()
            mBorderRect.roundOut(bounds)
            outline.setRoundRect(bounds, bounds.width() / 2.0f)
        }

    }

    public fun getBorderWidth(): Int {
        return cv_borderWidth.toInt()
    }

    public fun setBorderWidth(@Dimension dp: Int) {
        if (dp.toFloat() == cv_borderWidth) {
            return
        }

        cv_borderWidth = dp.toFloat();
        setup()
    }

    public fun getBorderColor(): Int {
        return cv_borderColor
    }

    public fun setBorderColor(@ColorRes colorId: Int) {
//        if (resources.getColor(colorId, context.theme) == cv_borderColor) {
        if (ContextCompat.getColor(context, colorId) == cv_borderColor) {
            return
        }


        cv_borderColor = ContextCompat.getColor(context, colorId)//resources.getColor(colorId, context.theme)
        mBorderPaint.setColor(cv_borderColor)
        invalidate()
    }

    public fun setBorderColor(hex: String) {
        if (hex[0].toString() != "#") {
            cv_borderColor = Color.parseColor("#$hex")
            // java.lang.Long.parseLong(hex, 16).
        } else {
            cv_borderColor = Color.parseColor(hex)
        }
        mBorderPaint.setColor(cv_borderColor)
        invalidate()
    }

    fun setInitialsImage(fN: String,lN: String) {
        if (Utils.toInitials(fN, lN)==null){
            setImageDrawable(resources.getDrawable(R.drawable.avatar_default, context.theme))
        }   else {
            val Initials = Utils.toInitials(fN, lN)!!
            val bitmap = createBitmap(1000, 1000, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            val value = TypedValue()
            context.theme.resolveAttribute(R.attr.colorAccent, value, true)

            val textScaleValue = bitmap.width / 200f

            val paintText = Paint().apply {
                color = Color.WHITE
                textAlign = Paint.Align.CENTER
                textSize = 80 * textScaleValue
            }

            canvas.drawColor(value.data)
            canvas.drawText(
                Initials,
                bitmap.width / 2f,
                (bitmap.height + 65 * textScaleValue) / 2f,
                paintText
            )

            setImageBitmap(bitmap)
        }
        invalidate()
    }

}