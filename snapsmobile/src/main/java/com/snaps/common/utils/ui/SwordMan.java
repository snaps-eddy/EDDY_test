package com.snaps.common.utils.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;

import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.utils.file.FileUtil;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.log.Dlog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class SwordMan {

    private static final String TAG = SwordMan.class.getSimpleName();

    public Single<List<ImageEdge>> getKnifeLine(final Bitmap bitmap, final int maskSize) {
        return Single.fromCallable(() -> {
            int bitmapWidth = bitmap.getWidth();
            int[] bitmapIntArray = UIUtil.getIntArray(bitmap);
            int[] grayScaleIntArray = getGrayScaleInt(bitmapIntArray);
            MorphologiedImage morphologiedImage = morphology(grayScaleIntArray, bitmapWidth, maskSize);
            return findEdge(morphologiedImage, bitmapWidth);
        }).subscribeOn(Schedulers.io());
    }

    private int[] getGrayScaleInt(final int[] bitmapInt) {
        int[] grayScleInt = new int[bitmapInt.length];
        for (int x = 0; x < bitmapInt.length; x++) {
            int pixelColor = bitmapInt[x];
            int pixelAlpha = Color.alpha(pixelColor);
            grayScleInt[x] = pixelAlpha > 125 ? Color.BLACK : Color.TRANSPARENT;
        }
        return grayScleInt;
    }

    private MorphologiedImage morphology(final int[] intArray, final int width, int maskSize) {
        long startTime = System.currentTimeMillis();

        int[] maskBoundaries = makeMaskBoundaries(maskSize);
        int[] tempArray = new int[intArray.length];

        int left = width;
        int bitmapTop = 0;
        int right = 0;
        int bitmapBottom = intArray.length / width;

        for (int i = 0; i < intArray.length; i++) {
            int color = intArray[i];
            if (color == Color.TRANSPARENT) {
                if (maskSize > 0 && dilationCircleMask(intArray, width, i, maskSize, maskBoundaries)) {
                    tempArray[i] = Color.BLACK;
                } else {
                    tempArray[i] = Color.TRANSPARENT;
                }
            } else {
                int x = i % width;
                int y = i / width;

                left = Math.min(x, left);
                right = Math.max(x, right);
                bitmapTop = Math.max(y, bitmapTop);
                bitmapBottom = Math.min(y, bitmapBottom);

                tempArray[i] = Color.RED;
            }
        }
        Rect fitRect = new Rect(left, bitmapTop, right, bitmapBottom);
        Dlog.d("Morphology Time : " + (System.currentTimeMillis() - startTime));
        return new MorphologiedImage(tempArray, fitRect);
    }

    private int[] makeMaskBoundaries(int maskSize) {
        int[] maskBoundaries = new int[maskSize + 1];
        for (int i = 0; i < maskBoundaries.length; i++) {
            maskBoundaries[i] = (int) Math.round(Math.sqrt(maskSize * maskSize - i * i));
        }
        return maskBoundaries;
    }

    private boolean dilationCircleMask(int[] intArray, int wdith, int x, int maskSize, int[] maskBoundaries) {
        int pixelCount = intArray.length;

        for (int count = -maskSize; count <= maskSize; count++) {
            int searchBoundary = maskBoundaries[Math.abs(count)];

            int startEx = x - (wdith * count) - searchBoundary;
            int endEx = x - (wdith * count) + searchBoundary;

            if (startEx < 0) {
                startEx = 0;
            }

            if (endEx >= pixelCount) {
                endEx = pixelCount - 1;
            }

            for (int eX = startEx; eX <= endEx; eX++) {
                if (eX == x) {
                    continue;
                }
                if (intArray[eX] != Color.TRANSPARENT) {
                    return true;
                }
            }
        }

        return false;
    }

    private List<ImageEdge> findEdge(final MorphologiedImage morphologiedImage, final int width) {

        long startTime = System.currentTimeMillis();
        int[] bitmapArray = morphologiedImage.getMopologiedArray();

        int bitmapLength = bitmapArray.length;
        List<ImageEdge> edges = new ArrayList<>();
        int[] outLineData = new int[bitmapLength];

        for (int i = 0; i < bitmapLength; i++) {

            int pixel = bitmapArray[i];

            int rightPixelIndex = i - width;
            boolean isEmptyRightPixel = rightPixelIndex < 0 || bitmapArray[rightPixelIndex] == Color.TRANSPARENT;
            int pixelAlpha = Color.alpha(pixel);

            if (pixelAlpha > 125 && isEmptyRightPixel) {

                int labelingId = 100000 + edges.size();
                int currentIndex = i;
                int startIndex = -1;
                int direction = 1;

                int left = width;
                int bitmapTop = 0;
                int right = 0;
                int bitmapBottom = bitmapLength / width;

                int rotateCount = 0;

                ArrayList<Integer> outlines = new ArrayList<>();

                Stack<Integer> stack = new Stack<>();
                stack.push(currentIndex);

                while (!stack.isEmpty()) {
                    int point = stack.pop();
                    int[] checkMaskIndexes = {point - 1, point + width, point + 1, point - width};

                    for (int maskIndex : checkMaskIndexes) {
                        if (maskIndex < 0 || maskIndex >= bitmapLength) {
                            continue;
                        }

                        int originPixel = bitmapArray[maskIndex];
                        int originAlph = Color.alpha(originPixel);
                        if (originAlph > 125) {
                            stack.push(maskIndex);
                            bitmapArray[maskIndex] = labelingId;
                        }
                    }
                }


                while (currentIndex != startIndex) {
                    int x = currentIndex % width;
                    int y = currentIndex / width;

                    left = Math.min(left, x);
                    right = Math.max(right, x);
                    bitmapTop = Math.max(bitmapTop, y);
                    bitmapBottom = Math.min(bitmapBottom, y);

                    outLineData[currentIndex] = labelingId;
                    bitmapArray[currentIndex] = labelingId;
                    if (!outlines.contains(currentIndex)) {
                        outlines.add(currentIndex);
                    }

                    int p1 = getNextPixelIndex(currentIndex, direction, 1, width);
                    if (bitmapLength > p1 && p1 > -1 && (bitmapArray[p1] == labelingId || Color.alpha(bitmapArray[p1]) > 125)) {
                        if (startIndex == -1) {
                            startIndex = currentIndex;
                        }
                        currentIndex = p1;
                        direction = (direction + 5) % 4;
                        rotateCount = 0;
                        continue;
                    }

                    int p2 = getNextPixelIndex(currentIndex, direction, 2, width);
                    if (bitmapLength > p2 && p2 > -1 && (bitmapArray[p2] == labelingId || Color.alpha(bitmapArray[p2]) > 125)) {
                        if (startIndex == -1) {
                            startIndex = currentIndex;
                        }
                        currentIndex = p2;
                        rotateCount = 0;
                        continue;
                    }

                    int p3 = getNextPixelIndex(currentIndex, direction, 3, width);
                    if (bitmapLength > p3 && p3 > -1 && (bitmapArray[p3] == labelingId || Color.alpha(bitmapArray[p3]) > 125)) {
                        if (startIndex == -1) {
                            startIndex = currentIndex;
                        }
                        currentIndex = p3;
                        rotateCount = 0;
                        continue;
                    }

                    if (++rotateCount > 2) {
                        break;
                    }

                    direction = (direction + 3) % 4;

                }
                edges.add(new ImageEdge(labelingId, outLineData, new Rect(left, bitmapBottom, right, bitmapTop), outlines, morphologiedImage.getFitImageEdge()));
            }
        }

        Dlog.d("Object count " + edges.size());
        Dlog.d("Spent time : " + (System.currentTimeMillis() - startTime));
        return edges;
    }

    private int getNextPixelIndex(int currentIndex, int direction, int nextIndex, int width) {

        int returnIndex = -1;

        if (direction == 1) {
            if (nextIndex == 1) {
                returnIndex = 1 - width;

            } else if (nextIndex == 2) {
                returnIndex = 1;

            } else if (nextIndex == 3) {
                returnIndex = 1 + width;
            }


        } else if (direction == 2) {
            if (nextIndex == 1) {
                returnIndex = -width - 1;

            } else if (nextIndex == 2) {
                returnIndex = -width;

            } else if (nextIndex == 3) {
                returnIndex = -width + 1;

            }

        } else if (direction == 3) {
            if (nextIndex == 1) {
                returnIndex = width - 1;

            } else if (nextIndex == 2) {
                returnIndex = -1;

            } else if (nextIndex == 3) {
                returnIndex = -1 - width;

            }

        } else if (direction == 0) {
            if (nextIndex == 1) {
                returnIndex = width + 1;

            } else if (nextIndex == 2) {
                returnIndex = width;

            } else if (nextIndex == 3) {
                returnIndex = width - 1;
            }
        }

        return currentIndex + returnIndex;
    }

    public Rect extractImageRect(Bitmap bitmap) {
        long startTime = System.currentTimeMillis();

        int[] bitmapArray = UIUtil.getIntArray(bitmap);

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int left = width;
        int bitmapTop = 0;
        int right = 0;
        int bitmapBottom = height;

        for (int i = 0; i < bitmapArray.length; i++) {
            int pixelColor = bitmapArray[i];
            int pixelAlpha = Color.alpha(pixelColor);

            if (pixelAlpha > 125) {
                int x = i % width;
                int y = i / width;

                if (right < x) {
                    right = x;
                }

                if (left > x) {
                    left = x;
                }

                if (bitmapTop < y) {
                    bitmapTop = y;
                }

                if (bitmapBottom > y) {
                    bitmapBottom = y;
                }
            }
        }
        // Bitmap 은 하단부터 검색하기 때문에 top 과 bottom 을 바꿔줘야 한다.
        Dlog.d("extractImageRect left : " + left + ", top : " + bitmapBottom + ", right : " + right + ", bottom : " + bitmapTop + " , taken time : " + (System.currentTimeMillis() - startTime));
        return new Rect(left, bitmapBottom, right, bitmapTop);
    }

    public Single<ImageEdgeValidation> validateMakingAcrylicKeyring(Context context, String imagePath) {

        return Single.fromCallable(() -> {
            File file = new File(imagePath);
            String imageType = FileUtil.getMimeType(Uri.fromFile(file).toString());

            ImageEdgeValidation.ImageValidationInfoProvider imageValidationInfo = SnapsTemplateManager.getInstance().getSnapsTemplate().getProductOption();

            int thicknessKnifeline = imageValidationInfo.getThicknessKnifelinePX();

            Bitmap originBitmap = ImageLoader.with(context).load(imagePath).centerInside().skipMemoryCache(true).override(800, 800).submit().get();
            Bitmap bitmap = Bitmap.createBitmap(originBitmap.getWidth() + thicknessKnifeline * 2, originBitmap.getHeight() + thicknessKnifeline * 2, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(originBitmap, thicknessKnifeline, thicknessKnifeline, null);

            int bitmapWidth = bitmap.getWidth();

            int[] bitmapIntArray = UIUtil.getIntArray(bitmap);
            int[] grayScaleIntArray = getGrayScaleInt(bitmapIntArray);

            MorphologiedImage morphologiedImage = new MorphologiedImage(grayScaleIntArray, new Rect());
            List<ImageEdge> edges = findEdge(morphologiedImage, bitmapWidth);

            if (edges.size() > 1) {
                bitmap = drawObjectRect(bitmap, edges);
            }

            return new ImageEdgeValidation(bitmap, edges, imageType);

        }).subscribeOn(Schedulers.io());
    }

    private Bitmap drawObjectRect(Bitmap bitmap, List<ImageEdge> edges) {
        Bitmap copiedBitmap = bitmap.copy(bitmap.getConfig(), true);
        Canvas bitmapCanvas = new Canvas(copiedBitmap);
        Paint paint = new Paint();
        paint.setColor(Color.MAGENTA);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);

        for (ImageEdge edge : edges) {
            Rect rect = edge.getEdgeRect();
            bitmapCanvas.drawRect(rect.left - 5, rect.top - 5, rect.right + 5, rect.bottom + 5, paint);
        }
        return copiedBitmap;
    }
}
