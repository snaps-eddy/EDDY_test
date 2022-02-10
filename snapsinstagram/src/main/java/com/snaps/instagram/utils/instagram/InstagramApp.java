package com.snaps.instagram.utils.instagram;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.snaps.common.utils.log.Dlog;
import com.snaps.instagram.model.sns.instagram.ImageData;
import com.snaps.instagram.model.sns.instagram.LocationData;
import com.snaps.instagram.model.sns.instagram.PostData;
import com.snaps.instagram.model.sns.instagram.UserData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class InstagramApp {
	private InstagramSession mSession;
	private InstagramDialog mDialog;
	private OAuthAuthenticationListener mListener;
	private ProgressDialog mProgress;
	private String mAuthUrl;
	private String mTokenUrl;
	private String mAccessToken;
	private Context mCtx;
	private String mClientId;
	private String mClientSecret;

	private static int WHAT_FINALIZE = 0;
	private static int WHAT_ERROR = 1;
	private static int WHAT_FETCH_INFO = 2;

	/**
	 * Callback url, as set in 'Manage OAuth Costumers' page
	 * (https://developer.github.com/)
	 */

	public static String mCallbackUrl = "";
	private static final String AUTH_URL = "https://api.instagram.com/oauth/authorize/";
	private static final String TOKEN_URL = "https://api.instagram.com/oauth/access_token";
	private static final String API_URL = "https://api.instagram.com/v1";

	private static final String TAG = "InstagramAPI";

	public InstagramApp(Context context, String clientId, String clientSecret,
						String callbackUrl) {
		mClientId = clientId;
		mClientSecret = clientSecret;
		mCtx = context;
		mSession = new InstagramSession(context);
		mAccessToken = mSession.getAccessToken();
		mCallbackUrl = callbackUrl;
		mTokenUrl = TOKEN_URL + "?client_id=" + clientId + "&client_secret="
				+ clientSecret + "&redirect_uri=" + mCallbackUrl + "&grant_type=authorization_code";
		mAuthUrl = AUTH_URL + "?client_id=" + clientId + "&redirect_uri="
				+ mCallbackUrl + "&response_type=code&display=touch&scope=basic"
				+ "&lang=ko";
		OAuthDialogListener listener = new OAuthDialogListener() {
			@Override
			public void onComplete(String code) {
				getAccessToken(code);
			}

			@Override
			public void onError(String error) {
				if( mListener != null ) mListener.onFail("Authorization failed");
			}
		};

		mDialog = new InstagramDialog(context, mAuthUrl, listener);
		mProgress = new ProgressDialog(context);
		mProgress.setCancelable(false);
	}

	public ArrayList<InstagramImageData> getPhotoUrlList() {
		if( getId() == null || getId().length() < 1 ) return null;

		ArrayList<InstagramImageData> data = new ArrayList<InstagramImageData>();
		URL url = null;
		try {
			String urlString = API_URL + "/users/" + getId() + "/media/recent/?access_token=" + mAccessToken;
			JSONObject jobj;
			while( urlString != null && urlString.length() > 0 ) {
				url = new URL(urlString);

				InputStream inputStream = url.openConnection().getInputStream();
				String response = InstagramApp.streamToString(inputStream);
				if( response == null || response.length() < 1 ) break;

				jobj = new JSONObject( response );
				JSONArray imgJAry = InstagramApp.jsonNotNullCheck( jobj, "data" ) ? jobj.getJSONArray( "data" ) : new JSONArray();
				for( int i = 0; i < imgJAry.length(); ++i ) data.add( new InstagramImageData(imgJAry.getJSONObject(i).getString("id"), imgJAry.getJSONObject(i).getLong("created_time"), imgJAry.getJSONObject(i).getJSONObject( "images" )) );

				if( !jobj.has("pagination") || !jobj.getJSONObject("pagination").has("next_url") ) break;
				urlString = jobj.getJSONObject( "pagination" ).getString( "next_url" );
			}
			return data;
		} catch (MalformedURLException e) {
			Dlog.e(TAG, e);
			return data;
		} catch (IOException e) {
			Dlog.e(TAG, e);
			return data;
		} catch (JSONException e) {
			Dlog.e(TAG, e);
			return data;
		} catch (NullPointerException e) {
			Dlog.e(TAG, e);
			return data;
		}
	}

	private void getAccessToken(final String code) {
		mProgress.setMessage("Getting access token ...");
		mProgress.show();

		new Thread() {
			@Override
			public void run() {
				Dlog.i(TAG, "getAccessToken() Getting access token");
				int what = WHAT_FETCH_INFO;
				try {
					URL url = new URL(TOKEN_URL);
					//URL url = new URL(mTokenUrl + "&code=" + code);
					Dlog.i(TAG, "getAccessToken() Opening Token URL:" + url.toString());
					HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
					urlConnection.setRequestMethod("POST");
					urlConnection.setDoInput(true);
					urlConnection.setDoOutput(true);
					//urlConnection.connect();
					OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
					writer.write("client_id="+mClientId+
							"&client_secret="+mClientSecret+
							"&grant_type=authorization_code" +
							"&redirect_uri="+mCallbackUrl+
							"&code=" + code);
					writer.flush();
					String response = InstagramApp.streamToString(urlConnection.getInputStream());
					Dlog.i(TAG, "getAccessToken() response:" + response);
					JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
					mAccessToken = jsonObj.getString("access_token");
					Dlog.i(TAG, "getAccessToken() Got access token:" + mAccessToken);
					String id = jsonObj.getJSONObject("user").getString("id");
					String user = jsonObj.getJSONObject("user").getString("username");
					String name = jsonObj.getJSONObject("user").getString("full_name");
					mSession.storeAccessToken(mAccessToken, id, user, name);
				} catch (Exception ex) {
					what = WHAT_ERROR;
					Dlog.e(TAG, ex);
				}

				mHandler.sendMessage(mHandler.obtainMessage(what, 1, 0));
			}
		}.start();
	}

	private void fetchUserName() {
		mProgress.setMessage("Finalizing ...");
		new Thread() {
			@Override
			public void run() {
				Dlog.i(TAG, "fetchUserName() Fetching user info");
				int what = WHAT_FINALIZE;
				try {
					URL url = new URL(API_URL + "/users/" + mSession.getId() + "/?access_token=" + mAccessToken);

					Dlog.i(TAG, "fetchUserName() Opening URL " + url.toString());
					HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
					urlConnection.setRequestMethod("GET");
					urlConnection.setDoInput(true);
					urlConnection.connect();
					String response = InstagramApp.streamToString(urlConnection.getInputStream());
					System.out.println(response);
					JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
					String name = jsonObj.getJSONObject("data").getString("full_name");
					String bio = jsonObj.getJSONObject("data").getString("bio");
					Dlog.i(TAG, "fetchUserName() Got name: " + name + ", bio [" + bio + "]");
				} catch (Exception ex) {
					what = WHAT_ERROR;
					Dlog.e(TAG, ex);
				}

				mHandler.sendMessage(mHandler.obtainMessage(what, 2, 0));
			}
		}.start();
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == WHAT_ERROR) {
				mProgress.dismiss();
				if(msg.arg1 == 1) {
					if( mListener != null ) mListener.onFail("Failed to get access token");
				}
				else if(msg.arg1 == 2) {
					if( mListener != null ) mListener.onFail("Failed to get user information");
				}
			}
			else if(msg.what == WHAT_FETCH_INFO) {
				fetchUserName();
			}
			else {
				mProgress.dismiss();
				if( mListener != null ) mListener.onSuccess();
			}
		}
	};

	public boolean hasAccessToken() {
		return (mAccessToken == null) ? false : true;
	}

	public void setListener(OAuthAuthenticationListener listener) {
		mListener = listener;
	}

	public String getUserName() {
		return mSession.getUsername();
	}

	public String getId() {
		return mSession.getId();
	}

	public String getName() {
		return mSession.getName();
	}

	public String getAccessToken() {
		return mSession.getAccessToken();
	}

	public void authorize() {
		//Intent webAuthIntent = new Intent(Intent.ACTION_VIEW);
		//webAuthIntent.setData(Uri.parse(AUTH_URL));
		//mCtx.startActivity(webAuthIntent);
		mDialog.show();
	}

	public static String streamToString(InputStream is) throws IOException {
		String str = "";

		if (is != null) {
			StringBuilder sb = new StringBuilder();
			String line;

			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is));

				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}

				reader.close();
			} finally {
				is.close();
			}

			str = sb.toString();
		}

		return str;
	}

	public static Calendar getCalFromTimestamp( Long timestamp ) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis( timestamp * 1000 );
		return cal;
	}

	public static String decodeString( String encoded ) {
		String result = "";
		try {
			result = URLDecoder.decode( encoded, "UTF-8" );
		} catch (UnsupportedEncodingException e) { Dlog.e(TAG, e); }

		return result;
	}

	public void resetAccessToken() {
		if (mAccessToken != null) {
			mSession.resetAccessToken();
			mAccessToken = null;
		}
	}

	public interface OAuthAuthenticationListener {
		public abstract void onSuccess();
		public abstract void onFail(String error);
	}

	public static class BookMaker {
		public static final String API_BASE_URL = "https://api.instagram.com/v1/";
		public static final String BASE_DATA_URL = API_BASE_URL + "users/self?";
		public static final String POST_DATA_URL = API_BASE_URL + "users/self/media/recent?";
		public static final String FEED_DATA_URL = API_BASE_URL + "users/self/feed?";

		public static final int LINE_SPACINNG = 0;
        public static final float LINE_HEIGHT = 6f;

		public static final String[][] TEMPLATE_ID_TYPE = {
				{ "045021007251", "045021007252" } };					// A type

		public static final int TYPE_A = 0;
		public static final int TYPE_B = 1;
		public static final int TYPE_C = 2;
		public static final int TYPE_D = 3;
		public static final int TYPE_E = 4;
		public static final int TYPE_F = 5;
		public static final int TYPE_G = 6;

		private static BookMaker instance;

		private String accessToken;
		private String coverTitle, templateId, productCode;
		private String paperCode;

		private long startStamp, endStamp;
		private Calendar startDate, endDate;

		private boolean getTaggedFeed = true;
		public boolean getComments = true;

		private boolean makeMaxImagesAndCommentsDone = false;

		private UserData user;

		private CompleteListener postListener;
		private CompleteListener imageAndCommentListener;

		private ArrayList<PostData> postList;
		private ArrayList<Runnable> requestList;
		private ArrayList<CompleteListener> processList;
		private ArrayList<String> selectedIndex;

		public interface CompleteListener {
			void onComplete(Object result);
		}

		private BookMaker() {

		}

		public void setimageAndCommentListener( CompleteListener listener ) { this.imageAndCommentListener = listener; }
		public ArrayList<CompleteListener> getProcessList() { return this.processList; }

		public synchronized static BookMaker getInstance() {
			if (instance == null)
				instance = new BookMaker();
			return instance;
		}

		public String getTemplateId() { return templateId; }
		public String getProductCode() { return productCode; }
		public String getPaperCode() { return paperCode; }

		public String getCoverTitle() { return coverTitle; }
		public void setCoverTitle( String title ) { coverTitle = title; }

		public UserData getUser() { return user; }

		public Calendar getStartDate() { return startDate; }
		public Calendar getEndDate() { return endDate; }

		public void setAccessToken( String token ) { accessToken = token; }

		public ArrayList<PostData> getPostList() { return postList; }

		public ArrayList<LocationData> getLocationList() {
			ArrayList<PostData> list = getPostList();
			if(list == null || list.isEmpty()) return null;

			ArrayList<LocationData> result = new ArrayList<>();
			for(PostData post : list) {
				if(post == null || post.location == null || post.location.lat == 0 || post.location.lon == 0) continue;
				result.add(post.location);
			}
			return result;
		}

		public ArrayList<ImageData> getThumbImageList() {
			ArrayList<ImageData> list = new ArrayList<ImageData>();
			if( postList == null || postList.size() < 15 ) return list;

			ArrayList<PostData> tempList = (ArrayList<PostData>) postList.clone();
			Collections.sort( tempList, new PostLikeDescAndDateAscCompare() );
			for( int i = 0; i < tempList.size(); ++i ) list.add( postList.get(i).image );

			return list;
		}

		public PostData getBestPost( ArrayList<PostData> list ) {
			PostData result = null;
			if( list != null && list.size() > 0 ) {
				for( int i = 0; i < list.size(); ++i ) {
					if( result == null || result.likeCount < list.get(i).likeCount || (result.likeCount == list.get(i).likeCount && result.createdLong > list.get(i).createdLong) ) // 좋아요가 많거나, 좋아요가 같으면 너 오래된 포스트를 선택
						result = list.get(i);
				}
			}
			return result;
		}
		public PostData getBestPost() {
			return getBestPost( postList );
		}

		public void init( String prodCode, String templateId, String paperCode, String coverTitle, String startDate, String endDate, boolean getComments, boolean getTaggedFeed ) {
			postList = new ArrayList<PostData>();
			user = null;
			postListener = null;
			imageAndCommentListener = null;
			requestList = new ArrayList<Runnable>();

			this.productCode = prodCode;
			this.templateId = templateId;
			this.paperCode = paperCode;
			this.coverTitle = coverTitle;
			this.getComments = getComments;
//			this.getTaggedFeed = getTaggedFeed;
			this.getTaggedFeed = false; // 우선 삭제

			makeMaxImagesAndCommentsDone = false;

			try {
				this.startDate = Calendar.getInstance();
				this.endDate = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat( "yyyyMMdd", Locale.getDefault() );
				this.startDate.setTime( sdf.parse(startDate) );
				String newEndDate = getCorrectDateString( endDate );
				this.endDate.setTime(sdf.parse(newEndDate));

				startStamp = this.startDate.getTimeInMillis() / 1000;
				endStamp = this.endDate.getTimeInMillis() / 1000 + 60 * 60 * 24;
			} catch (ParseException e) {
				Dlog.e(TAG, e);
			}
		}

		public void execute( CompleteListener listener ) {
			this.postListener = listener;
			executeRequest(getBaseDataRunnable);
		}

		private void executeRequest( Runnable r ) {
			requestList.add(r);
			AsyncTask.execute(r);
		}

		private void checkBaseRequestDone( Runnable r ) {
			if( requestList == null ) return;

			requestList.remove(r);
			if( requestList.size() < 1 ) {
				executeRequest( getPostDataRunnable );
				if( getTaggedFeed ) executeRequest( getFeedDataRunnable );
			}
		}

		private void checkPostRequestDone( Runnable r ) {
			if( requestList == null) return;

			requestList.remove(r);
			if( requestList.size() < 1 ) {
				Collections.sort(postList, new NumberAscCompare());
				executeRequest( getCommentAndMaxImageRunnable );
			}
		}

		private void checkGetMaxImagesAndCommentsDone( CompleteListener listener ) {
			if (processList == null) return;

			processList.remove(listener);
			if (this.imageAndCommentListener != null)
				this.imageAndCommentListener.onComplete(processList);

			if (processList.size() < 1) {
				makeMaxImagesAndCommentsDone = true;
			}
		}

		public void setSelectedPost( ArrayList<String> selected ) {
			if (selected == null || selected.isEmpty()) return;

			this.selectedIndex = (ArrayList<String>) selected.clone();

			if( postList == null || postList.size() < 1 || selectedIndex == null || selectedIndex.size() < 1 ) return;

			ArrayList<Integer> idList = new ArrayList<Integer>();
			for( int i = 0; i < selectedIndex.size(); ++i ) idList.add( postList.size() - 1 - Integer.parseInt(selectedIndex.get(i)) );
			Collections.sort( idList, new IndexDescCompare() );

			for( int i = 0; i < idList.size(); ++i ) {
				if( idList.get(i) > -1 && idList.get(i) < postList.size() )
					postList.get( idList.get(i) ).clearListener();
			}
		}
		public void clearSelectedPosts() {
			if( postList == null || postList.size() < 1 || selectedIndex == null || selectedIndex.size() < 1 ) return;

			ArrayList<Integer> idList = new ArrayList<Integer>();
			for( int i = 0; i < selectedIndex.size(); ++i ) idList.add( postList.size() - 1 - Integer.parseInt(selectedIndex.get(i)) );
			Collections.sort( idList, new IndexDescCompare() );

			for( int i = 0; i < idList.size(); ++i ) {
				if( idList.get(i) > -1 && idList.get(i) < postList.size() )
					postList.remove( (int) idList.get(i) );
			}
		}

		private Runnable getBaseDataRunnable = new Runnable() {
			@Override
			public void run() {
				URL url = null;
				try {
					String urlString = getBaseDataUrl();
					JSONObject jobj;
					url = new URL(urlString);

					InputStream inputStream = url.openConnection().getInputStream();
					String response = InstagramApp.streamToString(inputStream);
					if( response != null && response.length() > 0 ) {
						jobj = new JSONObject( response );
						if( jobj.has("meta") && jobj.getJSONObject("meta").has("code") && jobj.getJSONObject("meta").getInt("code") == 200 && jobj.has("data") ) {
							jobj = jobj.getJSONObject( "data" );
							user = new UserData( jobj );
						}
					}
					checkBaseRequestDone( this );
				} catch (MalformedURLException e) {
					Dlog.e(TAG, e);
				} catch (IOException e) {
					Dlog.e(TAG, e);
				} catch (JSONException e) {
					Dlog.e(TAG, e);
				} catch (NullPointerException e) {
					Dlog.e(TAG, e);
				}
			}
		};

		private Runnable getPostDataRunnable = new Runnable() {
			@Override
			public void run() {
				URL url = null;
				try {
					String urlString = getPostDataUrl();
					JSONObject jobj;
					while( urlString != null && urlString.length() > 0 ) {
						url = new URL(urlString);

						InputStream inputStream = url.openConnection().getInputStream();
						String response = InstagramApp.streamToString(inputStream);
						if( response == null || response.length() < 1 ) break;

						jobj = new JSONObject( response );
						if( jobj.has("meta") && jobj.getJSONObject("meta").has("code") && jobj.getJSONObject("meta").getInt("code") == 200 && jobj.has("data") ) {
							JSONArray jary = jobj != null && jobj.has( "data" ) ? jobj.getJSONArray( "data" ) : new JSONArray();
							for( int i = 0; i < jary.length(); ++i ) {
								postList.add( new PostData( jary.getJSONObject(i)) );
//								postList.add( new PostData( jary.getJSONObject(i)) );
//								postList.add( new PostData( jary.getJSONObject(i)) );
//								postList.add( new PostData( jary.getJSONObject(i)) );
							}

							if( !jobj.has("pagination") || !jobj.getJSONObject("pagination").has("next_url") ) break;
							urlString = jobj.getJSONObject( "pagination" ).getString( "next_url" );
						}
						else break;
					}

//					//TODO  test code
//					int index = 0;
//					while( postList.size() < 500 ) {
//						if( index > postList.size() - 1 ) index = 0;
//						postList.add( postList.get(index).clone() );
//						index ++;
//					}
//					//TODO  test code end

					checkPostRequestDone( this );
				} catch (MalformedURLException e) {
					Dlog.e(TAG, e);
				} catch (IOException e) {
					Dlog.e(TAG, e);
				} catch (JSONException e) {
					Dlog.e(TAG, e);
				} catch (NullPointerException e) {
					Dlog.e(TAG, e);
				}
			}
		};

		private Runnable getFeedDataRunnable = new Runnable() {
			@Override
			public void run() {
				URL url = null;
				try {
					String urlString = getFeedDataUrl();
					JSONObject jobj, temp;
					while( urlString != null && urlString.length() > 0 ) {
						url = new URL(urlString);

						InputStream inputStream = url.openConnection().getInputStream();
						String response = InstagramApp.streamToString(inputStream);
						if( response == null || response.length() < 1 ) break;

						jobj = new JSONObject( response );
						if( jobj.has("meta") && jobj.getJSONObject("meta").has("code") && jobj.getJSONObject("meta").getInt("code") == 200 && jobj.has("data") ) {
							JSONArray jary = jobj != null && jobj.has( "data" ) ? jobj.getJSONArray( "data" ) : new JSONArray();
							for( int i = 0; i < jary.length(); ++i ) {
								temp = jary.getJSONObject( i );
								if( temp.has("users_in_photo") && hasUserInArray(temp.getJSONArray("users_in_photo"), user.id) )
									postList.add( new PostData(temp) );
							}

							if( !jobj.has("pagination") || !jobj.getJSONObject("pagination").has("next_url") ) break;
							urlString = jobj.getJSONObject( "pagination" ).getString( "next_url" );
						}
						else break;
					}

					checkPostRequestDone( this );
				} catch (MalformedURLException e) {
					Dlog.e(TAG, e);
				} catch (IOException e) {
					Dlog.e(TAG, e);
				} catch (JSONException e) {
					Dlog.e(TAG, e);
				} catch (NullPointerException e) {
					Dlog.e(TAG, e);
				}
			}
		};

		private boolean hasUserInArray( JSONArray jary, String id ) {
			boolean flag = false;
			JSONObject jobj;
			try {
				if( jary != null && jary.length() > 0 ) {
					for( int i = 0; i < jary.length(); ++i ) {
						jobj = jary.getJSONObject( i );
						if( jobj.has("user") && jobj.getJSONObject("user").has("id") && id.equals(jobj.getJSONObject("user").getString("id")) ) {
							flag = true;
							break;
						}
					}
				}
			} catch (JSONException e) { Dlog.e(TAG, e); }
			return flag;
		}

		private String getCorrectDateString( String dateString ) {
			if( dateString == null || dateString.length() < 8 ) return dateString;

			try {
				int year, month, day, lastDay;
				year = Integer.parseInt( dateString.substring(0, 4) );
				month = Integer.parseInt( dateString.substring(4, 6) );
				day = Integer.parseInt( dateString.substring(6, 8) );

				Calendar cal = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
				cal.setTime(sdf.parse(year + "-" + month));
				lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
				if( day > lastDay ) day = lastDay;
				return "" + year + (month > 9 ? month : "0" + month) + (day > 9 ? day : "0" + day);
			} catch ( Exception e ) {
				return dateString;
			}
		}

		private Runnable getCommentAndMaxImageRunnable = new Runnable() {
			@Override
			public void run() {
				processList = new ArrayList<CompleteListener>();

				// user max image  
				CompleteListener getUserMaxImageListener = new CompleteListener() {
					@Override
					public void onComplete(Object result) {
						checkGetMaxImagesAndCommentsDone(this);
					}
				};
				processList.add(getUserMaxImageListener );
				user.profile.setCompleteListener( getUserMaxImageListener );

				// post max image and comments
				for( int i = 0; i < postList.size(); ++i ) {
					// max image
					CompleteListener getPostMaxImageListener = new CompleteListener() {
						@Override
						public void onComplete(Object result) {
							checkGetMaxImagesAndCommentsDone(this);
						}
					};
					processList.add(getPostMaxImageListener );
					postList.get(i).image.setCompleteListener( getPostMaxImageListener );

					// comments
					if( getComments ) {
						CompleteListener getPostCommentsListener = new CompleteListener() {
							@Override
							public void onComplete(Object result) {
								checkGetMaxImagesAndCommentsDone(this);
							}
						};
						processList.add(getPostCommentsListener );
						postList.get(i).setCompleteListener(getPostCommentsListener);
					}
				}

				if( postListener != null ) postListener.onComplete( this );

				// execute
				user.profile.makeMaxImageData();
                synchronized ( postList ) { // indexOutofBoundsException이 나오는 경우가 있어 추가.
                    for( int i = 0; i < postList.size(); ++i ) {
                    	if (postList == null || postList.size() <= i) break;
						PostData postData = postList.get(i);
						if (postData == null) continue;

						if (postData.image != null)
							postData.image.makeMaxImageData();

                        if( getComments ) {
                        	postData.makeCommentData();
						}
                    }
                }
			}
		};

		public String getBaseDataUrl() {
			String url = "";
			if( accessToken != null && accessToken.length() > 0 ) url = BookMaker.BASE_DATA_URL + "access_token=" + accessToken;
			return url;
		}
		public String getPostDataUrl() {
			String url = "";
			if( accessToken != null && accessToken.length() > 0 ) url = BookMaker.POST_DATA_URL + "max_timestamp=" + endStamp + "&min_timestamp=" + startStamp + "&access_token=" + accessToken;
			return url;
		}
		public String getFeedDataUrl() {
			String url = "";
			if( accessToken != null && accessToken.length() > 0 ) url = BookMaker.FEED_DATA_URL + "max_timestamp=" + endStamp + "&min_timestamp=" + startStamp + "&access_token=" + accessToken;
			return url;
		}
		public String getCommentsUrl( String mediaId ) {
			String url = "";
			if( accessToken != null && accessToken.length() > 0 ) url = BookMaker.API_BASE_URL + "media/" + mediaId + "/comments?access_token=" + accessToken;
			return url;
		}

		public static String getFormattedDateString( Calendar cal, String format) {
			return getFormattedDateString( cal, format, Locale.US );
		}
		public static String getFormattedDateString( Calendar cal, String format, Locale locale ) {
			SimpleDateFormat sdf = new SimpleDateFormat( format, locale );
			return sdf.format(cal.getTime());
		}

		public static String getScaledNumberString( double number ) {
			final double million = 1000000;
			final double thousand = 1000;
			if( number > million ) return (int)(number / thousand) + "m";
			else if( number > thousand ) return (int)(number / thousand) + "k";
			else return (int)number + "";
		}

		/**
		 * 포스트 생성순
		 *
		 */
		public static class NumberAscCompare implements Comparator<PostData> {
			@Override
			public int compare(PostData arg0, PostData arg1) {
				return arg0.createdLong < arg1.createdLong ? -1 : arg0.createdLong > arg1.createdLong ? 1 : 0;
			}
		}

		public static class IndexDescCompare implements Comparator<Integer> {
			@Override
			public int compare(Integer arg0, Integer arg1) {
				return arg0 > arg1 ? -1 : arg0 < arg1 ? 1 : 0;
			}
		}

		public static class PostLikeDescAndDateAscCompare implements Comparator<PostData> {
			@Override
			public int compare(PostData arg0, PostData arg1) {
				if( arg0.likeCount != arg1.likeCount ) return arg0.likeCount > arg1.likeCount ? -1 : arg0.likeCount < arg1.likeCount ? 1 : 0;
				else return arg0.createdLong < arg1.createdLong ? -1 : arg0.createdLong > arg1.createdLong ? 1 : 0;
			}
		}
	}

	public static boolean jsonNotNullCheck( JSONObject jobj, String key ) {
		try {
			return jobj != null && jobj.has( key ) && !"null".equalsIgnoreCase( jobj.getString(key) );
		} catch (JSONException e) {
			Dlog.e(TAG, e);
			return false;
		}
	}
}