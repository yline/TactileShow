package com.tactileshow.helper;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yline.view.recycler.adapter.CommonListAdapter;
import com.yline.view.recycler.holder.ViewHolder;

/**
 * 仅仅 展示 数据作用，没有其他作用
 *
 * @author yline 2017/9/15 -- 20:01
 * @version 1.0.0
 */
public class ListViewAdapter extends CommonListAdapter<BluetoothDevice> {
    private static final int Empty = -1024;

    public ListViewAdapter(Context context) {
        super(context);
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
        return super.getCount();
    }

    @Override
    public BluetoothDevice getItem(int position) {
        if (position >= sList.size()) {
            return null;
        }
        return sList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (getItemViewType(position) != Empty) {
            return super.getView(position, convertView, parent);
        } else {
            View view = LayoutInflater.from(sContext).inflate(getItemRes(position), parent, false);
            TextView textView = view.findViewById(android.R.id.text1);
            textView.setText("暂时没有搜索到BLE设备");
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 消除了 item动画
                }
            });
            return view;
        }
    }

    @Override
    protected int getItemRes(int position) {
        return android.R.layout.simple_expandable_list_item_1;
    }

    @Override
    protected void onBindViewHolder(ViewGroup parent, ViewHolder viewHolder, int position) {
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
}
