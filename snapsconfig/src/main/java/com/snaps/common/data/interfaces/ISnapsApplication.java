package com.snaps.common.data.interfaces;

import android.content.Context;

/**
 * Created by ysjeong on 16. 5. 13..
 */
public interface ISnapsApplication {
    void requestInitApplication();
    void requestGCMRegistrarDestroy();
    Context getSnapsApplication();
    String getLauncherActivityName();
}
