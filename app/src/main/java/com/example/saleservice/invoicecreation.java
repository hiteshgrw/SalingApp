package com.example.saleservice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ActivityNotFoundException;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.saleservice.classes.booklistlayout;
import com.example.saleservice.classes.invlstsetmapper;
import com.example.saleservice.helper.Classmgmthelper;
import com.example.saleservice.helper.Classschhelper;
import com.example.saleservice.helper.Companymgmthelper;
import com.example.saleservice.helper.bookdatabasehelper;
import com.example.saleservice.helper.booklisthelper;
import com.example.saleservice.helper.invoicehelper;
import com.example.saleservice.helper.invoicenomap;
import com.example.saleservice.helper.phnnohelper;
import com.example.saleservice.helper.schoolmgmthelper;
import com.example.saleservice.adapter.invlstsetadapter;
import com.example.saleservice.adapter.invblladapter;
import com.google.firebase.auth.FirebaseAuth;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class invoicecreation extends AppCompatActivity {
    private Spinner Schoolname;
    private Spinner Classname;
    private Button Start;
    private schoolmgmthelper schoolhelp;
    private Classschhelper classhelp;
    private Classmgmthelper sethelp;
    private booklisthelper bookhelp;
    private bookdatabasehelper bdbhelper;
    private Companymgmthelper companyhelper;
    private List<String> schoolnlist,classnlist;
    private Integer schname,clsname;
    private RadioGroup radioGroup;
    private CheckBox whatsappcheck;
    private invoicenomap invoicenohelp;
    private Integer invno;
    private String nameschool,nameclass;
    private String set1,set2,set3;
    private List<booklistlayout> setbll1,setbll2,setbll3;
    private invlstsetmapper firstmap;
    private List<invlstsetmapper> firstmaplist;
    private List<Integer> setids = new ArrayList<>();
    private Double total1,total2,total3;
    private Double dis1,dis2,dis3;
    private Integer count3;
    private Integer flag = -1;
    private EditText dist1,dist2,dist3;
    private Button createtheinv;
    private Date dobj;
    private DateFormat dfm;
    private Integer width = 550;
    private Integer height = 800;
    private Double dt1,dt2,dt3;
    private invoicehelper invhelp;
    private Integer lastposition = 0;
    private Long contactno;
    private EditText phnno;
    private phnnohelper phelp;
    private Integer wth = 600;
    private String schlname,clasname;
    public boolean wcheck;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        SQLiteDatabase.loadLibs(this);
        initialsetup();
    }
    public void initialsetup()
    {
        flag = 0;
        setContentView(R.layout.activity_invoicecreation);
        invalidateOptionsMenu();
        setbll1 = new ArrayList<>();
        setbll2 = new ArrayList<>();
        setbll3 = new ArrayList<>();
        firstmaplist = new ArrayList<>();
        Schoolname = findViewById(R.id.spcrtinvschname);
        Classname = findViewById(R.id.spcrtinvclsname);
        phnno = findViewById(R.id.etphoneno);
        Start = findViewById(R.id.btcrtinv);
        Classname.setEnabled(false);
//        schoolmgmthelper.getInstance(invoicecreation.this);
        schoolhelp = new schoolmgmthelper(invoicecreation.this);
        schoolnlist = new ArrayList<>();
        classnlist = new ArrayList<>();
        schoolsetup();
        Schoolname.setSelection(lastposition);
        Schoolname.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    Classname.setEnabled(true);
                    schname = schoolhelp.getid(Schoolname.getSelectedItem().toString());
                    schlname = Schoolname.getSelectedItem().toString();
                    nameschool = Schoolname.getSelectedItem().toString();
//                    Classschhelper.getInstance(invoicecreation.this,"School"+schname,"classmap");
                    classhelp = new Classschhelper(invoicecreation.this,"School"+schname,"classmap");
                    classsetup();
                } else
                    Classname.setEnabled(false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        try {
            Start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Classname.getSelectedItemPosition() != 0) {

                        lastposition = Schoolname.getSelectedItemPosition();
                        clsname = classhelp.getid(Classname.getSelectedItem().toString());
                        clasname = Classname.getSelectedItem().toString();
                        nameclass = Classname.getSelectedItem().toString();
    //                    invoicenomap.getInstance(invoicecreation.this,"School"+schname);
                        invoicenohelp = new invoicenomap(invoicecreation.this,"School"+schname);
                        sethelp = new Classmgmthelper(invoicecreation.this, "School" + schname,"ClsdetailClass" + clsname);
    //                    Classmgmthelper.getInstance(invoicecreation.this, "School" + schname,"ClsdetailClass" + clsname);
                        contactno = Long.parseLong(phnno.getText().toString());
                        phelp = new phnnohelper(invoicecreation.this,"PhnoSchool"+schname);
    //                    phnnohelper.getInstance(invoicecreation.this,"PhnoSchool"+schname);
                        try {
                            Cursor cursor = invoicenohelp.display();
                            if (cursor.getCount() > 0) {
                                cursor.moveToLast();
                                invno = cursor.getInt(0) + 1;
                            } else
                                invno = 1;
                        } catch (Exception e) {
                            invno = 1;
    //            Toast.makeText(invoicecreation.this, "No table", Toast.LENGTH_SHORT).show();
                        }
                        if(contactno!=0) {
                            phelp.insert(contactno,clasname);
                        }
                        listsetup();
                        secondsetup();
                    }
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void secondsetup()
    {
        flag = 1;
        firstmaplist.clear();
        total1 = 0.0;
        total2 = 0.0;
        total3 = 0.0;
        count3 = 0;
        setContentView(R.layout.invsetlistview);
        invalidateOptionsMenu();
        ListView listView = findViewById(R.id.invlistview);
        radioGroup = findViewById(R.id.llinvset);
        whatsappcheck = findViewById(R.id.cbwappcheck);
        dist1 = findViewById(R.id.invlistdis1);
        dist1.setVisibility(View.INVISIBLE);
        dist1.setText(""+0.0);
        dist2 = findViewById(R.id.invlistdis2);
        dist2.setVisibility(View.INVISIBLE);
        dist2.setText(""+0.0);
        dist3 = findViewById(R.id.invlistdis3);
        dist3.setVisibility(View.INVISIBLE);
        dist3.setText(""+0.0);
        createtheinv = findViewById(R.id.invlistcreatebutton);
        for (int i = 0; i <setids.size() ; i++) {
            if(setids.get(i) == 1)
            {
                for (int j = 0; j <setbll1.size() ; j++) {
                    total1 = total1 + (setbll1.get(j).getQuantity() * setbll1.get(j).getPrice());
                }
                dist1.setVisibility(View.VISIBLE);
                dist1.setText("" + dis1);
                firstmap = new invlstsetmapper(set1,setbll1.size() + " selected",total1);
                firstmaplist.add(firstmap);
            }
            else if(setids.get(i) == 2)
            {
                for (int j = 0; j <setbll2.size() ; j++) {
                    total2 = total2 + (setbll2.get(j).getQuantity() * setbll2.get(j).getPrice());
                }
                dist2.setVisibility(View.VISIBLE);
                dist2.setText("" + dis2);
                firstmap = new invlstsetmapper(set2,setbll2.size() + " selected",total2);
                firstmaplist.add(firstmap);
            }
            else {
                for (int j = 0; j <setbll3.size() ; j++) {
                    count3 = count3 + setbll3.get(j).getQuantity();
                    total3 = total3 + (setbll3.get(j).getQuantity() * setbll3.get(j).getPrice());
                }
                dist3.setVisibility(View.VISIBLE);
                dist3.setText("" + dis3);
                firstmap = new invlstsetmapper(set3,setbll3.size() + " selected @ "+count3+" Pcs",total3);
                firstmaplist.add(firstmap);
            }
        }
        invlstsetadapter adapter = new invlstsetadapter(invoicecreation.this,R.layout.invcrtsetdispfrmt,firstmaplist);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bookmapsetup(position);
            }
        });
        createtheinv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(whatsappcheck.isChecked() && contactno != 0)
                {
                    createcontact(schlname+"Invoiceno"+invno);
                    wcheck = true;
                }
                else {
                    wcheck = false;
                }
                Integer checkid = radioGroup.getCheckedRadioButtonId();
                if(checkid == R.id.rbposprint)
                    invoicecreator(1);
                else
                    invoicecreator(2);
            }
        });
    }
    public void invoicecreator(Integer printflag)
    {
        companyhelper = new Companymgmthelper(invoicecreation.this);
//        Companymgmthelper.getInstance(invoicecreation.this);
        for (int i = 0; i <setbll1.size() ; i++) {
            Integer cmpid = companyhelper.getid(setbll1.get(i).getCname());
            bdbhelper = new bookdatabasehelper(invoicecreation.this,"School"+schname,"Company"+cmpid+"bookdet");
//            stockhelper = new wholestockdbhelper(invoicecreation.this,"Company"+cmpid);
//            bookdatabasehelper.getInstance(invoicecreation.this,"School"+schname,"Company"+cmpid+"bookdet");
//            wholestockdbhelper.getInstance(invoicecreation.this,"Company"+cmpid);
            Integer saleq = setbll1.get(i).getQuantity();
            Integer presentq = bdbhelper.getsalestock(setbll1.get(i).getName());
//            Integer presentstock = stockhelper.getsalestock(setbll1.get(i).getName());
            bdbhelper.updatesale(setbll1.get(i).getName(),presentq+saleq);
//            stockhelper.updatesalestock(setbll1.get(i).getName(),presentstock+saleq);
        }
        for (int i = 0; i <setbll2.size() ; i++) {
            Integer cmpid = companyhelper.getid(setbll2.get(i).getCname());
            bdbhelper = new bookdatabasehelper(invoicecreation.this,"School"+schname,"Company"+cmpid+"bookdet");
//            stockhelper = new wholestockdbhelper(invoicecreation.this,"Company"+cmpid);
//            bookdatabasehelper.getInstance(invoicecreation.this,"School"+schname,"Company"+cmpid+"bookdet");
//            wholestockdbhelper.getInstance(invoicecreation.this,"Company"+cmpid);
            Integer saleq = setbll2.get(i).getQuantity();
            Integer presentq = bdbhelper.getsalestock(setbll2.get(i).getName());
//            Integer presentstock = stockhelper.getsalestock(setbll2.get(i).getName());
            bdbhelper.updatesale(setbll2.get(i).getName(),presentq+saleq);
//            stockhelper.updatesalestock(setbll2.get(i).getName(),presentstock+saleq);
        }
        for (int i = 0; i <setbll3.size() ; i++) {
            Integer cmpid = companyhelper.getid(setbll3.get(i).getCname());
            bdbhelper = new bookdatabasehelper(invoicecreation.this,"School"+schname,"Company"+cmpid+"bookdet");
//            stockhelper = new wholestockdbhelper(invoicecreation.this,"Company"+cmpid);
//            bookdatabasehelper.getInstance(invoicecreation.this,"School"+schname,"Company"+cmpid+"bookdet");
//            wholestockdbhelper.getInstance(invoicecreation.this,"Company"+cmpid);
            Integer saleq = setbll3.get(i).getQuantity();
            Integer presentq = bdbhelper.getsalestock(setbll3.get(i).getName());
//            Integer presentstock = stockhelper.getsalestock(setbll3.get(i).getName());
            bdbhelper.updatesale(setbll3.get(i).getName(),presentq+saleq);
//            stockhelper.updatesalestock(setbll3.get(i).getName(),presentstock+saleq);
        }
        boolean rut = invoicenohelp.insert(invno, nameschool, nameclass);
        if (rut == true) {
            Toast.makeText(invoicecreation.this, "Invoice No  -  " + invno + " CREATED", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(invoicecreation.this, "Invoice Failed", Toast.LENGTH_SHORT).show();
        dt1 = Double.parseDouble(dist1.getText().toString());
        dt2 = Double.parseDouble(dist2.getText().toString());
        dt3 = Double.parseDouble(dist3.getText().toString());
        invhelp = new invoicehelper(invoicecreation.this,"School"+schname + "INVOICENO"+invno);
//        invoicehelper.getInstance(invoicecreation.this,"School" + schname,"INVOICENO"+invno);
//        Double tl1 = total1 - ((total1*dt1)/100.0);
//        tl1 = Math.round(tl1*100.0)/100.0;
//        Double tl2 = total2 - ((total2*dt2)/100.0);
//        tl2 = Math.round(tl2*100.0)/100.0;
//        Double tl3 = total3 - ((total3*dt3)/100.0);
//        tl3 = Math.round(tl3*100.0)/100.0;
//        Double gtotal = tl1+tl2+tl3;
//        AlertDialog.Builder builder = new AlertDialog.Builder(invoicecreation.this);
//        builder.setTitle("Information total after discount: ");
//        builder.setMessage("Total 1 = " + tl1+"\n Total 2 = "+ tl2 + "\n Total 3 = "+tl3 + "\n G.TOTAL = "+gtotal);
//        builder.setCancelable(true);
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Toast.makeText(invoicecreation.this, "THANKS TO ME", Toast.LENGTH_SHORT).show();
//                initialsetup();
//            }
//        });
//        AlertDialog dialog = builder.create();
//        dialog.show();
        try {
            if(printflag == 1)
                pospdf();
            if(printflag == 2)
                pdfgenerate();
        }catch (Exception e)
        {
            Toast.makeText(this, "FAAIELD", Toast.LENGTH_SHORT).show();
        }
    }
    public void bookmapsetup(final Integer pos)
    {
        flag = 2;
        setContentView(R.layout.samplelistview);
        invalidateOptionsMenu();
        final ListView listView1 = findViewById(R.id.splistview);
        if(setids.get(pos) == 1)
        {
            final invblladapter adap = new invblladapter(invoicecreation.this,R.layout.invbllsamplelayout,setbll1);
            listView1.setAdapter(adap);
            listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    setbll1.remove(position);
                    listView1.setAdapter(adap);
                }
            });
        }
        else if(setids.get(pos) == 2)
        {
            final invblladapter adap = new invblladapter(invoicecreation.this,R.layout.invbllsamplelayout,setbll2);
            listView1.setAdapter(adap);
            listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    setbll2.remove(position);
                    listView1.setAdapter(adap);
                }
            });
        }
        else
        {
            final invblladapter adap = new invblladapter(invoicecreation.this,R.layout.invbllsamplelayout,setbll3);
            listView1.setAdapter(adap);
            listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    setbll3.remove(position);
                    listView1.setAdapter(adap);
                }
            });
            listView1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(invoicecreation.this);
                    View mview = getLayoutInflater().inflate(R.layout.activity_quantityreset,null);
                    final EditText newquant = mview.findViewById(R.id.quantityedit);
                    builder.setView(mview)
                            .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    setbll3.get(position).setQuantity(Integer.parseInt(newquant.getText().toString()));
                                    dialog.dismiss();
                                    listView1.setAdapter(adap);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    listView1.setAdapter(adap);
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    return true;
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if(flag == 2)
            secondsetup();
        else if(flag == 1)
            initialsetup();
        else
            finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(flag == 1)
            getMenuInflater().inflate(R.menu.invbllmenu,menu);
        if(flag ==2)
            getMenuInflater().inflate(R.menu.itemselectmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.invbllrefresh:{
                if(flag == 1)
                {
                    listsetup();
                    secondsetup();
                    break;
                }
            }
            case R.id.itemseldone:{
                if(flag == 2)
                {
                    secondsetup();
                    break;
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void listsetup()
    {
        setids.clear();
        setbll1.clear();
        setbll2.clear();
        setbll3.clear();
        total1 = 0.0;
        total2 = 0.0;
        total3 = 0.0;
        dis1 = 0.0;
        dis2 = 0.0;
        dis3 = 0.0;
        count3 = 0;
        try {
            Cursor cursor = sethelp.display();
            if(cursor.getCount()>0)
            {
                cursor.moveToFirst();
                for (int i = 0; i <cursor.getCount() ; i++) {
                    setids.add(cursor.getInt(0));
                    cursor.moveToNext();
                }
                for (int i = 0; i <setids.size() ; i++) {
                    if(setids.get(i)==1)
                    {
                        set1 = sethelp.getname(1);
                        dis1 = sethelp.getdis(1);
                        bookhelp = new booklisthelper(invoicecreation.this,"School"+schname,"BooklistClass"+clsname+"Set1");
//                        booklisthelper.getInstance(invoicecreation.this,"School"+schname,"BooklistClass"+clsname+"Set1");
                        Cursor cursor1 = bookhelp.display();
                        if(cursor1.getCount()>0)
                        {
                            while (cursor1.moveToNext())
                            {
                                booklistlayout bll = new booklistlayout(cursor1.getString(0),cursor1.getString(1),cursor1.getDouble(2),cursor1.getInt(3));
                                setbll1.add(bll);
                            }
//                            for (int j = 0; j <setbll1.size() ; j++) {
//                                total1 = total1 + (setbll1.get(j).getQuantity() * setbll1.get(j).getPrice());
//                            }
                        }
                    }
                    else if(setids.get(i)==2)
                    {
                        set2 = sethelp.getname(2);
                        dis2 = sethelp.getdis(2);
                        bookhelp = new booklisthelper(invoicecreation.this,"School"+schname,"BooklistClass"+clsname+"Set2");
//                        booklisthelper.getInstance(invoicecreation.this,"School"+schname,"BooklistClass"+clsname+"Set2");
                        Cursor cursor1 = bookhelp.display();
                        if(cursor1.getCount()>0)
                        {
                            while (cursor1.moveToNext())
                            {
                                booklistlayout bll = new booklistlayout(cursor1.getString(0),cursor1.getString(1),cursor1.getDouble(2),cursor1.getInt(3));
                                setbll2.add(bll);
                            }
//                            for (int j = 0; j <setbll2.size() ; j++) {
//                                total2 = total2 + (setbll2.get(j).getQuantity() * setbll2.get(j).getPrice());
//                            }
                        }
                    }
                    else
                    {
                        set3 = sethelp.getname(3);
                        dis3 = sethelp.getdis(3);
                        bookhelp = new booklisthelper(invoicecreation.this,"School"+schname,"BooklistClass"+clsname+"Set3");
//                        booklisthelper.getInstance(invoicecreation.this,"School"+schname,"BooklistClass"+clsname+"Set3");
                        Cursor cursor1 = bookhelp.display();
                        if(cursor1.getCount()>0)
                        {
                            while (cursor1.moveToNext())
                            {
                                booklistlayout bll = new booklistlayout(cursor1.getString(0),cursor1.getString(1),cursor1.getDouble(2),cursor1.getInt(3));
                                setbll3.add(bll);
                            }
//                            for (int j = 0; j <setbll3.size() ; j++) {
//                                count3 = count3 + setbll3.get(j).getQuantity();
//                                total3 = total3 + (setbll3.get(j).getQuantity() * setbll3.get(j).getPrice());
//                            }
                        }
                    }
                }
            }
        }catch (Exception e)
        {
            Toast.makeText(this, "Booklist not created properly, Data missing", Toast.LENGTH_SHORT).show();
            boolean ret = invoicenohelp.delete(invno);
            if(ret)
                Toast.makeText(this,"No table exists, check booklist is created for all set", Toast.LENGTH_SHORT).show();
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
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(invoicecreation.this, android.R.layout.simple_spinner_item, schoolnlist);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                Schoolname.setAdapter(adapter);
            } else {
                Toast.makeText(this, "No schools, Create school first", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Database link failed,check school is created", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void classsetup() {
        try {
            Cursor cursor = classhelp.display();
            if (cursor.getCount() > 0) {
                classnlist.clear();
                classnlist.add(0, "Choose Class Name");
                while (cursor.moveToNext()) {
                    classnlist.add(cursor.getString(1));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(invoicecreation.this, android.R.layout.simple_spinner_item, classnlist);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                Classname.setAdapter(adapter);
            } else {
                Toast.makeText(this, "No classes, Create class first", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Database link failed,check class is created", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    public void pdfgenerate() throws IOException {
        width = 550;
        height = 800;
        dobj = new Date();
        Integer count = setbll1.size() + setbll2.size() + setbll3.size();
        PdfDocument pdfDocument = new PdfDocument();
        Paint mypaint = new Paint();
        Paint title = new Paint();
        Paint subtitle = new Paint();
        Paint last = new Paint();
        String custname = "CASH";
        Integer billno = invno;
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(width,height,1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        title.setTextAlign(Paint.Align.CENTER);
        title.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        title.setTextSize(18f);
        canvas.drawText("NARAYANI BOOK WORLD",width/2,20,title);

        subtitle.setTextAlign(Paint.Align.CENTER);
        subtitle.setTextSize(13f);
        canvas.drawText("Shop No - 15 & 16 , Pani Market Complex", width / 2, 35, subtitle);
        canvas.drawText("Uditnagar, Rourkela, Odisha - 769012", width / 2, 50, subtitle);
        canvas.drawText("Ph no: 7894565330", width / 2, 65, subtitle);
        canvas.drawText("GSTN : 21BEKPA6908Q1ZA", width / 2, 80, subtitle);

        title.setTextAlign(Paint.Align.CENTER);
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
        title.setTextSize(11f);
        canvas.drawText("CASH MEMO", width / 2, 100, title);

        subtitle.setTextAlign(Paint.Align.LEFT);
        subtitle.setTextSize(10f);
        canvas.drawText("Party Name : " + custname, 30, 120, subtitle);
        canvas.drawText("Contact No : " + contactno,30,130,subtitle);
        subtitle.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(nameclass,width/2,120,subtitle);
        canvas.drawText("User : Admin",width/2,130,subtitle);

        subtitle.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("INVOICE NO : " + "SN"+schname+"UN1INO"+billno, width - 20, 120, subtitle);
        dfm = new SimpleDateFormat("dd/MM/yyyy");
        canvas.drawText("DATE : " + dfm.format(dobj), width - 20, 130, subtitle);

        mypaint.setStyle(Paint.Style.STROKE);
        mypaint.setStrokeWidth(2);
        canvas.drawRect(20, 140, width - 20, 160, mypaint);

        mypaint.setTextSize(10f);
        mypaint.setTextAlign(Paint.Align.LEFT);
        mypaint.setStyle(Paint.Style.FILL);
        mypaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("Sl.", 25, 150, mypaint);
        canvas.drawText("Item Description", 70, 150, mypaint);
        mypaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("Quantity",380,150, mypaint);
        canvas.drawText("Price", 425, 150, mypaint);
        canvas.drawText("Discount",475,150,mypaint);
        canvas.drawText("Total ", width-25, 150, mypaint);
        mypaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawLine(45, 140, 45, 160, mypaint);
        canvas.drawLine(330, 140, 330, 160, mypaint);
        canvas.drawLine(385, 140, 385, 160, mypaint);
        canvas.drawLine(430, 140, 430, 160, mypaint);
        canvas.drawLine(480, 140, 480, 160, mypaint);
        mypaint.setTextSize(10f);
        mypaint.setStrokeWidth(1f);
        canvas.drawLine(20,160,20,height-80,mypaint);
        canvas.drawLine(45,160,45,height-80,mypaint);
        canvas.drawLine(330,160,330,height-80,mypaint);
        canvas.drawLine(385,160,385,height-80,mypaint);
        canvas.drawLine(430,160,430,height-100,mypaint);
        canvas.drawLine(480,160,480,height-100,mypaint);
        canvas.drawLine(width-20,160,width-20,height-80,mypaint);
        canvas.drawLine(20,height-80,width-20,height-80,mypaint);
        mypaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        Integer ctr = 0;
        Integer counter  = 1;
        Double totalamt = 0.00;
        Integer totalquan = 0;
        if(!setbll1.isEmpty())
        {
            for (int i = 0; i <setbll1.size() ; i++) {
                canvas.drawText(counter + ")",25,180 + (ctr * 12),mypaint);
                canvas.drawText(setbll1.get(i).getName(),50,180 + (ctr * 12),mypaint);
                mypaint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText(setbll1.get(i).getQuantity() + " Pcs",375,180 + (ctr * 12),mypaint);
                canvas.drawText("" + setbll1.get(i).getPrice(),420,180 + (ctr * 12),mypaint);
                canvas.drawText(dt1 + " %",470,180 + (ctr * 12),mypaint);
                Double total = setbll1.get(i).getPrice()*setbll1.get(i).getQuantity() - ((setbll1.get(i).getPrice()*setbll1.get(i).getQuantity()*dt1)/100);
                total = Math.round(total*100.00)/100.00;
                String amt = String.format("%.2f",total);
                canvas.drawText(amt,width-25,180 + (ctr * 12),mypaint);
                totalamt = totalamt + total;
                totalquan = totalquan + setbll1.get(i).getQuantity();
                mypaint.setTextAlign(Paint.Align.LEFT);
                boolean ret = invhelp.insert(setbll1.get(i).getName(),setbll1.get(i).getCname(),setbll1.get(i).getQuantity(),setbll1.get(i).getPrice(),dt1,total);
                if(!ret)
                    Toast.makeText(this, "Invoice Entry Failed", Toast.LENGTH_SHORT).show();
                ctr++;
                counter++;
            }
        }
        if(!setbll2.isEmpty())
        {
            for (int i = 0; i <setbll2.size() ; i++) {
                canvas.drawText(counter + ")",25,180 + (ctr * 12),mypaint);
                canvas.drawText(setbll2.get(i).getName(),50,180 + (ctr * 12),mypaint);
                mypaint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText(setbll2.get(i).getQuantity() + " Pcs",375,180 + (ctr * 12),mypaint);
                canvas.drawText("" + setbll2.get(i).getPrice(),420,180 + (ctr * 12),mypaint);
                canvas.drawText(dt2 + " %",470,180 + (ctr * 12),mypaint);
                Double total = setbll2.get(i).getPrice()*setbll2.get(i).getQuantity() - ((setbll2.get(i).getPrice()*setbll2.get(i).getQuantity()*dt2)/100);
                total = Math.round(total*100.00)/100.00;
                String amt = String.format("%.2f",total);
                canvas.drawText(amt,width-25,180 + (ctr * 12),mypaint);
                totalamt = totalamt + total;
                totalquan = totalquan + setbll2.get(i).getQuantity();
                mypaint.setTextAlign(Paint.Align.LEFT);
                boolean ret = invhelp.insert(setbll2.get(i).getName(),setbll2.get(i).getCname(),setbll2.get(i).getQuantity(),setbll2.get(i).getPrice(),dt2,total);
                if(!ret)
                    Toast.makeText(this, "Invoice Entry Failed", Toast.LENGTH_SHORT).show();
                ctr++;
                counter++;
            }
        }
        if(!setbll3.isEmpty())
        {
            for (int i = 0; i <setbll3.size() ; i++) {
                canvas.drawText(counter + ")",25,180 + (ctr * 12),mypaint);
                canvas.drawText(setbll3.get(i).getName(),50,180 + (ctr * 13),mypaint);
                mypaint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText(setbll3.get(i).getQuantity() + " Pcs",375,180 + (ctr * 12),mypaint);
                canvas.drawText("" + setbll3.get(i).getPrice(),420,180 + (ctr * 12),mypaint);
                canvas.drawText(dt3 + " %",470,180 + (ctr * 12),mypaint);
                Double total = setbll3.get(i).getPrice()*setbll3.get(i).getQuantity() - ((setbll3.get(i).getPrice()*setbll3.get(i).getQuantity()*dt3)/100);
                total = Math.round(total*100.00)/100.00;
                String amt = String.format("%.2f",total);
                canvas.drawText(amt,width-25,180 + (ctr * 12),mypaint);
                totalamt = totalamt + total;
                totalquan = totalquan + setbll3.get(i).getQuantity();
                mypaint.setTextAlign(Paint.Align.LEFT);
                boolean ret = invhelp.insert(setbll3.get(i).getName(),setbll3.get(i).getCname(),setbll3.get(i).getQuantity(),setbll3.get(i).getPrice(),dt3,total);
                if(!ret)
                    Toast.makeText(this, "Invoice Entry Failed", Toast.LENGTH_SHORT).show();
                ctr++;
                counter++;
            }
        }
        mypaint.setTextAlign(Paint.Align.RIGHT);
        mypaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        canvas.drawText("ROUND OFF",325,(ctr*15) + 180,mypaint);
        Double d = (Math.round(Math.round(totalamt)*100.0)/100.0) - totalamt;
        d = Math.round(d*100.0)/100.0;
        String rfamt = String.format("%.2f",d);
        canvas.drawText(rfamt,width-25,(ctr*15) + 180,mypaint);
        totalamt = (Math.round(Math.round(totalamt)*100.0)/100.0);
        invoicenohelp.updatetotalamt(billno,totalamt);
        String tamt = String.format("%.2f",totalamt);
        mypaint.setTextAlign(Paint.Align.LEFT);
        mypaint.setTextSize(11f);
        canvas.drawLine(330,height-100,385,height-100,mypaint);
        canvas.drawLine(385, height-100, width - 20, height-100, mypaint);
        mypaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC));
        mypaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(totalquan + " Pcs",375,height-85,mypaint);
        canvas.drawText("SUBTOTAL: Rs " + tamt,width-35, height-85, mypaint);
        canvas.drawText("for NARAYANI BOOK WORLD",width-20,height-70,mypaint);
        canvas.drawText("Authorised Signature",width-20,height-25,mypaint);
        mypaint.setTextSize(8f);
        mypaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("*Books once sold will not be returned at any circumstances.",25,height-50,mypaint);
        pdfDocument.finishPage(page);
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SalesInvoice/School"+schname;
        File dir = new File(path);
        if(!dir.exists())
            dir.mkdirs();
        String filepath = "Invoice - "+billno + ".pdf";
        File file = new File(dir, filepath);
        pdfDocument.writeTo(new FileOutputStream(file));
        viewpdf(filepath,"SalesInvoice/School"+schname);
        totalquan = 0;
        totalamt = 0.0;
        pdfDocument.close();
    }
    public void pospdf() throws IOException {
        dobj = new Date();
        width = 290;
        Integer count = setbll1.size() + setbll2.size() + setbll3.size();
        height = 300+count*40;
        PdfDocument pdfDocument = new PdfDocument();
        Paint mypaint = new Paint();
        Paint title = new Paint();
        Paint subtitle = new Paint();
        Paint last = new Paint();
        String custname = "CASH";
        Integer billno = invno;
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(width,height,1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        title.setTextAlign(Paint.Align.CENTER);
        title.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        title.setTextSize(14f);
        canvas.drawText("NARAYANI BOOK WORLD",width/2,30,title);

        subtitle.setTextAlign(Paint.Align.CENTER);
        subtitle.setTextSize(12f);
        canvas.drawText("Shop No - 15 & 16 , Pani Market Complex", width / 2, 40, subtitle);
        canvas.drawText("Uditnagar, Rourkela, Odisha - 769012", width / 2, 50, subtitle);
        canvas.drawText("Ph no: 7894565330", width / 2, 60, subtitle);
        canvas.drawText("GSTN : 21BEKPA6908Q1ZA", width / 2, 70, subtitle);

        title.setTextAlign(Paint.Align.CENTER);
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
        title.setTextSize(10f);
        canvas.drawText("POS INVOICE", width / 2, 90, title);

        subtitle.setTextAlign(Paint.Align.LEFT);
        subtitle.setTextSize(10f);
        canvas.drawText("Party Name : " + custname, 20, 110, subtitle);
        canvas.drawText("Contact No : " + contactno,20,120,subtitle);
        subtitle.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(nameclass,width/2,110,subtitle);
        canvas.drawText("User : Admin",width/2,120,subtitle);

        subtitle.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("INVOICE NO : " + "SN"+schname+"UN1INO"+billno, width - 20, 110, subtitle);
        dfm = new SimpleDateFormat("dd/MM/yyyy");
        canvas.drawText("DATE : " + dfm.format(dobj), width - 20, 120, subtitle);

        mypaint.setStyle(Paint.Style.STROKE);
        mypaint.setStrokeWidth(2);
        canvas.drawRect(20, 130, width - 20, 145, mypaint);

        mypaint.setTextSize(8f);
        mypaint.setTextAlign(Paint.Align.LEFT);
        mypaint.setStyle(Paint.Style.FILL);
        mypaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("Sl.", 22, 139, mypaint);
        canvas.drawText("Description", 32, 139, mypaint);
        mypaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("Qty",175,139, mypaint);
        canvas.drawText("Price", 212, 139, mypaint);
        canvas.drawText("Dis",235,139,mypaint);
        canvas.drawText("Total ", width-20, 139, mypaint);
        mypaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawLine(30, 130, 30, 145, mypaint);
        mypaint.setTextSize(12f);
        mypaint.setStrokeWidth(1f);
        canvas.drawLine(20,145,20,height-100,mypaint);
        canvas.drawLine(width-20,145,width-20,height-100,mypaint);
        canvas.drawLine(20,height-100,width-20,height-100,mypaint);
        mypaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        Integer ctr = 1;
        Integer counter  = 1;
        Double totalamt = 0.00;
        Integer totalquan = 0;
        if(!setbll1.isEmpty())
        {
            for (int i = 0; i <setbll1.size() ; i++) {
                canvas.drawText(counter + ")",22,150 + (ctr * 13),mypaint);
                if(setbll1.get(i).getName().length()>14) {
                    canvas.drawText(setbll1.get(i).getName().substring(0, 15), 40, 150 + (ctr * 13), mypaint);
                    ctr++;
                    if(setbll1.get(i).getName().length()>28) {
                        canvas.drawText(setbll1.get(i).getName().substring(15, 29), 40, 150 + (ctr * 13), mypaint);
                        ctr++;
                        if(setbll1.get(i).getName().length()>42) {
                            canvas.drawText(setbll1.get(i).getName().substring(29, 43), 40, 150 + (ctr * 13), mypaint);
                            ctr++;
                            if(setbll1.get(i).getName().length()>56) {
                                canvas.drawText(setbll1.get(i).getName().substring(43, 57), 40, 150 + (ctr * 13), mypaint);
                                ctr++;
                                if (setbll1.get(i).getName().length() > 70) {
                                    canvas.drawText(setbll1.get(i).getName().substring(57, 70), 40, 150 + (ctr * 13), mypaint);
                                } else {
                                    canvas.drawText(setbll1.get(i).getName().substring(57), 40, 150 + (ctr * 13), mypaint);
                                }
                            }else
                                canvas.drawText(setbll1.get(i).getName().substring(43), 40, 150 + (ctr * 13), mypaint);
                        }else
                            canvas.drawText(setbll1.get(i).getName().substring(29), 40, 150 + (ctr * 13), mypaint);
                    }else
                        canvas.drawText(setbll1.get(i).getName().substring(15), 40, 150 + (ctr * 13), mypaint);
                }
                else
                    canvas.drawText(setbll1.get(i).getName(), 40, 150 + (ctr * 13), mypaint);
                mypaint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText(setbll1.get(i).getQuantity() + " Pcs",165,150 + (ctr * 13),mypaint);
                canvas.drawText("" + setbll1.get(i).getPrice(),200,150 + (ctr * 13),mypaint);
                mypaint.setTextSize(9f);
                canvas.drawText(dt1 + " %",width - 65,150 + (ctr * 13),mypaint);
                mypaint.setTextSize(12f);
                Double total = setbll1.get(i).getPrice()*setbll1.get(i).getQuantity() - ((setbll1.get(i).getPrice()*setbll1.get(i).getQuantity()*dt1)/100);
                total = Math.round(total*100.00)/100.00;
                String amt = String.format("%.2f",total);
                canvas.drawText(amt,width-20,150 + (ctr * 13),mypaint);
                totalamt = totalamt + total;
                totalquan = totalquan + setbll1.get(i).getQuantity();
                mypaint.setTextAlign(Paint.Align.LEFT);
                boolean ret = invhelp.insert(setbll1.get(i).getName(),setbll1.get(i).getCname(),setbll1.get(i).getQuantity(),setbll1.get(i).getPrice(),dt1,total);
                if(!ret)
                    Toast.makeText(this, "Invoice Entry Failed", Toast.LENGTH_SHORT).show();
                ctr++;
                counter++;
            }
        }
        if(!setbll2.isEmpty())
        {
            for (int i = 0; i <setbll2.size() ; i++) {
                canvas.drawText(counter + ")",22,150 + (ctr * 13),mypaint);
                if(setbll2.get(i).getName().length()>14) {
                    canvas.drawText(setbll2.get(i).getName().substring(0, 15), 40, 150 + (ctr * 13), mypaint);
                    ctr++;
                    if(setbll2.get(i).getName().length()>28) {
                        canvas.drawText(setbll2.get(i).getName().substring(15, 29), 40, 150 + (ctr * 13), mypaint);
                        ctr++;
                        if(setbll2.get(i).getName().length()>42) {
                            canvas.drawText(setbll2.get(i).getName().substring(29, 43), 40, 150 + (ctr * 13), mypaint);
                            ctr++;
                            if(setbll2.get(i).getName().length()>56) {
                                canvas.drawText(setbll2.get(i).getName().substring(43, 57), 40, 150 + (ctr * 13), mypaint);
                                ctr++;
                                if(setbll2.get(i).getName().length()>70) {
                                    canvas.drawText(setbll2.get(i).getName().substring(57, 70), 40, 150 + (ctr * 13), mypaint);
                                }else
                                    canvas.drawText(setbll2.get(i).getName().substring(57), 40, 150 + (ctr * 13), mypaint);
                            }else
                                canvas.drawText(setbll2.get(i).getName().substring(43), 40, 150 + (ctr * 13), mypaint);
                        }else
                            canvas.drawText(setbll2.get(i).getName().substring(29), 40, 150 + (ctr * 13), mypaint);
                    }else
                        canvas.drawText(setbll2.get(i).getName().substring(15), 40, 150 + (ctr * 13), mypaint);
                }
                else
                    canvas.drawText(setbll2.get(i).getName(), 40, 150 + (ctr * 13), mypaint);
                mypaint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText(setbll2.get(i).getQuantity() + " Pcs",165,150 + (ctr * 13),mypaint);
                canvas.drawText("" + setbll2.get(i).getPrice(),200,150 + (ctr * 13),mypaint);
                mypaint.setTextSize(9f);
                canvas.drawText(dt2 + " %",width - 65,150 + (ctr * 13),mypaint);
                mypaint.setTextSize(12f);
                Double total = setbll2.get(i).getPrice()*setbll2.get(i).getQuantity() - ((setbll2.get(i).getPrice()*setbll2.get(i).getQuantity()*dt2)/100);
                total = Math.round(total*100.00)/100.00;
                String amt = String.format("%.2f",total);
                canvas.drawText(amt,width-20,150 + (ctr * 13),mypaint);
                totalamt = totalamt + total;
                totalquan = totalquan + setbll2.get(i).getQuantity();
                mypaint.setTextAlign(Paint.Align.LEFT);
                boolean ret = invhelp.insert(setbll2.get(i).getName(),setbll2.get(i).getCname(),setbll2.get(i).getQuantity(),setbll2.get(i).getPrice(),dt2,total);
                if(!ret)
                    Toast.makeText(this, "Invoice Entry Failed", Toast.LENGTH_SHORT).show();
                ctr++;
                counter++;
            }
        }
        if(!setbll3.isEmpty())
        {
            for (int i = 0; i <setbll3.size() ; i++) {
                canvas.drawText(counter + ")",22,150 + (ctr * 13),mypaint);
                if(setbll3.get(i).getName().length()>14) {
                    canvas.drawText(setbll3.get(i).getName().substring(0, 15), 40, 150 + (ctr * 13), mypaint);
                    ctr++;
                    if(setbll3.get(i).getName().length()>28) {
                        canvas.drawText(setbll3.get(i).getName().substring(15, 29), 40, 150 + (ctr * 13), mypaint);
                        ctr++;
                        if(setbll3.get(i).getName().length()>42) {
                            canvas.drawText(setbll3.get(i).getName().substring(29, 43), 40, 150 + (ctr * 13), mypaint);
                            ctr++;
                            if(setbll3.get(i).getName().length()>56) {
                                canvas.drawText(setbll3.get(i).getName().substring(43, 57), 40, 150 + (ctr * 13), mypaint);
                                ctr++;
                                if(setbll3.get(i).getName().length()>70) {
                                    canvas.drawText(setbll3.get(i).getName().substring(57, 70), 40, 150 + (ctr * 13), mypaint);
                                }else
                                    canvas.drawText(setbll3.get(i).getName().substring(57), 40, 150 + (ctr * 13), mypaint);
                            }else
                                canvas.drawText(setbll3.get(i).getName().substring(43), 40, 150 + (ctr * 13), mypaint);
                        }else
                            canvas.drawText(setbll3.get(i).getName().substring(29), 40, 150 + (ctr * 13), mypaint);
                    }else
                        canvas.drawText(setbll3.get(i).getName().substring(15), 40, 150 + (ctr * 13), mypaint);
                }
                else
                    canvas.drawText(setbll3.get(i).getName(), 40, 150 + (ctr * 13), mypaint);
                mypaint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText(setbll3.get(i).getQuantity() + " Pcs",165,150 + (ctr * 13),mypaint);
                canvas.drawText("" + setbll3.get(i).getPrice(),200,150 + (ctr * 13),mypaint);
                mypaint.setTextSize(9f);
                canvas.drawText(dt3 + " %",width - 65,150 + (ctr * 13),mypaint);
                mypaint.setTextSize(12f);
                Double total = setbll3.get(i).getPrice()*setbll3.get(i).getQuantity() - ((setbll3.get(i).getPrice()*setbll3.get(i).getQuantity()*dt3)/100);
                total = Math.round(total*100.00)/100.00;
                String amt = String.format("%.2f",total);
                canvas.drawText(amt,width-20,150 + (ctr * 13),mypaint);
                totalamt = totalamt + total;
                totalquan = totalquan + setbll3.get(i).getQuantity();
                mypaint.setTextAlign(Paint.Align.LEFT);
                boolean ret = invhelp.insert(setbll3.get(i).getName(),setbll3.get(i).getCname(),setbll3.get(i).getQuantity(),setbll3.get(i).getPrice(),dt3,total);
                if(!ret)
                    Toast.makeText(this, "Invoice Entry Failed", Toast.LENGTH_SHORT).show();
                ctr++;
                counter++;
            }
        }
        mypaint.setTextAlign(Paint.Align.RIGHT);
        mypaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        canvas.drawText("ROUND OFF",140,(ctr*15) + 150,mypaint);
        Double d = (Math.round(Math.round(totalamt)*100.0)/100.0) - totalamt;
        d = Math.round(d*100.0)/100.0;
        String rfamt = String.format("%.2f",d);
        canvas.drawText(rfamt,width-20,(ctr*15) + 150,mypaint);
        totalamt = (Math.round(Math.round(totalamt)*100.0)/100.0);
        invoicenohelp.updatetotalamt(billno,totalamt);
        String tamt = String.format("%.2f",totalamt);
        mypaint.setTextAlign(Paint.Align.LEFT);
        mypaint.setTextSize(11f);
        canvas.drawLine(20,height-120,187,height-120,mypaint);
        canvas.drawLine(187, height-120, width - 20, height-120, mypaint);
        mypaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC));
        mypaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(totalquan + " Pcs",150,height-110,mypaint);
        canvas.drawText("SUBTOTAL: Rs " + tamt,width-20, height-110, mypaint);
        canvas.drawText("for NARAYANI BOOK WORLD",width-20,height-60,mypaint);
        canvas.drawText("Authorised Signature",width-20,height-30,mypaint);
        mypaint.setTextSize(8f);
        mypaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("*Books once sold will not be returned at any circumstances.",20,height-70,mypaint);
        pdfDocument.finishPage(page);
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SalesInvoice/School" + schname;
        File dir = new File(path);
        if(!dir.exists())
            dir.mkdirs();
        String filepath = "Invoice - "+billno + ".pdf";
        File file = new File(dir, filepath);
        pdfDocument.writeTo(new FileOutputStream(file));
        viewpdf(filepath,"SalesInvoice/School"+schname);
        totalquan = 0;
        totalamt = 0.0;
        pdfDocument.close();
    }
    private void createcontact(String savename)
    {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        int rawContact_NewID = ops.size();

        try {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE,null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME,null)
                    .build());
        }catch (Exception e)
        {
            Toast.makeText(invoicecreation.this, "Account Type not found", Toast.LENGTH_SHORT).show();
            return;
        }
        String name = savename;
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,rawContact_NewID)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,name)
                .build());
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,rawContact_NewID)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,contactno)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                .build());
        ContentProviderResult[] res = new ContentProviderResult[0];
        try {
            res = getContentResolver().applyBatch(ContactsContract.AUTHORITY,ops);
        }catch (RemoteException | OperationApplicationException e)
        {
            Toast.makeText(invoicecreation.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        if(res!=null && res[0] !=null)
        {
            Uri newContacturi = res[0].uri;
            Toast.makeText(invoicecreation.this, "Contact added " + newContacturi, Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(invoicecreation.this, "Failed", Toast.LENGTH_SHORT).show();
        }
    }
    private void viewpdf(String file, String directory)
    {
        File pdffile = new File(Environment.getExternalStorageDirectory() + "/" + directory + "/" + file);
        final Uri path = FileProvider.getUriForFile(this,BuildConfig.APPLICATION_ID + ".provider",pdffile);
        if(wcheck) {
            PackageManager packageManager = getPackageManager();
            boolean rst;
            try {
                packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                rst = true;
            } catch (PackageManager.NameNotFoundException e) {
                rst = false;
            }
            if (rst) {
                Toast.makeText(invoicecreation.this, "Whatsapp found", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(Intent.ACTION_VIEW);
//                        intent.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(path));
//                        intent.setDataAndType(Uri.parse("http://api.whatsapp.com/send?phone=+91"+no+"&text="+msg),"application/pdf");
//                        startActivity(intent);
                Intent intent = new Intent("android.intent.action.MAIN");
                intent.putExtra(Intent.EXTRA_STREAM, path);
                intent.putExtra("jid", "91" + contactno + "@s.whatsapp.net");
                intent.setAction(Intent.ACTION_SEND);
                intent.setPackage("com.whatsapp");
                intent.setType("application/pdf");
                startActivity(intent);
                initialsetup();
            } else {
                Toast.makeText(invoicecreation.this, "Whatsapp not found", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(invoicecreation.this, "Invoice Generated Successfully", Toast.LENGTH_SHORT).show();
        Intent vpdf = new Intent(Intent.ACTION_VIEW);
        vpdf.setDataAndType(path,"application/pdf");
        vpdf.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        vpdf.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try{
             startActivity(vpdf);
             initialsetup();
           } catch (ActivityNotFoundException e) {
               Toast.makeText(invoicecreation.this, "pdf not found, upload failed", Toast.LENGTH_SHORT).show();
        }
        }
    }
}
