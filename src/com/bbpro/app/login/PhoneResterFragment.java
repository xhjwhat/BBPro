package com.bbpro.app.login;

import org.json.JSONException;
import org.json.JSONObject;

import com.bbpro.app.R;
import com.bbpro.app.net.Constants;
import com.bbpro.app.net.HttpRequest;
import com.bbpro.app.net.HttpRequest.HttpCallBack;
import com.bbpro.app.util.StringUtil;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PhoneResterFragment extends Fragment implements OnClickListener{
	public EditText phoneEdit;
	public TextView protatolText,privateText;
	public Button nextBtn;
	public Dialog dialog;
	@Override
	public View onCreateView(LayoutInflater inflater,
			 ViewGroup container,  Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.phone_register, null);
		
		protatolText = (TextView) view.findViewById(R.id.protatol);
		protatolText.setOnClickListener(this);
		privateText = (TextView) view.findViewById(R.id.private_);
		privateText.setOnClickListener(this);
		nextBtn = (Button) view.findViewById(R.id.next_btn);
		nextBtn.setOnClickListener(this);
		phoneEdit = (EditText) view.findViewById(R.id.phone_num_edit);
		phoneEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(phoneEdit.getText().toString().trim().length() == 11){
					nextBtn.setClickable(true);
				}else{
					nextBtn.setClickable(false);
				}
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		return view;
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.protatol:
			break;
		case R.id.private_:
			break;
		case R.id.next_btn:
			String phone = phoneEdit.getText().toString().trim();
			if (StringUtil.isMobileNum(phone)) {
				// 需要验证
				if (phone.length() == 0) {
					Toast.makeText(getActivity(), "手机号码为空", Toast.LENGTH_SHORT).show();
					return;
				}
			} else {
				Toast.makeText(getActivity(), "非法手机号码", Toast.LENGTH_SHORT).show();
				return;
			}
			initDialog();
			break;
		}
		
	}
	HttpCallBack callback = new HttpCallBack() {
		public void success(String json) {
			try {
				JSONObject object = new JSONObject(json);
				if(HttpRequest.REQUEST_RET_SUCCESS.equals(object.getString("ret"))){
					//下一步界面
					((RegisteActivity)getActivity()).toVerificationFragment(phoneEdit.getText().toString().trim());
				}
				Toast.makeText(getActivity(), object.getString("errmsg"), Toast.LENGTH_SHORT).show();
			} catch (JSONException e) {
				Toast.makeText(getActivity(), "系统处理异常", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
		}
		
		@Override
		public void fail(String failReason) {
			Toast.makeText(getActivity(), failReason, Toast.LENGTH_SHORT).show();
		}
	};
	public void initDialog(){
		dialog = new Dialog(getActivity());
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.verification_dialog, null);
		TextView phoneText = (TextView) view.findViewById(R.id.phone_text);
		phoneText.setText(phoneEdit.getText().toString().trim());
		TextView cancelText = (TextView) view.findViewById(R.id.cancel_text);
		cancelText.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(dialog.isShowing()){
					dialog.dismiss();
				}
			}
		});
		TextView sureText = (TextView) view.findViewById(R.id.sure_text);
		sureText.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					HttpRequest request = new HttpRequest();
					JSONObject json = new JSONObject();
					json.put("vi", Constants.VI);
					json.put("bt", "1");
					json.put("cmd", "1");
					json.put("phone", phoneEdit.getText().toString().trim());
					request.request(json.toString(), callback);
					if(dialog!=null && dialog.isShowing()){
						dialog.dismiss();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
		dialog.setContentView(view);
		dialog.show();
	}
}
