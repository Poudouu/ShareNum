package com.example.arlau.sharenum;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.UUID;


import static android.bluetooth.BluetoothDevice.CREATOR;


public class MainActivity extends AppCompatActivity {

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    BluetoothAdapter btAdapter;
    BluetoothDevice remoteDevice;
    BluetoothDevice initDevice;
    boolean flag_bt_on = false;
    String toastText = "";
    String DiscoveredDevicesName[] = {"", "", "", "", "", "", "", "", "", ""};
    BluetoothDevice DiscoveredDevices0 = null;
    BluetoothDevice DiscoveredDevices1 = null;
    BluetoothDevice DiscoveredDevices2 = null;
    BluetoothDevice DiscoveredDevices3 = null;
    protected static final int SUCCESS_CONNECT = 0;
    protected static final int MESSAGE_READ = 1;
    int i = 0;
    Handler mhandler = new Handler() {
       public void handleMessage(Message msg) {
           super.handleMessage(msg);
           switch (msg.what) {
               case SUCCESS_CONNECT:
                   Toast.makeText(MainActivity.this, "CONNECT", Toast.LENGTH_SHORT).show();
                   Intent i = new Intent(Intent.ACTION_SEND);
                   i.setType("text/plain");
                   File f = new File (Environment.getExternalStorageDirectory(), "DCIM/test_file.txt");
                   i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
                   PackageManager pm = getPackageManager();
                   List<ResolveInfo> appsList=pm.queryIntentActivities(i, 0);

                   if (appsList.size()>0){
                       String packageName=null;
                       String className=null;
                       boolean found=false;

                       for (ResolveInfo info:appsList){
                           packageName=info.activityInfo.packageName;
                           if(packageName.equals("com.android.bluetooth")){
                               className=info.activityInfo.name;
                               found=true;
                               break;
                           }
                       }
                       if(!found){
                           Toast.makeText(MainActivity.this, "Bluetooth haven't been found", Toast.LENGTH_SHORT).show();
                       }else{
                           i.setClassName(packageName, className);
                           startActivity(i);
                       }
                   }
                   break;
               case MESSAGE_READ:
                   byte[] readbuf=(byte[])msg.obj;
                   String r=new String(readbuf);
                   Toast.makeText(MainActivity.this, r, Toast.LENGTH_SHORT).show();
                   break;
           }
       }
   };

