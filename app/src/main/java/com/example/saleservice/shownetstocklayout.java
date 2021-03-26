package com.example.saleservice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

import com.example.saleservice.adapter.netstockadapter;
import com.example.saleservice.classes.netstockclass;
import com.example.saleservice.helper.Companymgmthelper;
import com.example.saleservice.helper.bookdatabasehelper;
import com.example.saleservice.helper.schoolcompanymap;
import com.example.saleservice.helper.schoolmgmthelper;
import com.example.saleservice.helper.userbdbhelper;
import com.example.saleservice.helper.userhelper;
import com.google.android.material.textfield.TextInputLayout;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class shownetstocklayout extends AppCompatActivity {
    private Spinner schoolname,username;
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
    private netstockclass stkclass;
    private netstockadapter stkadapter;
    private userbdbhelper userstockhelper;
    private List<netstockclass> stklist;
    private userhelper userhelp;
    private Integer flag = -1,newprquant,newslquant;
    private Double newprice;
    private String itemname,urname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialsetup();
//        SQLiteDatabase.loadLibs(this);
//        initialsetup();
    }
    public void initialsetup()
    {
        flag = 0;
        setContentView(R.layout.activity_shownetstocklayout);
        invalidateOptionsMenu();
        schoolname = findViewById(R.id.spshownschstockschool);
        username = findViewById(R.id.spshownschstockuser);
        username.setEnabled(false);
        show = findViewById(R.id.btshownschstock);
        schoolhelp = new schoolmgmthelper(shownetstocklayout.this);
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
                    }
                    else {
                        username.setAdapter(null);
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
                        Toast.makeText(shownetstocklayout.this, "User cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if(username.getSelectedItem().toString().equals("admin"))
                            usname = 1;
                        else
                            usname = userhelp.getid(username.getSelectedItem().toString(),schname);
                        urname = username.getSelectedItem().toString();
                        companyhelp = new schoolcompanymap(shownetstocklayout.this,"School"+schname);
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
        Companymgmthelper companymgmthelper = new Companymgmthelper(this);
        flag = 1;
        stklist = new ArrayList<>();
        stockquan = new ArrayList<>();
        stockprice = new ArrayList<>();
        stockname = new ArrayList<>();
        setContentView(R.layout.samplelistview);
        listView = findViewById(R.id.splistview);
        invalidateOptionsMenu();
        if(usname == 1) {
            try {
                companynlist.clear();
                companynlist = companyhelp.display();
                stklist.clear();
                stockquan.clear();
                stockprice.clear();
                stockname.clear();
                for (int i = 0; i <companynlist.size() ; i++) {
                    cpname = companymgmthelper.getid(companynlist.get(i));
                    bdbdhelper = new bookdatabasehelper(shownetstocklayout.this, "School" + schname, "Company" + cpname + "bookdet");
                    Cursor cursor = bdbdhelper.display();
                    if (cursor.getCount() > 0) {
                        while (cursor.moveToNext()) {
                            stockname.add(cursor.getString(0));
                            stockprice.add(cursor.getDouble(1));
                            stockquan.add(cursor.getInt(2));
                            stkclass = new netstockclass(cursor.getString(0),companynlist.get(i), cursor.getDouble(1), cursor.getInt(2), cursor.getInt(3));
                            stklist.add(stkclass);
                        }
                    }
                }
                stkadapter = new netstockadapter(shownetstocklayout.this, R.layout.nstockviewlayout, stklist);
                listView.setAdapter(stkadapter);
            }catch (Exception e) {
                Toast.makeText(this, "No database found , Check for Books created", Toast.LENGTH_SHORT).show();
                finish();
            }
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    editter(position);
                }
            });
