package com.amt.dialog.concrete;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by djf on 2017/5/15.
 */
public class AmtDialog extends MyDialog {



    /**
     *
     * AMT风格布局为 新版IPTV推荐布局
     */
    public static final int DIALOG_TYPE_AMTNATURALSTYLE= 1;


    /**
     *  默认布局为IPTV联通集采规范基础布局
     */
    public static final int DIALOG_TYPE_DEFAULT=0;

    private MyDialogListener listener;//dialog按键监听
    private Context mContext;
    private String TAG="AmtDialog";
    private static AmtDialogController amtDialogController;
    public static String AmtDialogErrorTpye=null;
    /**
     * 在构造方法中传入context，按键监听Lisntener
     * 约定 第一个按钮默认设置为“确定”，第二个按钮默认设置为“取消”
     * @param context
     * @param mDialogListener
     */
    public AmtDialog(Context context, MyDialogListener mDialogListener) {
        super(context);
        // TODO Auto-generated constructor stub
        this.mContext=context;
        if (mDialogListener != null) {
            this.listener=mDialogListener;
        }
        amtDialogController = new AmtDialogController(context,this, listener,-1);
    }
    /**
     * 提供样式快速设定的修改方法
     * @param mDialogListener   监听器

     */
    public AmtDialog(Context context, MyDialogListener mDialogListener, int Type) {
        // TODO Auto-generated constructor stub
        super(context);
        this.mContext=context;
        this.listener=mDialogListener;
        amtDialogController = new AmtDialogController(context,this, listener,Type);
    }




    /**********************************构造者模式**************************************************/



    public static class Builder{
        /**
         *  Keep the obj unique and invariant
         */
        private  final AmtDialogParams dialogInfo=new AmtDialogParams();
        private Context mContext;
        /**
         * Constructor using a context for this builder and the {@link AmtDialog} it creates.
         */
        public Builder(Context context) {
            this.mContext =context;
            dialogInfo.mContext =context;
        }

        /**
         * Returns a {@link Context} with the appropriate theme for dialogs created by this Builder.
         * Applications should use this Context for obtaining LayoutInflaters for inflating views
         * that will be used in the resulting dialogs, as it will cause views to be inflated with
         * the correct theme.
         *
         * @return A Context for built Dialogs.
         */
        public Context getContext() {
            return dialogInfo.mContext;
        }

        /**
         * Set the screen content from a layout resource.  The resource will be
         * inflated, adding all top-level views to the screen.
         *
         * @param layoutResID Resource ID to be inflated.
         */
        public Builder setContentView(int layoutResID) {

            dialogInfo.mlayoutResID = layoutResID;
            return this;
        }

        /**
         * Set the screen content to an explicit view.  This view is placed
         * directly into the screen's view hierarchy.  It can itself be a complex
         * view hierarchy.
         * @param view The desired content to display.
         */
        public Builder setContentView(View view) {
            dialogInfo.mview =view;
            return this;
        }

