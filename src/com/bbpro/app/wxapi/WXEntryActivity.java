package com.bbpro.app.wxapi;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.bbpro.app.R;
import com.bbpro.app.net.Constants;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendAuth;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

	private IWXAPI api;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		api = WXAPIFactory.createWXAPI(this, Constants.WEIXIN_KEY);
		api.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);

	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		int result = 0;
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			result = R.string.errcode_success;
			SendAuth.Resp getres = (SendAuth.Resp) resp;
			loainQq(getres.token);
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			result = R.string.errcode_cancel;
			Toast.makeText(this, result, Toast.LENGTH_LONG).show();
			finish();
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			result = R.string.errcode_deny;
			Toast.makeText(this, result, Toast.LENGTH_LONG).show();
			finish();
			break;
		default:
			result = R.string.errcode_unknown;
			Toast.makeText(this, result, Toast.LENGTH_LONG).show();
			finish();
			break;
		}
	}

	// 微信点击登录
	private void loainQq(String code) {
		//LoginManager.doWeixinLogin(this, code, loginCallBackHandler_wx);
	}

	/**
	 * @param mLoginInfo
	 *            用于请求回调
	 */
	private Handler loginCallBackHandler_wx = new Handler() {

		public void handleMessage(Message msg) {
			Bundle bundle = msg.getData();
			switch (msg.what) {
//			case IHttpCallBack.HTTP_REQUEST_OK:
//				try {
//					if (bundle != null) {
//						String responseStr = (String) bundle.get("result");
//						JSONObject jsonObject = new JSONObject(responseStr);
//						if (!jsonObject.isNull("return_code")) {
//							String s = jsonObject.optString("return_code");
//							if ("1".equals(s)) {
//								Toast.makeText(WXEntryActivity.this, getString(R.string.usename_password_error), 1).show();
//							}
//							if ("2".equals(s)) {
//								Toast.makeText(WXEntryActivity.this, getString(R.string.usename_password_error), 1).show();
//							} else if ("0".equals(s)) {
//								JSONObject statusObj = new JSONObject(jsonObject.getString("status"));
//								String session = statusObj.getString("session");
//								String useid = statusObj.getString("user_id");
//								LoginManager.updateUserIdAndSession(session, useid);
//								LoginInfoEntity mLoginInfoEntitys = new LoginInfoEntity();
//								mLoginInfoEntitys.userName = useid;
//								mLoginInfoEntitys.userId = useid;
//								mLoginInfoEntitys.loginType = "1";
//								onLoginSuccess(mLoginInfoEntitys);
//							}
//						}
//					}
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//				finish();
//				break;
//			case IHttpCallBack.HTTP_REQUEST_FAIL:
//				Toast.makeText(WXEntryActivity.this, getString(R.string.net_exception), 1).show();
//				finish();
//				break;
//			case IHttpCallBack.HTTP_REQUEST_CANCAL:
//				finish();
//				break;
			}
		};
	};

	/**
	 * @param mLoginInfo
	 *            qq登录成功后然后跳转
	 */
	public void onLoginSuccess() {
	}
}