package com.snaps.common.utils.imageloader;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import androidx.annotation.WorkerThread;
import android.widget.ImageView;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ViewTarget;
import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.imageloader.filters.ImageFilters;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.ui.StringUtil;

import java.io.File;
import java.util.concurrent.ExecutionException;

/**
 * Created by songhw on 2016. 10. 21..
 * Glide 사용중.
 */
public class ImageLoader {
    private static final String TAG = ImageLoader.class.getSimpleName();
    public static final int DIARY_SMALL_CACHE_SIZE = 200;
    public static final int DEFALUT_CACHE_SIZE = 440; //메모리 이슈때문에 좀 줄임. 480도 에러가 많이 남..
    public static final int MAX_DOWN_SAMPLE_RATIO = 4;
    public static final Bitmap.Config DEFAULT_BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    public static final int INVALID_ANGLE = -999;

    private Context context;

    private RequestListener listener;

    private String path;
    private File file;
    private int placeholder;
    private boolean fitCenter, centerCrop, asBitmap, centerInside;
    private int width = -1, height = -1;
    private boolean isSkipMemoryCache = false;
    private int animateResId = -1;
    private int drawableResId = -1;
    private DrawableTransitionOptions drawableTransitionOptions = null;
    private Transformation<Bitmap> bitmapTransformation = null;
    private FutureTarget<Bitmap> futureTarget = null;

    public static ImageLoader with(Context context) {
        ImageLoader instance = new ImageLoader();
        instance.context = context;
        instance.isSkipMemoryCache = Setting.getBoolean(context, "do_not_use_imageCache", false);
        return instance;
    }

    public ImageLoader load(String path) {
        if (path != null) {
            if (path.startsWith("http")) {
                this.path = path;
            } else {
                this.file = new File(path);
                if (!file.exists()) {
                    this.path = path;
                }
            }
        }

        return this;
    }

    public ImageLoader load(int drawableResId) {
        if (drawableResId > 0) {
            this.drawableResId = drawableResId;
        }

        return this;
    }

    public ImageLoader bitmapTransformation(Transformation<Bitmap> bitmapTransformation) {
        this.bitmapTransformation = bitmapTransformation;
        return this;
    }

    public ImageLoader drawableTransitionOptions(DrawableTransitionOptions drawableTransitionOptions) {
        this.drawableTransitionOptions = drawableTransitionOptions;
        return this;
    }

    public ImageLoader animate(int animateResId) {
        this.animateResId = animateResId;
        return this;
    }

    public ImageLoader load(File file) {
        this.file = file;
        return this;
    }

    public ImageLoader fitCenter() {
        this.fitCenter = true;
        return this;
    }

    public ImageLoader centerCrop() {
        this.centerCrop = true;
        return this;
    }

    public ImageLoader centerInside() {
        this.centerInside = true;
        return this;
    }

    public ImageLoader placeholder(int drawableId) {
        this.placeholder = drawableId;
        return this;
    }

    public ImageLoader setListener(RequestListener listener) {
        this.listener = listener;
        return this;
    }

