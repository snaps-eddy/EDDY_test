package com.snaps.common.push;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import androidx.core.app.NotificationCompat;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.ui.menu.UIResourcesManager;
import com.snaps.mobile.service.NotificationActivity;
import com.snaps.mobile.service.PushDialogImageActivity;
import com.snaps.mobile.service.PushDialogTextActivity;

import static android.app.NotificationManager.IMPORTANCE_MAX;

public class PushNotificationService {
    private static final String TAG = PushNotificationService.class.getSimpleName();
    private final static String CHANNEL_ID = "snaps_channel";
    private final static String CHANNEL_NAME = "snaps";

    private Context context;

    public PushNotificationService(Context context) {
        this.context = context;
    }

    public void showToast(final String msg) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> Toast.makeText(context, msg, Toast.LENGTH_LONG).show());
    }

    @SuppressLint("WrongConstant")
    public void notifyInStatusBar(GCMContent content, boolean isRunning) {
        if (context == null || content == null) return;

        //큰 이미지 포함 여부
        Bitmap imgBitmap = UIResourcesManager.makeBitmapPushContent(content);

        Intent notiIntent = new Intent(context, NotificationActivity.class);
        notiIntent.putExtra(Const_EKEY.PUSH_RUN, true);
        notiIntent.putExtra("gototarget", content.getTarget());
        notiIntent.putExtra("wakePushImpo", isRunning);
        notiIntent.putExtra("fullurl", content.getFullTarget());
        notiIntent.putExtra("prmchnlcode", content.getPrmchnlcode());

        notiIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        int reqid = Math.abs(content.getMsg().hashCode());
        PendingIntent pi = PendingIntent.getActivity(context, reqid, notiIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        int iconRes = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? R.drawable.ic_status_new : R.drawable.ic_status;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager == null) {
            Dlog.w(TAG, "Notification Managser not found.");
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, IMPORTANCE_MAX);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setTicker(content.getMsg())
                .setSmallIcon(iconRes)
                .setContentTitle(content.getTitle())
                .setContentText(content.getMsg())
                .setChannelId(CHANNEL_ID)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            mBuilder.setPriority(Notification.PRIORITY_MAX);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setColor(0xFF000000);
        }

        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_status_large);
        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64f, context.getResources().getDisplayMetrics());
        mBuilder.setLargeIcon(Bitmap.createScaledBitmap(largeIcon, size, size, false));

        if (imgBitmap != null && !imgBitmap.isRecycled()) {
            NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle();
            style.setBigContentTitle(content.getTitle());
            style.setSummaryText(content.getMsg());
            style.bigPicture(imgBitmap);
            mBuilder.setStyle(style);
        }

        mBuilder.setContentIntent(pi);

        Notification noti = mBuilder.build();
        noti.defaults |= Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            int smallIconId = context.getResources().getIdentifier("right_icon", "id", android.R.class.getPackage().getName());
            if (smallIconId != 0)
                noti.contentView.setViewVisibility(smallIconId, View.INVISIBLE);
        }
        notificationManager.notify(reqid, noti);
    }

    //    public void wakePush(String msg, String title, String imagepath, String target, boolean running, String imginclude, String fulltarget) {
    public void notifyWakeup(GCMContent content, boolean isRunning) {
        Dlog.d("notifyWakeup() target:" + content.getTarget());

        if ("N".equalsIgnoreCase(content.getImageIncluded())) {
            Intent TpushIntent = new Intent(context, PushDialogTextActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            TpushIntent.putExtra("wakePushMsg", content.getMsg());
            TpushIntent.putExtra("wakePushTitle", content.getTitle());
            TpushIntent.putExtra("wakePushTarget", content.getTarget());
            TpushIntent.putExtra("wakePushFullTarget", content.getFullTarget());
            TpushIntent.putExtra("wakePushImpo", isRunning);
            context.startActivity(TpushIntent);

        } else if ("Y".equalsIgnoreCase(content.getImageIncluded())) {
            Intent IpushIntent = new Intent(context, PushDialogImageActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            IpushIntent.putExtra("wakePushImage", SnapsAPI.DOMAIN() + content.getBigImgPath());
            IpushIntent.putExtra("wakePushTarget", content.getTarget());
            IpushIntent.putExtra("wakePushFullTarget", content.getFullTarget());
            IpushIntent.putExtra("wakePushImpo", isRunning);
            context.startActivity(IpushIntent);
        }
    }

}
