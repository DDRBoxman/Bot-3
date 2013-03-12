package com.recursivepenguin.botcubed.ui.debug;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import com.actionbarsherlock.app.SherlockListActivity;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.SystemService;

import java.util.Map;

@EActivity
public class USBDebugActivity extends SherlockListActivity {

    @SystemService
    UsbManager usbManager;

    @Bean
    USBDebugAdapter adapter;

    @Override
    public void onResume() {
        super.onResume();
        Map<String, UsbDevice> devices = usbManager.getDeviceList();
        for (UsbDevice device : devices.values()) {
            adapter.addDevice(device);
        }
        setListAdapter(adapter);
    }

}
