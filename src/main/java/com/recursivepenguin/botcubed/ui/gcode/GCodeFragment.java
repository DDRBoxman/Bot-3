package com.recursivepenguin.botcubed.ui.gcode;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EFragment;
import com.ipaulpro.afilechooser.utils.FileUtils;
import com.recursivepenguin.botcubed.R;

import java.io.File;

@EFragment(R.layout.fragment_gcode)
public class GCodeFragment extends Fragment {

    private static final int REQUEST_CODE = 1234;
    private static final String CHOOSER_TITLE = "Select a file";

    @Click
    void loadGCode() {
        Intent target = FileUtils.createGetContentIntent();
        Intent intent = Intent.createChooser(target, CHOOSER_TITLE);
        try {
            startActivityForResult(intent, REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            // The reason for the existence of aFileChooser
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    // The URI of the selected file
                    final Uri uri = data.getData();
                    // Create a File from this Uri
                    File file = FileUtils.getFile(uri);
                }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
