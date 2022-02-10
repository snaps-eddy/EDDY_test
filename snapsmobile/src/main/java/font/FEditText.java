package font;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

public class FEditText extends EditText {

	public FEditText(Context context) {
		super(context);
//		this.setTypeface(Const_VALUE.SNAPS_TYPEFACE_YG033);
//		FontUtil.applyTextViewTypeface(this, FontUtil.eSnapsFonts.YOON_GOTHIC_740);
	}

	public FEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
//		this.setTypeface(Const_VALUE.SNAPS_TYPEFACE_YG033);
		init(context, attrs);
	}

	public FEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
//		this.setTypeface(Const_VALUE.SNAPS_TYPEFACE_YG033);
		init(context, attrs);
	}

	/***
	 * custom property로 font를 설정할수 있게 수정..
	 */
	private void init(Context context, AttributeSet attrs) {
//		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FTextViewAttrs);
//		if (a == null) return;
//
//		String fontString = a.getString(R.styleable.FTextViewAttrs_customFont);
//		if (fontString == null) return;
//
//		if (fontString.equalsIgnoreCase(context.getString(R.string.font_name_ygt_760))) {
//			FontUtil.applyTextViewTypeface(this, FontUtil.eSnapsFonts.YOON_GOTHIC_760);
//		} else if (fontString.equalsIgnoreCase(context.getString(R.string.font_name_ygt_740))) {
//			FontUtil.applyTextViewTypeface(this, FontUtil.eSnapsFonts.YOON_GOTHIC_740);
//		} else if (fontString.equalsIgnoreCase(context.getString(R.string.font_name_ygt_720))) {
//			FontUtil.applyTextViewTypeface(this, FontUtil.eSnapsFonts.YOON_GOTHIC_720);
//		} else {
//			FontUtil.applyTextViewTypeface(this, FontUtil.eSnapsFonts.YOON_GOTHIC_740);
//		}
//
//		a.recycle();
	}
}