    BroadcastReceiver bluetoothState = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String prevStateExtra = BluetoothAdapter.EXTRA_CONNECTION_STATE;
            String stateExtra = BluetoothAdapter.EXTRA_STATE;
            int state = intent.getIntExtra(stateExtra, -1);
            //int previousState=intent.getIntExtra(prevStateExtra,-1);
            switch (state) {
                case (BluetoothAdapter.STATE_TURNING_ON): {
                    toastText = "Bluetooth turning ON";
                    break;
                }
                case (BluetoothAdapter.STATE_ON): {
                    toastText = "Bluetooth ON";
                    flag_bt_on = check_bluetooth_status();
                    break;
                }
                case (BluetoothAdapter.STATE_TURNING_OFF): {
                    toastText = "Bluetooth turning OFF";
                    break;
                }
                case (BluetoothAdapter.STATE_OFF): {
                    toastText = "Bluetooth OFF";
                    flag_bt_on = check_bluetooth_status();
                    i = 0;
                    break;
                }
            }
            Toast.makeText(MainActivity.this, toastText, Toast.LENGTH_SHORT).show();
        }
    };
    private int DISCOVERY_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        flag_bt_on = check_bluetooth_status();
    }

    public boolean check_bluetooth_status() {
        Boolean bt_status = false;
        Button Connect = (Button) findViewById(R.id.bt_connect);
        Button Disconnect = (Button) findViewById(R.id.bt_disconnect);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        TextView statusUpdate = (TextView) findViewById(R.id.bt_status);
        try {
            bt_status = btAdapter.isEnabled();
        } catch (java.lang.NullPointerException e) {
            e.printStackTrace();
            statusUpdate.setText("Bluetooth not supported by the device!");
        }
        if (bt_status) {
            Connect.setVisibility(View.GONE);
            Disconnect.setVisibility(View.VISIBLE);
            String address = btAdapter.getAddress();
            String name = btAdapter.getName();
            String statusText = name + ":" + address;
            statusUpdate.setText(statusText);
        } else {
            Connect.setVisibility(View.VISIBLE);
            Disconnect.setVisibility(View.GONE);
            statusUpdate.setText("Bluetooth is not ON");
        }
        return bt_status;
    }


    public void fct_connect_bt(View vieuw) {
        //String actionStateChanged=BluetoothAdapter.ACTION_STATE_CHANGED;
        //String actionRequestEnable=BluetoothAdapter.ACTION_REQUEST_ENABLE;
        //IntentFilter filter=new IntentFilter(actionStateChanged);
        //registerReceiver(bluetoothState, filter);
        //startActivityForResult(new Intent(actionRequestEnable), 0);

        String scanModeChanged = BluetoothAdapter.ACTION_SCAN_MODE_CHANGED;
        String beDiscoverable = BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE;
        IntentFilter filter = new IntentFilter(scanModeChanged);
        registerReceiver(bluetoothState, filter);
        startActivityForResult(new Intent(beDiscoverable), DISCOVERY_REQUEST);

    }

    public void fct_disconnect_bt(View vieuw) {
        btAdapter.disable();
        flag_bt_on = check_bluetooth_status();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DISCOVERY_REQUEST) {
            Toast.makeText(MainActivity.this, "Discovery in progress", Toast.LENGTH_SHORT).show();
            flag_bt_on = check_bluetooth_status();
            findDevices();
        }
    }

    public void fct_connect_to_device_1(View vieuw) {
        ConnectThread Connect_to_device1 = new ConnectThread(DiscoveredDevices0);
        Connect_to_device1.run();
    }

    private void findDevices() {
        String LastUsedRemoteDevice = getLastUsedRemoteBTDDevice();
        if (LastUsedRemoteDevice != null) {
            toastText = "Checking for known paired devices, namely:" + LastUsedRemoteDevice;
            Toast.makeText(MainActivity.this, toastText, Toast.LENGTH_LONG).show();
            Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
            for (BluetoothDevice pairedDevice : pairedDevices) {
                toastText = "Found devices: " + pairedDevice.getName() + "@" + LastUsedRemoteDevice;
                Toast.makeText(MainActivity.this, toastText, Toast.LENGTH_SHORT).show();
                remoteDevice = pairedDevice;
            }
        }
        if (remoteDevice == null) {
            toastText = "Starting discovery for remote devices...";
            Toast.makeText(MainActivity.this, toastText, Toast.LENGTH_SHORT).show();
            if (btAdapter.startDiscovery()) {
                toastText = "Discovery thread started... Scanning for devices";
                Toast.makeText(MainActivity.this, toastText, Toast.LENGTH_SHORT).show();
                registerReceiver(discoveryResult, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            }
        }
    }

    BroadcastReceiver discoveryResult = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Toast.makeText(MainActivity.this, "New Device = " + device.getName(), Toast.LENGTH_SHORT).show();
                DiscoveredDevicesName[i] = device.getName();
                switch (i) {
                    case 0: {
                        DiscoveredDevices0 = device;
                        break;
                    }
                    case 1: {
                        DiscoveredDevices1 = device;
                        break;
                    }
                    case 2: {
                        DiscoveredDevices2 = device;
                        break;
                    }
                    case 3: {
                        DiscoveredDevices3 = device;
                        break;
                    }
                }
            }


            updateButtonText();
        }

        public void updateButtonText() {
            switch (i) {
                case 0: {
                    TextView device1 = (TextView) findViewById(R.id.accessible_device1);
                    device1.setText(DiscoveredDevicesName[0]);
                    break;
                }
                case 1: {
                    TextView device1 = (TextView) findViewById(R.id.accessible_device2);
                    device1.setText(DiscoveredDevicesName[1]);
                    break;
                }
            }
        }
    };

    private String getLastUsedRemoteBTDDevice() {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        String result = prefs.getString("LAST_REMOTE_DEVICES_ADDRESS", null);
        return result;
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;
            mmDevice = device;
            try {
                Method m = device.getClass().getMethod("createRfcommSocket", new Class[] { int.class });
                tmp = (BluetoothSocket) m.invoke(device, 1);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            mmSocket = tmp;
        }

        public void run() {
            btAdapter.cancelDiscovery();
            try {
                mmSocket.connect();
                mhandler.obtainMessage(SUCCESS_CONNECT, mmSocket).sendToTarget();
            } catch (IOException connectException) {
                Toast.makeText(MainActivity.this, "Couldn't connect to socket...", Toast.LENGTH_SHORT).show();
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                }
                return;
            }

        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }

    }
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Toast.makeText(MainActivity.this, "Error_with_socket configuration", Toast.LENGTH_SHORT).show();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity
                    mhandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Toast.makeText(MainActivity.this, "Error while writing the datas in the socket", Toast.LENGTH_SHORT).show();
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }

}
