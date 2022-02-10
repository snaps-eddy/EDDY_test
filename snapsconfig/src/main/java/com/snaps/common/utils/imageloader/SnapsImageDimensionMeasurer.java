package com.snaps.common.utils.imageloader;

import android.graphics.Rect;
import android.os.AsyncTask;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.http.HttpUtil;

/**
 * Created by ysjeong on 2017. 8. 10..
 */

public class SnapsImageDimensionMeasurer {
    private static final String TAG = SnapsImageDimensionMeasurer.class.getSimpleName();
    private static volatile SnapsImageDimensionMeasurer gInstance = null;

    private NetworkImageDimensionMeasurer networkImageDimensionGetter = null;

    public static SnapsImageDimensionMeasurer getInstance() {
        if (gInstance ==  null) {
            gInstance = new SnapsImageDimensionMeasurer();
        }
        return gInstance;
    }

    public static NetworkImageDimensionMeasurer getNetworkImageDimensionMeasurer() {
        return getInstance().networkImageDimensionGetter;
    }

    public static NetworkImageDimensionMeasurer createNetworkImageDimensionMeasurer(String url, Rect rect) {
        getInstance().networkImageDimensionGetter = new NetworkImageDimensionMeasurer(url, rect);
        return getInstance().networkImageDimensionGetter;
    }

    public static class NetworkImageDimensionMeasurer extends
            AsyncTask<Void, Void, Void> {

        String imgUrl;
        boolean isDownloading = false;
        Rect imageRect = null;

        private NetworkImageDimensionMeasurer(String url, Rect rect) {
            imgUrl = url;
            isDownloading = true;
            imageRect = rect;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                isDownloading = true;

                Rect result = HttpUtil.getNetworkImageRect(imgUrl);
                if (imageRect != null && result != null)
                    imageRect.set(result);

                isDownloading = false;
            } catch (Exception e) {
                Dlog.e(TAG, e);
            } finally {
                isDownloading = false;
            }
            return null;
        }

        public boolean isDownloading() {
            return isDownloading;
        }
    }
}
