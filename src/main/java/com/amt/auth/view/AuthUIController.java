package com.amt.auth.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.amt.auth.ProgressBarInterface;
import com.amt.utils.ALOG;
import com.android.smart.terminal.iptvNew.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by liaoyn on 2017/3/6.
 * 控制显示进度条UI，
 */

public class AuthUIController implements ProgressBarInterface{
    /**10% 网络接入中*/
    public static final int SHOWPIC_NET_LinkUp = 1;
    /** 50% 网络连接中（获取IP） */
    public static final int SHOWPIC_NET_Connected = 2;
    /**80% 网管正在连接网管平台*/
    public static final int SHOWPIC_AUTH_Data = 3;
    /**85% 零配置业务数据下发中*/
    public static final int SHOWPIC_AUTH_Connected = 4;
    /**100% 认证成功隐藏进度条*/
    public static final int SHOWPIC_AUTH_OK = 5;

    /*** 10% 家庭网络障碍*/
    public static final int SHOWPIC_LinkDown_ERROR = 6;
    /*** 50% 通路故障或AAA认证失败*/
    public static final int SHOWPIC_DisConnect_ERROR = 7;
    /*** 80% 注册ITMS失败*/
    public static final int SHOWPIC_AUTH_Data_ERROR = 8;
    /*** IPTV业务平台认证超时 85%*/
    public static final int SHOWPIC_AUTH_Connected_ERROR = 9;
    private static String TAG="AuthUIController";
    private final static String picURL="/data/local/LoadingPicURLAMT";
    /***进度条键值转换*/
    private HashMap<String, String > mapPercent;
    /***进度条提示文字*/
    private HashMap<String, String > mapPercentStr;
    private Context mContext=null;
    private View mRootView=null;
    /**认证进度条*/
    private static AuthProgressBar mAuthBar =null;
    /**认证主布局视图*/
    private RelativeLayout mainAuthView;
    /**认证背景图片*/
    private ImageView authImageView;
    /**
     * 控制进度条显示百分百比
     */
    private void  mapPercent() {
        mapPercent = new HashMap<String, String>() {{
            put(SHOWPIC_NET_LinkUp + "", "10");//10% 网络接入中
            put(SHOWPIC_NET_Connected + "", "50");//50% 网络连接中（获取IP）
            put(SHOWPIC_AUTH_Data + "", "80");//80% 零配置
            put(SHOWPIC_AUTH_Connected + "", "85");//85% 零配置业务数据下发中
            put(SHOWPIC_AUTH_OK + "", "100");//100% 认证成功
            put(SHOWPIC_LinkDown_ERROR + "", "10");//10%Error 网络障碍
            put(SHOWPIC_DisConnect_ERROR + "", "50");//50%Error 通路故障或AAA认证失败
            put(SHOWPIC_AUTH_Data_ERROR + "", "80");//80%Error 注册ITMS失败
            put(SHOWPIC_AUTH_Connected_ERROR + "", "85");//85%Error IPTV业务平台认证超时
        }};
        mapPercentStr = new HashMap<String, String>() {{
            put(SHOWPIC_NET_LinkUp + "", "  %网络接入中...");//10% 网络接入中
            put(SHOWPIC_NET_Connected + "", "  %IP地址获取中...");//50% 网络连接中（获取IP）
            put(SHOWPIC_AUTH_Data + "", "  %平台接入及MAC鉴权中...");//80% 零配置
            put(SHOWPIC_AUTH_Connected + "", "  %业务配置中...");//85% 零配置业务数据下发中
            put(SHOWPIC_AUTH_OK + "", "  %认证成功正进入平台...");//100% 认证成功
            put(SHOWPIC_LinkDown_ERROR + "", "  %家庭网络障碍");//10%Error 网络障碍
            put(SHOWPIC_DisConnect_ERROR + "", "  %通路故障或AAA认证失败");//50%Error 通路故障或AAA认证失败
            put(SHOWPIC_AUTH_Data_ERROR + "", "  %注册ITMS失败");//80%Error 注册ITMS失败
            put(SHOWPIC_AUTH_Connected_ERROR + "", "  %IPTV业务平台认证超时");//85%Error IPTV业务平台认证超时
        }};
    }

