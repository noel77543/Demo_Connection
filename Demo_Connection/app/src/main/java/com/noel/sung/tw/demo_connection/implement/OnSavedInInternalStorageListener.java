package com.noel.sung.tw.demo_connection.implement;

/**
 * Created by noel on 2018/4/27.
 */

public interface OnSavedInInternalStorageListener {
    //當成功下載並儲存至內部資料夾

    /***
     * @param responseCode 連線狀態碼
     * @param absolutePath 絕對路徑 內部資料夾/fileName.xxx
     * @param fileName     檔名.xxx
     */
    void onSuccessSaved(int responseCode, String absolutePath, String fileName);

    //當無法下載
    void onFailSaved(int responseCode);
}
