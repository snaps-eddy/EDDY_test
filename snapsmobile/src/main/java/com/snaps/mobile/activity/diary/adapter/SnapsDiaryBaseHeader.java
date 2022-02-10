package com.snaps.mobile.activity.diary.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.thread.ATask;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.SnapsDiaryCommonUtils;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;
import com.snaps.mobile.activity.diary.interfaces.ISnapsDiaryHeaderClickListener;
import com.snaps.mobile.activity.diary.interfaces.ISnapsDiaryHeaderStrategy;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryListInfo;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryUserInfo;

import java.io.File;
import java.io.FileOutputStream;

import static com.snaps.common.utils.imageloader.ImageLoader.DIARY_SMALL_CACHE_SIZE;

/**
 * Created by ysjeong on 16. 3. 31..
 */
public class SnapsDiaryBaseHeader implements ISnapsDiaryHeaderStrategy {
    private static final String TAG = SnapsDiaryBaseHeader.class.getSimpleName();
//    protected SnapsImageLoader imageLoader = SnapsImageLoader.getInstance();
    protected ISnapsDiaryHeaderClickListener headerClickListener;
    protected Context context;
    protected int shape;

    protected ImageView ivThumbnail;
    protected ImageView ivThumbnailMask;

    public SnapsDiaryBaseHeader(Context context, int shape, ISnapsDiaryHeaderClickListener stripListener) {
        this.context = context;
        this.headerClickListener = stripListener;
        this.shape = shape;
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(ViewGroup parent) {
        return null;
    }

    @Override
    public void refreshThumbnail() {
        if (ivThumbnail == null) return;
        SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
        SnapsDiaryUserInfo userInfo = dataManager.getSnapsDiaryUserInfo();
        if (userInfo == null) return;

        String path = userInfo.getThumbnailPath();

        if(path != null && path.length() > 0) {
            ivThumbnailMask.setVisibility(View.VISIBLE);
            Bitmap bmThumbnail = null;

            try {
                bmThumbnail = getThumbnailBitmapFromCacheFile(path);
            } catch (OutOfMemoryError e) {
                Dlog.e(TAG, e);
            };

            if (bmThumbnail != null) {
                ivThumbnail.setImageBitmap(bmThumbnail);
            } else {
                ImageLoader.with(context).load(SnapsAPI.DOMAIN(false) + path).override(DIARY_SMALL_CACHE_SIZE, DIARY_SMALL_CACHE_SIZE).into(ivThumbnail);

                createCacheFile(SnapsAPI.DOMAIN(false) + path);
            }
        } else {
            ivThumbnail.setImageResource(R.drawable.img_snaps_diary_default_profile);
            ivThumbnailMask.setVisibility(View.GONE);
        }
    }

    @Override
    public void setHeaderInfo(final RecyclerView.ViewHolder holder) {
        if(holder == null || !(holder instanceof SnapsBaseHeaderHolder)) return;
        SnapsBaseHeaderHolder baseHeaderHolder = (SnapsBaseHeaderHolder) holder;
        SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();

        ivThumbnail = baseHeaderHolder.getIvThumbnail();
        ivThumbnailMask = baseHeaderHolder.getIvThumbnailMask();

        refreshThumbnail();

        baseHeaderHolder.getIvThumbnail().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showThumbnailChangePopup();
            }
        });

        String userName = Setting.getString(context, Const_VALUE.KEY_USER_INFO_USER_NAME);
        if (userName.trim().length() > 0) {
            baseHeaderHolder.getTvUserName().setText(userName);
            baseHeaderHolder.getTvUserName().setVisibility(View.VISIBLE);
        } else {
            baseHeaderHolder.getTvUserName().setVisibility(View.GONE);
        }

        ImageButton btnList = baseHeaderHolder.getBtnList();
        ImageButton btnGrid = baseHeaderHolder.getBtnGrid();

        SnapsDiaryListInfo listInfo = dataManager.getListInfo();
        if (listInfo.isEmptyDiaryList()) {
            btnList.setEnabled(false);
            btnGrid.setEnabled(false);
            btnList.setImageResource(R.drawable.img_diary_header_list);
            btnGrid.setImageResource(R.drawable.img_diary_header_grid);
        } else {
            btnList.setEnabled(true);
            btnGrid.setEnabled(true);
            switch (shape) {
                case SnapsDiaryBaseAdapter.SHAPE_LIST :
                    btnList.setImageResource(R.drawable.img_diary_header_list_focus);
                    btnGrid.setImageResource(R.drawable.img_diary_header_grid);
                    btnGrid.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (headerClickListener != null)
                                headerClickListener.onStripClick((shape + 1) % 2);
                        }
                    });
                    break;
                case SnapsDiaryBaseAdapter.SHAPE_GRID :
                    btnGrid.setImageResource(R.drawable.img_diary_header_grid_focus);
                    btnList.setImageResource(R.drawable.img_diary_header_list);
                    btnList.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (headerClickListener != null) {
                                headerClickListener.onStripClick((shape + 1) % 2);
                            }
                        }
                    });
                    break;
            }
        }
    }

    protected void showThumbnailChangePopup() {
        if(headerClickListener != null)
            headerClickListener.onThumbnailClick(ivThumbnail);
    }

    @Override
    public void destoryView() {}

    private Bitmap getThumbnailBitmapFromCacheFile(String path) {
        if(path == null || !path.contains("/")) return null;

        SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
        SnapsDiaryUserInfo userInfo = dataManager.getSnapsDiaryUserInfo();
        if(userInfo == null) return null;

        Bitmap bm = userInfo.getThumbnailCache();
        if(bm != null && !bm.isRecycled()) return bm;

        String fileName = path.substring(path.lastIndexOf("/") + 1);
        String filePath = SnapsDiaryCommonUtils.getUserProfileCacheFilePath(context) + fileName;
        File file = new File(filePath);
        if(file.exists()) {
            bm = BitmapFactory.decodeFile(file.getAbsolutePath());
            if (bm != null && !bm.isRecycled()) {
                userInfo.setThumbnailCache(bm);
            }
        }

        return bm;
    }

    private void createCacheFile(final String url) {
        ATask.executeVoid(new ATask.OnTask() {
            @Override
            public void onPre() {
            }

            @Override
            public void onBG() {
                try {
                    String fileName = url.substring(url.lastIndexOf("/") + 1);
                    String outputPath = SnapsDiaryCommonUtils.getUserProfileCacheFilePath(context) + fileName;

                    Bitmap baseBitmap = ImageLoader.syncLoadBitmap(url, DIARY_SMALL_CACHE_SIZE, DIARY_SMALL_CACHE_SIZE, 0);

                    File tempSavePath = new File(SnapsDiaryCommonUtils.getUserProfileCacheFilePath(context));
                    // thumb path 폴더가 없으면 만든다.
                    if (!tempSavePath.exists())
                        tempSavePath.mkdirs();

                    tempSavePath = new File(outputPath);

                    if (!tempSavePath.exists()) {
                        tempSavePath.createNewFile();
                        tempSavePath.setWritable(true);
                        tempSavePath.setReadable(true);

                        FileOutputStream fos = new FileOutputStream(tempSavePath);
                        try {
                            baseBitmap.compress(Bitmap.CompressFormat.PNG, 95, fos);
                        } catch (Exception e) {
                            Dlog.e(TAG, e);
                        } finally {
                            if(fos != null)
                                fos.close();
                        }
                    }

                    baseBitmap.recycle();
                    baseBitmap = null;

                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }

            @Override
            public void onPost() {
            }
        });
    }
}
