package com.example.arlau.sharenum;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.CharBuffer;
import java.util.List;



public class MainActivity extends AppCompatActivity {

    BluetoothAdapter btAdapter;
    boolean flag_bt_on = false;
    boolean bt_was_on_at_startup=false;
    String toastText = "";
    private int DISCOVERY_REQUEST = 1;
    int i = 0;
    File file;

    OutputStreamWriter osw = null;
    InputStreamReader isr = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        flag_bt_on = check_bluetooth_status();
        bt_was_on_at_startup=flag_bt_on;

        read_infos();
    }

    public void fct_register_infos(View vieuw){
        Intent intent = new Intent(this, ContactInfos.class);
        startActivity(intent);
    }

    public void read_infos(){
        FileInputStream fileInputStream=null;
        byte [] inputBuffer = new byte[1024];
        file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "contact_info1.vcf");
        try {
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(inputBuffer);
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            String infoStringRead = new String(inputBuffer, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    public boolean check_bluetooth_status() {
        Boolean bt_status = false;
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        TextView statusUpdate = (TextView) findViewById(R.id.bt_status);
        try {
            bt_status = btAdapter.isEnabled();
        } catch (java.lang.NullPointerException e) {
            e.printStackTrace();
            statusUpdate.setText("Bluetooth not supported by the device!");
        }
        if (bt_status) {
            statusUpdate.setText("Bluetooth is activated");
        } else {
            statusUpdate.setText("Bluetooth is not activated");
        }
        return bt_status;
    }


    public void send_file_in_bt(View vieuw) {

        if(!flag_bt_on) {
            String actionRequestEnable=BluetoothAdapter.ACTION_REQUEST_ENABLE;
            startActivityForResult(new Intent(actionRequestEnable), 0);
        }
        flag_bt_on = check_bluetooth_status();

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        //i.setType("image/jpeg");
        File f = new File(Environment.getExternalStorageDirectory(), "contact_info1.vcf");
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

        if(!bt_was_on_at_startup){
            fct_disconnect_bt();
        }

    }
    public void fct_disconnect_bt() {
        btAdapter.disable();
    }
}
