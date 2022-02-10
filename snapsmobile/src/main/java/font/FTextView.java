package font;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import com.snaps.common.utils.ui.FontUtil;
import com.snaps.mobile.R;

//import com.snaps.common.R;

public class FTextView extends TextView {

	public FTextView(Context context) {
		super(context);
//		this.setTypeface(Const_VALUE.SNAPS_TYPEFACE_YG032);
		FontUtil.applyTextViewTypeface(this, FontUtil.eSnapsFonts.YOON_GOTHIC_740);
	}

	public FTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.init(context, attrs);
	}

	public FTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.init(context, attrs);
	}

	/***
	 * custom property로 font를 설정할수 있게 수정..
	 * 
	 */
	private void init(Context context, AttributeSet attrs) {
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FTextViewAttrs);
		if (a == null) return;

		String fontString = a.getString(R.styleable.FTextViewAttrs_customFont);
		if (fontString == null) {
			//FontUtil.applyTextViewTypeface(this, FontUtil.eSnapsFonts.YOON_GOTHIC_740);	//TODO::이거 주석 풀어주면 현재 폰트 작용 안되는 부분 적용됨
			return;
		}

		if (fontString.equalsIgnoreCase(context.getString(R.string.font_name_ygt_760))) {
			FontUtil.applyTextViewTypeface(this, FontUtil.eSnapsFonts.YOON_GOTHIC_760);
		} else if (fontString.equalsIgnoreCase(context.getString(R.string.font_name_ygt_740))) {
			FontUtil.applyTextViewTypeface(this, FontUtil.eSnapsFonts.YOON_GOTHIC_740);
		} else if (fontString.equalsIgnoreCase(context.getString(R.string.font_name_ygt_720))) {
			FontUtil.applyTextViewTypeface(this, FontUtil.eSnapsFonts.YOON_GOTHIC_720);
		} else {
			FontUtil.applyTextViewTypeface(this, FontUtil.eSnapsFonts.YOON_GOTHIC_740);
		}

		a.recycle();
	}
}
