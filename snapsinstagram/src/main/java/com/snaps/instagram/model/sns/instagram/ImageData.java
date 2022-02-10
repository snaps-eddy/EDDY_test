package com.snaps.instagram.model.sns.instagram;

import android.graphics.BitmapFactory;
import android.graphics.Rect;

import com.snaps.common.utils.log.Dlog;
import com.snaps.instagram.utils.instagram.InstagramApp.BookMaker.CompleteListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

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
public class ImageData {
	private static final String TAG = ImageData.class.getSimpleName();
//	public int[] thumbSize = new int[2];
	public int[] lowResSize = new int[2];
	public int[] standardResSize = new int[2];
	public int[] maxImageResSize = new int[2];
//	public String thumbUrl = "";
	public String lowUrl = "";
	public String standardUrl = "";
	public String maxImageUrl = "";
	
	public CompleteListener listener;

	public ImageData clone() {
		ImageData newIns = new ImageData();
		newIns.lowResSize = this.lowResSize;
		newIns.standardResSize = this.standardResSize;
		newIns.maxImageResSize = this.maxImageResSize;
		newIns.lowUrl = this.lowUrl;
		newIns.standardUrl = this.standardUrl;
		newIns.maxImageUrl = this.maxImageUrl;
		newIns.listener = this.listener;
		return newIns;
	}

	public ImageData() {}
	public ImageData( String url ) {
//		thumbUrl = url;
		lowUrl = url;
		standardUrl = url;
//		thumbSize = getSizeFromInstagramImageUrl( url );
		lowResSize = getSizeFromInstagramImageUrl( url );
		standardResSize = lowResSize;
	}
	
	public ImageData( JSONObject jobj ) {
		try {
			JSONObject thumb, low, standard;
			thumb = jobj.getJSONObject( "thumbnail" );
			low = jobj.getJSONObject( "low_resolution" );
			standard = jobj.getJSONObject( "standard_resolution" );
//			thumbSize = new int[]{ thumb.getInt("width"), thumb.getInt( "height" ) };
//			thumbUrl = thumb.getString( "url" );
			lowResSize = new int[]{ low.getInt("width"), low.getInt( "height" ) };
			lowUrl = low.getString( "url" );
			standardResSize = new int[]{ standard.getInt("width"), standard.getInt( "height" ) };
			standardUrl = standard.getString( "url" );
		} catch (JSONException e) {
			Dlog.e(TAG, e);
		}
	}
	
	public void setCompleteListener( CompleteListener listener ) { this.listener = listener; }
	public void makeMaxImageData() {
		if( standardUrl == null || standardUrl.length() < 1 ) return;
		
		boolean getMaxImageUrlDone = false;
		
		maxImageUrl = getMaxImageUrl( standardUrl );
		int[] size = null;
		if( maxImageUrl != null && maxImageUrl.length() > 0 && !standardUrl.equals(maxImageUrl) ) {
			size = getImageSize( maxImageUrl );
			if( size != null && size.length > 1 && size[0] > 0 && size[1] > 0 ) {
				maxImageResSize = size;
				getMaxImageUrlDone = true;
			}
		}

		if( !getMaxImageUrlDone ) {
			maxImageResSize = new int[2];
			maxImageUrl = "";
		}
		
		if( listener != null ) {
			listener.onComplete( null );
			listener = null;
		}
	}
	
	private int[] getImageSize( String url ) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Rect r = new Rect();
		int[] size = new int[2];
		try {
			BitmapFactory.decodeStream((InputStream)new URL(url).getContent(), r, options);
			size[0] = options.outWidth;
			size[1] = options.outHeight;
		} catch (MalformedURLException e) { Dlog.e(TAG, e);
		} catch (IOException e) { Dlog.e(TAG, e); }
		
		return size;
	}
	
	public static String getMaxImageUrl( String url ) {
		StringBuilder sb = new StringBuilder();
		
		String[] parsed = url.split( "/" );
		if( parsed != null && parsed.length > 0 ) {
			for( int i = 0; i < parsed.length; ++i ) {
				if( (parsed[i].startsWith("s") || parsed[i].startsWith("p")) && parsed[i].contains("x") && parsed[i].length() < 11 ); // max s9999x9999 10
				else {
					if( i != 0 ) sb.append( "/" );
					sb.append( parsed[i] );
				}
			}
		}
		return sb.toString();
	}
	
	public static int[] getSizeFromInstagramImageUrl( String url ) {
		int[] result = new int[2];
		
		String[] parsed = url.split( "/" );
		if( parsed != null && parsed.length > 0 ) {
			for( int i = 0; i < parsed.length; ++i ) {
				if( (parsed[i].startsWith("s") || parsed[i].startsWith("p")) && parsed[i].contains("x") && parsed[i].length() < 11 ) { // max s9999x9999 10
					parsed = parsed[i].substring( 1, parsed[i].length() ).split( "x" );
					try {
						result[0] = Integer.parseInt( parsed[0] );
						result[1] = Integer.parseInt( parsed[1] );
					} catch( NumberFormatException e ) {
						Dlog.e(TAG, e);
						continue;
					}
					break;
				}
			}
		}

		return result;
	}
}
