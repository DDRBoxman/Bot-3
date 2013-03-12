package com.recursivepenguin.botcubed.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.googlecode.androidannotations.annotations.EService;
import com.googlecode.androidannotations.annotations.SystemService;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;
import com.recursivepenguin.botcubed.Printer;
import com.recursivepenguin.botcubed.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@EService
public class PrinterConnectionService extends Service {

    final String TAG = "PrinterConnectionService";

    public static final String ACTION_PRINTER_VALUES_CHANGED = "com.recursivepenguin.botcubed.service.ACTION_PRINTER_VALUES_CHANGED";
    public static final String ACTION_CHANGED_STEP = "com.recursivepenguin.botcubed.service.ACTION_CHANGED_STEP";

    private static final String ACTION_USB_PERMISSION = "com.recursivepenguin.botcubed.USB_PERMISSION";
    public static final String ACTION_CONNECTION_FAILED = "com.recursivepenguin.botcubed.CONNECTION_FAILED";
    public static final String ACTION_CONNECTION_SUCCESS = "com.recursivepenguin.botcubed.CONNECTION_SUCCESS";

    private final int ONGOING_NOTIFICATION_ID = 3424;
    Notification mOngoingNotification;

    @SystemService
    UsbManager usbManager;

    @SystemService
    NotificationManager notificationManager;

    UsbSerialDriver mSerialDevice;

    private SerialInputOutputManager mSerialIoManager;

    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();

    boolean connected = false;

    private Printer printer = new Printer();

    LocalBroadcastManager mManager;

    LinkedBlockingQueue<String> commandQueue = new LinkedBlockingQueue<String>();

    String lastCommand;

    ArrayList<String> gcode;

    int gcodePos;

    boolean printing = true;
    boolean waitingOnCommand = false;

    PendingIntent mPermissionIntent;

    int commandLineNumber = 1;

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

