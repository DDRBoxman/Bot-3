package com.recursivepenguin.botcubed.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.googlecode.androidannotations.annotations.*;
import com.recursivepenguin.botcubed.Printer;
import com.recursivepenguin.botcubed.R;
import com.recursivepenguin.botcubed.service.PrinterConnectionProxy;
import com.recursivepenguin.botcubed.service.PrinterConnectionService;
import com.recursivepenguin.botcubed.service.PrinterConnectionService_;
import com.recursivepenguin.botcubed.service.PrinterError;
import com.recursivepenguin.botcubed.ui.gcode.GCodeFragment;
import com.recursivepenguin.botcubed.ui.gcode.GCodeFragment_;
import com.recursivepenguin.botcubed.ui.printpanel.PrintPanelFragment;
import com.recursivepenguin.botcubed.ui.printpanel.PrintPanelFragment_;
import com.recursivepenguin.botcubed.ui.sdcard.SDCardContentsFragment;
import com.recursivepenguin.botcubed.ui.sdcard.SDCardContentsFragment_;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

import java.util.ArrayList;

@EActivity(R.layout.activity_home)
@OptionsMenu(R.menu.main)
public class MainActivity extends SherlockFragmentActivity implements PrinterConnectionProxy, ActionBar.TabListener,
        ViewPager.OnPageChangeListener {

    private PrinterConnectionService mBoundService;

    boolean mIsBound = false;

    @ViewById(R.id.pager)
    ViewPager mViewPager;

    PrintPanelFragment mPrintPanelFragment;
    SDCardContentsFragment mSDCardContentsFragment;
    GCodeFragment mGCodeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        doBindService();
    }

    @AfterViews
    void setupUI() {
        FragmentManager fm = getSupportFragmentManager();

        if (mViewPager != null) {
            // Phone setup
            mViewPager.setAdapter(new HomePagerAdapter(getSupportFragmentManager()));
            mViewPager.setOnPageChangeListener(this);

            final ActionBar actionBar = getSupportActionBar();
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            actionBar.addTab(actionBar.newTab()
                    .setText(R.string.print_panel)
                    .setTabListener(this));
            actionBar.addTab(actionBar.newTab()
                    .setText(R.string.sd_card)
                    .setTabListener(this));
            actionBar.addTab(actionBar.newTab()
                    .setText(R.string.gcode)
                    .setTabListener(this));
        } else {
            mPrintPanelFragment = (PrintPanelFragment) fm.findFragmentById(R.id.fragment_print_panel);
            mSDCardContentsFragment = (SDCardContentsFragment) fm.findFragmentById(
                    R.id.fragment_sd_card);
            mGCodeFragment = (GCodeFragment) fm.findFragmentById(R.id.fragment_gcode);
        }
    }

    @OptionsItem
    void connect() {
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
            mBoundService.addToCodeQueue(command);
        }
    }

    @Override
    public Printer getPrinter() {
        if (mBoundService != null) {
            return mBoundService.getPrinter();
        }
        return null;
    }

    @Override
    public void setGcode(ArrayList<String> gcode) {
        if (mBoundService != null) {
            mBoundService.setGcode(gcode);
        }
    }

    @Override
    public void startPrint() {
        if (mBoundService != null) {
            mBoundService.startPrint();
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {
    }

    @Override
    public void onPageSelected(int position) {
        getSupportActionBar().setSelectedNavigationItem(position);
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }

    private class HomePagerAdapter extends FragmentPagerAdapter {
        public HomePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return (mPrintPanelFragment = new PrintPanelFragment_());

                case 1:
                    return (mSDCardContentsFragment = new SDCardContentsFragment_());

                case 2:
                    return (mGCodeFragment = new GCodeFragment_());
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}

