package com.recursivepenguin.botcubed.ui.printpanel;

import android.app.Fragment;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EFragment;
import com.recursivepenguin.botcubed.R;
import com.recursivepenguin.botcubed.service.PrinterConnectionProxy;

@EFragment(R.layout.fragment_print_panel)
public class PrintPanelFragment extends Fragment {

    @Click
    void zPlus1() {
        moveHead("Z", 1);
    }

    @Click
    void zPlus01() {
        moveHead("Z", 0.1);
    }

    private void moveHead(String axis, double amount) {
        PrinterConnectionProxy proxy = (PrinterConnectionProxy) getActivity();
        proxy.injectManualCommand("G91");
        proxy.injectManualCommand(String.format("G1 %s%s", axis, amount));
        proxy.injectManualCommand("G90");
    }
}
