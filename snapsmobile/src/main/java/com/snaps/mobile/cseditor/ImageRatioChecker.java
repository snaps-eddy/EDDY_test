package com.snaps.mobile.cseditor;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.image.AsyncTask;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.common.products.base.SnapsProductBaseEditorHandler;
import com.snaps.mobile.activity.ui.menu.renewal.GlideApp;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ImageRatioChecker {
    private static final String TAG = ImageRatioChecker.class.getSimpleName();

    public ImageRatioChecker() {
    }

    public void show(Activity activity, SnapsTemplate snapsTemplate) {
        show(activity, snapsTemplate, null);
    }

    public void show(Activity activity, SnapsTemplate snapsTemplate, SnapsProductBaseEditorHandler snapsProductBaseEditorHandler) {
        if (!Config.isDevelopVersion()) return;

        if (!Config.isFromCart()) {
            makeToast(activity, "장바구니 상품만 가능", Color.RED, Toast.LENGTH_SHORT).show();
            return;
        }

        ImageRatioCheckDialog imageRatioCheckDialog = new ImageRatioCheckDialog(activity, snapsTemplate, snapsProductBaseEditorHandler);
        imageRatioCheckDialog.show();
    }

    private Toast makeToast(Context context, String message,int bgColor, int length) {
        Toast toast = Toast.makeText(context, message, length);
        toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            View toastView = toast.getView();
            TextView textViewMessage = toastView.findViewById(android.R.id.message);
            if (textViewMessage == null) return toast;

            GradientDrawable gd = new GradientDrawable();
            gd.setColor(bgColor);
            gd.setCornerRadius(10);
            toastView.setBackground(gd);

            textViewMessage.setPadding(10,0,10,0);
            textViewMessage.setTextColor(Color.BLACK);
            textViewMessage.setGravity(Gravity.CENTER);
        }
        return toast;
    }

    private static class ImageInfo {
        public String mOrgUrl;
        public String mThumbUrl;
        public int mTemplateImgWidth;
        public int mTemplateImgHeight;
        public int mImgWidth;
        public int mImgHeight;
        public MyPhotoSelectImageData mMyPhotoSelectImageData;

        public ImageInfo(String orgUrl, String thumbUrl ,int templateImgWidth, int templateImgHeight, MyPhotoSelectImageData myPhotoSelectImageData) {
            mOrgUrl = orgUrl;
            mThumbUrl = thumbUrl;
            mTemplateImgWidth = templateImgWidth;
            mTemplateImgHeight = templateImgHeight;
            mMyPhotoSelectImageData = myPhotoSelectImageData;
            mImgWidth = 0;
            mImgHeight = 0;
        }
    }

    private class ImageRatioCheckDialog extends Dialog implements View.OnClickListener {
        private Context mContext;
        private SnapsTemplate mSnapsTemplate;
        private SnapsProductBaseEditorHandler mSnapsProductBaseEditorHandler;

        private List<ImageInfo> mImageInfoList;
        private List<ImageInfo> mRatioErrorImageInfoList;
        private int mCurrentIndex;
        private Button mButtonScan;
        private Button mButtonFix;
        private Button mButtonFixAll;
        private Button mButtonPre;
        private Button mButtonNext;
        private Button mButtonGoPage;
        private Button mButtonClose;
        private ImageView mImageViewPreview;
        private TextView mTextViewTotalCount;
        private TextView mTextViewRatioErrorCount;
        private TextView mTextViewCurrentIndex;
        private TextView mTextViewTotalIndex;
        private TextView mTextViewXmlImageInfo;
        private TextView mTextViewRealImageInfo;
        private TextView mTextViewServerFileName;
        private TextView mTextViewLocalFileName;

        public ImageRatioCheckDialog(Context context, SnapsTemplate snapsTemplate, SnapsProductBaseEditorHandler snapsProductBaseEditorHandler) {
            super(context);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_cs_image_check);

            mContext = context;
            mSnapsTemplate = snapsTemplate;
            mSnapsProductBaseEditorHandler = snapsProductBaseEditorHandler;

            mButtonScan = findViewById(R.id.button_scan);
            mButtonFix = findViewById(R.id.button_fix);
            mButtonFixAll = findViewById(R.id.button_fix_all);
            mButtonPre = findViewById(R.id.button_image_pre);
            mButtonNext = findViewById(R.id.button_image_next);
            mButtonGoPage = findViewById(R.id.button_go_page);
            mButtonClose = findViewById(R.id.button_close);

            mButtonScan.setOnClickListener(this);
            mButtonFix.setOnClickListener(this);
            mButtonFixAll.setOnClickListener(this);
            mButtonPre.setOnClickListener(this);
            mButtonNext.setOnClickListener(this);
            mButtonGoPage.setOnClickListener(this);
            mButtonClose.setOnClickListener(this);

            mImageViewPreview = findViewById(R.id.imageView_preview);

            mTextViewTotalCount = findViewById(R.id.textView_total_count);
            mTextViewRatioErrorCount = findViewById(R.id.textView_ratio_error_count);
            mTextViewServerFileName = findViewById(R.id.textView_server_file_name);
            mTextViewLocalFileName = findViewById(R.id.textView_local_file_name);

            mTextViewCurrentIndex = findViewById(R.id.textView_current_index);
            mTextViewTotalIndex = findViewById(R.id.textView_total_index);

            mTextViewXmlImageInfo = findViewById(R.id.textView_xml_image_info);
            mTextViewRealImageInfo = findViewById(R.id.textView_real_image_info);

            setCanceledOnTouchOutside(false);

            initUI();
        }

        private void initUI() {
            mTextViewCurrentIndex.setText("0");
            mButtonFix.setEnabled(false);
            mButtonFixAll.setEnabled(false);
            mButtonPre.setEnabled(false);
            mButtonGoPage.setEnabled(false);
            mButtonNext.setEnabled(false);

            mButtonGoPage.setText("Go Page");

            mImageViewPreview.setImageResource(0);
            mTextViewTotalCount.setText("0");
            mTextViewRatioErrorCount.setText("0");
            mTextViewServerFileName.setText("");
            mTextViewLocalFileName.setText("");
            mTextViewCurrentIndex.setText("");
            mTextViewTotalIndex.setText("");
            mTextViewXmlImageInfo.setText("");
            mTextViewRealImageInfo.setText("");
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.button_scan) {
                scan();
            }
            else if (id == R.id.button_fix) {
                fix();
            }
            else if (id == R.id.button_fix_all) {
                fixAll();
            }
            else if (id == R.id.button_image_pre) {
                mCurrentIndex--;
                if (mCurrentIndex < 0) {
                    mCurrentIndex = mRatioErrorImageInfoList.size() - 1;
                }
                showImage();
            }
            else if (id == R.id.button_image_next) {
                mCurrentIndex++;
                if (mCurrentIndex == mRatioErrorImageInfoList.size()) {
                    mCurrentIndex = 0;
                }
                showImage();
            }
            else if (id == R.id.button_go_page) {
                if (mSnapsProductBaseEditorHandler != null) {
                    ImageInfo imageInfo = mRatioErrorImageInfoList.get(mCurrentIndex);
                    mSnapsProductBaseEditorHandler.setPageCurrentItem_forCS(imageInfo.mMyPhotoSelectImageData.pageIDX, false);
                }
            }
            else if (id == R.id.button_close) {
                this.dismiss();
            }
        }

        private class ScanAsyncTask extends AsyncTask<Void, String, Void> {
            private ProgressDialog mProgressDialog;

            public ScanAsyncTask() {
            }

            @Override
            protected void onPreExecute() {
                mProgressDialog = new ProgressDialog(mContext);
                mProgressDialog.show();
                initUI();
            }

            @Override
            protected Void doInBackground(Void... arg0) {
                mRatioErrorImageInfoList = new ArrayList<>();
                mImageInfoList = exportImageInfo();

                RequestOptions options = new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .skipMemoryCache(false)
                        .format(DecodeFormat.PREFER_RGB_565).disallowHardwareConfig();

                int index = 0;
                for(ImageInfo imageInfo: mImageInfoList) {

                    index++;
                    publishProgress("이미지 분석 중 : " + index + " / " + mImageInfoList.size() + "\n\n" + imageInfo.mOrgUrl);

                    Point point = getUrlImgWidthAndHeight(imageInfo.mOrgUrl);
                    if (point.x == 0 || point.y == 0) continue;

                    imageInfo.mImgWidth = point.x;
                    imageInfo.mImgHeight = point.y;

                    //TEST
                    //mRatioErrorImageInfoList.add(imageInfo);

                    if (imageInfo.mImgWidth != imageInfo.mTemplateImgWidth ||
                            imageInfo.mImgHeight != imageInfo.mTemplateImgHeight)
                    {
                        Glide.with(mContext).load(imageInfo.mThumbUrl).apply(options);  //미리 다운로드 받는다.
                        mRatioErrorImageInfoList.add(imageInfo);
                    }
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(String... progress) {
                mProgressDialog.setMessage("" + progress[0]);
            }

            @Override
            protected void onPostExecute(Void result) {
                mProgressDialog.dismiss();

                mTextViewTotalCount.setText("" + mSnapsTemplate.myphotoImageList.size());
                mTextViewRatioErrorCount.setText("" + mRatioErrorImageInfoList.size());

                if (mSnapsTemplate.myphotoImageList.size() != mImageInfoList.size()) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                    alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alert.setMessage("서버에서 모든 이미지 정보를 구하지 못했습니다.\n[원본 사진이 사용자 프로필 이미지인 경우 원본 사진 경로가 없을 수 있습니다.]");
                    alert.show();
                }

                if (mRatioErrorImageInfoList.size() == 0) {
                    showNoError();
                    return;
                }

                mTextViewTotalIndex.setText("" + mRatioErrorImageInfoList.size());

                mCurrentIndex = 0;

                mButtonFix.setEnabled(true);
                mButtonFixAll.setEnabled(true);
                mButtonPre.setEnabled(true);
                mButtonNext.setEnabled(true);

                if (mSnapsProductBaseEditorHandler != null) {
                    mButtonGoPage.setEnabled(true);
                }

                showImage();
            }
        }

        private void showNoError() {
            Toast toast = Toast.makeText(mContext, "\uD83D\uDC4D", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                View toastView = toast.getView();
                TextView textViewMessage = toastView.findViewById(android.R.id.message);
                textViewMessage.setTextSize(120);

                GradientDrawable gd = new GradientDrawable();
                gd.setColor(Color.TRANSPARENT);
                toastView.setBackground(gd);
            }
            toast.show();
        }

        private void scan() {
            ScanAsyncTask scanAsyncTask = new ScanAsyncTask();
            scanAsyncTask.execute();
        }

        private void fix() {
            ImageInfo imageInfo = mRatioErrorImageInfoList.get(mCurrentIndex);
            imageInfo.mMyPhotoSelectImageData.F_IMG_WIDTH = "" + imageInfo.mImgWidth;
            imageInfo.mMyPhotoSelectImageData.F_IMG_HEIGHT = "" + imageInfo.mImgHeight;

            makeToast(mContext, "수정 완료\n장바구니 저장 필요", Color.CYAN, Toast.LENGTH_SHORT).show();
        }

        private void fixAll() {
            for(ImageInfo imageInfo : mRatioErrorImageInfoList) {
                imageInfo.mMyPhotoSelectImageData.F_IMG_WIDTH = "" + imageInfo.mImgWidth;
                imageInfo.mMyPhotoSelectImageData.F_IMG_HEIGHT = "" + imageInfo.mImgHeight;
            }

            makeToast(mContext, "" + mRatioErrorImageInfoList.size() + "개 수정 완료\n장바구니 저장 필요", Color.CYAN, Toast.LENGTH_SHORT).show();
        }

        private void showImage() {
            mTextViewCurrentIndex.setText("" + (mCurrentIndex + 1));

            ImageInfo imageInfo = mRatioErrorImageInfoList.get(mCurrentIndex);

            String serverFileName = imageInfo.mOrgUrl.substring(imageInfo.mOrgUrl.lastIndexOf('/')+1, imageInfo.mOrgUrl.length());
            mTextViewServerFileName.setText("Server : " + serverFileName);

            mTextViewLocalFileName.setText("Local : " + imageInfo.mMyPhotoSelectImageData.F_IMG_NAME);

            mTextViewXmlImageInfo.setText("" + imageInfo.mTemplateImgWidth + " x " + imageInfo.mTemplateImgHeight);
            mTextViewRealImageInfo.setText("" + imageInfo.mImgWidth + " x " + imageInfo.mImgHeight);

            if (mSnapsProductBaseEditorHandler != null) {
                mButtonGoPage.setText("Go " + imageInfo.mMyPhotoSelectImageData.pageIDX + " Page");
            }

            Glide.with(mContext).load(imageInfo.mThumbUrl).into(mImageViewPreview);

            StringBuilder sb = new StringBuilder();
            sb.append("showImage()").append("\n");
            sb.append("pageIDX : ").append(imageInfo.mMyPhotoSelectImageData.pageIDX).append("\n");
            sb.append("thumb : ").append(imageInfo.mThumbUrl).append("\n");
            sb.append("origin : ").append(imageInfo.mOrgUrl);
            Dlog.d(sb.toString());
        }

        private List<ImageInfo> exportImageInfo() {
            List<ImageInfo> imageInfoList = new ArrayList<>();

            if (mSnapsTemplate == null) {
                Dlog.e(TAG, "snapsTemplate is null");
                return imageInfoList;
            }

            ArrayList<MyPhotoSelectImageData> myPhotoImageList = mSnapsTemplate.myphotoImageList;
            for(MyPhotoSelectImageData myPhotoSelectImageData : myPhotoImageList) {
                String orgUrlPath = myPhotoSelectImageData.ORIGINAL_PATH;
                if (orgUrlPath == null || orgUrlPath.length() == 0) {
                    Dlog.e(TAG, "myPhotoSelectImageData.ORIGINAL_PATH is null " + " >> PATH:" + myPhotoSelectImageData.PATH);
                    continue;
                }

                String thumbUrlPath = myPhotoSelectImageData.THUMBNAIL_PATH;
                if (thumbUrlPath == null || thumbUrlPath.length() == 0) {
                    Dlog.e(TAG, "myPhotoSelectImageData.THUMBNAIL_PATH is null");
                    continue;
                }

                if (myPhotoSelectImageData.F_IMG_WIDTH == null || myPhotoSelectImageData.F_IMG_WIDTH.length() == 0) {
                    Dlog.e(TAG, "myPhotoSelectImageData.F_IMG_WIDTH is null");
                    continue;
                }

                if (myPhotoSelectImageData.F_IMG_HEIGHT == null || myPhotoSelectImageData.F_IMG_HEIGHT.length() == 0) {
                    Dlog.e(TAG, "myPhotoSelectImageData.F_IMG_WIDTH is null");
                    continue;
                }

                orgUrlPath = "https://www.snaps.com" + orgUrlPath;
                thumbUrlPath = "https://www.snaps.com" + thumbUrlPath;
                int templateImgWidth = (int)Float.parseFloat(myPhotoSelectImageData.F_IMG_WIDTH);
                int templateImgHeight = (int)Float.parseFloat(myPhotoSelectImageData.F_IMG_HEIGHT);
                imageInfoList.add(new ImageInfo(orgUrlPath, thumbUrlPath, templateImgWidth, templateImgHeight, myPhotoSelectImageData));
            }

            return imageInfoList;
        }


        private Point getUrlImgWidthAndHeight(String urlText) {
            Point point = new Point();

            byte[] response = null;
            InputStream in = null;
            ByteArrayOutputStream out = null;
            try {
                URL url = new URL(urlText);
                in = new BufferedInputStream(url.openStream());
                out = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                int n = 0;
                while (-1 != (n = in.read(buf))) {
                    out.write(buf, 0, n);
                }
                out.flush();
                response = out.toByteArray();
            }catch (Exception e) {
                Dlog.e(TAG, e);
            }finally {
                if (in != null) {
                    try {
                        in.close();
                    }catch (Exception e) {
                        Dlog.e(TAG, e);
                    }
                }
                if (out != null) {
                    try {
                        in.close();
                    }catch (Exception e) {
                        Dlog.e(TAG, e);
                    }
                }
            }

            if (response == null) return point;

            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeByteArray(response, 0, response.length, options);
                point.x = options.outWidth;
                point.y = options.outHeight;
            }catch (Exception e) {
                Dlog.e(TAG, e);
            }

            Dlog.d(urlText + " >> " + point.x + " x " + point.y);
            return point;
        }
    }
}