package com.recursivepenguin.botcubed.ui.sdcard;

import android.support.v4.app.ListFragment;
import android.widget.ArrayAdapter;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.recursivepenguin.botcubed.R;
import com.recursivepenguin.botcubed.service.PrinterConnectionProxy;

@EFragment
@OptionsMenu(R.menu.sdcard)
public class SDCardContentsFragment extends ListFragment {

    ArrayAdapter<String> fileAdapter;

    @AfterViews
    void init() {
        fileAdapter = new ArrayAdapter<String>(getActivity(), android.R.id.text1, android.R.layout.simple_list_item_1);
        setListAdapter(fileAdapter);
    }

    @OptionsItem(R.id.refresh)
    void refreshClicked() {
        PrinterConnectionProxy proxy = (PrinterConnectionProxy) getActivity();
        proxy.injectManualCommand("M21"); //init sd card
        proxy.injectManualCommand("M20"); //list sd card
    }

}
