package com.example.saleservice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.saleservice.Fragment.AddFragment;
import com.example.saleservice.Fragment.CreateFragment;
import com.example.saleservice.Fragment.HomeFragment;
import com.example.saleservice.Fragment.ShowFragment;
import com.example.saleservice.Fragment.StockFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        BottomNavigationView bottomNavigationView = findViewById(R.id.navbottom);
        bottomNavigationView.setSelectedItemId(R.id.nvbinv);
        getSupportFragmentManager().beginTransaction().replace(R.id.fraglayout,new AddFragment()).commit();
        checkdir();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedfrag = null;
                switch (item.getItemId()) {
                    case R.id.nvbhome: {
                        selectedfrag = new HomeFragment();
                        break;
                    }
                    case R.id.nvbcreate: {
                        selectedfrag = new CreateFragment();
                        break;
                    }
                    case R.id.nvbshow: {
                        selectedfrag = new ShowFragment();
                        break;
                    }
                    case R.id.nvbinv: {
                        selectedfrag = new AddFragment();
                        break;
                    }
                    case R.id.StockSummary:{
                        selectedfrag = new StockFragment();
                    }
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fraglayout,selectedfrag).commit();
                return true;
            }
        });
    }
    public void checkdir()
    {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.SaleServiceDB/Bookdata";
        String path1 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.SaleServiceDB/Schooldata";
        String path2 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SalesInvoice";
        File dir = new File(path);
        File dir1 = new File(path1);
        File dr2 = new File(path2);
        if(!dir.exists())
            dir.mkdirs();
        if(!dir1.exists())
            dir1.mkdirs();
        if(!dr2.exists())
            dr2.mkdirs();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu,menu);
        menu.removeItem(R.id.mmdelinv);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
//            case R.id.mmdelinv:{
//                Toast.makeText(this, "All Invoice Deleted Successfully", Toast.LENGTH_SHORT).show();
//                break;
//            }
            case R.id.mmresdb:{
                File oldflr = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/.SaleServiceDB","SchoolData");
                Date d = new Date();
                DateFormat dfm = new SimpleDateFormat("ddMMyyyyhhmmss");
                File newflr = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/.SaleServiceDB","SchoolData - "+dfm.format(d));
                boolean success = oldflr.renameTo(newflr);
                if(success) {
                    Toast.makeText(this, "Database Reset Successful", Toast.LENGTH_SHORT).show();
                    checkdir();
                }
                else
                    Toast.makeText(this, "Reset Failed", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.mmresinv:{
                File oldflr = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"SalesInvoice");
                Date d = new Date();
                DateFormat dfm = new SimpleDateFormat("ddMMyyyyhhmmss");
                File newflr = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"SalesInvoice - "+dfm.format(d));
                boolean success = oldflr.renameTo(newflr);
                if(success) {
                    Toast.makeText(this, "Invoice Folder Reset Successful", Toast.LENGTH_SHORT).show();
                    checkdir();
                }
                else
                    Toast.makeText(this, "Reset Failed", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.mnguser:{
                startActivity(new Intent(this,usermgmt.class));
                break;
            }
            case R.id.adminlogout:{
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(this,loginpage.class));
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
