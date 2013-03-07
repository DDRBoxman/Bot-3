package com.recursivepenguin.botcubed.ui.gcode;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.*;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.ViewById;
import com.ipaulpro.afilechooser.utils.FileUtils;
import com.recursivepenguin.botcubed.R;
import com.recursivepenguin.botcubed.service.PrinterConnectionProxy;
import com.recursivepenguin.botcubed.service.PrinterConnectionService;

import java.io.*;
import java.util.ArrayList;

@EFragment(R.layout.fragment_gcode)
public class GCodeFragment extends Fragment {

    private static final int REQUEST_CODE = 1234;
    private static final String CHOOSER_TITLE = "Select a file";

    ArrayList<String> gcode = new ArrayList<String>();
    ArrayAdapter<String> adapter;

    @ViewById
    ListView gcodeList;

    LocalBroadcastManager mManager;

    @Override
    public void onResume() {
        super.onResume();

        mManager = LocalBroadcastManager.getInstance(getActivity());
        mManager.registerReceiver(mMessageReceiver, new IntentFilter(PrinterConnectionService.ACTION_CHANGED_STEP));
    }

    @Override
    public void onPause() {
        super.onPause();

        mManager.unregisterReceiver(mMessageReceiver);
    }

    @AfterViews
    void initViews() {
        gcodeList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

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

    @Click
    void  startPrint() {
        PrinterConnectionProxy proxy = (PrinterConnectionProxy) getActivity();
        proxy.startPrint();
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
                    GcodeLoader loader = new GcodeLoader(file);
                    loader.execute();
                }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    class GcodeLoader extends AsyncTask<Void, Void, Void> {

        File gcodeFile;
        ProgressDialog dialog;

        public GcodeLoader(File file) {
            gcodeFile = file;
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(getActivity());
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(gcodeFile));
                String strLine;
                while ((strLine = br.readLine()) != null) {
                    gcode.add(strLine);
                }
                //Close the input stream
                br.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, gcode);
            gcodeList.setAdapter(adapter);
            PrinterConnectionProxy proxy = (PrinterConnectionProxy) getActivity();
            proxy.setGcode(gcode);
            dialog.dismiss();
        }
    }

    private void updateStepPos(int step) {
         gcodeList.setSelection(step);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(PrinterConnectionService.ACTION_CHANGED_STEP)) {
                int pos = intent.getIntExtra("pos", 0);
                updateStepPos(pos);
            }
        }
    };
}
