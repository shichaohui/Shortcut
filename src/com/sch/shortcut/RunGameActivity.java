package com.sch.shortcut;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.sch.shortcut.R;

public class RunGameActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_run_game);
		
		String name = getIntent().getStringExtra("name");
		((TextView) findViewById(R.id.tv_name)).setText(name + "");
		
		if (getIntent().getBooleanExtra("isShortcut", false)) {
		}
		
	}

}
