package errorhandle;

import android.app.Activity;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.mobile.activity.setting.SnapsSettingActivity;

import errorhandle.logger.Logg;

/**
 * Created by ysjeong on 2017. 4. 21..
 */

public class SnapsAssert {
    private static final String TAG = SnapsAssert.class.getSimpleName();
    public static void assertNotNull(Object object) {
        if (!Config.IS_USE_ERR_ASSERT) return;
//        Assert.assertNotNull(object);
    }

    public static void assertNotEmptyStr(String str) {
        if (!Config.IS_USE_ERR_ASSERT) return;
//        Assert.assertTrue(!StringUtil.isEmpty(str));
    }

    public static void assertTrue(final boolean isTrue) {
        if (!Config.IS_USE_ERR_ASSERT) return;
//        Assert.assertTrue(isTrue);
    }

    public static void assertException(final Exception e) {
        if (!Config.IS_USE_ERR_ASSERT || e == null) return;
//        Assert.assertTrue(e.toString(), false);
    }

    public static void assertException(Activity activity, final Exception e) {
        if (!Config.IS_USE_ERR_ASSERT || e == null || activity == null) return;

        StringBuilder builder = new StringBuilder();
        builder.append("assert! you'd check the error log. (").append(e.toString()).append(")");
        StackTraceElement[] traceElements = Thread.currentThread().getStackTrace();
        if (traceElements != null && traceElements.length > 0) {
            for (StackTraceElement element : traceElements) {
                if (element != null)
                    builder.append("\n").append(element.getLineNumber()).append("___").append(element.getMethodName()).append("*").append(element.getClassName()).append("!!!");
            }
        }

        MessageUtil.alertnoTitleOneBtn(activity, builder.toString(), null);
    }
}
