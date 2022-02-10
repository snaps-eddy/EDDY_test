package com.snaps.mobile.cseditor.api;

import androidx.annotation.NonNull;

import com.snaps.mobile.cseditor.CSEditorContract;
import com.snaps.mobile.cseditor.api.response.ResponseGetProjectDetail;
import com.snaps.mobile.cseditor.network.NetworkServiceGenerator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetProjectDetailIntractorImpl implements CSEditorContract.GetProjectDetailIntractor {

    @Override
    public void requestGetProjectDetail(String projectCode, OnFinishedListener onFinishedListener) {
        SnapsProjectService service = NetworkServiceGenerator.createService(SnapsProjectService.class);

        Call<ResponseGetProjectDetail> call = service.getProjectDetail("admin.common.EditorCS", "getProjectDetail", projectCode);
        call.enqueue(new Callback<ResponseGetProjectDetail>() {
            @Override
            public void onResponse(@NonNull Call<ResponseGetProjectDetail> call, @NonNull Response<ResponseGetProjectDetail> response) {
                onFinishedListener.onFinished(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<ResponseGetProjectDetail> call, @NonNull Throwable t) {
                onFinishedListener.onFailure(t);
            }
        });
    }
}
