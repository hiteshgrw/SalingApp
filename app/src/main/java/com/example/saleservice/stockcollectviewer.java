package com.example.saleservice;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

public class stockcollectviewer extends AppCompatActivity {
    private schoolmgmthelper schoolhelp;
    private schoolcompanymap companyhelp;
    private Companymgmthelper companymgmthelper;
    private tempstock temphelper;
    private bookdatabasehelper bdbdhelper;
    private userbdbhelper userstockhelper;
    private userhelper userhelp;
    private Integer schid,usname,cpid;
    private List<netstockclass> stklist;
    private netstockclass stkclass;
    private netstockadapter stkadapter;
    private ListView listView;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.samplelistview);
        deleteDatabase("booktempdata.db");
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Calculation in process ...");
        progressDialog.show();
        databasecreator();
        stklist = new ArrayList<>();
        stklist.clear();
        listView = findViewById(R.id.splistview);
        try {
            companymgmthelper = new Companymgmthelper(this);
            Cursor cursor = companymgmthelper.display();
            if(cursor.getCount()>0)
            {
                while (cursor.moveToNext()) {
                    cpid = cursor.getInt(0);
                    temphelper = new tempstock(this, "Company" + cpid);
                    Cursor tempcursor = temphelper.display();
                    if (tempcursor.getCount() > 0) {
                        while (tempcursor.moveToNext()) {
                            stkclass = new netstockclass(tempcursor.getString(0), cursor.getString(1), tempcursor.getDouble(1), tempcursor.getInt(2), tempcursor.getInt(3));
                            stklist.add(stkclass);
                        }
                    }
                }
            }
            stkadapter = new netstockadapter(this,R.layout.nstockviewlayout,stklist);
            progressDialog.dismiss();
            listView.setAdapter(stkadapter);
        } catch (Exception e) {
            progressDialog.dismiss();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        deleteDatabase("booktempdata.db");
//        Toast.makeText(this, "database validity:" + check, Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.invbllmenu,menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId())
//        {
//            case R.id.invbllrefresh:{
//                progressDialog = new ProgressDialog(this);
//                progressDialog.setMessage("Calculation in process ...");
//                progressDialog.show();
//                stklist = new ArrayList<>();
//                stklist.clear();
//                listView = findViewById(R.id.splistview);
//                try {
//                    companymgmthelper = new Companymgmthelper(this);
//                    Cursor cursor = companymgmthelper.display();
//                    if(cursor.getCount()>0)
//                    {
//                        while (cursor.moveToNext()) {
//                            cpid = cursor.getInt(0);
//                            temphelper = new tempstock(this, "Company" + cpid);
//                            Cursor tempcursor = temphelper.display();
//                            if (tempcursor.getCount() > 0) {
//                                while (tempcursor.moveToNext()) {
//                                    stkclass = new netstockclass(tempcursor.getString(0), cursor.getString(1), tempcursor.getDouble(1), tempcursor.getInt(2), tempcursor.getInt(3));
//                                    stklist.add(stkclass);
//                                }
//                            }
//                        }
//                    }
//                    stkadapter = new netstockadapter(this,R.layout.nstockviewlayout,stklist);
//                    progressDialog.dismiss();
//                    listView.setAdapter(stkadapter);
//                } catch (Exception e) {
//                    progressDialog.dismiss();
//                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
//        return super.onOptionsItemSelected(item);
//    }

    public void databasecreator()
    {
        adminsetupdata();
        schoolhelp = new schoolmgmthelper(this);
        userhelp = new userhelper(this);
        try {
            Cursor schcursor = schoolhelp.display();
            if(schcursor.getCount()>0)
            {
                while (schcursor.moveToNext())
                {
                    schid = schcursor.getInt(0);
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
                                                boolean res = temphelper.insert(bookcursor.getString(0),bookcursor.getDouble(1),bookcursor.getInt(2),bookcursor.getInt(3));
                                                if(!res)
                                                {
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
        try {
            schoolhelp = new schoolmgmthelper(this);
            Cursor schcursor = schoolhelp.display();
            if(schcursor.getCount()>0)
            {
                while (schcursor.moveToNext())
                {
                    schid = schcursor.getInt(0);
                    companyhelp = new schoolcompanymap(this,"School"+schid);
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
                                    boolean res = temphelper.insert(bookcursor.getString(0),bookcursor.getDouble(1),bookcursor.getInt(2),bookcursor.getInt(3));
//                                    Toast.makeText(this, bookcursor.getString(0) + res, Toast.LENGTH_SHORT).show();
                                    if(!res)
                                    {
                                        Integer oldpur = temphelper.getpurstock(bookcursor.getString(0));
                                        Integer oldsale = temphelper.getsalestock(bookcursor.getString(0));
//                                        Toast.makeText(this, bookcursor.getString(0) + oldpur + "item" , Toast.LENGTH_SHORT).show();
                                        temphelper.update(bookcursor.getString(0),oldpur+bookcursor.getInt(2),oldsale+bookcursor.getInt(3),bookcursor.getDouble(1));
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
        catch (Exception e)
        {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
