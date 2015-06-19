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
    int cntPass;
    int cntFail;

    private static final int PASS_MSG = 0x1234;
    private static final int FAIL_MSG = 0x1235;
    private CharSequence strTest = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456";

    private Handler hRefresh = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case PASS_MSG:
                if (tipPass != null) {
                    tipPass.setText("Pass: " + cntPass);
                }
                break;
            case FAIL_MSG:
                if (tipFail != null) {
                    tipFail.setText("Fail: " + cntFail);
                }
                if (cntFail > 1 && tipBack != null) {
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
        cntPass = 0;
        cntFail = 0;

        mSerialPortUtil = new SerialPortUtil();
        mSerialPortUtil.onCreate();

        // mSerialPortUtil.sendCmds("mike");
        mSerialPortUtil.setOnDataReceiveListener(this);
    }

    @Override
    protected void onDestroy() {
        mSerialPortUtil.closeSerialPort();
        super.onDestroy();
    }

    public void onDataReceive(byte[] buffer, int size) {
        if (size == 0) {
            cntFail = cntFail + 1;
            hRefresh.sendEmptyMessage(FAIL_MSG);
        } else if (size > 0) {
            String str = new String(buffer, 0, size);
            if (str.contains(strTest)) {
                cntPass = cntPass + 1;
                hRefresh.sendEmptyMessage(PASS_MSG);
            } else {
                cntFail = cntFail + 1;
                hRefresh.sendEmptyMessage(FAIL_MSG);
            }
        }
    }

}