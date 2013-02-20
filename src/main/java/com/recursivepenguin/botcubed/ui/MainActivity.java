package com.recursivepenguin.botcubed.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.recursivepenguin.botcubed.R;
import com.recursivepenguin.botcubed.service.PrinterConnectionProxy;
import com.recursivepenguin.botcubed.service.PrinterConnectionService;
import com.recursivepenguin.botcubed.service.PrinterConnectionService_;
import com.recursivepenguin.botcubed.service.PrinterError;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

@EActivity(R.layout.main)
public class MainActivity extends Activity implements PrinterConnectionProxy {

    private PrinterConnectionService mBoundService;

    boolean mIsBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        doBindService();
    }

    @Click
    void connectClicked() {
        if (mBoundService.isConnected()) {
            mBoundService.disconnectFromPrinter();
        } else {
            try {
                mBoundService.connectToPrinter();
                Crouton.makeText(this, "Connected to Printer", Style.CONFIRM).show();
            } catch (PrinterError printerError) {
                Crouton.makeText(this, printerError.getMessage(), Style.ALERT).show();
            }
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            mBoundService = ((PrinterConnectionService.LocalBinder) service).getService();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            mBoundService = null;
        }
    };

    void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        bindService(new Intent(MainActivity.this,
                PrinterConnectionService_.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }

    @Override
    public void injectManualCommand(String command) {
        if (mBoundService != null) {
            mBoundService.injectManualCommand(command);
        }
    }
}

