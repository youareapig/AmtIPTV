package com.amt.app.view;


import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.amt.app.IPTVActivity;
import com.amt.utils.ALOG;
import com.amt.utils.ResolutionHelper;
import com.android.smart.terminal.iptvNew.R;

/**
 * 左下角加载圈操作视图。构造方法自动根据传递的rootView寻找加载圈控件。
 * @author zw
 *
 * 2016-9-27
 */
public class LoadingView {

	private View loadingView;
	private ImageView loadingImage;
	private Animation operatingAnim;
	private LinearInterpolator lin;
	
	private LoadingView(){}
	
	public LoadingView(View rootView) {
		if(rootView != null){
			loadingView = rootView.findViewById(R.id.include_progress_loading);
		}
		loadingImage = (ImageView) loadingView.findViewById(R.id.loadingImage);
		operatingAnim = AnimationUtils.loadAnimation(IPTVActivity.context, R.anim.loading);
		lin = new LinearInterpolator();
		operatingAnim.setInterpolator(lin);
		adjustLayout();
	}
	
	/**
	 * 隐藏加载圈。
	 */
	public void hide() {
		if(loadingView != null){
			loadingView.setVisibility(View.GONE);
			loadingImage.clearAnimation();
			loadingImage.setVisibility(View.INVISIBLE);
		}
	}
	/**
	 * 显示加载圈。
	 */
	public void show(){
		if(loadingView != null) {
			adjustLayout();
			loadingView.setVisibility(View.VISIBLE);
			loadingImage.setVisibility(View.VISIBLE);
			loadingImage.startAnimation(operatingAnim);
		}
	}

	private void adjustLayout() {
		ViewGroup.LayoutParams layoutParams =  loadingView.getLayoutParams();
		int x = 0;
		int y = 0;
		layoutParams.width = layoutParams.height = 100 / 2;
		x = 50;
		y = ResolutionHelper.epgHeight - layoutParams.height - 50;
		loadingView.setLayoutParams(layoutParams);
		loadingView.requestLayout();
		loadingView.invalidate();
		ALOG.info("calclayoutSize--->width: " + loadingView.getLayoutParams().width + " height: " + loadingView.getLayoutParams().height);
	}
}
