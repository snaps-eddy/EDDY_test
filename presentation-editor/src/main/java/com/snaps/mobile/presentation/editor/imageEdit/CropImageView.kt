package com.snaps.mobile.presentation.editor.imageEdit

import android.content.Context
import android.graphics.*
import android.os.Build
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.util.SizeF
import android.util.TypedValue
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.snaps.common.utils.constant.Config
import com.snaps.common.utils.log.Dlog
import com.snaps.mobile.domain.save.Filter
import com.snaps.mobile.presentation.editor.R
import com.snaps.mobile.presentation.editor.databinding.ToastNoPrintCustomBinding
import java.lang.ref.WeakReference
import java.security.MessageDigest
import kotlin.math.abs

class CropImageView(context: Context, attrs: AttributeSet?, defStyle: Int) :
    AppCompatImageView(context, attrs, defStyle), ScaleGestureDetector.OnScaleGestureListener, GestureDetector.OnGestureListener {

    companion object {
        private const val IS_SUPPORT_DYNAMIC_LOW_RES_WARNNING = false
        private const val MAX_ZOOM = 4f
        private const val PRINT_AREA_LINE_THICKNESS_DP = 2
        private const val PRINT_AREA_OUTLINE_COLOR = "#ffffff"
        private const val NON_PRINT_AREA_COLOR = "#AA333333"
        private const val MSG_SET_IMAGE_EDIT_PARAMS = 1
    }

    private var myWidth = 0f
    private var myHeight = 0f

    private val mmToPixel: Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 1f, resources.displayMetrics)

    private val printAreaOutlinePaint: Paint
    private val nonPrintAreaPaint: Paint
    private val printAreaRectBaseOnView = RectF()
    private val drawPrintAreaRectBaseOnView = RectF()
    private val bitmapRectToViewRectMatrix = Matrix()

    private var imageEditParams = ImageEditParams()

    @Volatile
    private var isSetBitmap = false
    private var baseBitmapWidth = 0f
    private var baseBitmapHeight = 0f
    private var matrixBaseTranslateY = 0f

    private var warningResolutionBitmap: Bitmap? = null
    private var warningResolutionToast: Toast? = null

    @Volatile
    private var isWarningResolution = false

    private val scaleGestureDetector: ScaleGestureDetector = ScaleGestureDetector(context, this)
    private val gestureListener: GestureDetector = GestureDetector(context, this)
    private val matrixCalculator = MatrixCalculator()

    private var scaleForDebug = 0f
    private var offsetXForDebug = 0f
    private var offsetYForDebug = 0f
    private var widthForDebug = 0f
    private var heightForDebug = 0f

    //https://stackoverflow.com/questions/52025220/how-to-use-handler-and-handlemessage-in-kotlin
    private val cropImageView = WeakReference(this)
    private val myHandler = MyHandler(cropImageView)

    class MyHandler(private val cropImageView: WeakReference<CropImageView>) : Handler() {
        override fun handleMessage(msg: Message?) {
            msg?.let { message ->
                when (message.what) {
                    MSG_SET_IMAGE_EDIT_PARAMS -> cropImageView.get()?.let {
                        it.setImageEditParams(message.obj as ImageEditParams)
                    }
                    else -> ""
                }
            }
        }
    }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    init {
//        setLayerType(LAYER_TYPE_SOFTWARE, null) //필터 때문인듯
//        setWillNotDraw(false) //onDraw 호출 되도록  https://pupabu.tistory.com/46

        printAreaOutlinePaint = Paint().apply {
            isAntiAlias = true
            color = Color.parseColor(PRINT_AREA_OUTLINE_COLOR)
            style = Paint.Style.STROKE
            strokeWidth = convertDpToPixel(PRINT_AREA_LINE_THICKNESS_DP.toFloat())
        }

        nonPrintAreaPaint = Paint().apply {
            isAntiAlias = true
            color = Color.parseColor(NON_PRINT_AREA_COLOR)
            style = Paint.Style.FILL
        }
    }

    fun isSetBitmap(): Boolean {
        return isSetBitmap
    }

    override fun setImageBitmap(bitmap: Bitmap?) {
        isSetBitmap = (bitmap != null)
        super.setImageBitmap(bitmap)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //기존 이미지 상세 편집 화면은 가로/세로 모드를 지원하지만 새로 만드는 이미지 상세 편집 화면은 세로만 지원한다.
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val width = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> 0.coerceAtMost(widthSize)
            else -> 0
        }

        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> 0.coerceAtMost(heightSize)
            else -> 0
        }

        myWidth = 0f.coerceAtLeast(width.toFloat())
        myHeight = 0f.coerceAtLeast(height.toFloat())
        super.setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (isSetBitmap) {
            if (isWarningResolution) {
                if (warningResolutionBitmap == null) {
                    warningResolutionBitmap = ContextCompat.getDrawable(context, R.drawable.ic_notice)?.toBitmap()?.let {
                        val pixel = convertDpToPixel(20f)
                        Bitmap.createScaledBitmap(it, pixel.toInt(), pixel.toInt(), false)
                    }
                }
                warningResolutionBitmap?.let {
                    canvas.drawBitmap(it, (myWidth - it.width) / 2f, (myHeight - it.height) / 2f, null)
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                canvas.clipOutRect(drawPrintAreaRectBaseOnView)
            } else {
                canvas.clipRect(drawPrintAreaRectBaseOnView, Region.Op.DIFFERENCE)
            }

            val nonPrintRect = matrixCalculator.getBitmapRect(
                matrix = bitmapRectToViewRectMatrix,
                orgBitmapWidth = baseBitmapWidth,
                orgBitmapHeight = baseBitmapHeight
            )

            canvas.drawRect(nonPrintRect, nonPrintAreaPaint)

            canvas.drawRect(printAreaRectBaseOnView, printAreaOutlinePaint)

            showInfoFroDebug(canvas)
        }
    }

    fun showInfoFroDebug(canvas: Canvas) {
        if (!Config.isDevelopVersion()) return

        mutableListOf<String>().apply {
            add(imageEditParams.imageUri.substringAfterLast("/", ""))
            add(
                buildString {
                    append("org: ${imageEditParams.originWidth.toInt()} x ${imageEditParams.originHeight.toInt()}     ")
                    append("ot: ${imageEditParams.orientationAngle}     ")
                    append("max: ${imageEditParams.availableMaxWidth.toInt()} x ${imageEditParams.availableMaxHeight.toInt()}")
                }
            )
            add("angle: ${imageEditParams.edit.angle}")
            add("scale: $scaleForDebug")
            add("x: $offsetXForDebug")
            add("y: $offsetYForDebug")
            add("width: $widthForDebug")
            add("height: $heightForDebug")
        }.let {
            val paint = Paint().apply {
                isAntiAlias = true
                color = Color.MAGENTA
                textSize = 34f
            }
            it.forEachIndexed { index, info ->
                canvas.drawText(info, 16f, index * 40f + 30f, paint)
            }
        }
    }

    fun reset() {
        if (!imageEditParams.isValid()) return
        resetData()
//        imageEditParams.edit.angle = 0
        imageEditParams.edit.angle = imageEditParams.orientationAngle  //TODO
        setImageEditParams(imageEditParams)
    }

    fun rotate() {
        if (!imageEditParams.isValid()) return
        resetData()
        imageEditParams.edit.angle = (imageEditParams.edit.angle + 90) % 360
        setImageEditParams(imageEditParams)
    }

    fun setImageEditParams(params: ImageEditParams) {
        if (!params.isValid()) return

        Dlog.d("CropImageView", params.imageUri)

        if (myWidth < 1 || myHeight < 1) {
            myHandler.obtainMessage(MSG_SET_IMAGE_EDIT_PARAMS, params).sendToTarget()
            return
        }

        imageEditParams = params
        imageEditParams.apply {
            edit.width = frameWidth.coerceAtLeast(edit.width) // 혹시 모르니 보정
            edit.height = frameHeight.coerceAtLeast(edit.height) // 혹시 모르니 보정
        }

        initData()

        setImageBitmap(null)
        loadBitmap(imageEditParams.edit.filter.imageUri ?: imageEditParams.imageUri)

        //순서 중요! 먼저 확대 후 이동
        val scale = imageEditParams.run {
            (edit.width / frameWidth).coerceAtMost(edit.height / frameHeight)
        }
        applyMatrixScale(scale)

        val translatePoint = imageEditParams.run {
            val moveX = (frameWidth - edit.width) / 2f - edit.x
            val moveY = (frameHeight - edit.height) / 2f - edit.y
            val scaleEditor2Editor = printAreaRectBaseOnView.width() / frameWidth
            PointF(moveX * scaleEditor2Editor, moveY * scaleEditor2Editor)
        }
        applyMatrixTranslate(translatePoint.x * -1, translatePoint.y * -1)

        checkWarningResolution()

        calOffsetForDebug()
    }

    fun getImageEditResult(): ImageEditParams {
        if (!isSetBitmap) {
            return imageEditParams.copy()
        }
        val scaleEditor2Editor = imageEditParams.frameWidth / printAreaRectBaseOnView.width()
        val bitmapRect = matrixCalculator.getBitmapRect(bitmapRectToViewRectMatrix, baseBitmapWidth, baseBitmapHeight)

        val editedX = (bitmapRect.left - printAreaRectBaseOnView.left) * scaleEditor2Editor
        val editedY = (bitmapRect.top - printAreaRectBaseOnView.top) * scaleEditor2Editor

        val editedWidth = imageEditParams.frameWidth.coerceAtLeast(bitmapRect.width() * scaleEditor2Editor) //오차 보정
        val editedHeight = imageEditParams.frameHeight.coerceAtLeast(bitmapRect.height() * scaleEditor2Editor) //오차 보정

        return imageEditParams.copy(
            edit = ImageEditParams.Edit(
                x = editedX,
                y = editedY,
                width = editedWidth,
                height = editedHeight,
                angle = imageEditParams.edit.angle,
                filter = imageEditParams.edit.filter
            )
        )
    }

    fun clearBitmap() {
        setImageBitmap(null)
    }

    fun applyFilteredImage(imageUri: String) {
        loadBitmap(imageUri)
    }

    private fun loadBitmap(imageUri: String) {
        val bitmapSize = calcLoadBitmapSize()

        Glide.with(context)
            .asBitmap()
            .skipMemoryCache(false)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .override(bitmapSize.width.toInt(), bitmapSize.height.toInt())
            .fitCenter()
            .apply {
//                val angle = imageEditParams.run { orientationAngle + edit.angle }
                val angle = imageEditParams.edit.angle //TODO
                if (angle != 0) transform(RotateTransformation(angle))
            }
            .load(imageUri)
//            .listener(object : RequestListener<Bitmap> {
//                override fun onResourceReady(
//                    resource: Bitmap?,
//                    model: Any?,
//                    target: Target<Bitmap>?,
//                    dataSource: DataSource?,
//                    isFirstResource: Boolean
//                ): Boolean {
//                    return false
//                }
//
//                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
//                    return false
//                }
//            })
            .into(this)
    }

    private fun resetData() {
        isWarningResolution = false

        val scaleSize = imageEditParams.run {
            val bitmapSize = calcRotatedBitmapSize(calcLoadBitmapSize(), imageEditParams.orientationAngle)
            val scale = (frameWidth / bitmapSize.width).coerceAtLeast(frameHeight / bitmapSize.height)
            SizeF((bitmapSize.width * scale).coerceAtLeast(frameWidth), (bitmapSize.height * scale).coerceAtLeast(frameHeight))
        }

        imageEditParams.apply {
            edit.x = (frameWidth - scaleSize.width) / 2
            edit.y = (frameHeight - scaleSize.height) / 2
            edit.width = scaleSize.width
            edit.height = scaleSize.height
            edit.filter = Filter.None()
        }
    }

    private fun initData() {
        isWarningResolution = false

//        val bitmapSize = calcRotatedBitmapSize(calcLoadBitmapSize(), imageEditParams.run { orientationAngle + edit.angle })
        val bitmapSize = calcRotatedBitmapSize(calcLoadBitmapSize(), imageEditParams.edit.angle)  //TODO
        baseBitmapWidth = bitmapSize.width
        baseBitmapHeight = bitmapSize.height
        matrixBaseTranslateY = (myHeight - myWidth) / 2f

        printAreaRectBaseOnView.apply {
            val rect = createPrintAreaRect(
                loadImgWidth = baseBitmapWidth,
                loadImgHeight = baseBitmapHeight,
                frameWidth = imageEditParams.frameWidth,
                frameHeight = imageEditParams.frameHeight
            )
            set(rect)
            offset(0f, matrixBaseTranslateY)
        }

        drawPrintAreaRectBaseOnView.apply {
            set(printAreaRectBaseOnView)
            inset(1f, 1f)
        }

        bitmapRectToViewRectMatrix.apply {
            val matrix = matrixCalculator.createRectToRectMatrix(
                srcWidth = baseBitmapWidth,
                srcHeight = baseBitmapHeight,
                dstWidth = myWidth,
                dstHeight = myWidth
            )
            set(matrix)
            postTranslate(0f, matrixBaseTranslateY)
        }

        hideWarningResolutionToast()
    }

    private fun calcRotatedBitmapSize(size: SizeF, angle: Int): SizeF {
        val list = listOf(size.width, size.height)
        val index = (angle / 90) % 2
        return SizeF(list[index], list.reversed()[index])
    }

    private fun calcLoadBitmapSize(): SizeF {
        return imageEditParams.run {
            val scale = (myWidth / originWidth).coerceAtMost(myWidth / originHeight)
            if (originWidth > originHeight) {
                SizeF(myWidth.coerceAtLeast(originWidth * scale), originHeight * scale)
            } else {
                SizeF(originWidth * scale, myWidth.coerceAtLeast(originHeight * scale))
            }
        }
    }

    private fun createPrintAreaRect(loadImgWidth: Float, loadImgHeight: Float, frameWidth: Float, frameHeight: Float): RectF {
        val scaleSize = run {
            val scale = (loadImgWidth / frameWidth).coerceAtMost(loadImgHeight / frameHeight)
            SizeF((frameWidth * scale).coerceAtMost(loadImgWidth), (frameHeight * scale).coerceAtMost(loadImgHeight))
        }

        val x = (myWidth - scaleSize.width) / 2f
        val y = (myWidth - scaleSize.height) / 2f
        return RectF(x, y, x + scaleSize.width, y + scaleSize.height)
    }

    // https://stackoverflow.com/questions/4605527/converting-pixels-to-dp
    private fun convertDpToPixel(dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
    }

    private fun applyMatrixScale(scale: Float) {
        val practiceMatrix = Matrix()
        practiceMatrix.set(bitmapRectToViewRectMatrix)

        val bitmapRect = matrixCalculator.getBitmapRect(practiceMatrix, baseBitmapWidth, baseBitmapHeight)
        val middleX = bitmapRect.left + bitmapRect.width() / 2f
        val middleY = bitmapRect.top + bitmapRect.height() / 2f
        val rotateCenterPoint = if (scale < 1f) {
            val handShakeCorrection = 1f * mmToPixel
            val isApproachTop = abs(printAreaRectBaseOnView.top - bitmapRect.top) <= handShakeCorrection
            val isApproachBottom = abs(printAreaRectBaseOnView.bottom - bitmapRect.bottom) <= handShakeCorrection
            val isApproachLeft = abs(printAreaRectBaseOnView.left - bitmapRect.left) <= handShakeCorrection
            val isApproachRight = abs(printAreaRectBaseOnView.right - bitmapRect.right) <= handShakeCorrection

            when {
                isApproachTop && isApproachLeft -> PointF(bitmapRect.left, bitmapRect.top)
                isApproachTop && isApproachRight -> PointF(bitmapRect.right, bitmapRect.top)
                isApproachBottom && isApproachLeft -> PointF(bitmapRect.left, bitmapRect.bottom)
                isApproachBottom && isApproachRight -> PointF(bitmapRect.right, bitmapRect.bottom)
                isApproachTop -> PointF(middleX, bitmapRect.top)
                isApproachBottom -> PointF(middleX, bitmapRect.bottom)
                isApproachLeft -> PointF(bitmapRect.left, middleY)
                isApproachRight -> PointF(bitmapRect.right, middleY)
                else -> PointF(middleX, middleY)
            }
        } else {
            PointF(middleX, middleY)
        }

        practiceMatrix.postScale(scale, scale, rotateCenterPoint.x, rotateCenterPoint.y)
        val afterScale = matrixCalculator.getScale(practiceMatrix)
        scaleForDebug = afterScale
        if (afterScale > MAX_ZOOM) {
            //더 이상 확대 할 수 없습니다.
            return
        }

        val afterBitmapRect = matrixCalculator.getBitmapRect(practiceMatrix, baseBitmapWidth, baseBitmapHeight)
        if (!afterBitmapRect.contains(printAreaRectBaseOnView)) {
            // 더 이상 축소 할 수 없습니다.
            return
        }

        bitmapRectToViewRectMatrix.set(practiceMatrix)
        super.setImageMatrix(bitmapRectToViewRectMatrix)
    }

    private fun applyMatrixTranslate(x: Float, y: Float) {
        val snapShotMatrix = Matrix()
        snapShotMatrix.set(bitmapRectToViewRectMatrix)

        val practiceMatrix = Matrix()

        practiceMatrix.set(bitmapRectToViewRectMatrix)
        practiceMatrix.postTranslate(x, 0f)
        val bitmapRectMoveX = matrixCalculator.getBitmapRect(practiceMatrix, baseBitmapWidth, baseBitmapHeight)
        if (bitmapRectMoveX.contains(printAreaRectBaseOnView)) {
            bitmapRectToViewRectMatrix.set(practiceMatrix)
        } else {
            practiceMatrix.set(bitmapRectToViewRectMatrix) //reset
            val moveX = run {
                val rect = matrixCalculator.getBitmapRect(practiceMatrix, baseBitmapWidth, baseBitmapHeight)
                if (x > 0) {
                    printAreaRectBaseOnView.left - rect.left
                } else {
                    printAreaRectBaseOnView.right - rect.right
                }
            }
            practiceMatrix.postTranslate(moveX, 0f)
            bitmapRectToViewRectMatrix.set(practiceMatrix)
        }

        practiceMatrix.set(bitmapRectToViewRectMatrix)
        practiceMatrix.postTranslate(0f, y)
        val bitmapRectMoveY = matrixCalculator.getBitmapRect(practiceMatrix, baseBitmapWidth, baseBitmapHeight)
        if (bitmapRectMoveY.contains(printAreaRectBaseOnView)) {
            bitmapRectToViewRectMatrix.set(practiceMatrix)
        } else {
            practiceMatrix.set(bitmapRectToViewRectMatrix) //reset
            val moveY = run {
                val rect = matrixCalculator.getBitmapRect(practiceMatrix, baseBitmapWidth, baseBitmapHeight)
                if (y > 0) {
                    printAreaRectBaseOnView.top - rect.top
                } else {
                    printAreaRectBaseOnView.bottom - rect.bottom
                }
            }
            practiceMatrix.postTranslate(0f, moveY)
            bitmapRectToViewRectMatrix.set(practiceMatrix)
        }

        if (snapShotMatrix != bitmapRectToViewRectMatrix) {
            calOffsetForDebug()
            super.setImageMatrix(bitmapRectToViewRectMatrix)
        }
    }

    private fun calOffsetForDebug() {
        if (Config.isDevelopVersion()) {
            val scaleEditor2Editor = imageEditParams.frameWidth / printAreaRectBaseOnView.width()
            val bitmapRect = matrixCalculator.getBitmapRect(bitmapRectToViewRectMatrix, baseBitmapWidth, baseBitmapHeight)
            offsetXForDebug = (bitmapRect.left - printAreaRectBaseOnView.left) * scaleEditor2Editor
            offsetYForDebug = (bitmapRect.top - printAreaRectBaseOnView.top) * scaleEditor2Editor
            widthForDebug = (bitmapRect.right - bitmapRect.left) * scaleEditor2Editor
            heightForDebug = (bitmapRect.bottom - bitmapRect.top) * scaleEditor2Editor
        }
    }

    private fun hideWarningResolutionToast() {
        warningResolutionToast?.let {
            it.cancel()
        }
    }

    private fun showWarningResolutionToast() {
        hideWarningResolutionToast()
        warningResolutionToast = Toast(context).apply {
            val binding = ToastNoPrintCustomBinding.inflate(LayoutInflater.from(context), null, false)
            view = binding.root
            duration = Toast.LENGTH_SHORT
        }
        warningResolutionToast?.show()
    }

    // https://stackoverflow.com/questions/43043176/how-to-reduce-sensitivity-of-android-scalegesturedetector-simpleonscalegestureli
    private fun gestureTolerance(detector: ScaleGestureDetector): Boolean {
        val spanDelta = abs(detector.currentSpan - detector.previousSpan)
        return spanDelta > 3
    }

    private fun checkWarningResolution() {
        val (bitmapWidth, bitmapHeight) = run {
            val bitmapRect = matrixCalculator.getBitmapRect(bitmapRectToViewRectMatrix, baseBitmapWidth, baseBitmapHeight)
            val scaleEditor2Editor = imageEditParams.frameWidth / printAreaRectBaseOnView.width()
            if (imageEditParams.edit.angle == 90 || imageEditParams.edit.angle == 270) {
                Pair(bitmapRect.height() * scaleEditor2Editor, bitmapRect.width() * scaleEditor2Editor)
            } else {
                Pair(bitmapRect.width() * scaleEditor2Editor, bitmapRect.height() * scaleEditor2Editor)
            }
        }

        isWarningResolution = imageEditParams.availableMaxWidth < bitmapWidth || imageEditParams.availableMaxHeight < bitmapHeight
        if (isWarningResolution) {
            showWarningResolutionToast()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleGestureDetector.onTouchEvent(event)
        gestureListener.onTouchEvent(event)
        return true
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        if (gestureTolerance(detector)) {
            applyMatrixScale(detector.scaleFactor)
        }
        return true
    }

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector) {
        checkWarningResolution()
    }

    override fun onDown(e: MotionEvent): Boolean {
        return false
    }

    override fun onShowPress(e: MotionEvent) {
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        return false
    }

    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
        applyMatrixTranslate(distanceX * -1, distanceY * -1)
        if (IS_SUPPORT_DYNAMIC_LOW_RES_WARNNING) checkWarningResolution()
        return true
    }

    override fun onLongPress(e: MotionEvent) {
    }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        return true
    }

    // https://futurestud.io/tutorials/glide-how-to-rotate-images
    internal class RotateTransformation(
        private val rotateRotationAngle: Int
    ) : BitmapTransformation() {
        override fun transform(
            pool: BitmapPool,
            toTransform: Bitmap,
            outWidth: Int,
            outHeight: Int
        ): Bitmap {
            val matrix = Matrix()
            matrix.postRotate(rotateRotationAngle.toFloat())
            return Bitmap.createBitmap(
                toTransform,
                0,
                0,
                toTransform.width,
                toTransform.height,
                matrix,
                true
            )
        }

        override fun updateDiskCacheKey(messageDigest: MessageDigest) {
            messageDigest.update("rotate$rotateRotationAngle".toByteArray())
        }
    }
}