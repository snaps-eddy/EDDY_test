package com.snaps.common.utils.sound;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.sound.data.SnapsSoundData;
import com.snaps.common.utils.sound.interfaceis.ISoundStateChangeListener;

public class SnapsSoundPlayer {
    private static final String TAG = SnapsSoundPlayer.class.getSimpleName();
    /**
     * 대충 만들어 놨으니, 나중에 잘 고쳐서 쓰기 바람ㅋ
     */
    private static volatile SnapsSoundPlayer gInstance = null;

//    private Queue<SnapsSoundData> soundDataQueue = null;
    private MediaPlayer mediaPlayer = null;
    private int lastSoundPosition = 0;
    private boolean isPauseSoundPlay = false;

    private RingerBroadcastReceiver ringerModeReceiver = null;
    private SettingsContentObserver settingsContentObserver = null;

    private ISoundStateChangeListener soundStateChangeListener = null;

    public static void createInstance() {
        if (gInstance ==  null) {
            synchronized (SnapsSoundPlayer.class) {
                if (gInstance ==  null) {
                    gInstance = new SnapsSoundPlayer();
                }
            }
        }
    }

    private SnapsSoundPlayer() {
//        soundDataQueue = new LinkedList<>();
    }

    public static SnapsSoundPlayer getInstance() {
        if(gInstance ==  null)
            createInstance();

        return gInstance;
    }

    public static void finalizeInstance() {
        if (gInstance != null) {
            gInstance.stopSoundPlay();
            gInstance.soundStateChangeListener = null;
            gInstance = null;
        }
    }

    public void setSoundStateChangeListener(ISoundStateChangeListener soundStateChangeListener) {
        this.soundStateChangeListener = soundStateChangeListener;
    }

    public static void registerRingerModeStateChangeReceiver(Activity activity) throws Exception {
        if (activity == null) return;
        SnapsSoundPlayer soundPlayer = getInstance();
        if (soundPlayer.ringerModeReceiver != null) {
            unregisterRingerModeStateChangeReceiver(activity);
        }

        soundPlayer.ringerModeReceiver = new RingerBroadcastReceiver();
        IntentFilter filter = new IntentFilter(AudioManager.RINGER_MODE_CHANGED_ACTION);
        activity.registerReceiver(soundPlayer.ringerModeReceiver, filter);
    }

    public static void registerSettingsContentObserver(Activity activity) throws Exception {
        if (activity == null) return;
        SnapsSoundPlayer soundPlayer = getInstance();
        if (soundPlayer.settingsContentObserver != null) {
            unregisterSettingsContentObserver(activity);
        }

        soundPlayer.settingsContentObserver = new SettingsContentObserver(activity, new Handler());
        ContentResolver contentResolver = activity.getContentResolver();
        if (contentResolver != null) {
            contentResolver.registerContentObserver(android.provider.Settings.System.CONTENT_URI, true, soundPlayer.settingsContentObserver);
        }
    }

    public static void unregisterRingerModeStateChangeReceiver(Activity activity) throws Exception {
        if (activity == null) return;
        SnapsSoundPlayer soundPlayer = getInstance();
        if (soundPlayer.ringerModeReceiver == null) return;
        activity.unregisterReceiver(soundPlayer.ringerModeReceiver);
        soundPlayer.ringerModeReceiver = null;
    }

    public static void unregisterSettingsContentObserver(Activity activity) throws Exception {
        if (activity == null) return;
        SnapsSoundPlayer soundPlayer = getInstance();
        if (soundPlayer.settingsContentObserver == null) return;
        ContentResolver contentResolver = activity.getContentResolver();
        if (contentResolver != null) {
            contentResolver.unregisterContentObserver(soundPlayer.settingsContentObserver);
        }
        soundPlayer.settingsContentObserver = null;
    }

    public void stopSoundPlay() {
        if (mediaPlayer == null) return;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }

        mediaPlayer.release();
        mediaPlayer = null;
    }

    public void startSoundPlay(SnapsSoundData snapsSoundData) {
        if (snapsSoundData == null) return;
        try {
            stopSoundPlay();

            lastSoundPosition = 0;
            isPauseSoundPlay = false;

            mediaPlayer = MediaPlayer.create(snapsSoundData.getContext(), snapsSoundData.getLocalSoundRawId());

            mediaPlayer.setLooping(snapsSoundData.isRepeat());

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stopSoundPlay();
                }
            });

            mediaPlayer.start();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public boolean isPlayableRingerMode(Context context) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (am == null) return false;
        switch (am.getRingerMode()) {
            case AudioManager.RINGER_MODE_SILENT:
            case AudioManager.RINGER_MODE_VIBRATE:
                return false;
        }
        return true;
    }

    //FIXME 뮤트하고 나서 볼륨 복구 시키는 게 제대로 동작을 안 함..일단, 그냥 puase/resume으로 구현했으나, 추후 필요하다면 고쳐서 쓰기 바람ㅋ
