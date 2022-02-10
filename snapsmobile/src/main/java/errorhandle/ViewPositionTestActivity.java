package errorhandle;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.snaps.common.utils.pref.Setting;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.home.model.ImageData;
import com.snaps.mobile.activity.home.model.PageDataSet;
import com.snaps.mobile.activity.home.model.TextData;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by songhw on 2016. 7. 12..
 */
public class ViewPositionTestActivity extends AppCompatActivity {
    private ArrayList<PageDataSet> pageDataList;
    private TextView infoView;
    private FrameLayout layout;
    private int screenW, screenH, pageIdx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Point displaySize = new Point();
        getWindowManager().getDefaultDisplay().getSize(displaySize);
        screenW = displaySize.x;
        screenH = displaySize.y;

        pageIdx = 0;

        layout = new FrameLayout(this);
        setContentView(layout);

        loadPages();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 1, Menu.NONE, "텍스트 추가");
        menu.add(Menu.NONE, 2, Menu.NONE, "이미지 추가");
        menu.add(Menu.NONE, 3, Menu.NONE, "사이즈 변경");
        menu.add(Menu.NONE, 4, Menu.NONE, "페이지 변경");
        menu.add(Menu.NONE, 5, Menu.NONE, "새 페이지");
        menu.add(Menu.NONE, 6, Menu.NONE, "초기화");

        return super.onCreateOptionsMenu(menu);
    }

    private void loadPages() {
        int pageCount = Setting.getInt(this, "test_layout_count");

        pageDataList = new ArrayList<PageDataSet>();
        if (pageCount > 0) {
            Gson gson = new Gson();
            for (int i = 0; i < pageCount; ++i) pageDataList.add(gson.fromJson(Setting.getString(this, "test_layout_" + i), PageDataSet.class));
        }

        if (pageDataList.size() < 1) createPage();
        changePage(0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                showPopup(TYPE_TEXT);
                break;
            case 2:
                showPopup(TYPE_IMAGE);
                break;
            case 3:
                showPopup(TYPE_SIZE);
                break;
            case 4:
                showPopup(TYPE_PAGE);
                break;
            case 5:
                createPage();
                changePage(pageDataList.size() - 1);
                break;
            case 6:
                initSaves();
                createPage();
                changePage(0);

        }

        return super.onOptionsItemSelected(item);
    }

    private void createPage() {
        PageDataSet dataSet = new PageDataSet(screenW, screenH);
        pageDataList.add(dataSet);
    }

    private void changePage(int index) {
        pageIdx = index;

        layout.removeAllViews();
        PageDataSet item = pageDataList.get(pageIdx);
        drawBorder(item);

        ArrayList<TextData> textDataList = (ArrayList<TextData>) item.textList.clone();
        ArrayList<ImageData> imageDataList = (ArrayList<ImageData>) item.imageList.clone();
        while ((textDataList != null && textDataList.size() > 0) || (imageDataList != null && imageDataList.size() > 0)) {
            TextData tempText = textDataList != null && textDataList.size() > 0 ? textDataList.get(0) : null;
            ImageData tempImage = imageDataList != null && imageDataList.size() > 0 ? imageDataList.get(0) : null;
            if (tempText != null && tempImage != null) {
                if (tempText.z < tempImage.z) tempImage = null;
                else tempText = null;
            }

            if (tempImage != null) {
                drawImage(tempImage, false);
                imageDataList.remove(tempImage);
            } else if (tempText != null) {
                drawText(tempText, false);
                textDataList.remove(tempText);
            }
        }

        infoView = new TextView(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics()));
        infoView.setLayoutParams(params);
        infoView.setBackgroundColor(0x33000000);
        infoView.setTextColor(0xFF000000);
        infoView.setText("page" + pageIdx + " (page size: " + pageDataList.get(pageIdx).w + "x" + pageDataList.get(pageIdx).h + " / screen size: " + screenW + "x" + screenH + ")");
        infoView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        infoView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);

        layout.addView(infoView);
    }

    private void drawBorder(PageDataSet page) {
        RelativeLayout layout = new RelativeLayout(this);
        FrameLayout.LayoutParams fParams = new FrameLayout.LayoutParams(screenW, (int) ((float) screenW / (float) page.w * (float) page.h));
        layout.setLayoutParams(fParams);

        ImageView iv1 = new ImageView(this);
        RelativeLayout.LayoutParams rParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        iv1.setLayoutParams(rParams);
        iv1.setBackgroundColor(0xFF0000FF);

        ImageView iv2 = new ImageView(this);
        rParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        rParams.setMargins(5, 5, 5, 5);
        iv2.setLayoutParams(rParams);
        iv2.setBackgroundColor(0xFFFFF0FA);

        layout.addView(iv1);
        layout.addView(iv2);
        this.layout.addView(layout);
    }

    private static final int TYPE_TEXT = 0;
    private static final int TYPE_IMAGE = 1;
    private static final int TYPE_SIZE = 2;
    private static final int TYPE_PAGE = 3;

    private void showPopup(int type) {
        CustomDialog dialog;
        switch (type) {
            case TYPE_TEXT:
                dialog = new CustomDialog(this, R.layout.view_test_alert_text);
                dialog.show();
                break;
            case TYPE_IMAGE:
                dialog = new CustomDialog(this, R.layout.view_test_alert_image);
                dialog.show();
                break;
            case TYPE_SIZE:
                dialog = new CustomDialog(this, R.layout.view_test_alert_size);
                dialog.show();
                break;
            case TYPE_PAGE:
                if (pageDataList != null && pageDataList.size() > 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("select page");

                    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice);
                    for (int i = 0; i < pageDataList.size(); ++i) adapter.add("page" + i);
                    builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            changePage(which);
                        }
                    });
                    builder.create().show();
                }
                break;
        }
    }

    private void drawText(TextData data, boolean needSave) {
        if (data.z < 0) {
            int zIndex = 0;
            for (TextData item : pageDataList.get(pageIdx).textList) if (zIndex < item.z) zIndex = item.z;
            for (ImageData item : pageDataList.get(pageIdx).imageList) if (zIndex < item.z) zIndex = item.z;
            data.z = ++zIndex;
        }

        Random rnd = new Random();
        TextView tv = new TextView(this);
        float x, y, w, h;
        x = (float) data.x / (float) pageDataList.get(pageIdx).w * (float) screenW;
        y = (float) data.y / (float) pageDataList.get(pageIdx).w * (float) screenW;
        w = (float) data.w / (float) pageDataList.get(pageIdx).w * (float) screenW;
        h = (float) data.h / (float) pageDataList.get(pageIdx).w * (float) screenW;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int) w, (int) h);
        params.leftMargin = (int) x;
        params.topMargin = (int) y;
        tv.setLayoutParams(params);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        tv.setBackgroundColor(Color.argb(30, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));
        tv.setGravity(data.ori);
        tv.setText(data.text);

        layout.addView(tv);
        if (infoView != null) infoView.bringToFront();

        if (needSave) {
            pageDataList.get(pageIdx).textList.add(data);
            savePages();
        }
    }

    private void drawImage(ImageData data, boolean needSave) {
        int zIndex = 0;
        for (TextData item : pageDataList.get(pageIdx).textList) if (zIndex < item.z) zIndex = item.z;
        for (ImageData item : pageDataList.get(pageIdx).imageList) if (zIndex < item.z) zIndex = item.z;
        data.z = ++zIndex;

        ImageView iv = new ImageView(this);
        float x, y, w, h;
        x = (float) data.x / (float) pageDataList.get(pageIdx).w * (float) screenW;
        y = (float) data.y / (float) pageDataList.get(pageIdx).w * (float) screenW;
        w = (float) data.w / (float) pageDataList.get(pageIdx).w * (float) screenW;
        h = (float) data.h / (float) pageDataList.get(pageIdx).w * (float) screenW;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int) w, (int) h);
        params.leftMargin = (int) x;
        params.topMargin = (int) y;
        iv.setLayoutParams(params);
        iv.setScaleType(ImageView.ScaleType.FIT_XY);
        //iv.setImageResource(R.drawable.bg_upload);

        layout.addView(iv);
        if (infoView != null) infoView.bringToFront();

        if (needSave) {
            pageDataList.get(pageIdx).imageList.add(data);
            savePages();
        }
    }

    private void initSaves() {
        pageDataList = new ArrayList<PageDataSet>();
        Setting.set(this, "test_layout_count", 0);
    }

    private void savePages() {
        if (pageDataList == null) return;
        for (int i = 0; i < pageDataList.size(); ++i) {
            Gson gson = new Gson();
            Setting.set(this, "test_layout_" + i, gson.toJson(pageDataList.get(i)));
        }
        Setting.set(this, "test_layout_count", pageDataList.size());
    }

    public class CustomDialog extends Dialog implements View.OnClickListener {
        private int layoutId;

        public CustomDialog(Context context, int layoutId) {
            super(context);
            this.layoutId = layoutId;
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(layoutId);
            ((TextView) findViewById(R.id.confirm)).setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.confirm) {
                if (layoutId == R.layout.view_test_alert_text) {
                    TextData data = new TextData();
                    try {
                        data.x = Integer.parseInt(((EditText) findViewById(R.id.pos_x)).getEditableText().toString());
                        data.y = Integer.parseInt(((EditText) findViewById(R.id.pos_y)).getEditableText().toString());
                        data.w = Integer.parseInt(((EditText) findViewById(R.id.width)).getEditableText().toString());
                        data.h = Integer.parseInt(((EditText) findViewById(R.id.height)).getEditableText().toString());
                    } catch (NumberFormatException e) {
                        Toast.makeText(ViewPositionTestActivity.this, "input number", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                    data.text = ((EditText) findViewById(R.id.text)).getEditableText().toString();
                    String oriStr = ((EditText) findViewById(R.id.orientation)).getEditableText().toString();
                    data.ori = ("r".equalsIgnoreCase(oriStr) ? Gravity.RIGHT : "c".equalsIgnoreCase(oriStr) ? Gravity.CENTER : Gravity.LEFT) | Gravity.CENTER_VERTICAL;

                    drawText(data, true);
                    dismiss();
                } else if (layoutId == R.layout.view_test_alert_image) {
                    ImageData data = new ImageData();
                    try {
                        data.x = Integer.parseInt(((EditText) findViewById(R.id.pos_x)).getEditableText().toString());
                        data.y = Integer.parseInt(((EditText) findViewById(R.id.pos_y)).getEditableText().toString());
                        data.w = Integer.parseInt(((EditText) findViewById(R.id.width)).getEditableText().toString());
                        data.h = Integer.parseInt(((EditText) findViewById(R.id.height)).getEditableText().toString());
                    } catch (NumberFormatException e) {
                        Toast.makeText(ViewPositionTestActivity.this, "input number", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }

                    drawImage(data, true);
                    dismiss();
                } else if (layoutId == R.layout.view_test_alert_size) {
                    try {
                        pageDataList.get(pageIdx).w = Integer.parseInt(((EditText) findViewById(R.id.width)).getEditableText().toString());
                        pageDataList.get(pageIdx).h = Integer.parseInt(((EditText) findViewById(R.id.height)).getEditableText().toString());
                    } catch (NumberFormatException e) {
                        Toast.makeText(ViewPositionTestActivity.this, "input number", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }

                    changePage(pageIdx);
                    dismiss();
                }
            }
        }
    }
}
