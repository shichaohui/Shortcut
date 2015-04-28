package com.sch.shortcut;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;

import com.sch.shortcut.R;

public class MainActivity extends Activity implements OnClickListener {

	@Override
	public void onStart() {
		super.onStart();
		processIntent(getIntent());
	}

	/**
	 * ����������Intent
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		processIntent(intent);
	}

	/**
	 * ����Intent
	 * 
	 * @param intent
	 */
	private void processIntent(Intent intent) {
		// ȡ����
		String name = intent.getStringExtra("name");
		if (name != null && !"".equals(name)) {
			// ����������RunGameActivity
			Intent intent2 = new Intent(this, RunGameActivity.class);
			intent2.putExtras(intent.getExtras());
			startActivity(intent2);
		}
		intent.removeExtra("name"); // �Ƴ�gamexml
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ����Ҫ������
		setContentView(R.layout.activity_main);
		
		findViewById(R.id.shortcut1).setOnClickListener(this);
		findViewById(R.id.shortcut2).setOnClickListener(this);
		findViewById(R.id.shortcut3).setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.shortcut1:
			new ShortcutHelp(this).createShortCut("���1", BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
			break;
		case R.id.shortcut2:
			new ShortcutHelp(this).createShortCut("���2", BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
			break;
		case R.id.shortcut3:
			new ShortcutHelp(this).createShortCut("���3", BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
			break;
		}
		
	}
}