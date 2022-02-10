package com.snaps.common.utils.net;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.thread.ATask;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NTPClient {
    private static final String TAG = NTPClient.class.getSimpleName();
    private static final String TIME_SERVER = "time-a.nist.gov";

    public interface Listener {
        void onResult(long time);
        void onError(Exception e);
    }

    public NTPClient() {
    }

    public boolean getTime(Listener listener) {
        if (listener == null) return false;

        ATask.executeBoolean(new ATask.OnTaskResult() {
            private long mTime = 0;
            private Exception mException = null;

            @Override
            public void onPre() {
            }

            @Override
            public boolean onBG() {
                try {
                    NTPUDPClient timeClient = new NTPUDPClient();
                    InetAddress inetAddress = InetAddress.getByName(TIME_SERVER);
                    TimeInfo timeInfo = timeClient.getTime(inetAddress);
                    mTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
                    Dlog.d("time:" + mTime);
                }catch (Exception e) {
                    Dlog.e(TAG, e);
                    mException = e;
                    return false;
                }
                return true;
            }

            @Override
            public void onPost(boolean result) {
                if (result) {
                    listener.onResult(mTime);
                }
                else {
                    listener.onError(mException);
                }
            }
        });

        return true;
    }


    //일기 서비스 종료 대응
    //2020년 2월 스냅스 일기 서비스 종료 예정 (서버 저장 공간이 110TB인데 앞으로 한달 정도 분량의 용량만 남은 상태여서 일기 서비스 종료)
    //일기 서비스 종료 할 것인데 기준일 부터는 일기 쓰기가 안되도록 처리할 예정
    //일스 쓰기 막는 기능을 구현할 것인데 그래도 혹시나 버그로 일기 쓰기가 가능 할지도 몰라서 현재 날짜를 검사해서 일정 시점 이후에 일기 쓰기를 막는 2차 방어 코드를 만들 필요성이 있음
    //2차 방어가 코드의 일부가 아래 메소드
    private static volatile boolean IS_DEFAULT_BLOCK = false;   //TODO::일기 서비스 1차 종료 시점에 true로 만들어서 배포 필요

    private static volatile boolean sIsEnableDiaryNewOrEdit = !IS_DEFAULT_BLOCK;
    private static final String DIARY_BLOCK_TIME_URL = "https://www.snaps.com/Upload/Data1/mobile/cs/diary_block_time.txt";

    public static boolean isEnableDiaryNewOrEdit() {
        return sIsEnableDiaryNewOrEdit;
    }

    public static void checkEnableDiaryNewOrEdit() {

        checkTime(DIARY_BLOCK_TIME_URL, new TimeoutCheckListener() {
            @Override
            public void onResult(boolean isExcess) {
                sIsEnableDiaryNewOrEdit = !isExcess;
            }

            @Override
            public void onError(Exception e) {
                Dlog.e(TAG, e);

                if (IS_DEFAULT_BLOCK) {
                    sIsEnableDiaryNewOrEdit = false;
                }
            }
        });
    }

    private interface TimeoutCheckListener {
        void onResult(boolean isExcess);
        void onError(Exception e);
    }


    private static boolean checkTime(String timeInfoURL, TimeoutCheckListener timeoutCheckListener) {
        if (timeInfoURL == null || timeInfoURL.length() == 0) return false;
        if (timeoutCheckListener == null) return false;

        try {
            new URL(timeInfoURL);
        }catch (Exception e) {
            Dlog.e(TAG, e);
            return false;
        }

        ATask.executeBoolean(new ATask.OnTaskResult() {
            private long mTime = 0;
            private long mNTPTime = 0;
            private Exception mException = null;

            @Override
            public void onPre() {
            }

            @Override
            public boolean onBG() {
                final String dateFormat = "yyyy-MM-dd HH:mm:ss";
                final int MAX_RETRY_COUNT = 10;

                Calendar currentCal = Calendar.getInstance();
                currentCal.setTime(new Date());
                currentCal.add(Calendar.YEAR, -10);
                long minTime = currentCal.getTimeInMillis();

                for(int i = 0; i < MAX_RETRY_COUNT; i++) {
                    try {
                        StringBuilder sb = new StringBuilder();
                        URL url = new URL(timeInfoURL);
                        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
                        for (String line; (line = reader.readLine()) != null;) {
                            sb.append(line.trim());
                        }
                        String timeText = sb.toString();
                        if (timeText.length() != dateFormat.length()) throw new InvalidParameterException();
                        SimpleDateFormat sf = new SimpleDateFormat(dateFormat);
                        Date date = sf.parse(timeText);
                        Dlog.d("timeInfoURL : " + timeInfoURL + " >> " + sf.format(date));
                        mTime = date.getTime();

                        NTPUDPClient timeClient = new NTPUDPClient();
                        InetAddress inetAddress = InetAddress.getByName(TIME_SERVER);
                        TimeInfo timeInfo = timeClient.getTime(inetAddress);
                        mNTPTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
                        if (minTime > mNTPTime) {
                            //혹시나 타임 서버가 죽어서 1970년 뭐 이런 경우가 발생할까봐.. 이건 거의 희박 희박
                            throw new InvalidParameterException();
                        }

                        if (mNTPTime < 1) throw new InvalidParameterException();
                        Dlog.d("NTP time : " + sf.format(new Date(mNTPTime)));
                        return true;
                    } catch (Exception e) {
                        Dlog.e(TAG, e);
                        mException = e;
                    }

                    try {
                        Thread.sleep(200);
                    }catch (Exception e) {
                        Dlog.e(TAG, e);
                    }
                }

                return false;
            }

            @Override
            public void onPost(boolean result) {
                if (result) {
                    timeoutCheckListener.onResult(mNTPTime > mTime);
                }
                else {
                    timeoutCheckListener.onError(mException);
                }
            }
        });

        return true;
    }
}
