package com.bbpro.app.login;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bbpro.app.R;
import com.bbpro.app.net.Constants;
import com.bbpro.app.net.HttpRequest;
import com.bbpro.app.net.HttpRequest.HttpCallBack;

public class VerifiactionFragment extends Fragment{
	public TextView phoneText;
	public EditText verificationEdit;
	public Button nextBtn;
	public int count = 60;
	public Timer timer;
	public String phone;
	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container,  Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.verification_fragment, null);
		phoneText = (TextView) view.findViewById(R.id.phone_num_text);
		verificationEdit = (EditText) view.findViewById(R.id.verifcation_edit);
		verificationEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(verificationEdit.getText().toString().trim().length()>0){
					nextBtn.setText(R.string.next_stap);
					nextBtn.setClickable(true);
				}else{
					
				}
			}
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			public void afterTextChanged(Editable s) {
			}
		});
		nextBtn = (Button) view.findViewById(R.id.next_btn);
		nextBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(nextBtn.getText().toString().equals(getString(R.string.next_stap))){
					register();
				}else{
					resendVerification();
				}
			}
		});
		return view;
	}
	@Override
	public void onActivityCreated( Bundle savedInstanceState) {
		Bundle bundle = getArguments();
		phone = bundle.getString("phone");
		phoneText.setText(phone);
		timer = new Timer();
		timer.schedule(task, 0, 1000);
		super.onActivityCreated(savedInstanceState);
	}
	TimerTask task = new TimerTask() {
		@Override
		public void run() {
			if(count == 0){
				nextBtn.setClickable(true);
				count = 60;
				nextBtn.post(new Runnable(){
					public void run(){
						nextBtn.setText(R.string.resend_verification);
					}
				});
				
			}else{
				nextBtn.setClickable(false);
				nextBtn.post(new Runnable(){
					public void run(){
						nextBtn.setText(getString(R.string.resend_verification2, count));
					}
				});
				
				count--;
			}
		}
	};
	public void register(){
		try {
			HttpRequest request = new HttpRequest();
			JSONObject json = new JSONObject();
			json.put("bt", "1");
			json.put("cmd", "3");
			json.put("phone", phone);
			json.put("cc", verificationEdit.getText().toString().trim());
			request.request(json.toString(), callback);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void resendVerification(){
		try {
			HttpRequest request = new HttpRequest();
			JSONObject json = new JSONObject();
			json.put("vi", Constants.VI);
			json.put("bt", "1");
			json.put("cmd", "1");
			json.put("phone", phone);
			request.request(json.toString(), verificationCallBack);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	
	HttpCallBack callback = new HttpCallBack() {
		
		@Override
		public void success(String json) {
			try {
				JSONObject object = new JSONObject(json);
				if(HttpRequest.REQUEST_RET_SUCCESS.equals(object.get("ret"))){
					Intent intent = new Intent(getActivity(),RegisterInfoActivity.class);
					startActivity(intent);
				}else{
					Toast.makeText(getActivity(), object.getString("errmsg"), Toast.LENGTH_SHORT).show();
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		@Override
		public void fail(String failReason) {
			Toast.makeText(getActivity(), failReason, Toast.LENGTH_SHORT).show();
		}
	};
	HttpCallBack verificationCallBack = new HttpCallBack() {
		@Override
		public void success(String json) {
			try {
				JSONObject object = new JSONObject(json);
				if(HttpRequest.REQUEST_RET_SUCCESS.equals(object.getString("ret"))){
					timer = new Timer();
					timer.schedule(task, 0, 1000);
				}
				Toast.makeText(getActivity(), object.getString("errmsg"), Toast.LENGTH_SHORT).show();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void fail(String failReason) {
			Toast.makeText(getActivity(), failReason, Toast.LENGTH_SHORT).show();
		}
	};
	
}
