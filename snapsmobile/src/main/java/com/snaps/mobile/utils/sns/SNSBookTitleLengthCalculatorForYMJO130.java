package com.snaps.mobile.utils.sns;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.snaps.common.utils.log.Dlog;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ysjeong on 2017. 2. 27..
 */

public class SNSBookTitleLengthCalculatorForYMJO130 extends SNSBookTitleLengthCalculatorBase {
    private static final String TAG = SNSBookTitleLengthCalculatorForYMJO130.class.getSimpleName();
    @Override
    public void createFontMap() {

        String json = "{ \"A\":56, \"B\":47, \"C\":47, \"D\":52, \"E\":47, \"F\":45, \"G\":55, \"H\":57, \"I\":25, \"J\":40, \"K\":58, \"L\":46, \"M\":66, \"N\":58," +
                "\"O\":53, \"P\":47, \"Q\":54, \"R\":51, \"S\":41, \"T\":45, \"U\":58, \"V\":54, \"W\":75, \"X\":54, \"Y\":53, \"Z\":41, \"a\":38, \"b\":40," +
                "\"c\":31, \"d\":40, \"e\":34, \"f\":31, \"g\":42, \"h\":45, \"i\":35, \"j\":35, \"k\":44, \"l\":25, \"m\":66, \"n\":45, \"o\":37, \"p\":40, \"q\":40," +
                "\"r\":31, \"s\":31, \"t\":28, \"u\":45, \"v\":42, \"w\":58, \"x\":42, \"y\":42, \"z\":32, \"1\":50, \"2\":35, \"3\":36, \"4\":38, \"5\":35," +
                "\"6\":36, \"7\":34, \"8\":37, \"9\":35, \"0\":37, \" \":27, \"other\" : 75 }";
        
        try {
            Map<String, Integer> mapFonts = new Gson().fromJson(json, new TypeToken<HashMap<String, Integer>>(){}.getType());
            setMapFonts((HashMap<String, Integer>) mapFonts);
        } catch (Exception e) { Dlog.e(TAG, e); }
    }
}
