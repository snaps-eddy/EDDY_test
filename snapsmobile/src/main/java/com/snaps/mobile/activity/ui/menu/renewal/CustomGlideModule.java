package com.snaps.mobile.activity.ui.menu.renewal;

import android.content.Context;
import android.graphics.drawable.PictureDrawable;
import android.os.Build;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.ExternalPreferredCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.load.engine.executor.GlideExecutor;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.caverock.androidsvg.SVG;

import java.io.InputStream;

/**
 * Created by songhw on 2016. 8. 9..
 */
@GlideModule
public class CustomGlideModule extends AppGlideModule {

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        // https://github.com/bumptech/glide/blob/master/samples/svg/src/main/java/com/bumptech/glide/samples/svg/SvgModule.java
        // SVG 사용을 위한 컴포넌트 추가.
        registry
                .register(SVG.class, PictureDrawable.class, new SvgDrawableTranscoder())
                .append(InputStream.class, SVG.class, new SvgDecoder());

        // Usage
//        ImageView iv_svg_test = findViewById(R.id.iv_svg_test);
//        GlideApp.with(getApplicationContext)
//                .as(PictureDrawable.class)
//                .listener(new SvgSoftwareLayerSetter())
//                .load(SnapsAPI.DOMAIN() + "/Upload/Data1/Resource/scene_mask/keyring-heart.svg")
//                .into(iv_svg_test);
    }

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        RequestOptions requestOptions = null;

        if (Build.VERSION.SDK_INT >= 26) {
            requestOptions = new RequestOptions().format(DecodeFormat.PREFER_ARGB_8888).disallowHardwareConfig();
        } else {
            requestOptions = new RequestOptions().format(DecodeFormat.PREFER_RGB_565).disallowHardwareConfig();
        }

        builder.setDefaultRequestOptions(requestOptions);

        int diskCacheSizeBytes = 1024 * 1024 * 300; // 100 MB
        builder.setDiskCache(new ExternalPreferredCacheDiskCacheFactory(context, "cache", diskCacheSizeBytes));

        MemorySizeCalculator screenCacheCalculator = new MemorySizeCalculator.Builder(context)
                .setMemoryCacheScreens(2) //2의 의미가 뭔지 모르겠다
                .build();

        builder.setMemoryCache(new LruResourceCache(screenCacheCalculator.getMemoryCacheSize()));

        MemorySizeCalculator bitmapPoolCalculator = new MemorySizeCalculator.Builder(context)
                .setBitmapPoolScreens(3) //3의 의미가 뭔지 모르겠다
                .build();
        builder.setBitmapPool(new LruBitmapPool(bitmapPoolCalculator.getBitmapPoolSize()));

        final GlideExecutor.UncaughtThrowableStrategy myUncaughtThrowableStrategy = GlideExecutor.UncaughtThrowableStrategy.LOG;
        builder.setDiskCacheExecutor(GlideExecutor.newDiskCacheExecutor(myUncaughtThrowableStrategy));
        builder.setResizeExecutor(GlideExecutor.newSourceExecutor(myUncaughtThrowableStrategy));
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}
