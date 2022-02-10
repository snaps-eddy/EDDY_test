//package com.snaps.mobile.utils.ui;
//
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Matrix;
//import android.os.AsyncTask;
//import android.os.Build;
//import android.support.annotation.Nullable;
//import android.support.v4.app.NotificationCompat;
//import android.text.TextUtils;
//import android.util.TypedValue;
//import android.view.View;
//
//import com.snaps.common.data.img.ExifUtil;
//import com.snaps.mobile.deeplink.GCMContent;
//import com.snaps.common.utils.constant.Const_EKEY;
//import com.snaps.common.utils.constant.Const_VALUE;
//import com.snaps.common.utils.constant.SnapsAPI;
//import com.snaps.common.utils.file.FlushedInputStream;
//import com.snaps.common.utils.net.http.HttpUtil;
//import com.snaps.common.utils.pref.Setting;
//import com.snaps.mobile.R;
//import com.snaps.mobile.service.NotificationActivity;
//import com.snaps.mobile.service.PushDialogImageActivity;
//import com.snaps.mobile.service.PushDialogTextActivity;
//
//import org.apache.http.NameValuePair;
//import org.apache.http.message.BasicNameValuePair;
//
//import java.io.BufferedInputStream;
//import java.io.IOException;
//import java.net.URL;
//import java.net.URLConnection;
//import java.util.ArrayList;
//
//import errorhandle.logger.Logg;
//import errorhandle.logger.SnapsInterfaceLogDefaultHandler;
//
//import static android.app.NotificationManager.IMPORTANCE_MAX;
//
//public class NotifyUtil {
//
//    private final static String CHANNEL_ID = "snaps_channel";
//    private final static String CHANNEL_NAME = "snaps";
//
//    public void notify(Context context, GCMContent content, boolean isRunning) {
//        if (context == null || content == null) return;
//
//        //큰 이미지 포함 여부
//        Bitmap imgBitmap = makeBigPictureBitmap(content);
//
//        Intent notiIntent = new Intent(context, NotificationActivity.class);
//        notiIntent.putExtra(Const_EKEY.PUSH_RUN, true);
//        notiIntent.putExtra("gototarget", content.getTarget());
//        notiIntent.putExtra("wakePushImpo", isRunning);
//        notiIntent.putExtra("fullurl", content.getFullTarget());
//        notiIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        int reqid = Math.abs(content.getMsg().hashCode());
//        PendingIntent pi = PendingIntent.getActivity(context, reqid, notiIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        int iconRes = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? R.drawable.ic_status_new : R.drawable.ic_status;
//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, IMPORTANCE_MAX);
//            notificationChannel.enableLights(true);
//            notificationChannel.enableVibration(true);
//            notificationManager.createNotificationChannel(notificationChannel);
//        }
//
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
//                .setTicker(content.getMsg())
//                .setSmallIcon(iconRes)
//                .setContentTitle(content.getTitle())
//                .setContentText(content.getMsg())
//                .setChannelId(CHANNEL_ID)
//                .setAutoCancel(true);
//
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
//            mBuilder.setPriority(Notification.PRIORITY_MAX);
//        }
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            mBuilder.setColor(0xFF000000);
//        }
//
//        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_status_large);
//        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64f, context.getResources().getDisplayMetrics());
//        mBuilder.setLargeIcon(Bitmap.createScaledBitmap(largeIcon, size, size, false));
//
//        if (imgBitmap != null && !imgBitmap.isRecycled()) {
//            NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle();
//            style.setBigContentTitle(content.getTitle());
//            style.setSummaryText(content.getMsg());
//            style.bigPicture(imgBitmap);
//            mBuilder.setStyle(style);
//        }
//
//        mBuilder.setContentIntent(pi);
//
//        Notification noti = mBuilder.build();
//        noti.defaults |= Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
//            int smallIconId = context.getResources().getIdentifier("right_icon", "id", android.R.class.getPackage().getName());
//            if (smallIconId != 0)
//                noti.contentView.setViewVisibility(smallIconId, View.INVISIBLE);
//        }
//        notificationManager.notify(reqid, noti);
//        Logg.d("notify notify  Outs");
//    }
//
//    private static Bitmap makeBigPictureBitmap(GCMContent content) {
//
//        if (content.getBigImgPath() == null || content.getBigImgPath().length() < 1) {
//            return null;
//        }
//
//        Bitmap imgBitmap = null;
//        FlushedInputStream fis = null;
//        try {
//            URL url = new URL(content.getBigImgPath());
//            URLConnection conn = url.openConnection();
//            conn.connect();
//            fis = new FlushedInputStream(conn.getInputStream());
//            BufferedInputStream bis = new BufferedInputStream(fis);
//
//            imgBitmap = BitmapFactory.decodeStream(bis);
//            bis.close();
//
//            int imageOt = ExifUtil.parseOrientationToDegree(content.getImgOt());
//            if (imageOt != 0) {
//                Matrix matrix = new Matrix();
//                matrix.postRotate((float) imageOt);
//                imgBitmap = Bitmap.createBitmap(imgBitmap, 0, 0, imgBitmap.getUserSelectWidth(), imgBitmap.getHeight(), matrix, true);
//            }
//
//        } catch (Exception e) {
//            Dlog.e(TAG, e);
//
//        } finally {
//            try {
//                if (fis != null) {
//                    fis.close();
//                }
//            } catch (IOException e) {
//                Dlog.e(TAG, e);
//            }
//        }
//
//        return imgBitmap;
//    }
//
//    public static void wakePush(Context context, String msg, String title, String imagepath, String target, boolean running, String imginclude, String fulltarget) {
//
//        Logg.d("wakePush wakePush target " + target);
//
//        if (imginclude.equals("N")) {
//
//            Logg.d("TIpushIntent TIpushIntent " + "TIpushIntent");
//
//            Intent TpushIntent = new Intent(context, PushDialogTextActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            TpushIntent.putExtra("wakePushMsg", msg);
//            TpushIntent.putExtra("wakePushTitle", title);
//            TpushIntent.putExtra("wakePushTarget", target);
//            TpushIntent.putExtra("wakePushFullTarget", fulltarget);
//            TpushIntent.putExtra("wakePushImpo", running);
//            context.startActivity(TpushIntent);
//
//        } else if (imginclude.equals("Y")) {
//
//            Logg.d("IpushIntent IpushIntent " + "IpushIntent");
//
//            Intent IpushIntent = new Intent(context, PushDialogImageActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            IpushIntent.putExtra("wakePushImage", SnapsAPI.DOMAIN() + imagepath);
//            IpushIntent.putExtra("wakePushTarget", target);
//            IpushIntent.putExtra("wakePushFullTarget", fulltarget);
//            IpushIntent.putExtra("wakePushImpo", running);
//            context.startActivity(IpushIntent);
//        }
//
//    }
//
//
//    private static ArrayList<NameValuePair> params = new ArrayList<>();
//
//    public static void requestPushReceived(final Context con, boolean whenClickNoti) {
//
//        params = new ArrayList<>();
//        params.add(new BasicNameValuePair("deviceNo", Setting.getString(con, Const_VALUE.KEY_GCM_REGID)));
//        params.add(new BasicNameValuePair("userNo", Setting.getString(con, Const_VALUE.KEY_SNAPS_USER_NO)));
//        params.add(new BasicNameValuePair("brdcstCode", Setting.getString(con, Const_VALUE.KEY_BROADCAST_CODE)));
//        params.add(new BasicNameValuePair("resendNo", Setting.getString(con, Const_VALUE.KEY_RESEND_NO)));
//        params.add(new BasicNameValuePair("rcvYorn", "Y"));
//        params.add(new BasicNameValuePair("openYorn", whenClickNoti ? "Y" : "N"));
//
//        AsyncTask.execute(() -> {
//            String result = HttpUtil.connectGet(SnapsAPI.PUSH_RECEIVE_INTERFACE(), params, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
//        });
//    }
//}
