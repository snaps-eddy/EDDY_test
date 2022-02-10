package font;

import android.app.Dialog;
import android.content.Context;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;

import com.snaps.mobile.R;

public class FProgressDialog extends Dialog {
	public FProgressDialog(Context context) {
		super(context, R.style.blurDialog);
        setContentView(R.layout.dialog_progress_default);
	}

	public FProgressDialog(Context context, boolean isBackgroundDimEnabled) {
		super(context, (isBackgroundDimEnabled ? R.style.blurDialog : R.style.transparentDialog));
		setContentView(R.layout.dialog_progress_default);
	}


	public void setMessage(String s) {

	}
}