package com.bill.metrobus.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.bill.metrobus.R;

import android.R.interpolator;
import android.content.Context;
import android.os.Handler;

public class HttpClientHelper {
	private static HttpClient httpClient = null;
	public static final int LOGIN_SUCCESS = 1;
	public static final int LOGIN_FAILED = 0;
	public static final int NET_ERROR = -1;

	public static String doGet(String url, Map params) {
		/* 建立HTTPGet对象 */
		String paramStr = "";
		Iterator iter = params.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Object key = entry.getKey();
			Object val = entry.getValue();
			paramStr += paramStr = "&" + key + "=" + val;
		}
		if (!paramStr.equals("")) {
			paramStr = paramStr.replaceFirst("&", "?");
			url += paramStr;
		}
		HttpGet httpRequest = new HttpGet(url);
		String strResult = "doGetError";
		try {
			/* 发送请求并等待响应 */
			HttpResponse httpResponse = getHttpClient().execute(httpRequest);
			/* 若状态码为200 ok */
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				/* 读返回数据 */
				strResult = EntityUtils.toString(httpResponse.getEntity());
			} else {
				strResult = "Error Response: "
						+ httpResponse.getStatusLine().toString();
			}
		} catch (ClientProtocolException e) {
			strResult = e.getMessage().toString();
			e.printStackTrace();
		} catch (IOException e) {
			strResult = e.getMessage().toString();
			e.printStackTrace();
		} catch (Exception e) {
			strResult = e.getMessage().toString();
			e.printStackTrace();
		}
		return strResult;
	}

	public static String doPost(String url, List<NameValuePair> params) {
		/* 建立HTTPPost对象 */
		HttpPost httpRequest = new HttpPost(url);
		String strResult = "doPostError";
		HttpClient httpClient = getHttpClient();
		try {
			/* 添加请求参数到请求对象 */
			if (params != null)
				httpRequest.setEntity(new UrlEncodedFormEntity(params,
						HTTP.UTF_8));
			synchronized(httpClient){
				/* 发送请求并等待响应 */
				HttpResponse httpResponse = httpClient.execute(httpRequest);
				/* 若状态码为200 ok */
				if (httpResponse.getStatusLine().getStatusCode() == 200) {
					/* 读返回数据 */
					strResult = EntityUtils.toString(httpResponse.getEntity());
				} else {
	//				strResult = "Error Response: "
	//						+ httpResponse.getStatusLine().toString();
					strResult = NET_ERROR+"";
				}
			}
		} catch (ClientProtocolException e) {
			//strResult = e.getMessage().toString();
			strResult = NET_ERROR+"";
			e.printStackTrace();
		} catch (IOException e) {
			//strResult = e.getMessage().toString();
			strResult = NET_ERROR+"";
			e.printStackTrace();
		} catch (Exception e) {
			//strResult = e.getMessage().toString();
			strResult = NET_ERROR+"";
			e.printStackTrace();
		}
		return strResult;
	}

	private static HttpClient getHttpClient() {
		if (httpClient == null) {
			// 创建 HttpParams 以用来设置 HTTP 参数（这一部分不是必需的）
			HttpParams httpParams = new BasicHttpParams();
			// 设置连接超时和 Socket 超时，以及 Socket 缓存大小
			HttpConnectionParams.setConnectionTimeout(httpParams, 20 * 1000);
			HttpConnectionParams.setSoTimeout(httpParams, 20 * 1000);
			HttpConnectionParams.setSocketBufferSize(httpParams, 8192);
			// 设置重定向，缺省为 true
			HttpClientParams.setRedirecting(httpParams, true);
			// 设置 user agent
			String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2) Gecko/20100115 Firefox/3.6";
			HttpProtocolParams.setUserAgent(httpParams, userAgent);
			// 创建一个 HttpClient 实例
			httpClient = new DefaultHttpClient(httpParams);
		}
		return httpClient;
	}

	public static void login(String user, String password, Handler handler, Context context) {
		Thread thread = new Thread(new HttpClientLoginRunnable(user, password, handler, context));
		thread.start();
	}
	
	public static void sendMessage(String user, String content, Context context, MessageGettedLinstener messageGettedLinstener){
		Thread thread = new Thread(new HttpClientMessageRunnable(user, content, context, messageGettedLinstener));
		thread.start();
	}
	
	public static String blockSendMessage(String user, String content, Context context){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("user", user));
		params.add(new BasicNameValuePair("content", content));
		return HttpClientHelper.doPost(context.getResources().getString(R.string.url), params);
	}
	
	interface MessageGettedLinstener{
		public void messageGetted(String message);
	}
}


class HttpClientMessageRunnable implements Runnable{

	private String user = null;
	private String content = null;
	private Context context = null;
	
	private HttpClientHelper.MessageGettedLinstener messageGettedLinstener;
	
	public HttpClientMessageRunnable(String user, String content, Context context, HttpClientHelper.MessageGettedLinstener messageGettedLinstener){
		this.user = user;
		this.content = content;
		this.context = context;
		this.messageGettedLinstener = messageGettedLinstener;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("user", user));
		params.add(new BasicNameValuePair("content", content));
		String message = HttpClientHelper.doPost(context.getResources().getString(R.string.url), params);
		if(messageGettedLinstener!=null)
			messageGettedLinstener.messageGetted(message);
	}
}

class HttpClientLoginRunnable implements Runnable{

	private String user = null;
	private String password = null;
	private Handler handler = null;
	private Context context = null;
	
	public HttpClientLoginRunnable(String user, String password, Handler handler, Context context){
		this.user = user;
		this.password = password;
		this.handler = handler;
		this.context = context;
	}
	
	@Override
	public void run() {
		try {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("user", user));
			String sessionStr = HttpClientHelper.doPost(context.getResources().getString(R.string.url), params);
			if(sessionStr.equals("-1")){
				handler.obtainMessage(HttpClientHelper.NET_ERROR).sendToTarget();
				return;
			}
			JSONObject session = new JSONObject(sessionStr);
			/*
			String sessionId = session.getString("session");
			password = StringUtil.md5(StringUtil.md5(password)+sessionId);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("email", user));
			params.add(new BasicNameValuePair("password", password));
			String result = HttpClientHelper.doPost(LoginActivity.homeUrl
					+ LoginActivity.loginUrl, params);
			if(result.equals("0")){
				handler.obtainMessage(HttpClientHelper.LOGIN_FAILED).sendToTarget();
			}else if(result.equals("-1")){
				handler.obtainMessage(HttpClientHelper.NET_ERROR).sendToTarget();
			}else{
				try{
					JSONObject token = new JSONObject(result);
					handler.obtainMessage(HttpClientHelper.LOGIN_SUCCESS, token.get("token")).sendToTarget();
				}catch(Exception e){
					handler.obtainMessage(HttpClientHelper.LOGIN_FAILED).sendToTarget();
				}
			}
			*/
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
