package com.snaps.mobile.activity.card;

import android.os.Parcel;
import android.os.Parcelable;

import com.snaps.common.structure.control.TextFormat;
import com.snaps.mobile.activity.themebook.holder.IPhotobookCommonConstants.SnapsTextAlign;

import java.io.Serializable;

public class SnapsTextOptions implements Parcelable, Serializable {

    private static final long serialVersionUID = 699886728681024477L;

    private TextFormat textFormat = null;

    private SnapsTextAlign eAlign = SnapsTextAlign.ALIGN_LEFT;
    private int alignOrdinal = 0;
    private boolean isFromLandscapeMode = false;

    public void setAlign(String align) {
        if (align == null) return;
        if (align.equals(SnapsTextAlign.ALIGN_LEFT.getStr()))
            this.eAlign = SnapsTextAlign.ALIGN_LEFT;
        else if (align.equals(SnapsTextAlign.ALIGN_CENTER.getStr()))
            this.eAlign = SnapsTextAlign.ALIGN_CENTER;
        else if (align.equals(SnapsTextAlign.ALIGN_RIGHT.getStr()))
            this.eAlign = SnapsTextAlign.ALIGN_RIGHT;

        alignOrdinal = eAlign.ordinal();
    }

    public SnapsTextAlign getAlign() {
        return eAlign;
    }

    public void setAlign(SnapsTextAlign eAlign) {
        this.eAlign = eAlign;
        alignOrdinal = eAlign.ordinal();
    }

    public int getAlignOrdinal() {
        return alignOrdinal;
    }

    public void setAlignOrdinal(int alignOrdinal) {
        this.alignOrdinal = alignOrdinal;
    }

    public TextFormat getTextFormat() {
        return textFormat;
    }

    public void initByTextFormat(TextFormat textFormat, boolean isFromLandscapeMode) {
        if (textFormat == null) return;
        this.textFormat = textFormat;
        this.isFromLandscapeMode = isFromLandscapeMode;
        this.setAlign(textFormat.align);
    }

    public boolean isFromLandscapeMode() {
        return isFromLandscapeMode;
    }

    public void setFromLandscapeMode(boolean fromLandscapeMode) {
        isFromLandscapeMode = fromLandscapeMode;
    }

    public SnapsTextOptions() {
    }

    protected SnapsTextOptions(Parcel in) {
        textFormat = in.readParcelable(TextFormat.class.getClassLoader());
        alignOrdinal = in.readInt();
        isFromLandscapeMode = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(textFormat, flags);
        dest.writeInt(alignOrdinal);
        dest.writeByte((byte) (isFromLandscapeMode ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SnapsTextOptions> CREATOR = new Creator<SnapsTextOptions>() {
        @Override
        public SnapsTextOptions createFromParcel(Parcel in) {
            return new SnapsTextOptions(in);
        }

        @Override
        public SnapsTextOptions[] newArray(int size) {
            return new SnapsTextOptions[size];
        }
    };
}