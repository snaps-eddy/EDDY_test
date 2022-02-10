package com.snaps.mobile.activity.diary.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.SnapsDiaryCommonUtils;
import com.snaps.mobile.activity.diary.SnapsDiaryConstants;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;
import com.snaps.mobile.activity.diary.SnapsDiaryInterfaceUtil;
import com.snaps.mobile.activity.diary.interfaces.ISnapsDiaryUploadOpserver;
import com.snaps.mobile.activity.diary.json.SnapsDiaryMissionStateJson;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryUserInfo;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryWriteInfo;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

import errorhandle.CatchFragmentActivity;
import errorhandle.logger.SnapsLogger;
import errorhandle.logger.model.SnapsLoggerClass;
import font.FTextView;

/**
 * Created by ysjeong on 16. 3. 4..
 */
public class SnapsDiarySelectDateWeatherFeelActivity extends CatchFragmentActivity implements ISnapsDiaryUploadOpserver, View.OnClickListener, DatePickerDialog.OnDateSetListener {
    private static final String TAG = SnapsDiarySelectDateWeatherFeelActivity.class.getSimpleName();
    private DatePickerDialog mDatePicker = null;

    private FTextView m_tvDate = null;
    private FTextView m_tvWeatherState = null;
    private FTextView m_tvFeelsState = null;

