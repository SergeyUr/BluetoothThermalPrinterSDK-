package com.appdrafting.testbluetoothprinter_szzcs;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.imagpay.*;

import java.io.InputStream;

public class ContentActivity extends Activity {
	private final static String TAG = "BTPrinter";
	private BTReceiver receiver;
	private BTPrinter printer;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_content);

		receiver = new BTReceiver(this, "1234") {
			@Override
			public boolean isPrinter(BluetoothDevice device) {
				if (
						device.getName().equalsIgnoreCase("SZZCS")
						||device.getName().equalsIgnoreCase("ZCSPrinter")
						|| device.getName().equalsIgnoreCase("HC-05")
						|| device.getName().equalsIgnoreCase("ZCS103")
						|| device.getName().equalsIgnoreCase("Dual-SPP"))
					return true;
				return false;
			}

			@Override
			public void print(BluetoothDevice device) {
				printContent(device);
			}

			@Override
			public void startedDiscovery() {
				updateStatus("Start......");
			}

			@Override
			public void finishedDiscovery() {
				updateStatus("Finished discovery!");
			}
		};
		receiver.registerReceiver();
		ImageView iv = (ImageView) findViewById(R.id.qrcode);
		Bitmap qrcode = BitmapUtils.createQRCode("QRCODE://Shenzhen ZCS Technology Co.,Ltd.", 300, 300);
		Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
		BitmapUtils.createQRCodeWithPortrait(qrcode, logo);
		iv.setImageBitmap(qrcode);

		Button btn = (Button) findViewById(R.id.btnprint);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				receiver.start();
			}
		});
		
		btn = (Button) findViewById(R.id.btnprintnp);
		btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				receiver.setNeedsPin(false);
				receiver.start();
			}
		});
	}
	
	private void printContent(BluetoothDevice device) {
		updateStatus("Connect device " + device.getName());
		printer = new BTPrinter();
		if (printer.connect(device)) {
			try {
				updateStatus("Print......");
				printer.reset();


				printer.write(0x1B);
				printer.write(0x74);
				printer.write(0x82);

				printer.write(BTCommands.ALIGN_CENTER);
				printer.write(BTCommands.DEFAULT_BIG_FONT);
				printer.println("Test Page");
				printer.write(BTCommands.ALIGN_LEFT);
				printer.write(BTCommands.DEFAULT_NORMAL_FONT);
				printer.println("Shenzhen ZCS Technology Co.,Ltd.");
				printer.println("Chinese:您好");
				printer.println("Russian:Алло");
				printer.println("Japanese:こんにちは");

				//Print code128: No.123456
				//0x7b, 0x42: Code B(header of No.)
				//0x4e, 0x6f, 0x2e: No.
				//0x7b, 0x43: Code C(header of 123456)
				//12, 34, 56: 123456
				printer.write(BTCommands.ALIGN_LEFT);
				printer.println("Code128");
				printer.write(BTCommands.ALIGN_CENTER);
				printer.printBarcode(new byte[] { 0x7b, 0x42, 0x4e, 0x6f, 0x2e,
						0x7b, 0x43, 12, 34, 56 }, BTCommands.CODE128, 100);
				// Print code39: No.123456
				printer.write(BTCommands.ALIGN_LEFT);
				printer.println("Code39");
				printer.write(BTCommands.ALIGN_CENTER);
				printer.printBarcode(new byte[] { 0x31, 0x32, 0x33, 0x34, 0x35,
						0x36, 0x37, 0x38, 0x39 }, BTCommands.CODE39, 100);

				// Print qrcode
				printer.write(BTCommands.ALIGN_LEFT);
				printer.println("QRCode");
				printer.write(BTCommands.ALIGN_CENTER);
				printer.printQRCode("QRCODE://Shenzhen ZCS Technology Co.,Ltd.", 200, 200);

				// Print bitmap
				printer.write(BTCommands.ALIGN_LEFT);
				printer.println("Image");
				BitmapFactory.Options opt = new BitmapFactory.Options();
				opt.inPreferredConfig = Bitmap.Config.RGB_565;
				opt.inPurgeable = true;
				opt.inInputShareable = true;
				InputStream is = getResources().openRawResource(R.drawable.qrcode);
				Bitmap bitmap = BitmapFactory.decodeStream(is, null, opt);
				printer.write(BTCommands.ALIGN_CENTER);
				printer.printBitmap(bitmap);
				if (!bitmap.isRecycled()) {
					//Reclaim memory occupied by pictures
					bitmap.recycle();
				}
				bitmap = null;

				printer.write(BTCommands.LF);
				printer.finish();
				updateStatus("Printed");
			} catch (Exception e) {
				Log.d(TAG, "Print failed!", e);
				updateStatus("Print failed");
			}
		} else {
			updateStatus("Connection failed!");
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(5000);
				} catch (Exception e) {
				}
				printer.close();
				printer = null;
			}
		}).start();
	}
	
	private void updateStatus(final String msg) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				TextView tv = (TextView) findViewById(R.id.status);
				tv.setText(msg);
			}
		});
	}

	@Override
	public void onDestroy() {
		receiver.unregisterReceiver();
		super.onDestroy();
		android.os.Process.killProcess(android.os.Process.myPid());
	}
}