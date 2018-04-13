package com.garmin;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.garmin.health.ScannedDevice;
import com.sevencupsoftea.ears.R;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by gwicks on 11/04/2018.
 */

public class ScannedDeviceAdapter extends RecyclerView.Adapter<ScannedDeviceAdapter.ViewHolder> {

    private LayoutInflater mLayoutInflater;
    private CopyOnWriteArrayList<ScannedDevice> mScannedDevices;

    private OnItemClickListener mOnItemClickListener;

    public ScannedDeviceAdapter(Context context, List<ScannedDevice> data, OnItemClickListener onItemClickListener) {
        mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mScannedDevices = new CopyOnWriteArrayList<>(data);

        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View root = mLayoutInflater.inflate(R.layout.scanned_device_item, parent, false);
        return new ViewHolder(root);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ScannedDevice device = mScannedDevices.get(position);
        holder.bind(device, mOnItemClickListener);
    }

    @Override
    public int getItemCount() {
        return mScannedDevices != null ? mScannedDevices.size() : 0;
    }

    public void addDevice(ScannedDevice device) {

        for (ScannedDevice scannedDevice : mScannedDevices) {
            if (scannedDevice.address().equals(device.address())) {
                return;
            }
        }

        mScannedDevices.add(device);
        notifyDataSetChanged();
    }

    public void clearList() {
        mScannedDevices.clear();
        notifyDataSetChanged();
    }

    public void removeDevice(ScannedDevice device)
    {
        ScannedDevice found = null;

        for(ScannedDevice scannedDevice : mScannedDevices)
        {
            if (scannedDevice.address().equals(device.address()))
            {
                found = scannedDevice;
                break;
            }
        }

        mScannedDevices.remove(found);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mFriendlyName;
        private TextView mAddress;

        public ViewHolder(View view) {
            super(view);
            mFriendlyName = (TextView)view.findViewById(R.id.device_friendly_name);
            mAddress = (TextView)view.findViewById(R.id.device_address);
        }

        public void bind(final ScannedDevice scannedDevice, final OnItemClickListener itemClickListener) {

            mFriendlyName.setText(scannedDevice.friendlyName());
            mAddress.setText(scannedDevice.address());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onClick(scannedDevice);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onClick(ScannedDevice scannedDevice);
    }
}
