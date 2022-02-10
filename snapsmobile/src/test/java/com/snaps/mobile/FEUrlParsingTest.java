package com.snaps.mobile;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.snaps.common.structure.vo.AccessoriesOption;
import com.snaps.common.utils.log.Dlog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Iterator;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class FEUrlParsingTest {

    private URLUnpacker unpacker;
    private String rawFEParams = "{\"prmChnlCode\":\"KOR0031\",\"prmLangCode\":\"KOR\",\"prmProdCode\":\"00802800060001\",\"prmTmplCode\":\"045021016582\",\"prmTmplId\":\"ACRKRMT_test_lr\",\"prmPaperCode\":\"160902\",\"prmGlossyType\":\"M\",\"prmBackType\":\"397005\",\"productCode\":\"00802800060001\",\"paperCode\":\"160902\",\"glossytype\":\"M\",\"backType\":\"397005\",\"papertype\":\"160902\",\"millimeterWidth\":\"100\",\"millimeterHeight\":\"100\",\"accessory\":[{\"templateCode\":\"045025000016\",\"productCode\":\"00802500010010\",\"quantity\":1},{\"templateCode\":\"045025000018\",\"productCode\":\"00802500010010\",\"quantity\":1}],\"projectCount\":1}";

    @Before
    public void before() {
        unpacker = new URLUnpacker();
    }

    @Test
    public void test_making_url() throws JSONException {
        JSONObject jsonObject = new JSONObject(rawFEParams);
        String makingURL = unpacker.makeUrlFromJson(jsonObject, false);

        assertThat(makingURL).isNotEmpty();
        assertThat(makingURL).isNotNull();
        assertThat(makingURL).isNotBlank();
    }

    @Test
    public void parseParameters() throws JSONException {
        JSONObject jsonObject = new JSONObject(rawFEParams);
        String makingURL = unpacker.makeUrlFromJson(jsonObject, false);
        HashMap<String, String> result = unpacker.unpack(makingURL);

        assertThat(result).isNotNull();
        assertThat(result.get("accessory")).isEqualTo("[{\"productCode\":\"00802500010010\",\"quantity\":1,\"templateCode\":\"045025000016\"},{\"productCode\":\"00802500010010\",\"quantity\":1,\"templateCode\":\"045025000018\"}]");
    }

    @Test
    public void test_parse_accessories() throws JSONException {
        JSONObject jsonObject = new JSONObject(rawFEParams);
        String makingURL = unpacker.makeUrlFromJson(jsonObject, false);
        HashMap<String, String> result = unpacker.unpack(makingURL);
        String accessories = result.get("accessory");

        JSONArray jsonAcc = new JSONArray(accessories);
        assertThat(jsonAcc).isNotNull();
        assertThat(jsonAcc.length()).isEqualTo(2);

        Gson gson = new Gson();

        AccessoriesOption ao1 = gson.fromJson(jsonAcc.get(0).toString(), AccessoriesOption.class);
        AccessoriesOption ao2 = gson.fromJson(jsonAcc.get(1).toString(), AccessoriesOption.class);

        assertThat(ao1.getProductCode()).isEqualTo("00802500010010");
        assertThat(ao1.getQuantity()).isEqualTo(1);
        assertThat(ao1.getTemplateCode()).isEqualTo("045025000016");

        assertThat(ao2.getProductCode()).isEqualTo("00802500010010");
        assertThat(ao2.getQuantity()).isEqualTo(1);
        assertThat(ao2.getTemplateCode()).isEqualTo("045025000018");

    }

    /**
     * Just moving from Config
     */
    static class URLUnpacker {

        public static final String TAG = URLUnpacker.class.getSimpleName();

        public String makeUrlFromJson(JSONObject jsonObject, boolean bPreview) {
            StringBuilder snapsUrl = new StringBuilder(bPreview ? "snapsapp://preview?dumy=&" : "snapsapp://selectProduct?");
            Iterator<String> iter = jsonObject.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                try {

                    Object value = jsonObject.get(key);
                    snapsUrl.append(key).append("=").append((value == null || value.toString().equalsIgnoreCase("null")) ? "" : value.toString()).append("&");

                } catch (JSONException e) {
                    Dlog.e(TAG, e);
                }
            }
            return snapsUrl.toString();
        }

        public HashMap<String, String> unpack(String url) {
            if (url == null) return null;

            String SCHMA = "snapsapp://";

            HashMap<String, String> hashmap = new HashMap<String, String>();

            if (url.startsWith(SCHMA)) {

                String params = "";

                //snapsapp://scheme?body 형식으로 변경되어서 추가한 코드
                if (isOldStyleCmdKey(url)) {
                    params = url.substring(SCHMA.length());
                } else {
                    //신형식..
                    if (url.contains("?")) {
                        params = url.substring(url.indexOf("?") + 1);
                    } else {
                        params = url.substring(SCHMA.length());
                    }
                }

                params = params.replace("?", "&");
                String[] arParams1 = params.split("&");

                for (String find : arParams1) {
                    String[] jsonparam = find.split("=");
                    String key = null, value = null;
                    if (jsonparam.length > 1) {
                        key = jsonparam[0];
                        value = jsonparam[1];
                    }

                    if (key != null && key.length() > 0 && value != null && value.length() > 0)
                        hashmap.put(key, value);
                }
            }
            return hashmap;
        }

        public boolean isOldStyleCmdKey(String url) {
            return url != null && url.startsWith("snapsapp://cmd");
        }
    }

}




