package com.snaps.mobile;

import android.content.Context;

import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.cseditor.network.NetworkServiceGenerator;
import com.snaps.mobile.cseditor.api.SnapsProjectService;
import com.snaps.mobile.cseditor.api.response.ResponseGetProjectDetail;
import com.snaps.mobile.cseditor.model.SnapsSchemeURL;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
@RunWith(MockitoJUnitRunner.class)
public class ExampleUnitTest {
    private static final String TAG = ExampleUnitTest.class.getSimpleName();

    private String originalURL;

    @Mock
    private Context context;

    @Before
    public void setUp() {
        originalURL = "snapsapp://preview?dumy=&productCode=00800900110003&prjCode=20190704006681&templateCode=045021006503&smartYN=&unitPrice=0";
    }

    @Test
    public void testExtractWebURL() {

        SnapsSchemeURL snapsSchema = new SnapsSchemeURL(originalURL);

        assertThat("20190704006681", is(snapsSchema.getProjectCode()));
        assertThat("00800900110003", is(snapsSchema.getProductCode()));
        assertThat("045021006503", is(snapsSchema.getTemplateCode()));

    }

    @Test
    public void testImplyWebURL() {
        SnapsSchemeURL url = new SnapsSchemeURL("20190704006681", "00800900110003", "045021006503");

        assertThat("snapsapp://preview?dumy=&productCode=00800900110003&prjCode=20190704006681&templateCode=045021006503&smartYN=&unitPrice=0", is(url.getImpliedURL()));
    }

    @Test
    public void testGetSnapsProject() {
        SnapsProjectService service = NetworkServiceGenerator.createService(SnapsProjectService.class);

        Call<ResponseGetProjectDetail> call = service.getProjectDetail("admin.common.EditorCS", "getProjectDetail", "20060717000341");

        try {
            Response<ResponseGetProjectDetail> response = call.execute();
            ResponseGetProjectDetail detail = response.body();

            assertThat("20060717000341", is(detail.getProjectCode()));
            assertThat("00800500020001", is(detail.getProductCode()));
            assertThat("045001000003", is(detail.getTemplateCode()));

        } catch (IOException e) {
            Dlog.e(TAG, e);
        }

    }

}