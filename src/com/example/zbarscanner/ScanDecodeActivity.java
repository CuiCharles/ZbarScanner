package com.example.zbarscanner;

import java.util.ArrayList;
import java.util.List;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.qdtevc.zbar.core.BarcodeFormat;
import com.qdtevc.zbar.core.CameraSelectorDialogFragment;
import com.qdtevc.zbar.core.FormatSelectorDialogFragment;
import com.qdtevc.zbar.core.MessageDialogFragment;
import com.qdtevc.zbar.core.Result;
import com.qdtevc.zbar.core.ZBarScannerView;

/**
 * 扫描图片解码activity
 * 
 * @author charles
 * 
 */
public class ScanDecodeActivity extends ActionBarActivity implements
		MessageDialogFragment.MessageDialogListener,
		ZBarScannerView.ResultHandler,
		FormatSelectorDialogFragment.FormatSelectorDialogListener,
		CameraSelectorDialogFragment.CameraSelectorDialogListener {

	private static final String FLASH_STATE = "FLASH_STATE";
	private static final String AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE";
	private static final String SELECTED_FORMATS = "SELECTED_FORMATS";
	private static final String CAMERA_ID = "CAMERA_ID";
	private ZBarScannerView mScannerView;
	private boolean mFlash;
	private boolean mAutoFocus;
	private ArrayList<Integer> mSelectedIndices;
	private int mCameraId = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			mFlash = savedInstanceState.getBoolean(FLASH_STATE, false);
			mAutoFocus = savedInstanceState.getBoolean(AUTO_FOCUS_STATE, true);
			mSelectedIndices = savedInstanceState
					.getIntegerArrayList(SELECTED_FORMATS);
			mCameraId = savedInstanceState.getInt(CAMERA_ID, -1);
		} else {
			mFlash = false;
			mAutoFocus = true;
			mSelectedIndices = null;
			mCameraId = -1;
		}
		setContentView(R.layout.activity_full_scanner);

		ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
		mScannerView = new ZBarScannerView(this);
		setupFormats();
		contentFrame.addView(mScannerView);
	}

	@Override
	public void onResume() {
		super.onResume();
		mScannerView.setResultHandler(this);
		mScannerView.startCamera(mCameraId);
		mScannerView.setFlash(mFlash);
		mScannerView.setAutoFocus(mAutoFocus);
	}

	@Override
	public void onPause() {
		super.onPause();
		mScannerView.stopCamera();
		closeMessageDialog();
		closeFormatsDialog();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(FLASH_STATE, mFlash);
		outState.putBoolean(AUTO_FOCUS_STATE, mAutoFocus);
		outState.putIntegerArrayList(SELECTED_FORMATS, mSelectedIndices);
		outState.putInt(CAMERA_ID, mCameraId);
	}

	public void setupFormats() {
		List<BarcodeFormat> formats = new ArrayList<BarcodeFormat>();
		if (mSelectedIndices == null || mSelectedIndices.isEmpty()) {
			mSelectedIndices = new ArrayList<Integer>();
			for (int i = 0; i < BarcodeFormat.ALL_FORMATS.size(); i++) {
				mSelectedIndices.add(i);
			}
		}

		for (int index : mSelectedIndices) {
			formats.add(BarcodeFormat.ALL_FORMATS.get(index));
		}
		if (mScannerView != null) {
			mScannerView.setFormats(formats);
		}
	}

	public void closeMessageDialog() {
		closeDialog("scan_results");
	}

	public void closeFormatsDialog() {
		closeDialog("format_selector");
	}

	public void closeDialog(String dialogName) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		DialogFragment fragment = (DialogFragment) fragmentManager
				.findFragmentByTag(dialogName);
		if (fragment != null) {
			fragment.dismiss();
		}
	}

	@Override
	public void onDialogPositiveClick(DialogFragment dialog) {
		// Resume the camera
		mScannerView.resumeCameraPreview(this);
	}

	@Override
	public void onFormatsSaved(ArrayList<Integer> selectedIndices) {
		mSelectedIndices = selectedIndices;
		setupFormats();
	}

	@Override
	public void onCameraSelected(int cameraId) {
		// TODO Auto-generated method stub
		mCameraId = cameraId;
		mScannerView.startCamera(mCameraId);
		mScannerView.setFlash(mFlash);
		mScannerView.setAutoFocus(mAutoFocus);
	}

	@Override
	public void handleResult(Result rawResult) {
		// TODO Auto-generated method stub
		try {
			Uri notification = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			Ringtone r = RingtoneManager.getRingtone(getApplicationContext(),
					notification);
			r.play();
		} catch (Exception e) {
		}
		showMessageDialog("内容 = " + rawResult.getContents() + ", 格式 = "
				+ rawResult.getBarcodeFormat().getName());
	}

	public void showMessageDialog(String message) {
		DialogFragment fragment = MessageDialogFragment.newInstance("扫描结果",
				message, this);
		fragment.show(getSupportFragmentManager(), "scan_results");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem menuItem;

		if (mFlash) {
			menuItem = menu.add(Menu.NONE, R.id.menu_flash, 0,
					R.string.flash_on);
		} else {
			menuItem = menu.add(Menu.NONE, R.id.menu_flash, 0,
					R.string.flash_off);
		}
		MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER);

		if (mAutoFocus) {
			menuItem = menu.add(Menu.NONE, R.id.menu_auto_focus, 0,
					R.string.auto_focus_on);
		} else {
			menuItem = menu.add(Menu.NONE, R.id.menu_auto_focus, 0,
					R.string.auto_focus_off);
		}
		MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER);

		menuItem = menu.add(Menu.NONE, R.id.menu_formats, 0, R.string.formats);
		MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER);

		menuItem = menu.add(Menu.NONE, R.id.menu_camera_selector, 0,
				R.string.select_camera);
		MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_flash:
			mFlash = !mFlash;
			if (mFlash) {
				item.setTitle(R.string.flash_on);
			} else {
				item.setTitle(R.string.flash_off);
			}
			mScannerView.setFlash(mFlash);
			return true;
		case R.id.menu_auto_focus:
			mAutoFocus = !mAutoFocus;
			if (mAutoFocus) {
				item.setTitle(R.string.auto_focus_on);
			} else {
				item.setTitle(R.string.auto_focus_off);
			}
			mScannerView.setAutoFocus(mAutoFocus);
			return true;
		case R.id.menu_formats:
			DialogFragment fragment = FormatSelectorDialogFragment.newInstance(
					this, mSelectedIndices);
			fragment.show(getSupportFragmentManager(), "format_selector");
			return true;
		case R.id.menu_camera_selector:
			mScannerView.stopCamera();
			DialogFragment cFragment = CameraSelectorDialogFragment
					.newInstance(this, mCameraId);
			cFragment.show(getSupportFragmentManager(), "camera_selector");
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