    @Override
    public void onCreate() {
        super.onCreate();

        mManager = LocalBroadcastManager.getInstance(this);

        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);

        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);

        IntentFilter deviceDetachFilter = new IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mUsbReceiver, deviceDetachFilter);

        mOngoingNotification = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.icon).setContentTitle("Connected To Printer").setOngoing(true).getNotification();
    }

    public boolean isConnected() {
        return connected;
    }

    public void requestConnectToPrinter() throws PrinterError {
        // Find the first available driver.
        UsbDevice device = UsbSerialProber.findFirstSupported(usbManager);

        if (device != null) {
            usbManager.requestPermission(device, mPermissionIntent);
        } else {
            throw new PrinterError("No Printer Found");
        }
    }

    public void disconnectFromPrinter() {

        stopIoManager();

        connected = false;

        try {
            mSerialDevice.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Printer getPrinter() {
        return printer;
    }

    private final SerialInputOutputManager.Listener mListener =
            new SerialInputOutputManager.Listener() {

                @Override
                public void onRunError(Exception e) {
                    Log.d(TAG, "Runner stopped.");
                }

                @Override
                public void onNewData(final byte[] data) {
                    String response = new String(data);
                    String[] responses = response.split("\n");
                    for (String code : responses) {
                        Log.d(TAG, code);
                        parseResponse(code);
                    }
                }
            };

    private void stopIoManager() {
        if (mSerialIoManager != null) {

            notificationManager.cancel(ONGOING_NOTIFICATION_ID);

            Log.i(TAG, "Stopping io manager ..");
            mSerialIoManager.stop();
            mSerialIoManager = null;
        }
    }

    private void startIoManager() {
        if (mSerialDevice != null) {
            Log.i(TAG, "Starting io manager ..");


            notificationManager.notify(ONGOING_NOTIFICATION_ID, mOngoingNotification);

            mSerialIoManager = new SerialInputOutputManager(mSerialDevice, mListener);
            mExecutor.submit(mSerialIoManager);
            commandLineNumber = 1;
            addToCodeQueue("M114");
        }
    }

    /*
    Send a print command, that does not belong to a print job.
     */
    public void addToCodeQueue(String code) {
        commandQueue.add(code);
        sendNext();
    }

    public void setGcode(ArrayList<String> newGcode) {
        printing = false;
        gcode = newGcode;
        gcodePos = 0;
    }

    public ArrayList<String> getGcode() {
        return gcode;
    }

    public void startPrint() {
        if (!printing) {
            printing = true;

            sendNext();
        }
    }

    public void pausePrint() {
        if (printing) {
            printing = false;
        }
    }

    private void sendNext() {
        if (!waitingOnCommand) {
            if (!commandQueue.isEmpty()) {
                if (mSerialIoManager != null) {
                    lastCommand = commandQueue.poll();
                    waitingOnCommand = true;
                    sendCommand(lastCommand);
                }
            } else if (printing && gcode != null && gcode.size() > gcodePos) {
                String code = gcode.get(gcodePos);
                if (code.length() > 1 && code.charAt(0) != ';') {
                    waitingOnCommand = true;
                    sendCommand(code);

                    Intent intent = new Intent();
                    intent.setAction(ACTION_CHANGED_STEP);
                    intent.putExtra("pos", gcodePos);
                    mManager.sendBroadcast(intent);
                }
                gcodePos++;
                sendNext();
            }
        }
    }

    private void sendCommand(String command) {

        int comment = command.indexOf(';');
        if (comment > -1) {
            command = command.substring(0, comment);
            command = command.trim();
        }

        command = String.format("N%d %s ", commandLineNumber, command);
        int cs = 0;
        byte[] bytes = command.getBytes();
        for (int i = 0; i < bytes.length; i++) {
            cs = cs ^ bytes[i];
        }
        cs &= 0xff;  // Defensive programming...
        command = String.format("%s*%d\n", command, cs);

        Log.d(TAG, command);
        mSerialIoManager.writeAsync(command.getBytes());
        commandLineNumber++;
    }

    Pattern dataPattern = Pattern.compile("([A-Z]):(\\d+(\\.\\d+)?)");

    private void parseResponse(String response) {
        String type = response.substring(0, 2);
        if (type.equals("ok")) {
            //okay

            waitingOnCommand = false;
            sendNext();
        } else if (type.equals("rs")) {
            //resend

        } else if (type.equals("!!")) {
            //Oh shit hardware dead!
        } else {

            String id;
            double value;
            boolean updated = false;

            Matcher m = dataPattern.matcher(response);
            while (m.find()) {

                id = m.group(1);
                value = Double.parseDouble(m.group(2));

                if (id.equals("X")) {
                    printer.setxPos(value);
                    updated = true;
                } else if (id.equals("Y")) {
                    printer.setyPos(value);
                    updated = true;
                } else if (id.equals("Z")) {
                    printer.setzPos(value);
                    updated = true;
                } else if (id.equals("B")) {
                    printer.setBedTemp(value);
                    updated = true;
                } else if (id.equals("T")) {
                    printer.setExtruderTemp(value);
                    updated = true;
                }
            }

            if (updated) {
                Intent intent = new Intent();
                intent.setAction(ACTION_PRINTER_VALUES_CHANGED);
                mManager.sendBroadcast(intent);
            }
        }
    }

    private void notifyConnectionSuccess() {
        Intent intent = new Intent();
        intent.setAction(ACTION_CONNECTION_SUCCESS);
        mManager.sendBroadcast(intent);
    }

    private void notifyConnectionFailed() {
        Intent intent = new Intent();
        intent.setAction(ACTION_CONNECTION_FAILED);
        mManager.sendBroadcast(intent);
    }

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {

                            mSerialDevice = UsbSerialProber.acquire(usbManager, device);

                            if (mSerialDevice != null) {
                                try {
                                    mSerialDevice.open();

                                    connected = true;

                                    startIoManager();

                                    notifyConnectionSuccess();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                notifyConnectionFailed();
                            }
                        } else {
                            notifyConnectionFailed();
                            Log.d(TAG, "permission denied for device " + device);
                        }
                    }
                }
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                connected = false;
                notifyConnectionFailed();
                stopIoManager();
                commandQueue.clear();
            }
        }
    };
}