        /**
         * Set the screen content to an explicit view.  This view is placed
         * directly into the screen's view hierarchy.  It can itself be a complex
         * view hierarchy.
         * @param view The desired content to display.
         * @param params Layout parameters for the view.
         */
        public Builder setContentView(View view,ViewGroup.LayoutParams params) {
            dialogInfo.mview =view;
            dialogInfo.mlayoutparams = params;
            return this;
        }
        /**
         * Set the title using the given resource id.
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setDialogStyle(int Style) {
            dialogInfo.dialogStyle = Style;
            return this;
        }

        /**
         * Set the title using the given resource id.
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setTitle(int titleId) {
            if (titleId >0){
                dialogInfo.title = dialogInfo.mContext.getText(titleId).toString();
            }else {
                dialogInfo.title ="titleId does not exist";
            }
            return this;
        }


        /**
         * Set the title displayed in the {@link AmtDialog}.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setTitle(String title) {
            dialogInfo.title = title;
            return this;
        }

        /**
         * Set the title using the given resource id.
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setSubTitle(int subTitleId) {
            if (subTitleId >0){
                dialogInfo.subtitle = dialogInfo.mContext.getText(subTitleId).toString();
            }else {
                dialogInfo.subtitle ="subTitleId does not exist";
            }

            return this;
        }


        /**
         * Set the subTitle displayed in the {@link AmtDialog}.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setSubTitle(String title) {
            dialogInfo.subtitle = title;
            return this;
        }

        /**
         * Set the message to display using the given resource id.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setContext(int contentId) {
            if (contentId >0){
                dialogInfo.content = dialogInfo.mContext.getText(contentId).toString();
            }else {
                dialogInfo.content ="contentId does not exist";
            }

            return this;
        }


        /**
         * Set the message to display.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setContext(String content) {
            dialogInfo.content =content;
            return this;
        }
        /**
         *
         * @param text The text to display in the positive button
         * @param isCanColse The isCanColse will use to colse Dialog automatically
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setPositiveButtonText(String text, boolean isCanColse) {
            // set button visible
            dialogInfo.PositiveVisible =true;
            dialogInfo.PositiveButtonText = text;
            dialogInfo.PositiveDefaultColse =isCanColse;
            return this;
        }


        /**
         * @param textId The resource id of the text to display in the positive button
         * @param isCanColse The isCanColse will use to colse Dialog automatically
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setPositiveButtonText(int textId,boolean isCanColse) {
            // set button visible
            dialogInfo.PositiveVisible =true;
            if (textId >0){
                dialogInfo.PositiveButtonText = dialogInfo.mContext.getText(textId).toString();
            }else {
                dialogInfo.PositiveButtonText ="StringResourceIDNull";
            }
            dialogInfo.PositiveDefaultColse =isCanColse;
            return this;
        }

        /**
         *
         * @param text The text to display in the negative button
         * @param isCanColse The isCanColse will use to colse Dialog automatically
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setNegativeButtonText(String text, boolean isCanColse) {
            // set button visible
            dialogInfo.NegativeVisible = true;
            dialogInfo.NegativeButtonText = text;
            dialogInfo.NegativeDefaultColse =isCanColse;
            return this;
        }

        /**
         * @param textId The resource id of the text to display in the negative button
         * @param isCanColse The isCanColse will use to colse Dialog automatically
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setNegativeButtonText(int textId,boolean isCanColse) {
            // set button visible
            dialogInfo.NegativeVisible = true;
            if (textId >0){
                dialogInfo.NegativeButtonText = dialogInfo.mContext.getText(textId).toString();
            }else {
                dialogInfo.NegativeButtonText ="StringResourceIDNull";
            }
            dialogInfo.NegativeDefaultColse =isCanColse;
            return this;
        }

        /**
         *
         * @param text The text to display in the neutal button
         * @param isCanColse The isCanColse will use to colse Dialog automatically
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public  Builder setNeutralButton(String text, boolean isCanColse) {
            // set button visible
            dialogInfo.NeutralVisible = true;
            dialogInfo.NeutralButtonText = text;
            dialogInfo.NeutralDefaultColse =isCanColse;
            return this;
        }

        /**
         * @param textId The resource id of the text to display in the negative button
         * @param isCanColse The isCanColse will use to colse Dialog automatically
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setNeutralButton(int textId,boolean isCanColse) {
            // set button visible
            dialogInfo.NeutralVisible = true;
            if (textId >0){
                dialogInfo.NeutralButtonText = dialogInfo.mContext.getText(textId).toString();
            }else {
                dialogInfo.NeutralButtonText ="StringResourceIDNull";
            }
            dialogInfo.NeutralDefaultColse =isCanColse;
            return this;
        }

        /**
         * 设置自定义布局的title
         * @param titleId   文本
         * @param widgetid  控件ID
         * @return
         */
        public Builder setTitle(int titleId,int widgetid) {
            if (titleId >0){
                dialogInfo.title = dialogInfo.mContext.getText(titleId).toString();
            }else {
                dialogInfo.title ="titleId does not exist";
            }

            dialogInfo.title_id  = widgetid;
            return this;
        }

        public Builder setTitle(String title,int widgetid) {
            dialogInfo.title = title;
            dialogInfo.title_id  = widgetid;
            return this;
        }
        /**
         * 设置自定义布局的subTitle
         * @param subTitleId   文本
         * @param widgetid  控件ID
         * @return
         */
        public Builder setSubTitle(int subTitleId,int widgetid) {
            if (subTitleId >0){
                dialogInfo.subtitle = dialogInfo.mContext.getText(subTitleId).toString();
            }else {
                dialogInfo.subtitle ="subTitleId does not exist";
            }


            dialogInfo.subtitle_id  = widgetid;
            return this;
        }

        public Builder setSubTitle(String title,int widgetid) {
            dialogInfo.subtitle = title;
            dialogInfo.subtitle_id  = widgetid;
            return this;
        }


        /**
         * 自定义布局的 显示内容
         * @param contentId  文本
         * @param widget_id  控件ID
         * @return
         */
        public Builder setContext(int contentId,int widget_id) {
            if (contentId >0){
                dialogInfo.content =  dialogInfo.mContext.getText(contentId).toString();
            }else {
                dialogInfo.content ="contentId does not exist";
            }

            dialogInfo.content_id = widget_id;
            return this;
        }

