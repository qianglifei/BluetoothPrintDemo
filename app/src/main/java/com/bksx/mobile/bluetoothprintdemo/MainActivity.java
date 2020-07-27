package com.bksx.mobile.bluetoothprintdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.moible.bksx.xcb.granor.PermissionListener;
import com.moible.bksx.xcb.granor.PermissionsUtil;

import org.json.JSONObject;

import javax.xml.datatype.Duration;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Context mContext = this;
    private int REQUEST_ENABLE_BT = 100;
    private static final String TAG = "MainActivity";
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    private void initView() {
        Button buttonPrint = findViewById(R.id.button_print);
        buttonPrint.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId){
            case R.id.button_print:{
                Log.i(TAG, "===onClick: " + "打印");
                //加载动态授权
                PermissionsUtil.requestPermission(mContext, new PermissionListener() {
                    @Override
                    public void permissionGranted(String... permission) {
                        startScan();
                    }

                    @Override
                    public void permissionDenied(String... permission) {
                        Toast.makeText(mContext,"权限未打开", Toast.LENGTH_LONG);
                    }
                },PERMISSIONS_STORAGE);

            }
            default: break;
        }
    }


    private void startScan() {
        printEwm();
    }

    private void printEwm() {
        JSONObject jsonObject = new JSONObject();
        String strBase64 = "";
        try {
            jsonObject.put("zzmp_zjhm", "哎呀妈呀");
            strBase64 = Base64Utils.encodeToString(jsonObject.toString().trim());
            BlueToothPrinter mBlueToothPrinter = new BlueToothPrinter();
            mBlueToothPrinter.printQRCode(mContext, strBase64.trim(), "0", "0","哎呀妈呀");
        } catch (Exception e) {
            Log.e(TAG, "printEwm: ", e);
        }
    }
}
