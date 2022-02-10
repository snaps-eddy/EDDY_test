package com.snaps.mobile.utils.network.retrofit2.exception;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.utils.network.retrofit2.data.response.common.SnapsNetworkAPIBaseResponse;

import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;

import errorhandle.logger.Logg;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;

public class SnapsNetworkThrowable {
    private static final String TAG = SnapsNetworkThrowable.class.getSimpleName();

    public static final int UNKNOWN_RESULT_CODE = -999;
    public enum eThrowableType {
        HTTP_EXCEPTION,
        SOCKET_TIME_OUT,
        IO_EXCEPTION,
        UNKNOWN
    }

    private eThrowableType errorType = eThrowableType.UNKNOWN;

    private String statusCode = null;
    private String message = null;
    private String errorMessage = null;
    private String errorCode = null;

    private Throwable throwable = null;

    public static SnapsNetworkThrowable withErrorMessage(String message) {
        return convertThrowable(new SnapsNetworkException(message));
    }

    public static SnapsNetworkThrowable withException(Exception e) {
        return convertThrowable(new SnapsNetworkException(e != null ? e.toString() : ""));
    }

    public static SnapsNetworkThrowable convertThrowable(Throwable e) {
        SnapsNetworkThrowable networkThrowable = new SnapsNetworkThrowable();
        if (e == null) return networkThrowable;

        Dlog.e(TAG, e);

        networkThrowable.setThrowable(e);
        networkThrowable.setMessage(e.getMessage());

        if (e instanceof HttpException) {
            networkThrowable.setErrorType(eThrowableType.HTTP_EXCEPTION);

            parseErrorBody(networkThrowable, e);
        } else if (e instanceof SocketTimeoutException) {
            networkThrowable.setErrorType(eThrowableType.SOCKET_TIME_OUT);
        } else if (e instanceof IOException) {
            networkThrowable.setErrorType(eThrowableType.IO_EXCEPTION);
        } else {

        }

        return networkThrowable;
    }

    public static SnapsNetworkThrowable convertThrowable(SnapsNetworkAPIBaseResponse baseResponse) {
        SnapsNetworkThrowable networkThrowable = new SnapsNetworkThrowable();
        if (baseResponse == null) return networkThrowable;

        String errMsg = baseResponse.getStatus() + ", " + baseResponse.getErrorCode() + ", " + baseResponse.getErrorMessage() + ", " + baseResponse.getMessage();
        networkThrowable.setThrowable(new SnapsNetworkException(errMsg));
        networkThrowable.setMessage(errMsg);

        return networkThrowable;
    }

    public static SnapsNetworkThrowable convertThrowable(Response response) {
        SnapsNetworkThrowable networkThrowable = new SnapsNetworkThrowable();
        if (response == null) return networkThrowable;
        try {
            ResponseBody responseBody = response.errorBody();
            if (responseBody != null) {
                parseErrorBody(networkThrowable, responseBody.string());
            }
        } catch (IOException e) {
            Dlog.e(TAG, e);
        }
//        String errMsg = response.code() + ", " + response.errorBody(); //FIXME....
//        networkThrowable.setThrowable(new SnapsNetworkException(errMsg));
//        networkThrowable.setMessage(errMsg);

        return networkThrowable;
    }

    private void setErrorType(eThrowableType errorType) {
        this.errorType = errorType;
    }

    private void setMessage(String message) {
        this.message = message;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    private void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public eThrowableType getErrorType() {
        return errorType;
    }

    public String getMessage() {
        return message;
    }

    public String getValueFromErrorBody() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    private static void parseErrorBody(SnapsNetworkThrowable networkThrowable, Throwable e) {
        try {
            ResponseBody responseBody = ((HttpException)e).response().errorBody();
            if (responseBody == null) return;
            String errorBodyStr = responseBody.string();
            parseErrorBody(networkThrowable, errorBodyStr);
        } catch (Exception e2) {
            Dlog.e(TAG, e);
        }
    }

    private static void parseErrorBody(SnapsNetworkThrowable networkThrowable, String errMsgJson) {
        try {
            if (StringUtil.isEmpty(errMsgJson)) return;
            Dlog.w(TAG, "Retrofit onResultFailed:" + errMsgJson);
            JSONObject jsonObject = new JSONObject(errMsgJson);
            JSONObject errJSON = jsonObject.getJSONObject("error");

            networkThrowable.setErrorCode(errJSON.getString("code"));
            networkThrowable.setStatusCode(errJSON.getString("status"));
            networkThrowable.setMessage(errJSON.getString("message"));
//            networkThrowable.setErrorMessage(errJSON.getString("errorMessage"));


        } catch (Exception e2) {
            Dlog.e(TAG, e2);
        }
    }
}
