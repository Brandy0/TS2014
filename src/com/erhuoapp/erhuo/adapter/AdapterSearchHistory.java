package com.erhuoapp.erhuo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.erhuoapp.erhuo.R;

import java.util.List;

/**
 * 搜索历史列表Adapter
 *
 * @author hujiawei
 * @datetime 15/1/17
 */
@SuppressLint("UseSparseArrays")
public class AdapterSearchHistory extends ArrayAdapter<String> {

    private List<String> list;

    public AdapterSearchHistory(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        this.list = objects;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //因为列表项并不多，所以不用考虑复用，但是因为这些view经常性invalid，所以还是复用的比较好
        ViewHolder viewHolder;
        if (convertView != null && convertView.getTag() instanceof ViewHolder) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_listview_searchhistory, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        viewHolder.textView.setText(list.get(position));

        return convertView;
    }

    private static class ViewHolder {
        public final TextView textView;

        public ViewHolder(View view) {
            textView = (TextView) view.findViewById(R.id.tv_search_history);
        }
    }

}
