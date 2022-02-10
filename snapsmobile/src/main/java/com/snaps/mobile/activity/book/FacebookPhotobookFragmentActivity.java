package com.snaps.mobile.activity.book;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.view.MotionEvent;
import android.widget.TextView;

import com.snaps.common.data.request.GetTemplateLoad;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.xml.GetParsedXml;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.thread.ATask.OnTask;
import com.snaps.common.utils.ui.FontUtil;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.facebook.utils.sns.FacebookUtil;
import com.snaps.facebook.utils.sns.FacebookUtil.BookMaker;
import com.snaps.facebook.utils.sns.FacebookUtil.ProcessListener;
import com.snaps.facebook.utils.sns.FacebookUtil.ProgressListener;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.book.SNSBookRecorder.SNSBookInfo;
import com.snaps.mobile.activity.edit.view.CircleProgressView;
import com.snaps.mobile.activity.edit.view.custom_progress.SnapsTimerProgressView;
import com.snaps.mobile.activity.edit.view.custom_progress.SnapsTimerProgressViewFactory;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderState;
import com.snaps.mobile.tutorial.new_tooltip_tutorial.SnapsTutorialUtil;

import errorhandle.logger.Logg;
import errorhandle.logger.SnapsInterfaceLogDefaultHandler;

public class FacebookPhotobookFragmentActivity extends SNSBookFragmentActivity {
    private static final String TAG = FacebookPhotobookFragmentActivity.class.getSimpleName();
    private FacebookPhotobookDrawManager drawManager;

    @Override
    protected void initByType() {
        this.type = SNSBookFragmentActivity.TYPE_FACEBOOK_PHOTOBOOK;

        // 재편집인 경우 스토리 데이터를 가져오지 않는다.
        if (!IS_EDIT_MODE) {
            BookMaker maker = FacebookUtil.BookMaker.getInstance();
            if (maker != null) {
                templateId = maker.getTemplateId();
                productCode = maker.getProductCode();
            }
        }
    }

    @Override
    protected void onPageSelect(int index) {
        String prefix = getResources().getString(R.string.preview);
        String tailText = "";
        if (index == 0) {
            tailText = "(" + getString(R.string.cover) + ")";
        } else if (index == 1) {
            tailText = "(" + getString(R.string.index) + ")";
        } else if (index == 2) {
            tailText = "(" + getString(R.string.photos) + ")";
        } else if (index == 3) {
            tailText = "(" + getString(R.string.statistics) + ")";
        } else {
            int pp = (index - 2) * 2 + 2;
            int totalPage = (_pageList.size() - 3) * 2 + 3;
            tailText = String.format("(%d,%d / %d p)", pp, ++pp, totalPage);
        }

        TextView titleView = (TextView) findViewById(R.id.btnTopTitle);
        titleView.setText(prefix + " " + tailText);

    }

