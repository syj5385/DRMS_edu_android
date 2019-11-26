package com.drms.drmakersystem.Dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.drms.drmakersystem.Adapter.BtListAdapter.Custom3_Item;
import com.drms.drmakersystem.Adapter.BtListAdapter.CustomAdapter3;
import com.drms.drmakersystem.Adapter.CustomAdapter2.Custom2_Item;
import com.drms.drmakersystem.Adapter.CustomAdapter2.CustomAdapter2;
import com.drms.drmakersystem.Communication.BluetoothService;
import com.drms.drmakersystem.FileManagement.FileManagement;
import com.drms.drmakersystem.R;
import com.drms.drmakersystem.View.ExecuteView;
import com.drms.drmakersystem.View.MainView;

import java.util.Set;

import static android.content.ContentValues.TAG;

/**
 * Created by comm on 2018-02-21.
 */

public class MainSettingDialog extends AlertDialog.Builder{

    private static final boolean D = true;

    private FileManagement mFileManagement;

    protected int isDialogOpen = 0;
    public static final int NOT_OPEN = 0;
    public static final int MAIN_SETTING = 1;
    public static final int PAIRED_DEVICE =2;
    public static final int FIND_DEVICE = 3;

    private Context context;
    private Activity activity;

    private LinearLayout dialogView;
    private LinearLayout pairedDeviceView;
    private Handler mHandler;

    // in the dialogView
    private ListView set_content;

    private CustomAdapter2 adapter;

    public MainSettingDialog(Context context, Activity activity, Handler mHandler) {
        super(context);

        this.context = context;
        this.activity = activity;
        this.mHandler = mHandler;

        dialogView = (LinearLayout) View.inflate(context, R.layout.main_setting_adapter,null);
        setView(dialogView);
        set_content = (ListView)dialogView.findViewById(R.id.setting_list);
        adapter = new CustomAdapter2(context);
        adapter.addItem(new Custom2_Item("나의 블루투스"));
        adapter.addItem(new Custom2_Item("새로운 장치 검색하기"));
        adapter.addItem(new Custom2_Item("장치 초기화"));

        TextView mydevice = (TextView)dialogView.findViewById(R.id.myDevice);
        String name = new FileManagement(context).readBTAddress()[0];
        String address = new FileManagement(context).readBTAddress()[1];
        if(name == "" || address == ""){
            mydevice.setText("No Device");
        }
        else{
            mydevice.setText(name + "\n"+ address);
        }
        implementationMainSetting();

        isDialogOpen = MAIN_SETTING;

    }

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
            if(btAdapter.isEnabled()) {
                switch (i) {
                    case 0: // bluetooth Device
                        implementationPairedDeviceList();
                        break;

                    case 1:
                        implementationNewDeviceList();
                        break;

                    case 2:
                        new FileManagement(context, mHandler).writeBtAddressOnFile("", "");
                        Log.d(TAG, "initialize BT Address");
                        TextView mydevice = (TextView) dialogView.findViewById(R.id.myDevice);
                        mydevice.setText("No Device");
                        break;

                    case 3:

                        break;
                }
            }
            else{
                Toast.makeText(context,"블루투스가 활성화 되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
                if(!btAdapter.isEnabled()){
                    Intent bt_intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    activity.startActivityForResult(bt_intent, BluetoothService.REQUEST_ENABLE_BT);
                }
            }
        }
    };

    protected BluetoothAdapter btAdapter;

    private void implementationPairedDeviceList(){
        set_content.setAdapter(null);
        isDialogOpen = PAIRED_DEVICE;

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        CustomAdapter3 pairedAdapter = new CustomAdapter3(context);
        set_content.setAdapter(pairedAdapter);
        set_content.setOnItemClickListener(btItemListener);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

        if(pairedDevices.size() >0 ){
            for(BluetoothDevice device : pairedDevices){
                pairedAdapter.addItem(new Custom3_Item(context.getResources().getDrawable(R.drawable.main_setting_image2),device.getName(), device.getAddress()));
            }
        }
        else{
            pairedAdapter.addItem(new Custom3_Item(context.getResources().getDrawable(R.drawable.main_setting_image3),"No Device",""));
        }
    }

    private CustomAdapter3 newDeviceAdapter;
    private void implementationNewDeviceList(){
        set_content.setAdapter(null);
        isDialogOpen = FIND_DEVICE;
        newDeviceAdapter = new CustomAdapter3(context);
        set_content.setAdapter(newDeviceAdapter);
        set_content.setOnItemClickListener(newbtItemListener);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        context.registerReceiver(mReceiver, filter);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        doDiscovery();

    }

    public void implementationMainSetting(){
        isDialogOpen = MAIN_SETTING;
        set_content.setAdapter(adapter);
        set_content.setOnItemClickListener(itemClickListener);
    }

    public DialogInterface.OnKeyListener keyListener = new DialogInterface.OnKeyListener() {
        @Override
        public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
            if(keyEvent.getAction() == KeyEvent.ACTION_UP){
                if(keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK){
                    Log.d(TAG,"press back key");
                    if(isDialogOpen == MAIN_SETTING){
                        mHandler.obtainMessage(MainView.REQUEST_CLOSE).sendToTarget();
                    }
                    else if(isDialogOpen == PAIRED_DEVICE){
                        implementationMainSetting();
                    }
                    else if(isDialogOpen == FIND_DEVICE){
                        cancelDiscovery();
                        try{
                            Thread.sleep(500);
                        }catch (InterruptedException e){};
                        implementationMainSetting();
                    }
                }
            }
            return true;
        }
    };

    public DialogInterface.OnKeyListener getKeyListener(){
        return keyListener;
    }


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    String name = device.getName();
                    String address = device.getAddress();
