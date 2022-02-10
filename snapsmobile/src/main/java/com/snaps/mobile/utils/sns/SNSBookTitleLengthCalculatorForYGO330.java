package com.snaps.mobile.utils.sns;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.snaps.common.utils.log.Dlog;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ysjeong on 2017. 2. 27..
 */

public class SNSBookTitleLengthCalculatorForYGO330 extends SNSBookTitleLengthCalculatorBase {
    private static final String TAG = SNSBookTitleLengthCalculatorForYGO330.class.getSimpleName();

    @Override
    public void createFontMap() {
        String json = "{ \"A\":74, \"B\":60, \"C\":68, \"D\":60, \"E\":54, \"F\":54, \"G\":69, \"H\":59, \"I\":50, \"J\":44, \"K\":65, \"L\":51, \"M\":70, \"N\":61, \"O\":69," +
                "\"P\":57, \"Q\":71, \"R\":61, \"S\":60, \"T\":60, \"U\":58, \"V\":72, \"W\":92, \"X\":62, \"Y\":65, \"Z\":59, \"a\":54, \"b\":54, \"c\":54, \"d\":56," +
                "\"e\":56, \"f\":26, \"g\":56, \"h\":50, \"i\":35, \"j\":35, \"k\":53, \"l\":35, \"m\":75, \"n\":50, \"o\":57, \"p\":55, \"q\":56, \"r\":35, \"s\":50, \"t\":26," +
                "\"u\":49, \"v\":54, \"w\":78, \"x\":54, \"y\":55, \"z\":50, \"1\":50, \"2\":52, \"3\":54, \"4\":58, \"5\":54, \"6\":55, \"7\":54, \"8\":58, \"9\":55 ," +
                "\"0\":57, \" \":38, \"other\" : 98 }";
        try {
            Map<String, Integer> mapFonts = new Gson().fromJson(json, new TypeToken<HashMap<String, Integer>>(){}.getType());
            setMapFonts((HashMap<String, Integer>) mapFonts);
        } catch (Exception e) { Dlog.e(TAG, e); }
    }
}
