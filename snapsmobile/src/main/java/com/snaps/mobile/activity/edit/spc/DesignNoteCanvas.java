package com.snaps.mobile.activity.edit.spc;

import android.content.Context;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.activity.edit.skin.SnapsSkinConstants;
import com.snaps.mobile.activity.edit.skin.SnapsSkinRequestAttribute;
import com.snaps.mobile.activity.edit.skin.SnapsSkinUtil;
import com.snaps.mobile.utils.custom_layouts.ARelativeLayoutParams;

public class DesignNoteCanvas extends ThemeBookCanvas {
	private static final String TAG = DesignNoteCanvas.class.getSimpleName();
	public DesignNoteCanvas(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

    @Override
    public void setBgColor(int color) {
        color = 0xFFEEEEEE;
        super.setBgColor(color);
    }

	@Override
	protected void loadShadowLayer() {

	}

	@Override
	protected void loadPageLayer() {
	}

	@Override
	protected void loadBonusLayer() {
		if (_page == 0 || isSpringNote() || isStudyNote()) {
			loadNoteCoverPageBonusLayer();
		} else {
			loadNoteInnerPageBonusLayer();
		}
	}

	private void loadNoteCoverPageBonusLayer() {
		int topMargin = isSpringNote() ? 11 : 9;

		ARelativeLayoutParams params = (ARelativeLayoutParams) bonusLayer.getLayoutParams();
		params.topMargin = topMargin;
		bonusLayer.setLayoutParams(params);

		String skinName = "";
		if( isSpringNote() )
			skinName = isA5Size() ? SnapsSkinConstants.DESIGN_NOTE_A5_SPRING_FILE_NAME : SnapsSkinConstants.DESIGN_NOTE_B5_SPRING_FILE_NAME;//"skin_note_a5_spring" : "skin_note_b5_spring";
		else if(isStudyNote())
			skinName = SnapsSkinConstants.STUDY_NOTE_B5_SPRING_FILE_NAME;
		else
			skinName = isA5Size() ? SnapsSkinConstants.DESIGN_NOTE_A5_SOFT_FILE_NAME : SnapsSkinConstants.DESIGN_NOTE_B5_SOFT_FILE_NAME;//"skin_note_a5_soft" : "skin_note_b5_soft";

		if( !StringUtil.isEmpty(skinName) ) {
			try {
				SnapsSkinUtil.loadSkinImage(new SnapsSkinRequestAttribute.Builder()
						.setContext(getContext())
						.setResourceFileName(skinName)
						.setSkinBackgroundView(bonusLayer)
						.create());
			} catch (Exception e) {
				Dlog.e(TAG, e);
			}
		}

		float scaleX, scaleY;
		if (isSpringNote() || isStudyNote()) {
			scaleX = isA5Size() ? 1.09f : 1.09f;
			scaleY = isA5Size() ? 1.04f : 1.05f;
		} else {
			scaleX = isA5Size() ? 1.12f : 1.07f;
			scaleY = isA5Size() ? 1.04f : 1.07f;
		}

		bonusLayer.setScaleX( scaleX );
		bonusLayer.setScaleY( scaleY );
	}

	private void loadNoteInnerPageBonusLayer() {
		int topMargin = isA5Size() ? 8 : 10;

		ARelativeLayoutParams params = (ARelativeLayoutParams) bonusLayer.getLayoutParams();
		params.topMargin = topMargin;
		bonusLayer.setLayoutParams(params);
		String skinName = "";
		if(isStudyNote()) {
			skinName = SnapsSkinConstants.STUDY_NOTE_A5_SPRING_INNER_FILE_NAME;
		}else {
			skinName = isA5Size() ? SnapsSkinConstants.DESIGN_NOTE_A5_SOFT_INNER_FILE_NAME : SnapsSkinConstants.DESIGN_NOTE_B5_SOFT_INNER_FILE_NAME;
		}

		if( !StringUtil.isEmpty(skinName) ) {
			try {
				SnapsSkinUtil.loadSkinImage(new SnapsSkinRequestAttribute.Builder()
						.setContext(getContext())
						.setResourceFileName(skinName)
						.setSkinBackgroundView(bonusLayer)
						.create());
			} catch (Exception e) {
				Dlog.e(TAG, e);
			}
		}

		float scaleX = isA5Size() ? 1.13f : 1.10f;
		float scaleY = isA5Size() ? 1.08f : 1.07f;

		bonusLayer.setScaleX( scaleX );
		bonusLayer.setScaleY( scaleY );
	}

	@Override
	protected void initMargin() {
	}

	/***
	 * 스프링노트인지 아닌지 판단하는 함수..
	 * 
	 * @return
	 */
	boolean isSpringNote() {

		// 00802200010001 A5
		// 00802200010003 B4

		if (Config.getPROD_CODE().equals("00802200010001") || Config.getPROD_CODE().equals("00802200010003") || Config.getPROD_CODE().equals("00802200030001") || Config.getPROD_CODE().equals("00802200030002"))
//		if ()
			return true;

		return false;//
	}

	boolean isStudyNote() {

		if (Config.getPROD_CODE().equals("00802200040001") )
			return true;

		return false;//
	}

	boolean isA5Size() {
        //        00802200010002 // soft a5
        //        00802200010001 sping a5
        if (Config.getPROD_CODE().equals("00802200010001") || Config.getPROD_CODE().equals("00802200010002") || Config.getPROD_CODE().equals("00802200040001") || Config.getPROD_CODE().equals("00802200030001"))
            return true;

        return false;//
    }
}
