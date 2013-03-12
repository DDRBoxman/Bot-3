package com.recursivepenguin.botcubed.ui.debug;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.googlecode.androidannotations.annotations.EViewGroup;
import com.googlecode.androidannotations.annotations.ViewById;
import com.recursivepenguin.botcubed.R;

@EViewGroup(R.layout.item_usbdevice)
public class UsbDeviceItemView extends RelativeLayout {

    @ViewById
    TextView vendor;

    @ViewById
    TextView product;

    public UsbDeviceItemView(Context context) {
        super(context);
    }

    public void bind(UsbDevice device) {

        vendor.setText("" + Integer.toHexString(device.getVendorId()));
        product.setText("" + Integer.toHexString(device.getProductId()));

    }

}
