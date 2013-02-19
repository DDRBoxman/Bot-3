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

    UsbSerialDriver driver;

    ReceiveThread mReceiveThread;

    boolean connected = false;

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

    public boolean isConnected() {
        return connected;
    }

    public void connectToPrinter() throws PrinterError {
        // Find the first available driver.
        driver = UsbSerialProber.acquire(usbManager);

        if (driver != null) {
            try {
                driver.open();
                driver.setBaudRate(115200);

                connected = true;

                mReceiveThread = new ReceiveThread();
                mReceiveThread.start();
            } catch (IOException e) {
                throw new PrinterError("Failed to open printer connection.");
            }
        }
        else {
            throw new PrinterError("No Printer Found");
        }
    }

    public void disconnectFromPrinter() {

        connected = false;

        mReceiveThread = null;

        try {
            driver.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    Send a print command, that does not belong to a print job.
     */
    public void injectManualCommand(String command) {
        Log.d(TAG, command);
    }

    private class ReceiveThread extends Thread {
        @Override
        public synchronized void start() {

            byte buffer[] = new byte[16];
            int numBytesRead = 0;
            try {
                numBytesRead = driver.read(buffer, 1000);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "Read " + numBytesRead + " bytes.");
        }
    }
}
