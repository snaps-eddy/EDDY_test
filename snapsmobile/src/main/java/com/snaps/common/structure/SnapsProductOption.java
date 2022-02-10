package com.snaps.common.structure;

import android.os.Parcel;
import android.os.Parcelable;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.ImageEdgeValidation;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class SnapsProductOption implements Parcelable, Serializable, ImageEdgeValidation.ImageValidationInfoProvider {

    private static final String TAG = SnapsProductOption.class.getSimpleName();
    private static final long serialVersionUID = 5118375929403739549L;

    private static final String NO_WRITING_XML = "NO_PERSISTENCE";

    public static final String KEY_USER_SELECTED_MM_HEIGHT = "USER_SELECTED_MM_HEIGHT";
    public static final String KEY_USER_SELECTED_MM_WIDTH = "USER_SELECTED_MM_WIDTH";
    public static final String KEY_MM_WIDTH = "MM_WIDTH";
    public static final String KEY_MM_HEIGHT = "MM_HEIGHT";
    public static final String KEY_ZOOM_LEVEL = "ZOOM_LEVEL";
    public static final String KEY_KNIFE_LINE_PX = "KNIFE_LINE_PX";
    public static final String KEY_STICK_HEIGHT_PX = "STICK_HEIGHT_PX";
    public static final String KEY_STICK_WIDTH_PX = "STICK_WIDTH_PX";
    public static final String KEY_HELPER_MIN_WIDTH_PX = "HELPER_MIN_WIDTH_PX";
    public static final String KEY_MARGIN_PX = "MARGIN_PX";
    public static final String KEY_CASE_COLOR = "CASE_COLOR";
    public static final String KEY_KEYING_TYPE = "KEYING_TYPE";
    public static final String KEY_KEY_HOLE_DIAMETER = "KEYHOLE_DIAMETER";
    public static final String KEY_MINIMUM_IMAGE_SIZE_PX = "MINIMUM_SIZE";
    public static final String KEY_GLOSSY_TYPE = "GLOSSY_TYPE";
    public static final String KEY_GRADIENT_TYPE = "KEY_GRADIENT_TYPE";
    public static final String KEY_KEYRING_TEXTURE_TYPE = "KEY_KEYRING_TEXTURE_TYPE";
    public static final String KEY_PHONE_CASE_DEVICE_COLOR = "KEY_PHONE_CASE_DEVICE_COLOR";
    public static final String KEY_PHONE_CASE_CASE_CODE = "KEY_PHONE_CASE_CASE_CODE";
    public static final String KEY_PHONE_CASE_CASE_COLOR_CODE = "KEY_PHONE_CASE_CASE_COLOR_CODE";

    // @Marko NO_PERSISTENCE 가 붙은 key는 save.xml에 쓰지 않도록 땜빵 처리하긴 했는데, 새로운 객체 만들어서 할 필요가 았나 싶음.
    public static final String KEY_ACCESSORIES = NO_WRITING_XML + "KEY_ACCESSORIES";

    private Map<String, String> optionMap;

    public void set(String key, String value) {
        optionMap.put(key, value);
    }

    public String get(String key) {
        return optionMap.get(key);
    }

    public boolean isExist(String key) {
        return optionMap.containsKey(key);
    }

    public boolean isExist(List<String> keylist) {
        for (String key : keylist) {
            if (!optionMap.containsKey(key)) return false;
        }
        return true;
    }


    public void clear() {
        optionMap.clear();
    }

    public SnapsXML getSaveXML(SnapsXML xml) {
        try {
            xml.startTag(null, "ProductOption");

            for (String key : optionMap.keySet()) {
                if (key.startsWith(NO_WRITING_XML)) {
                    continue;
                }
                xml.addTag(null, key, optionMap.get(key));
            }

            xml.endTag(null, "ProductOption");

        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        return xml;
    }

    public SnapsProductOption() {
        optionMap = new HashMap<>();
    }

    public SnapsProductOption(Parcel in) {
        optionMap = new HashMap<>();
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(optionMap.size());
        for (Map.Entry<String, String> entry : optionMap.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeString(entry.getValue());
        }
    }

    private void readFromParcel(Parcel in) {
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            String key = in.readString();
            String value = in.readString();
            optionMap.put(key, value);
        }
    }

    @Override
    public String toString() {
        return optionMap.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @SuppressWarnings("rawtypes")
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public SnapsProductOption createFromParcel(Parcel in) {
            return new SnapsProductOption(in);
        }

        @Override
        public SnapsProductOption[] newArray(int size) {
            return new SnapsProductOption[size];
        }
    };

    /**
     * ImageValidationInfoProvider implementation
     */
    @Override
    public int getUserSelectWidth() {
        return (int) Float.parseFloat(get(KEY_USER_SELECTED_MM_WIDTH)) * (int) Float.parseFloat(get(KEY_ZOOM_LEVEL));
    }

    @Override
    public int getUserSelectHeight() {
        return (int) Float.parseFloat(get(KEY_USER_SELECTED_MM_HEIGHT)) * (int) Float.parseFloat(get(KEY_ZOOM_LEVEL));
    }

    @Override
    public int getMinimumPx() {
        return (int) Float.parseFloat(get(KEY_MINIMUM_IMAGE_SIZE_PX));
    }

    @Override
    public int getThicknessKnifelinePX() {
        return (int) Float.parseFloat(get(KEY_KNIFE_LINE_PX));
    }

    @Nullable
    public String getAccessoriesRawText() {
        return get(KEY_ACCESSORIES);
    }

    public boolean hasAccessories() {
        if (optionMap == null) {
            return false;
        }

        if (!optionMap.containsKey(KEY_ACCESSORIES)) {
            return false;
        }

        String accessoriesRawText = getAccessoriesRawText();
        if (accessoriesRawText == null || accessoriesRawText.trim().length() < 1) {
            return false;
        }

        try {
            JSONArray jsonArray = new JSONArray(accessoriesRawText);
            return jsonArray.length() > 0;

        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void removeAccessoriesInfo() {
        if (optionMap == null) {
            return;
        }
        optionMap.remove(KEY_ACCESSORIES);

        Dlog.d("Is remained key ? : " + optionMap.containsKey(KEY_ACCESSORIES));
    }
}
