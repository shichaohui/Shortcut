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
	 * 处理新来的Intent
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		processIntent(intent);
	}

	/**
	 * 处理Intent
	 * 
	 * @param intent
	 */
	private void processIntent(Intent intent) {
		// 取数据
		String name = intent.getStringExtra("name");
		if (name != null && !"".equals(name)) {
			// 带参数启动RunGameActivity
			Intent intent2 = new Intent(this, RunGameActivity.class);
			intent2.putExtras(intent.getExtras());
			startActivity(intent2);
		}
		intent.removeExtra("name"); // 移除gamexml
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 做需要做的事
		setContentView(R.layout.activity_main);
		
		findViewById(R.id.shortcut1).setOnClickListener(this);
		findViewById(R.id.shortcut2).setOnClickListener(this);
		findViewById(R.id.shortcut3).setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.shortcut1:
			new ShortcutHelp(this).createShortCut("快捷1", BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
			break;
		case R.id.shortcut2:
			new ShortcutHelp(this).createShortCut("快捷2", BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
			break;
		case R.id.shortcut3:
			new ShortcutHelp(this).createShortCut("快捷3", BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
			break;
		}
		
	}
}