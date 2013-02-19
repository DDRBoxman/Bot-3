package com.recursivepenguin.botcubed.service;

import android.app.Service;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import com.googlecode.androidannotations.annotations.EService;
import com.googlecode.androidannotations.annotations.SystemService;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import java.io.IOException;

@EService
public class PrinterConnectionService extends Service {

    final String TAG = "PrinterConnectionService";

    @SystemService
    UsbManager usbManager;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public PrinterConnectionService getService() {
            return PrinterConnectionService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    public void acquirePrinter() {
        // Find the first available driver.
        UsbSerialDriver driver = UsbSerialProber.acquire(usbManager);

        if (driver != null) {
            try {
                driver.open();
                try {
                    driver.setBaudRate(115200);

                    byte buffer[] = new byte[16];
                    int numBytesRead = driver.read(buffer, 1000);
                    Log.d(TAG, "Read " + numBytesRead + " bytes.");
                } catch (IOException e) {
                    // Deal with error.
                } finally {
                    driver.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /*
    Send a print command, that does not belong to a print job.
     */
    public void injectManualCommand(String command) {
         Log.d(TAG, command);
    }
}
