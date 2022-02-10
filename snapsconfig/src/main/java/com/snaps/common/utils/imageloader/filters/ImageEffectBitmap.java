package com.snaps.common.utils.imageloader.filters;

import android.graphics.Bitmap;

import com.snaps.common.R;
import com.snaps.common.utils.ui.ContextUtil;


public class ImageEffectBitmap {
	
	public enum EffectType 
	{
		ORIGIN,
		SHARPEN, //또렷한/선명
		GRAY_SCALE,//흑백
		SEPHIA, //세피아
		WARM, //따뜻한
		AMERALD, //에메랄드
		BLACK_CAT, //블랙캣
		DAWN, //새벽녘
		FILM, //필름느낌
		SHADY, //그늘진
		VINTAGE, //빈티지
		
		SNOW, // 눈꽃
		WATER, // 물방울
		BOKE, // 보케
		OLD_LIGHT, // 빛바랜
		SHINY, // 샤이니
		AURORA, // 오로라
		MEMORY, // 추억
		WINTER // 그겨울
	}

	public static String getEffectTypeWebLogCode(EffectType effectType) {
		if (effectType == null) return "";
		switch (effectType) {
			case ORIGIN:
				return "value_photo_edit_filter_org";
			case SHARPEN:
				return "value_photo_edit_filter_sharpen";
			case GRAY_SCALE:
				return "value_photo_edit_filter_gray_scale";
			case SEPHIA:
				return "value_photo_edit_filter_sephia";
			case WARM:
				return "value_photo_edit_filter_warm";
			case AMERALD:
				return "value_photo_edit_filter_amerald";
			case BLACK_CAT:
				return "value_photo_edit_filter_black_cat";
			case DAWN:
				return "value_photo_edit_filter_dawn";
			case FILM:
				return "value_photo_edit_filter_film";
			case SHADY:
				return "value_photo_edit_filter_shady";
			case VINTAGE:
				return "value_photo_edit_filter_vintage";
			case SNOW:
				return "value_photo_edit_filter_snow";
			case WATER:
				return "value_photo_edit_filter_water";
			case BOKE:
				return "value_photo_edit_filter_boke";
			case OLD_LIGHT:
				return "value_photo_edit_filter_old_light";
			case SHINY:
				return "value_photo_edit_filter_shiny";
			case AURORA:
				return "value_photo_edit_filter_aurora";
			case MEMORY:
				return "value_photo_edit_filter_momory";
			case WINTER:
				return "value_photo_edit_filter_winter";
		}
		return "";
	}
	
	public static String getAuraEffectValue(final String TYPE)
	{
		if(TYPE.equals(EffectType.SHARPEN.toString()))
			return "0x00000020";
		else if(TYPE.equals(EffectType.GRAY_SCALE.toString()))
			return "0x00000040";
		else if(TYPE.equals(EffectType.SEPHIA.toString()))
			return "0x00000001";
		else if(TYPE.equals(EffectType.WARM.toString()))
			return "0x00000002";
		else if(TYPE.equals(EffectType.AMERALD.toString()))
			return "0x00000004";
		else if(TYPE.equals(EffectType.BLACK_CAT.toString()))
			return "0x00000008";
		else if(TYPE.equals(EffectType.DAWN.toString()))
			return "0x00000010";
		else if(TYPE.equals(EffectType.FILM.toString()))
			return "0x00000080";
		else if(TYPE.equals(EffectType.SHADY.toString()))
			return "0x00000100";
		else if(TYPE.equals(EffectType.VINTAGE.toString()))
			return "0x00000200";
		else if(TYPE.equals(EffectType.SNOW.toString())) //FIXME 렌더 값 적용해야 함.
			return "0x00008000";
		else if(TYPE.equals(EffectType.WATER.toString()))
			return "0x00800000";
		else if(TYPE.equals(EffectType.BOKE.toString()))
			return "0x10000";
		else if(TYPE.equals(EffectType.OLD_LIGHT.toString()))
			return "0x00040000";
		else if(TYPE.equals(EffectType.SHINY.toString()))
			return "0x00020000";
		else if(TYPE.equals(EffectType.AURORA.toString()))
			return "0x00200000";
		else if(TYPE.equals(EffectType.MEMORY.toString()))
			return "0x00100000";
		else if(TYPE.equals(EffectType.WINTER.toString()))
			return "0x00080000";
		else
			return "";
	}
	