//            try {
//                Cursor cursor = bdbdhelper.display();
//                stklist.clear();
//                stockquan.clear();
//                stockprice.clear();
//                stockname.clear();
//                if (cursor.getCount() > 0) {
//                    while (cursor.moveToNext()) {
//                        stockname.add(cursor.getString(0));
//                        stockprice.add(cursor.getDouble(1));
//                        stockquan.add(cursor.getInt(2));
//                        stkclass = new stockclass(cursor.getString(0), cursor.getDouble(1), cursor.getInt(2), cursor.getInt(3));
//                        stklist.add(stkclass);
//                    }
//                    stkadapter = new stockadapter(showschoolstock.this, R.layout.stockviewlayout, stklist);
//                    listView.setAdapter(stkadapter);
//                } else {
//                    Toast.makeText(this, "No Books yet", Toast.LENGTH_SHORT).show();
//                    finish();
//                }
//            }
        }
        else {
            try {
                companynlist.clear();
                companynlist = companyhelp.display();
                stklist.clear();
                stockquan.clear();
                stockprice.clear();
                stockname.clear();
                for (int i = 0; i <companynlist.size() ; i++) {
                    cpname = companymgmthelper.getid(companynlist.get(i));
                    userstockhelper = new userbdbhelper(shownetstocklayout.this,"School" + schname, "Company" + cpname + "bookdet",usname);
                    Cursor cursor = userstockhelper.display();
                    if (cursor.getCount() > 0) {
                        while (cursor.moveToNext()) {
                            stockname.add(cursor.getString(0));
                            stockprice.add(cursor.getDouble(1));
                            stockquan.add(cursor.getInt(2));
                            stkclass = new netstockclass(cursor.getString(0),companynlist.get(i),cursor.getDouble(1), cursor.getInt(2), cursor.getInt(3));
                            stklist.add(stkclass);
                        }
                    }
                }
                stkadapter = new netstockadapter(shownetstocklayout.this, R.layout.nstockviewlayout, stklist);
                listView.setAdapter(stkadapter);
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
            case R.id.rqmprint:{
                if(flag == 1)
                {
                    try {
                        pdfgenerate();
                    } catch (IOException e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
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
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(shownetstocklayout.this, android.R.layout.simple_spinner_item, schoolnlist);
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
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(shownetstocklayout.this, android.R.layout.simple_spinner_item, usernlist);
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
    private void pdfgenerate() throws IOException {
        Integer i,j,ctr;
        Integer nxtpgae = 0;
        Double total = 0.0;
        Integer width = 550;
        Integer height = 800;
        PdfDocument pdfDocument = new PdfDocument();
        Paint mypaint = new Paint();
        Paint title = new Paint();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(width,height,1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        title.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.NORMAL));
        title.setTextSize(10f);
        ctr = 0;
        title.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("School Name : " + schoolhelp.getname(schname), 30, 30, title);
        title.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("User Name : "+ urname,width-30,30,title);
        Integer size;
        if(stklist.size()<40) {
            size = stklist.size();
            nxtpgae = 0;
        }
        else {
            size = 40;
            nxtpgae = 1;
        }
        mypaint.setStrokeWidth(1);
        canvas.drawLine(25,35,width-25,35,mypaint);
        canvas.drawLine(25,35,25,height-30,mypaint);
        canvas.drawLine(width/2 + 15,35,width/2 + 15,height-30,mypaint);
        canvas.drawLine(width-125,35,width - 125,height-30,mypaint);
        canvas.drawLine(width-75,35,width - 75,height-30,mypaint);
        canvas.drawLine(width-25,35,width-25,height-30,mypaint);
        canvas.drawLine(25,height-30,width-25,height-30,mypaint);
//        if(ctr<=80) {
        for (j = 0; j < size; j++) {
            title.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(stklist.get(j).getName(), 30, 50 + ctr * 10, title);
            canvas.drawText(stklist.get(j).getCname(), width/2 + 20, 50 + ctr * 10, title);
            title.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(stklist.get(j).getPquan() + " Pcs", width - 80, 50 + ctr * 10, title);
            canvas.drawText(stklist.get(j).getSquan() + " Pcs", width - 30, 50 + ctr * 10, title);
            if (j!=size-1) {
                if(!stklist.get(j).getCname().equals(stklist.get(j+1).getCname()))
                {
                    ctr++;
                    canvas.drawLine(25,50+ctr*10,width-25,50+ctr*10,mypaint);
                }
            }
            ctr++;
        }
//            }
//            else {
//                nxtpgae = 1;
//            }
//        if(nxtpgae == 0) {
//            title.setTextAlign(Paint.Align.CENTER);
//            title.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
//            canvas.drawText("G.TOTAL : " + total, width / 2, 60 + ctr * 15, title);
//        }
        pdfDocument.finishPage(page);
        if(nxtpgae == 1)
        {
            PdfDocument.PageInfo pageInfo2 = new PdfDocument.PageInfo.Builder(width,height,1).create();
            PdfDocument.Page page2 = pdfDocument.startPage(pageInfo2);
            Canvas canvas2 = page2.getCanvas();
            title.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
            title.setTextSize(10f);
            title.setTextAlign(Paint.Align.LEFT);
            canvas2.drawText("School Name : " + schoolhelp.getname(schname), 30, 30, title);
            title.setTextAlign(Paint.Align.RIGHT);
            canvas2.drawText("User Name : "+ urname,width-30,30,title);
            ctr = 0;
            if(stklist.size()<80) {
                size = stklist.size();
                nxtpgae = 0;
            }
            else {
                size = 80;
                nxtpgae = 1;
            }
            mypaint.setStrokeWidth(1);
            canvas2.drawLine(25,35,width-25,35,mypaint);
            canvas2.drawLine(25,35,25,height-30,mypaint);
            canvas2.drawLine(width/2 + 15,35,width/2 + 15,height-30,mypaint);
            canvas2.drawLine(width-125,35,width - 125,height-30,mypaint);
            canvas2.drawLine(width-75,35,width - 75,height-30,mypaint);
            canvas2.drawLine(width-25,35,width-25,height-30,mypaint);
            canvas2.drawLine(25,height-30,width-25,height-30,mypaint);
//        if(ctr<=80) {
            for (j = 40; j < size; j++) {
                title.setTextAlign(Paint.Align.LEFT);
                canvas2.drawText(stklist.get(j).getName(), 30, 50 + ctr * 10, title);
                canvas2.drawText(stklist.get(j).getCname(), width/2, 50 + ctr * 10, title);
                title.setTextAlign(Paint.Align.RIGHT);
                canvas2.drawText(stklist.get(j).getPquan() + " Pcs", width - 80, 50 + ctr * 10, title);
                canvas2.drawText(stklist.get(j).getSquan() + " Pcs", width - 30, 50 + ctr * 10, title);
                if (j!=size-1) {
                    if(!stklist.get(j).getCname().equals(stklist.get(j+1).getCname()))
                    {
                        ctr++;
                        canvas2.drawLine(25,50+ctr*10,width-25,50+ctr*10,mypaint);
                    }
                }
                ctr++;
            }
            pdfDocument.finishPage(page2);
        }
        if(nxtpgae == 1)
        {
            PdfDocument.PageInfo pageInfo3 = new PdfDocument.PageInfo.Builder(width,height,1).create();
            PdfDocument.Page page3 = pdfDocument.startPage(pageInfo3);
            Canvas canvas3 = page3.getCanvas();
            title.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
            title.setTextSize(10f);
            title.setTextAlign(Paint.Align.LEFT);
            canvas3.drawText("School Name : " + schoolhelp.getname(schname), 30, 30, title);
            title.setTextAlign(Paint.Align.RIGHT);
            canvas3.drawText("User Name : "+ urname,width-30,30,title);
            ctr = 0;
            if(stklist.size()<120) {
                size = stklist.size();
                nxtpgae = 0;
            }
            else {
                size = 120;
                nxtpgae = 1;
            }
            mypaint.setStrokeWidth(1);
            canvas3.drawLine(25,35,width-25,35,mypaint);
            canvas3.drawLine(25,35,25,height-30,mypaint);
            canvas3.drawLine(width/2 + 15,35,width/2 + 15,height-30,mypaint);
            canvas3.drawLine(width-125,35,width - 125,height-30,mypaint);
            canvas3.drawLine(width-75,35,width - 75,height-30,mypaint);
            canvas3.drawLine(width-25,35,width-25,height-30,mypaint);
            canvas3.drawLine(25,height-30,width-25,height-30,mypaint);
//        if(ctr<=80) {
            for (j = 80; j < size; j++) {
                title.setTextAlign(Paint.Align.LEFT);
                canvas3.drawText(stklist.get(j).getName(), 30, 50 + ctr * 10, title);
                canvas3.drawText(stklist.get(j).getCname(), width/2, 50 + ctr * 10, title);
                title.setTextAlign(Paint.Align.RIGHT);
                canvas3.drawText(stklist.get(j).getPquan() + " Pcs", width - 80, 50 + ctr * 10, title);
                canvas3.drawText(stklist.get(j).getSquan() + " Pcs", width - 30, 50 + ctr * 10, title);
                if (j!=size-1) {
                    if(!stklist.get(j).getCname().equals(stklist.get(j+1).getCname()))
                    {
                        ctr++;
                        canvas3.drawLine(25,50+ctr*10,width-25,50+ctr*10,mypaint);
                    }
                }
                ctr++;
            }
            pdfDocument.finishPage(page3);
        }
        if(nxtpgae == 1)
        {
            PdfDocument.PageInfo pageInfo4 = new PdfDocument.PageInfo.Builder(width,height,1).create();
            PdfDocument.Page page4 = pdfDocument.startPage(pageInfo4);
            Canvas canvas4 = page4.getCanvas();
            title.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
            title.setTextSize(10f);
            title.setTextAlign(Paint.Align.LEFT);
            canvas4.drawText("School Name : " + schoolhelp.getname(schname), 30, 30, title);
            title.setTextAlign(Paint.Align.RIGHT);
            canvas4.drawText("User Name : "+ urname,width-30,30,title);
            ctr = 0;
            if(stklist.size()<160) {
                size = stklist.size();
                nxtpgae = 0;
            }
            else {
                size = 160;
                nxtpgae = 1;
            }
            mypaint.setStrokeWidth(1);
            canvas4.drawLine(25,35,width-25,35,mypaint);
            canvas4.drawLine(25,35,25,height-30,mypaint);
            canvas4.drawLine(width/2 + 15,35,width/2 + 15,height-30,mypaint);
            canvas4.drawLine(width-125,35,width - 125,height-30,mypaint);
            canvas4.drawLine(width-75,35,width - 75,height-30,mypaint);
            canvas4.drawLine(width-25,35,width-25,height-30,mypaint);
            canvas4.drawLine(25,height-30,width-25,height-30,mypaint);
//        if(ctr<=80) {
            for (j = 120; j < size; j++) {
                title.setTextAlign(Paint.Align.LEFT);
                canvas4.drawText(stklist.get(j).getName(), 30, 50 + ctr * 10, title);
                canvas4.drawText(stklist.get(j).getCname(), width/2, 50 + ctr * 10, title);
                title.setTextAlign(Paint.Align.RIGHT);
                canvas4.drawText(stklist.get(j).getPquan() + " Pcs", width - 80, 50 + ctr * 10, title);
                canvas4.drawText(stklist.get(j).getSquan() + " Pcs", width - 30, 50 + ctr * 10, title);
                if (j!=size-1) {
                    if(!stklist.get(j).getCname().equals(stklist.get(j+1).getCname()))
                    {
                        ctr++;
                        canvas4.drawLine(25,50+ctr*10,width-25,50+ctr*10,mypaint);
                    }
                }
                ctr++;
            }
            pdfDocument.finishPage(page4);
        }
        if(nxtpgae == 1)
        {
            PdfDocument.PageInfo pageInfo5 = new PdfDocument.PageInfo.Builder(width,height,1).create();
            PdfDocument.Page page5 = pdfDocument.startPage(pageInfo5);
            Canvas canvas5 = page5.getCanvas();
            title.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
            title.setTextSize(10f);
            title.setTextAlign(Paint.Align.LEFT);
            canvas5.drawText("School Name : " + schoolhelp.getname(schname), 30, 30, title);
            title.setTextAlign(Paint.Align.RIGHT);
            canvas5.drawText("User Name : "+ urname,width-30,30,title);
            ctr = 0;
            if(stklist.size()<200) {
                size = stklist.size();
                nxtpgae = 0;
            }
            else {
                size = 200;
                nxtpgae = 1;
            }
            mypaint.setStrokeWidth(1);
            canvas5.drawLine(25,35,width-25,35,mypaint);
            canvas5.drawLine(25,35,25,height-30,mypaint);
            canvas5.drawLine(width/2 + 15,35,width/2 + 15,height-30,mypaint);
            canvas5.drawLine(width-125,35,width - 125,height-30,mypaint);
            canvas5.drawLine(width-75,35,width - 75,height-30,mypaint);
            canvas5.drawLine(width-25,35,width-25,height-30,mypaint);
            canvas5.drawLine(25,height-30,width-25,height-30,mypaint);
//        if(ctr<=80) {
            for (j = 160; j < size; j++) {
                title.setTextAlign(Paint.Align.LEFT);
                canvas5.drawText(stklist.get(j).getName(), 30, 50 + ctr * 10, title);
                canvas5.drawText(stklist.get(j).getCname(), width/2, 50 + ctr * 10, title);
                title.setTextAlign(Paint.Align.RIGHT);
                canvas5.drawText(stklist.get(j).getPquan() + " Pcs", width - 80, 50 + ctr * 10, title);
                canvas5.drawText(stklist.get(j).getSquan() + " Pcs", width - 30, 50 + ctr * 10, title);
                if (j!=size-1) {
                    if(!stklist.get(j).getCname().equals(stklist.get(j+1).getCname()))
                    {
                        ctr++;
                        canvas5.drawLine(25,50+ctr*10,width-25,50+ctr*10,mypaint);
                    }
                }
                ctr++;
            }
            pdfDocument.finishPage(page5);
        }
        if(nxtpgae == 1)
        {
            PdfDocument.PageInfo pageInfo6 = new PdfDocument.PageInfo.Builder(width,height,1).create();
            PdfDocument.Page page6 = pdfDocument.startPage(pageInfo6);
            Canvas canvas6 = page6.getCanvas();
            title.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
            title.setTextSize(10f);
            title.setTextAlign(Paint.Align.LEFT);
            canvas6.drawText("School Name : " + schoolhelp.getname(schname), 30, 30, title);
            title.setTextAlign(Paint.Align.RIGHT);
            canvas6.drawText("User Name : "+ urname,width-30,30,title);
            ctr = 0;
            if(stklist.size()<240) {
                size = stklist.size();
                nxtpgae = 0;
            }
            else {
                size = 240;
                nxtpgae = 1;
            }
            mypaint.setStrokeWidth(1);
            canvas6.drawLine(25,35,width-25,35,mypaint);
            canvas6.drawLine(25,35,25,height-30,mypaint);
            canvas6.drawLine(width/2 + 15,35,width/2 + 15,height-30,mypaint);
            canvas6.drawLine(width-125,35,width - 125,height-30,mypaint);
            canvas6.drawLine(width-75,35,width - 75,height-30,mypaint);
            canvas6.drawLine(width-25,35,width-25,height-30,mypaint);
            canvas6.drawLine(25,height-30,width-25,height-30,mypaint);
//        if(ctr<=80) {
            for (j = 200; j < size; j++) {
                title.setTextAlign(Paint.Align.LEFT);
                canvas6.drawText(stklist.get(j).getName(), 30, 50 + ctr * 10, title);
                canvas6.drawText(stklist.get(j).getCname(), width/2, 50 + ctr * 10, title);
                title.setTextAlign(Paint.Align.RIGHT);
                canvas6.drawText(stklist.get(j).getPquan() + " Pcs", width - 80, 50 + ctr * 10, title);
                canvas6.drawText(stklist.get(j).getSquan() + " Pcs", width - 30, 50 + ctr * 10, title);
                if (j!=size-1) {
                    if(!stklist.get(j).getCname().equals(stklist.get(j+1).getCname()))
                    {
                        ctr++;
                        canvas6.drawLine(25,50+ctr*10,width-25,50+ctr*10,mypaint);
                    }
                }
                ctr++;
            }
            pdfDocument.finishPage(page6);
        }
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SalesInvoice/Stock"+schname+"User"+usname;
        File dir = new File(path);
        if(!dir.exists())
            dir.mkdirs();
        String filepath = "StockSummary.pdf";
        File file = new File(dir, filepath);
        if(file.exists())
            file.delete();
        pdfDocument.writeTo(new FileOutputStream(file));
        viewpdf(filepath,"SalesInvoice/Stock"+schname+"User"+usname);
        pdfDocument.close();
    }
    private void viewpdf(String file, String directory)
    {
        File pdffile = new File(Environment.getExternalStorageDirectory() + "/" + directory + "/" + file);
        final Uri path = FileProvider.getUriForFile(this,BuildConfig.APPLICATION_ID + ".provider",pdffile);
        Intent vpdf = new Intent(Intent.ACTION_VIEW);
        vpdf.setDataAndType(path,"application/pdf");
        vpdf.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        vpdf.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try{
            startActivity(vpdf);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(shownetstocklayout.this, "pdf not found, upload failed", Toast.LENGTH_SHORT).show();
        }
    }
//    public void initialsetup()
//    {
//        flag = 0;
//        setContentView(R.layout.activity_shownetstocklayout);
//        invalidateOptionsMenu();
//        companyname = findViewById(R.id.spshowstockcompany);
//        show = findViewById(R.id.btshowstock);
//        companyhelp = new Companymgmthelper(shownetstocklayout.this);
////        Companymgmthelper.getInstance(shownetstocklayout.this);
//        companynlist = new ArrayList<>();
//        companysetup();
//        show.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(companyname.getSelectedItemPosition() == 0)
//                    Toast.makeText(shownetstocklayout.this, "Field cannot be empty", Toast.LENGTH_SHORT).show();
//                else {
//                    cpname = companyhelp.getid(companyname.getSelectedItem().toString());
//                    setupview();
//                }
//            }
//        });
//    }
//    public void setupview()
//    {
//        flag = 1;
//        stklist = new ArrayList<>();
//        stockquan = new ArrayList<>();
//        stockprice = new ArrayList<>();
//        stockname = new ArrayList<>();
//        setContentView(R.layout.samplelistview);
//        invalidateOptionsMenu();
//        listView = findViewById(R.id.splistview);
////        bdbdhelper = new wholestockdbhelper(shownetstocklayout.this,"Company"+cpname);
////        wholestockdbhelper.getInstance(shownetstocklayout.this,"Company"+cpname);
//        try {
////            Cursor cursor = bdbdhelper.display();
//            stklist.clear();
//            stockquan.clear();
//            stockprice.clear();
//            stockname.clear();
//            if(cursor.getCount()>0)
//            {
//                while (cursor.moveToNext())
//                {
//                    stockname.add(cursor.getString(0));
//                    stockprice.add(cursor.getDouble(1));
//                    stockquan.add(cursor.getInt(2));
//                    stkclass = new stockclass(cursor.getString(0),cursor.getDouble(1),cursor.getInt(2),cursor.getInt(3));
//                    stklist.add(stkclass);
//                }
//                stockadapter adapter = new stockadapter(shownetstocklayout.this,R.layout.stockviewlayout,stklist);
//                listView.setAdapter(adapter);
//            }
//            else
//            {
//                Toast.makeText(this, "No Books yet", Toast.LENGTH_SHORT).show();
//                finish();
//            }
//        }catch (Exception e)
//        {
//            Toast.makeText(this, "No database found , Check for Books created", Toast.LENGTH_SHORT).show();
//            finish();
//        }
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                editter(position);
//            }
//        });
//    }
//    public void editter(final Integer pos)
//    {
//        flag = 2;
//        setContentView(R.layout.schoolbookquatpriceedit);
//        invalidateOptionsMenu();
//        final EditText quantity = findViewById(R.id.eteditschbookquant);
//        final EditText price = findViewById(R.id.eteditschbookprice);
//        Button savebt = findViewById(R.id.bteditschbook);
//        itemname = stockname.get(pos);
//        quantity.setText("" + stockquan.get(pos));
//        price.setText("" + stockprice.get(pos));
//        savebt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(quantity.getText().toString().isEmpty() || price.getText().toString().isEmpty())
//                    Toast.makeText(shownetstocklayout.this, "Field cannot be empty", Toast.LENGTH_SHORT).show();
//                else {
//                    final Integer newquant = Integer.parseInt(quantity.getText().toString());
//                    final Double newprice = Double.parseDouble(price.getText().toString());
////                    boolean result = bdbdhelper.update(stockname.get(pos),newquant,newprice);
//                    Toast.makeText(shownetstocklayout.this, "" + newprice, Toast.LENGTH_SHORT).show();
//                    if(result == false)
//                        Toast.makeText(shownetstocklayout.this, "Update failed", Toast.LENGTH_SHORT).show();
//                    else
//                        setupview();
//                }
//            }
//        });
//    }
//
//    @Override
//    public void onBackPressed() {
//        if(flag == 2)
//            setupview();
//        else if(flag == 1)
//            initialsetup();
//        else
//            finish();
//    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        if(flag == 1 || flag == 2)
//        {
//            getMenuInflater().inflate(R.menu.delmenu,menu);
//            menu.removeItem(R.id.delinv);
//        }
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId())
//        {
//            case R.id.delquan:{
//                if(flag == 1) {
////                    bdbdhelper.setquan0();
//                    setupview();
//                }
//                else {
//                    if(flag == 2)
//                    {
////                        bdbdhelper.updatesalestock(itemname,0);
//                        flag = 1;
//                        setupview();
//                    }
//                }
//                break;
//            }
//        }
//        return super.onOptionsItemSelected(item);
//    }
//    public void companysetup() {
//        try {
//            Cursor cursor = companyhelp.display();
//            if(cursor.getCount()>0) {
//                companynlist.clear();
//                companynlist.add(0, "Choose Company Name");
//                while (cursor.moveToNext()) {
//                    companynlist.add(cursor.getString(1));
//                }
//                ArrayAdapter<String> adapter = new ArrayAdapter<String>(shownetstocklayout.this, android.R.layout.simple_spinner_item, companynlist);
//                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                companyname.setAdapter(adapter);
//            }else {
//                Toast.makeText(this, "No companies, Create Company first", Toast.LENGTH_SHORT).show();
//                finish();
//            }
//        }catch (Exception e)
//        {
//            Toast.makeText(this, "Database link failed, check company is created", Toast.LENGTH_SHORT).show();
//            finish();
//        }
//    }
}
