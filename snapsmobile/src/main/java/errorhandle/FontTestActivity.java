package errorhandle;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.image.AsyncTask;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.http.HttpUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import errorhandle.logger.SnapsInterfaceLogDefaultHandler;

/**
 * Created by songhw on 2016. 6. 23..
 */
public class FontTestActivity extends AppCompatActivity {
    private static final String TAG = FontTestActivity.class.getSimpleName();
    private final static String FONT_LIST_URL = "http://www.dropbox.com/s/orvkptghicq6gc4/snaps_font_list.xml?dl=1";
    private final static int BG_COLOR_0 = 0x00FFFFFF;
    private final static int BG_COLOR_1 = 0xAAFFFFFF;
    private final static int BG_COLOR_2 = 0xAACCCCCC;
    private final static int BG_COLOR_NEED_DOWN = 0xAAFFFF00;
    private final static int BG_COLOR_DOWN_FAIL = 0xAAFF0000;
    private final static int BG_COLOR_APPLY_FAIL = 0xAAFF00FF;

    private ArrayList<FontData> fontList = new ArrayList<FontData>();

    private ProgressDialog pd;
    private FontListAdapter adapter;

    private float fontSize = 30f;
    private String customText = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_font_text);

        pd = new ProgressDialog(this);
        pd.setMessage("구성중");
        pd.show();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                getFontListXml();
            }
        });
    }

    public void showTextInput(View v) {
        findViewById(R.id.input_layout).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.input_title)).setText("텍스트 입력");
        ((EditText) findViewById(R.id.input_text)).setText(customText);
        findViewById(R.id.confirm_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setOnClickListener(null);
                customText = ((EditText) findViewById(R.id.input_text)).getEditableText().toString();
                findViewById(R.id.input_layout).setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void showSizeSelect(View v) {
        findViewById(R.id.input_layout).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.input_title)).setText("텍스트 사이즈 입력");
        ((EditText) findViewById(R.id.input_text)).setText(fontSize + "");
        findViewById(R.id.confirm_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setOnClickListener(null);
                String fontSizeStr = ((EditText) findViewById(R.id.input_text)).getEditableText().toString();
                findViewById(R.id.input_layout).setVisibility(View.GONE);

                try {
                    fontSize = Float.parseFloat(fontSizeStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(FontTestActivity.this, "숫자만 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void doSort(View v) {
        ArrayList<FontData> tempList = new ArrayList<FontData>();

        FontData data;
        for (int i = 0; i < fontList.size(); ++i) {
            data = fontList.get(i);
            if (data.status == FontData.STATUS_NORMAL) tempList.add(data);
        }
        for (int i = 0; i < fontList.size(); ++i) {
            data = fontList.get(i);
            if (data.status == FontData.STATUS_NEED_DOWN) tempList.add(data);
        }
        for (int i = 0; i < fontList.size(); ++i) {
            data = fontList.get(i);
            if (data.status == FontData.STATUS_DOWN_FAIL) tempList.add(data);
        }
        for (int i = 0; i < fontList.size(); ++i) {
            data = fontList.get(i);
            if (data.status == FontData.STATUS_APPLY_FAIL) tempList.add(data);
        }

        fontList = tempList;
        adapter.notifyDataSetChanged();
    }

    private void getFontListXml() {
        fontList = new ArrayList<FontData>();

        String xml = HttpUtil.connectGet(FONT_LIST_URL, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser parser = factory.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            FontXmlHandler handler = new FontXmlHandler();
            reader.setContentHandler(handler);
            reader.parse(new InputSource(new ByteArrayInputStream(xml.getBytes())));

            if (pd != null && pd.isShowing() && !this.isFinishing()) pd.dismiss();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    drawList();
                }
            });
        } catch (ParserConfigurationException e) {
            Dlog.e(TAG, e);
        } catch (SAXException e) {
            Dlog.e(TAG, e);
        } catch (IOException e) {
            Dlog.e(TAG, e);
        }
    }

    private void drawList() {
        ListView lv = (ListView) findViewById(R.id.list);
        adapter = new FontListAdapter();
        lv.setAdapter(adapter);

    }

    private File getFontFile(String name) {
        File file = new File(getFontStorageDir() + "/" + name);
        return file;
    }

    private String getFontStorageDir() {
        String dirPath = Const_VALUE.PATH_PACKAGE(this, false) + "/font";

        File file = new File(dirPath);
        if (!file.exists()) file.mkdirs();
        return dirPath;
    }

    private void setTypeFace(View v) {
        TextView title = (TextView) v.findViewById(R.id.text1);
        TextView content = (TextView) v.findViewById(R.id.text2);
        RelativeLayout bg1 = (RelativeLayout) v.findViewById(R.id.bg_layout);
        LinearLayout bg2 = (LinearLayout) v.findViewById(R.id.inner_bg_layout);
        FontData data = (FontData) v.getTag();

        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, UIUtil.convertPixelsToSp(this, fontSize));
        content.setTextSize(TypedValue.COMPLEX_UNIT_SP, UIUtil.convertPixelsToSp(this, fontSize));

        try {
            File font = getFontFile(data.file);
            if (!font.exists()) {
                title.setTypeface(null);
                content.setTypeface(null);
                content.setText("폰트 다운로드 실패");
                bg2.setBackgroundColor(BG_COLOR_DOWN_FAIL);
                data.status = FontData.STATUS_DOWN_FAIL;
                v.invalidate();
                return;
            }

            Typeface tf = Typeface.createFromFile(font);
            title.setTypeface(tf);
            content.setTypeface(tf);
            content.setText(StringUtil.isEmpty(customText) ? "ABCDabcd가나다라!?.," : customText);
            bg2.setBackgroundColor(BG_COLOR_0);
            v.invalidate();
        } catch (Exception e) {
            title.setTypeface(null);
            content.setTypeface(null);
            content.setText("폰트 적용 실패");
            bg2.setBackgroundColor(BG_COLOR_APPLY_FAIL);
            data.status = FontData.STATUS_APPLY_FAIL;
            v.invalidate();
            Dlog.e(TAG, e);
        }
    }

    private class FontListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return fontList.size();
        }

        @Override
        public Object getItem(int position) {
            return fontList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(FontTestActivity.this);
                v = inflater.inflate(R.layout.font_test_item, null);
            } else v = convertView;

            FontData data = fontList.get(position);
            v.setTag(data);

            TextView title = (TextView) v.findViewById(R.id.text1);
            TextView content = (TextView) v.findViewById(R.id.text2);
            RelativeLayout bg1 = (RelativeLayout) v.findViewById(R.id.bg_layout);
            LinearLayout bg2 = (LinearLayout) v.findViewById(R.id.inner_bg_layout);

            bg1.setBackgroundColor(position % 2 == 0 ? BG_COLOR_1 : BG_COLOR_2);

            title.setText(data.name + " / " + data.face);

            File fontFile = getFontFile(data.file);
            if (!fontFile.exists()) {
                content.setText("폰트 다운로드 중입니다.");
                title.setTypeface(null);
                content.setTypeface(null);
                data.status = FontData.STATUS_NEED_DOWN;
                bg2.setBackgroundColor(BG_COLOR_NEED_DOWN);
                FontDownloader downloader = new FontDownloader(v);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) downloader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                else downloader.execute();
            } else setTypeFace(v);

            ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) v.getLayoutParams();
            if (params == null) params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UIUtil.convertDPtoPX(FontTestActivity.this, 100));
            else params.height = UIUtil.convertDPtoPX(FontTestActivity.this, 80);
            v.setLayoutParams(params);

            return v;
        }
    }

    private class FontXmlHandler extends DefaultHandler {
        @Override
        public void startDocument() throws SAXException {
        }

        @Override
        public void endDocument() throws SAXException {
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (!StringUtil.isEmpty(attributes.getValue("name"))) {
                FontData data = new FontData(attributes.getValue("name"), attributes.getValue("face"), attributes.getValue("file"), attributes.getValue("etc"));
                if (!getFontFile(data.file).exists()) data.status = FontData.STATUS_NEED_DOWN;
                fontList.add(data);
            }
        }
    }

    private class FontData {
        public static final int STATUS_NORMAL = 0;
        public static final int STATUS_NEED_DOWN = 1;
        public static final int STATUS_DOWN_FAIL = 2;
        public static final int STATUS_APPLY_FAIL = 3;


        public String name, face, file, etc;
        public int status = STATUS_NORMAL;

        public FontData(String name, String face, String file, String etc) {
            this.name = name;
            this.face = face;
            this.file = file;
            this.etc = etc;
        }

        public FontData(String name, String face, String file) {
            this.name = name;
            this.face = face;
            this.file = file;
        }
    }

    private class FontDownloader extends AsyncTask<Void, Void, Boolean> {
        private Object tag;
        private View view;

        public FontDownloader(View v) {
            view = v;
            tag = v.getTag();
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            URL url;
            try {
                url = new URL(SnapsAPI.DOMAIN() + "/Upload/Data1/Resource/Font/" + ((FontData) tag).file);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();

                InputStream is = new BufferedInputStream(url.openStream());
                File output = getFontFile(((FontData) tag).file);
                OutputStream os = new FileOutputStream(output);
                byte[] data = new byte[1024];

                int count = 0;
                while ((count = is.read(data)) != -1) {
                    os.write(data, 0, count);
                }

                os.flush();
                os.close();
                is.close();
            } catch (MalformedURLException e) {
                Dlog.e(TAG, e);
                return false;
            } catch (IOException e) {
                Dlog.e(TAG, e);
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean param) {
            if (!view.getTag().equals(tag)) return;
            setTypeFace(view);
        }
    }
}
