package com.bbpro.app.login;

import com.bbpro.app.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ForgetPwdActivity extends Activity {
	public EditText phoneEdit;
	public Button nextBtn;
	public void onCreate(Bundle bundle){
		super.onCreate(bundle);
		setContentView(R.layout.forget_pwd_phone);
		nextBtn = (Button) findViewById(R.id.next_btn);
		nextBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
		phoneEdit = (EditText) findViewById(R.id.phone_num_edit);
	}
}
