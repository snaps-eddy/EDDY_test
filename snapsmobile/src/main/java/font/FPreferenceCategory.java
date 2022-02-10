package font;

import android.content.Context;
import android.preference.PreferenceCategory;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.snaps.common.utils.constant.Const_VALUE;

public class FPreferenceCategory extends PreferenceCategory {
	public FPreferenceCategory(Context context) {
		super(context);
	}

	public FPreferenceCategory(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FPreferenceCategory(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onBindView(View view) {
		super.onBindView(view);
		TextView titleView = (TextView) view.findViewById(android.R.id.title);
		titleView.setTypeface(Const_VALUE.SNAPS_TYPEFACE_YG033);
	}
}
