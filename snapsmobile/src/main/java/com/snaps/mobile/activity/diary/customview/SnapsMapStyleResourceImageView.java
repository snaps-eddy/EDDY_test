package com.snaps.mobile.activity.diary.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import androidx.core.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.R;

/**
 * Created by ysjeong on 16. 4. 6..
 */
public class SnapsMapStyleResourceImageView extends ImageView {
    private static final String TAG = SnapsMapStyleResourceImageView.class.getSimpleName();
    private final int MAX_INK_CNT = 20;
    private final int COLUMN_INK_CNT = 10;
    private final int[] BASE_RES_ID_ARR = { R.drawable.img_diary_ink_part1, R.drawable.img_diary_ink_part2 };

    private Bitmap m_bmInk;

    private int m_iWidth = 0;
    private int m_iHeight = 0;

    private MapStyleBitmap mapStyleBitmap = null;

    public SnapsMapStyleResourceImageView(Context context) {
        super(context);
        init(context);
   }

    public SnapsMapStyleResourceImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SnapsMapStyleResourceImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setCroppedImage(int number) {
        createMapStyleBaseBitmap(number);

        if (mapStyleBitmap == null || mapStyleBitmap.bitmap == null)
            return;

        int offsetX = (number % COLUMN_INK_CNT) * m_iWidth;

        if(!isValidPosition(offsetX, 0)) return;

        this.setImageBitmap(null);
        if(this.getDrawable() != null)
            this.getDrawable().setCallback(null);

        if(m_bmInk != null && !m_bmInk.isRecycled()) {
            m_bmInk.recycle();
            m_bmInk = null;
        }

        try {
            m_bmInk =  Bitmap.createBitmap(mapStyleBitmap.bitmap, offsetX, 0, m_iWidth, m_iHeight);
        } catch (OutOfMemoryError e) {
            Dlog.e(TAG, e);
        }

        if (m_bmInk != null && !m_bmInk.isRecycled()) {
            this.setImageBitmap(m_bmInk);
        }
    }

    public void releaseBitmap() {
        try {
            this.setImageBitmap(null);
            if(this.getDrawable() != null)
              this.getDrawable().setCallback(null);
            if (mapStyleBitmap != null && mapStyleBitmap.bitmap != null && !mapStyleBitmap.bitmap.isRecycled()) {
                mapStyleBitmap.bitmap.recycle();
                mapStyleBitmap.bitmap = null;
            }

            if (m_bmInk != null && !m_bmInk.isRecycled()) {
                m_bmInk.recycle();
                m_bmInk = null;
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        };
    }

    private boolean isValidPosition(int x, int y) {
        if (mapStyleBitmap == null || mapStyleBitmap.bitmap == null || mapStyleBitmap.bitmap.isRecycled()) return false;

        else if (x < 0 || y < 0 || x + m_iWidth > mapStyleBitmap.bitmap.getWidth() || y + m_iHeight > mapStyleBitmap.bitmap.getHeight()) return false;

        return true;
    }

    private void init(Context context) {
        this.mapStyleBitmap = new MapStyleBitmap();
    }

    private void createMapStyleBaseBitmap(int number) {
        if(number >= MAX_INK_CNT || mapStyleBitmap == null) return;
        if(mapStyleBitmap.isCreatedBitmap(number)) return;
        releaseBitmap();

        mapStyleBitmap.arrLineNumber = getBaseResIdx(number);
        int m_iBaseResId = BASE_RES_ID_ARR[mapStyleBitmap.arrLineNumber];

        try {
            BitmapDrawable ink = (BitmapDrawable) ResourcesCompat.getDrawable(getResources(), m_iBaseResId, null);
            if(ink != null && ink.getBitmap() != null && !ink.getBitmap().isRecycled()) {
                mapStyleBitmap.bitmap = ink.getBitmap().copy(Bitmap.Config.ARGB_8888, true);
                m_iWidth = mapStyleBitmap.bitmap.getWidth() / COLUMN_INK_CNT;
                m_iHeight = mapStyleBitmap.bitmap.getHeight();
            }
        } catch (OutOfMemoryError e) {
            Dlog.e(TAG, e);
        }
    }

    private int getBaseResIdx(int inkNumber) {
        return Math.min((inkNumber / COLUMN_INK_CNT), BASE_RES_ID_ARR.length - 1);
    }

    private class MapStyleBitmap {
        int arrLineNumber = 0;
        Bitmap bitmap;

        private boolean isCreatedBitmap(int number) {
            return (getBaseResIdx(number) == arrLineNumber && bitmap != null && !bitmap.isRecycled());
        }
    }
}