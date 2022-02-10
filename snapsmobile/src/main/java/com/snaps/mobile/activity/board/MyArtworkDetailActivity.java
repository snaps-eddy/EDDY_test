package com.snaps.mobile.activity.board;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

import com.snaps.common.customui.PagerContainer;
import com.snaps.common.data.parser.GetTemplateXMLHandler;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.xml.GetParsedXml;
import com.snaps.common.utils.net.xml.bean.Xml_MyArtworkDetail;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.system.ViewUnbindHelper;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.board.adapter.MyArtworkDetailPagerAdapter;
import com.snaps.mobile.activity.board.fragment.DialogSharePopupFragment;

import java.util.ArrayList;

import errorhandle.logger.SnapsInterfaceLogDefaultHandler;
import errorhandle.logger.SnapsLogger;
import errorhandle.logger.model.SnapsLoggerClass;

public class MyArtworkDetailActivity extends BaseMyArtworkDetail {
    private static final String TAG = MyArtworkDetailActivity.class.getSimpleName();
    // layout
    PagerContainer pager_container;
    ViewPager vpagerMyArtworkDetail;
    TextView txtMyartworkCount;
    ImageView btnTopShare;
    ImageView btnTopOrder;
    TextView txtTopOrder;

    TextView txtProfileName;

    // data
    MyArtworkDetailPagerAdapter pagerAdapter;
    public String projCode;
    String prodCode;
    String[] textCollage;
    String[] textNamecard;
    String bagStat;
    public Xml_MyArtworkDetail myartworkDetail;
    String userNo;
    boolean workOk = false;
    public int paddRight;
    public int paddBottom;

    // 카카오톡 공유 관련
    boolean _bUriScheme = false;

    // 테마북 미리보기 여부
    boolean isThemebookPreView = false;
    // 샘플뷰 미리보보기 여부..
    boolean isSampleView = false;
    String tempId = "";

    String mSaveExist = "";

    public ArrayList<String> themeBookPageThumbnailPaths = null;
    boolean isVertical = false;

