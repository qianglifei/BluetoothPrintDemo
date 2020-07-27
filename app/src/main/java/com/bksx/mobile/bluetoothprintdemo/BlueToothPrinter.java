package com.bksx.mobile.bluetoothprintdemo;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import java.util.Iterator;
import java.util.Set;

import cn.com.itep.cpclprint.GloableConstant;
import cn.com.itep.cpclprint.StartCPCLPrintApi;
import cn.com.itep.cpclprint.impl.StartCPCLPrintApiImpl;


public class BlueToothPrinter {

    public static BluetoothDevice printer;
    public static Context printer_context;
    private StartCPCLPrintApi printApiInstance;
    private Handler handler;
    private CustomDialog mDialogWaiting;
    private String name = "";
    public void printQRCode(Context context, final String house_id, final String height, final String width, String zzmp_xm){
        printer_context = context;
        name = zzmp_xm;
        printApiInstance = StartCPCLPrintApiImpl.getInstance(context);
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> bluetoothDeviceSet = bluetoothAdapter.getBondedDevices();
        if (bluetoothDeviceSet.size()>0) {
            for(Iterator<BluetoothDevice> iterator = bluetoothDeviceSet.iterator(); iterator.hasNext();){
                BluetoothDevice bluetoothDevice = iterator.next();
                Log.i("bluetooth",bluetoothDevice.getName());
                if (bluetoothDevice.getName().startsWith("START-M22")){
                    printer = bluetoothDevice;
                    break;
                }
            }
        }else {
            showTipMsg("请先到蓝牙设置中配对");
            printApiInstance.START_stopFindPrinter();
            return;
        }
        Log.i("object",printApiInstance.toString());
        handler = new Handler(Looper.getMainLooper());
        showWaitingDialog("正在连接，请稍等...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                //停止搜索设备
                printApiInstance.START_stopFindPrinter();
                //根据搜索打印设备类型，连接设备
                final String[] strings = printApiInstance.START_printerConnect(printer);
                if (strings[0].equals("0")) {
//                    printApiInstance.START_initPrinter("ZPLC", PageConfig.A4);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            printApiInstance.PrintAreaSize("0", "180", "100", "700", "1");
//                            printApiInstance.Note("注释:QR码");
                            printApiInstance.Text(GloableConstant.TEXT90,"0","1","420","220",name);
                            //疫贯通 现用设备 s2 为55
                            printApiInstance.PrintQR(GloableConstant.BARCODE, "120", "220", "2", "8", house_id);
//                            printApiInstance.PrintQR(GloableConstant.BARCODE,"0","0","2","15","zzt-d5c20313f61e42a7bd506bad3daa87d1");
//                            printApiInstance.Note("注释:打印字符串");
                            //printApiInstance.Text(GloableConstant.TEXT,"0","0","10","300","START123456");
                            int result = printApiInstance.Print();
                            try {
                                if (result > 0) {
                                    showTipMsg("打印成功");
                                    printApiInstance.START_printerDisconnect();
                                } else {
                                    printApiInstance.START_printerDisconnect();
                                    showTipMsg("连接错误，打印失败，请稍后再试");
                                }
                            }catch (Exception e){
                                showTipMsg("打印失败，请稍后再试");
                                printApiInstance.START_printerDisconnect();
                            }

                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            showTipMsg(strings[1]);
                        }
                    });
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        hideWaitingDialog();
                    }
                });
            }
        }).start();
    }

    /**
     * 显示等待提示框
     */
    public Dialog showWaitingDialog(String tip) {
        hideWaitingDialog();
        View view = View.inflate(printer_context, R.layout.dialog_waiting, null);
        if (!TextUtils.isEmpty(tip))
            ((TextView) view.findViewById(R.id.tvTip)).setText(tip);
        mDialogWaiting = new CustomDialog(printer_context, view, R.style.NewMyDialog);
        mDialogWaiting.show();
        mDialogWaiting.setCancelable(false);
        return mDialogWaiting;
    }

    /**
     * 隐藏等待提示框
     */
    public void hideWaitingDialog() {
        if (mDialogWaiting != null) {
            mDialogWaiting.dismiss();
            mDialogWaiting = null;
        }
    }

    public void showTipMsg(String msg){
        Toast.makeText(printer_context,msg,Toast.LENGTH_LONG).show();
    }

}