    @Override
    protected void makeBookLayout() {
        // 템플릿 가져오기..
        ATask.executeVoid(new OnTask() {
            boolean isSuccessDownload = false;

            @Override
            public void onPre() {
                CircleProgressView.getInstance(FacebookPhotobookFragmentActivity.this).setMessage(getString(R.string.facebook_photobook_making_msg));
                CircleProgressView.getInstance(FacebookPhotobookFragmentActivity.this).load(CircleProgressView.VIEW_PROGRESS);
            }

            @Override
            public void onBG() {
                // 템플릿 다운로드..
                if (IS_EDIT_MODE) ;
                else isSuccessDownload = downloadTemplate(templateId);
            }

            @Override
            public void onPost() {
                if (isSuccessDownload) calcTemplate();
                else {
                    MessageUtil.toast(FacebookPhotobookFragmentActivity.this, getString(R.string.kakao_book_make_err_template_download));
                    finishActivity();
                }
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            SnapsTutorialUtil.clearTooltip();
        }

        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void getLoadSaveXML(final Activity activity) {
        final String url = SnapsAPI.GET_API_SAVE_XML() + "&prmProjCode=" + Config.getPROJ_CODE();
        Dlog.d("getLoadSaveXML() url:" + url);

        ATask.executeVoid(new OnTask() {
            SnapsTemplate template = null;

            @Override
            public void onPre() {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                SnapsTimerProgressView.showProgress(activity,
                        SnapsTimerProgressViewFactory.eTimerProgressType.PROGRESS_TYPE_LOADING,
                        activity.getString(R.string.templete_data_downloaing));
            }

            @Override
            public void onBG() {
                template = GetTemplateLoad.getTemplateByXmlPullParser(url, true, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
                if (template != null)
                    calcTextControl(template);

                String projectTitle = Config.getPROJ_NAME();

                BookMaker maker = BookMaker.getInstance();
                if (maker != null)
                    maker.coverTitle = projectTitle;

                if (IS_EDIT_MODE) {
                    String prmProjCode = Config.getPROJ_CODE();// "20150217004103";
                    // 커버 색상을 구할려면 필
                    templateId = Config.getTMPL_CODE();
                    saveXMLPriceInfo = GetParsedXml.getProductPriceInfo(prmProjCode, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
                }
            }

            @Override
            public void onPost() {
                if (template == null) {
                    MessageUtil.toast(activity, getString(R.string.kakao_book_make_err_template_download));
                    finishActivity();
                } else {
                    setTemplate(template);
                    SnapsTimerProgressView.destroyProgressView();

                    checkBookPageCount();

                    if (SnapsOrderManager.getSnapsOrderStatePauseCode().equalsIgnoreCase(SnapsOrderState.PAUSE_APPLICATION)) {
                        requestNotifycation();
                    }
                }
            }
        });
    }

    void calcTemplate() {
        // 페이지가 많을 경우, 생성하는 데만해도 시간이 많이 소요 되어서, 프로그레스 처리를 세분화 처리 한다.
        ATask.executeVoid(new OnTask() {
            BookMaker maker = BookMaker.getInstance();

            @Override
            public void onPre() {
                if (maker != null) {
                    maker.setProgressListener(new ProgressListener() {
                        @Override
                        public void onUpdate(final float per) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    CircleProgressView.getInstance(FacebookPhotobookFragmentActivity.this).setValue((int) per);
                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onPost() {

            }

            @Override
            public void onBG() {
                // 전체적으로 auraTextFontSize를 설정한다.
                for (SnapsPage p : multiTemplate.getPages()) {
                    p.setTextControlFont(Const_PRODUCT.AURATEXT_RATION);
                }

                drawManager = new FacebookPhotobookDrawManager(FacebookPhotobookFragmentActivity.this);
                drawManager.makeProfileData(new ProcessListener() {
                    @Override
                    public void onFail(Object result) {
                    }

                    @Override
                    public void onError(Object result) {
                    }

                    @Override
                    public void onComplete(Object result) {
                        calcTemplate2();
                    }
                });


            }
        });
    }

    void calcTemplate2() {
        // 중간에 하나 더.
        ATask.executeVoid(new OnTask() {
            BookMaker maker = BookMaker.getInstance();

            @Override
            public void onPre() {
            }

            @Override
            public void onPost() {
                loadFinish();
            }

            @Override
            public void onBG() {
                drawManager.makePages(multiTemplate);
                Config.setPROJ_NAME(maker.coverTitle);

                FontUtil.downloadFontFiles(FacebookPhotobookFragmentActivity.this, multiTemplate.fonts); // font download

                maker.updateProgress(99);

                setTemplate(multiTemplate);

                maker.updateProgress(100);
            }
        });
    }

    @Override
    protected void setTemplate(SnapsTemplate template) {
        String paperCode = FacebookUtil.BookMaker.getInstance().getPaperCode();
        if (!StringUtil.isEmpty(paperCode))
            template.info.F_PAPER_CODE = paperCode;

        super.setTemplate(template);

        if (drawManager != null)
            drawManager.totalPage = _pageList.size();
    }

    @Override
    protected SNSBookInfo getSNSBookInfo() {
        if (IS_EDIT_MODE)
            return createSNSBookInfoFromSaveXml();
        else if (drawManager != null)
            return drawManager.getInfo();
        return null;
    }
}