package com.noel.sung.tw.demo_connection;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.noel.sung.tw.demo_connection.connect.MyConnect;
import com.noel.sung.tw.demo_connection.implement.OnSavedInInternalStorageListener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnSavedInInternalStorageListener
//        implements  MyConnect.OnConnectListener
{

    private MyConnect myConnect;

    //url 1  ＰＯＳＴ
//        private String url = "http://app.creatidea.com.tw/TaoyuanAgricultureExpo/Api/Scratch/AddMember";
    //url 2  ＧＥＴ
//    private String url = "http://data.ntpc.gov.tw/od/data/api/18621BF3-6B00-4A07-B49C-0C5CCABFE026";
    //url 3 download
    private String url = "https://ciappdownload.azurewebsites.net/APP/illegalDrug/android/commodity.csv";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        final Map<String, String> data = new HashMap<>();
        //data 1
//        data.put("Email", "XXX@hotmail.com");
//        data.put("Name", "name");
//        data.put("Phone", "0910000000");


        //data 2
//        data.put("$format", "json");
//        data.put("$filter", "routeNameZh%20eq%20637");


        myConnect = new MyConnect(this);

        //connect 1
//        myConnect.connect(MyConnect.CONNECT_TYPE_POST, url, data);
//        myConnect.setOnConnectListener(this);


        //connect 2
//        myConnect.connect(MyConnect.CONNECT_TYPE_GET, url, data);
//        myConnect.setOnConnectListener(this);


        //connect 3
        myConnect.downloadFileToInternalStorage(url, "commodity.csv");
        myConnect.setOnSavedInInternalStorageListener(this);
    }
    //------------------------

    /***
     *  TODO 以下接口 適用於  myConnect.downloadFileToInternalStorage
     *
     *  //當成功存取至內部資料夾後 進行讀取
     *
     * @param responseCode
     * @param absolutePath
     * @param fileName
     */
    @Override
    public void onSuccessSaved(int responseCode, String absolutePath, String fileName) {


        try {
            FileInputStream fileInputStream = openFileInput(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);

            char[] inputBuffer = new char[1024];
            int charRead;


            while ((charRead = inputStreamReader.read(inputBuffer)) > 0) {
                String readstring = String.copyValueOf(inputBuffer, 0, charRead);
                Log.e("readstring", readstring);
            }
            inputStreamReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onFailSaved(int responseCode) {

    }

    //------------------------

    /***
     *  TODO 以下接口 適用於 myConnect.connect
     */
//    @Override
//    public void onSuccessConnect(String response,int responseCode) {
//        Log.e("response", response);
//        Log.e("responseCode", responseCode + "");
//
//    }
//
//    @Override
//    public void onFailedConnect() {
//        Log.e("failed", "failed");
//
//    }
}
