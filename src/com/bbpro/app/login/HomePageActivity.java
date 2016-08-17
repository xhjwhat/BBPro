package com.bbpro.app.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.bbpro.app.R;

public class HomePageActivity extends Activity implements OnClickListener{
	public Button phoneBtn,weixinBtn;
	public TextView loginText;
	public void onCreate(Bundle bundle){
		super.onCreate(bundle);
		setContentView(R.layout.main_register);
		phoneBtn = (Button) findViewById(R.id.phone_register_btn);
		phoneBtn.setOnClickListener(this);
		weixinBtn = (Button) findViewById(R.id.weixin_register_btn);
		weixinBtn.setOnClickListener(this);
		loginText = (TextView) findViewById(R.id.login);
		loginText.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.phone_register_btn:
			Intent intent = new Intent(this,RegisteActivity.class);
			startActivity(intent);
			break;
		case R.id.weixin_register_btn:
			break;
		case R.id.login:
			Intent loginIntent = new Intent(this,LoginActivity.class);
			startActivity(loginIntent);
			break;
		}
		
	}
}
