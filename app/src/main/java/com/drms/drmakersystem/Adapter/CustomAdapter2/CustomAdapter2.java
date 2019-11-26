package com.drms.drmakersystem.Adapter.CustomAdapter2;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jjunj on 2017-09-13.
 */

public class CustomAdapter2 extends BaseAdapter {
    private Context mContext;
    private List<Custom2_Item> IconBox = new ArrayList<Custom2_Item>();

    public CustomAdapter2(Context context) {
        mContext = context;
    }

    public int getCount(){
        return IconBox.size();
    }

    @Override
    public Object getItem(int i) {
        return IconBox.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public View getView(int position, View converView, ViewGroup parent){
        Custom2_View itembox;
        if(converView == null)
            itembox = new Custom2_View(mContext, IconBox.get(position));
        else
            itembox = (Custom2_View)converView;

        itembox.setName(IconBox.get(position).getName());

        return itembox;
    }

    public void addItem(Custom2_Item item){
        IconBox.add(item);
    }
}