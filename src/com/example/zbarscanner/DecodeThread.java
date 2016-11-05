package com.example.zbarscanner;

import java.util.Collection;
import java.util.List;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.qdtevc.zbar.core.BarcodeFormat;
import com.qdtevc.zbar.core.Result;

/**
 * 解码图片线程类
 * 
 * @author charles
 * 
 */
public class DecodeThread extends Thread {

	private Handler mHandler;
	private String photo_path;

	static {
		System.loadLibrary("iconv");
	}

	private ImageScanner mScanner;
	private List<BarcodeFormat> mFormats;

	DecodeThread(Handler mHandler, String photo_path) {
		this.mHandler = mHandler;
		this.photo_path = photo_path;
		setupScanner();
	}

	private void setupScanner() {
		mScanner = new ImageScanner();
		mScanner.setConfig(0, Config.X_DENSITY, 3);
		mScanner.setConfig(0, Config.Y_DENSITY, 3);
		mScanner.setConfig(Symbol.NONE, Config.ENABLE, 0);
//		for (BarcodeFormat format : getFormats()) {
//			mScanner.setConfig(format.getId(), Config.ENABLE, 1);
//		}
		mScanner.setConfig(Symbol.QRCODE, Config.ENABLE, 1);
	}

	public Collection<BarcodeFormat> getFormats() {
		if (mFormats == null) {
			return BarcodeFormat.ALL_FORMATS;
		}
		return mFormats;
	}

	@Override
	public void run() {
		Bitmap scanBitmap = null;
		try {
			if ("".equals(photo_path) || photo_path == null) {
				Message msg = new Message();
				msg.what = 2;
				mHandler.sendMessage(msg);
				return;
			}
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = false;
			options.inSampleSize = calculateInSampleSize(options, 256, 256);
			scanBitmap = BitmapFactory.decodeFile(photo_path, options);
			int width = scanBitmap.getWidth();
			int height = scanBitmap.getHeight();
			Image barcode = new Image(width, height, "Y800");
			int[] pixelData = new int[width * width];
			scanBitmap.getPixels(pixelData, 0, width, 0, 0, width, height);
			byte[] data = new byte[width * width];
			data = Bitmap2YUV.getYUV420sp(width, height, scanBitmap);
			barcode.setData(data);
			int result = mScanner.scanImage(barcode);
			if (result != 0) {
				SymbolSet syms = mScanner.getResults();
				final Result rawResult = new Result();
				for (Symbol sym : syms) {
					String symData;
					symData = new String(sym.getDataBytes(), "UTF-8");
					if (!TextUtils.isEmpty(symData)) {
						rawResult.setContents(symData);
						rawResult.setBarcodeFormat(BarcodeFormat
								.getFormatById(sym.getType()));
						break;
					}
				}

				Message msg = new Message();
				msg.what = 1;
				msg.obj = rawResult;
				mHandler.sendMessage(msg);

			} else {
				Message msg = new Message();
				msg.what = 0;
				mHandler.sendMessage(msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Message msg = new Message();
			msg.what = 0;
			mHandler.sendMessage(msg);
		} finally {
			if (scanBitmap != null) {
				scanBitmap.recycle();
			}
		}

	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

}
