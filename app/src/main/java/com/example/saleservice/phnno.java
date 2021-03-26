package com.example.saleservice;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.saleservice.helper.phnnohelper;
import com.example.saleservice.helper.schoolmgmthelper;


import java.util.ArrayList;
import java.util.List;

public class phnno extends AppCompatActivity {
    private Spinner showschool;
    private Button display;
    private schoolmgmthelper schoolhelp;
    private List<String> schoolnlist;
    private Integer schname;
    private phnnohelper phelp;
    private List<String> phnum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phnno);
//        SQLiteDatabase.loadLibs(this);
        showschool = findViewById(R.id.spshowcnctschool);
        display = findViewById(R.id.btshowcnct);
        schoolhelp = new schoolmgmthelper(phnno.this);
//        schoolmgmthelper.getInstance(phnno.this);
        schoolnlist = new ArrayList<>();
        schoolsetup();
        display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showschool.getSelectedItemPosition() != 0) {
                    schname = schoolhelp.getid(showschool.getSelectedItem().toString());
                    show();
                }
            }
        });
    }
    public void show()
    {
        phnum = new ArrayList<>();
        phelp = new phnnohelper(phnno.this,"PhnoSchool"+schname);
//        phnnohelper.getInstance(phnno.this,"PhnoSchool"+schname);
        setContentView(R.layout.samplelistview);
        ListView listView = findViewById(R.id.splistview);
        try {
            phnum.clear();
            Cursor cursor = phelp.display();
            if(cursor.getCount()>0)
            {
                while (cursor.moveToNext())
                {
                    phnum.add(cursor.getLong(0) + " @ " + cursor.getString(1));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(phnno.this,android.R.layout.simple_list_item_1,phnum);
                listView.setAdapter(adapter);
            }
            else {
                Toast.makeText(this, "No contact exists", Toast.LENGTH_SHORT).show();
                finish();
            }
        }catch (Exception e)
        {
            Toast.makeText(this, "No contact exist", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    public void schoolsetup() {
        try {
            Cursor cursor = schoolhelp.display();
            if (cursor.getCount() > 0) {
                schoolnlist.clear();
                schoolnlist.add(0, "Choose School Name");
                while (cursor.moveToNext()) {
                    schoolnlist.add(cursor.getString(1));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(phnno.this, android.R.layout.simple_spinner_item, schoolnlist);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                showschool.setAdapter(adapter);
            } else {
                Toast.makeText(this, "No schools, Create school first", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Database link failed,check school is created", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
