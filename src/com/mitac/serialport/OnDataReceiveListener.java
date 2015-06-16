package com.mitac.serialport;

public interface OnDataReceiveListener {
    public void onDataReceive(byte[] buffer, int size);
}
