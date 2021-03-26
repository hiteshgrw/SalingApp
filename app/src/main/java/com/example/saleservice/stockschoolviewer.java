package com.example.saleservice;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.saleservice.adapter.netstockadapter;
import com.example.saleservice.classes.netstockclass;
import com.example.saleservice.helper.Companymgmthelper;
import com.example.saleservice.helper.bookdatabasehelper;
import com.example.saleservice.helper.schoolcompanymap;
import com.example.saleservice.helper.schoolmgmthelper;
import com.example.saleservice.helper.tempstock;
import com.example.saleservice.helper.userbdbhelper;
import com.example.saleservice.helper.userhelper;

import java.util.ArrayList;
import java.util.List;

public class stockschoolviewer extends AppCompatActivity {
    private schoolmgmthelper schoolhelp;
    private schoolcompanymap companyhelp;
    private Companymgmthelper companymgmthelper;
    private tempstock temphelper;
    private bookdatabasehelper bdbdhelper;
    private userbdbhelper userstockhelper;
    private userhelper userhelp;
    private Spinner spinner;
    private List<String> schoolnlist;
    private Integer schid,usname,cpid;
    private Integer flag = 0;
    private List<netstockclass> stklist;
    private netstockclass stkclass;
    private netstockadapter stkadapter;
    private ListView listView;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialsetup();
    }
    public void initialsetup()
    {
        deleteDatabase("booktempdata.db");
        flag = 0;
        setContentView(R.layout.schoolspinner);
        spinner = findViewById(R.id.spschoolspinner);
        schoolhelp = new schoolmgmthelper(this);
        schoolnlist = new ArrayList<>();
        schoolsetup();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0)
                {
                    schid = schoolhelp.getid(spinner.getSelectedItem().toString());
                    setup();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    public void setup()
    {
        flag = 1;
        setContentView(R.layout.samplelistview);
        deleteDatabase("booktempdata.db");
        progressDialog = new ProgressDialog(stockschoolviewer.this);
        progressDialog.setMessage("Calculation in process ...");
        progressDialog.show();
        databasecreator();
        stklist = new ArrayList<>();
        stklist.clear();
        listView = findViewById(R.id.splistview);
        try {
            companymgmthelper = new Companymgmthelper(stockschoolviewer.this);
            companyhelp = new schoolcompanymap(stockschoolviewer.this,"School"+schid);
            List<String> cmplist = new ArrayList<>();
            cmplist.clear();
            cmplist = companyhelp.display();
            for (int i = 0; i < cmplist.size() ; i++) {
                    cpid = companymgmthelper.getid(cmplist.get(i));
                    temphelper = new tempstock(stockschoolviewer.this, "Company" + cpid);
                    Cursor tempcursor = temphelper.display();
                    if (tempcursor.getCount() > 0) {
                        while (tempcursor.moveToNext()) {
                            stkclass = new netstockclass(tempcursor.getString(0),cmplist.get(i), tempcursor.getDouble(1), tempcursor.getInt(2), tempcursor.getInt(3));
                            stklist.add(stkclass);
                        }
                    }
            }
            stkadapter = new netstockadapter(stockschoolviewer.this,R.layout.nstockviewlayout,stklist);
            progressDialog.dismiss();
            listView.setAdapter(stkadapter);
        } catch (Exception e) {
            Toast.makeText(stockschoolviewer.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onBackPressed() {
        if(flag == 1)
        {
            deleteDatabase("booktempdata.db");
            initialsetup();
        }
        else {
            deleteDatabase("booktempdata.db");
            super.onBackPressed();
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
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, schoolnlist);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
            } else {
                Toast.makeText(this, "No schools, Create school first", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Database link failed,check school is created", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    public void databasecreator()
    {
        adminsetupdata();
        userhelp = new userhelper(this);
        try {
            Cursor usrcursor = userhelp.display();
            if(usrcursor.getCount()>0)
            {
                while (usrcursor.moveToNext())
                {
                    if(usrcursor.getInt(4)==schid && !usrcursor.getString(1).equals("admin"))
                    {
                        usname = usrcursor.getInt(0);
                        companyhelp = new schoolcompanymap(this,"School"+schid);
                        List<String> cmplist = new ArrayList<>();
                        cmplist.clear();
                        cmplist = companyhelp.display();
                        for (int i = 0; i <cmplist.size() ; i++) {
                            cpid = companymgmthelper.getid(cmplist.get(i));
                            try {
                                userstockhelper = new userbdbhelper(this,"School" + schid, "Company" + cpid + "bookdet",usname);
                                Cursor bookcursor = userstockhelper.display();
                                if(bookcursor.getCount()>0)
                                {
                                    while (bookcursor.moveToNext())
                                    {
                                        temphelper = new tempstock(this,"Company"+cpid);
                                        try {
                                            boolean res = temphelper.insert(bookcursor.getString(0),bookcursor.getDouble(1),bookcursor.getInt(2),bookcursor.getInt(3));
                                            if(!res)
                                            {
                                                Integer oldpur = temphelper.getpurstock(bookcursor.getString(0));
                                                Integer oldsale = temphelper.getsalestock(bookcursor.getString(0));
                                                temphelper.update(bookcursor.getString(0),oldpur,oldsale+bookcursor.getInt(3),bookcursor.getDouble(1));
                                            }
                                        } catch (Exception e) {
                                            Integer oldpur = temphelper.getpurstock(bookcursor.getString(0));
                                            Integer oldsale = temphelper.getsalestock(bookcursor.getString(0));
                                            temphelper.update(bookcursor.getString(0),oldpur,oldsale+bookcursor.getInt(3),bookcursor.getDouble(1));
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void adminsetupdata()
    {
        usname = 1;
        companymgmthelper = new Companymgmthelper(this);
        try
        {
         companyhelp = new schoolcompanymap(this,"School"+schid);
//            Toast.makeText(this, "school"+schid, Toast.LENGTH_SHORT).show();
         List<String> cmplist = new ArrayList<>();
         cmplist.clear();
         cmplist = companyhelp.display();
         for (int i = 0; i <cmplist.size() ; i++) {
             cpid = companymgmthelper.getid(cmplist.get(i));
             try {
                 bdbdhelper = new bookdatabasehelper(this, "School" + schid, "Company" + cpid + "bookdet");
                 Cursor bookcursor = bdbdhelper.display();
                 if(bookcursor.getCount()>0)
                 {
                     while (bookcursor.moveToNext())
                     {
                         temphelper = new tempstock(this,"Company"+cpid);
//                             Toast.makeText(this, bookcursor.getString(0)+"entry", Toast.LENGTH_SHORT).show();
                             boolean res = temphelper.insert(bookcursor.getString(0),bookcursor.getDouble(1),bookcursor.getInt(2),bookcursor.getInt(3));
//                             Toast.makeText(this, bookcursor.getString(0) + res, Toast.LENGTH_SHORT).show();
                             if(!res)
                             {
                                 Integer oldpur = temphelper.getpurstock(bookcursor.getString(0));
                                 Integer oldsale = temphelper.getsalestock(bookcursor.getString(0));
//                                 Toast.makeText(this, bookcursor.getString(0) + oldpur + "item" , Toast.LENGTH_SHORT).show();
                                 temphelper.update(bookcursor.getString(0),oldpur+bookcursor.getInt(2),oldsale+bookcursor.getInt(3),bookcursor.getDouble(1));
                             }
                     }
                 }
             } catch (Exception e) {
                 Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
             }
         }
        }
        catch (Exception e)
        {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
