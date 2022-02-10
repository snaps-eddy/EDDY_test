package com.snaps.mobile.activity.common.interfacies;

import com.snaps.common.spc.view.SnapsDiaryTextView;
import com.snaps.mobile.activity.edit.BaseEditFragmentActivity;

/**
 * Created by ysjeong on 2017. 10. 12..
 */

public abstract class SnapsDiaryEditActToFragmentBridgeActivity extends BaseEditFragmentActivity implements SnapsEditActExternalConnectionBridge {
    public abstract SnapsDiaryTextView.ISnapsDiaryTextControlListener getDiaryTextControlListener();
}
