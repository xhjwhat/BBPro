package com.bbpro.app.net;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
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

import com.bbpro.app.R;
import com.bbpro.app.net.HttpRequest.HttpCallBack;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class UploadTask extends AsyncTask<String, Integer, String> {
	private static int NET_CONNECTION_TIMEOUT = 2000;
	private static int NET_READDATA_TIMEOUT = 4000;
	private static HttpClient httpc = null;
	private static HttpClient httpsc = null;

	InputStream is = null;
	String result = "";
	private int flag = 0;
	private HttpCallBack callBack;
	public Context context;
	private ProgressDialog dialog;
	String url;

	public UploadTask(Context context, HttpCallBack callBack) {
		this.callBack = callBack;
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		if (context != null) {
			try {
				dialog = new ProgressDialog(context);
				dialog.setMessage(context.getString(R.string.info_upload));
				dialog.show();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		super.onPreExecute();
	}

	@Override
	protected String doInBackground(String... params) {
		try {
			HttpClient httpclient = null;
			HttpPost httppost = null;
			url = Constants.TEST_BBPRO_URL;
			MultipartEntity mulEntity = new MultipartEntity();
			httpclient = getClient(url);
			httppost = new HttpPost(url);
			mulEntity.addPart("file", new File(params[0]), true);
			httppost.setEntity(mulEntity);
			HttpResponse response;
			if (null != httpclient) {// clear null pointer
				response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
				is = entity.getContent();
			}
		} catch (IOException e) {
			flag = -1;
			e.printStackTrace();
			// return "connection error";
		}
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "UTF-8"));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			is.close();
			Log.e("=====", sb.toString());
			result = sb.toString();
		} catch (Exception e) {
			flag = -2;
			// return e.toString();
		}
		flag = 0;
		if (context == null) {
			if (callBack != null) {
				if (flag == 0) {
					callBack.success(result);
				} else {
					callBack.fail("connection error");
				}
			}
		}
		return result;
	}

	@Override
	protected void onPostExecute(String r) {
		if (context == null)
			return;
		if (callBack != null) {
			if (flag == 0) {
				callBack.success(r);
			} else {
				callBack.fail(r);
			}
		}
		if (dialog != null) {
			dialog.dismiss();
		}
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		if (dialog != null) {
			dialog.dismiss();
		}
	}

	@SuppressLint("NewApi")
	@Override
	protected void onCancelled(String result) {
		super.onCancelled(result);
		if (dialog != null) {
			dialog.dismiss();
		}
	}

	private static HttpClient getClient(String url) {
		HttpClient client = null;
		if (url.contains("https://")) {
			client = httpsc;
		} else {
			client = httpc;
		}
		// if null ,create
		if (client == null) {
			if (url.contains("https://")) {
				HttpParams param = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(param,
						NET_CONNECTION_TIMEOUT);// connecttimeout
				HttpConnectionParams.setSoTimeout(param, NET_READDATA_TIMEOUT);// read
																				// data
																				// timeout
				SchemeRegistry schemeRegistry = new SchemeRegistry();
				// schemeRegistry.register(new Scheme("https", new
				// EasySSLSocketFactory(), 443));
				ClientConnectionManager connManager = new ThreadSafeClientConnManager(
						param, schemeRegistry);
				httpsc = new DefaultHttpClient(connManager, param);
				client = httpsc;
			} else {
				HttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams,
						NET_CONNECTION_TIMEOUT);
				HttpConnectionParams.setSoTimeout(httpParams,
						NET_READDATA_TIMEOUT);
				SchemeRegistry sr = new SchemeRegistry();
				sr.register(new Scheme("http", PlainSocketFactory
						.getSocketFactory(), 80));
				ClientConnectionManager connManager = new ThreadSafeClientConnManager(
						httpParams, sr);
				httpc = new DefaultHttpClient(connManager, httpParams);
				client = httpc;
			}
		}
		return client;
	}

}