    public ImageLoader override(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public ImageLoader size(int size) {
        this.width = size;
        this.height = size;
        return this;
    }

    public ImageLoader asBitmap() {
        this.asBitmap = true;
        return this;
    }

    public ImageLoader asBitmap(boolean asBitmap) {
        this.asBitmap = asBitmap;
        return this;
    }

    public ImageLoader skipMemoryCache(boolean skipMemoryCache) {
        isSkipMemoryCache = skipMemoryCache;
        return this;
    }

    @WorkerThread
    public ImageLoader submit() {
        RequestOptions options = new RequestOptions()
                .skipMemoryCache(isSkipMemoryCache)
                .override(width, height)
                .disallowHardwareConfig();

        if (centerInside) {
            options = options.centerInside();
        }

        futureTarget = Glide.with(context).asBitmap().apply(options).load(getLoadTarget()).submit();
        return this;
    }

    @WorkerThread
    public Bitmap get() throws ExecutionException, InterruptedException {
        if (futureTarget == null) {
            return null;
        }

        return futureTarget.get();
    }

    private Object getLoadTarget() {
        return StringUtil.isEmpty(path) ? file : path;
    }

    public void into(Object target) {
        RequestOptions requestOption;

        // builder 종류가 다르니 분기.
        if (drawableResId > 0) {
            RequestBuilder<Drawable> drawableRequestBuilder = Glide.with(context).load(drawableResId);
            requestOption = new RequestOptions().skipMemoryCache(isSkipMemoryCache).dontAnimate();
            intoWithRequestOption(requestOption, drawableRequestBuilder, target);
        } else if (target instanceof SnapsCustomTargets || asBitmap) {
            if (StringUtil.isEmpty(path) && file == null) return;

            Object loadTarget = getLoadTarget();

            if (asBitmap) {
                requestOption = new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .skipMemoryCache(isSkipMemoryCache)
                        .dontAnimate()
                        .placeholder(new ColorDrawable(Color.argb(153, 218, 218, 218)));

                RequestBuilder<Bitmap> bitmapRequestBuilder = Glide.with(context).asBitmap().load(loadTarget);
                intoWithRequestOption(requestOption, bitmapRequestBuilder, target);
            } else {
                requestOption = new RequestOptions()
                        .skipMemoryCache(isSkipMemoryCache)
                        .dontAnimate()
                        .placeholder(new ColorDrawable(Color.argb(153, 218, 218, 218)))
                        .format(DecodeFormat.PREFER_ARGB_8888).disallowHardwareConfig();
                RequestBuilder<Bitmap> bitmapRequestBuilder = Glide.with(context).asBitmap().load(loadTarget);
                intoWithRequestOption(requestOption, bitmapRequestBuilder, target);
            }
        } else if (!StringUtil.isEmpty(path)) {
            RequestBuilder<Drawable> drawableRequestBuilder = Glide.with(context).load(path);
            requestOption = new RequestOptions().skipMemoryCache(isSkipMemoryCache).dontAnimate();
            intoWithRequestOption(requestOption, drawableRequestBuilder, target);
        } else if (file != null) {
            RequestBuilder<Drawable> drawableRequestBuilder = Glide.with(context).load(file);
            requestOption = new RequestOptions().skipMemoryCache(isSkipMemoryCache).dontAnimate();
            intoWithRequestOption(requestOption, drawableRequestBuilder, target);
        }
    }

    private void intoWithRequestOption(RequestOptions requestOption, RequestBuilder requestBuilder, Object target) {

        if (drawableTransitionOptions != null) {
            requestBuilder.transition(drawableTransitionOptions);
        } else if (animateResId != -1) {
            requestBuilder.transition(GenericTransitionOptions.with(animateResId));
        }

        if (placeholder != 0) {
            requestOption.placeholder(placeholder);
        }

        if (fitCenter) {
            requestOption.fitCenter();
        } else if (centerCrop) {
            requestOption.centerCrop();
        }

        if (width > -1 && height > -1) {
            requestOption.override(width, height);
        }

        if (bitmapTransformation != null) {
            requestOption.transform(bitmapTransformation);
        }

        //TODO  특이한 케이스로 아래 로직이 안 먹히는 고객이 있어서 반응을 보기 위해 전 코드로 롤백시켰다..만약, 반응이 잠잠하다면 다시 코드를 원복하자.
//        if (target instanceof ISnapsImageViewTarget) {
//            ISnapsImageViewTarget snapsImageViewTarget = ((ISnapsImageViewTarget)target);
//            if (snapsImageViewTarget.getRotate() != 0) {
//                requestOption.transform(new RotateTransformation(context, snapsImageViewTarget.getRotate()));
//            }
//        }

        if (listener != null)
            requestBuilder.listener(listener);

        requestBuilder.apply(requestOption);

        if (target instanceof ImageView)
            requestBuilder.into((ImageView) target);
        else if (target instanceof SnapsCustomTargets) {
            requestBuilder.into((SnapsCustomTargets) target);
        }
    }

    public void downloadOnly(int width, int height) {
        try {
            if (getLoadTarget() != null && getLoadTarget() instanceof String) {
                String imagePath = (String) getLoadTarget();
                if (imagePath.startsWith("/"))
                    imagePath = SnapsAPI.DOMAIN() + imagePath;
                Glide.with(context).load(imagePath).preload(width, height);
            } else
                Glide.with(context).load(getLoadTarget()).preload(width, height);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public static void clear(Context context, Object target) {
        try {
            if (target == null) return;

            if (context == null) return;

            if (context instanceof Activity) {
                if (((Activity) context).isFinishing()) {
                    return;
                }
            }

            if (target instanceof ImageView)
                Glide.with(context).clear((ImageView) target);
            else if (target instanceof SnapsCustomTargets)
                Glide.with(context).clear((SnapsCustomTargets) target);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public static void asyncDisplayImage(Context context, final String path, ViewTarget target) {
        asyncDisplayImage(context, null, path, target, -1);
    }

    public static void asyncDisplayImage(Context context, MyPhotoSelectImageData imageData, final String path, ViewTarget target, int size) {
        asyncDisplayImage(context, imageData, path, target, size, 0, 0);
    }

    public static void asyncDisplayImage(Context context, MyPhotoSelectImageData imageData, final String path, ViewTarget target, int size, int width, int height) {
        RequestBuilder<Bitmap> bitmapRequestBuilder = null;
        if (path.contains("http")) {
            bitmapRequestBuilder = Glide.with(context).asBitmap().load(path);
        } else {
            bitmapRequestBuilder = Glide.with(context).asBitmap().load(new File(path));
        }

        RequestOptions options = new RequestOptions()
                .placeholder(new ColorDrawable((imageData != null ? Color.argb(153, 218, 218, 218) : 0)))
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .skipMemoryCache(false)
                .format(DecodeFormat.PREFER_RGB_565).disallowHardwareConfig();

        if (size > 0) {
            options.override(size);
        } else if (size == 0) {
            if (imageData == null) {
                if (width != 0 && height != 0) {
                    if (width == height) {
                        options.override(DEFALUT_CACHE_SIZE);
                    } else {
                        options.override(width, height);
                    }
                }
            }

        }

        //TODO  특이한 케이스로 아래 로직이 안 먹히는 고객이 있어서 반응을 보기 위해 전 코드로 롤백시켰다..만약, 반응이 잠잠하다면 다시 코드를 원복하자.
//        if (target instanceof ISnapsImageViewTarget) {
//            ISnapsImageViewTarget snapsImageViewTarget = ((ISnapsImageViewTarget)target);
//            if (snapsImageViewTarget.getRotate() != 0) {
//                options.transform(new RotateTransformation(context, snapsImageViewTarget.getRotate()));
//            }
//        }

        bitmapRequestBuilder.apply(options).into(target);
    }

    public static void asyncDisplayImageCenterInside(Context context, MyPhotoSelectImageData imageData, final String path, ViewTarget target, int width, int height) {
        RequestBuilder<Bitmap> bitmapRequestBuilder;
        if (path.contains("http")) {
            bitmapRequestBuilder = Glide.with(context).asBitmap().load(path);
        } else {
            bitmapRequestBuilder = Glide.with(context).asBitmap().load(new File(path));
        }

        RequestOptions options = new RequestOptions()
                .placeholder(new ColorDrawable((imageData != null ? Color.argb(153, 218, 218, 218) : 0)))
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .skipMemoryCache(false)
                .centerInside()
                .override(width, height)
                .format(DecodeFormat.PREFER_RGB_565).disallowHardwareConfig();

        bitmapRequestBuilder.apply(options).into(target);
    }

    public static void asyncDisplayCircleCropImage(Activity activity, final String path, ImageView view) {
        if (activity == null || activity.isFinishing()) return;

        try {
            RequestBuilder<Bitmap> bitmapRequestBuilder = null;
            if (path.contains("http")) {
                bitmapRequestBuilder = Glide.with(activity).asBitmap().load(path);
            } else {
                bitmapRequestBuilder = Glide.with(activity).asBitmap().load(new File(path));
            }
            RequestOptions options = new RequestOptions().circleCrop();
            bitmapRequestBuilder.apply(options).into(view);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private static boolean isValidDrawable(Drawable drawable) {
        if (drawable != null && !(drawable instanceof BitmapDrawable)) return false;
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        return bitmapDrawable != null && bitmapDrawable.getBitmap() != null && !bitmapDrawable.getBitmap().isRecycled();
    }

    public synchronized static Bitmap syncLoadBitmap(final String uri, int width, int height, int angle) {
        return SnapsImageDownloader.sycnLoadImage(uri, width, height, angle);
    }

    public static Bitmap loadImageSyncFromUri(Context context, String uri, int angle) {
        if (context == null) return null;
        return ImageLoader.syncLoadBitmap(uri, ImageFilters.getImageEditPreviewBitmapSize(context), ImageFilters.getImageEditPreviewBitmapSize(context), angle);
    }


    private static Bitmap drawableToBitmap(Drawable drawable) throws Exception {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static void clearMemory(final Context context) {
        Glide glide = Glide.get(context);
        if (glide != null)
            glide.clearMemory();
    }
}
