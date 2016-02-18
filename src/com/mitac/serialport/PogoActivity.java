package com.mitac.serialport;


import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.os.BatteryManager;
import android.os.Message;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.os.UEventObserver;
import android.view.View;


public class PogoActivity extends Activity implements OnDataReceiveListener {
    private static String TAG = "PogoActivity";
    private static String VERSION = "1.1.0.3      2016/2/18 15:42";
    /** Called when the activity is first created. */
    //SerialPortUtil mSerialPortUtil;

    TextView tipPass;
    TextView tipFail;
    LinearLayout tipBackUrat;
    TextView tipAttach;
    TextView tipDettach;
    LinearLayout tipBackDet;
    TextView tipAC;
    TextView tipUSB;
    TextView tipBATT;
    LinearLayout tipBackBatt;

    private static int cntPass = 0;
    private static int cntFail = 0;
    private static int cntAttach = 0;
    private static int cntDettach = 0;
    private static int cntAC = 0;
    private static int cntUSB = 0;
    private static int cntBATT = 0;

    private static final int MSG_REFRESH = 0x1234;
    private CharSequence strTest = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456";
    
    private IntentFilter mIntentFilter;
    private static int mPlugged = 0;
    private boolean mDetach = false;


    private Handler hRefresh = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_REFRESH:
                if (tipPass != null) {
                    tipPass.setText("Pass: " + cntPass);
                }
                if (tipFail != null) {
                    tipFail.setText("Fail: " + cntFail);
                }
                if (cntFail > 0 && tipBackUrat != null) {
                    tipBackUrat.setBackgroundColor(Color.RED);
                }
                
                if (tipAttach != null) {
                    tipAttach.setText("Attach: " + cntAttach);
                }
                if (tipDettach != null) {
                    tipDettach.setText("Dettach: " + cntDettach);
                }
                if (cntDettach>0 && tipBackDet != null) {
                    tipBackDet.setBackgroundColor(Color.RED);
                }
                
                if (tipAC != null) {
                    tipAC.setText("Power source(AC): " + cntAC);
                }
                if (tipUSB != null) {
                    tipUSB.setText("Power source(USB): " + cntUSB);
                }
                if (tipBATT != null) {
                    tipBATT.setText("Power source(Battery): " + cntBATT);
                }
                if (mDetach && tipBackBatt != null) {
                    tipBackBatt.setBackgroundColor(Color.RED);
                }
                break;
            default:
                break;
            }
        }
    };


    private UEventObserver m_cradleObserver = new UEventObserver() {
        @Override
        public void onUEvent(UEvent event) {
            Log.d(TAG, "Event: " + event);
            boolean bCradle = "dock".equals(event.get("SWITCH_NAME")) ? true
                    : false;
            if (bCradle) {
                String status = event.get("SWITCH_STATE");
                if ("1".equals(status)) {
                    Log.d(TAG, "Cradle is attached.");
                    cntAttach += 1;
                } else if ("0".equals(status)) {
                    Log.d(TAG, "Cradle is dettached.");
                    cntDettach += 1;
                }
                hRefresh.sendEmptyMessage(MSG_REFRESH);
            }
        }
    };
    
    void getTime1() {
        long time = System.currentTimeMillis();
        // long now = android.os.SystemClock.uptimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d1 = new Date(time);
        String t1 = format.format(d1);
        Log.i(TAG, t1);
    }
    
    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//
//            int level = intent.getIntExtra("level", 0);
//            int status = intent.getIntExtra("status",
//                    BatteryManager.BATTERY_STATUS_CHARGING);
            int plugged = intent.getIntExtra("plugged", 0);
//            int scale = intent.getIntExtra("scale", 0);
//            int voltage = intent.getIntExtra("voltage", 0);
//            int temperature = intent.getIntExtra("temperature", 0);
//            int health = intent.getIntExtra("health",
//                     BatteryManager.BATTERY_HEALTH_UNKNOWN);
//
//            int icon_small = intent.getIntExtra("icon-small", 0);
//            boolean present = intent.getBooleanExtra("present", false);
//            String technology = intent.getStringExtra("technology");
            
            if(mPlugged != plugged) {
                if(!mDetach && (cntAC>0 || cntUSB>0)) {
                    mDetach = true;
                }
                mPlugged = plugged;
                if(mPlugged == BatteryManager.BATTERY_PLUGGED_AC) {
                    cntAC += 1;
                }
                else if(mPlugged == BatteryManager.BATTERY_PLUGGED_USB) {
                    cntUSB += 1;
                }
                else  {
                    cntBATT += 1;
                }
                hRefresh.sendEmptyMessage(MSG_REFRESH);
            }

            // =====================================================
