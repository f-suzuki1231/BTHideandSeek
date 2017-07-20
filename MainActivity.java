package jp.ac.titech.itpro.sdl.bthideandseek;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.ArrayList;
import java.util.Set;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Toast;
import android.view.View;

import static jp.ac.titech.itpro.sdl.bthideandseek.R.id.parent;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";
    private BluetoothAdapter myBluetoothAdapter;
    private TextView textView;
    private ListView listView;
    private ArrayList<BluetoothDevice> foundDeviceList = new ArrayList<BluetoothDevice>();
    static int offSet = 0;


    private final static String KEY_DEVLIST = "MainActivity.devList";
    private ProgressBar scanProgress;

    private final static int REQUEST_ENABLE_BT = 1111;
    private final static int REQCODE_PERMISSIONS = 2222;
    private final static String[] PERMISSIONS = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    public Context mContext;
    public String myNumber="0";
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        textView = (TextView) findViewById(R.id.textView1);
        //ListView を取得
        listView = (ListView) findViewById(R.id.listView1);

        //ListView に表示する項目設定


        // Bluetooth
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (myBluetoothAdapter == null) {
            textView.append("Bluetoothをサポートしていません。\n");
        } else {
            textView.append("Bluetoothをサポートしています。\n");
            //Bluetoothが利用できるか確認する
            if (myBluetoothAdapter.isEnabled()) {
                textView.append("Bluetoothを利用できます。\n");
                //自機ディバイスの調査
                getLocalInformation();
                //ペアリング済みのディバイスの調査
                findPairedDevices();
                click();

                //ディバイスを見つけて調査
                discoverDevices();
            }else{
                textView.append("Bluetoothを利用できません。\n");
                //Bluetoothを使えるようにする処理を書いたりする。
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            }
        }
    }

// 自機ディバイスの調査メソッド

    private void getLocalInformation() {
        // TODO 自動生成されたメソッド・スタブ
        textView.append("自機Bluetoothディバイスの調査\n");
        textView.append("ディバイス名:" + myBluetoothAdapter.getName() + "\n");
        textView.append("アドレス:" + myBluetoothAdapter.getAddress() + "\n");

        textView.append("SCAN_MODE:");
        switch (myBluetoothAdapter.getScanMode()) {
            case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                textView.append("SCAN_MODE_CONNECTABLE" + "\n");
                break;
            case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                textView.append("SCAN_MODE_CONNECTABLE_DISCOVERABLE" + "\n");
                break;
            case BluetoothAdapter.SCAN_MODE_NONE:
                textView.append("SCAN_MODE_NONE" + "\n");
                break;
        }

        textView.append("STATE:");
        switch (myBluetoothAdapter.getState()) {
            case BluetoothAdapter.STATE_OFF:
                textView.append("STATE_OFF\n");
                break;
            case BluetoothAdapter.STATE_ON:
                textView.append("STATE_ON\n");
                break;
            case BluetoothAdapter.STATE_TURNING_OFF:
                textView.append("STATE_TURNING_OFF\n");
                break;
            case BluetoothAdapter.STATE_TURNING_ON:
                textView.append("STATE_TURNING_ON\n");
                break;
        }
    }

    // ペアリング済みの調査
    private void findPairedDevices() {

        //接続履歴のあるデバイスを取得
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        //BluetoothAdapterから、接続履歴のあるデバイスの情報を取得
        Set<BluetoothDevice> pairedDevices = myBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0){
            //接続履歴のあるデバイスが存在する
            for(BluetoothDevice device:pairedDevices){
                //接続履歴のあるデバイスの情報を順に取得してアダプタに詰める
                //getName()・・・デバイス名取得メソッド
                //getAddress()・・・デバイスのMACアドレス取得メソッド
                adapter.add(device.getName() + "\n" + device.getAddress()+ "\n");
                foundDeviceList.add(device);
                offSet++;
            }

            listView.setAdapter(adapter);
        }

    }
    //ListViewのクリック処理
    private void click(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                BluetoothDevice device = foundDeviceList.get(position);
                BluetoothClientThread BtClientThread = new BluetoothClientThread(mContext, myNumber, device, myBluetoothAdapter);
                BtClientThread.start();
                    String msg1 = listView.getItemAtPosition(position) + "が近くに来ています";

                    Toast.makeText(getApplicationContext(), msg1, Toast.LENGTH_LONG).show();

                

            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
        //サーバースレッド起動、クライアントのからの要求待ちを開始
        BluetoothServerThread BtServerThread = new BluetoothServerThread(this ,myNumber, myBluetoothAdapter);
        BtServerThread.start();

    }



// 他のBluetoothディバイスを見つけるレシーバーを登録

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                textView.append("ディバイス名: " + device.getName() + "\n");
                textView.append("アドレス: " + device.getAddress() + "\n");
                short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
                textView.append("RSSI: " + rssi + "dBm" + "\n");
            }
        }
    };


    // ディバイスを見つける
    private void discoverDevices() {
        // TODO 自動生成されたメソッド・スタブ

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy

        myBluetoothAdapter.startDiscovery();
       // textView.append("\nディバイスを検索中\n");
    }


}
