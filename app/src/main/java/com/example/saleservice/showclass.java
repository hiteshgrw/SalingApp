package com.example.saleservice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.saleservice.helper.Classmgmthelper;
import com.example.saleservice.helper.Classschhelper;
import com.example.saleservice.helper.schoolmgmthelper;


import java.util.ArrayList;
import java.util.List;

public class showclass extends AppCompatActivity {
    private Spinner showschool, spinnershowclass;
    private Button display;
    private schoolmgmthelper schoolhelp;
    private Classschhelper classhelp;
    private List<String> schoolnlist;
    private List<String> classnlist;
    private Classmgmthelper classmgmthelper;
    private Integer schname, clsname;
    private String classname;
    private Integer flag1;
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
        setContentView(R.layout.activity_showclass);
//        SQLiteDatabase.loadLibs(this);
        showschool = findViewById(R.id.spshowclassschool);
        spinnershowclass = findViewById(R.id.spshowclassclass);
        display = findViewById(R.id.btshowclass);
        spinnershowclass.setEnabled(false);
        schoolhelp = new schoolmgmthelper(showclass.this);
//        schoolmgmthelper.getInstance(showclass.this);
        schoolnlist = new ArrayList<>();
        classnlist = new ArrayList<>();
        schoolsetup();
        showschool.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    spinnershowclass.setEnabled(true);
                    Integer iid = schoolhelp.getid(showschool.getSelectedItem().toString());
                    classhelp = new Classschhelper(showclass.this,"School"+iid,"classmap");
//                    Classschhelper.getInstance(showclass.this,"School"+iid,"classmap");
                    classsetup();
                } else
                    spinnershowclass.setEnabled(false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spinnershowclass.getSelectedItemPosition() != 0) {
                    schname = schoolhelp.getid(showschool.getSelectedItem().toString());
                    clsname = classhelp.getid(spinnershowclass.getSelectedItem().toString());
                    classname = spinnershowclass.getSelectedItem().toString();
                    editer();
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
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(showclass.this, android.R.layout.simple_spinner_item, schoolnlist);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                showschool.setAdapter(adapter);
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
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(showclass.this, android.R.layout.simple_spinner_item, classnlist);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnershowclass.setAdapter(adapter);
            } else {
                Toast.makeText(this, "No classes, Create class first", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Database link failed,check class is created", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void editer() {
        invalidateOptionsMenu();
        setContentView(R.layout.showallclass);
        final EditText cssname = findViewById(R.id.etshowclassname);
        cssname.setText(classname);
        cssname.setFilters(new InputFilter[]{filter});
        LinearLayout linearLayout1 = findViewById(R.id.llshowfield1);
        LinearLayout linearLayout2 = findViewById(R.id.llshowfield2);
        LinearLayout linearLayout3 = findViewById(R.id.llshowfield3);
        final EditText name1 = findViewById(R.id.etshowfldname1);
        name1.setFilters(new InputFilter[]{filter});
        final EditText name2 = findViewById(R.id.etshowfldname2);
        name2.setFilters(new InputFilter[]{filter});
        final EditText name3 = findViewById(R.id.etshowfldname3);
        linearLayout1.setVisibility(View.INVISIBLE);
        linearLayout2.setVisibility(View.INVISIBLE);
        linearLayout3.setVisibility(View.INVISIBLE);
        name3.setEnabled(false);
        final EditText dis1 = findViewById(R.id.etshowflddis1);
        final EditText dis2 = findViewById(R.id.etshowflddis2);
        final EditText dis3 = findViewById(R.id.etshowflddis3);
        Button upload = findViewById(R.id.btupdateclassname);
        try {
            classmgmthelper = new Classmgmthelper(showclass.this, "School" + schname,"ClsdetailClass" + clsname);
//            Classmgmthelper.getInstance(showclass.this, "School" + schname,"ClsdetailClass" + clsname);
            Cursor cursor = classmgmthelper.display();
            Cursor cursor1 = classmgmthelper.display();
            cursor1.moveToFirst();
            final List<Integer> ida = new ArrayList<>();
            flag1 = 0;
            ida.clear();
            for (int i = 0; i < cursor1.getCount(); i++) {
                ida.add(cursor1.getInt(0));
                cursor1.moveToNext();
            }
//        final Integer count = cursor.getCount();
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                for (int i = 0; i < ida.size(); i++) {
                    if (ida.get(i) == 1) {
                        linearLayout1.setVisibility(View.VISIBLE);
                        name1.setText(cursor.getString(1));
                        dis1.setText("" + cursor.getDouble(2));
                    } else if (ida.get(i) == 2) {
                        linearLayout2.setVisibility(View.VISIBLE);
                        name2.setText(cursor.getString(1));
                        dis2.setText("" + cursor.getDouble(2));
                    } else {
                        linearLayout3.setVisibility(View.VISIBLE);
                        name3.setText(cursor.getString(1));
                        dis3.setText("" + cursor.getDouble(2));
                    }
                    cursor.moveToNext();
                }
//            cursor.moveToFirst();
//            name1.setText(cursor.getString(1));
//            dis1.setText("" + cursor.getDouble(2));
//            if (cursor.getCount() < 2) {
//                linearLayout3.setVisibility(View.INVISIBLE);
//                linearLayout2.setVisibility(View.INVISIBLE);
//            } else {
//                cursor.moveToNext();
//                name2.setText(cursor.getString(1));
//                dis2.setText("" + cursor.getDouble(2));
//                if (cursor.getCount() < 3)
//                    linearLayout3.setVisibility(View.INVISIBLE);
//                else {
//                    cursor.moveToNext();
//                    name3.setText(cursor.getString(1));
//                    dis3.setText("" + cursor.getDouble(2));
//                }
//            }
            } else {
                Toast.makeText(this, "NO CLASS", Toast.LENGTH_SHORT).show();
                finish();
            }
            upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (cssname.getText().toString().isEmpty())
                        Toast.makeText(showclass.this, "Field cannot be empty", Toast.LENGTH_SHORT).show();
                    else {
                        boolean resu4 = classhelp.update(clsname, cssname.getText().toString());
                        if(resu4 != true) {
                            flag1 = 1;
                            Toast.makeText(showclass.this, "Update Failed", Toast.LENGTH_SHORT).show();
                        }
                        for (int i = 0; i < ida.size(); i++) {
                            if (ida.get(i) == 1) {
                                boolean resu1 = classmgmthelper.update(1, name1.getText().toString(), Double.parseDouble(dis1.getText().toString()));
                                if (resu1 != true) {
                                    flag1 = 1;
                                    Toast.makeText(showclass.this, "Update Failed", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            } else if (ida.get(i) == 2) {
                                boolean resu2 = classmgmthelper.update(2, name2.getText().toString(), Double.parseDouble(dis2.getText().toString()));
                                if (resu2 != true) {
                                    flag1 = 1;
                                    Toast.makeText(showclass.this, "Update Failed", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            } else {
                                boolean resu3 = classmgmthelper.update(3, name3.getText().toString(), Double.parseDouble(dis3.getText().toString()));
                                if (resu3 != true) {
                                    flag1 = 1;
                                    Toast.makeText(showclass.this, "Update Failed", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }
                        }
                        if (flag1 == 0) {
                            Toast.makeText(showclass.this, "Update Successful", Toast.LENGTH_SHORT).show();
                            finish();
                        }
//                {
//                    if(count<=3)
//                    {
//                        boolean resu2 = classmgmthelper.update(2,name2.getText().toString(),Double.parseDouble(dis2.getText().toString()));
//                        if(resu2 == true)
//                        {
//                            if(count == 3)
//                            {
//                                boolean resu3 = classmgmthelper.update(3,name3.getText().toString(),Double.parseDouble(dis3.getText().toString()));
//                                if(resu3 == true)
//                                {
//                                    Toast.makeText(showclass.this, "Update Successful", Toast.LENGTH_SHORT).show();
//                                    finish();
//                                }
//                                else {
//                                    Toast.makeText(showclass.this, "Update Failed", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                            else {
//                                Toast.makeText(showclass.this, "Update Successful", Toast.LENGTH_SHORT).show();
//                                finish();
//                            }
//                        }
//                        else {
//                            Toast.makeText(showclass.this, "Update Failed", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                    else {
//                        Toast.makeText(showclass.this, "Update Successful", Toast.LENGTH_SHORT).show();
//                        finish();
//                    }
//                }
//                else {
//                    Toast.makeText(showclass.this, "Update Failed", Toast.LENGTH_SHORT).show();
//                }
                    }
                }
            });
        }catch (Exception e)
        {
            Toast.makeText(this, "Database Link Failed, Please check class is created", Toast.LENGTH_SHORT).show();
            classhelp.delete(clsname);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(classmgmthelper != null)
            getMenuInflater().inflate(R.menu.showclassmenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.itdeleteclass:
            {
                classmgmthelper.droptable();
                classhelp.delete(clsname);
                Toast.makeText(this, "Delete Successful", Toast.LENGTH_SHORT).show();
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
