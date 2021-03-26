package com.example.saleservice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class usermgmt extends AppCompatActivity {
    private TextView email;
    private LinearLayout createuser;
    private LinearLayout showalluser;
    private LinearLayout deleteuser;
    private LinearLayout getdata;
    private LinearLayout resetpass;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewsetup();
    }
    public void viewsetup()
    {
        setContentView(R.layout.activity_usermgmt);
        email = findViewById(R.id.usershowemail);
        createuser = findViewById(R.id.llcreateuser);
        showalluser = findViewById(R.id.llshowuser);
        deleteuser = findViewById(R.id.lldeleteuser);
        getdata = findViewById(R.id.llgetuserdata);
        resetpass = findViewById(R.id.llresetuserpass);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        email.setText(firebaseUser.getEmail());
        createuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(usermgmt.this,createuser.class));
            }
        });
        showalluser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(usermgmt.this, com.example.saleservice.showalluser.class));
            }
        });
        deleteuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(usermgmt.this,com.example.saleservice.deleteuser.class));
            }
        });
        getdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(usermgmt.this,dwnlduserdata.class));
            }
        });
        resetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(usermgmt.this,resetuserpass.class));
            }
        });
    }
}
