package com.snaps.mobile.component.image_edit_componet;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ProgressBar;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.image.ResolutionUtil;
import com.snaps.common.utils.imageloader.CropUtil;
import com.snaps.common.utils.imageloader.recoders.AdjustableCropInfo;
import com.snaps.common.utils.imageloader.recoders.CropInfo.CORP_ORIENT;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.BitmapUtil;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;
import com.snaps.mobile.activity.themebook.SnapsImageEffector;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsUtil;

import errorhandle.logger.SnapsLogger;
import errorhandle.logger.web.WebLogConstants;
import errorhandle.logger.web.request.WebLogRequestBuilder;

public class SnapsImageCropView extends AppCompatImageView implements
        OnTouchListener {
    private static final String TAG = SnapsImageCropView.class.getSimpleName();

    private static final float MIN_SCALE_RATIO = .5f;
    private static final float MAX_SCALE_RATIO = 2f;

    private static final float PRINT_AREA_LINE_SIZE = 1.f;

    public static final int NONE = 0;
    public static final int DRAG = 1;
    public static final int ZOOM = 2;
    public int mode = NONE;

    private SnapsImageEffector.IEffectStatusListener mIEffectStatusListener;
    private EditorInitializeListener mEditorInitListener;

    // 인화영역 크기...
    private RectF printAreaClipRect = new RectF(); // 영역그리는 크기
    private RectF clipRectRedLine = new RectF(); // 선을 그리기 위한.
    private RectF clipRectTranspar = new RectF(); // 영역 투명하게 그리는 크기...

    private Paint printAreaLinePrint; // 인화영역 그리는 Paint
    private Paint outsideOfPrintAreaPaint;
//    private Paint mPassportTextPaint;

    private float viewPortWRation = 0.0f;
    private float viewPortHRation = 0.0f;
    private float ratio = 0.0f;

    // 화면에 뿌려지는 이미지 크기...
    private float screenImgWidth = 0.0f, screenImgHeight = 0.0f;

    // 인화영역 색상..
    private int printAreaLineColor;

    private int pageIdx = 0;

    // view전체크기
    private int screenWidth, screenHeight;
    private int originImgWidth, originImgHeight;

    private MyPhotoSelectImageData imgData;

    private boolean isViewRotate = false;

    private float ratioX = 0.f, ratioY = 0.f;

    // 크롭되는 사각형의 가로, 세
    private float imgCropWidth = 0.f;
    private float imgCropHeight = 0.f;

    private Context context;
    private Bitmap noPrintMarker;
//    private Bitmap m_bmPassportGuide;

    private boolean isNoPrint = false;

    public Path cropRangePath = new Path();
    //	public MatrixBounds matrixBounds = new MatrixBounds();

    //	private Object mHolder = new Object();
    private ProgressBar progressBar;

    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    private Matrix tempMatrix = new Matrix();
    private Matrix scaledMatrix = new Matrix();
    private Matrix originMatrix = new Matrix(); //최초 초기값
    private Matrix touchDownMatrix = new Matrix(); //화면을 클릭하는 순간의 값
    private Matrix lastAllowMatrix = new Matrix(); //화면을 클릭하는 순간의 값

    private ImgRectAttribute curImgRect = new ImgRectAttribute(); //현재 이미지를 컨트롤 하기 위함.
    private ImgRectAttribute totalImgRect = new ImgRectAttribute(); //모든 이동 궤적이나 스케일이 합산된 상태
    private ImgRectAttribute touchdownImgRect = new ImgRectAttribute(); //터치 다운한 상태를 저장.
    private ImgRectAttribute lastAllowImgRect = new ImgRectAttribute(); //터치 다운한 상태를 저장.

    public CORP_ORIENT orientValue = CORP_ORIENT.NONE;

    private PointF start = new PointF();

    public RectF clipRectRange = null;

    private Path boundsPath = new Path();
    private Path touchDownPath = new Path();
    private Path lastAllowPath = new Path();

    private Region rangeCheckRegion;

    private MatrixBounds originalBounds = null;
//	public MatrixBounds curBounds = null;

    private float prevRotateAngle = 0.f;

    private float tempWidth, tempHeight;
    private float resWidth, resHeight;

    private float oldDragDist = 1f;
    private float curRotate = 0f;
    private float newRotate = 0f;
    private float[] lastEvent = null;

    private float lineSize = 1.f; // 인화영역 두께..

    private long initLockTime = 0l;
    private long prevToastTime = 0l;

    private boolean isEdited = false, isScaleable = false, isOnTouch = false, isEditable = false, isScaledOrRotated = false,
            isActionPointerUp = false, isChangedMatrixValue = false, isAction = false, isInitialized = false, isRotateMode = false,
            isLandScapeMode = false;

    private float scaleValueOnTouchDown = 1.f;
    private float angleValueOnTouchDown = 0.f;

    public SnapsImageCropView(Context context, AttributeSet attrs,
                              int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public SnapsImageCropView(Context context) {
        this(context, null);
        init(context);
    }

    public SnapsImageCropView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init(context);
    }

    public void init(Context context) {
        setOnTouchListener(this);

        setEdited(false);

        this.context = context;

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        printAreaLineColor = Color.parseColor("#ffffff");

        // px => dip
        float scale = getResources().getDisplayMetrics().density;
        lineSize = (int) (PRINT_AREA_LINE_SIZE * scale + 0.5f);

        printAreaLinePrint = new Paint();
        printAreaLinePrint.setAntiAlias(true);
        printAreaLinePrint.setColor(printAreaLineColor);
        printAreaLinePrint.setStyle(Paint.Style.STROKE);
        printAreaLinePrint.setStrokeWidth(lineSize);

        outsideOfPrintAreaPaint = new Paint();
        outsideOfPrintAreaPaint.setAntiAlias(true);
        outsideOfPrintAreaPaint.setColor(Color.parseColor("#AA333333"));
        outsideOfPrintAreaPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        outsideOfPrintAreaPaint.setStrokeWidth(lineSize);

//        mPassportTextPaint = new Paint();
//        mPassportTextPaint.setAntiAlias(true);
//        mPassportTextPaint.setColor(Color.parseColor("#ffffffff"));
//
//        int spSize = 10;
//        float scaledSizeInPixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
//                spSize, getResources().getDisplayMetrics());
//        mPassportTextPaint.setTextSize(scaledSizeInPixels);
//        mPassportTextPaint.setTextAlign(Paint.Align.CENTER);
//
//        Typeface typeface = FontUtil.getTypeface(FontUtil.eSnapsFonts.YOON_GOTHIC_740);
//        if (typeface != null) {
//            mPassportTextPaint.setTypeface(typeface);
//        }

        Resources res = getResources();
        noPrintMarker = BitmapFactory.decodeResource(res, R.drawable.alert_01);

        setWillNotDraw(false);  // https://pupabu.tistory.com/46  <-- 요약: onDraw 호출 되도록

        createEffectStatusListener();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        if (com.snaps.common.utils.constant.Config.useDrawSmartSnapsImageArea()) {
            if (bm != null) {
                Bitmap bitmap = SmartSnapsUtil.drawSmartSnapsImageArea(bm, imgData);
                super.setImageBitmap(bitmap);
            } else {
                super.setImageBitmap(null);
            }
        } else {
            super.setImageBitmap(bm);
        }
    }

    private void saveTouchDownInfo() {
        touchDownMatrix.set(matrix);
        touchdownImgRect.setMovedX(totalImgRect.getMovedX());
        touchdownImgRect.setMovedY(totalImgRect.getMovedY());
        touchdownImgRect.setWidth(curImgRect.getWidth());
        touchdownImgRect.setHeight(curImgRect.getHeight());

        touchDownPath.set(boundsPath);

        lastAllowMatrix.set(matrix);
        lastAllowImgRect.setMovedX(totalImgRect.getMovedX());
        lastAllowImgRect.setMovedY(totalImgRect.getMovedY());
        lastAllowImgRect.setWidth(curImgRect.getWidth());
        lastAllowImgRect.setHeight(curImgRect.getHeight());

        lastAllowPath.set(boundsPath);
    }

    private Matrix getClipLineMatchedMatrix(int moveX, int moveY) {
        Matrix fixedMatrix = new Matrix(matrix);
        RectF rect = MatrixUtil.getMatrixRect(matrix, this);
        if (!isScaledOrRotated) {
            if (orientValue == CORP_ORIENT.HEIGHT) {
                if (moveY > 0 && rect.top + moveY >= clipRectRange.top) {
                    moveY = (int) (clipRectRange.top - rect.top) - 4; //4를 빼주는 이유는 가끔 선 밖으로 나가게 되는 오류가 확인 되기 때문에..
                } else if (moveY < 0 && rect.bottom + moveY <= clipRectRange.bottom) {
                    moveY = (int) (clipRectRange.bottom - rect.bottom) + 4;
                }
            } else {
                if (moveX > 0 && rect.left + moveX >= clipRectRange.left) {
                    moveX = (int) (clipRectRange.left - rect.left) - 4;
                } else if (moveX < 0 && rect.right + moveX <= clipRectRange.right) {
                    moveX = (int) (clipRectRange.right - rect.right) + 4;
                }
            }

            fixedMatrix.postTranslate(moveX, moveY);
        }
        return fixedMatrix;
    }

    private boolean isBlockImageEdit() {
        return System.currentTimeMillis() - initLockTime < 200 || isShowingProgress() || !isEditable();
    }

    private void initEditActionInfoByMotionEvent(MotionEvent event) {
        setOnTouch(true);

        saveTouchDownInfo();

        curImgRect.setMovedX(0.f);
        curImgRect.setMovedY(0.f);
        savedMatrix.set(matrix);
        start.set(event.getX(), event.getY());
        mode = DRAG;
        lastEvent = null;
        isChangedMatrixValue = false;
        isActionPointerUp = false;
        curImgRect.setScale(1.f);

        scaleValueOnTouchDown = getScaleX();
        angleValueOnTouchDown = MatrixUtil.getAngle(matrix);
    }

    private void onMultiTouchEvent(MotionEvent event) {
        oldDragDist = ImageEditMotionUtil.getPinchSpacing(event);
        if (oldDragDist > 10f) {
            savedMatrix.set(matrix);
            // midPoint(mid, event);
            mode = ZOOM;
        }
        lastEvent = new float[4];
        lastEvent[0] = event.getX(0);
        lastEvent[1] = event.getX(1);
        lastEvent[2] = event.getY(0);
        lastEvent[3] = event.getY(1);
        curRotate = ImageEditMotionUtil.getRotationDegree(event);
    }

    private void onCanceledMultiTouchAction() {
        mode = NONE;
        lastEvent = null;

        tempWidth = curImgRect.getWidth();
        tempHeight = curImgRect.getHeight();

        isActionPointerUp = true;
    }

    private boolean onActionTouchUp() {
        setOnTouch(false);
        mode = NONE;
        lastEvent = null;
        if (isChangedMatrixValue) {

            if (!validRange()) {
                initLockTime = System.currentTimeMillis();

                if (!SnapsDiaryDataManager.isAliveSnapsDiaryService()) {
                    showToastMsg(R.string.invalid_image_range_msg);
                }

                initPos();
                return false;
            }

            totalImgRect.addMovedX(curImgRect.getMovedX());
            totalImgRect.addMovedY(curImgRect.getMovedY());

            totalImgRect.setWidth(curImgRect.getWidth());
            totalImgRect.setHeight(curImgRect.getHeight());
        }
        isActionPointerUp = false;

        checkResolution(getScale(), MatrixUtil.getAngle(matrix));

        WebLogConstants.eWebLogName logName = null;
        if (scaleValueOnTouchDown < getScaleX()) {
            logName = WebLogConstants.eWebLogName.photobook_annie_editphoto_scaleupImg;
        } else if (scaleValueOnTouchDown > getScaleX()) {
            logName = WebLogConstants.eWebLogName.photobook_annie_editphoto_scaledownImg;
        } else {
            logName = WebLogConstants.eWebLogName.photobook_annie_editphoto_moveImg;
        }

        SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(logName)
                .appendPayload(WebLogConstants.eWebLogPayloadType.TEMPLATE_CODE, Config.getTMPL_CODE())
                .appendPayload(WebLogConstants.eWebLogPayloadType.IMG_PATH, (imgData != null ? imgData.getImagePathForWebLog() : ""))
                .appendPayload(WebLogConstants.eWebLogPayloadType.PAGE, String.valueOf(PhotobookCommonUtils.findPageIndexThatContainImage(pageIdx, imgData)))
                .appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));

        if (angleValueOnTouchDown != MatrixUtil.getAngle(matrix)) {
            SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_editphoto_rotateImg)
                    .appendPayload(WebLogConstants.eWebLogPayloadType.TEMPLATE_CODE, Config.getTMPL_CODE())
                    .appendPayload(WebLogConstants.eWebLogPayloadType.IMG_PATH, (imgData != null ? imgData.getImagePathForWebLog() : ""))
                    .appendPayload(WebLogConstants.eWebLogPayloadType.PAGE, String.valueOf(PhotobookCommonUtils.findPageIndexThatContainImage(pageIdx, imgData)))
                    .appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));
        }

        return true;
    }

    private void onDraggingAction(MotionEvent event) {
        int moveX = (int) (event.getX() - start.x);
        int moveY = (int) (event.getY() - start.y);

        if (!isScaledOrRotated) {
            if (orientValue == CORP_ORIENT.HEIGHT) {
                moveX = 0;
            } else {
                moveY = 0;
            }
        }

        curImgRect.setMovedX(moveX);
        curImgRect.setMovedY(moveY);
        tempMatrix.set(getClipLineMatchedMatrix(moveX, moveY));

        matrix.set(savedMatrix);
        matrix.postTranslate(curImgRect.getMovedX(), curImgRect.getMovedY());
        isAction = true;

        checkImageRectLimitLine();
    }

    private void onZoomingAction(MotionEvent event) {
        if (isActionPointerUp) {
            return;
        }

        if (lastEvent != null && event.getPointerCount() == 2) {
            float newDragDist = ImageEditMotionUtil.getPinchSpacing(event);

            boolean isScaleable = false;
            // Scale
            if (newDragDist > 10f) {
                float newScaleValue = newDragDist / oldDragDist;

                isScaleable = isValidScale(newScaleValue);
                PointF center = getCurCenter();

                if (isScaleable) {
                    isAction = true;

                    matrix.set(savedMatrix);
                    curImgRect.setScale(newScaleValue);
                    matrix.postScale(curImgRect.getScale(), curImgRect.getScale(), center.x,
                            center.y);
                } else {
                    showToastMsg(R.string.cannot_zooming_more);

                    scaledMatrix.set(savedMatrix);
                    scaledMatrix.postScale(curImgRect.getScale(), curImgRect.getScale(), center.x,
                            center.y);
                }
            }

            newRotate = ImageEditMotionUtil.getRotationDegree(event);
            float r = newRotate - curRotate;

            if (!isScaleable) {
                if (Math.abs(prevRotateAngle - r) < .1) {
                    return;
                } else {
                    matrix.set(scaledMatrix);
                }
            }

            isAction = true;
            PointF center = getCurCenter();
            matrix.postRotate(r, center.x, center.y);
            prevRotateAngle = r;
        }
    }

    private void updateMatrixInfo(MotionEvent event) {
        if (lastEvent != null && event.getPointerCount() > 2) {
            isAction = false;
        }

        if (isAction) {
            if (isActionPointerUp) {
                curImgRect.setWidth(tempWidth * curImgRect.getScale());
                curImgRect.setHeight(tempHeight * curImgRect.getScale());
            } else {
                curImgRect.setWidth(touchdownImgRect.getWidth() * curImgRect.getScale());
                curImgRect.setHeight(touchdownImgRect.getHeight() * curImgRect.getScale());
            }

            PointF center = getCurCenter();

            OrientedBoundingBox bow = new OrientedBoundingBox(-MatrixUtil.getAngle(matrix),
                    center.x, center.y, curImgRect.getWidth(),
                    curImgRect.getHeight());

            boundsPath = bow.toPath();

            saveAllowMatrix();

            notifyMatrix(null, matrix);

            OrientedBoundingBox drawingBox = new OrientedBoundingBox(-MatrixUtil.getAngle(matrix),
                    center.x, center.y, curImgRect.getWidth() + 100,
                    curImgRect.getHeight() + 100);
            setCropRangePath(drawingBox.toPath());

            isChangedMatrixValue = true;

            setEdited(true);
        }
    }

    public boolean onTouch(View v, MotionEvent event) {
        if (isBlockImageEdit()) {
            return false;
        }

        isAction = false;

        // https://64.media.tumblr.com/25e8624f2ea1cfb272925c895453a7a1/4bed1c091ad1b0fa-1b/s540x810/8853cbdc37d4394a89ca1c8860cf7fdb1cc16dc7.png
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                initEditActionInfoByMotionEvent(event);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() <= 2) {
                    onMultiTouchEvent(event);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                return onActionTouchUp();
            case MotionEvent.ACTION_POINTER_UP:
                onCanceledMultiTouchAction();
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    onDraggingAction(event);
                } else if (mode == ZOOM) {
                    onZoomingAction(event);
                }
                break;
        }

        updateMatrixInfo(event);

        return true;
    }

    protected void saveAllowMatrix() {
        if (validRange()) {
            lastAllowImgRect.setMovedX(totalImgRect.getMovedX() + curImgRect.getMovedX());
            lastAllowImgRect.setMovedY(totalImgRect.getMovedY() + curImgRect.getMovedY());
            lastAllowImgRect.setWidth(curImgRect.getWidth());
            lastAllowImgRect.setHeight(curImgRect.getHeight());

            lastAllowMatrix.set(matrix);

            lastAllowPath.set(boundsPath);
        }
    }

    //스케일링이나, 로테이트가 되지 않은 상태에서 이동을 해 클립영역을 벗어나지 못하게 하는 함수
    private void checkImageRectLimitLine() {
        if (!isScaledOrRotated) {
            RectF rect = MatrixUtil.getMatrixRect(matrix, this);
            if (orientValue == CORP_ORIENT.HEIGHT) {
                if (rect.top > clipRectRange.top) {
                    matrix.set(tempMatrix);
                    curImgRect.setMovedY(MatrixUtil.getYValueFromMatrix(tempMatrix) - MatrixUtil.getYValueFromMatrix(savedMatrix));
                } else if (rect.bottom < clipRectRange.bottom) {
                    matrix.set(tempMatrix);
                    curImgRect.setMovedY(MatrixUtil.getYValueFromMatrix(tempMatrix) - MatrixUtil.getYValueFromMatrix(savedMatrix));
                }
            } else {
                if (rect.left > clipRectRange.left) {
                    matrix.set(tempMatrix);
                    curImgRect.setMovedX(MatrixUtil.getXValueFromMatrix(tempMatrix) - MatrixUtil.getXValueFromMatrix(savedMatrix));
                } else if (rect.right < clipRectRange.right) {
                    matrix.set(tempMatrix);
                    curImgRect.setMovedX(MatrixUtil.getXValueFromMatrix(tempMatrix) - MatrixUtil.getXValueFromMatrix(savedMatrix));
                }
            }
        }
    }

    public PointF getCurCenter() {
        return new PointF((curImgRect.getCenterX() + totalImgRect.getMovedX()) + curImgRect.getMovedX(),
                (curImgRect.getCenterY() + totalImgRect.getMovedY()) + curImgRect.getMovedY());
    }

    public boolean validRange() {
        // 이미지가 무조건 클립 영역보다는 크거나 같아야 한다.
        if (clipRectRange == null) {
            return false;
        }

        if (boundsPath == null) {
            return true;
        }

        if (rangeCheckRegion == null) {
            rangeCheckRegion = new Region();
        }

        int w = this.getMeasuredWidth();
        int h = this.getMeasuredHeight();

        rangeCheckRegion.setPath(boundsPath, new Region(0, 0, w + 100, h + 100));

        boolean valid = rangeCheckRegion.contains((int) clipRectRange.left,
                (int) clipRectRange.top)
                && rangeCheckRegion.contains((int) clipRectRange.right,
                (int) clipRectRange.top)
                && rangeCheckRegion.contains((int) clipRectRange.left,
                (int) clipRectRange.bottom)
                && rangeCheckRegion.contains((int) clipRectRange.right,
                (int) clipRectRange.bottom);

        if (valid && getScale() != 1.f) {
            isScaledOrRotated = true;
        }

        return isScaledOrRotated ? valid : (getScale() == 1.f && MatrixUtil.getAngle(matrix) % 90 == 0);
    }

    private void showToastMsg(int resId) {
        if (context == null || System.currentTimeMillis() - prevToastTime < 2000) {
            return;
        }

        prevToastTime = System.currentTimeMillis();
        MessageUtil.toast(context, resId, Gravity.CENTER);
    }

    private boolean isValidScale(float newScaleValue) {
        float calculatedScale = (getScale() - 1) + newScaleValue;
        return calculatedScale > MIN_SCALE_RATIO && calculatedScale < MAX_SCALE_RATIO;
    }

    public float getMoveX() {
        return MatrixUtil.getMeasuredMatrixValue(2, matrix, originMatrix);
    }

    public float getMoveY() {
        return MatrixUtil.getMeasuredMatrixValue(5, matrix, originMatrix);
    }

    public float getScaleX() {
        return MatrixUtil.getMeasuredMatrixValue(0, matrix, originMatrix);
    }

    public float getScaleY() {
        return MatrixUtil.getMeasuredMatrixValue(4, matrix, originMatrix);
    }

    public float getScale() {
        if (originalBounds == null || curImgRect == null) {
            return 1.f;
        }

        float originWidth = originalBounds.getWidth();
        float curWidth = curImgRect.getWidth();
        return curWidth / originWidth;
    }

    public void initPos() {
        if (!adjustPosByLastAllowMatrix()) {
            totalImgRect.setMovedX(touchdownImgRect.getMovedX());
            totalImgRect.setMovedY(touchdownImgRect.getMovedY());
            curImgRect.setWidth(touchdownImgRect.getWidth());
            curImgRect.setHeight(touchdownImgRect.getHeight());

            matrix.set(touchDownMatrix);
            boundsPath.set(touchDownPath);

            notifyMatrix(touchDownPath, touchDownMatrix);
        }

        notifyMatrix(lastAllowPath, lastAllowMatrix);
    }

    private void notifyMatrix(Path path, Matrix matrix) {
        setCropRangePath(path);

        super.setImageMatrix(matrix);  //super를 일부러 써준다. 코드 파악하기 편하게
    }

    private void setCropRangePath(Path path) {
        if (path != null && cropRangePath != null) {
            cropRangePath.reset();
            cropRangePath.set(path);
        }
    }

    private boolean adjustPosByLastAllowMatrix() {
        totalImgRect.setMovedX(lastAllowImgRect.getMovedX());
        totalImgRect.setMovedY(lastAllowImgRect.getMovedY());
        curImgRect.setWidth(lastAllowImgRect.getWidth());
        curImgRect.setHeight(lastAllowImgRect.getHeight());

        matrix.set(lastAllowMatrix);
        boundsPath.set(lastAllowPath);
        return validRange();
    }

    private void createEffectStatusListener() {
        mIEffectStatusListener = new SnapsImageEffector.IEffectStatusListener() {
            @Override
            public void onChangedStatus(boolean isLoading) {
                setEditable(!isLoading);
            }
        };
    }

    /***
     * 라인 크기를 설정하는 함수
     *
     * @param width
     */