//                    if((address.charAt(0) == '9' && address.charAt(1) == '8') || ( address.charAt(0) == '2' &&  address.charAt(1) == '0')) {

                    if(device.getName() != null) {
                        Log.d("BT_FOUND", "Discoverd Device : " + device.getName() + " \n address : " + device.getAddress());
                        boolean alreadyDiscovered = false;

                        if(newDeviceAdapter.getCount() != 0) {
                            for (int i = 0; i < newDeviceAdapter.getCount(); i++) {
                                Custom3_Item temp = (Custom3_Item) newDeviceAdapter.getItem(i);
                                String name_temp = temp.getData()[0];
                                String address_temp = temp.getData()[1];
                                Log.d("BT_FOUND", "nameTemp : " + name_temp + "\naddressTemp : " + address_temp);
                                if (name.equals(name_temp) && address.equals(address_temp)) {
                                    alreadyDiscovered = true;
                                }
                            }
                        }
                        if(alreadyDiscovered){
                            Log.d("BT_FOUND","this device is already discovered");
                        }
                        else {
                            if(newDeviceAdapter.getCount() != 0) {
                                Custom3_Item temp = (Custom3_Item) newDeviceAdapter.getItem(0);
                                if (temp.getData()[0].equals("No device")) {
                                    newDeviceAdapter.removeItem();
                                }
                            }
                            newDeviceAdapter.addItem(new Custom3_Item(context.getResources().getDrawable(R.drawable.main_setting_image2), device.getName(), device.getAddress()));
                        }
//
                    }

                    set_content.setAdapter(newDeviceAdapter);

                }
                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.d("BT_FOUND","finished " + action);
                doDiscovery();
                activity.setProgressBarIndeterminateVisibility(false);
                if (newDeviceAdapter.getCount() == 0) {
                    newDeviceAdapter.addItem(new Custom3_Item(context.getResources().getDrawable(R.drawable.main_setting_image3),"No device",""));
                }
            }
        }
    };

    private int DiscoverCount = 0;

    private void doDiscovery() {
        if (D) Log.d("BT_FOUND", "doDiscovery()");

        // Indicate scanning in the title
        activity.setProgressBarIndeterminateVisibility(true);

        // Turn on sub-title for new devices

        // If we're already discovering, stop it
        if (btAdapter.isDiscovering()) {
            btAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter

        new Thread(new Runnable() {
            @Override
            public void run() {
                btAdapter.startDiscovery();
                DiscoverCount ++;
                if(DiscoverCount > 1){
                    newDeviceAdapter.removeItem();
                    DiscoverCount = 0;
                }
            }
        }).start();


    }

    protected DialogInterface.OnDismissListener dismissListener = new DialogInterface.OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialogInterface) {
            if(isDialogOpen == FIND_DEVICE){
                cancelDiscovery();
            }
        }
    };

    public void setDismissListener(){}

    public DialogInterface.OnDismissListener getDismissListener(){
        return dismissListener;
    }

    private void cancelDiscovery(){
        try {
            activity.unregisterReceiver(mReceiver);
        }catch (Exception e){
            Log.e("BT", "no Registered Receiver");
        }

        btAdapter.cancelDiscovery();
    }

    private AdapterView.OnItemClickListener btItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            cancelDiscovery();
            String name = ((Custom3_Item)adapterView.getAdapter().getItem(i)).getData()[0];
            String Address =  ((Custom3_Item)adapterView.getAdapter().getItem(i)).getData()[1];

            Log.d("BT","Name : " + name + "\n" + "Address : " + Address);

            new FileManagement(context,mHandler).writeBtAddressOnFile(name,Address);
            Toast.makeText(context,"'" + name + "' 등록", Toast.LENGTH_SHORT).show();
            TextView mydevice = (TextView)dialogView.findViewById(R.id.myDevice);
            mydevice.setText(name + "\n" + Address);
            implementationMainSetting();
            mHandler.obtainMessage(ExecuteView.REQUEST_CONTROLLER).sendToTarget();
        }
    };

    private Intent newBtintent;
    private BluetoothService mBluetoothService;

    private AdapterView.OnItemClickListener newbtItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            cancelDiscovery();
            String name = ((Custom3_Item)adapterView.getAdapter().getItem(i)).getData()[0];
            String Address =  ((Custom3_Item)adapterView.getAdapter().getItem(i)).getData()[1];

            Log.d("BT","Name : " + name + "\n" + "Address : " + Address);
