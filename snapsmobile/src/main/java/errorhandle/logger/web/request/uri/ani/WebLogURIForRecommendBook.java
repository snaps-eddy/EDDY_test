package errorhandle.logger.web.request.uri.ani;

import com.snaps.common.utils.constant.Config;

import errorhandle.logger.web.WebLogConstants;
import errorhandle.logger.web.request.uri.WebLogBaseURI;

public class WebLogURIForRecommendBook extends WebLogBaseURI {

    public static WebLogURIForRecommendBook createURI(WebLogConstants.eWebLogName logName) {
        return new WebLogURIForRecommendBook(logName);
    }

    private WebLogURIForRecommendBook(WebLogConstants.eWebLogName logName) {
        super(logName);
    }

    @Override
    protected void initHook() {
        setShouldSendLogMessage(Config.isSmartSnapsRecommendLayoutPhotoBook());
    }
}
