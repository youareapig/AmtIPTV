package com.amt.dialog.concrete;

/**
 * Dialog 自定义监听接口
 * @author djf  20161012
 *
 */
public class MyDialogListener {

    /**
     * The identifier for the positive button.
     */
    public static final int BUTTON_POSITIVE = -1;

    /**
     * The identifier for the negative button.
     */
    public static final int BUTTON_NEGATIVE = -2;

    /**
     * The identifier for the neutral button.
     */
    public static final int BUTTON_NEUTRAL = -3;
    /**
     * PositiveButton
     */
    public void PositiveButton(){};

    /**
     * negative button
     */
    public void NegativeButton(){};

    /**
     * neutral button
     */
    public void NeutalButton(){};

    /**
     * Interface used to allow the creator of a dialog to run some code when an
     * item on the dialog is clicked..
     */
    interface OnClickListener {
        /**
         * This method will be invoked when a button in the dialog is clicked.
         *
         * @param dialog The dialog that received the click.
         * @param which The button that was clicked (e.g.
         *            {@link MyDialogListener#BUTTON_POSITIVE}
         *            {@link MyDialogListener#BUTTON_NEGATIVE}
         *            {@link MyDialogListener#BUTTON_NEUTRAL}) or the position
         *            of the item clicked.
         */
        /* TODO: Change to use BUTTON_POSITIVE after API council */
        public void onClick(MyDialogListener dialog, int which);
    }

}
