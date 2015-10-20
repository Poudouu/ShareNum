package com.example.arlau.sharenum;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    BluetoothAdapter btAdapter;
    boolean flag_bt_on = false;
    boolean bt_was_on_at_startup=false;
    private int DISCOVERY_REQUEST = 1;
    CountDownTimer Count;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        flag_bt_on = check_bluetooth_status();
        bt_was_on_at_startup=flag_bt_on;

    }

    public void fct_register_infos(View vieuw){
        Intent intent = new Intent(this, ContactInfos.class);
        startActivity(intent);
    }

    public boolean check_bluetooth_status() {
        Boolean bt_status = false;
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        TextView statusUpdate = (TextView) findViewById(R.id.bt_status);
        try {
            bt_status = btAdapter.isEnabled();
        } catch (java.lang.NullPointerException e) {
            e.printStackTrace();
            statusUpdate.setText("Bluetooth not supported by the device!!!");
        }
        if (bt_status) {
            statusUpdate.setText("");
        } else {
            statusUpdate.setText("");
        }
        return bt_status;
    }


    public void send_file_in_bt(View vieuw) {

        if(!flag_bt_on) {
            String beDiscoverable = BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE;
            startActivityForResult(new Intent(beDiscoverable), DISCOVERY_REQUEST);
        }
        flag_bt_on = check_bluetooth_status();

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
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

        Count = new CountDownTimer(3000, 500) {
            // Action to check at every tic
            public void onTick(long millisUntilFinished) {

            }
            // Reset of the game when the timeout goes to 0
            public void onFinish() {
                btAdapter.disable();
            }
        };

    }
}
