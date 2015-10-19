package com.example.arlau.sharenum;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ContactInfos extends AppCompatActivity {
    File file;


    @SuppressLint("MissingSuperCall")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_infos);
    }

    public void register_infos(View vieuw){
        EditText FirstName;
        EditText LastName;
        EditText Org;
        EditText PhoneNum;
        EditText Email;

        FirstName = (EditText)findViewById(R.id.firstName);
        LastName = (EditText)findViewById(R.id.lastName);
        Org = (EditText)findViewById(R.id.orgName);
        PhoneNum = (EditText)findViewById(R.id.phoneNum);
        Email = (EditText)findViewById(R.id.email);

        String firstName = FirstName.getText()+"";
        String lastName = LastName.getText()+"";
        String orgName = Org.getText()+"";
        String phoneNum = PhoneNum.getText()+"";
        String email = Email.getText()+"";

        String infosStringWrite="BEGIN:VCARD\n" +
                "VERSION:3.0\n" +
                "FN:"+firstName+" "+lastName+"\n" +
                "ORG:"+orgName+"\n"+
                "N:"+lastName+";"+firstName+"\n" +
                "TEL;CELL:"+phoneNum+"\n" +
                "EMAIL;INTERNET:"+email+"\n" +
                "UID:\n" +
                "END:VCARD";

        FileOutputStream fileOutputStream = null;
        try {
            file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "contact_info1.vcf");
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(infosStringWrite.getBytes());
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finish();
    }

}
