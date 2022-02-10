package com.snaps.common.structure;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.file.InFileUtils;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.xml.XmlResult;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

public class SnapsMaxPageInfo implements Parcelable, Serializable {
	private static final String TAG = SnapsMaxPageInfo.class.getSimpleName();
	private static final long serialVersionUID = 7345049930822150227L;
	// 캐쉬에 저장이 되는 파일명 설정.
	static String fileName = "maxpageInfo.xml";

	ArrayList<SnapsPaperInfo> pageInfos = new ArrayList<SnapsPaperInfo>();
	String sPath = "";
	
	private String versionCode;


	public SnapsMaxPageInfo(Context context) {
		sPath = getFilePath(context);
//		loadInfo();
	}

	protected SnapsMaxPageInfo(Parcel in) {
		pageInfos = in.createTypedArrayList(SnapsPaperInfo.CREATOR);
		sPath = in.readString();
		versionCode = in.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeTypedList(pageInfos);
		dest.writeString(sPath);
		dest.writeString(versionCode);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<SnapsMaxPageInfo> CREATOR = new Creator<SnapsMaxPageInfo>() {
		@Override
		public SnapsMaxPageInfo createFromParcel(Parcel in) {
			return new SnapsMaxPageInfo(in);
		}

		@Override
		public SnapsMaxPageInfo[] newArray(int size) {
			return new SnapsMaxPageInfo[size];
		}
	};

	public void addPageInfo(String paperCode, JsonObject object ) {
        pageInfos.add( SnapsPaperInfo.getPaperInfo(paperCode, object) );
    }

    public int getPageInfoCount() {
        return pageInfos != null ? pageInfos.size() : 0;
    }

	//mo_maxpage적용을 위해 method 추가 // MenuDataManager 접근할수 없으므로 삭제
//	public static SnapsMaxPageInfo readMaxPageInfo(Context context){
//		return (SnapsMaxPageInfo)InFileUtils.readInnerFile(context,fileName);



//        try {
//            FileReader reader = new FileReader( getFilePath(context) );
//            Gson gson = new Gson();
//            return gson.fromJson( reader, SnapsMaxPageInfo.class );
//        } catch (FileNotFoundException e) {
//            Dlog.e(TAG, e);
//        }
//        return null;
//	}


	/***
	 * 페이퍼 코드를 가지고 맥스페이지를 구하는 함수
	 * @param paperCode
	 * @return
     */
	public String getMaxPageWithPaperCode(String paperCode)
	{
		for (SnapsPaperInfo pageInfo : pageInfos) {
			if (pageInfo.paperCode.equals(paperCode)) {
				//내부적으로 151 => 75
				//131=>65 사용하기 때문에 2분의 1로 변환을 한다
				return Integer.parseInt(pageInfo.mo_maxPage)/2 + "";
			}
		}

		return null;
	}
	/***
	 * Object형태로 저장을 하는 함수
	 */
	void writeMaxPageInfoToObject(Context context){
		//파일을 지운다
		InFileUtils.deleteInnerFile(context,fileName);
		//파일을 저장한다.
		InFileUtils.saveInnerFile(context,this,fileName);
	}

	public boolean loadInfo(Context context) {

		File file = new File(sPath);
		try {
			XmlResult result = new XmlResult(file);
			versionCode = result.getFromRoot( "version" );
			NodeList pageList = result.getNodeList("paper");

			for (int i = 0; i < pageList.getLength(); i++) {

				SnapsPaperInfo pagerInfo = new SnapsPaperInfo();

				Node page = pageList.item(i);
				pagerInfo.paperCode = ((Element) page).getAttribute("code");
				pagerInfo.millimeter = ((Element) page).getAttribute("millimeter");
				pagerInfo.mo_maxPage = ((Element) page).getAttribute("mo_maxpage");
				NodeList spineList = result.getElements(page, "spine");

				for (int a = 0; a < spineList.getLength(); a++) {
					Node spine = spineList.item(a);

					SnapsSpineInfo spineInfo = new SnapsSpineInfo();

					spineInfo.number = ((Element) spine).getAttribute("number");
					spineInfo.millimeter = ((Element) spine).getAttribute("millimeter");
					spineInfo.thickness = ((Element) spine).getAttribute("thickness");
					spineInfo.page_min = ((Element) spine).getAttribute("page_min");
					spineInfo.page_max = ((Element) spine).getAttribute("page_max");

					pagerInfo.spineInfo.add(spineInfo);

				}

				pageInfos.add(pagerInfo);

			}

			result.close();
			writeMaxPageInfoToObject(context);
		} catch (Exception e) {
			Dlog.e(TAG, e);
			return false;
		}

		return pageInfos != null && !pageInfos.isEmpty();
	}
	static public boolean isMaxpageInfoFile(Context context) {
		String sPath = getFilePath(context);
		File file = new File(sPath);
		return file.exists();
	}

	static public String getFilePath(Context context) {
		String filePath = Const_VALUE.PATH_PACKAGE(context, false) + "/template/" + fileName;
		return filePath;
	}

	/***
	 * 페이퍼 코드와 장수를 가지고 spine MM 사이즈를 구하는 함수.
	 * 
	 * @param paperCode
	 * @param pageCnt
	 * @return
	 */
	public float getHardCoverSpineMMSize(String paperCode, int pageCnt) {
		int page = pageCnt * 2 + 1;
		if (page > 401)
			page = 401;

		float firstSection = -1;
		for (SnapsPaperInfo pageInfo : pageInfos) {
			if (pageInfo.paperCode.equals(paperCode)) {
				for (SnapsSpineInfo spine : pageInfo.spineInfo) {
					if (firstSection == -1)
						firstSection = Float.parseFloat(spine.millimeter);
					if (Integer.parseInt(spine.page_max) >= page && Integer.parseInt(spine.page_min) <= page) {
						return Float.parseFloat(spine.millimeter);
					}
				}
			}
		}

		if (firstSection == -1) {
			Dlog.e(TAG, "getHardCoverSpineMMSize() unknown paperCode:" + paperCode);
		}

		return firstSection;
	}
	
	public String getVersionCode() {
		return versionCode;
	}
    public void setVersionCode( String code ) { versionCode = code; }

	public float getSoftCoverPageThickMMSize(String paperCode) {
		for (SnapsPaperInfo pageInfo : pageInfos) {
			if (pageInfo.paperCode.equals(paperCode)) {
				return Float.parseFloat(pageInfo.millimeter);
			}
		}

		Dlog.e(TAG, "getSoftCoverPageThickMMSize() unknown PaperCode:" + paperCode);
		return 0.f;
	}

	/***
	 * 커버엣지 타입을 가져오는 함수...
	 * 
	 * @param paperCode
	 * @param pageCnt
	 * @return
	 */
	public String getCoverEdgeType(String paperCode, int pageCnt) {
		int page = pageCnt * 2 + 1;
		if (page > 401)
			page = 401;
		for (SnapsPaperInfo pageInfo : pageInfos) {
			if (pageInfo.paperCode.equals(paperCode)) {
				for (SnapsSpineInfo spine : pageInfo.spineInfo) {
					if (Integer.parseInt(spine.page_max) >= page && Integer.parseInt(spine.page_min) <= page) {
						return spine.number;
					}
				}
			}
		}

		return "1";
	}

	static class SnapsSpineInfo implements Parcelable, Serializable {
		private static final long serialVersionUID = -8839886113659513963L;
		String number = "";
		String millimeter = "";
		String thickness = "";
		String page_min = "";
		String page_max = "";
		
		public SnapsSpineInfo() {
			
		}

		protected SnapsSpineInfo(Parcel in) {
			number = in.readString();
			millimeter = in.readString();
			thickness = in.readString();
			page_min = in.readString();
			page_max = in.readString();
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(number);
			dest.writeString(millimeter);
			dest.writeString(thickness);
			dest.writeString(page_min);
			dest.writeString(page_max);
		}

		@Override
		public int describeContents() {
			return 0;
		}

		public static final Creator<SnapsSpineInfo> CREATOR = new Creator<SnapsSpineInfo>() {
			@Override
			public SnapsSpineInfo createFromParcel(Parcel in) {
				return new SnapsSpineInfo(in);
			}

			@Override
			public SnapsSpineInfo[] newArray(int size) {
				return new SnapsSpineInfo[size];
			}
		};

		public static SnapsSpineInfo getSpineInfo(JsonObject object ) {
            SnapsSpineInfo info = new SnapsSpineInfo();
            info.number = object.has( "number" ) ? object.get( "number" ).getAsString() : "";
            info.millimeter = object.has( "millimeter" ) ? object.get( "millimeter" ).getAsString() : "";
            info.thickness = object.has( "thickness" ) ? object.get( "thickness" ).getAsString() : "";
            info.page_min = object.has( "page_min" ) ? object.get( "page_min" ).getAsString() : "";
            info.page_max = object.has( "page_max" ) ? object.get( "page_max" ).getAsString() : "";
            return info;
        }
		
	}

	static class SnapsPaperInfo implements Parcelable, Serializable {
		private static final long serialVersionUID = 7179692917831859529L;
		String paperCode = "";
		String millimeter = "";
		ArrayList<SnapsSpineInfo> spineInfo = new ArrayList<SnapsSpineInfo>();
		String mo_maxPage = "";
		
		public SnapsPaperInfo() {
			
		}

		protected SnapsPaperInfo(Parcel in) {
			paperCode = in.readString();
			millimeter = in.readString();
			spineInfo = in.createTypedArrayList(SnapsSpineInfo.CREATOR);
			mo_maxPage = in.readString();
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(paperCode);
			dest.writeString(millimeter);
			dest.writeTypedList(spineInfo);
			dest.writeString(mo_maxPage);
		}

		@Override
		public int describeContents() {
			return 0;
		}

		public static final Creator<SnapsPaperInfo> CREATOR = new Creator<SnapsPaperInfo>() {
			@Override
			public SnapsPaperInfo createFromParcel(Parcel in) {
				return new SnapsPaperInfo(in);
			}

			@Override
			public SnapsPaperInfo[] newArray(int size) {
				return new SnapsPaperInfo[size];
			}
		};

		public static SnapsPaperInfo getPaperInfo(String paperCode, JsonObject object ) {
            SnapsPaperInfo info = new SnapsPaperInfo();
            info.paperCode = paperCode;
            info.millimeter = object.has( "millimeter" ) ? object.get( "millimeter" ).getAsString() : "";
            info.mo_maxPage = object.has( "mo_maxpage" ) ? object.get( "mo_maxpage" ).getAsString() : "";

            JsonArray jsonArray = object.has( "spine" ) ? object.getAsJsonArray( "spine" ) : null;
            if( jsonArray != null && jsonArray.size() > 0 ) {
                for( int i = 0; i < jsonArray.size(); ++i )
                    info.spineInfo.add( SnapsSpineInfo.getSpineInfo(jsonArray.get(i).getAsJsonObject()) );
            }
            return info;
        }
		
	}
}
