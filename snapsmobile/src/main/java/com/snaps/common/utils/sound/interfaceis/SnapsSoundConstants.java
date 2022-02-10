package com.snaps.common.utils.sound.interfaceis;

import com.snaps.mobile.R;

public interface SnapsSoundConstants {

    enum eSnapsLocalSoundName {
        ANI_BOOK_MAKING_SOUND_FOR_BABY(R.raw.ani_making_sound_for_baby),
        ANI_BOOK_MAKING_SOUND_FOR_COUPLE(R.raw.ani_making_sound_for_couple),
        ANI_BOOK_MAKING_SOUND_FOR_FAMILY(R.raw.ani_making_sound_for_family),
        ANI_BOOK_MAKING_SOUND_FOR_TRAVEL(R.raw.ani_making_sound_for_travel);

        private int rawId = -1;

        eSnapsLocalSoundName(int rawId) {
            this.rawId = rawId;
        }

        public int getRawId() {
            return rawId;
        }
    }
}
