package com.example.arlau.sharenum;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class ContactInfos extends AppCompatActivity {
    File file;

    @SuppressLint("MissingSuperCall")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_infos);

        read_infos();
    }

    public void register_infos(View vieuw){
        FileOutputStream fileOutputStream = null;
        file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "contact_info1.vcf");

        //Get values in the corresponding EditText fields
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

        //Create the VCARD formated string to write in the file
        String infosStringWrite="BEGIN:VCARD\n" +
                "VERSION:3.0\n" +
                "FN:"+firstName+" "+lastName+"\n" +
                "ORG:"+orgName+"\n"+
                "N:"+lastName+";"+firstName+"\n" +
                "TEL;CELL:"+phoneNum+"\n" +
                "EMAIL;INTERNET:"+email+"\n" +
                "UID:\n" +
                "END:VCARD";

        //Write the VCARD string in the file
        try {
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(infosStringWrite.getBytes());
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(ContactInfos.this, "Datas saved.", Toast.LENGTH_SHORT).show();
    }

    public void read_infos(){
        FileInputStream fileInputStream=null;
        byte [] inputBuffer = new byte[1024];
        file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "contact_info1.vcf");
        String firstName="";
        String lastName="";
        String org ="";
        String phone ="";
        String email ="";

        //Read file infos
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
            //Parse string infos
            String infoStringRead = new String(inputBuffer, "UTF-8");
            String delims = "[\n]";
            String[] tokens = infoStringRead.split(delims);
            //Parse name string part
            String delims_1="[:]";
            String [] parseName=tokens[2].split(delims_1);
            String delims_2="[ ]";
            String [] parseName2=parseName[1].split(delims_2);
            firstName=parseName2[0];
            lastName=parseName2[1];
            //Parse org name
            String [] parseOrg=tokens[3].split(delims_1);
            org =parseOrg[1];
            //Parse phone
            String [] parsePhone=tokens[5].split(delims_1);
            phone =parsePhone[1];
            //Parse email
            String [] parseEmail=tokens[6].split(delims_1);
            email =parseEmail[1];
            String Cheval="";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Write values in the file in the correct fields
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

        FirstName.setText(firstName);
        LastName.setText(lastName);
        Org.setText(org);
        PhoneNum.setText(phone);
        Email.setText(email);

    }

    public void close_screen(View vieuw){
        finish();
    }

}
