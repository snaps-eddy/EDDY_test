package com.snaps.mobile.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;

import java.util.List;

/**
 * Created by ifunbae on 16. 4. 22..
 */
public class SnapsTrayLayoutView extends View {
     /*그리기 위한 필요정보
    1.페이지 사이즈
    2.레이아웃 정보
    3.
     */

    Rect viewRect = new Rect();
    //배경컬러
    int mBackgroundColor;
    //생성은 activity에서 한다
    //메모리 사용량을 줄이기 위해..
    private Paint paint = null;
    //가운데 선을 그릴지 말지 결정하는 함수.
    boolean isHalfLineDraw = true;
    //페이지 정보
    SnapsPage page = null;
    //선택이 된 레이아웃 인덱스.
    int drawFillIdx = 0;
    //마진설정
    int leftrightMargin = 10;

    private Context context;

    public SnapsTrayLayoutView(Context context) {
        super(context);
        this.context = context;
    }

    public SnapsTrayLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public SnapsTrayLayoutView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    public void init(SnapsPage page, int drawFillIdx, boolean isHalfLineDraw) {
        this.page = page;
        this.paint = new Paint();
        this.drawFillIdx = drawFillIdx;
        this.isHalfLineDraw = isHalfLineDraw;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        if (page == null)
            return;

        canvas.save();
        calcScaleTranslate(canvas);
        //border 그리기
        drawBorder(canvas);
        //레이아웃 그리기
        drawLayout(canvas);
        canvas.restore();

    }

    float getPageWidth() {
        if (page == null) return 0;
        if (SnapsDiaryDataManager.isAliveSnapsDiaryService()) {
            if (page.getImageLayerRect() != null) {
               return  page.getImageLayerRect().width();
            }
        }
        return (float) page.getWidth();
    }

    float getPageHeight() {
        if (page == null) return 0;
        if (SnapsDiaryDataManager.isAliveSnapsDiaryService()) {
            if (page.getImageLayerRect() != null) {
               return  page.getImageLayerRect().height();
            }
        }
        return Float.parseFloat(page.height);
    }


    void calcScaleTranslate(Canvas canvas) {
        float ratio = 1.0f;
        float dx = 0;
        float dy = 0;

        float pageWidth = getPageWidth();
        float pageHeight = getPageHeight();

        float wRatio = ((float) viewRect.width()) / pageWidth;
        float hRatio = ((float) viewRect.height()) / pageHeight;

        if (wRatio > hRatio) { //height 기준
            ratio = hRatio;
            dx = ((float) viewRect.width() - pageWidth * ratio) / 2.f;
        } else {//width 기준
            ratio = wRatio;
            dy = ((float) viewRect.height() - pageHeight * ratio) / 2.f;
        }

        canvas.translate(dx, dy);
        canvas.scale(ratio, ratio);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //사이즈가 변경이 될때만다 사이즈를 다시 저장을 한다
        viewRect.set(0, 0, w, h);
    }


    void drawLayout(Canvas canvas) {
        if (page == null) return;

        List<SnapsControl> snapsControlList = page.getLayoutList();
        if (snapsControlList == null) return;

        int idx = 0;
        for (SnapsControl control : snapsControlList) {
            boolean isDrawFill = false;
            if (idx == drawFillIdx)
                isDrawFill = true;
            drawLayout(canvas, (SnapsLayoutControl) control, isDrawFill);
            idx++;
        }
    }

    void drawLayout(Canvas canvas, SnapsLayoutControl layoutControl, boolean isDrawFill) {
        if (paint == null) return;

        if (isDrawFill) {
            paint.setStyle(Paint.Style.FILL);
            paint.setPathEffect(null);
            paint.setColor(Color.parseColor("#e8625a"));
        } else {
            paint.setStyle(Paint.Style.FILL);
            paint.setPathEffect(null);
            paint.setColor(Color.parseColor("#f0f0f0"));
        }

        //선택이 된 셀과 선택이 되지 않은 색을
        int x = layoutControl.getIntX();
        int y = layoutControl.getIntY();
        int width = (int) layoutControl.getIntWidth();
        int height = (int) layoutControl.getIntHeight();

        Rect aa = new Rect(x, y, x + width, y + height);

        if (paint.getStyle() == Paint.Style.FILL) {
            int halfWidth = 4;
            aa.inset(-halfWidth, -halfWidth);

        }

        canvas.drawRect(aa, paint);
        paint.reset();

    }

    void drawBorder(Canvas canvas) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.parseColor("#cccccc"));
        paint.setStrokeWidth(UIUtil.convertDPtoPX(context, 2));

        float pageWidth = getPageWidth();
        float pageHeight = getPageHeight();

        canvas.drawRect(0, 0, pageWidth, pageHeight, paint);

        if (isHalfLineDraw) {
            paint.reset();
            paint.setColor(Color.parseColor("#ededed"));
            paint.setStrokeWidth(UIUtil.convertDPtoPX(context, 2));
            canvas.drawLine(pageWidth / 2, 0, pageWidth / 2, pageHeight, paint);
        }
        paint.reset();
    }


    public String getLayoutWidth() {
        int idx = 0;
        for (SnapsControl control : page.getLayoutList()) {
            boolean isDrawFill = false;
            if (idx == drawFillIdx)
                return control.width;

            idx++;
        }

        return "";
    }

    public SnapsLayoutControl getLayout() {
        int idx = 0;
        for (SnapsControl control : page.getLayoutList()) {
            boolean isDrawFill = false;
            if (idx == drawFillIdx)
                return (SnapsLayoutControl) control;

            idx++;
        }

        return null;
    }
}
