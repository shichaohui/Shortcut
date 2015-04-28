package com.sch.shortcut;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class LoadingActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_loading);

		Intent intent = new Intent(this, MainActivity.class);

		String name = getIntent().getStringExtra("name");
		if (name != null) {
			intent.putExtra("name", name);
			intent.putExtra("isShortcut", true);
		}

		startActivity(intent);
		finish();

	}

}