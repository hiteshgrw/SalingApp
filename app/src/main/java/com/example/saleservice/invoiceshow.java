package com.example.saleservice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ActivityNotFoundException;
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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.saleservice.adapter.invoiceshowadapter;
import com.example.saleservice.classes.classidamountmap;
import com.example.saleservice.classes.invoiceamountmap;
import com.example.saleservice.classes.invoiceshowclass;
import com.example.saleservice.classes.schoolusername;
import com.example.saleservice.classes.userdetails;
import com.example.saleservice.helper.Classschhelper;
import com.example.saleservice.helper.Companymgmthelper;
import com.example.saleservice.helper.bookdatabasehelper;
import com.example.saleservice.helper.invoicehelper;
import com.example.saleservice.helper.invoicenomap;
import com.example.saleservice.helper.mainuserinvoicenomap;
import com.example.saleservice.helper.muserinvoicehelper;
import com.example.saleservice.helper.schoolmgmthelper;
import com.example.saleservice.helper.userbdbhelper;
import com.example.saleservice.helper.userhelper;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class invoiceshow extends AppCompatActivity {
    private Spinner schoolname,username;
    private Button show;
    private userhelper userhelp;
    private schoolmgmthelper schoolhelp;
    private List<String> schoolnlist,usernlist;
    private List<String> invnolist;
    private List<Integer> nolist;
    private List<invoiceshowclass> invoicelist;
    private invoicenomap invoicenohelp;
    private mainuserinvoicenomap userinvoicenohelp;
    private Classschhelper classhelp;
    private invoicehelper invoicehelp;
    private muserinvoicehelper userinvoicehelp;
    private invoiceshowclass  invoiceclass;
    private invoiceshowadapter invoiceadapter;
    private bookdatabasehelper bdbhelper;
    private userbdbhelper userstockhelper;
    private invoiceamountmap invoiceamtdetails;
    private Companymgmthelper companyhelp;
    private Integer invno;
    private Integer schname,usname;
    private Integer flag = -1;
    private List<invoiceamountmap> invamtdetlist;
    private List<classidamountmap> cidamtlist;
    private classidamountmap cidamtclass;
    private schoolusername schusr;
    private List<schoolusername> schusrlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        SQLiteDatabase.loadLibs(this);
        initialsetup();
    }
    public void initialsetup()
    {
        flag = 0;
        setContentView(R.layout.actitvity_maininvoiceshow);
        schoolname = findViewById(R.id.spmshowinvsch);
        schoolname.setEnabled(false);
        username = findViewById(R.id.spmshowinvuser);
        show = findViewById(R.id.btmshowinvoice);
        schoolhelp = new schoolmgmthelper(invoiceshow.this);
        userhelp = new userhelper(this);
//        schoolmgmthelper.getInstance(invoiceshow.this);
        schoolnlist = new ArrayList<>();
        usernlist = new ArrayList<>();
        schusrlist = new ArrayList<>();
        usersetup();
        username.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0 && !schusrlist.get(position).getUname().equals("admin"))
                {
                    schname = userhelp.getschid(schusrlist.get(position).getUname());
                    usname = userhelp.getid(schusrlist.get(position).getUname(),schname);
                    classhelp = new Classschhelper(invoiceshow.this,"School"+schname,"classmap");
//                    schoolnlist.clear();
//                    schoolnlist.add(schoolhelp.getname(schname));
                    userinvoicenohelp = new mainuserinvoicenomap(invoiceshow.this,"School"+schname,usname);
                    usersecondsetup();
                }
                else if(schusrlist.get(position).getUname().equals("admin")){
                    usname = 1;
                    schoolname.setEnabled(true);
                    schname = schoolhelp.getid(schusrlist.get(position).getSchool());
                    invoicenohelp = new invoicenomap(invoiceshow.this,"School"+schname);
                    classhelp = new Classschhelper(invoiceshow.this,"School"+schname,"classmap");
                    secondsetup();
                }
                else
                    schoolname.setEnabled(false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
//        show.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(schoolname.getSelectedItemPosition()!=0)
//                {
//                    schname = schoolhelp.getid(schoolname.getSelectedItem().toString());
//
////                    invoicenomap.getInstance(invoiceshow.this,"School"+schname);
//
//                }
//                else {
//                    Toast.makeText(invoiceshow.this, "Field cannot be empty", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }
    public void secondsetup()
    {
        List<Integer> classidchecklist = new ArrayList<>();
        flag = 1;
        setContentView(R.layout.samplelistview);
        invalidateOptionsMenu();
        ListView  listView = findViewById(R.id.splistview);
        invnolist = new ArrayList<>();
        nolist = new ArrayList<>();
        invamtdetlist = new ArrayList<>();
        cidamtlist = new ArrayList<>();
        try {
            Cursor cursor = invoicenohelp.display();
            if(cursor.getCount()>0)
            {
                invnolist.clear();
                invamtdetlist.clear();
                nolist.clear();
                cidamtlist.clear();
                classidchecklist.clear();
                while (cursor.moveToNext())
                {
                    nolist.add(cursor.getInt(0));
                    invnolist.add(cursor.getInt(0) + " @ " + cursor.getString(2) + " @ " + cursor.getDouble(3));
                    Integer clid = classhelp.getid(cursor.getString(2));
                    if(!classidchecklist.contains(clid))
                    {
                        classidchecklist.add(clid);
                        cidamtclass = new classidamountmap(clid,cursor.getDouble(3));
                        cidamtlist.add(cidamtclass);
                    }
                    else {
                        Integer post = classidchecklist.indexOf(clid);
                        Double tempamt = cidamtlist.get(post).getAmount();
                        cidamtlist.get(post).setAmount(tempamt + cursor.getDouble(3));
                    }
                    invoiceamtdetails = new invoiceamountmap(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getDouble(3));
                    invamtdetlist.add(invoiceamtdetails);
                }
            }
            else {
                Toast.makeText(this, "No invoice exists", Toast.LENGTH_SHORT).show();
                finish();
            }
        }catch (Exception e)
        {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(invoiceshow.this,android.R.layout.simple_list_item_1,invnolist);
        listView.setAdapter(adapter);
        totaldisp();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                thirdsetup(position);
            }
        });
    }
    public void totaldisp()
    {
        Double shtotalamt = 0.0;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < cidamtlist.size() ; i++) {
            stringBuilder.append(classhelp.getname(cidamtlist.get(i).getId())).append(" = Rs ").append(cidamtlist.get(i).getAmount()).append("\n");
            shtotalamt = shtotalamt + cidamtlist.get(i).getAmount();
        }
        stringBuilder.append("\n\n G TOTAL: Rs ").append(shtotalamt);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Total Amount : ");
        builder.setMessage(stringBuilder);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public void usersecondsetup()
    {
        List<Integer> classidchecklist = new ArrayList<>();
        flag = 1;
        setContentView(R.layout.samplelistview);
        invalidateOptionsMenu();
        ListView  listView = findViewById(R.id.splistview);
        invnolist = new ArrayList<>();
        nolist = new ArrayList<>();
        invamtdetlist = new ArrayList<>();
        cidamtlist = new ArrayList<>();
        try {
            Cursor cursor = userinvoicenohelp.display();
            if(cursor.getCount()>0)
            {
                invnolist.clear();
                invamtdetlist.clear();
                cidamtlist.clear();
                nolist.clear();
                classidchecklist.clear();
                while (cursor.moveToNext())
                {
                    nolist.add(cursor.getInt(0));
                    invnolist.add(cursor.getInt(0) + " @ " + cursor.getString(2) + " @ " + cursor.getDouble(3));
                    Integer clid = classhelp.getid(cursor.getString(2));
                    if(!classidchecklist.contains(clid))
                    {
                        classidchecklist.add(clid);
                        cidamtclass = new classidamountmap(clid,cursor.getDouble(3));
                        cidamtlist.add(cidamtclass);
                    }
                    else {
                        Integer post = classidchecklist.indexOf(clid);
                        Double tempamt = cidamtlist.get(post).getAmount();
                        cidamtlist.get(post).setAmount(tempamt + cursor.getDouble(3));
                    }
                    invoiceamtdetails = new invoiceamountmap(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getDouble(3));
                    invamtdetlist.add(invoiceamtdetails);
                }
            }
            else {
                Toast.makeText(this, "No invoice exists", Toast.LENGTH_SHORT).show();
                finish();
            }
        }catch (Exception e)
        {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(invoiceshow.this,android.R.layout.simple_list_item_1,invnolist);
        listView.setAdapter(adapter);
        totaldisp();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                userthirdsetup(position);
            }
        });
    }
    public void thirdsetup(Integer pos)
    {
        flag = 2;
        setContentView(R.layout.samplelistview);
        invalidateOptionsMenu();
        ListView listView1 = findViewById(R.id.splistview);
        invoicehelp = new invoicehelper(invoiceshow.this,"School"+schname + "INVOICENO"+nolist.get(pos));
//        invoicehelper.getInstance(invoiceshow.this,"School" + schname,"INVOICENO"+nolist.get(pos));
        invno = nolist.get(pos);
        invoicelist = new ArrayList<>();
        try {
            Cursor cursor = invoicehelp.display();
            if(cursor.getCount()>0)
            {
                invoicelist.clear();
                while (cursor.moveToNext())
                {
                    invoiceclass = new invoiceshowclass(cursor.getString(1),cursor.getString(2),cursor.getInt(3),cursor.getDouble(4),cursor.getDouble(5),cursor.getDouble(6));
                    invoicelist.add(invoiceclass);
                }
                invoiceadapter = new invoiceshowadapter(invoiceshow.this,R.layout.invoiceviewerlayout,invoicelist);
                listView1.setAdapter(invoiceadapter);
            }
            else {
                Toast.makeText(this, "No invoice exists", Toast.LENGTH_SHORT).show();
                finish();
            }
        }catch (Exception e)
        {
            Toast.makeText(this, "Database Link Failed, Check invoice is created first", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    public void userthirdsetup(Integer pos)
    {
        flag = 2;
        setContentView(R.layout.samplelistview);
        invalidateOptionsMenu();
        ListView listView1 = findViewById(R.id.splistview);
        userinvoicehelp = new muserinvoicehelper(invoiceshow.this,"School"+schname + "INVOICENO"+nolist.get(pos),usname);
//        invoicehelper.getInstance(invoiceshow.this,"School" + schname,"INVOICENO"+nolist.get(pos));
        invno = nolist.get(pos);
        invoicelist = new ArrayList<>();
        try {
            Cursor cursor = userinvoicehelp.display();
            if(cursor.getCount()>0)
            {
                invoicelist.clear();
                while (cursor.moveToNext())
                {
                    invoiceclass = new invoiceshowclass(cursor.getString(1),cursor.getString(2),cursor.getInt(3),cursor.getDouble(4),cursor.getDouble(5),cursor.getDouble(6));
                    invoicelist.add(invoiceclass);
                }
                invoiceadapter = new invoiceshowadapter(invoiceshow.this,R.layout.invoiceviewerlayout,invoicelist);
                listView1.setAdapter(invoiceadapter);
            }
            else {
                Toast.makeText(this, "No invoice exists", Toast.LENGTH_SHORT).show();
                finish();
            }
        }catch (Exception e)
        {
            Toast.makeText(this, "Database Link Failed, Check invoice is created first", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    public void resetdata()
    {
        companyhelp = new Companymgmthelper(invoiceshow.this);
//        Companymgmthelper.getInstance(invoiceshow.this);
        for (int i = 0; i <invoicelist.size() ; i++) {
            Integer cpid = companyhelp.getid(invoicelist.get(i).getCmpname());
            bdbhelper = new bookdatabasehelper(invoiceshow.this,"School"+schname,"Company"+cpid+"bookdet");
//            stockhelp = new wholestockdbhelper(invoiceshow.this,"Company"+cpid);
//            bookdatabasehelper.getInstance(invoiceshow.this,"School"+schname,"Company"+cpid+"bookdet");
//            wholestockdbhelper.getInstance(invoiceshow.this,"Company"+cpid);
            Integer getsold = bdbhelper.getsalestock(invoicelist.get(i).getName());
//            Integer getnetsold = stockhelp.getsalestock(invoicelist.get(i).getName());
            bdbhelper.updatesale(invoicelist.get(i).getName(),getsold - invoicelist.get(i).getQuanity());
//            stockhelp.updatesalestock(invoicelist.get(i).getName(),getnetsold - invoicelist.get(i).getQuanity());
        }
    }
    public void userresetdata()
    {
        companyhelp = new Companymgmthelper(invoiceshow.this);
//        Companymgmthelper.getInstance(invoiceshow.this);
        for (int i = 0; i <invoicelist.size() ; i++) {
            Integer cpid = companyhelp.getid(invoicelist.get(i).getCmpname());
            userstockhelper = new userbdbhelper(invoiceshow.this,"School"+schname,"Company"+cpid+"bookdet",usname);
//            stockhelp = new wholestockdbhelper(invoiceshow.this,"Company"+cpid);
//            bookdatabasehelper.getInstance(invoiceshow.this,"School"+schname,"Company"+cpid+"bookdet");
//            wholestockdbhelper.getInstance(invoiceshow.this,"Company"+cpid);
            Integer getsold = userstockhelper.getsalestock(invoicelist.get(i).getName());
//            Integer getnetsold = stockhelp.getsalestock(invoicelist.get(i).getName());
            userstockhelper.updatesale(invoicelist.get(i).getName(),getsold - invoicelist.get(i).getQuanity());
//            stockhelp.updatesalestock(invoicelist.get(i).getName(),getnetsold - invoicelist.get(i).getQuanity());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(flag == 2)
        {
            getMenuInflater().inflate(R.menu.delmenu,menu);
            menu.getItem(0).setTitle("DELETE INVOICE");
            menu.getItem(1).setTitle("PRINT INVOICE");
        }
        if(flag == 1)
        {
            getMenuInflater().inflate(R.menu.invtotalmenu,menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.delquan:{
                if(usname == 1) {
                    resetdata();
                    invoicenohelp.delete(invno);
                    invoicehelp.droptable();
                    flag = 1;
                    secondsetup();
                }
                else {
                    userresetdata();
                    userinvoicenohelp.delete(invno);
                    userinvoicehelp.droptable();
                    flag = 1;
                    usersecondsetup();
                }
                break;
            }
            case R.id.delinv:{
                if(usname == 1) {
                    String filepath = "Invoice - " + invno + ".pdf";
                    viewpdf(filepath, "SalesInvoice/School" + schname);
                }
                else {
                    Toast.makeText(this, "User bills cannot be printed", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.totalclasswisesale:{
                totaldisp();
                break;
            }
            case R.id.printentireusersale:{
                try {
                    pdfgenerate();
                } catch (IOException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
        return super.onOptionsItemSelected(item);
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
            Toast.makeText(invoiceshow.this, "pdf not found, upload failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if(flag == 2)
        {
            flag = 1;
            if(usname == 1)
                secondsetup();
            else
                usersecondsetup();
        }
        else if(flag == 1)
        {
            flag = 0;
            initialsetup();
        }
        else {
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
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(invoiceshow.this, android.R.layout.simple_spinner_item, schoolnlist);
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
    public void usersetup() {
        List<String> list = new ArrayList<>();
        try {
            Cursor cursor = userhelp.display();
            if (cursor.getCount() > 0) {
                usernlist.clear();
                list.clear();
                schusrlist.clear();
                usernlist.add(0, "Choose User");
                list.add(0,"Choose User");
                schusrlist.add(0,new schoolusername("Choose User","Choose School"));
                while (cursor.moveToNext()) {
                    usernlist.add(cursor.getString(1));
                    list.add(cursor.getString(1)+" @ "+schoolhelp.getname(cursor.getInt(4)));
                    schusrlist.add(new schoolusername(cursor.getString(1),schoolhelp.getname(cursor.getInt(4))));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(invoiceshow.this, android.R.layout.simple_spinner_item, list);
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
        canvas.drawText("School Name : " + schoolhelp.getname(schname), 30, 30, title);
        for (i = 0; i <cidamtlist.size(); i++) {
            if(ctr<=80) {
                for (j = 0; j < invamtdetlist.size(); j++) {
                    if (classhelp.getid(invamtdetlist.get(j).getClassname()) == cidamtlist.get(i).getId()) {
                        title.setTextAlign(Paint.Align.LEFT);
                        canvas.drawText("Invoice No - " + invamtdetlist.get(j).getInvno(), 30, 50 + ctr * 10, title);
                        title.setTextAlign(Paint.Align.CENTER);
                        canvas.drawText(invamtdetlist.get(j).getClassname(), width / 2, 50 + ctr * 10, title);
                        title.setTextAlign(Paint.Align.RIGHT);
                        canvas.drawText("Rs " + invamtdetlist.get(j).getAmount(), width - 30, 50 + ctr * 10, title);
                        ctr++;
                    }
                }
                mypaint.setStrokeWidth(1);
                canvas.drawLine(width / 2, 50 + ctr * 10 - 5, width - 30, 50 + ctr * 10 - 5, mypaint);
                ctr++;
                title.setTextAlign(Paint.Align.RIGHT);
                title.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
                canvas.drawText("Total : Rs " + cidamtlist.get(i).getAmount(), width - 30, 50 + ctr * 10, title);
                title.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.NORMAL));
                total = total + cidamtlist.get(i).getAmount();
                ctr++;
                nxtpgae = 0;
            }
            else {
                nxtpgae = 1;
            }
        }
        if(nxtpgae == 0) {
            title.setTextAlign(Paint.Align.CENTER);
            title.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
            canvas.drawText("G.TOTAL : " + total, width / 2, 60 + ctr * 15, title);
        }
        pdfDocument.finishPage(page);
        if(nxtpgae == 1)
        {
            PdfDocument.PageInfo pageInfo2 = new PdfDocument.PageInfo.Builder(width,height,1).create();
            PdfDocument.Page page2 = pdfDocument.startPage(pageInfo2);
            Canvas canvas2 = page2.getCanvas();
            title.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
            title.setTextSize(10f);
            ctr = 0;
            canvas2.drawText("Party Name : " + schoolhelp.getname(schname), 30, 30, title);
            for (; i <cidamtlist.size(); i++) {
                if(ctr<=80) {
                    for (j = 0; j < invamtdetlist.size(); j++) {
                        if (classhelp.getid(invamtdetlist.get(j).getClassname()) == cidamtlist.get(i).getId()) {
                            title.setTextAlign(Paint.Align.LEFT);
                            canvas2.drawText("Invoice No - " + invamtdetlist.get(j).getInvno(), 30, 50 + ctr * 15, title);
                            title.setTextAlign(Paint.Align.CENTER);
                            canvas2.drawText(invamtdetlist.get(j).getClassname(), width / 2, 50 + ctr * 15, title);
                            title.setTextAlign(Paint.Align.RIGHT);
                            canvas2.drawText("Rs " + invamtdetlist.get(j).getAmount(), width - 30, 50 + ctr * 15, title);
                            ctr++;
                        }
                    }
                    mypaint.setStrokeWidth(1);
                    canvas2.drawLine(width / 2, 50 + ctr * 15 - 8, width - 30, 50 + ctr * 15 - 8, mypaint);
                    ctr++;
                    title.setTextAlign(Paint.Align.RIGHT);
                    title.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
                    canvas2.drawText("Total : Rs " + cidamtlist.get(i).getAmount(), width - 30, 50 + ctr * 15, title);
                    title.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.NORMAL));
                    total = total + cidamtlist.get(i).getAmount();
                    ctr++;
                    nxtpgae = 0;
                }
                else {
                    nxtpgae = 1;
                }
            }
            title.setTextAlign(Paint.Align.CENTER);
            title.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
            canvas2.drawText("G.TOTAL : "+total,width/2,60+ctr*15,title);
            pdfDocument.finishPage(page2);
        }
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SalesInvoice/User"+usname;
        File dir = new File(path);
        if(!dir.exists())
            dir.mkdirs();
        String filepath = "Invsummary.pdf";
        File file = new File(dir, filepath);
        if(file.exists())
            file.delete();
        pdfDocument.writeTo(new FileOutputStream(file));
        viewpdf(filepath,"SalesInvoice/User"+usname);
        pdfDocument.close();
    }
}
