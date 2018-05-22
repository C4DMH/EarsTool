package com.garmin;

/**
 * Created by gwicks on 11/04/2018.
 */

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.garmin.health.ConnectionState;
import com.garmin.health.Device;
import com.sevencupsoftea.ears.R;

import java.util.ArrayList;
import java.util.List;


/* package */ class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {

    private static final String TAG = "DeviceAdapter";

    private final static String IMAGE_URL = "https://static.garmincdn.com/com.garmin.connect/content/images/device-images/%s.png";

    private LayoutInflater mLayoutInflater;
    private List<ConnectedDevice> mDevices;

    private OnItemClickListener mOnItemClickListener;
    Context mContext;
    TextView t1;

    DeviceAdapter(Context context, List<Device> data, OnItemClickListener onItemClickListener)
    {
        mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mDevices = new ArrayList<>();
        for (Device device : data) {
            mDevices.add(new ConnectedDevice(device));
        }
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View root = mLayoutInflater.inflate(R.layout.connected_device_item, parent, false);
        mContext = parent.getContext();

        return new ViewHolder(root);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final ConnectedDevice connectedDevice = mDevices.get(position);

        holder.mFriendlyName.setText(connectedDevice.device.friendlyName());
        holder.showAddress(connectedDevice.device.address());
        holder.showUnitId(connectedDevice.device.unitId());
        holder.connectStatus.setText("STATUS: NOT CONNECTED");
        holder.connectStatus.setTextColor(Color.RED);
        //t1 =  holder. findViewById(R.id.status);

        holder.setConnectivityStatus(connectedDevice.isConnected());
        //holder.showDeviceIcon(connectedDevice.device);


        holder.mForgetDevice.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onForgetDeviceClick(connectedDevice.device);
            }
        });

        holder.itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: in Device Adaptor itemVIew click");
                mOnItemClickListener.onItemClick(connectedDevice.device);
                Toast.makeText(mContext, "You have clicked itemview onclicklistener",Toast.LENGTH_LONG).show();
                //t1.setText("Thanks, if the text below says CONNECTED, YOU CAN EXIT TO HOME SCREEN");
                holder.connectStatus.setText("Thanks, if the text below says CONNECTED, YOU CAN EXIT TO HOME SCREEN");

            }
        });

        holder.mDeviceConnectedStatus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemClickListener.onConnectionClick(connectedDevice.device);
                notifyDataSetChanged();
                Toast.makeText(mContext, "You have clicked deviceconnectedstatus onclicklistener",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDevices != null ? mDevices.size() : 0;
    }

    void addDevice(Device device) {
        ConnectedDevice connectedDevice = getDevice(device.address());
        if (connectedDevice == null) {
            mDevices.add(new ConnectedDevice(device));
        }
        notifyDataSetChanged();
    }

    void refreshDevice(Device device) {
        ConnectedDevice connectedDevice = getDevice(device.address());
        if (connectedDevice != null) {
            notifyDataSetChanged();
        }
    }

    void removeDevice(String macAddress) {
        ConnectedDevice connectedDevice = getDevice(macAddress);
        if (connectedDevice != null) {
            mDevices.remove(connectedDevice);
            notifyDataSetChanged();
        }
    }

    private ConnectedDevice getDevice(String address) {
        for (ConnectedDevice connectedDevice : mDevices) {
            if (connectedDevice.device.address().equals(address)) {
                return connectedDevice;
            }
        }
        return null;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView mFriendlyName;
        TextView mAddress;
        ImageView mForgetDevice;
        ImageView mDeviceConnectedStatus;
        ImageView mDeviceIcon;
        TextView mUid;
        TextView connectStatus;

        ViewHolder(View view) {
            super(view);
            mFriendlyName = (TextView)view.findViewById(R.id.device_friendly_name);
            mAddress = (TextView)view.findViewById(R.id.device_address);
            mForgetDevice = (ImageView)view.findViewById(R.id.device_forget_device);
            mDeviceConnectedStatus = (ImageView)view.findViewById(R.id.device_connected_status);
            mDeviceIcon = (ImageView)view.findViewById(R.id.device_image);
            mUid = (TextView)view.findViewById(R.id.device_uid);
            connectStatus = view.findViewById(R.id.status);
        }

        void setConnectivityStatus(boolean isDeviceConnected) {

            float alpha = isDeviceConnected ? 1.0f : .25f;
            mDeviceConnectedStatus.setAlpha(alpha);
            if(isDeviceConnected){
                connectStatus.setText("DEVICE STATUS: READY, PLEASE PRESS BIG BLUE RECTANGLE");
                connectStatus.setTextColor(Color.GREEN);


            }

        }

        void showUnitId(long unitId) {
            mUid.setText(itemView.getContext().getString(R.string.device_uid, String.valueOf(unitId)));
        }

        void showAddress(String address) {
            mAddress.setText(itemView.getContext().getString(R.string.device_address, address));
        }

//        void showDeviceIcon(Device device) {
//
//            String deviceIcon = DeviceIcon.deviceIcon(device.friendlyName());
//
//            Glide.with(itemView.getContext())
//                    .load(String.format(IMAGE_URL, deviceIcon))
//                    .centerCrop()
//                    .crossFade()
//                    .into(mDeviceIcon);
//        }
    }

    private class ConnectedDevice {
        private Device device;

        ConnectedDevice(Device device) {
            this.device = device;
        }

        boolean isConnected() {
            return device.connectionState() == ConnectionState.CONNECTED;
        }
    }

    interface OnItemClickListener {
        void onItemClick(Device device);

        void onForgetDeviceClick(Device device);

        void onConnectionClick(Device device);
    }
}