        public Builder setContext(String content,int widget_id) {
            dialogInfo.content =  content;
            dialogInfo.content_id = widget_id;
            return this;
        }
        /**
         * 自定义布局  positiveBtn
         * @param text
         * @param isCanColse
         * @param widget_id
         * @return
         */
        public Builder setPositiveButton(String text, boolean isCanColse, int widget_id) {
            // set button visible
            dialogInfo.PositiveVisible =true;
            dialogInfo.PositiveButtonText = text;
            dialogInfo.PositiveDefaultColse =isCanColse;
            dialogInfo.PositiveButton_id = widget_id;
            return this;
        }

        public Builder setPositiveButton(int text, boolean isCanColse, int widget_id) {
            // set button visible
            dialogInfo.PositiveVisible =true;
            if (text >0){
                dialogInfo.PositiveButtonText = dialogInfo.mContext.getText(text).toString();
            }else {
                dialogInfo.PositiveButtonText ="StringResourceIDNull";
            }

            dialogInfo.PositiveDefaultColse =isCanColse;
            dialogInfo.PositiveButton_id = widget_id;
            return this;
        }

        /**
         * 自定义布局  NegativeBtn
         * @param text
         * @param isCanColse
         * @param widget_id
         * @return
         */

        public Builder setNegativeButton(String text, boolean isCanColse, int widget_id) {
            // set button visible
            dialogInfo.NegativeVisible = true;
            dialogInfo.NegativeButtonText = text;
            dialogInfo.NegativeDefaultColse =isCanColse;
            dialogInfo.NegativeButton_id = widget_id;
            return this;
        }

        public Builder setNegativeButton(int text, boolean isCanColse, int widget_id) {
            // set button visible
            dialogInfo.NegativeVisible = true;
            if (text >0){
                dialogInfo.NegativeButtonText = dialogInfo.mContext.getText(text).toString();
            }else {
                dialogInfo.NegativeButtonText ="StringResourceIDNull";
            }
            dialogInfo.NegativeDefaultColse =isCanColse;
            dialogInfo.NegativeButton_id = widget_id;
            return this;
        }
        /**
         * 自定义布局  positiveBtn
         * @param text
         * @param isCanColse
         * @param widget_id
         * @return
         */
        public  Builder setNeutralButton(String text, boolean isCanColse, int widget_id) {
            // set button visible
            dialogInfo.NeutralVisible = true;
            dialogInfo.NeutralButtonText = text;
            dialogInfo.NeutralDefaultColse =isCanColse;
            dialogInfo.NeutalButton_id = widget_id;
            return this;
        }

        public  Builder setNeutralButton(int text, boolean isCanColse, int widget_id) {
            // set button visible
            dialogInfo.NeutralVisible = true;
            if (text >0){
                dialogInfo.NeutralButtonText = dialogInfo.mContext.getText(text).toString();
            }else {
                dialogInfo.NeutralButtonText ="StringResourceIDNull";
            }
            dialogInfo.NeutralDefaultColse =isCanColse;
            dialogInfo.NeutalButton_id = widget_id;
            return this;
        }
        /**
         *
         * @param width  Scaling of screenwidth
         * @param height Scaling of screenheight
         *  realsize= screenheight/ height;
         *  realsize= screenwidth/ width
         */
        public Builder setDialogSize(double width,double height){
            dialogInfo.width = width;
            dialogInfo.height = height;
            return this;
        }

        /**
         * @param sec The Autotime  of the dialog to dismiss
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setAutoColse(int sec){
            dialogInfo.AutoCloseMs = sec;
            return this;
        }

        /**
         * @param myDialogListener The Tpye  of the dialog to display in activity or system
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setMyDialogListener(MyDialogListener myDialogListener){
            dialogInfo.myDialogListener = myDialogListener;
            return this;
        }


        /**
         * @param systemTpye The Tpye  of the dialog to display in activity or system
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setSystemDialog(boolean systemTpye){
            dialogInfo.systemType =systemTpye;
            return this;
        }


        /**
         * Creates a {@link AmtDialog} with the arguments supplied to this builder. It does not
         * {@link Dialog#show()} the dialog. This allows the user to do any extra processing
         * before displaying the dialog. Use {@link #show()} if you don't have any other processing
         * to do and want this to be created and displayed.
         */

        public AmtDialog create_() {
            final AmtDialog dialog = new AmtDialog(dialogInfo.mContext,dialogInfo.myDialogListener,dialogInfo.dialogStyle);
            amtDialogController.apply(dialogInfo);
            return dialog;
        }
        /**
         * Creates a {@link AmtDialog} with the arguments supplied to this builder and
         * {@link Dialog#show()}'s the dialog.
         */
        public AmtDialog show() {
            AmtDialog dialog = create_();
            dialog.show();
            return dialog;
        }


    }




}
