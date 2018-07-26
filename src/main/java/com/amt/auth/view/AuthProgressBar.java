package com.amt.auth.view;

import com.amt.utils.ALOG;
import com.android.org.sychromium.ui.gfx.BitmapHelper;
import com.android.smart.terminal.iptvNew.R;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 认证进度条，适用于开机认证时，在屏幕下方显示认证进度
 * 
 * @author Administrator
 * 
 */
public class AuthProgressBar {
	Context context;
	View progressbarView;

	TextView tvwMsg;
	ProgressBar viewProgressBar;
	ImageView imageView;
	// 进度条的大小
	int width, height;
	static String TAG="AuthProgressBar";


	private AuthProgressBar() {
	}

	public AuthProgressBar(Context context, View iptvRootView) {
		init(context, iptvRootView);
	}

	private void init(Context context, View iptvRootView) {
		this.context = context;
		ALOG.debug(TAG,"--->init...");

		progressbarView = iptvRootView.findViewById(R.id.include_progressbar_auth);
		imageView=(ImageView) iptvRootView.findViewById(R.id.image_auth);
		tvwMsg = (TextView) progressbarView.findViewById(R.id.tvwMsg);
		viewProgressBar = (ProgressBar) progressbarView.findViewById(R.id.progressbar);



		ViewTreeObserver vto = progressbarView.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				progressbarView.getViewTreeObserver().removeGlobalOnLayoutListener(this);

				height = progressbarView.getLayoutParams().height;
				if (height <= 0)
					height = progressbarView.getHeight();

				width = progressbarView.getLayoutParams().width;
				if (width <= 0)
					width = progressbarView.getWidth();
			}
		});

		// addView(view, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
		// LayoutParams.WRAP_CONTENT));
	}

	/**
	 * 当前webview的长度
	 * 
	 * @param width
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * 显示认证进度信息
	 * 
	 * @param percentage
	 *            百分比，0-100
	 * @param msg
	 *            对应内容
	 */
	public void ShowPercentage(int percentage, String msg) {
		ALOG.debug(TAG,"ShowPercentage >>percentage:"+percentage+">>msg:"+msg);
		try {

			Show();

			viewProgressBar.setProgress(percentage);

			LinearLayout.LayoutParams params = new LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

			if (percentage < 50) {// 小于50，右对齐
				tvwMsg.setGravity(Gravity.LEFT);
				params.leftMargin = this.width * percentage / 100;
			} else if (percentage == 50) {// 居中
				tvwMsg.setGravity(Gravity.CENTER);
			} else {// 大于50，左对齐
				params.rightMargin = this.width - (this.width * percentage / 100);
				tvwMsg.setGravity(Gravity.RIGHT);
			}


			ALOG.debug(TAG,"ShowPercentage-->width:" + width + ",p:" + percentage + ",left:" + params.leftMargin);
			tvwMsg.setLayoutParams(params);
			tvwMsg.setText(msg);
			viewProgressBar.setVisibility(View.VISIBLE);
			tvwMsg.setVisibility(View.VISIBLE);

			//view.bringToFront();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	public void Hidden() {

		if (progressbarView != null && progressbarView.getVisibility() != View.GONE) {
			progressbarView.setVisibility(View.GONE);
		}

		ALOG.debug(TAG,"hidden:--progressbarView:"+progressbarView);
	}

	public void Show() {
		if (progressbarView != null && progressbarView.getVisibility() != View.VISIBLE)
			progressbarView.setVisibility(View.VISIBLE);

		ALOG.debug(TAG,"Show:--progressbarView:"+progressbarView);
	}

}
