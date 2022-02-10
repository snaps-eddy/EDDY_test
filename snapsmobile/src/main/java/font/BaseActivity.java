package font;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.snaps.common.utils.constant.Const_VALUE;

import errorhandle.CatchActivity;

public class BaseActivity extends CatchActivity {
	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);

		ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
		setGlobalFont(root);
	}

	void setGlobalFont(ViewGroup root) {
		for (int i = 0; i < root.getChildCount(); i++) {
			View child = root.getChildAt(i);
			if (child instanceof TextView) {
				((TextView) child).setTypeface(Const_VALUE.SNAPS_TYPEFACE_YG033);
			} else if (child instanceof ViewGroup) {
				setGlobalFont((ViewGroup) child);
			}
		}
	}
}
