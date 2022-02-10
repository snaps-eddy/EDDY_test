package com.snaps.common.utils.sound.data;

import android.content.Context;

import com.snaps.common.utils.sound.interfaceis.SnapsSoundConstants;

public class SnapsSoundData {
    private Context context;
    private boolean localFile = false;
    private SnapsSoundConstants.eSnapsLocalSoundName localSoundName = null;
    private String fileUrl = null;
    private boolean isRepeat = false;

    private SnapsSoundData(Builder builder) {
        this.context = builder.context;
        this.localFile = builder.localFile;
        this.localSoundName = builder.localSoundName;
        this.fileUrl = builder.fileUrl;
        this.isRepeat = builder.isRepeat;
    }

    public Context getContext() {
        return context;
    }

    public boolean isLocalFile() {
        return localFile;
    }

    public SnapsSoundConstants.eSnapsLocalSoundName getLocalSoundName() {
        return localSoundName;
    }

    public int getLocalSoundRawId() {
        return localSoundName != null ? localSoundName.getRawId() : -1;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public boolean isRepeat() {
        return isRepeat;
    }

    public static class Builder {
        private Context context;
        private boolean localFile = false;
        private SnapsSoundConstants.eSnapsLocalSoundName localSoundName = null;
        private String fileUrl = null;
        private boolean isRepeat = false;

        public Builder(Context context) {
            this.context = context;
        }

        private Builder() {}

        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }

        public Builder setLocalFile(boolean localFile) {
            this.localFile = localFile;
            return this;
        }

        public Builder setLocalSoundName(SnapsSoundConstants.eSnapsLocalSoundName localSoundName) {
            this.localSoundName = localSoundName;
            return this;
        }

        public Builder setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
            return this;
        }

        public Builder setRepeat(boolean repeat) {
            isRepeat = repeat;
            return this;
        }

        public SnapsSoundData create() {
            return new SnapsSoundData(this);
        }
    }
}
