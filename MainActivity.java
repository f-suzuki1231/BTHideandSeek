package jp.ac.titech.itpro.sdl.bthideandseek;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.widget.TextView;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter myBluetoothAdapter;
    private TextView textView;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView1);

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
                //ディバイスを見つけて調査
                discoverDevices();
            }else{
                textView.append("Bluetoothを利用できません。\n");
                //Bluetoothを使えるようにする処理を書いたりする。
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
        textView.append("\n登録済みディバイス\n");

    Set<BluetoothDevice> pairedDevices = myBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                textView.append("ディバイス名：" + device.getName() + "\n");
                textView.append("アドレス：" + device.getAddress() + "\n");
                textView.append("クラス：" + device.getBluetoothClass() + "\n");
            }
        } else {
            textView.append("登録されているBluetoothデバイスはありません。\n");
        }
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
        textView.append("\nディバイスを検索中\n");
    }
}
