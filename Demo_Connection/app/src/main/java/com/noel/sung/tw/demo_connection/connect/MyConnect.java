package com.noel.sung.tw.demo_connection.connect;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by noel on 2018/3/28.
 */

public class MyConnect {
    private OnConnectListener onConnectListener;
    private static final String TAG = MyConnect.class.getSimpleName();
    private HttpURLConnection httpURLConnection;
    private final int TIME_OUT = 15 * 1000;

    public static final String CONNECT_TYPE_GET = "GET";
    public static final String CONNECT_TYPE_POST = "POST";

    private final String NULL_REQUEST = "null";


    private Thread connectThread;
    private Timer timer;
    private boolean isTimeOut;
    private final String RETRY_TIME = "retry:";
    private int retryTime = 1;
    private int MAX_RETRY_TIME = 5;


    private String requestMethod;
    private String urlString;
    private Map<String, String> data;

    //---------------------------------------------------------------------------

    /***
     * 發起連線
     * @param requestMethod 連線方式
     * @param urlString  url
     * @param data  傳的參數 HashMap<String,String>
     */
    public void connect(final String requestMethod, final String urlString, final @Nullable Map<String, String> data) {
        this.requestMethod = requestMethod;
        this.urlString = urlString;
        this.data = data;

        connectThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String response = NULL_REQUEST;
                String newUrlString = urlString;

                try {
                    if (requestMethod.equals(CONNECT_TYPE_GET) && data != null) {
                        newUrlString = putParamsForGet(newUrlString, data);
                    }
                    Log.e("urlString", newUrlString);
                    URL url = new URL(newUrlString);
                    httpURLConnection = (HttpURLConnection) url.openConnection();


                    // 設定TimeOut時間
                    httpURLConnection.setReadTimeout(TIME_OUT);
                    httpURLConnection.setConnectTimeout(TIME_OUT);

                    //get or post
                    httpURLConnection.setRequestMethod(requestMethod);
                    // 設定開啟自動轉址用以處理https問題
                    httpURLConnection.setInstanceFollowRedirects(true);

                    httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    httpURLConnection.setRequestProperty("Accept", "application/json");
                    if (requestMethod.equals(CONNECT_TYPE_POST) && data != null) {
                        OutputStream outputStream = httpURLConnection.getOutputStream();
                        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                        dataOutputStream.writeBytes(putParamsForPost(data));
                        dataOutputStream.flush();
                        dataOutputStream.close();
                        outputStream.close();
                    }
                    httpURLConnection.connect();

                    InputStream inputStream = httpURLConnection.getInputStream();
                    response = convertStreamToString(new InputStreamReader(inputStream, "utf-8"));
                    inputStream.close();



                    if (onConnectListener != null && !response.equals(NULL_REQUEST)) {
                        httpURLConnection.disconnect();
                        onConnectListener.onSuccessConnect(response,httpURLConnection.getResponseCode());
                        return;
                    }
                    //沒有回傳的話設下計時器 等時間到再次發起連線直到累積次數達到MAX_RETRY_TIME
                    setTimerTask();


                } catch (MalformedURLException e) {
                    Log.e(TAG, "MalformedURLException: " + e.getMessage());
                } catch (ProtocolException e) {
                    Log.e(TAG, "ProtocolException: " + e.getMessage());
                } catch (IOException e) {
                    Log.e(TAG, "IOException: " + e.getMessage());
                } catch (Exception e) {
                    Log.e(TAG, "Exception: " + e.getMessage());
                }
            }
        });
        connectThread.start();


    }

    //-----

    /***
     * 用來計時timeout
     */
    private void setTimerTask() {
        removeTimer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {

                retryTime++;
                Log.w(TAG, RETRY_TIME + retryTime);
                Message msg = handler.obtainMessage();
                msg.arg1 = 1;
                handler.sendMessage(msg);
            }
        };
        timer = new Timer();
        timer.schedule(task, TIME_OUT, TIME_OUT);
    }
    //---------------------

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.arg1 == 1) {
                if (retryTime <= MAX_RETRY_TIME) {
                    connect(requestMethod, urlString, data);
                }
                //重新連線滿五次
                else {
                    removeTimer();
                    if (onConnectListener != null) {
                        onConnectListener.onFailedConnect();
                    }
                }
            }
        }
    };

    //---------------------

    /**
     * 移除 timer
     */
    private void removeTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

    //-----

    /**
     * Post 用 ->放入參數
     *
     * @param data
     * @return
     */
    private String putParamsForPost(Map<String, String> data) {
        JSONObject jsonObject = new JSONObject();
        try {
            for (String key : data.keySet()) {
                jsonObject.put(key, data.get(key));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return jsonObject.toString();
    }

    //---------------------------------------------------------------------------

    /***
     *  Get 用 ->放入參數
     * @param url
     * @param data
     * @return
     */
    private String putParamsForGet(String url, Map<String, String> data) {
        StringBuilder stringBuilder = new StringBuilder(url + "?");
        int index = 0;
        try {
            for (String key : data.keySet()) {
                String appendStr = index == data.size() - 1 ? key + "=" + data.get(key) : key + "=" + data.get(key) + "&";
                stringBuilder.append(appendStr);
                index++;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stringBuilder.toString();
    }


    //---------------------------------------------------------------------------

    /***
     * 將server端回傳 一行一行加入並換行 直到沒有
     * @param inputStreamReader
     * @return
     */
    private String convertStreamToString(InputStreamReader inputStreamReader) {
        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
                inputStreamReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    //-----
    public interface OnConnectListener {
        void onSuccessConnect(String response,int responseCode);

        void onFailedConnect();
    }

    public void setOnConnectListener(OnConnectListener onConnectListener) {
        this.onConnectListener = onConnectListener;
    }
}
