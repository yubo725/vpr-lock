package com.vpr.vprlock.http;

import java.io.StringReader;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Xml;

import com.vpr.vprlock.utils.UITools;

/**
 * 封装网络请求和数据解析的抽象类
 * @author yubo
 *
 * @param <T>
 */
public abstract class BaseHttpManager<T> {
	private Context context;
	private FinalHttp finalHttp;
	private String urlStr;
	private OnRequestListener<T> listener;
	private XmlPullParser xmlPullParser;
	private DataType dataType;
	private boolean needLoadingDialog = true;
	private String loadingMsg = "";
	private ProgressDialog pd;

	public enum DataType {
		JSON, XML
	}

	public BaseHttpManager(Context context, String urlStr, DataType type) {
		this.context = context;
		this.urlStr = urlStr;
		this.dataType = type;
		finalHttp = new FinalHttp();
		finalHttp.configTimeout(10 * 1000);
		finalHttp.configCharset("UTF-8");
		xmlPullParser = Xml.newPullParser();
		pd = new ProgressDialog(context);
		pd.setTitle("提示");
	}

	public interface OnRequestListener<T> {
		void onSuccess(T t);
		void onFailure();
	}

	/**设置请求时是否需要显示加载对话框，默认为显示*/
	public void setNeedLoadingDialog(boolean b){
		this.needLoadingDialog = b;
	}

	/**设置进度框显示的提示文字*/
	public void setLoadingMsg(String msg){
		if(!TextUtils.isEmpty(msg)){
			loadingMsg = msg;
		}
	}

	public void setOnRequestListener(OnRequestListener<T> listener) {
		this.listener = listener;
	}

	/** 由子类复写的方法，用于解析从服务器获取的XML数据 */
	public abstract T parseXml(XmlPullParser xmlPullParser);

	/** 由子类复写的方法，用于解析从服务器获取的json数据 */
	public abstract T parseJson(String json);

	/**开始http请求, 参数params为null时是get请求, 不为null时是post请求*/
	public void startManager(AjaxParams params) {
		if (TextUtils.isEmpty(urlStr)) {
			throw new IllegalArgumentException("request url is null");
		} else if (listener == null) {
			throw new IllegalArgumentException("OnRequestListener is null");
		} else if(params == null){
			// 请求服务器获取返回的数据
			finalHttp.get(urlStr, ajaxCallBack);
		} else {
			finalHttp.post(urlStr, params, ajaxCallBack);
		}
	}

	private AjaxCallBack<String> ajaxCallBack = new AjaxCallBack<String>(){

		@Override
		public void onFailure(Throwable t, int errorNo, String strMsg) {
			super.onFailure(t, errorNo, strMsg);
			UITools.showToast(context, "请求服务器出错");
			listener.onFailure();
			if(needLoadingDialog){
				UITools.dismissLoadingDialog();
			}
		}

		@Override
		public void onStart() {
			super.onStart();
			if(needLoadingDialog){
				UITools.showLoadingDialog(context, loadingMsg);
			}
		}

		@Override
		public void onSuccess(String t) {
			super.onSuccess(t);
			if(needLoadingDialog){
				UITools.dismissLoadingDialog();
			}
			if (!TextUtils.isEmpty(t)) {
				// 如果返回的数据不为空，则解析数据
				if (dataType == DataType.XML) {
					try {
						xmlPullParser.setInput(new StringReader(t));
					} catch (XmlPullParserException e) {
						e.printStackTrace();
					}
					new ParseXmlTask(null).execute();
				}else if(dataType == DataType.JSON){
					new ParseXmlTask(t).execute();
				}
			} else {
				UITools.showToast(context, "服务器没有返回数据");
			}
		}

	};

	/** 解析XML或json数据的任务 */
	private class ParseXmlTask extends AsyncTask<Void, Void, T> {
		String data;

		public ParseXmlTask(String data){
			this.data = data;
		}

		@Override
		protected T doInBackground(Void... params) {
			try {
				if(this.data == null){
					return parseXml(xmlPullParser);
				}else{
					return parseJson(data);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(T result) {
			super.onPostExecute(result);
			if (result != null) {
				// 解析完成
				listener.onSuccess(result);
			} else {
				listener.onSuccess(null);
			}
		}

	}
}