    String title = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));

        title = getString(R.string.preview);
        isVertical = Const_PRODUCT.isWoodBlockProduct() || Const_PRODUCT.isTtabujiProduct() || Const_PRODUCT.isSquareProduct() || Const_PRODUCT.isPolaroidPackProduct() || Const_PRODUCT.isNewPolaroidPackProduct();
        if (isVertical) {
            UIUtil.fixOrientation(this, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            int curOrientation = UIUtil.getScreenOrientation(this);
            if (curOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE)
                UIUtil.fixOrientation(this, ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
            else
                UIUtil.fixOrientation(this, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        isThemebookPreView = getIntent().getBooleanExtra("themebookPreView", false);

        isSampleView = getIntent().getBooleanExtra("sampleView", false);
        tempId = getIntent().getStringExtra("tempId");

        mSaveExist = getIntent().getStringExtra("saveexist");
        Dlog.d("onCreate() saveexist:" + mSaveExist);

        if (isThemebookPreView || isSampleView) {

        } else {
            Uri data = getIntent().getData();
            if (data != null) {
                /** data가 있는 경우는 url scheme로 실행된 결과다. */
                projCode = data.getQueryParameter("prjcode");
                prodCode = data.getQueryParameter("prdcode");
                _bUriScheme = true;
            } else {
                projCode = getIntent().getStringExtra(Const_EKEY.MYART_PROJCODE);
                prodCode = getIntent().getStringExtra(Const_EKEY.MYART_PRODCODE);
                bagStat = getIntent().getStringExtra(Const_EKEY.MYART_BAG_STAT);
                _bUriScheme = false;
            }
        }


        setContentView(R.layout.activity_myartworkdetail_);

        if (isThemebookPreView || isSampleView) {
            RelativeLayout lyTitle = (RelativeLayout) findViewById(R.id.rl_edittitle);
            lyTitle.setBackgroundDrawable(null);
        }

        userNo = Setting.getString(this, Const_VALUE.KEY_SNAPS_USER_NO, "");

        textCollage = new String[2];
        textCollage[0] = getString(R.string.cover);
        textCollage[1] = getString(R.string.inner_title_page);

        textNamecard = new String[3];
        textNamecard[0] = getString(R.string.front_page);
        textNamecard[1] = getString(R.string.back_page);
        textNamecard[2] = getString(R.string.item_case);

        pager_container = (PagerContainer) findViewById(R.id.pager_container);
        vpagerMyArtworkDetail = (ViewPager) findViewById(R.id.vpagerMyArtworkDetail);
        txtMyartworkCount = (TextView) findViewById(R.id.txtMyartworkCount);
        txtProfileName = (TextView) findViewById(R.id.ThemeTitleText);

        btnTopShare = (ImageView) findViewById(R.id.btnTopShare);

        btnTopOrder = (ImageView) findViewById(R.id.ThemecartBtn);
        txtTopOrder = (TextView) findViewById(R.id.ThemecartTxt);
        txtTopOrder.setPadding(0, 0, UIUtil.convertDPtoPX(this, 16), 0);

        findViewById(R.id.ThemeTitleLeft).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();

            }
        });

        findViewById(R.id.ThemeTitleLeftLy).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();

            }
        });

        if (Config.isSnapsSticker(prodCode) || Config.isThemeBook(prodCode)) {
            txtTopOrder.setVisibility(View.GONE);
            btnTopOrder.setVisibility(View.GONE);
        } else {
            txtTopOrder.setVisibility(View.VISIBLE);
            btnTopOrder.setVisibility(View.GONE);
        }

        btnTopOrder.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();

            }
        });

        txtTopOrder.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();

            }
        });

        if (bagStat != null) {
            btnTopOrder.setVisibility(View.GONE);
            txtTopOrder.setVisibility(View.GONE);

            btnTopShare.setVisibility(View.VISIBLE);
        }

        ATask.executeVoidDefProgress(this, new ATask.OnTask() {
            @Override
            public void onPre() {
            }

            @Override
            public void onBG() {
                if (isSampleView)
                    themeBookPageThumbnailPaths = GetParsedXml.getSampleViewUrl(tempId, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
                else if (isThemebookPreView)
                    themeBookPageThumbnailPaths = getIntent().getStringArrayListExtra("pageThumbnailPaths");
                else
                    myartworkDetail = GetParsedXml.getMyArtworkDetail(projCode, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
            }

            @Override
            public void onPost() {
                if (isSampleView || isThemebookPreView) {

                    if (themeBookPageThumbnailPaths == null) {
                        MessageUtil.alert(MyArtworkDetailActivity.this, R.string.myartwork_nothing, new ICustomDialogListener() {
                            @Override
                            public void onClick(byte clickedOk) {
                                MyArtworkDetailActivity.this.finish();
                            }
                        });
                        return;
                    }
                    layout();

                } else if (myartworkDetail != null) {
                    if ("".equals(myartworkDetail.F_PROJ_CODE)) {// proj_code가 비었으면 삭제된 작품임
                        MessageUtil.alert(MyArtworkDetailActivity.this, R.string.myartwork_nothing, new ICustomDialogListener() {
                            @Override
                            public void onClick(byte clickedOk) {
                                MyArtworkDetailActivity.this.finish();
                            }
                        });
                        return;
                    }
                    layout();
                } else
                    Toast.makeText(MyArtworkDetailActivity.this, R.string.loading_fail, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            ViewUnbindHelper.unbindReferences(getWindow().getDecorView(), null, false);
            System.gc();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    void layout() {

        if (isSampleView) {
            btnTopShare.setVisibility(View.GONE);// 공유 불가
            txtProfileName.setText(getString(R.string.sample_preview));
            title = getString(R.string.sample_preview);
            pagerAdapter = new MyArtworkDetailPagerAdapter(this, 1);
        } else if (isThemebookPreView) {
            btnTopShare.setVisibility(View.GONE);// 공유 불가
            txtTopOrder.setVisibility(View.VISIBLE);
            btnTopOrder.setVisibility(View.GONE);

            txtProfileName.setText(getString(R.string.preview));
            title = getString(R.string.preview);
            pagerAdapter = new MyArtworkDetailPagerAdapter(this, 2);
        } else {
            if (!"".equals(myartworkDetail.F_USER_NO) && !userNo.equals(myartworkDetail.F_USER_NO)) {// 친구의 작품을 열었을때
                btnTopShare.setVisibility(View.GONE);// 공유 불가
            }
            if (Config.getCHANNEL_CODE() != null && Config.getCHANNEL_CODE().equalsIgnoreCase(Config.CHANNEL_SNAPS_JPN))// 일본버전은 아직 공유불가
                btnTopShare.setVisibility(View.GONE);// 공유 불가
            txtProfileName.setText(getString(R.string.preview));
            pagerAdapter = new MyArtworkDetailPagerAdapter(this, 0);
        }
        vpagerMyArtworkDetail.setAdapter(pagerAdapter);
        vpagerMyArtworkDetail.setOffscreenPageLimit(4);
        vpagerMyArtworkDetail.setClipChildren(false);

        int margin = 40;
        if (isVertical) {
            margin = 0;
            vpagerMyArtworkDetail.getLayoutParams().width = UIUtil.getCalcMyartworkWidthVerticalMode(this);
        } else {
            if (Const_PRODUCT.isPostCardProduct()) {
                margin = 0;
                vpagerMyArtworkDetail.getLayoutParams().width = UIUtil.getCalcMyartworkWidthForPostCard(this);
            } else
                vpagerMyArtworkDetail.getLayoutParams().width = UIUtil.getCalcMyartworkWidth2(this);
        }

        vpagerMyArtworkDetail.setPageMargin(UIUtil.convertDPtoPX(this, margin));

        vpagerMyArtworkDetail.setOnPageChangeListener(new OnPageChangeListener() {
            public void onPageSelected(int position) {
                setStatus();
            }

            public void onPageScrolled(int position, float positionOffest, int positionOffsetPixels) {
                pager_container.invalidate();// 4.0.3 버전에서 PagerContainer가 invalidate 되지 않는 문제 해결법
            }

            public void onPageScrollStateChanged(int state) {
            }
        });
        setStatus();
    }

    private String getCalendarPreviewStatus(int curIdx) {
        String _label = "";

        String sideLabel = "";

        int size = pagerAdapter.getCount();
        int div = size % 2;
        int nStartYear = GetTemplateXMLHandler.getStartYear();
        int nStartMonth = GetTemplateXMLHandler.getStartMonth();

        int label;
        int cmp = 0;
        if (div != 0) {
            cmp = (int) (Math.ceil((double) curIdx / 2.0)) - 1;
        } else {
            if (size > 12)
                cmp = (int) (Math.floor(curIdx / 2.0));
            else
                cmp = (int) (curIdx);


        }
        if ((nStartMonth + cmp) > 12)
            label = ((nStartMonth + cmp)) % 12;
        else
            label = nStartMonth + cmp;


        if (div == 0) //  커버가 없는 경우
        {
            if (size == 12) {
                _label = Integer.toString(label) + getString(R.string.month);

            } else if (size == 24) {
                _label = Integer.toString(label) + getString(R.string.month);
            }

        } else if (size == 13) {

            if (curIdx == 0) // 커버
                _label = getString(R.string.cover);
            else {
                _label = Integer.toString(label) + getString(R.string.month);

            }

        } else if (size == 25) {

            if (curIdx == 0) // 커버
                _label = getString(R.string.cover);
            else {
                _label = Integer.toString(label) + getString(R.string.month);
            }

        }
        _label = "";
        return _label;
    }

    void setStatus() {
        if (pagerAdapter.getCount() == 0)
            txtMyartworkCount.setText("");
        else {
            String msg = "";
            int curIdx = vpagerMyArtworkDetail.getCurrentItem();
            if (isSampleView || isThemebookPreView || Config.isThemeBook(prodCode) || Config.isSimplePhotoBook(prodCode) || Config.isSimpleMakingBook(prodCode)) {
                if (Config.isCalendar()) {
                    msg = "";//getCalendarPreviewStatus(curIdx);

                } else {
                    if (curIdx < textCollage.length)
                        msg = textCollage[curIdx];
                    else {
                        int pp = (curIdx - 2) * 2 + 2;

                        msg = Integer.toString(pp) + " , " + Integer.toString(++pp) + " p";
                    }
                }

            } else if (Config.isCalendar(prodCode)) {
                msg = "";//getCalendarPreviewStatus(curIdx);
            } else if (Config.isSnapsSticker(prodCode)) {// 스티커
                if (curIdx == 0)
                    msg = getResources().getString(R.string.cover);

                else
                    msg = curIdx + " / " + (pagerAdapter.getCount() - 1);
            } else if (Const_PRODUCT.isPackageProduct()) {
                msg = "";
            } else if (Const_PRODUCT.isCardProduct()) {
                msg = "";
            }

            String text = "";
            if (!Config.isNotCoverPhotoBook())
                text = String.format("%s (%s)", title, msg);
            else
                text = String.format("%s", title);

            SpannableString ss = new SpannableString(text);
            ss.setSpan(new RelativeSizeSpan(0.8f), title.length(), text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            txtProfileName.setText(ss, BufferType.SPANNABLE);

        }
    }

    public void onClick(View v) {
        if (v.getId() == R.id.ThemeTitleLeft || v.getId() == R.id.ThemeTitleLeftLy) {// 뒤로
            finish();
        } else if (v.getId() == R.id.btnTopShare) {// 공유
            DialogSharePopupFragment.newInstance().show(getSupportFragmentManager(), "dialog");
        } else if (v.getId() == R.id.btn_share_kakaotalk) {// 카카오톡 공유
            Config.setPROJ_CODE(projCode);
            Config.setPROD_CODE(prodCode);
            KakaosendData();
            assignMsgBuilder();
        }
    }

    @Override
    public int getSharePaddingBottom() {
        paddBottom = btnTopShare.getBottom();
        return paddBottom;
    }

    @Override
    public int getSharePaddingRight() {
        paddRight = txtTopOrder.getRight();
        return paddRight;
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
