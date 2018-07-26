package com.amt.dialog;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.amt.dialog.concrete.AmtDialog;
import com.amt.utils.ALOG;
import com.android.smart.terminal.iptvNew.R;

import java.util.List;

/**
 * Created by zw on 2017/6/22.
 */

public class LTJC {

    private Context mContext;
    private AmtDialog amtDialog;
    private ListView listView;
    private ListAdapter infoAdapter;
    private List<String> infoList;

    public LTJC(Context mContext) {
        this.mContext = mContext;
        createViewInfoDialog();

    }

    public void setList(List<String> infoList) {
        this.infoList = infoList;
        initView();
    }

    private void createViewInfoDialog() {
        AmtDialog.Builder builder = new AmtDialog.Builder(mContext);
        builder.setContentView(R.layout.layout_view_info);
        amtDialog = builder.create_();

    }


    private void initView() {
        listView = (ListView) amtDialog.findViewById(R.id.view_info_list);
        listView.setVerticalScrollBarEnabled(true);
        infoAdapter = new ArrayAdapter(mContext, android.R.layout.simple_expandable_list_item_1, infoList);
        listView.setAdapter(infoAdapter);

        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    public void showViewInfoDialog() {
        ALOG.debug("showViewInfoDialog!!");
        if (amtDialog != null) {
            amtDialog.show();
        }
    }

    public void dismissViewInfoDialog() {
        ALOG.debug("dismissViewInfoDialog!!");
        if (amtDialog != null) {
            amtDialog.dismiss();
        }
    }


}
