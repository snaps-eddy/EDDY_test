package com.snaps.common.utils.ui;

import android.app.Activity;
import android.content.Context;
import android.widget.ArrayAdapter;

import com.snaps.common.data.img.ImageSelectSNSImageData;
import com.snaps.common.data.img.MyKakaoStoryImageData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class IKakao {

    public interface ISNSPhotoHttpHandler {
        void onSNSPhotoHttpResult(ArrayList<ImageSelectSNSImageData> result);

        void onSNSPhotoError(int errorCode, String errorMessage);
    }


    public abstract boolean isKakaoLogin();

    public abstract void onKakaoClickLogout();
//	public abstract void initKakoLoginButton();

    public abstract String getLastID();

    public abstract void sendKakaoData(Context context);

    public abstract void startKakaoLoginActivity(Context context);

    public abstract String createKakaoInstance(Context context);

    public abstract void assignMessageBuilder();

    public abstract void initializeSession(Context context);

//	public abstract void getRequestKakao(String lastid, final int photoCount, final ArrayAdapter<MyKakaoStoryImageData> adapter, final CustomFragment _customFragment);

    public abstract void getRequestKakao(String lastid, int count, ISNSPhotoHttpHandler handler, final ArrayList<ImageSelectSNSImageData> dataList);

    /***
     *
     * @param context
     * @param text
     * @param url
     * @param urlText
     * @param imagUrl
     * @param imgWidth
     * @param imgHeight
     */
    public abstract void sendInviteMessage(Context context, String text, String url, String urlText, String imagUrl, String imgWidth, String imgHeight, String isRunApp);

    public abstract void sendInviteMessage(Context context, String text, String url, String urlText, String imagUrl, String imgWidth, String imgHeight, String isRunApp, String executeParams);

    public abstract void sendInviteFriend(Context context, String content, String buttonTitle, String imagUrl, String imgWidth, String imgHeight, String parameter);

    public abstract boolean sendPostingLink(Activity activity, String url, String title, String desc, String imgUrl, String type);

    public abstract void sendPostingLink(String url, String content, IPostingResult listener);

    // public abstract void sendPostingPhoto(final String[] imageURLs, final String content, IPostingResult listener);
    public abstract void sendPostingPhoto(final List<File> files, final String content, IPostingResult listener);

}
