package com.bbpro.app.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import com.bbpro.app.BBProApp;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

public class HttpRequest {
	public static final int REQUEST_POST = 1;
	public static final int REQUEST_GET = 0;

	public int requestType = REQUEST_POST;
	public static final String REQUEST_RET_SUCCESS = "0";
	

	public SharedPreferences preferences;
	public String json;
	public String url = Constants.TEST_BBPRO_URL;

	public interface HttpCallBack {
		public void success(String json);

		public void fail(String failReason);
	}

	public HttpRequest(){
		
	}
	public HttpRequest(String json) {
		this.json = json;
	}

	

	public void request(String json, HttpCallBack callback) {
		RequestTask task = new RequestTask(json, callback);
		task.executeOnExecutor(BBProApp.getInstance().threadPool, "");
	}

	public void request(HttpCallBack callback) {
		RequestTask task = new RequestTask(this.json, callback);
		task.executeOnExecutor(BBProApp.getInstance().threadPool, "");
	}

	public class RequestTask extends AsyncTask<String, Integer, String> {
		private String json;
		private HttpCallBack callback;

		public RequestTask(String json, HttpCallBack callback) {
			this.json = json;
			this.callback = callback;
		}

		@Override
		protected String doInBackground(String... params) {
			InputStream is = null;
			String result = "";
			try {
//				HttpParams httpParams = new BasicHttpParams();
//				HttpConnectionParams.setSoTimeout(httpParams, 10000);
//				HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
//				HttpClient httpclient = new DefaultHttpClient(httpParams);
				 HttpParams httpParams = new BasicHttpParams();
			        HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
			        HttpConnectionParams.setSoTimeout(httpParams, 10000);
			        SchemeRegistry sr = new SchemeRegistry();
			        sr.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			        ClientConnectionManager connManager = new ThreadSafeClientConnManager(httpParams, sr);
			        HttpClient httpclient = new DefaultHttpClient(connManager, httpParams);
				Log.e("url", url);
				HttpResponse response;
				if (requestType == REQUEST_GET) {
					HttpGet httpget = new HttpGet(url);
					response = httpclient.execute(httpget);

				} else {
					HttpPost httppost = new HttpPost(url);
					StringEntity httpbody = new StringEntity(json,HTTP.UTF_8);
					httppost.setEntity(httpbody);
					httppost.setHeader("Content-Type", "application/json");
					response = httpclient.execute(httppost);

				}
				HttpEntity entity = response.getEntity();
				is = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is, "UTF-8"));
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}
				is.close();
				result = sb.toString();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				Log.e("What", result);
				JSONObject jsonObject = new JSONObject(result);
				if (!jsonObject.isNull("ret")) {
					if (jsonObject.optString("ret").equals("0")) {
						callback.success(result);
					} else {
						callback.fail(jsonObject.optString("errmsg"));
					}
				}
				return;
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			callback.fail("网络异常，请稍后再试");
		}

	}

	public int getRequestType() {
		return requestType;
	}

	public void setRequestType(int requestType) {
		this.requestType = requestType;
	}

	public void setUrl(String url){
		this.url = url;
	}
	
}
