package com.snaps.mobile.service.ai;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface SyncPhotoServerAPI {
    @GET("v1.0/m/api/exif/{userNo}/sync")
    Call<ResponseBody> getConfirmSentExifZipFile(
            @Path("userNo") String userNo,
            @QueryMap(encoded = true) Map<String, String> params);

    @Multipart
    @POST("v1.0/m/api/exif/{userNo}/file/{syncType}")
    Call<ResponseBody> postExifZipFile(
            @Path("userNo") String userNo,
            @Path("syncType") String syncType,
            @Part MultipartBody.Part metaFile,
            @Part("fileSize") RequestBody fileSize);


    @GET("v1.0/m/api/sync/images/{userNo}")
    Call<ResponseBody> getUploadThumbImgList(
            @Path("userNo") String userNo,
            @QueryMap(encoded = true) Map<String, String> params);


    @Multipart
    @POST("v1.0/m/api/image/{userNo}/file/{lastFileYN}")
    Call<ResponseBody> postThumbImg(
            @Path("userNo") String userNo,
            @Path("lastFileYN") String lastFileYN,
            @Part MultipartBody.Part middleFile,
            @PartMap Map<String, RequestBody> params);
}
