package com.snaps.mobile.utils.network.retrofit2.data.request.body;

import com.snaps.common.utils.file.FileUtil;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public abstract class SnapsRetrofitRequestBaseBody {
    protected abstract boolean isMultipartBody();

    public MultipartBody generateMultipartRequestBody() throws Exception {
        if (!isMultipartBody()) return null;

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

//        java.lang.reflect.Field[] allFields = getClass().getFields(); //public
        java.lang.reflect.Field[] allFields = getClass().getDeclaredFields(); //private
        for (java.lang.reflect.Field field : allFields) {
            if (!isSupportFieldType(field)) continue;

            field.setAccessible(true);

            String fieldName = field.getName();
            String value = "";
            if (field.getType() == Integer.class) {
                value = String.valueOf((int) field.get(this));
                builder.addFormDataPart(fieldName, value);
            } else if (field.getType() == String.class) {
                value = (String) field.get(this);
                if (value == null) value = "";
                builder.addFormDataPart(fieldName, value);
            } else if (field.getType() == String[].class) {
                String[] values = (String[]) field.get(this);
                if (values != null && values.length > 0) {
                    for (String str : values) {
                        builder.addFormDataPart(fieldName, str);
                    }
                } else {
                    builder.addFormDataPart(fieldName, "");
                }
            } else if (field.getType() == File.class) {
                File fieldFile = (File) field.get(this);
                if (fieldFile != null) {

                    String mimeType = "";
                    try {
                        mimeType = FileUtil.getMimeType(fieldFile.getAbsolutePath());
                    } catch (Exception e) {
                        mimeType = "image/jpeg";
                    }

                    if (mimeType == null) mimeType = "image/jpeg";

                    builder.addFormDataPart(fieldName,
                            fieldFile.getName(),
                            RequestBody.create(MediaType.parse(mimeType), fieldFile));
                }
            }
        }

        return builder.build();
    }

    private boolean isSupportFieldType(java.lang.reflect.Field field) {
        return field != null
                && (field.getType() == String.class || field.getType() == String[].class
                || field.getType() == Integer.class  || field.getType() == int.class
                || field.getType() == File.class);
    }
}
