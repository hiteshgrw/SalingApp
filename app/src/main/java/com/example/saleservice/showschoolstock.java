package com.example.saleservice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.saleservice.adapter.stockadapter;
import com.example.saleservice.classes.stockclass;
import com.example.saleservice.helper.Companymgmthelper;
import com.example.saleservice.helper.bookdatabasehelper;
import com.example.saleservice.helper.schoolcompanymap;
import com.example.saleservice.helper.schoolmgmthelper;
import com.example.saleservice.helper.userbdbhelper;
import com.example.saleservice.helper.userhelper;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class showschoolstock extends AppCompatActivity {
    private Spinner schoolname,companyname,username;
    private Button show;
    private schoolmgmthelper schoolhelp;
    private schoolcompanymap companyhelp;
    private bookdatabasehelper bdbdhelper;
    private List<String> schoolnlist;
    private List<String> companynlist,usernlist;
    private ListView listView;
    private Integer schname,cpname,usname;
    private List<Integer> stockquan;
    private List<Double> stockprice;
    private List<String> stockname;
    private stockclass stkclass;
    private stockadapter stkadapter;
    private userbdbhelper userstockhelper;
    private List<stockclass> stklist;
    private userhelper userhelp;
    private Integer flag = -1,newprquant,newslquant;
    private Double newprice;
    private String itemname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        SQLiteDatabase.loadLibs(this);
        initialsetup();
    }
    public void initialsetup()
    {
        flag = 0;
        setContentView(R.layout.activity_showschoolstock);
        invalidateOptionsMenu();
        schoolname = findViewById(R.id.spshowschstockschool);
        companyname = findViewById(R.id.spshowschstockcompany);
        username = findViewById(R.id.spshowschstockuser);
        username.setEnabled(false);
        companyname.setEnabled(false);
        show = findViewById(R.id.btshowschstock);
        schoolhelp = new schoolmgmthelper(showschoolstock.this);
        userhelp = new userhelper(this);
//        schoolmgmthelper.getInstance(showschoolstock.this);
        schoolnlist = new ArrayList<>();
        companynlist = new ArrayList<>();
        usernlist = new ArrayList<>();
        schoolsetup();
        try {
            schoolname.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position>0)
                    {
                        schname = schoolhelp.getid(schoolname.getSelectedItem().toString());
                        username.setEnabled(true);
                        usersetup(schname);
                        companynlist.clear();
                        companyhelp = new schoolcompanymap(showschoolstock.this,"School"+schname);
    //                    schoolcompanymap.getInstance(showschoolstock.this,"School"+schname);
                        companynlist = companyhelp.display();
                        companyname.setEnabled(true);
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(showschoolstock.this, android.R.layout.simple_spinner_item,companynlist);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        companyname.setAdapter(adapter);
                    }
                    else {
                        companyname.setEnabled(false);
                        username.setEnabled(false);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            show.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(username.getSelectedItemPosition() == 0)
                    {
                        Toast.makeText(showschoolstock.this, "User cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Companymgmthelper helper = new Companymgmthelper(showschoolstock.this);
                        //                Companymgmthelper.getInstance(showschoolstock.this);
                        cpname = helper.getid(companyname.getSelectedItem().toString());
                        if(username.getSelectedItem().toString().equals("admin"))
                            usname = 1;
                        else
                            usname = userhelp.getid(username.getSelectedItem().toString(),schname);
                        setupview();
                    }
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void setupview()
    {
        flag = 1;
        stklist = new ArrayList<>();
        stockquan = new ArrayList<>();
        stockprice = new ArrayList<>();
        stockname = new ArrayList<>();
        setContentView(R.layout.samplelistview);
        listView = findViewById(R.id.splistview);
        invalidateOptionsMenu();
        if(usname == 1) {
            bdbdhelper = new bookdatabasehelper(showschoolstock.this, "School" + schname, "Company" + cpname + "bookdet");
//        bookdatabasehelper.getInstance(showschoolstock.this,"School"+schname,"Company"+cpname+"bookdet");
//        stockhelper = new wholestockdbhelper(showschoolstock.this,"Company"+cpname);
//        wholestockdbhelper.getInstance(showschoolstock.this,"Company"+cpname);
            try {
                Cursor cursor = bdbdhelper.display();
                stklist.clear();
                stockquan.clear();
                stockprice.clear();
                stockname.clear();
                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        stockname.add(cursor.getString(0));
                        stockprice.add(cursor.getDouble(1));
                        stockquan.add(cursor.getInt(2));
                        stkclass = new stockclass(cursor.getString(0), cursor.getDouble(1), cursor.getInt(2), cursor.getInt(3));
                        stklist.add(stkclass);
                    }
                    stkadapter = new stockadapter(showschoolstock.this, R.layout.stockviewlayout, stklist);
                    listView.setAdapter(stkadapter);
                } else {
                    Toast.makeText(this, "No Books yet", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } catch (Exception e) {
                Toast.makeText(this, "No database found , Check for Books created", Toast.LENGTH_SHORT).show();
                finish();
            }
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    editter(position);
                }
            });
        }
        else {
            userstockhelper = new userbdbhelper(showschoolstock.this,"School" + schname, "Company" + cpname + "bookdet",usname);
            try {
                Cursor cursor = userstockhelper.display();
                stklist.clear();
                stockquan.clear();
                stockprice.clear();
                stockname.clear();
                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        stockname.add(cursor.getString(0));
                        stockprice.add(cursor.getDouble(1));
                        stockquan.add(cursor.getInt(2));
                        stkclass = new stockclass(cursor.getString(0), cursor.getDouble(1), cursor.getInt(2), cursor.getInt(3));
                        stklist.add(stkclass);
                    }
                    stkadapter = new stockadapter(showschoolstock.this, R.layout.stockviewlayout, stklist);
                    listView.setAdapter(stkadapter);
                } else {
                    Toast.makeText(this, "No Books yet", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } catch (Exception e) {
                Toast.makeText(this, "No database found , Check for Books created", Toast.LENGTH_SHORT).show();
                finish();
            }
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    usereditter(position);
                }
            });
        }
    }
    public void editter(final Integer pos)
    {
        flag = 2;
        View mview = getLayoutInflater().inflate(R.layout.schoolbookquatpriceedit,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(mview);
        builder.setCancelable(false);
        final TextInputLayout soldquantity = mview.findViewById(R.id.eteditschsalebookquant);
        final TextInputLayout purcquantity = mview.findViewById(R.id.eteditschpurbookquant);
        final TextInputLayout price = mview.findViewById(R.id.eteditschbookprice);
        itemname = stockname.get(pos);
        soldquantity.getEditText().setText(""+stklist.get(pos).getSquan());
        purcquantity.getEditText().setText("" + stklist.get(pos).getPquan());
        price.getEditText().setText("" + stklist.get(pos).getPrice());
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(purcquantity.getEditText().getText().toString().isEmpty())
                    purcquantity.setError("Field cannot be empty");
                else
                    purcquantity.setError(null);
                if(soldquantity.getEditText().getText().toString().isEmpty())
                    soldquantity.setError("Field cannot be empty");
                else
                    soldquantity.setError(null);
                if(price.getEditText().getText().toString().isEmpty())
                    price.setError("Field cannot be empty");
                else
                    price.setError(null);
                if(!purcquantity.getEditText().getText().toString().isEmpty() && !soldquantity.getEditText().getText().toString().isEmpty() && !price.getEditText().getText().toString().isEmpty())
                {
                    newprquant = Integer.parseInt(purcquantity.getEditText().getText().toString());
                    newslquant = Integer.parseInt(soldquantity.getEditText().getText().toString());
                    newprice = Double.parseDouble(price.getEditText().getText().toString());
                    bdbdhelper.updateall(stockname.get(pos),newslquant,newprquant,newprice);
//                    stockhelper.update(stockname.get(pos),oldnetstck + (newquant-oldstock),newprice);
                    setupview();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                setupview();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.show();
//        final Integer oldstock = stockquan.get(pos);
//        final Integer oldnetstck = stockhelper.getstock(stockname.get(pos));
    }
    public void usereditter(final Integer pos)
    {
        flag = 2;
        View mview = getLayoutInflater().inflate(R.layout.schoolbookquatpriceedit,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(mview);
        builder.setCancelable(false);
        final TextInputLayout soldquantity = mview.findViewById(R.id.eteditschsalebookquant);
        final TextInputLayout purcquantity = mview.findViewById(R.id.eteditschpurbookquant);
        final TextInputLayout price = mview.findViewById(R.id.eteditschbookprice);
        itemname = stockname.get(pos);
        soldquantity.getEditText().setText(""+stklist.get(pos).getSquan());
        purcquantity.getEditText().setText("" + stklist.get(pos).getPquan());
        price.getEditText().setText("" + stklist.get(pos).getPrice());
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(purcquantity.getEditText().getText().toString().isEmpty())
                    purcquantity.setError("Field cannot be empty");
                else
                    purcquantity.setError(null);
                if(soldquantity.getEditText().getText().toString().isEmpty())
                    soldquantity.setError("Field cannot be empty");
                else
                    soldquantity.setError(null);
                if(price.getEditText().getText().toString().isEmpty())
                    price.setError("Field cannot be empty");
                else
                    price.setError(null);
                if(!purcquantity.getEditText().getText().toString().isEmpty() && !soldquantity.getEditText().getText().toString().isEmpty() && !price.getEditText().getText().toString().isEmpty())
                {
                    newprquant = Integer.parseInt(purcquantity.getEditText().getText().toString());
                    newslquant = Integer.parseInt(soldquantity.getEditText().getText().toString());
                    newprice = Double.parseDouble(price.getEditText().getText().toString());
                    userstockhelper.updateall(stockname.get(pos),newslquant,newprquant,newprice);
//                    stockhelper.update(stockname.get(pos),oldnetstck + (newquant-oldstock),newprice);
                    setupview();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                setupview();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
    @Override
    public void onBackPressed() {
        if(flag == 2)
            setupview();
        else if(flag == 1)
            initialsetup();
        else
            finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(flag == 1)
        {
            getMenuInflater().inflate(R.menu.resetquantmenu,menu);
            menu.removeItem(R.id.rqmprint);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.rqmdelsalequan: {
                if (flag == 1) {
                    if (usname == 1)
                        bdbdhelper.setquan0();
                    else
                        userstockhelper.setquan0();
//                    stockhelper.setquan0();
                    setupview();
                }
                break;
            }
            case R.id.rqmdelpurquan:{
                if(flag == 1)
                {
                    if(usname == 1)
                        bdbdhelper.setpurquan0();
                    else
                        userstockhelper.setpurquan0();
                    setupview();
                }
                break;
            }
        }
        return super.onOptionsItemSelected(item);
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
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(showschoolstock.this, android.R.layout.simple_spinner_item, schoolnlist);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                schoolname.setAdapter(adapter);
            } else {
                Toast.makeText(this, "No schools, Create school first", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Database link failed,check school is created", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    public void usersetup(Integer id) {
        try {
            Cursor cursor = userhelp.display();
            if (cursor.getCount() > 0) {
                usernlist.clear();
                usernlist.add(0, "Choose User");
                while (cursor.moveToNext()) {
                    if(cursor.getInt(4)==id)
                        usernlist.add(cursor.getString(1));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(showschoolstock.this, android.R.layout.simple_spinner_item, usernlist);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                username.setAdapter(adapter);
            } else {
                Toast.makeText(this, "No users, Create user first", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Database link failed,check user is created", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
