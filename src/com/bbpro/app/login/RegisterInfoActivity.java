package com.bbpro.app.login;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Picture;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
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
import android.widget.Toast;

import com.bbpro.app.BBProApp;
import com.bbpro.app.R;
import com.bbpro.app.net.Constants;
import com.bbpro.app.net.HttpRequest;
import com.bbpro.app.net.HttpRequest.HttpCallBack;
import com.bbpro.app.net.UploadTask;
import com.bbpro.app.person.PersonCenterActivity;
import com.bbpro.app.util.BitMapUtil;
import com.bbpro.app.util.DESCrypto;
import com.bbpro.app.util.GetPicture;

public class RegisterInfoActivity extends Activity implements OnClickListener {
	public static final int IMAGE_REQUEST_CODE = 0;
	public static final int CAMERA_REQUEST_CODE = 1;
	public static final int RESULT_REQUEST_CODE = 2;
	public static final int SELECT_PIC_KITKAT = 4;
	public EditText nickName, password, question;
	public Button nextBtn;
	public ImageView headImg;
	public Dialog dialog;
	public GetPicture picture;
	public String tempFile;
	public String logoLocalPath;

	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.register_info);
		initView();
		logoLocalPath = Constants.BBPRO_FILE;
		picture = new GetPicture(this);
	}

	public void initView() {
		nickName = (EditText) findViewById(R.id.nick_name_edit);
		password = (EditText) findViewById(R.id.password_edit);
		question = (EditText) findViewById(R.id.issue_edit);
		headImg = (ImageView) findViewById(R.id.head_img);
		headImg.setOnClickListener(this);
		nextBtn = (Button) findViewById(R.id.next_btn);
		nextBtn.setOnClickListener(this);
	}

	public void showChoosePicDialog() {
		if (dialog != null) {
			dialog.show();
			return;
		}
		dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		Window window = dialog.getWindow();
		window.setGravity(Gravity.BOTTOM);
		View view = LayoutInflater.from(this).inflate(
				R.layout.choose_pic_dialog, null);
		TextView takePhotoText = (TextView) view.findViewById(R.id.take_photo);
		takePhotoText.setOnClickListener(this);
		TextView localPhotoText = (TextView) view
				.findViewById(R.id.local_photo);
		localPhotoText.setOnClickListener(this);
		TextView cancelText = (TextView) view.findViewById(R.id.cancel_text);
		cancelText.setOnClickListener(this);
		dialog.setContentView(view);
		dialog.show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.take_photo:
			tempFile = logoLocalPath + System.currentTimeMillis() + "temp.jpg";
			picture.choosePicFromCarma(tempFile);
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
			break;
		case R.id.local_photo:
			tempFile = logoLocalPath + System.currentTimeMillis() + "temp.jpg";
			picture.choosePicFromLocal();
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
			break;
		case R.id.cancel_text:
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
			break;
		case R.id.next_btn:
			updateInfo();
			break;
		case R.id.head_img:
			showChoosePicDialog();
			break;
		}

	}

	public void updateInfo() {
		
		try {
			HttpRequest request = new HttpRequest();
			JSONObject json = new JSONObject();
			json.put("vi", Constants.VI);
			json.put("bt", "1");
			json.put("cmd", "5");
			json.put("cname", nickName.getText().toString().trim());
			json.put("question", question.getText().toString().trim());
			json.put("bbnum", "");
			json.put("phone", BBProApp.getInstance().share.getString("phone", ""));
			json.put("pwd", DESCrypto.GetMD5Code(password.getText().toString().trim()));
			request.request(json.toString(), callback);
			
			UploadTask task = new UploadTask(this, imgCallback);
			task.execute(tempFile);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public HttpCallBack imgCallback = new HttpCallBack() {
		
		@Override
		public void success(String json) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void fail(String failReason) {
			// TODO Auto-generated method stub
			
		}
	};
	public HttpCallBack callback = new HttpCallBack() {
		@Override
		public void success(String json) {
			try {
				JSONObject object = new JSONObject(json);
				if(HttpRequest.REQUEST_RET_SUCCESS.equals(object.getString("ret"))){
					Intent intent = new Intent(RegisterInfoActivity.this,PersonCenterActivity.class);
					startActivity(intent);
				}else{
					Toast.makeText(RegisterInfoActivity.this, object.getString("errmsg"), Toast.LENGTH_SHORT).show();
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void fail(String failReason) {
			Toast.makeText(RegisterInfoActivity.this, failReason, Toast.LENGTH_SHORT).show();
		}
	};
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_CANCELED) {
			switch (requestCode) {
			case IMAGE_REQUEST_CODE:
				if (data == null)
					return;
				picture.startCropPhoto(data.getData());
				break;
			case SELECT_PIC_KITKAT:
				if (data == null)
					return;
				picture.startCropPhoto(data.getData());
				break;

			case CAMERA_REQUEST_CODE:
				File temp = new File(tempFile);
				picture.startCropPhoto(Uri.fromFile(temp));
				break;

			case RESULT_REQUEST_CODE:
				if (data != null) {
					getImageToView(data);
				}
				break;
			}
		}

	}
	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	private void getImageToView(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			BitMapUtil.saveBitmap2fileForSetting(photo, tempFile);
			headImg.setImageDrawable(new BitmapDrawable(photo));
		}
	}
}
