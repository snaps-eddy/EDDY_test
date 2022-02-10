package com.snaps.facebook.model.sns.facebook;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AttachmentData {
	private static final String TAG = AttachmentData.class.getSimpleName();
	public static final int INVALID_IMAGE_DIMENSION = -999;

	public static final int TYPE_PHOTO = 0;
	public static final int TYPE_VIDEO = 1;
	public static final int TYPE_ALBUM = 2;
	public static final int TYPE_LINK = 3;
	public static final int TYPE_MAP = 4;

	public String description;
	public String title;
	public String imageUrl;
	public String thumbUrl;
	public String targetId;
	public String targetUrl;
	public String rawType;
	private String qrCodeUrl; // 타겟 주소를 qr코드로 뽑아야 될 경우를 위하여 변수 추가.
	public int width, height;
	public int type = TYPE_PHOTO;

	public String albumId = ""; // 이전 게시물 중 연속된 사진 포스트가 그룹핑 되는 경우가 있다. 그럴때 댓글을 앨범에서 불러오기 위하여 사진의 아이디를 기록해둠.

	public AttachmentData() {

	}

	public AttachmentData( String url, String thumb ) { // attachment 없이 full_picture, picture로 그릴 경우.
		imageUrl = url;
		thumbUrl = thumb;

		type = TYPE_PHOTO;
		title = "";
		targetId = "";
		targetUrl = "";
		qrCodeUrl = "";

		width = 0;
		height = 0;
	}


//	public AttachmentData( ArrayList<AttachmentData> list, String message, int type ) {}
//	
//	public AttachmentData( String jsonString, ArrayList<AttachmentData> list, String message, int type ) {
//		JSONObject jobj = null;
//		try {
//			jobj = new JSONObject( jsonString );
//		} catch (JSONException e) {
//			Dlog.e(TAG, e);
//			new AttachmentData( list, message, type );
//		}
//		
//		new AttachmentData( jobj, list, message, type, "" );
//	}

	public String getQrCodeUrl() { return this.qrCodeUrl; }

	public static ArrayList<AttachmentData> makeAttList( JSONObject jobj, String parentMessage, int parentType, String albumId ) {
		ArrayList<AttachmentData> attList = new ArrayList<AttachmentData>();
		AttachmentData att = new AttachmentData();

		try {
			att.description = jobj.has( "description" ) ? jobj.getString( "description" ) : "";
			att.rawType = jobj.getString( "type" );
			att.type = att.rawType.startsWith( "video" ) ? TYPE_VIDEO : TYPE_PHOTO;
			switch( att.rawType ) {
				case "map": att.type = TYPE_MAP;
				case "album": att.type = TYPE_ALBUM; break;
				case "share": att.type = TYPE_LINK; break;
			}

			if( jobj.has("target") ) {
				if( jobj.getJSONObject("target").has("id") ) att.targetId = jobj.getJSONObject("target").getString( "id" );
				if( jobj.getJSONObject("target").has("url") ) att.targetUrl = jobj.getJSONObject("target").getString( "url" );
			}
			else if( jobj.has("url") ) att.targetId = jobj.getString( "url" );

			att.qrCodeUrl = ( (att.type == TYPE_VIDEO || att.type == TYPE_LINK ) && att.targetUrl != null ) ? att.targetUrl : "";
			if( (att.type == TYPE_VIDEO || att.type == TYPE_LINK ) && att.qrCodeUrl.length() < 1 && jobj.has("url") ) att.qrCodeUrl = jobj.getString( "url" ); // 데이터형에 따른 보정코드.

			att.title = jobj.has( "title" ) ? jobj.getString( "title" ) : "";

			JSONArray jary = null;
			boolean isVideoContents = jobj.has("type") && jobj.getString( "type" ).contains("video");

			if (isVideoContents) {
				att.imageUrl = jobj.getJSONObject( "media" ).getJSONObject( "image" ).has( "src" ) ? jobj.getJSONObject( "media" ).getJSONObject( "image" ).getString( "src" ) : "";
				//들어오는 정보가 부정확하기 때문에 만들기 단계에서 직접 측정한다
//				att.width = jobj.getJSONObject( "media" ).getJSONObject( "image" ).has( "width" ) ? jobj.getJSONObject( "media" ).getJSONObject( "image" ).getInt( "width" ) : 0;
//				att.height = jobj.getJSONObject( "media" ).getJSONObject( "image" ).has( "height" ) ? jobj.getJSONObject( "media" ).getJSONObject( "image" ).getInt( "height" ) : 0;
				att.width = INVALID_IMAGE_DIMENSION;
				att.height = INVALID_IMAGE_DIMENSION;

				att.thumbUrl = att.imageUrl;
				att.albumId = albumId;
				attList.add( att );
			} else if( att.type != TYPE_ALBUM && jobj.has("media") ) {
				att.width = jobj.getJSONObject( "media" ).getJSONObject( "image" ).has( "width" ) ? jobj.getJSONObject( "media" ).getJSONObject( "image" ).getInt( "width" ) : 0;
				att.height = jobj.getJSONObject( "media" ).getJSONObject( "image" ).has( "height" ) ? jobj.getJSONObject( "media" ).getJSONObject( "image" ).getInt( "height" ) : 0;
				att.imageUrl = jobj.getJSONObject( "media" ).getJSONObject( "image" ).has( "src" ) ? jobj.getJSONObject( "media" ).getJSONObject( "image" ).getString( "src" ) : "";

				att.thumbUrl = att.imageUrl;
				att.albumId = albumId;
				attList.add( att );
			}
			else if( jobj.has("subattachments") ) {
				jary = jobj.getJSONObject( "subattachments" ).getJSONArray( "data" );
				if( !jobj.has("target") || (jobj.has("media") && !StringUtil.isEmpty(parentMessage)) ) { // media가 있으면 중복된 포스트(스크랩되어 합쳐진 포스트이므로 1개만) -> target이 없을때 중복된 포스트로 판단하는걸로 변경. -> 둘다 조건으로 넣는걸로.
//				if( !jobj.has("target") || jobj.has("media") ) { // media가 있으면 중복된 포스트(스크랩되어 합쳐진 포스트이므로 1개만) -> target이 없을때 중복된 포스트로 판단하는걸로 변경. -> 둘다 조건으로 넣는걸로.
					for( int i = 0; i < jary.length(); ++i ) {
						jobj = jary.getJSONObject(i);
						if( jobj.has("description") && parentMessage.equalsIgnoreCase(jobj.getString("description")) ) { // 포스트의 메세지와 subattachment의 description이 같은놈을 추가.
							if( jobj.has("target") && jobj.getJSONObject("target").has("id") )
								albumId = jobj.getJSONObject( "target" ).getString( "id" );
							attList.addAll( AttachmentData.makeAttList(jobj, att.description, att.type, albumId) );
							return attList;
						}
					}
					if( jary != null && jary.length() > 0 ) {
						jobj = jary.getJSONObject( jary.length() - 1 );
						if( jobj.has("target") && jobj.getJSONObject("target").has("id") )
							albumId = jobj.getJSONObject( "target" ).getString( "id" );
						attList.addAll( AttachmentData.makeAttList(jobj, att.description, att.type, albumId) ); // 추가되는 놈이 없다면 리스트의 마지막놈을 추가.
					}
				}
				else {
					for( int i = 0; i < jary.length(); ++i )
						attList.addAll( AttachmentData.makeAttList(jary.getJSONObject(i), att.description, att.type, "") );
				}
			}
		} catch (JSONException e) {
			Dlog.e(TAG, e);
		}

		return attList;
	}
}