//            btAdapter = null;

            mBluetoothService = new BluetoothService(activity,mHandler,"DRS");
            newBtintent = new Intent();
            newBtintent.putExtra(BluetoothService.EXTRA_DEVICE_NAME,name);
            newBtintent.putExtra(BluetoothService.EXTRA_DEVICE_ADDRESS,Address);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mBluetoothService.getDeviceInfo(newBtintent);
                    mHandler.obtainMessage(MainView.TESTING_BT).sendToTarget();
                }
            }).start();

//            new FileManagement(context,mHandler).writeBtAddressOnFile(name,Address);
//            Toast.makeText(context,"'" + name + "' 등록",Toast.LENGTH_SHORT).show();
            implementationMainSetting();
        }
    };

    public void BtConnectionTest(int state){
        if(state == BluetoothService.STATE_CONNECTED){
            if(newBtintent != null){
                String name = newBtintent.getStringExtra(BluetoothService.EXTRA_DEVICE_NAME);
                String Address = newBtintent.getStringExtra(BluetoothService.EXTRA_DEVICE_ADDRESS);
                new FileManagement(context,mHandler).writeBtAddressOnFile(name,Address);
                try{
                    Thread.sleep(1000);
                }catch (InterruptedException e){

                }
                Toast.makeText(context,"'" + name + "' 등록 완료", Toast.LENGTH_SHORT).show();
                TextView mydevice = (TextView)dialogView.findViewById(R.id.myDevice);
                mydevice.setText(name + "\n" + Address);
                implementationMainSetting();

                mBluetoothService.stop();
                mBluetoothService = null;

                implementationMainSetting();
                mHandler.obtainMessage(ExecuteView.REQUEST_CONTROLLER).sendToTarget();
            }
        }
        else if(state == BluetoothService.STATE_FAIL) {
            Toast.makeText(context,"등록 실패", Toast.LENGTH_SHORT).show();
            mBluetoothService.stop();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while(mBluetoothService.getState() != BluetoothService.STATE_CONNECTED ){
                        mBluetoothService = null;
                    }
                }
            });
            implementationMainSetting();

        }

    }

}
