package com.snaps.instagram.model.sns.instagram;

import com.snaps.common.utils.log.Dlog;

import org.json.JSONException;
import org.json.JSONObject;

public class LocationData {
	private static final String TAG = LocationData.class.getSimpleName();
	public double lon, lat;
	public String id, name;

	public LocationData( JSONObject jobj ) {
		try {
			id = jobj.has( "id" ) ? jobj.getString( "id" ) : "";
			name = jobj.has( "name" ) ? jobj.getString( "name" ) : "";
			lon = jobj.has( "longitude" ) ? jobj.getDouble( "longitude" ) : 0;
			lat = jobj.has( "latitude" ) ? jobj.getDouble( "latitude" ) : 0;
		} catch (JSONException e) {
			Dlog.e(TAG, e);
		}
	}

}