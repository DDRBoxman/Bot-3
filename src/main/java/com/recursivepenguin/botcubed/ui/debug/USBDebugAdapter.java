package com.recursivepenguin.botcubed.ui.debug;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.RootContext;

import java.util.ArrayList;

@EBean
public class USBDebugAdapter extends BaseAdapter {

    @RootContext
    Context context;

    ArrayList<UsbDevice> devices = new ArrayList<UsbDevice>();

    public void addDevice(UsbDevice device) {
        devices.add(device);
    }

    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public UsbDevice getItem(int i) {
        return devices.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        UsbDeviceItemView usbDeviceItemView;
        if (convertView == null) {
            usbDeviceItemView = UsbDeviceItemView_.build(context);
        } else {
            usbDeviceItemView = (UsbDeviceItemView) convertView;
        }

        usbDeviceItemView.bind(getItem(position));

        return usbDeviceItemView;
    }
}
