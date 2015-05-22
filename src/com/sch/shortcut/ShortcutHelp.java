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
	 * ������ݷ�ʽ
	 * @param name ������ݷ�ʽ������
	 * @param icon ������ݷ�ʽ��ͼ��
	 */
	@SuppressLint("NewApi") public void createShortCut(String name, Bitmap icon) {
		if (!hasShortcut(name)) {
			// ----��ݼ�������Ķ���----
			Intent intent = new Intent(Intent.ACTION_MAIN);
			// ʹ�ñ��������Ӧ����ȫ�˳����ܴ�������ݷ�ʽ�����⡣
			ComponentName componentName = new ComponentName(pkg, cls);
			intent.setComponent(componentName);
			// ���ݲ���
			intent.putExtra("name", name);
			
			// ������ݷ�ʽ��Intent
			Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
			// �������ظ�����
			shortcut.putExtra("duplicate", false);
			// ��Ҫ��ʾ������
			shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
			// ���ͼƬ
			shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON, icon);
			// ���ÿ�ݼ��Ķ���
			shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
			// ���͹㲥��OK
			context.sendBroadcast(shortcut);
			// Toast.makeText(context, "��ݼ�\""+ name +"\"�Ѵ���", 0).show();
		} else {
			Toast.makeText(context, "��ݼ�\""+ name +"\"�Ѵ���", 0).show();
			deleteShortcutByComponent("com.sch");
		}
	}

	/**
	 * ���� title �жϿ�ݷ�ʽ�Ƿ����
	 * @param title ��ݼ��ı���
	 * @return �Ƿ����
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
	 * ɾ������ƥ�䵽�Ŀ�ݼ�
	 * @param componentName ��ݼ���component
	 */
	public void deleteShortcutByComponent(String component) {
		Cursor cursor = queryShortcut(new String[] { "title", "intent" }, null, null, null);
		if (cursor != null && cursor.getCount() > 0) {
			String intent = "";
			while (cursor.moveToNext()) {
				intent = cursor.getString(cursor.getColumnIndex("intent"));
				if (intent != null && intent.contains(component)) {
					// Ӧ�ò����ڣ�˵���˿�ݼ�ʧЧ
					if (!checkPackage(intent.split("component=")[1].split("/")[0])) {
						delShortcut(cursor.getString(cursor.getColumnIndex("title")), intent);
					}
				}
			}
			cursor.close();
		}
	}

	/**
	 * ɾ������Ŀ�ݷ�ʽ
	 * 
	 * @param title
	 *            Ҫɾ���Ŀ�ݼ�������
	 * @param intent
	 *            ��ݼ�cursor�е�intent�������ɾ����Ӧ�õĿ�ݷ�ʽ���˲�������Ϊ""����null
	 */
	public void delShortcut(String title, String intent){
        Intent shortcut = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");
        //��ݷ�ʽ������
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
	 * ���ð�������Ӧ��Ӧ���Ƿ����
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
	 * ��ѯ�����ݼ�
	 * @param projection Ҫ��ѯ����
	 * @param selection ��ѯ����
	 * @param selectionArgs ����ѯ������ռλ��
	 * @param sortOrder ����
	 * @return ��ѯ���Ŀ�ݼ���cursor
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
	 * ��ȡLauncher�İ���
	 * @return
	 */
	private String getAuthorityFromPermission(){
		String permission = "com.android.launcher.permission.READ_SETTINGS"; // Ȩ��
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

	/** ��ȡ�����������������ע�����ڶ������ʱ��δָ��Ĭ������ʱ���÷�������Null,ʹ��ʱ�账����������  */
    public String getLauncherPackageName(Context context) {
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        final ResolveInfo res = context.getPackageManager().resolveActivity(intent, 0);
        if (res.activityInfo == null) {
            // should not happen. A home is always installed, isn't it?
            return null;
        }
        if (res.activityInfo.packageName.equals("android")) {
            // �ж�����������ڣ���δָ��Ĭ����ʱ��     
            return null;
        } else {
            return res.activityInfo.packageName;
        }
    }
	
}