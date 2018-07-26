package com.amt.app.view;

import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.amt.utils.ALOG;
import com.android.smart.terminal.iptvNew.R;

/**
 * 时移图标
 * Created by Kelvin young on 2017/5/26.
 */

public class ShiftView {
    /*时移的view*/
    private View shiftView;
    private ImageView shiftimage;
    /*时移追上直播的view*/
    private View shift2liveView;
    private ImageView shift2liveimage;
    // 时移图标X相对于屏幕长的比例（防止音量、快进/退图标挡住时移图标，消失时会出现时移图标残缺）
    private Handler mhandler = new Handler();
    private static int dismisstime = 3000;

    Runnable r = new Runnable() {
        @Override
        public void run() {
                shift2liveView.setVisibility(View.INVISIBLE);
        }
    };


    public ShiftView(View rootView){
        shiftView = rootView.findViewById(R.id.include_progress_shift);
        shiftimage = (ImageView) shiftView.findViewById(R.id.shiftimage);
        shift2liveView = rootView.findViewById(R.id.include_progress_shift2live);
        shift2liveimage = (ImageView) shift2liveView.findViewById(R.id.shift2liveimage);
    }

    /**
     * 是否显示时移图标
     * @param isshow
     */
    public void showShiftImage(Boolean isshow){
        ALOG.info("showShiftImage--->show: " + isshow);
        if(shiftimage != null) {
            if (isshow) {
                shiftView.setVisibility(View.VISIBLE);
                mhandler.post(r);
            } else {
                shiftView.setVisibility(View.INVISIBLE);
            }
        }
    }


    /**
     * 显示时移追上直播图标三秒后自动消失
     *
     */
    public void showShift2liveImage(){
        shift2liveView.setVisibility(View.VISIBLE);
        shiftView.setVisibility(View.INVISIBLE);
        mhandler.postDelayed(r,dismisstime);
    }

    //abandon  
    private void adjustLayout(View view){
//        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
//        int Imgzoom = (int)(ResolutionHelper.epgWidth/1280.0f);
//        layoutParams.width = 71*Imgzoom;
//        layoutParams.height = 76*Imgzoom;
//        int x = (int)shiftImageXScale*ResolutionHelper.epgWidth;
//        int y = 10;
//        view.setX(x);
//        view.setY(y);
//        view.setLayoutParams(layoutParams);
//        view.requestLayout();
//        view.invalidate();
//        ALOG.debug("adjust layout-->width : "+layoutParams.width + ", height : " + layoutParams.height);
    }
}
