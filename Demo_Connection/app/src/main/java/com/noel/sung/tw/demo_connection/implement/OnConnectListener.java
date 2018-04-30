package com.noel.sung.tw.demo_connection.implement;


/**
 * Created by noel on 2018/4/27.
 */

public interface OnConnectListener {
    void onSuccessConnect(String response, int responseCode);
    void onFailedConnect();
}