    public AuthUIController(Context context, View rootView){
        mContext=context;
        mRootView=rootView;
        mapPercent();
        initAuthView();
    }
    private AuthUIController(){

    }

    /**
     * 更新进度条
     * @param percent
     */
    @Override
    public void updatePercent(int percent){
        int percentValue=1;
        String percentStr="";
        if(mAuthBar==null){
            ALOG.debug(TAG,"updatePercent--->progressbar is null");
            return;
        }
        percentValue=Integer.parseInt(mapPercent.get(percent+""));
        percentStr=mapPercentStr.get(percent+"");
        ALOG.debug(TAG,"updatePercent--->progressbar > "+percent+" >> percentValue:"+percentValue);
        String msg="";
        switch (percent){
            case SHOWPIC_NET_LinkUp:
                msg=percentValue+percentStr;
                break;
            case SHOWPIC_NET_Connected:
                msg=percentValue+percentStr;
                break;
            case SHOWPIC_AUTH_Data:
                msg=percentValue+percentStr;
                break;
            case SHOWPIC_AUTH_Connected:
                msg=percentValue+percentStr;
                break;
            case SHOWPIC_AUTH_OK:
                msg=percentValue+percentStr;
                break;
            case SHOWPIC_LinkDown_ERROR:
                msg=percentValue+percentStr;
                break;
            case SHOWPIC_DisConnect_ERROR:
                msg=percentValue+percentStr;
                break;
            case SHOWPIC_AUTH_Data_ERROR:
                msg=percentValue+percentStr;
                break;
            case SHOWPIC_AUTH_Connected_ERROR:
                msg=percentValue+percentStr;
                break;
        }
        authPicShow();//显示认证图片
        LoadingPic(picURL);//背景图片
        mAuthBar.ShowPercentage(percentValue, msg);//进度条显示
    }

    /**
     * 隐藏进度条和背景图片
     */
    @Override
    public void Hidden() {
        if(mainAuthView!=null){
            mainAuthView.setVisibility(View.GONE);
        }
    }

    /**
     * 显示认证图片，这里要操作一次要不然认证图片显示不出来
     */
    public void authPicShow() {
        if(mainAuthView!=null){
            mainAuthView.setVisibility(View.VISIBLE);
        }
    }
    /**
     *根据图片地址替换背景图片
     * @param picurl
     */
    private void LoadingPic(String picurl){
        InputStream is=null;
        if(!TextUtils.isEmpty(picurl)) {
            try {
                File file = new File(picurl);
                if(file.exists()){
                    is=new FileInputStream(file);
                }
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }
        }
        if(is!=null&&authImageView!=null){
            Bitmap bm = BitmapFactory.decodeStream(is);
            bm =scaleBitmap(bm,1280,720);
            authImageView.setImageBitmap(bm);
        }
    }

    /**
     * 初始化进度条UI控件
     */
    protected void initAuthView(){
        ALOG.debug(TAG,"initAuthView");
        if(mRootView == null){
            ALOG.debug(TAG,"initAuthView Failed!! rootView is null!");
            return;
        }
        if(mContext == null){
            ALOG.debug(TAG,"initAuthView Failed!! Context is null!");
            return;
        }
        mainAuthView = (RelativeLayout)mRootView.findViewById(R.id.include_auth);
        mAuthBar = new AuthProgressBar(mContext, mRootView);
        updatePercent(SHOWPIC_NET_LinkUp);
        authImageView=(ImageView) mRootView.findViewById(R.id.image_auth);
    }
    /**
     * 拉伸bitmap图片对象
     *
     * @param bm
     *            图片对象
     * @param w
     *            拉伸至长
     * @param h
     *            拉伸至高
     * @return
     */
    public static Bitmap scaleBitmap(Bitmap bm, int w, int h) {
        ALOG.debug("w:"+w+"---h:"+h);
        if (bm == null)
            return bm;
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) w) / width;
        float scaleHeight = ((float) h) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
    }
}
