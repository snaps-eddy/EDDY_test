package com.snaps.mobile.utils.sns.googlephoto;

/**
 * Created by ysjeong on 2017. 5. 19..
 */

public interface GoogleApiConstants {
    String GOOGLE_PHOTO_WEB_CLIENT_ID = "639058883054-ddgbho0brp85gkcdiuhrb01e9rigrrlm.apps.googleusercontent.com";
    String GOOGLE_PHOTO_CLIENT_SECRET = "ScxzBZ0v7BJevBDhZ5KKKVnM";

    String GOOGLE_AUTH_TOKEN_REQUEST_URL = "https://www.googleapis.com/oauth2/v4/token";
//    String GOOGLE_PHOTO_SCOPE = "https://picasaweb.google.com/data/";
    String GOOGLE_PHOTO_SCOPE = "https://www.googleapis.com/auth/photoslibrary https://www.googleapis.com/auth/photoslibrary.readonly https://www.googleapis.com/auth/photoslibrary.readonly.appcreateddata";

    String GOOGLE_PHOTO_ALBUM_REUQUEST_URL = "https://picasaweb.google.com/data/feed/api/user/default";


//    String GOOGLE_PHOTO_LIBRARY_SCOPE = "oauth2:https://www.googleapis.com/auth/photoslibrary.readonly";
    String GOOGLE_PHOTO_LIBRARY_SCOPE = "oauth2:https://www.googleapis.com/auth/photoslibrary https://www.googleapis.com/auth/photoslibrary.readonly https://www.googleapis.com/auth/photoslibrary.readonly.appcreateddata";

    String GOOGLE_PHOTO_LIBRARY_DOMAIN = "https://photoslibrary.googleapis.com/";

    int REQUEST_CODE_FOR_GOOGLE_SIGN_IN = 9000;

    int ONCE_LOAD_CNT = 24;
}
