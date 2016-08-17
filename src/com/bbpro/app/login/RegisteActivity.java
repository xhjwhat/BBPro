package com.bbpro.app.login;

import com.bbpro.app.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class RegisteActivity extends FragmentActivity {
	public final int PHONE_FRAGMENT = 1;
	public final int VER_FRAGMENT = 2;
	public Fragment phoneFragment,verFragment;
	public FragmentManager manager;
	public FragmentTransaction transaction;
	public ImageView backImg;
	public int currentFragment = PHONE_FRAGMENT;
	public void onCreate(Bundle bundle){
		super.onCreate(bundle);
		setContentView(R.layout.register_fragment);
		backImg = (ImageView)findViewById(R.id.back_img);
		backImg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(currentFragment == PHONE_FRAGMENT){
					finish();
				}else{
					transaction.remove(verFragment);
				}
			}
		});
		phoneFragment = new PhoneResterFragment();
		manager = getSupportFragmentManager();
		transaction = manager.beginTransaction();
		transaction.add(R.id.register_frame, phoneFragment , "phone_register_fragment");
		transaction.commit();
	}
	
	public void toVerificationFragment(String phone){
		verFragment = new VerifiactionFragment();
		Bundle bundle = new Bundle();
		bundle.putString("phone", phone);
		verFragment.setArguments(bundle);
		transaction = manager.beginTransaction();
		transaction.replace(R.id.register_frame, verFragment , "phone_register_fragment");
		transaction.addToBackStack("");
		transaction.commit();
	}
}
