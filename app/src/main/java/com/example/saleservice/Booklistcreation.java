package com.example.saleservice;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.saleservice.helper.Classmgmthelper;
import com.example.saleservice.helper.Classschhelper;
import com.example.saleservice.helper.Companymgmthelper;
import com.example.saleservice.helper.bookdatabasehelper;
import com.example.saleservice.helper.booklisthelper;
import com.example.saleservice.helper.entirestockhelper;
import com.example.saleservice.helper.schoolcompanymap;
import com.example.saleservice.helper.schoolmgmthelper;
import com.google.android.material.textfield.TextInputLayout;


import java.util.ArrayList;
import java.util.List;

public class Booklistcreation extends AppCompatActivity {
    private Spinner schoolname;
    private Spinner classname;
    private Spinner setname;
    private Button create;
    private Classschhelper classhelp;
    private schoolmgmthelper schoolhelp;
    private Classmgmthelper sethelp;
    private List<String> schoolnlist;
    private List<String> classnlist;
    private List<String> cmpnlist;
    private List<String> setlist;
    private schoolcompanymap cmpnamemapper;
    private Companymgmthelper companyhelp;
    private Spinner companyname;
    private AutoCompleteTextView bookname;
    private EditText bookprice,bookquantity,booknetquant;
    private TextView nclassname;
    private entirestockhelper wholestockhelp;
    private Button addbook;
    private List<String> booknamelist;
    private Integer schname,clsname;
    private Integer stname;
    private booklisthelper blisthelper;
    private bookdatabasehelper bdbhelper;
    private InputFilter filter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            for (int i = start; i <end ; i++) {
                if(source.charAt(i) == '\'')
                {
                    String sample = source.toString();
                    String s = sample.substring(start,i) + sample.substring(i+1,end);
                    return s;
                }
            }
            return null;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookliststart);
//        SQLiteDatabase.loadLibs(Booklistcreation.this);
        initialsetup();
    }
    public void initialsetup()
    {
        final TextInputLayout textInputLayout = findViewById(R.id.tilsp1);
        schoolname = findViewById(R.id.spblcrtschoolname);
        classname = findViewById(R.id.spblcrtclassname);
        create = findViewById(R.id.btstartbl);
        setname = findViewById(R.id.spblcrtsetname);
        schoolnlist = new ArrayList<>();
        classnlist = new ArrayList<>();
        setlist = new ArrayList<>();
//        schoolmgmthelper.getInstance(Booklistcreation.this);
        schoolhelp = new schoolmgmthelper(Booklistcreation.this);
        schoolsetup();
        classname.setEnabled(false);
        setname.setEnabled(false);
        schoolname.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0)
                {
                    textInputLayout.setError("");
                    classname.setEnabled(true);
                    schname = schoolhelp.getid(schoolname.getSelectedItem().toString());
//                    Classschhelper.getInstance(Booklistcreation.this,"School"+schname,"classmap");
                    classhelp = new Classschhelper(Booklistcreation.this,"School"+schname,"classmap");
                    classsetup();
                }
                else {
//                    textInputLayout.setError("Field cannot be empty");
                    classname.setEnabled(false);
                    setname.setEnabled(false);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        classname.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0)
                {
                    clsname = classhelp.getid(classname.getSelectedItem().toString());
                    setname.setEnabled(true);
//                    Classmgmthelper.getInstance(Booklistcreation.this,"School"+schname,"ClsdetailClass"+clsname);
                    sethelp = new Classmgmthelper(Booklistcreation.this,"School"+schname,"ClsdetailClass"+clsname);
                    setsetup();
                }
                else
                    setname.setEnabled(false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        setname.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0)
                    create.setEnabled(true);
                else
                    create.setEnabled(false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(setname.getSelectedItemPosition()!=0)
                {
                    stname = sethelp.getid(setname.getSelectedItem().toString());
                    secondsetup();
                }
                else {
                    Toast.makeText(Booklistcreation.this, "Field cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void secondsetup()
    {
        setContentView(R.layout.activity_booklistcreation);
        cmpnlist = new ArrayList<>();
        booknamelist = new ArrayList<>();
        final TextInputLayout textInputLayout = findViewById(R.id.tilsp4);
        final TextInputLayout textInputLayout1 = findViewById(R.id.tilactv1);
        final TextInputLayout textInputLayout2 = findViewById(R.id.tilet1);
        final TextInputLayout textInputLayout3 = findViewById(R.id.tilet2);
        final TextInputLayout textInputLayout4 = findViewById(R.id.tilet3);
        companyname = findViewById(R.id.spcrtblcmpname);
        bookname = findViewById(R.id.etcrbookname);
        bookname.setFilters(new InputFilter[]{ filter });
        bookprice = findViewById(R.id.etcrbookprice);
        bookquantity = findViewById(R.id.etcrbookquantity);
        booknetquant = findViewById(R.id.etcrbooknetquan);
        nclassname = findViewById(R.id.etnewcopier);
        addbook = findViewById(R.id.btaddbook);
        List<String> newclasslist = classhelp.getdata(clsname);
        final List<String> classdetails = new ArrayList<>();
        final String[] name = new  String[newclasslist.size()];
        for (int i = 0; i <newclasslist.size() ; i++) {
            name[i] = newclasslist.get(i);
        }
        final boolean [] checkitem = new boolean[newclasslist.size()];
        nclassname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Booklistcreation.this);
                builder.setTitle("Choose Classes");
                builder.setMultiChoiceItems(name, checkitem, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if(classdetails.contains(name[which]))
                            classdetails.remove(name[which]);
                        else
                            classdetails.add(name[which]);
                    }
                });
                builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 0; i <classdetails.size() ; i++) {
                            if(i!=classdetails.size() - 1)
                                stringBuilder.append(classdetails.get(i)).append(",");
                            else
                                stringBuilder.append(classdetails.get(i));
                        }
                        if(stringBuilder.toString().isEmpty())
                            nclassname.setText("Click to add the book to other classes");
                        else
                            nclassname.setText(stringBuilder);
                    }
                });
                builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i <checkitem.length ; i++)
                            checkitem[i] = false;
                        classdetails.clear();
                        nclassname.setText("Click to add the book to other classes");
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        });
        if(stname == 3)
            bookquantity.setEnabled(true);
        else
            bookquantity.setEnabled(false);
