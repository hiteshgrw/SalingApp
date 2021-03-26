package com.example.saleservice;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.saleservice.classes.orderlayout;
import com.example.saleservice.helper.Companymgmthelper;
import com.example.saleservice.helper.bookdatabasehelper;
import com.example.saleservice.helper.ordercreatorhelper;
import com.example.saleservice.helper.schoolcompanymap;
import com.example.saleservice.helper.schoolmgmthelper;
import com.example.saleservice.adapter.orderadapter;
import com.google.android.material.textfield.TextInputLayout;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ordercreator extends AppCompatActivity {
    private Spinner schoolname;
    private Integer schname,oflag;
    private String nameschool;
    private schoolmgmthelper schoolhelp;
    private bookdatabasehelper bdbhelper;
    private ordercreatorhelper orderhelp;
    private List<String> schoolnlist;
    private List<orderlayout> orderlist;
    private orderlayout orderclass;
    List<String> cmpnlist;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialsetup();
    }
    private void initialsetup()
    {
        oflag = 0;
        setContentView(R.layout.schoolspinner);
        invalidateOptionsMenu();
        schoolhelp = new schoolmgmthelper(this);
        orderhelp = new ordercreatorhelper(this);
        schoolnlist = new ArrayList<>();
        orderlist = new ArrayList<>();
        schoolname = findViewById(R.id.spschoolspinner);
        TextView textView = findViewById(R.id.tvshownuschstocktitle);
        textView.setText("ORDER CREATION");
        schoolsetup();
        schoolname.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0)
                {
                    schname = schoolhelp.getid(schoolname.getSelectedItem().toString());
                    nameschool = schoolname.getSelectedItem().toString();
                    listcreator();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(oflag ==1)
            getMenuInflater().inflate(R.menu.itemselectmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.itemseldone)
        {
            if(oflag == 1)
                try {
                    createexcelfile();
                } catch (Exception e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
        }
        return super.onOptionsItemSelected(item);
    }

    public void listcreator()
    {
        oflag = 1;
        setContentView(R.layout.samplelistview);
        invalidateOptionsMenu();
        final ListView listView = findViewById(R.id.splistview);
        Companymgmthelper companymgmthelper = new Companymgmthelper(this);
        schoolcompanymap companyhelp = new schoolcompanymap(this,"School"+schname);
        cmpnlist = new ArrayList<>();
        cmpnlist.clear();
        orderlist.clear();
        cmpnlist = companyhelp.display();
        for (int i = 0; i <cmpnlist.size() ; i++) {
            Integer cmpname = companymgmthelper.getid(cmpnlist.get(i));
            bdbhelper = new bookdatabasehelper(this,"School"+schname,"Company"+cmpname+"bookdet");
            try {
                Cursor cursor = bdbhelper.display();
                if(cursor.getCount()>0)
                {
                    while (cursor.moveToNext()) {
                        orderclass = new orderlayout(cursor.getString(0),cmpnlist.get(i),cursor.getInt(2));
                        orderlist.add(orderclass);
                    }
                }
            }
            catch (Exception e)
            {
                Log.e("Excelcreation",cmpnlist.get(i)+" book database sync failed");
            }
        }
        final orderadapter adapter = new orderadapter(ordercreator.this,R.layout.orderlayout,orderlist);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(ordercreator.this);
                View mview = getLayoutInflater().inflate(R.layout.orderquantreset,null);
                final TextInputLayout newquant = mview.findViewById(R.id.odrquantres);
                newquant.getEditText().setText(""+orderlist.get(position).getQuantity());
//                ((TextInputLayout) mview.findViewById(R.id.odrquantres)).getEditText().setText(""+orderlist.get(position).getQuantity());
                builder.setView(mview)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                listView.setAdapter(adapter);
                            }
                        })
                        .setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                orderlist.remove(position);
                                dialog.dismiss();
                                listView.setAdapter(adapter);
                            }
                        });
                builder.setPositiveButton("Done",null);
