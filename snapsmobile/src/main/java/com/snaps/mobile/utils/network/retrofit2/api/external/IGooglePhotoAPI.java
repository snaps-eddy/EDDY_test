package com.snaps.mobile.utils.network.retrofit2.api.external;


import io.reactivex.Flowable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface IGooglePhotoAPI {

    @GET("/v1/albums")
    Flowable<Response<String>> requestAPIAlbum(@Header("Accept") String json, @Header("Authorization") String authHeader);

    @POST("/v1/mediaItems:search")
    Flowable<Response<String>> requestAPIPhotoList(@Header("Accept") String jsonAccept, @Header("Content-Type") String jsonContentType, @Header("Authorization") String authHeader, @Body String jsonBody);

    @GET("/v1/mediaItems")
    Flowable<Response<String>> requestAPIMediaList(@Header("Accept") String jsonAccept, @Header("Content-Type") String jsonContentType, @Header("Authorization") String authHeader, @Query("pageSize") int size, @Query("pageToken") String token);
}
