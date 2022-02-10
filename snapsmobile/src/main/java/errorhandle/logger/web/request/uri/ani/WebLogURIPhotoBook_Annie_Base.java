package errorhandle.logger.web.request.uri.ani;

import com.snaps.common.utils.constant.Config;

import errorhandle.logger.web.WebLogConstants;
import errorhandle.logger.web.request.uri.WebLogBaseURI;

public abstract class WebLogURIPhotoBook_Annie_Base extends WebLogBaseURI {
    public WebLogURIPhotoBook_Annie_Base(WebLogConstants.eWebLogName logName) {
        super(logName);
    }

    @Override
    public boolean shouldSendLogMessage() {
        return Config.isSmartSnapsRecommendLayoutPhotoBook();
    }
}
