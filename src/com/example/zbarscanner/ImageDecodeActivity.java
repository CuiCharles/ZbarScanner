package com.example.zbarscanner;

import java.lang.ref.WeakReference;

import com.qdtevc.zbar.core.Result;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 *  选择图片解码activity
 * @author charles
 *
 */
public class ImageDecodeActivity extends Activity {
	
	private static final int PICK_IMAGE_FROM_ALBUM_CODE = 0X0023;
	private String photo_path;
	private TextView text_result;
	ProgressDialog progressDialog;

	
	private DataProcessHanlder handler  = new DataProcessHanlder(this);

	private static class DataProcessHanlder extends Handler {
		
		private final WeakReference<ImageDecodeActivity> mActivity;

		public DataProcessHanlder(ImageDecodeActivity activity) {
			mActivity = new WeakReference<ImageDecodeActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			ImageDecodeActivity activity = mActivity.get();
			if (activity != null) {
				activity.progressDialog.dismiss();
				switch (msg.what) {
				case 0:
					activity.text_result.setTextColor(Color.RED);
					activity.text_result.setText("格式错误");
					break;
				case 1:
					Result rawResult = (Result)msg.obj;
					if(rawResult != null)
					{
						activity.text_result.setTextColor(Color.BLACK);
						activity.text_result.setText(rawResult.getContents());
					}
					else
					{
						activity.text_result.setTextColor(Color.RED);
						activity.text_result.setText("内容为空");
					}
					break;
				case 2:
					activity.text_result.setTextColor(Color.RED);
					activity.text_result.setText("图片路径为空");
					break;
				}
			}
		}
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_full_image);
		text_result = (TextView)findViewById(R.id.text_result);
	}

	
	public void PickupIamge(View v)
	{
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		startActivityForResult(intent, PICK_IMAGE_FROM_ALBUM_CODE);
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case PICK_IMAGE_FROM_ALBUM_CODE:
				String[] proj = { MediaStore.Images.Media.DATA };
				// 获取选中图片的路径
				Cursor cursor = getContentResolver().query(data.getData(),
						proj, null, null, null);
				if (cursor.moveToFirst()) {
					int column_index = cursor
							.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
					photo_path = cursor.getString(column_index);
					if (photo_path == null) {
						photo_path = Utils.getPath(getApplicationContext(),data.getData());
					}
				}
				cursor.close();
				progressDialog = ProgressDialog.show(this, "解码中...", "请等待...", true, false);
				new DecodeThread(handler,photo_path).start();
				break;
			default:
				break;
				
			}
		}
	}
}
