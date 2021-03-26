package com.example.saleservice;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.saleservice.helper.Classmgmthelper;
import com.example.saleservice.helper.schoolmgmthelper;
import com.example.saleservice.helper.Classschhelper;

import java.util.ArrayList;
import java.util.List;

public class Classmgmt extends AppCompatActivity {
    private Spinner schoolname;
    private EditText classname;
    private CheckBox fld1,fld2,fld3;
    private EditText tfldname1,tfldname2;
    private TextView tfldname3;
    private EditText tflddis1,tflddis2,tflddis3;
    private Button create;
    private List<String> schoolnlist;
    private schoolmgmthelper schoolhelp;
    private Classschhelper classschhelper;
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
        setContentView(R.layout.activity_classmgmt);
//        SQLiteDatabase.loadLibs(Classmgmt.this);
        viewsetup();
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addentry();
            }
        });
    }
    public void viewsetup()
    {
        schoolname = findViewById(R.id.spclasscschname);
        classname = findViewById(R.id.etclassname);
        classname.setFilters(new InputFilter[]{filter});
        fld1 = findViewById(R.id.cbcrfield1);
        fld2 = findViewById(R.id.cbcrfield2);
        fld3 = findViewById(R.id.cbcrfield3);
        tfldname1 = findViewById(R.id.etcrfield1name);
        tfldname1.setFilters(new InputFilter[]{filter});
        tfldname2 = findViewById(R.id.etcrfield2name);
        tfldname2.setFilters(new InputFilter[]{filter});
        tfldname3 = findViewById(R.id.etcrfield3name);
        tfldname3.setFilters(new InputFilter[]{filter});
        tflddis1 = findViewById(R.id.etcrfield1dis);
        tflddis2 = findViewById(R.id.etcrfield2dis);
        tflddis3 = findViewById(R.id.etcrfield3dis);
        create = findViewById(R.id.btcreateclassname);
        schoolnlist = new ArrayList<>();
//        schoolmgmthelper.getInstance(this);
        schoolhelp = new schoolmgmthelper(this);
        schoolsetup();
    }
    public void settoempty()
    {
        classname.setText("");
        fld1.setChecked(false);
        fld2.setChecked(false);
        fld3.setChecked(false);
        tfldname1.setText("");
        tfldname2.setText("");
        tflddis1.setText("");
        tflddis2.setText("");
        tflddis3.setText("");
    }
    public void schoolsetup()
    {
        try {
            Cursor cursor = schoolhelp.display();
            if(cursor.getCount()>0)
            {
                schoolnlist.clear();
                schoolnlist.add(0,"Choose School Name");
                while (cursor.moveToNext())
                {
                    schoolnlist.add(cursor.getString(1));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(Classmgmt.this,android.R.layout.simple_spinner_item,schoolnlist);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                schoolname.setAdapter(adapter);
            }
            else {
                Toast.makeText(this, "No schools, Create school first", Toast.LENGTH_SHORT).show();
                finish();
            }
        }catch (Exception e)
        {
            Toast.makeText(this, "Database link failed,check school is created", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    public void addentry() {
        Integer flag = 1;
        if (schoolname.getSelectedItem().toString().isEmpty() || classname.getText().toString().isEmpty()) {
            Toast.makeText(this, "Field cannot be empty", Toast.LENGTH_SHORT).show();
        } else if (fld1.isChecked() && (tflddis1.getText().toString().isEmpty() || tfldname1.getText().toString().isEmpty()))
            Toast.makeText(this, "Field cannot be empty", Toast.LENGTH_SHORT).show();
        else if (fld2.isChecked() && (tflddis2.getText().toString().isEmpty() || tfldname2.getText().toString().isEmpty()))
            Toast.makeText(this, "Field cannot be empty", Toast.LENGTH_SHORT).show();
        else if (fld3.isChecked() && (tflddis3.getText().toString().isEmpty() || tfldname3.getText().toString().isEmpty()))
            Toast.makeText(this, "Field cannot be empty", Toast.LENGTH_SHORT).show();
        else {
            Integer schname = schoolhelp.getid(schoolname.getSelectedItem().toString());
            classschhelper = new Classschhelper(Classmgmt.this,"School"+schname,"classmap");
//            Classschhelper.getInstance(Classmgmt.this,"School"+schname,"classmap");
            boolean resu = classschhelper.insert(classname.getText().toString());
            if (resu == true) {
                Integer clsname = classschhelper.getid(classname.getText().toString());
                Classmgmthelper classhelp = new Classmgmthelper(Classmgmt.this, "School"+schname,"ClsdetailClass"+clsname);
//                Classmgmthelper.getInstance(Classmgmt.this, "School"+schname,"ClsdetailClass"+clsname);
                String name1 = tfldname1.getText().toString();
                String name2 = tfldname2.getText().toString();
                String name3 = tfldname3.getText().toString();
                if (((!name1.isEmpty()) && (name1.equals(name2) || name1.equals(name3))) || ((!name2.isEmpty()) && (name2.equals(name3)))) {
                    Toast.makeText(this, "Field name cannot be same", Toast.LENGTH_SHORT).show();
                } else {
                    if (fld1.isChecked()) {
                        boolean res1 = classhelp.insert(1,tfldname1.getText().toString(), Double.parseDouble(tflddis1.getText().toString()));
                        if (res1 == false)
                            flag = 0;
                    }
                    if (fld2.isChecked()) {
                        boolean res2 = classhelp.insert(2,tfldname2.getText().toString(), Double.parseDouble(tflddis2.getText().toString()));
                        if (res2 == false)
                            flag = 0;
                    }
                    if (fld3.isChecked()) {
                        boolean res3 = classhelp.insert(3,tfldname3.getText().toString(), Double.parseDouble(tflddis3.getText().toString()));
                        if (res3 == false)
                            flag = 0;
                    }
                    {
                        if (flag == 0) {
                            Toast.makeText(this, "Entry Failed", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Entry Successful", Toast.LENGTH_SHORT).show();
                            settoempty();
                        }
                    }
                }
            }
            else
                Toast.makeText(this, "Class Entry Failed", Toast.LENGTH_SHORT).show();
        }
    }
}
