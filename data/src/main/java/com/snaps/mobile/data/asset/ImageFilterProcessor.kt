package com.snaps.mobile.data.asset

import android.content.Context
import android.graphics.*
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicConvolve3x3
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.snaps.common.R
import com.snaps.common.android_utils.SchedulerProvider
import com.snaps.common.utils.log.Dlog
import com.snaps.mobile.domain.save.Filter
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.toObservable
import java.security.MessageDigest
import javax.inject.Inject


class ImageFilterProcessor @Inject constructor(
    private val context: Context,
    private val imageFilterFileStorage: ImageFilterFileStorage,
    private val schedulerProvider: SchedulerProvider,
) {
    fun cleanUp() {
        imageFilterFileStorage.cleanUp()
    }

    fun getFilteredImage(uriText: String, filter: Filter): String? {
        return imageFilterFileStorage.getFileUri(uriText, filter.name) ?: run {
            loadBitmap(uriText)?.let {
                applyFilter(it, filter).let { resultBitmap ->
                    imageFilterFileStorage.save(uriText, resultBitmap, filter.name).also {
                        resultBitmap.recycle()
                    }
                }
            }
        }
    }

    private fun savePreviewFilteredImage(uriText: String, filter: Filter, bitmap: Bitmap): String {
        return imageFilterFileStorage.getPreviewFileUri(uriText, filter.name) ?: run {
            applyFilter(bitmap, filter).let { resultBitmap ->
                (imageFilterFileStorage.savePriview(uriText, resultBitmap, filter.name) ?: "").also {
                    resultBitmap.recycle()
                }
            }
        }
    }

    private fun rxSavePreviewFilteredImage(uriText: String, filter: Filter, bitmap: Bitmap): Single<Pair<Filter, String>> {
        return Single.fromCallable { savePreviewFilteredImage(uriText, filter, bitmap) }
            .subscribeOn(schedulerProvider.io)
            .map {
                filter to it
            }
    }

    private fun loadBitmap(uriText: String, orientationAngle: Int = 0, size: Int = 0): Bitmap? {
        return try {
            Glide.with(context)
                .asBitmap()
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .apply {
                    if (size > 0) override(size)
                    if (orientationAngle != 0) transform(RotateTransformation(orientationAngle))
                }
                .load(uriText)
                .submit()
                .get()
        } catch (e: Exception) {
            Dlog.e(e)
            null
        }
    }

    private class RotateTransformation(
        private val rotateRotationAngle: Int
    ) : BitmapTransformation() {
        private val id = "ImageFilterProcessor.RotateTransformation".toByteArray()

        override fun transform(
            pool: BitmapPool,
            toTransform: Bitmap,
            outWidth: Int,
            outHeight: Int
        ): Bitmap {
            return Bitmap.createBitmap(
                toTransform,
                0,
                0,
                toTransform.width,
                toTransform.height,
                Matrix().apply {
                    postRotate(rotateRotationAngle.toFloat())
                },
                true
            )
        }

        override fun equals(o: Any?) = o is RotateTransformation
        override fun hashCode() = id.hashCode()
        override fun updateDiskCacheKey(messageDigest: MessageDigest) = messageDigest.update("$id$rotateRotationAngle".toByteArray())
    }


    private fun getFilterList(): MutableList<Filter> {
        return mutableListOf<Filter>().apply {
            add(Filter.None())
            addAll(Filter.getList())
        }
    }

    fun rxGetPreviewFilteredImages(uriText: String, orientationAngle: Int, size: Int): Single<Map<Filter, String?>> {
        return Single.fromCallable { loadBitmap(uriText, orientationAngle, size) }
            .subscribeOn(schedulerProvider.io)
            .flatMap { bitmap ->
                bitmap?.let {
                    getFilterList().toObservable()
                        .flatMapSingle { filter ->
                            rxSavePreviewFilteredImage(uriText, filter, it)
                        }.toList().map { list ->
                            list.toMap()
                        }
                } ?: Single.just(getFilterList().map { it to null }.toMap())
            }.onErrorReturn {
                getFilterList().map { it to null }.toMap()
            }
    }

    private fun copyBgWhiteBitmap(bitmap: Bitmap): Bitmap {
        return Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888).apply {
            Canvas(this).apply {
                drawColor(Color.WHITE)  //아놔! 필터의 비밀을 알지만 기록 할 수가 없다...
                drawBitmap(bitmap, 0f, 0f, null)
            }
        }
    }

    private fun applyFilter(bitmap: Bitmap, filter: Filter): Bitmap {
        return when (filter) {
            is Filter.Warm, is Filter.Aurora -> copyBgWhiteBitmap(bitmap)
            else -> bitmap.copy(Bitmap.Config.ARGB_8888, true)
        }.let { copyBitmap ->
            when (filter) {
                is Filter.None -> copyBitmap.copy(bitmap.config, true)
                is Filter.Sharpen -> createSharpen(copyBitmap)
                is Filter.Sephia -> createSephia(copyBitmap)
                is Filter.GrayScale -> createGrayscale(copyBitmap)
                is Filter.OldLight -> createOldLight(copyBitmap)
                is Filter.Vintage -> createVintage(copyBitmap)
                is Filter.Winter -> createWinter(copyBitmap)
                is Filter.Warm -> createWarm(copyBitmap)
                is Filter.Aurora -> createAurora(copyBitmap)
                is Filter.Amerald -> createAmerald(copyBitmap)
            }.also {
                copyBitmap.recycle()
            }
        }
    }

    //이건 건드리지 말아야지
    private fun createSharpen(inputBitmap: Bitmap): Bitmap {
        val outputBitmap = Bitmap.createBitmap(inputBitmap)

        val sharpenValue = -0.13f //낮을수록 밝아진다 높을수록 어두워 진다(선명해진다) 1.5 기준
        val sharpenPointValue = 2.0f //낮을수록 어두워진다 높을수록 밝아진다 2.2 기준
        val sharp = floatArrayOf(
            sharpenValue, sharpenValue, sharpenValue,
            sharpenValue, sharpenPointValue, sharpenValue,
            sharpenValue, sharpenValue, sharpenValue
        )
        val rs = RenderScript.create(context)
        val allocIn = Allocation.createFromBitmap(rs, inputBitmap)
        val allocOut = Allocation.createFromBitmap(rs, outputBitmap)

        val convolution = ScriptIntrinsicConvolve3x3.create(rs, Element.U8_4(rs))
        convolution.setInput(allocIn)
        convolution.setCoefficients(sharp)
        convolution.forEach(allocOut)
        allocOut.copyTo(outputBitmap)
        rs.destroy()

        return outputBitmap
    }

    private fun createSephiaColorMatrix(): ColorMatrix {
        return ColorMatrix().apply {
            setSaturation(.3f)
            postConcat(
                ColorMatrix().apply {
                    array[0] = 1.4f //red
                    array[6] = 1.2f //green
                    array[12] = 1.1f //blue
                }
            )
        }
    }

    private fun createSephia(inputBitmap: Bitmap): Bitmap {
        return Bitmap.createBitmap(inputBitmap).apply {
            Canvas(this).drawBitmap(
                inputBitmap,
                0f,
                0f,
                Paint().apply {
                    colorFilter = ColorMatrixColorFilter(createSephiaColorMatrix())
                }
            )
        }
    }

    private fun createGrayscale(inputBitmap: Bitmap): Bitmap {
        return Bitmap.createBitmap(inputBitmap).apply {
            Canvas(this).drawBitmap(
                inputBitmap,
                0f,
                0f,
                Paint().apply {
                    colorFilter = ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0f) })
                }
            )
        }
    }

    private fun createOldLight(inputBitmap: Bitmap): Bitmap {
        return createBland(
            inputBitmap = inputBitmap,
            filterBitmap = loadResource(R.drawable.img_filter_old_light),
            mode = PorterDuff.Mode.LIGHTEN,
            alphaPercentage = 100
        )
    }

    private fun createVintage(inputBitmap: Bitmap): Bitmap {
        return createBland(
            inputBitmap = inputBitmap,
            filterBitmap = loadResource(R.drawable.img_filter_vintage),
            mode = PorterDuff.Mode.SCREEN,
            alphaPercentage = 100
        )
    }

    private fun createWinter(inputBitmap: Bitmap): Bitmap {
        return createBland(
            inputBitmap = inputBitmap,
            filterBitmap = loadResource(R.drawable.img_filter_winter),
            mode = PorterDuff.Mode.SCREEN,
            alphaPercentage = 100
        )
    }

    private fun createWarm(inputBitmap: Bitmap): Bitmap {
        return createBland(
            inputBitmap = inputBitmap,
            filterBitmap = loadResource(R.drawable.img_filter_warm),
            mode = PorterDuff.Mode.MULTIPLY,
            alphaPercentage = 100
        )
    }

    private fun createAurora(inputBitmap: Bitmap): Bitmap {
        return createBland(
            inputBitmap = inputBitmap,
            filterBitmap = loadResource(R.drawable.img_filter_aurora),
            mode = PorterDuff.Mode.MULTIPLY,
            alphaPercentage = 65
        )
    }

    private fun createAmerald(inputBitmap: Bitmap): Bitmap {
        return createBland(
            inputBitmap = inputBitmap,
            filterBitmap = loadResource(R.drawable.img_filter_emeralde),
            mode = PorterDuff.Mode.SCREEN,
            alphaPercentage = 100
        )
    }

    private fun loadResource(resId: Int): Bitmap {
        return Glide.with(context)
            .asBitmap()
            .load(resId)
            .submit()
            .get()
    }


    private fun createPorterDuffMultiplyBitmap(inputBitmap: Bitmap, blandBitmap: Bitmap): Bitmap {
        return Bitmap.createBitmap(inputBitmap).apply {
            Canvas(this).drawRect(
                0f,
                0f,
                width.toFloat(),
                height.toFloat(),
                Paint().apply {
                    xfermode = PorterDuffXfermode(PorterDuff.Mode.OVERLAY)
                    shader = BitmapShader(blandBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
                }
            )
        }
    }

    private fun createBland(
        inputBitmap: Bitmap,
        filterBitmap: Bitmap,
        mode: PorterDuff.Mode,
        alphaPercentage: Int
    ): Bitmap {
        return createBlandBitmap(
            inputBitmap = inputBitmap,
            filterBitmap = filterBitmap,
            mode = mode,
            alphaPercentage = alphaPercentage
        ).let { blandBitmap ->
            when (mode) {
                PorterDuff.Mode.MULTIPLY -> {
                    createPorterDuffMultiplyBitmap(inputBitmap = inputBitmap, blandBitmap = blandBitmap).also {
                        blandBitmap.recycle()
                    }
                }
                else -> blandBitmap
            }
        }
    }

    private fun createBlandBitmap(
        inputBitmap: Bitmap,
        filterBitmap: Bitmap,
        mode: PorterDuff.Mode,
        alphaPercentage: Int
    ): Bitmap {
        return Bitmap.createBitmap(inputBitmap).apply {
            Bitmap.createScaledBitmap(filterBitmap, inputBitmap.width, inputBitmap.height, false).let { scaleFilterBitmap ->
                Canvas(this).drawRect(
                    0f,
                    0f,
                    width.toFloat(),
                    height.toFloat(),
                    Paint().apply {
                        alpha = (255f * alphaPercentage.toFloat() / 100f).toInt()
                        xfermode = PorterDuffXfermode(mode)
                        shader = BitmapShader(scaleFilterBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
                    }
                )
                scaleFilterBitmap.recycle()
            }
        }
    }
}