//    public void setLineBorderWidth(int width) {
//        float scale = getResources().getDisplayMetrics().density;
//        lineSize = (int) (width * scale + 0.5f);
//    }

    /***
     * 인화영역 설정 뷰의 크기를 조정한다.
     *
     */
    public void setAdjustClipBound(MyPhotoSelectImageData imgData,
                                   float viewPortWRation, float viewPortHRation, boolean isTouch) {
        this.imgData = imgData;
        this.viewPortWRation = viewPortWRation;
        this.viewPortHRation = viewPortHRation;

        if (this.imgData.F_IMG_WIDTH != null && this.imgData.F_IMG_WIDTH.length() > 0 && this.imgData.F_IMG_HEIGHT != null && this.imgData.F_IMG_HEIGHT.length() > 0) {
            originImgWidth = Integer.parseInt(this.imgData.F_IMG_WIDTH);
            originImgHeight = Integer.parseInt(this.imgData.F_IMG_HEIGHT);
        }
    }

    /***
     * 인화영역 설정하는 함수...
     */
    public void calculatorClipRect() {
        // 이미지 크기를 가지고 뷰의 크기를 조정한다.
        // 이미지가 가로에 맞춰야할지 세로에 맞춰야 할지 확

        int originWidth = originImgWidth;
        int originHeight = originImgHeight;

        // exif에 이미지의 기본 회전정보
        boolean isRotate = false;
        if (!isViewRotate && imgData != null
                && ((imgData.ROTATE_ANGLE == 90) || (imgData.ROTATE_ANGLE == 270))) {
            int temp = originWidth;
            originWidth = originHeight;
            originHeight = temp;
            isRotate = true;
        }

        ratioX = (float) screenWidth / (float) originWidth;
        ratioY = (float) screenHeight / (float) originHeight;

        // 스크린에 뿌려지는 이미지 크기를 구한다
        if (ratioX >= ratioY) {
            // _cropOrient = LEFT_RIGHT;
            screenImgHeight = screenHeight;
            screenImgWidth = originWidth * ratioY;
        } else {
            // _cropOrient = UP_DOWN;
            screenImgWidth = screenWidth;
            screenImgHeight = originHeight * ratioX;
        }

        if (originWidth > originHeight) {
            ratio = viewPortWRation;
        } else if (originWidth < originHeight) {
            ratio = viewPortHRation;
        } else if (originWidth == originHeight) {

            if (isRotate) {
                ratio = viewPortWRation;
            } else {
                ratio = viewPortHRation;
            }

        }

        // viewPort 이동방향을 설정한다.
        boolean isStandard = (screenImgWidth / screenImgHeight) > ratio ? true
                : false;
        orientValue = isStandard ? CORP_ORIENT.WIDTH : CORP_ORIENT.HEIGHT;

        // viewport 크기 설정...
        imgCropWidth = isStandard ? ((screenImgHeight - lineSize) * ratio)
                : (screenImgWidth - lineSize);
        imgCropHeight = isStandard ? (screenImgHeight - lineSize)
                : ((screenImgWidth - lineSize) / ratio);

        // 크롭영역 위치를 조정한다.
        adjustCropRect(imgCropWidth, imgCropHeight);

        isInitialized = true;
    }

    void adjustCropRect(float cWidth, float cHeight) {

        // 크롭영역을 가운데에 맞춘다.
        int x = (int) ((screenImgWidth - cWidth) / 2);
        int y = (int) Math.floor(((screenImgHeight - cHeight) / 2));

        RectF tempClipRect = null;
        // 불투명하게 그릴영
        if (screenImgWidth == screenWidth) {
            tempClipRect = new RectF(0,
                    (screenHeight - screenImgHeight) / 2.0f, screenImgWidth,
                    screenImgHeight + (screenHeight - screenImgHeight)
                            / 2.0f);
        } else {
            tempClipRect = new RectF((screenWidth - screenImgWidth) / 2.0f, 0,
                    screenImgWidth + (screenWidth - screenImgWidth) / 2.0f,
                    screenImgHeight);
        }

        // 인화영역
        printAreaClipRect.set(x + tempClipRect.left, y + tempClipRect.top, cWidth
                + x + tempClipRect.left, y + cHeight + tempClipRect.top);

        // m_fCropStartXPt = printAreaClipRect.left;
        // m_fCropStartYPt = printAreaClipRect.top;

        // 인화영역 그리는 영역 (빨간 사각형..)
        clipRectRedLine = new RectF(printAreaClipRect);
        clipRectRedLine.inset(0, 0);

        clipRectTranspar = new RectF(printAreaClipRect);
        clipRectTranspar.inset(lineSize / 2, lineSize / 2);

        setClipRectRange(printAreaClipRect);

        loadMatrix();

        checkResolution(getScale(), MatrixUtil.getAngle(matrix));

//        Resources res = getResources();
//        m_bmPassportGuide = BitmapFactory.decodeResource(res,
//                R.drawable.pass_port_image_skin);
//        m_bmPassportGuide = BitmapUtil.getScaledBitmap(m_bmPassportGuide, (int) printAreaClipRect.width(), (int) printAreaClipRect.height());
    }

    void loadMatrix() {
        if (imgData != null && imgData.isAdjustableCropMode) {
            AdjustableCropInfo cropInfo = imgData.ADJ_CROP_INFO;
            if (cropInfo != null) {
                AdjustableCropInfo.CropImageRect imgRect = cropInfo.getImgRect();

                int iCurDeviceScreenWidth = getMeasuredWidth(); //현재 측정된 뷰의 크기
                int iCurDeviceScreenHeight = getMeasuredHeight();

                if (imgRect != null) {
                    float fRatioWidth = totalImgRect.getWidth() / (imgRect.width / imgRect.scaleX); //편집 할 당시의 크기와 비교한다
                    float fRatioHeight = totalImgRect.getHeight() / (imgRect.height / imgRect.scaleY);
                    imgRect.movedX *= fRatioWidth; //편집 할 당시와 크기가 차이가 난다면 차이 나는 만큼 더 이동을 해 준다
                    imgRect.movedY *= fRatioHeight;

                    matrix.postRotate(imgRect.angle, iCurDeviceScreenWidth / 2, iCurDeviceScreenHeight / 2); //회전각은 편집 당시 상황과 관계가 없다
                    matrix.postScale(imgRect.scaleX, imgRect.scaleY, iCurDeviceScreenWidth / 2, iCurDeviceScreenHeight / 2); //스케일 역시 영향을 안 받는다
                    matrix.postTranslate(imgRect.movedX, imgRect.movedY);

                    imgRect.width = totalImgRect.getWidth() * imgRect.scaleX; //이미지 크기를 편집 할 당시의 배율로 재현을 한다
                    imgRect.height = totalImgRect.getHeight() * imgRect.scaleY;

                    totalImgRect.set(imgRect);

                    curImgRect.setWidth(totalImgRect.getWidth());
                    curImgRect.setHeight(totalImgRect.getHeight());

                    PointF center = getCurCenter();
                    OrientedBoundingBox bow = new OrientedBoundingBox(-MatrixUtil.getAngle(matrix),
                            center.x, center.y, curImgRect.getWidth(),
                            curImgRect.getHeight());

                    boundsPath = bow.toPath();

                    notifyMatrix(boundsPath, matrix);

                    saveAllowMatrix();

                    if (imgRect.scaleX != 1) {
                        isScaledOrRotated = true;
                    }

                    if (imgRect.scaleX != 1 || imgRect.movedX != 0 || imgRect.movedY != 0) {
                        setEdited(true);
                    }
                }
            }
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!isInitialized) {
            return;
        }

        canvas.save(); //현재 소스 기준으로 필요없는데.. (https://simsimjae.tistory.com/269)

        if (cropRangePath != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                canvas.clipOutRect(clipRectTranspar);
            }
            else {
                canvas.clipRect(clipRectTranspar, Region.Op.DIFFERENCE);
            }
            canvas.drawPath(cropRangePath, outsideOfPrintAreaPaint);
            canvas.drawRect(clipRectRedLine, printAreaLinePrint);
        }

        canvas.restore();  //현재 소스 기준으로 필요없는데.

