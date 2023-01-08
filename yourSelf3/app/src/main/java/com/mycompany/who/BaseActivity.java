package com.mycompany.who;

import android.app.*;
import android.os.*;
import android.view.*;
import android.content.*;

public class BaseActivity extends Activity
{
	protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
	    dismiss_Title_And_ActionBar(this);
		dismiss_DownBar(this);
    }

	public static void dismiss_Title_And_ActionBar(Activity act){
		act.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //取消标题
        act.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
								 WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//取消状态栏	
	}

	public static void dismiss_DownBar(Activity act){
		//隐藏底部工具栏
	    act.getWindow().getDecorView().setSystemUiVisibility(
			View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
			| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
			| View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
	}

	public void onWindowFocusChanged(boolean hasFocus)
	{
        //被切换到后台及切回前台窗口焦点都会变化，而只有切回才重新隐藏系统UI控件
		super.onWindowFocusChanged(hasFocus);
        if (hasFocus)
			dismiss_DownBar(this);
    }
	

	class myDialog extends Dialog{
		myDialog(Context cont){
			super(cont);
		}

		public void dismiss_DownBar(Dialog dlog){
			//隐藏底部工具栏
			dlog.getWindow().getDecorView().setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
		}

		public void onWindowFocusChanged(boolean hasFocus)
		{
			//展示Dialog时，Dialog获得焦点，配置一下
			super.onWindowFocusChanged(hasFocus);
			if (hasFocus)
				dismiss_DownBar(this);
		}
	}
}
