package com.bbpro.app.login;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bbpro.app.R;

public class RegisterInfoActivity extends Activity implements OnClickListener{
	public EditText nickName,password,question;
	public Button nextBtn;
	public ImageView headImg;
	public Dialog dialog;
	public void onCreate(Bundle bundle){
		super.onCreate(bundle);
		setContentView(R.layout.register_info);
		initView();
	}
	public void initView(){
		nickName = (EditText) findViewById(R.id.nick_name_edit);
		password = (EditText) findViewById(R.id.password_edit);
		question = (EditText) findViewById(R.id.issue_edit);
		headImg = (ImageView) findViewById(R.id.head_img);
		nextBtn = (Button) findViewById(R.id.next_btn);
		nextBtn.setOnClickListener(this);
	}
	
	
	public void showChoosePicDialog() {
		dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		Window window = dialog.getWindow();
		window.setGravity(Gravity.BOTTOM);
		View view = LayoutInflater.from(this).inflate(R.layout.choose_pic_dialog, null);
		TextView takePhotoText = (TextView) view.findViewById(R.id.take_photo);
		takePhotoText.setOnClickListener(this);
		TextView localPhotoText = (TextView) view.findViewById(R.id.local_photo);
		localPhotoText.setOnClickListener(this);
		TextView cancelText = (TextView) view.findViewById(R.id.cancel_text);
		cancelText.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.take_photo:
			
			break;
		case R.id.local_photo:
			break;
		case R.id.cancel_text:
			break;
		case R.id.next_btn:
			break;
		}
		
	}
	
}