	public static EffectType convertEffectStrToEnumType(String str) {
		
		if(str == null || str.length() < 1) return null;
		
		if(str.equalsIgnoreCase("ORIGIN"))
			return EffectType.ORIGIN;
		else if(str.equalsIgnoreCase("SHARPEN"))
			return EffectType.SHARPEN;
		else if(str.equalsIgnoreCase("GRAY_SCALE"))
			return EffectType.GRAY_SCALE;
		else if(str.equalsIgnoreCase("SEPHIA"))
			return EffectType.SEPHIA;
		else if(str.equalsIgnoreCase("WARM"))
			return EffectType.WARM;
		else if(str.equalsIgnoreCase("AMERALD"))
			return EffectType.AMERALD;
		else if(str.equalsIgnoreCase("BLACK_CAT"))
			return EffectType.BLACK_CAT;
		else if(str.equalsIgnoreCase("DAWN"))
			return EffectType.DAWN;
		else if(str.equalsIgnoreCase("FILM"))
			return EffectType.FILM;
		else if(str.equalsIgnoreCase("SHADY"))
			return EffectType.SHADY;
		else if(str.equalsIgnoreCase("VINTAGE"))
			return EffectType.VINTAGE;
		
		else if(str.equalsIgnoreCase("SNOW"))
			return EffectType.SNOW;
		else if(str.equalsIgnoreCase("WATER"))
			return EffectType.WATER;
		else if(str.equalsIgnoreCase("BOKE"))
			return EffectType.BOKE;
		else if(str.equalsIgnoreCase("OLD_LIGHT"))
			return EffectType.OLD_LIGHT;
		else if(str.equalsIgnoreCase("SHINY"))
			return EffectType.SHINY;
		else if(str.equalsIgnoreCase("AURORA"))
			return EffectType.AURORA;
		else if(str.equalsIgnoreCase("MEMORY"))
			return EffectType.MEMORY;
		else if(str.equalsIgnoreCase("WINTER"))
			return EffectType.WINTER;
			
		return null;
	}
	
	public static String getAuraEffectName(final String TYPE)
	{
		if(TYPE.equals(EffectType.SHARPEN.toString()))
			return ContextUtil.getContext().getString(R.string.vivid2);
		else if(TYPE.equals(EffectType.GRAY_SCALE.toString()))

			return ContextUtil.getContext().getString(R.string.black_and_white);
		else if(TYPE.equals(EffectType.SEPHIA.toString()))
			return ContextUtil.getContext().getString(R.string.sepia);
		else if(TYPE.equals(EffectType.WARM.toString()))
			return ContextUtil.getContext().getString(R.string.warmth);
		else if(TYPE.equals(EffectType.AMERALD.toString()))
			return ContextUtil.getContext().getString(R.string.emerald);
		else if(TYPE.equals(EffectType.BLACK_CAT.toString()))
			return ContextUtil.getContext().getString(R.string.blackcat);
		else if(TYPE.equals(EffectType.DAWN.toString()))
			return ContextUtil.getContext().getString(R.string.dawm);
		else if(TYPE.equals(EffectType.FILM.toString()))
			return ContextUtil.getContext().getString(R.string.film_camera);
		else if(TYPE.equals(EffectType.SHADY.toString()))
			return ContextUtil.getContext().getString(R.string.shade);
		else if(TYPE.equals(EffectType.VINTAGE.toString()))
			return ContextUtil.getContext().getString(R.string.vintage);
		else if(TYPE.equals(EffectType.SNOW.toString()))
			return ContextUtil.getContext().getString(R.string.snowflakes);
		else if(TYPE.equals(EffectType.WATER.toString()))
			return ContextUtil.getContext().getString(R.string.water_drop);
		else if(TYPE.equals(EffectType.BOKE.toString()))
			return ContextUtil.getContext().getString(R.string.boke);
		else if(TYPE.equals(EffectType.OLD_LIGHT.toString()))
			return ContextUtil.getContext().getString(R.string.faded);
		else if(TYPE.equals(EffectType.SHINY.toString()))
			return ContextUtil.getContext().getString(R.string.shiny);
		else if(TYPE.equals(EffectType.AURORA.toString()))
			return ContextUtil.getContext().getString(R.string.aurora);
		else if(TYPE.equals(EffectType.MEMORY.toString()))
			return ContextUtil.getContext().getString(R.string.memory);
		else if(TYPE.equals(EffectType.WINTER.toString()))
			return ContextUtil.getContext().getString(R.string.the_winter);
		else
			return "";
	}

	public EffectType effectType;
	public Bitmap bitmapThumb;
	public Bitmap bitmapPreview;
	public boolean isCreatedPreview; // 화면에 가득찬 미리 보기 화면
	public boolean isCreatedThumbnail; //하단에 효과 필 썸네일
//	public boolean isCreating;
}
