package com.snaps.mobile.activity.ui.menu.webinterface;

import android.app.Activity;

import com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers.SnapsRenewalWebEventHandler;
import com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers.SnapsWebEventAppFinishHandler;
import com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers.SnapsWebEventBaseHandler;
import com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers.SnapsWebEventCloseAppPopupHandler;
import com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers.SnapsWebEventCountHandler;
import com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers.SnapsWebEventForKakaoHandler;
import com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers.SnapsWebEventGetDeviceHandler;
import com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers.SnapsWebEventGoCustomerActHandler;
import com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers.SnapsWebEventGoDeliveryHandler;
import com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers.SnapsWebEventGoLoginHandler;
import com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers.SnapsWebEventKakaoStoryCheckHandler;
import com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers.SnapsWebEventOpenAppPopupHandler;
import com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers.SnapsWebEventOpenBrowserHandler;
import com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers.SnapsWebEventOpenGalleryHandler;
import com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers.SnapsWebEventOpenIntentHandler;
import com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers.SnapsWebEventPlayYoutubeHandler;
import com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers.SnapsWebEventPresentPaymentHandler;
import com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers.SnapsWebEventPreviewHandler;
import com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers.SnapsWebEventSNSShareHandler;
import com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers.SnapsWebEventSampleViewHandler;
import com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers.SnapsWebEventSelectProductHandler;
import com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers.SnapsWebEventStoryLinkHandler;
import com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers.SnapsWebStackPageHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ysjeong on 2016. 11. 21..
 */

public class SnapsWebEventHandlerStrSwicher implements ISnapsWebEventCMDConstants {

    private Activity activity = null;
    private SnapsShouldOverrideUrlLoader.SnapsShouldHandleData handleDatas = null;

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void setHandleDatas(SnapsShouldOverrideUrlLoader.SnapsShouldHandleData handleDatas) {
        this.handleDatas = handleDatas;
    }

    private Map<Object, ISnapsWebEventHandlerStrSwitchPerform> performMap = new HashMap<>();

    public SnapsWebEventHandlerStrSwicher() {
        createCases();
    }

    public void clear() {
        if (performMap != null && !performMap.isEmpty())
            performMap.clear();
    }

