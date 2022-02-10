package com.snaps.common.data.img;

import android.media.ExifInterface;
import android.os.Parcel;
import android.os.Parcelable;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.system.DateUtil;
import com.snaps.common.utils.ui.StringUtil;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExifUtil {
    private static final String TAG = MyKakaoStoryImageData.class.getSimpleName();

    public static SnapsExifInfo getExifInfoWithFilePath(String filePath) {
        ExifInterface exifInterface = getExifInterfaceWithFilePath(filePath);
        if (exifInterface != null) {
            try {
                GeoDegree geoDegree = new GeoDegree(exifInterface);
                String latitude = geoDegree.getLatitude() != null ? String.valueOf(geoDegree.getLatitude()) : null;
                String longitude = geoDegree.getLongitude() != null ? String.valueOf(geoDegree.getLongitude()) : null;
                int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                String exifDate = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
                exifDate = checkSmartSnapsAnalysisFormatIfIsNotReturnEmpty(exifDate);

                return new SnapsExifInfo.Builder()
                        .setLatitude(latitude)
                        .setLongitude(longitude)
                        .setOrientationTag(String.valueOf(orientation))
                        .setDate(exifDate).create();
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }
        return null;
    }

    private static String checkSmartSnapsAnalysisFormatIfIsNotReturnEmpty(String date) {
        if (StringUtil.isEmpty(date)) return date;
        try {
            SimpleDateFormat fromFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
            Date fromDate = fromFormat.parse(date);
            long time = fromDate.getTime();
            if (DateUtil.isValidSmartSnapsDate(time)) {
                SimpleDateFormat toFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                return toFormat.format(fromDate);
            }
        } catch (ParseException e) {
            Dlog.e(TAG, e);
            return "";
        }
        return "";
    }

    public static ExifInterface getExifInterfaceWithFilePath(String filePath) {
        try {
            if (filePath != null) { //png는 exif가 없다.
                String trimUri = filePath.trim().toLowerCase();
                if (trimUri.endsWith("png"))
                    return null;
            }

            return new ExifInterface(filePath);
        } catch (IOException e) {
            Dlog.e(TAG, e);
        }
        return null;
    }

    /**
     * Local Jpeg Image Path를 입력하여 exif Orientaion 정보를 얻는다.
     *
     * @return
     */
//	public static int getExifOrientation(String imageUri) {
//
//		if (isPNGFile(imageUri)) return 0;
//
//		int rotation = 0;
//		try {
//			ExifInterface exif = new ExifInterface(imageUri);
//			int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//			switch (exifOrientation) {
//				case ExifInterface.ORIENTATION_NORMAL:
//					rotation = 0;
//					break;
//				case ExifInterface.ORIENTATION_ROTATE_90:
//				case ExifInterface.ORIENTATION_TRANSPOSE:
//					rotation = 90;
//					break;
//				case ExifInterface.ORIENTATION_ROTATE_180:
//				case ExifInterface.ORIENTATION_FLIP_VERTICAL:
//					rotation = 180;
//					break;
//				case ExifInterface.ORIENTATION_TRANSVERSE:
//				case ExifInterface.ORIENTATION_ROTATE_270:
//					rotation = 270;
//					break;
//			}
//		} catch (Exception e) {
//			Dlog.e(TAG, e);
//			Logg.y("not valid exif file : " + imageUri);
//		}
//		return rotation;
//	}

//	public static void writeExifOrientation(String inputPath, String outputPath) {
//
//		if(inputPath == null || (!inputPath.endsWith(".jpg") && !inputPath.endsWith(".JPG"))) return;
//
//		try {
//			ExifInterface oldExif = new ExifInterface(inputPath);
//			String exifOrientation = oldExif.getAttribute(ExifInterface.TAG_ORIENTATION);
//
//			ExifInterface newExif = new ExifInterface(outputPath);
//			if (exifOrientation != null) {
//				newExif.setAttribute(ExifInterface.TAG_ORIENTATION, exifOrientation);
//			}
//			newExif.saveAttributes();
//		} catch (IOException e) {
//			Dlog.e(TAG, e);
//		}
//	}
    public static int parseOrientationToDegree(int exifOrientation) {
        int rotation = 0;
        switch (exifOrientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                rotation = 0;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
            case ExifInterface.ORIENTATION_TRANSPOSE:
                rotation = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                rotation = 180;
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotation = 270;
                break;
        }
        return rotation;
    }

    public static class SnapsExifInfo implements Parcelable, Serializable {
        private static final long serialVersionUID = -2899398592859463570L;
        private String latitude, longitude;
        private String date;
        private String orientationTag;

        private SnapsExifInfo(Builder builder) {
            this.latitude = builder.latitude;
            this.longitude = builder.longitude;
            this.date = builder.date;
            this.orientationTag = builder.orientationTag;
        }

        protected SnapsExifInfo(Parcel in) {
            latitude = in.readString();
            longitude = in.readString();
            date = in.readString();
            orientationTag = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(latitude);
            dest.writeString(longitude);
            dest.writeString(date);
            dest.writeString(orientationTag);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<SnapsExifInfo> CREATOR = new Creator<SnapsExifInfo>() {
            @Override
            public SnapsExifInfo createFromParcel(Parcel in) {
                return new SnapsExifInfo(in);
            }

            @Override
            public SnapsExifInfo[] newArray(int size) {
                return new SnapsExifInfo[size];
            }
        };

        public String getLocationStr() {
            return StringUtil.isSafeString(getLatitude()) && StringUtil.isSafeString(getLongitude()) ? String.format("%s,%s", getLatitude(), getLongitude()) : "";
        }

        private String getLatitude() {
            return latitude;
        }

        private String getLongitude() {
            return longitude;
        }

        public String getDate() {
            return date;
        }

        public String getOrientationTag() {
            return orientationTag;
        }

        public static class Builder {
            private String latitude, longitude;
            private String date;
            private String orientationTag;

            public Builder setLatitude(String latitude) {
                this.latitude = latitude;
                return this;
            }

            public Builder setLongitude(String longitude) {
                this.longitude = longitude;
                return this;
            }

            public Builder setDate(String date) {
                this.date = date;
                return this;
            }

            public Builder setOrientationTag(String orientationTag) {
                this.orientationTag = orientationTag;
                return this;
            }

            public SnapsExifInfo create() {
                return new SnapsExifInfo(this);
            }
        }
    }

    public static class GeoDegree {
        private boolean valid = false;
        private Float latitude, longitude;

        public GeoDegree(ExifInterface exif) throws Exception {
            String attrLATITUDE = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            String attrLATITUDE_REF = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
            String attrLONGITUDE = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
            String attrLONGITUDE_REF = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);

            if ((attrLATITUDE != null) && (attrLATITUDE_REF != null) && (attrLONGITUDE != null)
                    && (attrLONGITUDE_REF != null)) {
                valid = true;

                if (attrLATITUDE_REF.equals("N")) {
                    latitude = convertToDegree(attrLATITUDE);
                } else {
                    latitude = 0 - convertToDegree(attrLATITUDE);
                }

                if (attrLONGITUDE_REF.equals("E")) {
                    longitude = convertToDegree(attrLONGITUDE);
                } else {
                    longitude = 0 - convertToDegree(attrLONGITUDE);
                }
            }
        }

        private Float convertToDegree(String stringDMS) {
            Float result = null;
            String[] DMS = stringDMS.split(",", 3);

            String[] stringD = DMS[0].split("/", 2);
            Double D0 = new Double(stringD[0]);
            Double D1 = new Double(stringD[1]);
            Double FloatD = D0 / D1;

            String[] stringM = DMS[1].split("/", 2);
            Double M0 = new Double(stringM[0]);
            Double M1 = new Double(stringM[1]);
            Double FloatM = M0 / M1;

            String[] stringS = DMS[2].split("/", 2);
            Double S0 = new Double(stringS[0]);
            Double S1 = new Double(stringS[1]);
            Double FloatS = S0 / S1;

            result = new Float(FloatD + (FloatM / 60) + (FloatS / 3600));

            return result;

        }

        public boolean isValid() {
            return valid;
        }

        @Override
        public String toString() {
            return (String.valueOf(latitude) + ", " + String.valueOf(longitude));
        }

        public Float getLatitude() {
            return latitude;
        }

        public Float getLongitude() {
            return longitude;
        }

        public int getLatitudeE6() {
            return (int) (latitude * 1000000);
        }

        public int getLongitudeE6() {
            return (int) (longitude * 1000000);
        }
    }
}
