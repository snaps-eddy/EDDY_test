package com.snaps.common.utils.ui;

import static com.snaps.common.utils.ui.VoidContainsBoolean.eResult.VOID;

public class VoidContainsBoolean {
    public enum eResult {
        TRUE,
        FALSE,
        VOID
    }
    eResult result = VOID;

    public eResult getResult() {
        return result;
    }

    public void setResult(eResult result) {
        this.result = result;
    }
}
