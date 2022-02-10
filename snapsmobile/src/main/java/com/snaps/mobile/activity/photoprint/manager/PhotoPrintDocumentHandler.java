package com.snaps.mobile.activity.photoprint.manager;

import android.content.Context;
import android.content.res.AssetManager;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.snaps.common.structure.photoprint.json.PhotoPrintJsonObjectTemplate;
import com.snaps.common.structure.photoprint.json.PhotoPrintJsonObjectTmplnfo;
import com.snaps.common.structure.photoprint.json.PhotoPrintProduct;
import com.snaps.common.utils.log.Dlog;

import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by songhw on 2017. 3. 14..
 */

public class PhotoPrintDocumentHandler extends DefaultHandler {
    private static final String TAG = PhotoPrintDocumentHandler.class.getSimpleName();

    private PhotoPrintJsonObjectTemplate template;
    private PhotoPrintJsonObjectTmplnfo templateInfo;

    private Context context;

    private String prodCode;

    public PhotoPrintDocumentHandler(Context context, String prodCode ) {
        this.context = context;
        this.prodCode = prodCode;

        init();
    }

    private void init() {
        AssetManager assetManager = context.getAssets();
        Type mapType = new TypeToken<Map<String, PhotoPrintProduct>>() {}.getType();
        PhotoPrintProduct product = null;
        try {
            Map<String, PhotoPrintProduct> map = new GsonBuilder().create().fromJson( new InputStreamReader(assetManager.open("photo_print.json")), mapType );
            product = map.get( prodCode );
        } catch (IOException e) {
            Dlog.e(TAG, e);
        }

        if( product != null ) {
            template = product.getTemplate();
            templateInfo = product.getTmplInfo();
        }
    }

    public PhotoPrintJsonObjectTemplate getTemplate() {
        return template;
    }

    public PhotoPrintJsonObjectTmplnfo getTemplateInfo() {
        return templateInfo;
    }
}
