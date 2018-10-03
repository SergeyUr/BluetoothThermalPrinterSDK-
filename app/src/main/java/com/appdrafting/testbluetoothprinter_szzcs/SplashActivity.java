package com.appdrafting.testbluetoothprinter_szzcs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import com.imagpay.R;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		TextView tv = (TextView) findViewById(R.id.version);
		// Display the current version number
		tv.setText("V " + getLocalVersionName(this, this.getPackageName()));
		tv = (TextView) findViewById(R.id.poweredby);
		tv.setText("Powered by www.szzcs.com");

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				/* Create an Intent that will start the Main Activity. */
				Intent intent = new Intent(SplashActivity.this,
						ContentActivity.class);
				SplashActivity.this.startActivity(intent);
				SplashActivity.this.finish();
			}
		}, 2500);
	}
	
	private String getLocalVersionName(Context context, String packageName) {
		PackageManager pm = context.getPackageManager();
		try {
			return pm.getPackageInfo(packageName, 0).versionName;
		} catch (NameNotFoundException e) {
			return "";
		}
	}
}