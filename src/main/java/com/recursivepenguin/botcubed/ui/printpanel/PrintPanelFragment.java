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
        PrinterConnectionProxy proxy = (PrinterConnectionProxy) getActivity();
        proxy.injectManualCommand("G91");
        proxy.injectManualCommand("G1 Z1");
        proxy.injectManualCommand("G90");
    }

}
