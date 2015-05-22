package com.sch.shortcut;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

public class ShortcutHelp {

	private Context context = null;
	private String pkg = null;
	private String cls = null;
	
	public ShortcutHelp(Context context) {
		this.context = context;
		pkg = context.getPackageName();
		cls = pkg + ".LoadingActivityAlias";
	}
	
	/**
	 * 创建快捷方式
	 * @param name 创建快捷方式的名字
	 * @param icon 创建快捷方式的图标
	 */
	@SuppressLint("NewApi") public void createShortCut(String name, Bitmap icon) {
		if (!hasShortcut(name)) {
			// ----快捷键被点击的动作----
			Intent intent = new Intent(Intent.ACTION_MAIN);
			// 使用别名，解决应用完全退出后不能处理多个快捷方式的问题。
			ComponentName componentName = new ComponentName(pkg, cls);
			intent.setComponent(componentName);
			// 传递参数
			intent.putExtra("name", name);
			
			// 创建快捷方式的Intent
			Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
			// 不允许重复创建
			shortcut.putExtra("duplicate", false);
			// 需要显示的名称
			shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
			// 快捷图片
			shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON, icon);
			// 设置快捷键的动作
			shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
			// 发送广播。OK
			context.sendBroadcast(shortcut);
			// Toast.makeText(context, "快捷键\""+ name +"\"已创建", 0).show();
		} else {
			Toast.makeText(context, "快捷键\""+ name +"\"已存在", 0).show();
			deleteShortcutByComponent("com.sch");
		}
	}

	/**
	 * 根据 title 判断快捷方式是否存在
	 * @param title 快捷键的标题
	 * @return 是否存在
	 */
	private boolean hasShortcut(String title) {
		Cursor cursor = queryShortcut(new String[] { "title", "iconResource" },
				"title=?", new String[] { title }, null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cursor.close();
				return true;
			} else {
				cursor.close();
				return false;
			}
		}
		return false;
	}
	
	/** 
	 * 删除所有匹配到的快捷键
	 * @param componentName 快捷键的component
	 */
	public void deleteShortcutByComponent(String component) {
		Cursor cursor = queryShortcut(new String[] { "title", "intent" }, null, null, null);
		if (cursor != null && cursor.getCount() > 0) {
			String intent = "";
			while (cursor.moveToNext()) {
				intent = cursor.getString(cursor.getColumnIndex("intent"));
				if (intent != null && intent.contains(component)) {
					// 应用不存在，说明此快捷键失效
					if (!checkPackage(intent.split("component=")[1].split("/")[0])) {
						delShortcut(cursor.getString(cursor.getColumnIndex("title")), intent);
					}
				}
			}
			cursor.close();
		}
	}

	/**
	 * 删除程序的快捷方式
	 * 
	 * @param title
	 *            要删除的快捷键的名字
	 * @param intent
	 *            快捷键cursor中的intent，如果是删除本应用的快捷方式，此参数可以为""或者null
	 */
	public void delShortcut(String title, String intent){
        Intent shortcut = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");
        //快捷方式的名称
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
        String d_pkg = "";
        String d_cls = "";
        ComponentName comp = null;
		if (intent != null && !"".equals(intent)) {
			d_pkg = intent.split("component=")[1].split("/")[0];
			d_cls = pkg + intent.split("component=")[1].split("/")[1].split(";")[0];
			comp = new ComponentName(d_pkg, d_cls);
		} else {
			comp = new ComponentName(pkg, cls);
		}
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(Intent.ACTION_MAIN).setComponent(comp));
        context.sendBroadcast(shortcut);
    }
    
	/**
	 * 检测该包名所对应的应用是否存在
	 * @param packageName
	 * @return
	 */
	private boolean checkPackage(String packageName) {
		if (packageName == null || "".equals(packageName))
			return false;
		try {
			context.getPackageManager().getApplicationInfo(packageName,
					PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}

	/** 
	 * 查询桌面快捷键
	 * @param projection 要查询的项
	 * @param selection 查询条件
	 * @param selectionArgs 填充查询条件的占位符
	 * @param sortOrder 排序
	 * @return 查询到的快捷键的cursor
	 */
	private Cursor queryShortcut(String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		String url = null;
//		String pName = getAuthorityFromPermission();
		String pName = getLauncherPackageName(context);
		System.out.println(pName);
		if (pName != null && !"".equals(pName)) {
			url = "content://" + pName + "/favorites?notify=true";
		} else {
			if (Build.VERSION.SDK_INT < 8) {
				url = "content://com.android.launcher.settings/favorites?notify=true";
			} else {
				url = "content://com.android.launcher2.settings/favorites?notify=true";
			}
		}
		ContentResolver resolver = context.getContentResolver();
		return resolver.query(Uri.parse(url), projection, selection, selectionArgs, sortOrder);
	}
	
	/**
	 * 获取Launcher的包名
	 * @return
	 */
	private String getAuthorityFromPermission(){
		String permission = "com.android.launcher.permission.READ_SETTINGS"; // 权限
	    List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS);
	    if (packs != null) {
	        for (PackageInfo pack : packs) { 
	            ProviderInfo[] providers = pack.providers; 
	            if (providers != null) { 
	                for (ProviderInfo provider : providers) { 
	                    if (permission.equals(provider.readPermission)) return provider.authority;
	                    if (permission.equals(provider.writePermission)) return provider.authority;
	                } 
	            }
	        }
	    }
	    return null;
	}

	/** 获取正在运行桌面包名（注：存在多个桌面时且未指定默认桌面时，该方法返回Null,使用时需处理这个情况）  */
    public String getLauncherPackageName(Context context) {
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        final ResolveInfo res = context.getPackageManager().resolveActivity(intent, 0);
        if (res.activityInfo == null) {
            // should not happen. A home is always installed, isn't it?
            return null;
        }
        if (res.activityInfo.packageName.equals("android")) {
            // 有多个桌面程序存在，且未指定默认项时；     
            return null;
        } else {
            return res.activityInfo.packageName;
        }
    }
	
}