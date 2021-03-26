package com.example.saleservice;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.saleservice.classes.schoolmgmtclass;
import com.example.saleservice.helper.schoolmgmthelper;
import com.example.saleservice.helper.schoolnameaddhelper;
import com.example.saleservice.helper.userhelper;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class schoolmgmt extends AppCompatActivity {
    private EditText name;
    private Button create,delet;
    private TextView showall;
    private schoolmgmthelper schoolhelper;
    private schoolmgmtclass schoolclass;
    private schoolnameaddhelper schooladder;
    private userhelper userhelp;
    private List<schoolmgmtclass> schoolname;
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
        setContentView(R.layout.activity_schoolmgmt);
//        SQLiteDatabase.loadLibs(this);
        viewsetup();
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createschool();
            }
        });
        showall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showschool();
            }
        });
    }
    public void viewsetup()
    {
        name = findViewById(R.id.etcrschname);
        name.setFilters(new InputFilter[]{filter});
        create = findViewById(R.id.btcreateschname);
        showall = findViewById(R.id.tvallschool);
        delet = findViewById(R.id.btdeleteschool);
        schoolhelper = new schoolmgmthelper(this);
//        schoolmgmthelper.getInstance(this);
        schoolname = new ArrayList<>();
    }
    public void createschool()
    {
        if(name.getText().toString().isEmpty())
            Toast.makeText(this, "Name Field is Empty", Toast.LENGTH_SHORT).show();
        else {
            boolean result = schoolhelper.insert(name.getText().toString());
            if(result == true)
            {
                Integer id = schoolhelper.getid(name.getText().toString());
                schooladder = new schoolnameaddhelper(schoolmgmt.this,"School"+id);
                userhelp = new userhelper(schoolmgmt.this);
                schooladder.insert(name.getText().toString());
                userhelp.insert("admin","admin","admin",id);
                Toast.makeText(this, "School Created", Toast.LENGTH_SHORT).show();
                name.setText("");
            }
            else
                Toast.makeText(this, "School Creation Failed", Toast.LENGTH_SHORT).show();
        }
    }
    public void showschool()
    {
        setContentView(R.layout.samplelistview);
        ListView listView = findViewById(R.id.splistview);
        List<String> slname = new ArrayList<>();
        try {
            Cursor cursor = schoolhelper.display();
            if(cursor.getCount()>0)
            {
                schoolname.clear();
                slname.clear();
                while (cursor.moveToNext())
                {
                    schoolclass = new schoolmgmtclass(cursor.getString(1),cursor.getInt(0));
                    schoolname.add(schoolclass);
                    slname.add(cursor.getInt(0) + " @ " + cursor.getString(1));
                }
                ArrayAdapter<String> adapter =new ArrayAdapter<String>(schoolmgmt.this,android.R.layout.simple_list_item_1,slname);
                listView.setAdapter(adapter);
            }
            else {
                Toast.makeText(this, "No entry exists", Toast.LENGTH_SHORT).show();
                finish();
            }
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    editschool(position);
                }
            });
        }catch (Exception e)
        {
            Toast.makeText(this, "No database/table exists", Toast.LENGTH_SHORT).show();
        }
    }
    public void editschool(final int pos){
        setContentView(R.layout.activity_schoolmgmt);
        name = findViewById(R.id.etcrschname);
        name.setFilters(new InputFilter[]{filter});
        create = findViewById(R.id.btcreateschname);
        showall = findViewById(R.id.tvallschool);
        delet = findViewById(R.id.btdeleteschool);
        showall.setVisibility(View.INVISIBLE);
        name.setText(schoolname.get(pos).getName());
        delet.setVisibility(View.VISIBLE);
        create.setText("SAVE");
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name.getText().toString().isEmpty())
                    Toast.makeText(schoolmgmt.this, "Field cannot be empty", Toast.LENGTH_SHORT).show();
                else {
                    boolean result = schoolhelper.update(schoolname.get(pos).getId(),name.getText().toString());
                    if(result == true)
                    {
                        Integer id = schoolname.get(pos).getId();
                        schooladder = new schoolnameaddhelper(schoolmgmt.this,"School"+id);
                        schooladder.update(schoolname.get(pos).getName(),name.getText().toString());
                        Toast.makeText(schoolmgmt.this, "Update successful", Toast.LENGTH_SHORT).show();
                        showschool();
                    }
                    else {
                        Toast.makeText(schoolmgmt.this, "Update Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        delet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Integer id = schoolname.get(pos).getId();
                final File file =new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/.SaleServiceDB/Schooldata/School" + id + ".db");
                if(file.exists())
                {
                    AlertDialog dialog;
                    final AlertDialog.Builder builder = new AlertDialog.Builder(schoolmgmt.this);
                    builder.setTitle("Please Confirm ");
                    builder.setMessage("Deleting school will delete entire school data, Do you want to continue to delete the school ?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            boolean res = file.delete();
                            File file1 =new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/.SaleServiceDB/Schooldata/School" + id + ".db-journal");
                            if(file1.exists())
                                file1.delete();
                            if(res) {
                                Integer row = schoolhelper.delete(schoolname.get(pos).getId());
                                if (row > 0) {
                                    Toast.makeText(schoolmgmt.this, "Delete Successful", Toast.LENGTH_SHORT).show();
                                    showschool();
                                }
                                else
                                    Toast.makeText(schoolmgmt.this, "Delete Failed", Toast.LENGTH_SHORT).show();
                            }
                            else
                                Toast.makeText(schoolmgmt.this, "Delete Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            showschool();
                        }
                    });
                    dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }
}

