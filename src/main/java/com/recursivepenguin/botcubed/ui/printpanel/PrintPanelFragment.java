package com.recursivepenguin.botcubed.ui.printpanel;

import android.app.Fragment;
import android.widget.RadioGroup;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.ViewById;
import com.recursivepenguin.botcubed.R;
import com.recursivepenguin.botcubed.service.PrinterConnectionProxy;

@EFragment(R.layout.fragment_print_panel)
public class PrintPanelFragment extends Fragment {

    @ViewById
    RadioGroup magnitude;

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
}
