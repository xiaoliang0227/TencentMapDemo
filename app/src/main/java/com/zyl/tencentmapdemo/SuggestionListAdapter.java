package com.zyl.tencentmapdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.tencent.lbssearch.object.result.SuggestionResultObject;

import java.util.List;

/**
 * Created by zhaoyongliang on 2017/9/5.
 */

public class SuggestionListAdapter extends BaseAdapter implements Filterable {

    private Context context;

    private List<SuggestionResultObject.SuggestionData> data;

    private CustomFilter filter = new CustomFilter();

    public SuggestionListAdapter(Context context, List<SuggestionResultObject.SuggestionData> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return null == data ? 0 : data.size();
    }

    @Override
    public Object getItem(int position) {
        return null == data ? null : data.get(position).title;
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
            view = LayoutInflater.from(context).inflate(R.layout.suggestion_item, viewGroup, false);
            holder.title = view.findViewById(R.id.title);
            holder.address = view.findViewById(R.id.address);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        SuggestionResultObject.SuggestionData item = data.get(position);
        holder.title.setText(item.title);
        holder.address.setText(item.address);
        return view;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private class CustomFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            results.values = data;
            results.count = data.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }

    static class ViewHolder {
        TextView title;
        TextView address;
    }
}