    private void createCases() {
        addCase(SNAPS_SCHEME_TYPE_LIST, new ISnapsWebEventHandlerStrSwitchPerform() {
            @Override
            public SnapsWebEventBaseHandler getHandler() {
                return new SnapsRenewalWebEventHandler( activity, handleDatas, SnapsRenewalWebEventHandler.TYPE_LIST );
            }
        });
        addCase(SNAPS_SCHEME_TYPE_DETAIL, new ISnapsWebEventHandlerStrSwitchPerform() {
            @Override
            public SnapsWebEventBaseHandler getHandler() {
                return new SnapsRenewalWebEventHandler( activity, handleDatas, SnapsRenewalWebEventHandler.TYPE_DETAIL );
            }
        });
        addCase(SNAPS_SCHEME_TYPE_SIZE, new ISnapsWebEventHandlerStrSwitchPerform() {
            @Override
            public SnapsWebEventBaseHandler getHandler() {
                return new SnapsRenewalWebEventHandler( activity, handleDatas, SnapsRenewalWebEventHandler.TYPE_SIZE );
            }
        });
        addCase(SNAPS_SCHEME_TYPE_ALERT, new ISnapsWebEventHandlerStrSwitchPerform() {
            @Override
            public SnapsWebEventBaseHandler getHandler() {
                return new SnapsRenewalWebEventHandler( activity, handleDatas, SnapsRenewalWebEventHandler.TYPE_ALERT );
            }
        });
        addCase(SNAPS_CMD_SAVE_CART, new ISnapsWebEventHandlerStrSwitchPerform() {
            @Override
            public SnapsWebEventBaseHandler getHandler() {
                return new SnapsWebEventSelectProductHandler(activity, handleDatas);
            }
        });
        addCase(SNAPS_CMD_SELECT_PRODUCT, new ISnapsWebEventHandlerStrSwitchPerform() {
            @Override
            public SnapsWebEventBaseHandler getHandler() {
                return new SnapsWebEventSelectProductHandler(activity, handleDatas);
            }
        });
        addCase(SNAPS_CMD_SAMPLE_VIEW, new ISnapsWebEventHandlerStrSwitchPerform() {
            @Override
            public SnapsWebEventBaseHandler getHandler() {
                return new SnapsWebEventSampleViewHandler(activity, handleDatas);
            }
        });
        addCase(SNAPS_CMD_OPEN_APP_POPUP, new ISnapsWebEventHandlerStrSwitchPerform() {
            @Override
            public SnapsWebEventBaseHandler getHandler() {
                return new SnapsWebEventOpenAppPopupHandler(activity, handleDatas);
            }
        });
        addCase(SNAPS_CMD_SNS_SHARE, new ISnapsWebEventHandlerStrSwitchPerform() {
            @Override
            public SnapsWebEventBaseHandler getHandler() {
                return new SnapsWebEventSNSShareHandler(activity, handleDatas);
            }
        });
        addCase(SNAPS_CMD_PLAY_YOUTUBE, new ISnapsWebEventHandlerStrSwitchPerform() {
            @Override
            public SnapsWebEventBaseHandler getHandler() {
                return new SnapsWebEventPlayYoutubeHandler(activity, handleDatas);
            }
        });
        addCase(SNAPS_CMD_PREVIEW, new ISnapsWebEventHandlerStrSwitchPerform() {
            @Override
            public SnapsWebEventBaseHandler getHandler() {
                return new SnapsWebEventPreviewHandler(activity, handleDatas);
            }
        });
        addCase(SNAPS_CMD_PRESENT_PAYMENT, new ISnapsWebEventHandlerStrSwitchPerform() {
            @Override
            public SnapsWebEventBaseHandler getHandler() {
                return new SnapsWebEventPresentPaymentHandler(activity, handleDatas);
            }
        });
        addCase(SNAPS_CMD_GO_DELIVERY, new ISnapsWebEventHandlerStrSwitchPerform() {
            @Override
            public SnapsWebEventBaseHandler getHandler() {
                return new SnapsWebEventGoDeliveryHandler(activity, handleDatas);
            }
        });
        addCase(SNAPS_CMD_CART_COUNT, new ISnapsWebEventHandlerStrSwitchPerform() {
            @Override
            public SnapsWebEventBaseHandler getHandler() {
                return new SnapsWebEventCountHandler(activity, handleDatas);
            }
        });
        addCase(SNAPS_CMD_COUNT, new ISnapsWebEventHandlerStrSwitchPerform() {
            @Override
            public SnapsWebEventBaseHandler getHandler() {
                return new SnapsWebEventCountHandler(activity, handleDatas);
            }
        });
        addCase(SNAPS_CMD_FOR_KAKAO, new ISnapsWebEventHandlerStrSwitchPerform() {
            @Override
            public SnapsWebEventBaseHandler getHandler() {
                return new SnapsWebEventForKakaoHandler(activity, handleDatas);
            }
        });
        addCase(SNAPS_CMD_GO_LOGIN, new ISnapsWebEventHandlerStrSwitchPerform() {
            @Override
            public SnapsWebEventBaseHandler getHandler() {
                return new SnapsWebEventGoLoginHandler(activity, handleDatas);
            }
        });
        addCase(SNAPS_CMD_APP_FINISH, new ISnapsWebEventHandlerStrSwitchPerform() {
            @Override
            public SnapsWebEventBaseHandler getHandler() {
                return new SnapsWebEventAppFinishHandler(activity, handleDatas);
            }
        });
        addCase(SNAPS_CMD_STORY_LINK, new ISnapsWebEventHandlerStrSwitchPerform() {
            @Override
            public SnapsWebEventBaseHandler getHandler() {
                return new SnapsWebEventStoryLinkHandler(activity, handleDatas);
            }
        });
        addCase(SNAPS_CMD_CLOSE_APP_POPUP, new ISnapsWebEventHandlerStrSwitchPerform() {
            @Override
            public SnapsWebEventBaseHandler getHandler() {
                return new SnapsWebEventCloseAppPopupHandler(activity, handleDatas);
            }
        });
        addCase(SNAPS_CMD_POP_PAGE, new ISnapsWebEventHandlerStrSwitchPerform() {
            @Override
            public SnapsWebEventBaseHandler getHandler() {
                return new SnapsWebEventCloseAppPopupHandler(activity, handleDatas);
            }
        });
        addCase(SNAPS_CMD_CLOSE_KAKAO_STORY_CHECK, new ISnapsWebEventHandlerStrSwitchPerform() {
            @Override
            public SnapsWebEventBaseHandler getHandler() {
                return new SnapsWebEventKakaoStoryCheckHandler(activity, handleDatas);
            }
        });
        addCase(SNAPS_CMD_CLOSE_OPEN_INTENT, new ISnapsWebEventHandlerStrSwitchPerform() {
            @Override
            public SnapsWebEventBaseHandler getHandler() {
                return new SnapsWebEventOpenIntentHandler(activity, handleDatas);
            }
        });
        addCase(SNAPS_CMD_CLOSE_OPEN_GALLERY, new ISnapsWebEventHandlerStrSwitchPerform() {
            @Override
            public SnapsWebEventBaseHandler getHandler() {
                return new SnapsWebEventOpenGalleryHandler(activity, handleDatas);
            }
        });
        addCase(SNAPS_CMD_CLOSE_GET_DEVICE_ID, new ISnapsWebEventHandlerStrSwitchPerform() {
            @Override
            public SnapsWebEventBaseHandler getHandler() {
                return new SnapsWebEventGetDeviceHandler(activity, handleDatas);
            }
        });
        addCase(SNAPS_CMD_STACK_PAGE, new ISnapsWebEventHandlerStrSwitchPerform() {
            @Override
            public SnapsWebEventBaseHandler getHandler() {
                return new SnapsWebStackPageHandler(activity, handleDatas);
            }
        });
        addCase(SNAPS_CMD_OPEN_BROWSER, new ISnapsWebEventHandlerStrSwitchPerform() {
            @Override
            public SnapsWebEventBaseHandler getHandler() {
                return new SnapsWebEventOpenBrowserHandler(activity, handleDatas);
            }
        });
        addCase(SNAPS_CMD_GO_CUSTOMER, new ISnapsWebEventHandlerStrSwitchPerform() {
            @Override
            public SnapsWebEventBaseHandler getHandler() {
                return new SnapsWebEventGoCustomerActHandler(activity, handleDatas);
            }
        });
    }

    private void addCase(String str, ISnapsWebEventHandlerStrSwitchPerform perform) {
        if (performMap != null)
            performMap.put(str, perform);
    }

    public SnapsWebEventBaseHandler getHandler(String key) {
        if (performMap == null || !performMap.containsKey(key)) return null;
        ISnapsWebEventHandlerStrSwitchPerform perform = performMap.get(key);
        if (perform != null)
            return perform.getHandler();
        return null;
    }
}
