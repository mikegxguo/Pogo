package com.mitac.serialport;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.os.Message;
import android.os.Handler;

public class PogoActivity extends Activity implements OnDataReceiveListener {
    private static String TAG = "PogoActivity";
    /** Called when the activity is first created. */
    SerialPortUtil mSerialPortUtil;

    TextView tipPass;
    TextView tipFail;
    LinearLayout tipBack;
    private static int cntPass = 0;
    private static int cntFail = 0;

    private static final int MSG_REFRESH = 0x1234;
    private CharSequence strTest = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456";

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
                if (cntFail > 0 && tipBack != null) {
                    tipBack.setBackgroundColor(Color.RED);
                }
                break;
            default:
                break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tipPass = (TextView) findViewById(R.id.pass);
        tipFail = (TextView) findViewById(R.id.fail);
        tipBack = (LinearLayout) findViewById(R.id.background);

        mSerialPortUtil = new SerialPortUtil();
        mSerialPortUtil.onCreate();

        Log.d(TAG, "onCreate");
        // mSerialPortUtil.sendCmds("mike");
        mSerialPortUtil.setOnDataReceiveListener(this);
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
        mSerialPortUtil.closeSerialPort();
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

}