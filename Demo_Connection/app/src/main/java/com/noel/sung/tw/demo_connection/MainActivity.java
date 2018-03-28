package com.noel.sung.tw.demo_connection;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.noel.sung.tw.demo_connection.connect.MyConnect;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements  MyConnect.OnConnectListener {

    private MyConnect myConnect;

    //url 1  ＰＯＳＴ
        private String url = "https://agriexpo.tycg.gov.tw/Api/Scratch/AddMember";
    //url 2  ＧＥＴ
//    private String url = "http://data.ntpc.gov.tw/od/data/api/18621BF3-6B00-4A07-B49C-0C5CCABFE026";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Map<String, String> data = new HashMap<>();
        //data 1
        data.put("Email", "XXX@hotmail.com");
        data.put("Name", "name");
        data.put("Phone", "0910359664");


        //data 2
//        data.put("$format", "json");
//        data.put("$filter", "routeNameZh%20eq%20637");



        myConnect = new MyConnect();
        myConnect.setOnConnectListener(this);
        myConnect.connect(MyConnect.CONNECT_TYPE_POST, url, data);

    }


    @Override
    public void onSuccessConnect(String response,int responseCode) {
        Log.e("response", response);
        Log.e("responseCode", responseCode + "");

    }

    @Override
    public void onFailedConnect() {
        Log.e("failed", "failed");

    }
}
