package com.drms.drmakersystem;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jjunj on 2016-12-20.
 */

public class IconTextListAdapter extends BaseAdapter{
    private Context mContext;

    private List<IconTextItem> mItems = new ArrayList<IconTextItem>();

    public IconTextListAdapter(Context context){
        mContext = context;
    }

    public int getCount(){
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        IconTextView itemView;
        if (convertView == null) {
            itemView = new IconTextView(mContext, mItems.get(position));
        } else {
            itemView = (IconTextView) convertView;
        }

        itemView.setIcon(mItems.get(position).getIcon());
        itemView.setText(0, mItems.get(position).getData(0));
        itemView.setText(1, mItems.get(position).getData(1));

        return itemView;
    }

    public void addItem(IconTextItem item){
        mItems.add(item);
    }

}
