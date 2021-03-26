package com.example.saleservice;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.saleservice.adapter.invoiceshowadapter;
import com.example.saleservice.classes.classidamountmap;
import com.example.saleservice.classes.invoiceamountmap;
import com.example.saleservice.classes.invoiceshowclass;
import com.example.saleservice.classes.schoolmgmtclass;
import com.example.saleservice.helper.Classschhelper;
import com.example.saleservice.helper.Companymgmthelper;
import com.example.saleservice.helper.bookdatabasehelper;
import com.example.saleservice.helper.invoicehelper;
import com.example.saleservice.helper.invoicenomap;
import com.example.saleservice.helper.userclasshelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class userinvshow extends AppCompatActivity {
    private Spinner schoolname;
    private Button show;
    private userclasshelper schoolhelp;
    private schoolmgmtclass schoolclass;
    private List<String> schoolnlist;
    private List<String> invnolist;
    private List<Integer> nolist;
    private List<invoiceshowclass> invoicelist;
    private invoicenomap invoicenohelp;
    private invoicehelper invoicehelp;
    private invoiceshowclass  invoiceclass;
    private invoiceshowadapter invoiceadapter;
    private bookdatabasehelper bdbhelper;
    private Companymgmthelper companyhelp;
    private Classschhelper classhelp;
    private Integer invno;
    private Integer schname;
    private Integer flag = -1;
    private List<invoiceamountmap> invamtdetlist;
    private List<classidamountmap> cidamtlist;
    private classidamountmap cidamtclass;
    private invoiceamountmap invoiceamtdetails;
    private String nameschool;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialsetup();
    }
    public void initialsetup()
    {
        flag = 0;
        setContentView(R.layout.activity_invoiceshow);
        schoolname = findViewById(R.id.spshowinvsch);
        show = findViewById(R.id.btshowinvoice);
        schoolhelp = new userclasshelper(this);
        schoolclass = schoolhelp.getdata();
//        schoolmgmthelper.getInstance(invoiceshow.this);
        schoolnlist = new ArrayList<>();
        schoolnlist.clear();
        schoolnlist.add(schoolclass.getName());
        nameschool = schoolclass.getName();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(userinvshow.this,android.R.layout.simple_spinner_item,schoolnlist);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        schoolname.setAdapter(adapter);
        schoolname.setSelection(0);
        schoolname.setEnabled(false);
        schname = schoolclass.getId();
        invoicenohelp = new invoicenomap(userinvshow.this,"School"+schname);
        classhelp = new Classschhelper(userinvshow.this,"School"+schname,"classmap");
//      invoicenomap.getInstance(invoiceshow.this,"School"+schname);
        secondsetup();
//        show.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(schoolname.getSelectedItemPosition()!=0)
//                {
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
        ListView listView = findViewById(R.id.splistview);
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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(userinvshow.this,android.R.layout.simple_list_item_1,invnolist);
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
    public void thirdsetup(Integer pos)
    {
        flag = 2;
        setContentView(R.layout.samplelistview);
        invalidateOptionsMenu();
        ListView listView1 = findViewById(R.id.splistview);
        invoicehelp = new invoicehelper(userinvshow.this,"School"+schname+"INVOICENO"+nolist.get(pos));
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
                invoiceadapter = new invoiceshowadapter(userinvshow.this,R.layout.invoiceviewerlayout,invoicelist);
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
        companyhelp = new Companymgmthelper(userinvshow.this);
//        Companymgmthelper.getInstance(invoiceshow.this);
        for (int i = 0; i <invoicelist.size() ; i++) {
            Integer cpid = companyhelp.getid(invoicelist.get(i).getCmpname());
            bdbhelper = new bookdatabasehelper(userinvshow.this,"School"+schname,"Company"+cpid+"bookdet");
//            stockhelp = new wholestockdbhelper(userinvshow.this,"Company"+cpid);
//            bookdatabasehelper.getInstance(invoiceshow.this,"School"+schname,"Company"+cpid+"bookdet");
//            wholestockdbhelper.getInstance(invoiceshow.this,"Company"+cpid);
            Integer getsold = bdbhelper.getsalestock(invoicelist.get(i).getName());
//            Integer getnetsold = stockhelp.getsalestock(invoicelist.get(i).getName());
            bdbhelper.updatesale(invoicelist.get(i).getName(),getsold - invoicelist.get(i).getQuanity());
//            stockhelp.updatesalestock(invoicelist.get(i).getName(),getnetsold - invoicelist.get(i).getQuanity());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(flag == 2)
        {
            getMenuInflater().inflate(R.menu.delmenu,menu);
            menu.removeItem(R.id.delquan);
            menu.getItem(0).setTitle("PRINT INVOICE");
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
//            case R.id.delquan:{
//                resetdata();
//                invoicenohelp.delete(invno);
//                invoicehelp.droptable();
//                flag = 1;
//                secondsetup();
//                break;
//            }
            case R.id.delinv:{
                String filepath = "Invoice - "+ invno + ".pdf";
                viewpdf(filepath,"SalesInvoice/School"+schname);
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
        canvas.drawText("School Name : " + nameschool, 30, 30, title);
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
            canvas2.drawText("School Name : " + nameschool, 30, 30, title);
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
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SalesInvoice/User";
        File dir = new File(path);
        if(!dir.exists())
            dir.mkdirs();
        String filepath = "Invsummary.pdf";
        File file = new File(dir, filepath);
        if(file.exists())
            file.delete();
        pdfDocument.writeTo(new FileOutputStream(file));
        viewpdf(filepath,"SalesInvoice/User");
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
            Toast.makeText(userinvshow.this, "pdf not found, upload failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if(flag == 2)
        {
            flag = 1;
            secondsetup();
        }
        else if(flag == 1)
        {
            finish();
        }
        else {
            finish();
        }
    }

//    public void schoolsetup() {
//        try {
//            Cursor cursor = schoolhelp.display();
//            if (cursor.getCount() > 0) {
//                schoolnlist.clear();
//                schoolnlist.add(0, "Choose School Name");
//                while (cursor.moveToNext()) {
//                    schoolnlist.add(cursor.getString(1));
//                }
//                ArrayAdapter<String> adapter = new ArrayAdapter<String>(invoiceshow.this, android.R.layout.simple_spinner_item, schoolnlist);
//                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                schoolname.setAdapter(adapter);
//            } else {
//                Toast.makeText(this, "No schools, Create school first", Toast.LENGTH_SHORT).show();
//                finish();
//            }
//        } catch (Exception e) {
//            Toast.makeText(this, "Database link failed,check school is created", Toast.LENGTH_SHORT).show();
//            finish();
//        }
//    }
}