//            String statusString = "";
//            switch (status) {
//            case BatteryManager.BATTERY_STATUS_UNKNOWN:
//                statusString = "unknown";
//                break;
//            case BatteryManager.BATTERY_STATUS_CHARGING:
//                statusString = "charging";
//                break;
//            case BatteryManager.BATTERY_STATUS_DISCHARGING:
//                statusString = "discharging";
//                break;
//            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
//                statusString = "not charging";
//                break;
//            case BatteryManager.BATTERY_STATUS_FULL:
//                statusString = "full";
//                break;
//            }

            // =====================================================
//            String healthString = "";
//            switch (health) {
//            case BatteryManager.BATTERY_HEALTH_UNKNOWN:
//                healthString = "unknown";
//                break;
//            case BatteryManager.BATTERY_HEALTH_GOOD:
//                healthString = "good";
//                break;
//            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
//                healthString = "overheat";
//                break;
//            case BatteryManager.BATTERY_HEALTH_DEAD:
//                healthString = "dead";
//                break;
//            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
//                healthString = "voltage";
//                break;
//            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
//                healthString = "unspecified failure";
//                break;
//            }

            // =====================================================
//            String acString = "";
//            switch (plugged) {
//            case BatteryManager.BATTERY_PLUGGED_AC:
//                acString = "plugged ac";
//                break;
//            case BatteryManager.BATTERY_PLUGGED_USB:
//                acString = "plugged usb";
//                break;
//            }
            // ==========================================================
//            if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
//                Log.d(TAG, "================================================");
//                long time = System.currentTimeMillis();
//
//                getTime1();
//                Log.d(TAG, "Level: " + level);
//                Log.d(TAG, "Capacity: " + scale);
//                Log.d(TAG, "Voltage: " + voltage);
//                Log.d(TAG, "Temperature: " + temperature);
//
//                Log.d(TAG, "Status: " + statusString);
//                Log.d(TAG, "Charge Type: " + acString);
//                Log.d(TAG, "Health: " + healthString);
//
//                Log.i(TAG, "Present: " + present);
//                Log.i(TAG, "Icon_Small: " + icon_small);
//                Log.i(TAG, "Technology: " + technology);
//
//                SimpleDateFormat format = new SimpleDateFormat(
//                        "yyyy-MM-dd HH:mm:ss");
//                Date d1 = new Date(time);
//                String strTime = format.format(d1);

                // String strTime = String.valueOf(time);
//                String str = strTime + "," + level + "," + scale + ","
//                        + voltage + "," + temperature + "," + statusString
//                        + "," + acString + "," + healthString + "\n";
//
//                Log.i(TAG, str);

                //Save(str);
                //if (mStorage) {
                //    CopyFileToSD(strName);
                //}

                // writeToSD(str);
                // ============================================================
//            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tipPass = (TextView) findViewById(R.id.pass);
        tipFail = (TextView) findViewById(R.id.fail);
        tipBackUrat = (LinearLayout) findViewById(R.id.background_urat);
        tipBackUrat.setVisibility(View.GONE); //Aiken requested on 2016/2/18
        
        tipAttach = (TextView) findViewById(R.id.attach);
        tipDettach = (TextView) findViewById(R.id.dettach);
        tipBackDet = (LinearLayout) findViewById(R.id.background_det);

        tipAC = (TextView) findViewById(R.id.batt_ac);
        tipUSB = (TextView) findViewById(R.id.batt_usb);
        tipBATT = (TextView) findViewById(R.id.batt_batt);
        tipBackBatt = (LinearLayout) findViewById(R.id.background_batt);

        //mSerialPortUtil = new SerialPortUtil();
        //mSerialPortUtil.onCreate();

        Log.d(TAG, "onCreate");
        Log.d(TAG, VERSION);
        // mSerialPortUtil.sendCmds("mike");
        //mSerialPortUtil.setOnDataReceiveListener(this);
        
        m_cradleObserver.startObserving("SUBSYSTEM=switch");
        //ORV cradle has no such pin(DETECT) between cradle and CPU
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mIntentReceiver, mIntentFilter);
   }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        hRefresh.sendEmptyMessage(MSG_REFRESH);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        //mSerialPortUtil.closeSerialPort();
        m_cradleObserver.stopObserving();
        unregisterReceiver(mIntentReceiver);
        super.onDestroy();
    }

    public void onDataReceive(byte[] buffer, int size) {
        if (size == 0) {
            cntFail = cntFail + 1;
        } else if (size > 0) {
            String str = new String(buffer, 0, size);
            if (str.contains(strTest)) {
                cntPass = cntPass + 1;
            } else {
                cntFail = cntFail + 1;
            }
        }
        hRefresh.sendEmptyMessage(MSG_REFRESH);
    }

    @Override
    public void openOptionsMenu() {
        // TODO Auto-generated method stub
        super.openOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        super.onCreateOptionsMenu(menu);
        int group1 = 0;
        menu.add(group1, 1, 0, VERSION);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
        case 1:
            Log.i(TAG, "menu item 1");
            break;
        default:
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

}
