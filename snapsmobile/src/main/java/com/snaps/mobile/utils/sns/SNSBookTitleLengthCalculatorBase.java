package com.snaps.mobile.utils.sns;

import com.snaps.common.utils.log.Dlog;

import java.util.HashMap;

/**
 * Created by ysjeong on 2017. 2. 27..
 */

public abstract class SNSBookTitleLengthCalculatorBase {
    private static final String TAG = SNSBookTitleLengthCalculatorBase.class.getSimpleName();
    public SNSBookTitleLengthCalculatorBase() {
        createFontMap();
    }

    private HashMap<String,Integer> mapFonts = null;

    public abstract void createFontMap();

    public void releaseInstance() {
        if (mapFonts != null) {
            mapFonts.clear();
            mapFonts = null;
        }
    }

    public boolean isAllowTitleLength(String text, int maxWidth) {
        if (mapFonts == null || text == null) return false;

        int currentTextWidth = 0;
        for (int ii = 0; ii < text.length(); ii++) {
            try {
                String charStr = String.valueOf(text.charAt(ii));
                if (mapFonts.containsKey(charStr)) {
                    currentTextWidth += mapFonts.get(charStr);
                } else {
                    currentTextWidth += mapFonts.get("other");
                }
            } catch (Exception e) { Dlog.e(TAG, e); }

            if (currentTextWidth > maxWidth) return false;
        }

        return true;
    }

    public HashMap<String, Integer> getMapFonts() {
        return mapFonts;
    }

    public void setMapFonts(HashMap<String, Integer> mapFonts) {
        this.mapFonts = mapFonts;
    }
}
