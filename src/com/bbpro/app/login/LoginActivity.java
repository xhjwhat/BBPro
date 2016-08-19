package com.bbpro.app.login;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bbpro.app.BBProApp;
import com.bbpro.app.R;
import com.bbpro.app.cache.ModelCache;
import com.bbpro.app.entity.AccountInfo;
import com.bbpro.app.net.Constants;
import com.bbpro.app.net.HttpRequest;
import com.bbpro.app.net.HttpRequest.HttpCallBack;
import com.bbpro.app.util.DESCrypto;
import com.bbpro.app.util.StringUtil;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendAuth;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class LoginActivity extends Activity implements OnClickListener{
	public EditText phoneEdit,passwordEdit;
	public Button loginBtn;
	public RelativeLayout weixinLayout;
	public TextView forgetText;
	public String phone;
	public String password;
	public IWXAPI mWeixinAPI;
	public void onCreate(Bundle bundle){
		super.onCreate(bundle);
		setContentView(R.layout.login);
		initView();
		initWeixin();
	}
	public void initView(){
		loginBtn = (Button) findViewById(R.id.login_btn);
		loginBtn.setOnClickListener(this);
		weixinLayout = (RelativeLayout) findViewById(R.id.weixin_fast_login);
		weixinLayout.setOnClickListener(this);
		phoneEdit = (EditText) findViewById(R.id.phone_num_edit);
		passwordEdit = (EditText) findViewById(R.id.password_edit);
		forgetText = (TextView) findViewById(R.id.forget_password_text);
		forgetText.setOnClickListener(this);
	}
	// 微信登录初始化
		private void initWeixin() {
			if (null == mWeixinAPI) {
				mWeixinAPI = WXAPIFactory.createWXAPI(this,
						Constants.WEIXIN_KEY);
			}
		}
	public void onClick(View v){
		switch(v.getId()){
		case R.id.login_btn:
			phone = phoneEdit.getText().toString().trim();
			password = passwordEdit.getText().toString().trim();
			login(phone,password);
			break;
		case R.id.weixin_fast_login:
			break;
		case R.id.forget_password_text:
			
			break;
		}
	}
	private void login(String account, String passWord) {

		// 验证用户名必须是手机号
		if (StringUtil.isMobileNum(account)) {
			// 需要验证
			if (account.length() == 0 || passWord.length() == 0) {
				Toast.makeText(this, R.string.account_password_not_null, Toast.LENGTH_SHORT).show();
				return;
			}
		} else {
			Toast.makeText(this, R.string.illegal_mobile_num, Toast.LENGTH_SHORT).show();
			return;
		}
		if (!StringUtil.isStandar(passWord)) {
			Toast.makeText(this, R.string.password_alert, Toast.LENGTH_SHORT).show();
			return;
		}
		try {
			HttpRequest request = new HttpRequest();
			JSONObject json = new JSONObject();
			json.put("bt", "1");
			json.put("cmd", "2");
			json.put("phone", account);
			json.put("pwd", DESCrypto.GetMD5Code(passWord));
			json.put("vi",Constants.VI);
			request.request(json.toString(), loginCallback);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	// 微信登录
//		private void loginWithWeixin() {
//			if (!mWeixinAPI.isWXAppInstalled()) {
//				mContractView.showToastStatus(R.string.wx_no_install);
//				return;
//			}
//			boolean isSuccess = mWeixinAPI.registerApp(OtherLoginKey.WX_APP_ID);
//			if (!MPREpubReaderTool.checkNetState(mContext)) {
//				mContractView.showToastStatus(R.string.net_exception);
//				return;
//			}
//			if (isSuccess) {
//				final SendAuth.Req req = new SendAuth.Req();
//				req.scope = OtherLoginKey.SNA_USER;
//				req.state = OtherLoginKey.WX_STATE;
//				boolean bool = mWeixinAPI.sendReq(req);
//				if (bool) {
//					mContractView.showToastStatus(R.string.wx_login);
//				}
//			}
//
//		}
	HttpCallBack loginCallback = new HttpCallBack() {
		@Override
		public void success(String json) {
			try {
				JSONObject object = new JSONObject(json);
				if(HttpRequest.REQUEST_RET_SUCCESS.equals(object.get("ret"))){
					AccountInfo info = new AccountInfo();
					JSONObject account = object.getJSONObject("accinfo");
					info.setBbNum(account.getString("bbnum"));
					info.setcName(account.getString("cname"));
					info.setHdUrl(account.getString("hdurl"));
					info.setPassword(account.getString("password"));
					info.setPhone(account.getString("phone"));
					info.setQuestion(account.getString("question"));
					ModelCache modelCache = new ModelCache(LoginActivity.this, "BBPro");
					info.setSerializableId("account01");
					info.save(modelCache);
					SharedPreferences share = BBProApp.getInstance().share;
					Editor editor = share.edit();
					editor.putString("phone", account.getString("phone"));
					editor.putString("password", account.getString("password"));
					editor.commit();
				}else{
					Toast.makeText(LoginActivity.this, object.getString("errmsg"), Toast.LENGTH_SHORT).show();
				}
				
			} catch (JSONException e) {
				Toast.makeText(LoginActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
			
			
//			Intent intent = new Intent(LoginActivity.this,MainActivity.class);
//			startActivity(intent);
//			finish();
		}
		@Override
		public void fail(String failReason) {
			Toast.makeText(LoginActivity.this, failReason, Toast.LENGTH_SHORT).show();
		}
	};
}
