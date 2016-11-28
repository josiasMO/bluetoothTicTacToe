package com.project.josias.bluetoothtictactoe;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainBluetoothActivity extends AppCompatActivity {

    public static int ENABLE_BLUETOOTH = 1;
    public static int SELECT_PAIRED_DEVICE = 2;
    public static int SELECT_DISCOVERED_DEVICE = 3;

    static TextView statusMessage;
    static TextView textSpace;

    static ConnectionThread connect;

    static Resources res;
    static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_bluetooth);



        statusMessage = (TextView) findViewById(R.id.statusMessage);

        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            statusMessage.setText(getString(R.string.bluetoothFail));
        } else {
            statusMessage.setText(getString(R.string.bluetoothOk));
            if(!btAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH);
                statusMessage.setText(getString(R.string.bluetoothSolicitation));
            } else {
                statusMessage.setText(getString(R.string.bluetoothActive));
                waitConnection();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_bluetooth, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == ENABLE_BLUETOOTH) {
            if(resultCode == RESULT_OK) {
                statusMessage.setText(getString(R.string.bluetoothActivated));
            }
            else {
                statusMessage.setText(getString(R.string.bluetoothNotActivated));
            }
        }
        else if(requestCode == SELECT_PAIRED_DEVICE || requestCode == SELECT_DISCOVERED_DEVICE) {
            if(resultCode == RESULT_OK) {
                statusMessage.setText(getString(R.string.selected)+ data.getStringExtra("btDevName") + "\n"
                                        + data.getStringExtra("btDevAddress"));

                connect = new ConnectionThread(data.getStringExtra("btDevAddress"));
                connect.start();
            }
            else {
                statusMessage.setText(getString(R.string.bluetoothNoDevice));
            }
        }
    }

    public void searchPairedDevices(View view) {

        Intent searchPairedDevicesIntent = new Intent(this, PairedDevices.class);
        startActivityForResult(searchPairedDevicesIntent, SELECT_PAIRED_DEVICE);
    }

    public void discoverDevices(View view) {
        Intent intentOpenBluetoothSettings = new Intent();
        intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
        startActivity(intentOpenBluetoothSettings);

    }

    public void enableVisibility(View view) {

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 30);
        startActivity(discoverableIntent);
    }

    public void waitConnection() {
        connect = new ConnectionThread();
        connect.start();
    }

    public static ConnectionThread getInstance() {
        return connect;
    }
    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            Bundle bundle = msg.getData();
            byte[] data = bundle.getByteArray("data");
            String dataString= new String(data);

            if(dataString.equals("---N")) {
                statusMessage.setText("Failed to connect!");
            }
            else if(dataString.equals("---S")) {
                Intent intent = new Intent(mContext, MainActivity.class);
                ConnectionThread.config = false;
                mContext.startActivity(intent);
            }


        }
    };

}