    private boolean m_isInfoModifyMode = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));

        setContentView(R.layout.snaps_diary_select_date_weather_feels_layout);

        SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
        dataManager.registDiaryUploadObserver(this);
        dataManager.setIsWritingDiary(true);

        initControls();

        setDefaultDatas();
    }

    private void initControls() {
        TextView themeTitle = (TextView) findViewById(R.id.ThemeTitleText);
        themeTitle.setText(R.string.diary_write);

        m_isInfoModifyMode = getIntent().getBooleanExtra(Const_EKEY.DIARY_INFO_CHANGE, false);

        TextView tvNext = (TextView) findViewById(R.id.ThemebtnTopNext);
        tvNext.setText(getString(m_isInfoModifyMode ? R.string.done : R.string.next));
        tvNext.setOnClickListener(this);

        m_tvDate = (FTextView) findViewById(R.id.snaps_diary_write_one_step_date_tv);
        m_tvWeatherState = (FTextView) findViewById(R.id.snaps_diary_write_one_step_weather_state_tv);
        m_tvFeelsState = (FTextView) findViewById(R.id.snaps_diary_write_one_step_feels_state_tv);

        findViewById(R.id.snaps_diary_write_one_step_calendar_ly).setOnClickListener(this);

        SnapsDiaryConstants.eWeather[] weathers = SnapsDiaryConstants.eWeather.values();
        for (int ii = 1; ii < weathers.length; ii++) { //0은 None이다..
            SnapsDiaryConstants.eWeather weather = weathers[ii];
            findViewById(getWeatherImageViewId(weather)).setOnClickListener(this);
        }

        SnapsDiaryConstants.eFeeling[] feelings = SnapsDiaryConstants.eFeeling.values();
        for (int ii = 1; ii < feelings.length; ii++) { //0은 None이다..
            SnapsDiaryConstants.eFeeling feeling = feelings[ii];
            findViewById(getFeelImageViewId(feeling)).setOnClickListener(this);
        }

        if (SnapsDiaryConstants.IS_QA_VERSION) {
            findViewById(R.id.snaps_diary_write_one_step_feels_qa_test_btn).setVisibility(View.VISIBLE);
            findViewById(R.id.snaps_diary_write_one_step_feels_qa_test_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestSetMissionFailed();
                }
            });
        }
    }

    @Override
    public void onFinishDiaryUpload(boolean isIssuedInk, boolean isNewWrite) {
        finish();
    }

    @Override
    public void onClick(View v) {
        UIUtil.blockClickEvent(v, 1000L);
        if(v.getId() == R.id.ThemeTitleLeftLy || v.getId() == R.id.ThemeTitleLeft)
            onBackPressed();
        else if(v.getId() == R.id.ThemebtnTopNext)
            startSelectPhotoTemplateActivity();
        else if(v.getId() == R.id.snaps_diary_write_one_step_calendar_ly)
            SnapsDiaryCommonUtils.showCalendar(this, this, mDatePicker);
        else {
            boolean isWeatherSelected = false;
            SnapsDiaryConstants.eWeather[] weathers = SnapsDiaryConstants.eWeather.values();
            for (int ii = 1; ii < weathers.length; ii++) { //0은 None이다..
                SnapsDiaryConstants.eWeather weather = weathers[ii];
                if(v.getId() == getWeatherImageViewId(weather)) {
                    isWeatherSelected = true;
                    setWeather(weather);
                    break;
                }
            }

            if (!isWeatherSelected) {
                SnapsDiaryConstants.eFeeling[] feelings = SnapsDiaryConstants.eFeeling.values();
                for (int ii = 1; ii < feelings.length; ii++) { //0은 None이다..
                    SnapsDiaryConstants.eFeeling feeling = feelings[ii];
                    if(v.getId() == getFeelImageViewId(feeling)) {
                        setFeeling(feeling);
                        break;
                    }
                }
            }
        }
    }

    private void setWeather(SnapsDiaryConstants.eWeather selctedWeather) {
        if(selctedWeather == null || selctedWeather == SnapsDiaryConstants.eWeather.NONE) return;

        try {
            SnapsDiaryConstants.eWeather[] weathers = SnapsDiaryConstants.eWeather.values();
            for (int ii = 1; ii < weathers.length; ii++) { //0은 None이다..
                SnapsDiaryConstants.eWeather weather = weathers[ii];
                ImageView ivWeather = (ImageView) findViewById(getWeatherImageViewId(weather));
                ivWeather.setImageResource(weather.getIconResId(weather.equals(selctedWeather)));
            }

            if(m_tvWeatherState != null)
                m_tvWeatherState.setText(selctedWeather.getTextResId());

            SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
            SnapsDiaryWriteInfo writeInfo = dataManager.getWriteInfo();
            writeInfo.setWeather(selctedWeather);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void setFeeling(SnapsDiaryConstants.eFeeling selctedFeeling) {
        if(selctedFeeling ==  null || selctedFeeling == SnapsDiaryConstants.eFeeling.NONE) return;

        try {
            SnapsDiaryConstants.eFeeling[] feelings = SnapsDiaryConstants.eFeeling.values();
            for (int ii = 1; ii < feelings.length; ii++) { //0은 None이다..
                SnapsDiaryConstants.eFeeling feeling = feelings[ii];
                ImageView ivFeeling = (ImageView) findViewById(getFeelImageViewId(feeling));
                ivFeeling.setImageResource(feeling.getIconResId(feeling.equals(selctedFeeling)));
            }

            if(m_tvFeelsState != null)
                m_tvFeelsState.setText(selctedFeeling.getTextResId());

            SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
            SnapsDiaryWriteInfo writeInfo = dataManager.getWriteInfo();
            writeInfo.setFeels(selctedFeeling);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public int getWeatherImageViewId(SnapsDiaryConstants.eWeather weather) {
        switch (weather) {
            case SUNSHINE : return R.id.snaps_diary_write_one_step_weather_sunshine_iv;
            case CLOUDY : return R.id.snaps_diary_write_one_step_weather_cloudy_iv;
            case WIND : return R.id.snaps_diary_write_one_step_weather_wind_iv;
            case RAINY : return R.id.snaps_diary_write_one_step_weather_rainy_iv;
            case SNOWY : return R.id.snaps_diary_write_one_step_weather_snowy_iv;
            case DUST_STORM : return R.id.snaps_diary_write_one_step_weather_dust_storm_iv;
            case LIGHTNING : return R.id.snaps_diary_write_one_step_weather_lightning_iv;
            case FOG: return R.id.snaps_diary_write_one_step_weather_fog_iv;
        }
        return 0;
    }

    public int getFeelImageViewId(SnapsDiaryConstants.eFeeling feeling) {
        switch (feeling) {
            case HAPPY : return R.id.snaps_diary_write_one_step_feels_happy_iv;
            case NO_FEELING : return R.id.snaps_diary_write_one_step_feels_no_feel_iv;
            case FUNNY : return R.id.snaps_diary_write_one_step_feels_funny_iv;
            case THANKS : return R.id.snaps_diary_write_one_step_feels_thanks_iv;
            case MISFORTUNE : return R.id.snaps_diary_write_one_step_feels_misfortune_iv;
            case SAD : return R.id.snaps_diary_write_one_step_feels_sad_iv;
            case ANGRY : return R.id.snaps_diary_write_one_step_feels_angry_iv;
            case TIRED : return R.id.snaps_diary_write_one_step_feels_tired_iv;
        }
        return -1;
    }

    @Override
    protected void onDestroy() {
        SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
        dataManager.removeDiaryUploadObserver(this);

        if (mDatePicker != null)
            mDatePicker.onDestroy();

        super.onDestroy();
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        if(m_tvDate == null) return;

        if(!SnapsDiaryCommonUtils.isAllowDiaryRegisterDate(year, monthOfYear, dayOfMonth)) {
            MessageUtil.alertnoTitleOneBtn(this, getString(R.string.diary_invalid_date_msg), null);
            return;
        }

        SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
        SnapsDiaryWriteInfo writeInfo = dataManager.getWriteInfo();
        writeInfo.setYear(year);
        writeInfo.setMonth(monthOfYear + 1);
        writeInfo.setDay(dayOfMonth);
        writeInfo.setYMDToDateStr();

        m_tvDate.setText(writeInfo.getDateFormatted());
    }

    private void startSelectPhotoTemplateActivity() {
        if(m_isInfoModifyMode) {
            setResult(RESULT_OK);
            finish();
            return;
        }

        Intent ittNextStep = new Intent(this, SnapsDiarySelectPhotoTemplateActivity.class);
        startActivity(ittNextStep);
    }

    private void setDefaultDatas() {
        SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();

        SnapsDiaryWriteInfo writeInfo = dataManager.getWriteInfo();
        if (m_tvDate != null) {
            if(writeInfo.getDate() == null || writeInfo.getDate().length() < 1) {
                Calendar now = Calendar.getInstance();
                writeInfo.setYear(now.get(Calendar.YEAR));
                writeInfo.setMonth(now.get(Calendar.MONTH) + 1);
                writeInfo.setDay(now.get(Calendar.DAY_OF_MONTH));
                writeInfo.setYMDToDateStr();
            }

            m_tvDate.setText(writeInfo.getDateFormatted());
        }

        setWeather(writeInfo.getWeather());

        setFeeling(writeInfo.getFeels());

        dataManager.setWriteInfo(writeInfo);
    }

    private void requestSetMissionFailed() {
        SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
        SnapsDiaryUserInfo userInfo = dataManager.getSnapsDiaryUserInfo();
        if(userInfo == null) return;
        SnapsDiaryInterfaceUtil.requestChangeMissionState(this, SnapsDiaryConstants.INTERFACE_CODE_MISSION_STATE_FAILED, userInfo.getMissionNo(), new SnapsDiaryInterfaceUtil.ISnapsDiaryInterfaceCallback() {
            @Override
            public void onPreperation() {
            }

            @Override
            public void onResult(boolean result, Object resultObj) {
                if (result) {
                    SnapsDiaryMissionStateJson missionResult = (SnapsDiaryMissionStateJson) resultObj;
                    SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
                    SnapsDiaryUserInfo userInfo = dataManager.getSnapsDiaryUserInfo();
                    if (userInfo != null) {
                        userInfo.setMissionNo(missionResult.getMissionNo());
                        userInfo.setMissionStat(SnapsDiaryConstants.INTERFACE_CODE_MISSION_STATE_FAILED);
                    }

                    MessageUtil.alertnoTitleOneBtn(SnapsDiarySelectDateWeatherFeelActivity.this, "미션 상태 실패 설정 완료! (일기서비스 종료 후 다시 시작하세요.)", new ICustomDialogListener() {
                        @Override
                        public void onClick(byte clickedOk) {
                            finish();
                        }
                    });
                }
            }
        });
    }
}