//		if (com.snaps.common.utils.constant.Config.shouldShowPassportImageEditGuide()) {
//			if (m_bmPassportGuide != null && !m_bmPassportGuide.isRecycled()) {
//				Bitmap bmPassportGuide = m_bmPassportGuide.copy(Bitmap.Config.ARGB_8888, true);
//
//				canvas.drawBitmap(bmPassportGuide, printAreaClipRect.left, printAreaClipRect.top, null);
//				bmPassportGuide.recycle();
//
//				String desc = getContext().getResources().getString(R.string.pass_port_image_edit_desc);
//
//				int textX = (int) (printAreaClipRect.left + (printAreaClipRect.width()/2));
//				int textY = (int) (printAreaClipRect.bottom - (printAreaClipRect.height()*.14f));
//
//				canvas.drawText(desc, textX, textY, mPassportTextPaint);
//			}
//		}

        if (isNoPrint) {
            if (noPrintMarker != null && !noPrintMarker.isRecycled()) {
                Bitmap bmNoPrintMarker = CropUtil.getInSampledBitmapCopy(noPrintMarker, Bitmap.Config.ARGB_8888);

                int markerLeft = (int) (((printAreaClipRect.width() / 2) - (bmNoPrintMarker
                        .getWidth() / 2)) + printAreaClipRect.left);
                int markerTop = (int) (((printAreaClipRect.height() / 2) - (bmNoPrintMarker
                        .getHeight() / 2)) + printAreaClipRect.top);

                canvas.drawBitmap(bmNoPrintMarker, markerLeft, markerTop, null);
                bmNoPrintMarker.recycle();
            }
        }
    }

    public void checkResolution(float scale, float angle) {
        if (imgData != null) {
            try {
                isNoPrint = ResolutionUtil.isEnableResolution(
                        imgData.mmPageWidth,
                        imgData.pxPageWidth,
                        imgData.controlWidth,
                        imgData,
                        scale,
                        angle);
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }

        invalidate();
    }

    public void recycleBitmaps() {
        if (noPrintMarker != null && !noPrintMarker.isRecycled()) {
            noPrintMarker.recycle();
            noPrintMarker = null;
        }

//        if (m_bmPassportGuide != null && !m_bmPassportGuide.isRecycled()) {
//            m_bmPassportGuide.recycle();
//            m_bmPassportGuide = null;
//        }
    }

    // https://duzi077.tistory.com/188
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (imgData == null) {
            return;
        }

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);  // this.getSuggestedMinimumHeight()?????
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        // https://sungcheol-kim.gitbook.io/android-custom-view-programming/chapter01
        // Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            // Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            // Can't be bigger than...
            width = Math.min(0, widthSize);
        } else {
            // Be whatever you want
            width = 0;
        }

        // Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            // Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            // Can't be bigger than...
            height = Math.min(0, heightSize);
        } else {
            // Be whatever you want
            height = 0;
        }

        int shortSideLength = Math.min(width, height) != 0 ? Math.min(width, height) : Math.max(width, height);
        if (isLandScapeMode()) {
            height = shortSideLength;
            width = (int) (height * 1.38f);
        } else {
            width = shortSideLength;
            height = shortSideLength;
        }

        screenWidth = width;
        screenHeight = height;
        super.setMeasuredDimension(width, height);

        if (isRotateMode) {
            notifyCenter();
            isRotateMode = false;
        }
    }

    public void setRotateMode(boolean isFlag) {
        isRotateMode = isFlag;
    }

    private void notifyCenter() {
        try {
            Bitmap bitmap = ((BitmapDrawable) getDrawable()).getBitmap();
            fitToScreen(bitmap);
            calculatorClipRect();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public void setProgress(ProgressBar pb) {
        progressBar = pb;
    }

    public boolean isShowingProgress() {
        return progressBar != null && progressBar.isShown();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // calculatorClipRect();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        // if (visibility == View.VISIBLE)
        // calculatorClipRect();
        super.onWindowVisibilityChanged(visibility);
    }

    public AdjustableCropInfo getAdjustCropInfo() {
        if (printAreaClipRect == null || totalImgRect == null || curImgRect == null || imgData == null) {
            return null;
        }

        float w = printAreaClipRect.width();
        float h = printAreaClipRect.height();

        AdjustableCropInfo.CropImageRect clipImageRect = new AdjustableCropInfo.CropImageRect();
        clipImageRect.width = w;
        clipImageRect.height = h;
        clipImageRect.centerX = curImgRect.getCenterX();
        clipImageRect.centerY = curImgRect.getCenterY();

        AdjustableCropInfo.CropImageRect imgRect = new AdjustableCropInfo.CropImageRect();
        PointF center = getCurCenter();
        imgRect.angle = -MatrixUtil.getAngle(matrix);
        imgRect.rotate = imgData.ROTATE_ANGLE;
        float[] values = new float[9];
        matrix.getValues(values);
        imgRect.matrixValue = values;
        imgRect.centerX = center.x;
        imgRect.centerY = center.y;
        imgRect.scaleX = getScale();
        imgRect.scaleY = getScale();
        imgRect.width = curImgRect.getWidth();
        imgRect.height = curImgRect.getHeight();
        imgRect.movedX = totalImgRect.getMovedX();
        imgRect.movedY = totalImgRect.getMovedY();
        imgRect.resWidth = resWidth;
        imgRect.resHeight = resHeight;

        return new AdjustableCropInfo(imgRect, clipImageRect);
    }

    public void fitToScreen(Bitmap bm) {
        if (bm == null || bm.isRecycled()) {
            return;
        }
        int w = this.getMeasuredWidth();
        int h = this.getMeasuredHeight();

        RectF drawableRect = new RectF(0, 0, bm.getWidth(), bm.getHeight());
        RectF viewRect = new RectF(0, 0, w, h);
        matrix.setRectToRect(drawableRect, viewRect, Matrix.ScaleToFit.CENTER);
        setImageMatrix(matrix);

        float[] values = new float[9];
        matrix.getValues(values);
        originMatrix.set(matrix);

        resWidth = MatrixUtil.getWidthFromMatrix(originMatrix, this) / values[0];  // resWidth는 bm.getWidth()랑 같은 값인데 이걸 왜 계산?
        resHeight = MatrixUtil.getHeightFromMatrix(originMatrix, this) / values[4]; // resHeight bm.getHeight()랑 같은 값인데 이걸 왜 계산?

        RectF rect = MatrixUtil.getMatrixRect(matrix, this);  //이것도 궅이 복잡하게..
        originalBounds = new MatrixBounds(rect.left, rect.top, rect.right,
                rect.top, rect.right, rect.bottom, rect.left, rect.bottom);
//		curBounds = new MatrixBounds(originalBounds);

        boundsPath = new Path();
        boundsPath.moveTo(originalBounds.LT.x, originalBounds.LT.y);
        boundsPath.lineTo(originalBounds.RT.x, originalBounds.RT.y);
        boundsPath.lineTo(originalBounds.RB.x, originalBounds.RB.y);
        boundsPath.lineTo(originalBounds.LB.x, originalBounds.LB.y);

        //Path 초기화.
        initImgRectValues();

        curImgRect.setCenterX(rect.left + (rect.width() / 2));
        curImgRect.setCenterY(rect.top + (rect.height() / 2));

        curImgRect.setWidth(rect.width());
        curImgRect.setHeight(rect.height());

        totalImgRect.setWidth(curImgRect.getWidth());
        totalImgRect.setHeight(curImgRect.getHeight());

        notifyMatrix(boundsPath, matrix);

        setVisibility(View.VISIBLE);
    }

    public void initImgRectValues() {
        if (curImgRect != null) {
            curImgRect.clear();
        }
        if (totalImgRect != null) {
            totalImgRect.clear();
        }
        if (touchdownImgRect != null) {
            touchdownImgRect.clear();
        }

        isScaledOrRotated = false;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean m_isEditable) {
        this.isEditable = m_isEditable;
    }

    public boolean isValidArea() {
        return validRange();
    }

    public void setScaleable(boolean able) {
        isScaleable = able;
    }

    public boolean isScaleable() {
        return isScaleable;
    }

    public boolean isOnTouch() {
        return isOnTouch;
    }

    public void setOnTouch(boolean m_isOnTouch) {
        this.isOnTouch = m_isOnTouch;
    }

    public boolean isLandScapeMode() {
        return isLandScapeMode;
    }

    public void setLandScapeMode(boolean m_isLandScapeMode) {
        this.isLandScapeMode = m_isLandScapeMode;
    }

    public void setViewRotate(boolean viewRotate) {
        this.isViewRotate = viewRotate;
    }

    public void setEditorInitialListener(EditorInitializeListener listener) {
        this.mEditorInitListener = listener;
    }

    public SnapsImageEffector.IEffectStatusListener getEffectStatusListener() {
        return mIEffectStatusListener;
    }

    public boolean isEdited() {
        return isEdited;
    }

    public void setEdited(boolean isEdited) {
        this.isEdited = isEdited;
    }

    public void setClipRectRange(RectF rect) {
        clipRectRange = new RectF(rect);
    }

    public void setPageIdx(int pageIdx) {
        this.pageIdx = pageIdx;
    }
}
