package com.snaps.common.utils.sound.interfaceis;

public interface ISoundStateChangeListener {
    enum eSoundState {
        NONE,
        START,
        PAUSE,
        STOP
    }

    void onSoundStateChanged(eSoundState soundState);
}
