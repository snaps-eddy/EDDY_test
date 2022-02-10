package com.snaps.common.utils.net.http;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Looper;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.file.FlushedInputStream;
import com.snaps.common.utils.imageloader.SnapsImageDimensionMeasurer;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.log.SnapsInterfaceLogListener;
import com.snaps.common.utils.ui.StringUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HttpUtil {
	private static final String TAG = HttpUtil.class.getSimpleName();

	static final int TIMEOUT = 60 * 1000;
	static final int IO_BUFFER_SIZE = 8 * 1024 * 10;// 80kb

	public static String connectPost(String httpUrl, List<NameValuePair> postParameters,  SnapsInterfaceLogListener interfaceLogListener) {
		return connectPost(httpUrl, null, postParameters, interfaceLogListener);
	}

	public static String connectPost(String httpUrl, String header, List<NameValuePair> postParameters,  SnapsInterfaceLogListener interfaceLogListener) {
		BufferedReader bufferedReader = null;
		String res = null;
		InputStream inputStream = null;
		try {
			HttpClient client = new DefaultHttpClient();
			client.getParams().setParameter("http.protocol.expect-continue", false);
			client.getParams().setParameter("http.connection.timeout", TIMEOUT);
			client.getParams().setParameter("http.socket.timeout", TIMEOUT);

			HttpPost request = new HttpPost(httpUrl);

			if (!StringUtil.isEmpty(header)) {
				request.setHeader("Content-Type", header);
			}

			if (postParameters != null) {
				UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postParameters, "UTF-8");
				request.setEntity(formEntity);
			}

			if (postParameters != null) {
				httpUrl += (httpUrl.contains("?") ? "&" : "?") + URLEncodedUtils.format(postParameters, "utf-8");
			}
			if (interfaceLogListener != null) {
				interfaceLogListener.onSnapsInterfacePreRequest(httpUrl);
			}

			HttpResponse response = client.execute(request);

			//성공인 아닌 경우 로그 추가 및 에러 처리
			if (response != null && response.getStatusLine() != null) {
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode != 200) {
					if (interfaceLogListener != null) {
						String log = "";
						log += "connectPost()\n";
						log += "request : " + httpUrl + "\n";
						log += "response : " + response.getStatusLine().toString();
						interfaceLogListener.onSnapsInterfaceResult(statusCode, log);
					}
					return null;
				}
			}

			inputStream = response.getEntity().getContent();

			bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

			StringBuilder result = new StringBuilder();
			String line = "";
			while ((line = bufferedReader.readLine()) != null)
				result.append(line);

			res = result.toString();
			if (interfaceLogListener != null) {
				interfaceLogListener.onSnapsInterfaceResult(getHttpResponseStatusCode(response), res);
			}
		} catch (Exception e) {
			Dlog.e(TAG, e);
			if (interfaceLogListener != null) {
				interfaceLogListener.onSnapsInterfaceException(e);
			}
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				Dlog.e(TAG, e);
			}

			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					Dlog.e(TAG, e);
				}
			}
		}

		return res;
	}

	/**
	 * ###### Stream 반드시 닫을 것!!!
	 */
	public static HttpResponse connectPostReturnHttpResponse(String httpUrl, String header, List<NameValuePair> postParameters, SnapsInterfaceLogListener interfaceLogListener) {
		try {
			HttpClient client = new DefaultHttpClient();
			client.getParams().setParameter("http.protocol.expect-continue", false);
			client.getParams().setParameter("http.connection.timeout", TIMEOUT);
			client.getParams().setParameter("http.socket.timeout", TIMEOUT);

			HttpPost request = new HttpPost(httpUrl);

			if (!StringUtil.isEmpty(header)) {
				request.setHeader("Content-Type", header);
			}

			if (postParameters != null) {
				UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postParameters);
				request.setEntity(formEntity);
			}

			if (postParameters != null) {
				httpUrl += (httpUrl.contains("?") ? "&" : "?") + postParameters;
			}
			if (interfaceLogListener != null) {
				interfaceLogListener.onSnapsInterfacePreRequest(httpUrl);
			}

			return client.execute(request);
		} catch (Exception e) {
			Dlog.e(TAG, e);
			if (interfaceLogListener != null) {
				interfaceLogListener.onSnapsInterfaceException(e);
			}
		}

		return null;
	}

	public static String getConnectRequestUrl(String httpUrl, List<NameValuePair> postParameters) {
		return postParameters != null ? (httpUrl + (httpUrl.contains("?") ? "&" : "?") + postParameters) : httpUrl;
	}

	public static int requestJsonReturnResponseCode(String httpUrl, String jsonStr, SnapsInterfaceLogListener interfaceLogListener) {
		HttpURLConnection urlConnection = null;
//		BufferedReader reader = null;
		try {
			if (interfaceLogListener != null) {
				interfaceLogListener.onSnapsInterfacePreRequest(httpUrl);
			}

			URL url = new URL(httpUrl);
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setDoOutput(true);
			urlConnection.setRequestMethod("POST");
			urlConnection.setRequestProperty("Content-Type", "application/json");
			urlConnection.setRequestProperty("Accept", "application/json");

			Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
			writer.write(jsonStr);
			writer.close();

			return urlConnection.getResponseCode();
//			InputStream inputStream = urlConnection.getInputStream();
//			StringBuilder buffer = new StringBuilder();
//			if (inputStream == null) {
//				return null;
//			}
//			reader = new BufferedReader(new InputStreamReader(inputStream));
//
//			String inputLine;
//			while ((inputLine = reader.readLine()) != null)
//				buffer.append(inputLine).append("\n");
//
//			return buffer.toString();
		} catch(Exception e){
			Dlog.e(TAG, e);
			if (interfaceLogListener != null) {
				interfaceLogListener.onSnapsInterfaceException(e);
			}
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
//			if (reader != null) {
//				try {
//					reader.close();
//				} catch (final IOException e) {}
//			}
		}
		return -1;
	}

	public static String connectGet(String httpUrl, SnapsInterfaceLogListener interfaceLogListener) {
		return connectGet(httpUrl, null, interfaceLogListener);
	}

	public static String getEncodedUrlWithParams(String httpUrl, List<NameValuePair> params) {
		return params != null
				? httpUrl + (httpUrl.contains("?") ? "&" : "?") + URLEncodedUtils.format(params, "utf-8")
				: httpUrl;
	}

	public static String connectGet(String httpUrl, List<NameValuePair> params, SnapsInterfaceLogListener interfaceLogListener) {
		BufferedReader in = null;
		String res = null;
		try {
			HttpClient client = new DefaultHttpClient();
			client.getParams().setParameter("http.protocol.expect-continue", false);
			client.getParams().setParameter("http.connection.timeout", TIMEOUT);
			client.getParams().setParameter("http.socket.timeout", TIMEOUT);

			httpUrl = getEncodedUrlWithParams(httpUrl, params);

			if (interfaceLogListener != null) {
				interfaceLogListener.onSnapsInterfacePreRequest(httpUrl);
			}

			HttpGet request = new HttpGet(httpUrl);
			HttpResponse response = client.execute(request);
			in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			StringBuilder result = new StringBuilder();
			String line = "";
			while ((line = in.readLine()) != null)
				result.append(line);

			res = result.toString();
			if (interfaceLogListener != null) {
				interfaceLogListener.onSnapsInterfaceResult(getHttpResponseStatusCode(response), res);
			}

			// return res;
		} catch (Exception e) {
			Dlog.e(TAG, e);
			res = "connect exception : " + e.toString();
			if (interfaceLogListener != null) {
				interfaceLogListener.onSnapsInterfaceException(e);
			}
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					Dlog.e(TAG, e);
				}
			}
		}

		return res;
	}

	public static int getHttpResponseStatusCode(HttpResponse response) {
		if (response == null) return -1;
		try {
			return response.getStatusLine().getStatusCode();
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
		return -1;
	}

	public static Reader connectGetReader(String httpUrl, List<NameValuePair> params, SnapsInterfaceLogListener interfaceLogListener) {
		try {
			HttpClient client = new DefaultHttpClient();
			client.getParams().setParameter("http.protocol.expect-continue", false);
			client.getParams().setParameter("http.connection.timeout", TIMEOUT);
			client.getParams().setParameter("http.socket.timeout", TIMEOUT);

			if (params != null) {
				httpUrl += (httpUrl.contains("?") ? "&" : "?") + URLEncodedUtils.format(params, "utf-8");

                for( NameValuePair param : params )
                    if( param.getValue() == null )
                        param = new BasicNameValuePair( param.getName(), "" );
			}

			if (interfaceLogListener != null) {
				interfaceLogListener.onSnapsInterfacePreRequest(httpUrl);
			}

			HttpGet request = new HttpGet(httpUrl);
			HttpResponse response = client.execute(request);

			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			if (interfaceLogListener != null) {
				interfaceLogListener.onSnapsInterfaceResult(getHttpResponseStatusCode(response), "connectGetReader");
			}

			return reader;
		} catch (Exception e) {
			Dlog.e(TAG, e);
			if (interfaceLogListener != null) {
				interfaceLogListener.onSnapsInterfaceException(e);
			}
		} //TODO  구현 부에 반드시 리더 close 해 줄것!!!

		return null;
	}

	/*
	 * public static String connectGet2(String httpUrl) { String url = httpUrl;
	 * 
	 * URL obj; try { obj = new URL(url); HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	 * 
	 * // optional default is GET con.setRequestMethod("GET");
	 * 
	 * // add request header // con.setRequestProperty("User-Agent", USER_AGENT);
	 * 
	 * int responseCode = con.getResponseCode(); System.out.println("\nSending 'GET' request to URL : " + url); System.out.println("Response Code : " + responseCode);
	 * 
	 * BufferedReader in;
	 * 
	 * in = new BufferedReader(new InputStreamReader(con.getInputStream()));
	 * 
	 * String inputLine; StringBuffer response = new StringBuffer();
	 * 
	 * while ((inputLine = in.readLine()) != null) { response.append(inputLine); }
	 * 
	 * in.close(); return response.toString(); } catch (MalformedURLException e) { // TODO Auto-generated catch block Dlog.e(TAG, e); } catch (IOException e) { // TODO Auto-generated catch block
	 * Dlog.e(TAG, e); } return null;
	 * 
	 * }
	 */
	/**
	 * 서버 Url로 부터 파일을 다운받아 로컬경로에 저장합니다.
	 * 
	 * @param urlString
	 * @param filePath
	 * @return
	 */
	public static boolean saveUrlToFile(String urlString, String filePath) {
		HttpURLConnection urlConnection = null;
		BufferedOutputStream out = null;
		BufferedInputStream in = null;

        File targetFile = new File( filePath );
		File targetDir = targetFile.getParentFile();
		if (!targetDir.exists() && !targetDir.mkdirs()) {
			return false;
		}

        if( !targetFile.exists() ) {
            try {
				targetFile.createNewFile();
            } catch (IOException e) {
				Dlog.e(TAG, e);
            }
        }

        boolean isSucess = false;
		FlushedInputStream fis = null;

		try {
			final URL url = new URL(urlString);
			urlConnection = (HttpURLConnection) url.openConnection();

			fis = new FlushedInputStream(urlConnection.getInputStream());
			in = new BufferedInputStream(fis, IO_BUFFER_SIZE);
			out = new BufferedOutputStream(new FileOutputStream(filePath), IO_BUFFER_SIZE);

			int b;
			while ((b = in.read()) != -1) {
				out.write(b);
			}
			isSucess = true;
		} catch (IOException e) {
			Dlog.e(TAG, e);
			isSucess = false;
		} finally {
			if (urlConnection != null)
				urlConnection.disconnect();
			try {
				if (fis != null) {
					fis.close();
				}

				if (out != null) {
					out.flush();
					out.close();
				}
				if (in != null)
					in.close();
			} catch (IOException e) {
				Dlog.e(TAG, e);
				isSucess = false;
			}
			if(targetFile.length() == 0) {
				targetFile.delete();
				isSucess = false;
			}
		}
		return isSucess;
	}

    public static interface DownloadProgressListener {
        public void updateProgress( long current, long total );
    }

    /**
     * 서버 Url로 부터 파일을 다운받아 로컬경로에 저장합니다.
     *
     * @param urlString
     * @param filePath
     * @return
     */
    public static boolean saveUrlToFileWithListener(String urlString, String filePath, DownloadProgressListener listener ) {
        HttpURLConnection urlConnection = null;
        FileOutputStream out = null;

        File targetFile = new File( filePath );
        File targetDir = targetFile.getParentFile();
        if (!targetDir.exists() && !targetDir.mkdirs()) {
			return false;
        }

        if( !targetFile.exists() ) {
            try {
				if (!targetFile.createNewFile()) {
					return false;
				}
            } catch (IOException e) {
				Dlog.e(TAG, e);
			}
        }

        boolean isSucess = false;

        FlushedInputStream fis = null;
        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
			fis = new FlushedInputStream(urlConnection.getInputStream());
            out = new FileOutputStream( filePath );

            long fileLength = urlConnection.getContentLength();

            byte[] data = new byte[4096];
            int b;
            long total = 0;
            while ((b = fis.read(data)) != -1) {
                out.write( data, 0, b );
                total += b;
                if( listener != null )
                    listener.updateProgress( total, fileLength );
            }
            isSucess = true;
        } catch (IOException e) {
			Dlog.e(TAG, e);
            isSucess = false;
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
                if (fis != null)
					fis.close();
            } catch (IOException e) {
				Dlog.e(TAG, e);
                isSucess = false;
            }
        }
        return isSucess;
    }

	/***
	 * 토큰을 발급받는 화면..
	 * 
	 * @param address
	 * @param token
	 * @param client_id
	 * @param client_secret
	 * @param redirect_uri
	 * @param grant_type
	 * @return
	 */
	static public HashMap<String, String> getToken(String address, String token, String client_id, String client_secret, String redirect_uri, String grant_type) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		InputStream is = null;
		JSONObject jObj = null;
		String json = "";
		HashMap<String, String> result = null;

		try {

			// DefaultHttpClient
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(address);

			params.add(new BasicNameValuePair("code", token));
			params.add(new BasicNameValuePair("client_id", client_id));
			params.add(new BasicNameValuePair("client_secret", client_secret));
			params.add(new BasicNameValuePair("redirect_uri", redirect_uri));
			params.add(new BasicNameValuePair("grant_type", grant_type));

			httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
			httpPost.setEntity(new UrlEncodedFormEntity(params));
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();

			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();

			json = sb.toString();
			jObj = new JSONObject(json);
			String access_token = jObj.getString("access_token");
			String expires_in = jObj.getString("expires_in");
			String refresh_token = null;
			if (jObj.has("refresh_token"))
				refresh_token = jObj.getString("refresh_token");
			result = new HashMap<String, String>();

			if (access_token != null)
				result.put("access_token", access_token);
			if (expires_in != null)
				result.put("expires_in", expires_in);
			if (refresh_token != null)
				result.put("refresh_token", refresh_token);

		} catch (JSONException e) {
			Dlog.e(TAG, e);
		} catch (UnsupportedEncodingException e) {
			Dlog.e(TAG, e);
		} catch (ClientProtocolException e) {
			Dlog.e(TAG, e);
		} catch (IOException e) {
			Dlog.e(TAG, e);
		}

		return result;
	}

	public static String readStringFromHttpEntity(HttpEntity httpEntity) {
		InputStream is = null;
		StringBuilder sb = new StringBuilder();
		try {
			is = httpEntity.getContent();

			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
		} catch (IOException e) {
			Dlog.e(TAG, e);
		}
		return sb.toString();
	}
	
	static public HashMap<String, String> getTokenRefresh(String address, String refreshToken, String client_id, String client_secret) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		InputStream is = null;
		JSONObject jObj = null;
		String json = "";
		HashMap<String, String> result = null;

		try {

			// DefaultHttpClient
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(address);

			params.add(new BasicNameValuePair("refresh_token", refreshToken));
			params.add(new BasicNameValuePair("client_id", client_id));
			params.add(new BasicNameValuePair("client_secret", client_secret));
			params.add(new BasicNameValuePair("grant_type", "refresh_token"));

			httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
			httpPost.setEntity(new UrlEncodedFormEntity(params));
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();

			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();

			json = sb.toString();
			jObj = new JSONObject(json);
			String access_token = jObj.getString("access_token");
			String expires_in = jObj.getString("expires_in");
			String refresh_token = null;
			if (jObj.has("refresh_token"))
				refresh_token = jObj.getString("refresh_token");
			result = new HashMap<String, String>();

			if (access_token != null)
				result.put("access_token", access_token);
			if (expires_in != null)
				result.put("expires_in", expires_in);
			if (refresh_token != null)
				result.put("refresh_token", refresh_token);

		} catch (JSONException e) {
			Dlog.e(TAG, e);
		} catch (UnsupportedEncodingException e) {
			Dlog.e(TAG, e);
		} catch (ClientProtocolException e) {
			Dlog.e(TAG, e);
		} catch (IOException e) {
			Dlog.e(TAG, e);
		}

		return result;
	}

	public static boolean makeDiaryTemplateFile(Context context, String templateUrl, String outputPath) {
		if(context == null) return false;

		File targetDir = new File(outputPath).getParentFile();
		if (!targetDir.exists()) {
			targetDir.mkdirs();
		}

		HttpURLConnection urlConnection = null;
		BufferedReader urlReader = null;

		AssetManager assetManager = context.getAssets();
		BufferedOutputStream out = null;
		BufferedReader assetReader = null;
		InputStream ims = null;

		boolean isSucess = false;

		FlushedInputStream fis = null;
		try {
			ims = assetManager.open(Config.DIARY_BASE_TEMPLATE_FILE_NAME);
			assetReader = new BufferedReader(new InputStreamReader(ims, "UTF-8"));
			out = new BufferedOutputStream(new FileOutputStream(outputPath), IO_BUFFER_SIZE);
			String str;

			final URL url = new URL(templateUrl);
			urlConnection = (HttpURLConnection) url.openConnection();
			fis = new FlushedInputStream(urlConnection.getInputStream());
			urlReader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));

			StringBuilder baseTemplateBuilder = new StringBuilder();
			StringBuilder imageLayerBuilder = new StringBuilder();
			while ((str = assetReader.readLine()) != null) {
				baseTemplateBuilder.append(str);
			}

			boolean isStart = false;
			while ((str = urlReader.readLine()) != null) {
				if(str.trim().startsWith("<layer"))
					isStart = true;

				if(!isStart) continue;
				imageLayerBuilder.append(str);
				if(str.trim().startsWith("</layer>")) break;
			}

			String result = baseTemplateBuilder.toString();
			result = result.replace("INPUT_HERE", imageLayerBuilder.toString());

			out.write(result.getBytes(Charset.forName("utf-8")));
			isSucess = true;
		} catch (IOException e) {
			Dlog.e(TAG, e);
			isSucess = false;
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}

				if (urlReader != null)
					urlReader.close();

				if (ims != null)
					ims.close();

				if (assetReader != null)
					assetReader.close();

				if (out != null) {
					out.flush();
					out.close();
				}
			} catch (IOException e) {
				Dlog.e(TAG, e);
			}

			if (urlConnection != null)
				urlConnection.disconnect();
		}
		return isSucess;
	}


	/***
	 * 이미지 크기를 구하는 함수..
	 *
	 * @param imageUrl
	 * @return
	 */
	public static Rect getNetworkImageRect(String imageUrl) {
		if (Looper.myLooper() == Looper.getMainLooper()) {
			try {
				return getNetworkImageRectOnNetworkSync(imageUrl);
			} catch (Exception e) {
				Dlog.e(TAG, e);
			}
			return new Rect(0, 0, 0, 0);
		}

		FlushedInputStream fis = null;
		Rect imgRect = new Rect();
		HttpURLConnection conn = null;
		try {
			URL url = new URL(imageUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.connect();
			fis = new FlushedInputStream(conn.getInputStream());
			final BitmapFactory.Options option = new BitmapFactory.Options();
			option.inJustDecodeBounds = true;
			option.inDither = false;

			BitmapFactory.decodeStream(fis, new Rect(), option);

			imgRect.set(0, 0, option.outWidth, option.outHeight);

		} catch (Exception e) {
			Dlog.e(TAG, e);
		} finally {
			try {
				if(conn != null)
					conn.disconnect();

				if (fis != null)
					fis.close();
			} catch (IOException e) {
				Dlog.e(TAG, e);
			}
		}

		return imgRect;
	}

	public static Rect getNetworkImageRectOnNetworkSync(String imageUrl) throws Exception {
		SnapsImageDimensionMeasurer.NetworkImageDimensionMeasurer networkImageDimensionMeasurer = SnapsImageDimensionMeasurer.getNetworkImageDimensionMeasurer();
		Rect imageRect = new Rect();
		if (networkImageDimensionMeasurer != null
				&& networkImageDimensionMeasurer.getStatus() == AsyncTask.Status.RUNNING) {
			while (networkImageDimensionMeasurer.isDownloading()) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					Dlog.e(TAG, e);
				}
			}
		}

		networkImageDimensionMeasurer = SnapsImageDimensionMeasurer.createNetworkImageDimensionMeasurer(imageUrl, imageRect);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) { // 여러곳에서 호출하는 case가 있는가 보다. log에 "Cannot execute task: the task is already running." error 가 있어, 시작하기 전에 실행중인 download가 있으면 대기하도록 수정.
			networkImageDimensionMeasurer.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			networkImageDimensionMeasurer.execute();
		}

		while (networkImageDimensionMeasurer.isDownloading()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				Dlog.e(TAG, e);
			}
		}

		return imageRect;
	}
}
