package jp.ac.titech.itpro.sdl.bthideandseek;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by fuyu_suzuki on 2017/07/13.
 */

public class BluetoothClientThread extends Thread {
    //クライアント側の処理
    private final BluetoothSocket clientSocket;
    private final BluetoothDevice mDevice;
    private Context mContext;
    private TextView textView;
    //UUIDの生成
    public static final UUID TECHBOOSTER_BTSAMPLE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    static BluetoothAdapter myClientAdapter;
    public String myNumber;

    //コンストラクタ定義
    public BluetoothClientThread(Context context ,String myNum, BluetoothDevice device, BluetoothAdapter btAdapter){
        //各種初期化
        mContext = context;
        BluetoothSocket tmpSock = null;
        mDevice = device;
        myClientAdapter = btAdapter;
        myNumber = myNum;

        try{
            //自デバイスのBluetoothクライアントソケットの取得
            tmpSock = device.createRfcommSocketToServiceRecord(TECHBOOSTER_BTSAMPLE_UUID);
        }catch(IOException e){
            e.printStackTrace();
        }
        clientSocket = tmpSock;
    }

    public void run(){
        //接続要求を出す前に、検索処理を中断する。

        if(myClientAdapter.isDiscovering()){
            myClientAdapter.cancelDiscovery();
        }
        try{
            //サーバー側に接続要求
            clientSocket.connect();
        }catch(IOException e){
            try {
                clientSocket.close();
            } catch (IOException closeException) {
                e.printStackTrace();
                String msg2 =  "鬼は遠くにいます";
                Toast.makeText(mContext, msg2, Toast.LENGTH_LONG).show();
            }
            return;
        }

        //接続完了時の処理
      // ReadWriteModel rw = new ReadWriteModel(mContext, clientSocket, myNumber);
        //rw.start();
        System.out.println("接続完了");
    }

    public void cancel() {
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

