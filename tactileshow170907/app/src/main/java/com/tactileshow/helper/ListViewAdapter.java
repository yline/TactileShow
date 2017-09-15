package com.tactileshow.helper;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v4.util.SparseArrayCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 仅仅 展示 数据作用，没有其他作用
 *
 * @author yline 2017/9/15 -- 20:01
 * @version 1.0.0
 */
public class ListViewAdapter extends BaseAdapter {
    private static final int Empty = -1024;

    private Context sContext;

    private List<BluetoothDevice> sList;

    public ListViewAdapter(Context context) {
        this.sContext = context;
        this.sList = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        if (sList.size() == 0) {
            return Empty;
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getCount() {
        if (sList.size() == 0) {
            return 1;
        }
        return sList.size();
    }

    @Override
    public BluetoothDevice getItem(int position) {
        if (position >= sList.size()) {
            return null;
        }
        return sList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (getItemViewType(position) == Empty) {
            View view = LayoutInflater.from(sContext).inflate(android.R.layout.simple_expandable_list_item_1, parent, false);
            TextView textView = view.findViewById(android.R.id.text1);
            textView.setText("暂时没有搜索到BLE设备");
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 消除了 item动画
                }
            });
            return view;
        } else {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(sContext).inflate(android.R.layout.simple_expandable_list_item_1, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            onBindViewHolder(parent, holder, position);

            return convertView;
        }
    }

    private void onBindViewHolder(ViewGroup parent, final ViewHolder viewHolder, final int position) {
        BluetoothDevice bluetoothDevice = sList.get(position);
        String itemStr = bluetoothDevice.getAddress() + " " + bluetoothDevice.getName();
        viewHolder.setText(android.R.id.text1, itemStr);
    }

    /**
     * 给Adapter添加数据
     *
     * @param bluetoothDevice
     * @return
     */
    public boolean addData(BluetoothDevice bluetoothDevice) {
        if (!sList.contains(bluetoothDevice)) {
            boolean isSuccess = sList.add(bluetoothDevice);
            this.notifyDataSetChanged();
            return isSuccess;
        }
        return false;
    }

    /**
     * 清除数据，并更新界面
     */
    public void clear() {
        this.sList.clear();
        this.notifyDataSetChanged();
    }

    private class ViewHolder {
        private SparseArrayCompat<View> sArray;

        private View sView;

        public ViewHolder(View view) {
            this.sView = view;
            sArray = new SparseArrayCompat<>();
        }

        public <T extends View> T get(int viewId) {
            if (sArray.get(viewId) == null) {
                View view = sView.findViewById(viewId);
                sArray.put(viewId, view);
            }
            return (T) sArray.get(viewId);
        }

        public TextView setText(int viewId, String content) {
            TextView textView = this.get(viewId);
            textView.setText(content);
            return textView;
        }

        public void setOnClickListener(int viewId, View.OnClickListener listener) {
            this.get(viewId).setOnClickListener(listener);
        }
    }
}