//    public static void muteSoundVolume() {
//        SnapsSoundPlayer soundPlayer = SnapsSoundPlayer.getInstance();
//        if (soundPlayer.mediaPlayer == null) return;
//        soundPlayer.mediaPlayer.setVolume(0f, 0f);
//    }
//
//    public static void recoverySoundVolume(Context context) {
//        SnapsSoundPlayer soundPlayer = SnapsSoundPlayer.getInstance();
//        if (soundPlayer.mediaPlayer == null || context == null) return;
//
//        AudioManager am = (AudioManager) context.getSystemService(AUDIO_SERVICE);
//        if (am == null) return;
//
//        float volume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
//        soundPlayer.mediaPlayer.setVolume(volume, volume);
//    }

    public static void pauseSoundPlay() {
        SnapsSoundPlayer soundPlayer = SnapsSoundPlayer.getInstance();
        if (soundPlayer.mediaPlayer == null || !soundPlayer.mediaPlayer.isPlaying()) return;
        try {
            soundPlayer.isPauseSoundPlay = true;
            soundPlayer.mediaPlayer.pause();
            soundPlayer.lastSoundPosition = soundPlayer.mediaPlayer.getCurrentPosition();
        } catch (Exception e) { Dlog.e(TAG, e); }

        if (soundPlayer.soundStateChangeListener != null) {
            soundPlayer.soundStateChangeListener.onSoundStateChanged(ISoundStateChangeListener.eSoundState.PAUSE);
        }
    }

    public static void resumeSoundPlay() {
        SnapsSoundPlayer soundPlayer = SnapsSoundPlayer.getInstance();
        if (soundPlayer.mediaPlayer == null || !soundPlayer.isPauseSoundPlay) return;
        try {
            soundPlayer.isPauseSoundPlay = false;
            soundPlayer.mediaPlayer.seekTo(soundPlayer.lastSoundPosition);
            soundPlayer.mediaPlayer.start();
        } catch (Exception e) { Dlog.e(TAG, e); }

        if (soundPlayer.soundStateChangeListener != null) {
            soundPlayer.soundStateChangeListener.onSoundStateChanged(ISoundStateChangeListener.eSoundState.START);
        }
    }

    public static class RingerBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (context == null || intent == null || intent.getAction() == null) return;

            if (intent.getAction().equals(AudioManager.RINGER_MODE_CHANGED_ACTION)) {
                SnapsSoundPlayer soundPlayer = getInstance();
                if (soundPlayer.isPlayableRingerMode(context)) {
                    resumeSoundPlay();
                } else {
                    pauseSoundPlay();
                }
            }
        }
    }

    public static class SettingsContentObserver extends ContentObserver {
        private int previousVolume;
        private Context context;

        public SettingsContentObserver(Context c, Handler handler) {
            super(handler);
            context=c;

            AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (audio != null) {
                previousVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
            }
        }

        @Override
        public boolean deliverSelfNotifications() {
            return super.deliverSelfNotifications();
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);

            try {
                AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                if (audio == null) return;

                int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);

                SnapsSoundPlayer soundPlayer = SnapsSoundPlayer.getInstance();
                int delta = previousVolume-currentVolume;
                if(delta > 0) {
//                Logger.d("Decreased");
                    if (currentVolume <= 0) {
                        if (soundPlayer.soundStateChangeListener != null) {
                            soundPlayer.soundStateChangeListener.onSoundStateChanged(ISoundStateChangeListener.eSoundState.PAUSE);
                        }
                    }
                    previousVolume=currentVolume;
                } else if(delta<0) {
//                Logger.d("Increased");
                    if (previousVolume == 0 && currentVolume > 0) {
                        if (soundPlayer.soundStateChangeListener != null) {
                            soundPlayer.soundStateChangeListener.onSoundStateChanged(ISoundStateChangeListener.eSoundState.START);
                        }
                    }
                    previousVolume=currentVolume;
                }
            } catch (Exception e) { Dlog.e(TAG, e); }
        }
    }
}
