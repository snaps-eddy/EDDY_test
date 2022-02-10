package com.snaps.mobile.cseditor.api;

import com.snaps.mobile.cseditor.api.response.ResponseGetProjectDetail;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SnapsProjectService {

    @GET("/servlet/Command.do?")
    Call<ResponseGetProjectDetail> getProjectDetail(@Query("part") String part, @Query("cmd") String cmd, @Query("projectCode") String projectCode);

}
