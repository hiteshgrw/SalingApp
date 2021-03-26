package com.example.saleservice;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.saleservice.classes.booklistlayout;
import com.example.saleservice.helper.Classmgmthelper;
import com.example.saleservice.helper.Classschhelper;
import com.example.saleservice.helper.Companymgmthelper;
import com.example.saleservice.helper.bookdatabasehelper;
import com.example.saleservice.helper.booklisthelper;
import com.example.saleservice.helper.schoolmgmthelper;


import java.util.ArrayList;
import java.util.List;

public class Showbooklist extends AppCompatActivity {
    private TextView textView;
    private Spinner schoolname,classname;
    private Button showbklst;
    private schoolmgmthelper schoolhelp;
    private Classschhelper classhelp;
    private Classmgmthelper sethelper;
    private bookdatabasehelper bdbhelper;
    private Companymgmthelper companyhelp;
    private List<String> schoolnlist;
    private List<String> classnlist;
    private List<String> setnlist;
    private List<String> bkllist;
    private Integer schname,clsname;
    private Integer stname;
    private ListView listView,listView1;
    private booklisthelper blhelper;
    private booklistlayout bklayout;
    private List<booklistlayout> bklayoutlst;
    private Integer flag = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        SQLiteDatabase.loadLibs(this);
        initialsetup();
    }
    public void initialsetup()
    {
        setContentView(R.layout.activity_showclass);
        flag = 0;
        textView = findViewById(R.id.tvshowclasstitle);
        schoolname = findViewById(R.id.spshowclassschool);
        classname = findViewById(R.id.spshowclassclass);
        showbklst = findViewById(R.id.btshowclass);
        textView.setText("SHOW BOOK LIST");
        classname.setEnabled(false);
        schoolnlist = new ArrayList<>();
        classnlist = new ArrayList<>();
        schoolhelp = new schoolmgmthelper(this);
//        schoolmgmthelper.getInstance(this);
        schoolsetup();
        schoolname.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0)
                {
                    classname.setEnabled(true);
                    schname = schoolhelp.getid(schoolname.getSelectedItem().toString());
                    classhelp = new Classschhelper(Showbooklist.this,"School"+schname,"classmap");
//                    Classschhelper.getInstance(Showbooklist.this,"School"+schname,"classmap");
                    classsetup();
                }
                else
                    classname.setEnabled(false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        showbklst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(classname.getSelectedItemPosition() != 0)
                {
                    clsname = classhelp.getid(classname.getSelectedItem().toString());
                    sethelper = new Classmgmthelper(Showbooklist.this,"School"+schname,"ClsdetailClass"+clsname);
//                    Classmgmthelper.getInstance(Showbooklist.this,"School"+schname,"ClsdetailClass"+clsname);
                    secondsetup();
                }
                else
                    Toast.makeText(Showbooklist.this, "Field Cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void secondsetup(){
        setContentView(R.layout.samplelistview);
        flag = 1;
        setnlist = new ArrayList<>();
        final List<String> stame = new ArrayList<>();
        listView = findViewById(R.id.splistview);
        try {
            Cursor cursor = sethelper.display();
            if(cursor.getCount()>0)
            {
                setnlist.clear();
                stame.clear();
                while(cursor.moveToNext())
                {
                    setnlist.add(cursor.getInt(0) + " @ "+cursor.getString(1) + " @ " + cursor.getDouble(2));
                    stame.add(cursor.getString(1));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(Showbooklist.this,android.R.layout.simple_list_item_1,setnlist);
                listView.setAdapter(adapter);
            }
            else {
                Toast.makeText(this, "No Set Exists, Create First", Toast.LENGTH_SHORT).show();
                finish();
            }
        }catch (Exception e)
        {
            Toast.makeText(this, "Database link failed, Check set is created or not", Toast.LENGTH_SHORT).show();
            finish();
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                stname = sethelper.getid(stame.get(position));
                blhelper = new booklisthelper(Showbooklist.this,"School"+schname,"BooklistClass"+clsname+"Set"+stname);
//                booklisthelper.getInstance(Showbooklist.this,"School"+schname,"BooklistClass"+clsname+"Set"+stname);
                thirdsetup();
            }
        });
    }
    public void thirdsetup()
    {
        setContentView(R.layout.samplelistview);
        flag = 2;
        listView1 = findViewById(R.id.splistview);
        bkllist = new ArrayList<>();
        bklayoutlst = new ArrayList<>();
        try {
            Cursor cursor = blhelper.display();
            if(cursor.getCount()>0)
            {
                bkllist.clear();
                while (cursor.moveToNext())
                {
                    bklayout = new booklistlayout(cursor.getString(0),cursor.getString(1),cursor.getDouble(2),cursor.getInt(3));
                    bklayoutlst.add(bklayout);
                    bkllist.add(cursor.getString(0) + " @ " + cursor.getString(1) + " @ " + cursor.getDouble(2)+" @ "+cursor.getInt(3)+"Pcs");
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(Showbooklist.this,android.R.layout.simple_list_item_1,bkllist);
                listView1.setAdapter(adapter);
            }
            else {
                Toast.makeText(this, "No Book List Exists, Create First", Toast.LENGTH_SHORT).show();
                finish();
            }
        }catch (Exception e)
        {
            Toast.makeText(this, "Database link failed, Check booklist is created or not", Toast.LENGTH_SHORT).show();
            finish();
        }
        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                fourthsetup(bklayoutlst.get(position).getName(),bklayoutlst.get(position).getCname(),bklayoutlst.get(position).getPrice(),bklayoutlst.get(position).getQuantity());
//                bkllist.remove(position);
//                ArrayAdapter<String> adapter = new ArrayAdapter<String>(Showbooklist.this,android.R.layout.simple_list_item_1,bkllist);
//                listView1.setAdapter(adapter);
            }
        });
    }
    public void fourthsetup(final String spname,final String cname,Double spprice, Integer spquantity)
    {
        setContentView(R.layout.editbooklist);
        flag = 3;
        final EditText edquan = findViewById(R.id.etshowbklquan);
        final EditText edprice = findViewById(R.id.etshowbklprice);
        Button savebkl = findViewById(R.id.bteditbkl);
        Button deletebkl = findViewById(R.id.btdeletebkl);
        companyhelp = new Companymgmthelper(Showbooklist.this);
//        Companymgmthelper.getInstance(Showbooklist.this);
        Integer cpid = companyhelp.getid(cname);
        bdbhelper = new bookdatabasehelper(Showbooklist.this,"School"+schname,"Company"+cpid+"bookdet");
//        bookdatabasehelper.getInstance(Showbooklist.this,"School"+schname,"Company"+cpid+"bookdet");
//        stockhelp = new wholestockdbhelper(Showbooklist.this,"Company"+cpid);
//        wholestockdbhelper.getInstance(Showbooklist.this,"Company"+cpid);
        if(stname == 3)
            edquan.setEnabled(true);
        else
            edquan.setEnabled(false);
        edquan.setText("" + spquantity);
        edprice.setText(""+spprice);
        savebkl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Double pric = Double.parseDouble(edprice.getText().toString());
                Integer quant = Integer.parseInt(edquan.getText().toString());
                bdbhelper.updateprice(spname,pric);
//                stockhelp.updateprice(spname,pric);
                boolean result = blhelper.update(spname,pric,quant);
                if(result == true)
                {
                    Toast.makeText(Showbooklist.this, "Update Successful", Toast.LENGTH_SHORT).show();
                    thirdsetup();
                }
                else
                    Toast.makeText(Showbooklist.this, "Update Failed", Toast.LENGTH_SHORT).show();
            }
        });
        deletebkl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean result = blhelper.delete(spname);
                if(result == true)
                {
                    Toast.makeText(Showbooklist.this, "Delete Successful", Toast.LENGTH_SHORT).show();
                    thirdsetup();
                }
                else {
                    Toast.makeText(Showbooklist.this, "Delete Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(flag == 3)
        {
            thirdsetup();
        }
        else if(flag == 2)
        {
            secondsetup();
        }
        else if(flag == 1)
        {
            initialsetup();
        }
        else {
            finish();
        }
//        if(sethelper == null && blhelper == null)
//            finish();
//        else if(blhelper == null) {
//            sethelper = null;
//            initialsetup();
//        }
//        else {
//            blhelper = null;
//            secondsetup();
//        }
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
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(Showbooklist.this, android.R.layout.simple_spinner_item, schoolnlist);
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

    public void classsetup() {
        try {
            Cursor cursor = classhelp.display();
            if (cursor.getCount() > 0) {
                classnlist.clear();
                classnlist.add(0, "Choose Class Name");
                while (cursor.moveToNext()) {
                    classnlist.add(cursor.getString(1));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(Showbooklist.this, android.R.layout.simple_spinner_item, classnlist);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                classname.setAdapter(adapter);
            } else {
                Toast.makeText(this, "No classes, Create class first", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Database link failed,check class is created", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
