package com.snaps.instagram.model.sns.instagram;

import com.snaps.common.utils.log.Dlog;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 인스타그램북용 이미지 데이터 클래스.
 * 기존 사진선택에서 사용하던 InstagramImageData 클래스가 있지만 맥스 사이즈 이미지를 구하기 위해 인스타그램북용으로 다시 만듬.
 * 맥스 사이즈는 api제공이 아니므로 검증단계가 항상 필요함. 데이터가 없는 경우 standard로 대체.
 * @참고
 * @thumbnailSize 150x150
 * @lowResSize 320x320
 * @standardResSize 640x640
 * @maxSize 1080x1080
 * @author SongHW
 *
 */
public class VideoData {
	private static final String TAG = VideoData.class.getSimpleName();
	public int[] lowBandSize = new int[2];
	public int[] lowResSize = new int[2];
	public int[] standardResSize = new int[2];
	public String thumbUrl = "";
	public String lowUrl = "";
	public String standardUrl = "";
	
	public VideoData( JSONObject jobj ) {
		try {
			JSONObject lowBand, low, standard;
			lowBand = jobj.getJSONObject( "low_bandwidth" );
			low = jobj.getJSONObject( "low_resolution" );
			standard = jobj.getJSONObject( "standard_resolution" );
			lowBandSize = new int[]{ lowBand.getInt("width"), lowBand.getInt( "width" ) };
			thumbUrl = lowBand.getString( "url" );
			lowResSize = new int[]{ low.getInt("width"), low.getInt( "width" ) };
			lowUrl = low.getString( "url" );
			standardResSize = new int[]{ standard.getInt("width"), standard.getInt( "width" ) };
			standardUrl = standard.getString( "url" );
		} catch (JSONException e) {
			Dlog.e(TAG, e);
		}
	}
}