//                builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        if(!newquant.getEditText().getText().toString().isEmpty() && Integer.parseInt(newquant.getEditText().getText().toString())!=0) {
//                            newquant.setError(null);
//                            orderlist.get(position).setQuantity(Integer.parseInt(newquant.getEditText().getText().toString()));
//                            dialog.dismiss();
//                            listView.setAdapter(adapter);
//                        }
//                        else {
////                                    if (newquant.getEditText().getText().toString().isEmpty()) {
////                                        newquant.setError("Field cannot be empty");
////                                    } else (Integer.parseInt(newquant.getEditText().getText().toString()) == 0)
////                                        newquant.setError("Quantity cannot be 0");d
//                            Toast.makeText(ordercreator.this, "Not Allowed", Toast.LENGTH_SHORT).show();
//                        }
////                               else {
////                                    if(!newquant.getEditText().getText().toString().isEmpty() && Integer.parseInt(newquant.getEditText().getText().toString())!=0) {
////                                        newquant.setError(null);
////                                        orderlist.get(position).setQuantity(Integer.parseInt(newquant.getEditText().getText().toString()));
////                                        dialog.dismiss();
////                                        listView.setAdapter(adapter);
////                                    }
////                                    else
////                                        newquant.setError("Error Found");
////                                }
//                    }
//                });
                final AlertDialog dialog = builder.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog1) {
                        dialog.getButton(dialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(!newquant.getEditText().getText().toString().isEmpty() && Integer.parseInt(newquant.getEditText().getText().toString())!=0) {
                                    newquant.setError(null);
                                    orderlist.get(position).setQuantity(Integer.parseInt(newquant.getEditText().getText().toString()));
                                    dialog.dismiss();
                                    listView.setAdapter(adapter);
                                }
                                else {
                                    if (newquant.getEditText().getText().toString().isEmpty()) {
                                        newquant.setError("Field cannot be empty");
                                    }
                                    else if (Integer.parseInt(newquant.getEditText().getText().toString()) == 0)
                                    {
                                        newquant.setError("Quantity cannot be 0");
                                    }
                                    else
                                        newquant.setError("Error");
                                }
                            }
                        });
                    }
                });
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        });
    }
    public void createexcelfile()
    {
        Integer index;
        Integer rowno;
        Workbook workbook = new HSSFWorkbook();
        Cell cell = null;
        CellStyle cs = workbook.createCellStyle();
        cs.setFillForegroundColor(HSSFColor.LIME.index);
        cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        Sheet sheet = null;
//        Integer normflag;
        for (int i = 0; i <cmpnlist.size() ; i++) {
            rowno = 0;
            index = 1;
            sheet = workbook.createSheet(cmpnlist.get(i));
            Row row = sheet.createRow(rowno);
            rowno++;
            cell = row.createCell(0);
            cell.setCellValue("Sl. No.");
            cell.setCellStyle(cs);
            cell = row.createCell(1);
            cell.setCellValue("Item Name");
            cell.setCellStyle(cs);
            cell = row.createCell(2);
            cell.setCellValue("Item Quantity");
            cell.setCellStyle(cs);
            for (int j = 0; j <orderlist.size() ; j++) {
                if (orderlist.get(j).getCmpname().equals(cmpnlist.get(i)))
                {
                    Row row1 = sheet.createRow(rowno);
                    rowno++;
                    cell = row1.createCell(0);
                    cell.setCellValue("" + index);
                    index++;
                    cell = row1.createCell(1);
                    cell.setCellValue(orderlist.get(j).getName());
                    cell = row1.createCell(2);
                    cell.setCellValue(orderlist.get(j).getQuantity() + " Pcs");
                }
            }
            sheet.setColumnWidth(0,(15 * 150));
            sheet.setColumnWidth(1,(15 * 600));
            sheet.setColumnWidth(2,(15 * 250));
        }
//        for (int i = 0; i < orderlist.size();i++) {
//            rowno = 0;
//            index = 1;
//            normflag = 1;
////            Toast.makeText(ordercreator.this, "No" + i, Toast.LENGTH_SHORT).show();
////            Toast.makeText(this,orderlist.get(i).getCmpname() , Toast.LENGTH_SHORT).show();
//            sheet = workbook.createSheet(orderlist.get(i).getCmpname());
//            Row row = sheet.createRow(rowno);
//            rowno++;
//            cell = row.createCell(0);
//            cell.setCellValue("Sl. No.");
//            cell.setCellStyle(cs);
//            cell = row.createCell(1);
//            cell.setCellValue("Item Name");
//            cell.setCellStyle(cs);
//            cell = row.createCell(2);
//            cell.setCellValue("Item Quantity");
//            cell.setCellStyle(cs);
//            if(i==0)
//            {
//                Row row1 = sheet.createRow(rowno);
//                rowno++;
//                cell = row1.createCell(0);
//                cell.setCellValue("" + index);
//                index++;
//                cell = row1.createCell(1);
//                cell.setCellValue(orderlist.get(i).getName());
//                cell = row1.createCell(2);
//                cell.setCellValue(orderlist.get(i).getQuantity() + " Pcs");
//            }
//            while (normflag == 1) {
//                if (orderlist.get(i).getCmpname().equals(orderlist.get(i - 1).getCmpname())) {
//                    Row row1 = sheet.createRow(rowno);
//                    rowno++;
//                    cell = row1.createCell(0);
//                    cell.setCellValue("" + index);
//                    index++;
//                    cell = row1.createCell(1);
//                    cell.setCellValue(orderlist.get(i).getName());
//                    cell = row1.createCell(2);
//                    cell.setCellValue(orderlist.get(i).getQuantity() + " Pcs");
//                    i++;
//                }
//                else
//                    normflag = 0;
//            }
//            sheet.setColumnWidth(0,(15 * 150));
//            sheet.setColumnWidth(1,(15 * 600));
//            sheet.setColumnWidth(2,(15 * 250));
//        }
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Orders");
        if(!file.exists())
            file.mkdirs();
        DateFormat df = new SimpleDateFormat("ddMMyyyhhmmss");
        Date date = new Date();
        File file1 = new File(file,nameschool+"-"+df.format(date)+".xls");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file1);
            workbook.write(fileOutputStream);
            orderhelp.insert(nameschool,1);
            notificationstart();
            Toast.makeText(ordercreator.this, "File Created Successfully", Toast.LENGTH_SHORT).show();
            fileOutputStream.close();
            initialsetup();
//            viewxls(file1.toString());
        } catch (IOException e) {
            Toast.makeText(ordercreator.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void notificationstart()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,20);
        calendar.set(Calendar.MINUTE,30);
        calendar.set(Calendar.SECOND,0);
        Intent intent = new Intent(ordercreator.this,Notificationreciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ordercreator.this,0,intent,0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
        Toast.makeText(this, "D", Toast.LENGTH_SHORT).show();
    }
    private void viewxls(String file)
    {
        File pdffile = new File(Environment.getExternalStorageDirectory() + "/" + file);
        final Uri path = FileProvider.getUriForFile(this,BuildConfig.APPLICATION_ID + ".provider",pdffile);
        MimeTypeMap myMime = MimeTypeMap.getSingleton();
        String mimetype = myMime.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(path.toString()));
        Intent vpdf = new Intent(Intent.ACTION_VIEW);
        vpdf.setDataAndType(path,"application/vnd.ms-excel");
        vpdf.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        vpdf.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try{
            startActivity(vpdf);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(ordercreator.this, "File cannot be opened", Toast.LENGTH_SHORT).show();
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
}
