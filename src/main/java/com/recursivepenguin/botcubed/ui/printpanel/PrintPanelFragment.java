package com.recursivepenguin.botcubed.ui.printpanel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.ViewById;
import com.recursivepenguin.botcubed.Printer;
import com.recursivepenguin.botcubed.R;
import com.recursivepenguin.botcubed.service.PrinterConnectionProxy;
import com.recursivepenguin.botcubed.service.PrinterConnectionService;

@EFragment(R.layout.fragment_print_panel)
public class PrintPanelFragment extends Fragment {

    @ViewById
    TextView xpos;

    @ViewById
    TextView ypos;

    @ViewById
    TextView zpos;

    @ViewById
    RadioGroup magnitude;

    @ViewById
    TextView bedTemp;

    @ViewById
    TextView extruderTemp;

    @ViewById
    EditText extruderHeat;

    @ViewById
    EditText bedHeat;

    LocalBroadcastManager mManager;

    @Click
    void homeall() {
        homeAxis();
    }

    @Click
    void xhome() {
        homeAxis("X");
    }

    @Click
    void yhome() {
        homeAxis("Y");
    }

    @Click
    void zhome() {
        homeAxis("Z");
    }

    @Click
    void xplus() {
        moveHead("X", 1);
    }

    @Click
    void xminus() {
        moveHead("X", -1);
    }

    @Click
    void yplus() {
        moveHead("Y", 1);
    }

    @Click
    void yminus() {
        moveHead("Y", -1);
    }

    @Click
    void zplus() {
        moveHead("Z", 1);
    }

    @Click
    void zminus() {
        moveHead("Z", -1);
    }

    @Click
    void extruderHeatOn() {
        if (extruderHeat.getText().length() > 0) {
            int temp = Integer.parseInt(extruderHeat.getText().toString());
            PrinterConnectionProxy proxy = (PrinterConnectionProxy) getActivity();
            proxy.injectManualCommand(String.format("M104 S%s", temp));
        }
    }

    @Click
    void bedHeatOn() {
        if (bedHeat.getText().length() > 0) {
            int temp = Integer.parseInt(bedHeat.getText().toString());
            PrinterConnectionProxy proxy = (PrinterConnectionProxy) getActivity();
            proxy.injectManualCommand(String.format("M140 S%s", temp));
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        mManager = LocalBroadcastManager.getInstance(getActivity());
        mManager.registerReceiver(mMessageReceiver, new IntentFilter(PrinterConnectionService.ACTION_PRINTER_VALUES_CHANGED));
    }

    @Override
    public void onPause() {
        super.onPause();

        mManager.unregisterReceiver(mMessageReceiver);
    }

    private void homeAxis() {
        PrinterConnectionProxy proxy = (PrinterConnectionProxy) getActivity();
        proxy.injectManualCommand("G28");
    }

    private void homeAxis(String axis) {
        PrinterConnectionProxy proxy = (PrinterConnectionProxy) getActivity();
        proxy.injectManualCommand(String.format("G28 %s0.0", axis));
    }

    private void moveHead(String axis, double direction) {

        int id = magnitude.getCheckedRadioButtonId();

        double amount = 0;
        if (id == R.id.magnitude01) {
            amount = 0.1;
        } else if (id == R.id.magnitude1) {
            amount = 1;
        } else if (id == R.id.magnitude10) {
            amount = 10;
        } else if (id == R.id.magnitude100) {
            amount = 100;
        }

        amount *= direction;

        PrinterConnectionProxy proxy = (PrinterConnectionProxy) getActivity();
        proxy.injectManualCommand("G91");
        proxy.injectManualCommand(String.format("G1 %s%s", axis, amount));
        proxy.injectManualCommand("G90");
    }

    private void updatePosDisplay() {
        PrinterConnectionProxy proxy = (PrinterConnectionProxy) getActivity();
        Printer printer = proxy.getPrinter();
        if (printer != null) {
            xpos.setText("X: " + printer.getxPos());
            ypos.setText("Y: " + printer.getyPos());
            zpos.setText("Z: " + printer.getzPos());
        }
    }

    private void updateTempDisplay() {
        PrinterConnectionProxy proxy = (PrinterConnectionProxy) getActivity();
        Printer printer = proxy.getPrinter();
        if (printer != null) {
            bedTemp.setText("Bed: " + printer.getBedTemp());
            extruderTemp.setText("Extruder: " + printer.getExtruderTemp());
        }
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(PrinterConnectionService.ACTION_PRINTER_VALUES_CHANGED)) {
                updatePosDisplay();
                updateTempDisplay();
            }
        }
    };
}
