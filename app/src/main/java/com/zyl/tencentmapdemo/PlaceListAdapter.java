package com.zyl.tencentmapdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tencent.lbssearch.object.result.SearchResultObject;

import java.util.List;

/**
 * Created by zhaoyongliang on 2017/9/5.
 */

public class PlaceListAdapter extends BaseAdapter {

    private Context context;

    private List<SearchResultObject.SearchResultData> data;

    public PlaceListAdapter(Context context, List<SearchResultObject.SearchResultData> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return null == data ? 0 : data.size();
    }

    @Override
    public Object getItem(int position) {
        return null == data ? null : data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (null == view) {
            holder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.place_item, viewGroup, false);
            holder.title = view.findViewById(R.id.title);
            holder.address = view.findViewById(R.id.address);
            holder.tel = view.findViewById(R.id.tel);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        SearchResultObject.SearchResultData item = data.get(position);
        holder.title.setText(item.title);
        holder.address.setText(item.address);
        holder.tel.setText(item.tel);
        return view;
    }

    static class ViewHolder {
        TextView title;
        TextView address;
        TextView tel;
    }
}