//        booklisthelper.getInstance(Booklistcreation.this,"School"+schname,"BooklistClass"+clsname+"Set"+stname);
        blisthelper = new booklisthelper(Booklistcreation.this,"School"+schname,"BooklistClass"+clsname+"Set"+stname);
//        Companymgmthelper.getInstance(Booklistcreation.this);
        companyhelp = new Companymgmthelper(Booklistcreation.this);
        companysetup();
        companyname.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    Integer cpname = companyhelp.getid(companyname.getSelectedItem().toString());
//                wholestockdbhelper.getInstance(Booklistcreation.this,"Company"+cpname);
                    bdbhelper = new bookdatabasehelper(Booklistcreation.this, "School" + schname, "Company" + cpname + "bookdet");
                    wholestockhelp = new entirestockhelper(Booklistcreation.this, "Company" + cpname);
//                bookdatabasehelper.getInstance(Booklistcreation.this,"School"+schname,"Company"+cpname+"bookdet");
                    try {
                        Cursor cursor = wholestockhelp.display();
                        booknamelist.clear();
                        if (cursor.getCount() > 0) {
                            while (cursor.moveToNext()) {
                                booknamelist.add(cursor.getString(0));
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(Booklistcreation.this, android.R.layout.simple_list_item_1, booknamelist);
                            bookname.setAdapter(adapter);
                        }
                    } catch (Exception e) {
                        Toast.makeText(Booklistcreation.this, "No book database exist", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        bookname.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Double pr = wholestockhelp.getprice(bookname.getText().toString());
                bookprice.setText(""+pr);
            }
        });
        addbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(companyname.getSelectedItemPosition() == 0)
                    textInputLayout.setError("Field Cannot be Empty");
                else if(bookname.getText().toString().isEmpty())
                    textInputLayout1.setError("Field Cannot be Empty");
                else if(bookquantity.getText().toString().isEmpty())
                    textInputLayout2.setError("Field Cannot be Empty");
                else if(bookprice.getText().toString().isEmpty())
                    textInputLayout3.setError("Field Cannot be Empty");
                else if(booknetquant.getText().toString().isEmpty())
                    textInputLayout4.setError("Field Cannot be Empty");
                else {
                    if(companyname.getSelectedItemPosition()!=0 && !bookname.getText().toString().isEmpty()&& !bookquantity.getText().toString().isEmpty()&& !bookprice.getText().toString().isEmpty()&& !booknetquant.getText().toString().isEmpty()) {
                        String boname = bookname.getText().toString();
                        Double boprice = Double.parseDouble(bookprice.getText().toString());
                        Integer boquan = Integer.parseInt(bookquantity.getText().toString());
                        Integer bnquant = Integer.parseInt(booknetquant.getText().toString());
                        String coname = companyname.getSelectedItem().toString();
                        Integer cpname = companyhelp.getid(companyname.getSelectedItem().toString());
//                    stockhelper = new wholestockdbhelper(Booklistcreation.this,"Company"+cpname);
//                    wholestockdbhelper.getInstance(Booklistcreation.this,"Company"+cpname);
                        wholestockhelp = new entirestockhelper(Booklistcreation.this, "Company" + cpname);
                        cmpnamemapper = new schoolcompanymap(Booklistcreation.this, "School" + schname);
//                    schoolcompanymap.getInstance(Booklistcreation.this,"School"+schname);
                        bdbhelper = new bookdatabasehelper(Booklistcreation.this, "School" + schname, "Company" + cpname + "bookdet");
//                    bookdatabasehelper.getInstance(Booklistcreation.this,"School"+schname,"Company"+cpname+"bookdet");
                        Integer boldquant;
                        try {
                            boldquant = bdbhelper.getstock(boname);
                        } catch (Exception e) {
                            boldquant = 0;
                        }
                        cmpnamemapper.insert(coname);
                        boolean resul1 = bdbhelper.insert(boname, boprice, bnquant);
                        if (!resul1) {
                            bdbhelper.update(boname, bnquant + boldquant, boprice);
                            wholestockhelp.updateprice(boname, boprice);
                            Toast.makeText(Booklistcreation.this, "book db updated", Toast.LENGTH_SHORT).show();
                        } else {
                            wholestockhelp.insert(boname, boprice);
                            Toast.makeText(Booklistcreation.this, "Book added", Toast.LENGTH_SHORT).show();
                        }
                        boolean resul2 = blisthelper.insert(boname, coname, boprice, boquan);
                        for (int i = 0; i < classdetails.size(); i++) {
                            Integer id = classhelp.getid(classdetails.get(i));
                            booklisthelper booklisthelper1 = new booklisthelper(Booklistcreation.this, "School" + schname, "BooklistClass" + id + "Set" + stname);
                            booklisthelper1.insert(boname, coname, boprice, boquan);
                        }
                        if (resul2) {
                            Integer itemstck = bdbhelper.getstock(boname);
//                        Toast.makeText(Booklistcreation.this, "Entry Successful" + " Net Quantity "+itemstck, Toast.LENGTH_SHORT).show();
                            bookname.setText("");
                            companyname.setSelection(0);
                            bookprice.setText("");
                            bookquantity.setText("1");
                            booknetquant.setText("0");
                            for (int i = 0; i <checkitem.length ; i++)
                                checkitem[i] = false;
                            classdetails.clear();
                            nclassname.setText("Click to add the book to other classes");
                        } else {
                            Toast.makeText(Booklistcreation.this, "Entry Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                        Toast.makeText(Booklistcreation.this, "Some Fields are empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(Booklistcreation.this, android.R.layout.simple_spinner_item, schoolnlist);
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
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(Booklistcreation.this, android.R.layout.simple_spinner_item, classnlist);
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
    public void setsetup()
    {
        try {
            Cursor cursor = sethelp.display();
            if(cursor.getCount()>0)
            {
                setlist.clear();
                setlist.add(0,"Choose Set name");
                while (cursor.moveToNext())
                    setlist.add(cursor.getString(1));
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(Booklistcreation.this,android.R.layout.simple_spinner_item,setlist);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                setname.setAdapter(adapter);
            }
            else {
                Toast.makeText(this, "No Set exists, Create Set first", Toast.LENGTH_SHORT).show();
                finish();
            }
        }catch (Exception e)
        {
            Toast.makeText(this, "Database link failed, check set is created", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    public void companysetup() {
        try {
            Cursor cursor = companyhelp.display();
            if(cursor.getCount()>0) {
                cmpnlist.clear();
                cmpnlist.add(0, "Choose Company Name");
                while (cursor.moveToNext()) {
                    cmpnlist.add(cursor.getString(1));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(Booklistcreation.this, android.R.layout.simple_spinner_item, cmpnlist);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                companyname.setAdapter(adapter);
            }else {
                Toast.makeText(this, "No companies, Create Company first", Toast.LENGTH_SHORT).show();
                finish();
            }
        }catch (Exception e)
        {
            Toast.makeText(this, "Database link failed, check company is created", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
