package com.example.saleservice;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.saleservice.helper.Companymgmthelper;


import java.util.ArrayList;
import java.util.List;

public class Companymgmt extends AppCompatActivity {
    private EditText name;
    private Button create,delet;
    private TextView showall;
    private Companymgmthelper companymgmthelper;
    private List<String> cmpname;
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
        setContentView(R.layout.activity_companymgmt);
//        SQLiteDatabase.loadLibs(Companymgmt.this);
        viewsetup();
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createcompany();
            }
        });
        showall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showcompany();
            }
        });
    }
    public void viewsetup()
    {
        name = findViewById(R.id.etcrcmpname);
        name.setFilters(new InputFilter[]{filter});
        create = findViewById(R.id.btcreatecmpname);
        delet = findViewById(R.id.btdeletecompany);
        showall = findViewById(R.id.tvallcompany);
//        Companymgmthelper.getInstance(this);
        companymgmthelper = new Companymgmthelper(this);
        cmpname = new ArrayList<>();
    }
    public void createcompany()
    {
        if(name.getText().toString().isEmpty())
            Toast.makeText(this, "Name Field is Empty", Toast.LENGTH_SHORT).show();
        else {
            boolean result = companymgmthelper.insert(name.getText().toString());
            if(result == true)
            {
                Toast.makeText(this, "Company Created", Toast.LENGTH_SHORT).show();
                name.setText("");
            }
            else
                Toast.makeText(this, "Company Creation Failed", Toast.LENGTH_SHORT).show();
        }
    }
    public void showcompany()
    {
        setContentView(R.layout.samplelistview);
        ListView listView = findViewById(R.id.splistview);
        try {
            Cursor cursor = companymgmthelper.display();
            if(cursor.getCount()>0)
            {
                cmpname.clear();
                while (cursor.moveToNext())
                {
                    cmpname.add(cursor.getString(1));
                }
                ArrayAdapter<String> adapter =new ArrayAdapter<String>(Companymgmt.this,android.R.layout.simple_list_item_1,cmpname);
                listView.setAdapter(adapter);
            }
            else {
                Toast.makeText(this, "No entry exists", Toast.LENGTH_SHORT).show();
                finish();
            }
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    editcompany(position);
                }
            });
        }catch (Exception e)
        {
            Toast.makeText(this, "No database/table exists", Toast.LENGTH_SHORT).show();
        }
    }
    public void editcompany(final int pos){
        setContentView(R.layout.activity_companymgmt);
        name = findViewById(R.id.etcrcmpname);
        name.setFilters(new InputFilter[]{filter});
        create = findViewById(R.id.btcreatecmpname);
        showall = findViewById(R.id.tvallcompany);
        delet = findViewById(R.id.btdeletecompany);
        showall.setVisibility(View.INVISIBLE);
        name.setText(cmpname.get(pos));
        delet.setVisibility(View.VISIBLE);
        create.setText("SAVE");
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name.getText().toString().isEmpty())
                    Toast.makeText(Companymgmt.this, "Field cannot be empty", Toast.LENGTH_SHORT).show();
                else {
                    boolean result = companymgmthelper.update(cmpname.get(pos),name.getText().toString());
                    if(result == true)
                    {
                        Toast.makeText(Companymgmt.this, "Update successful", Toast.LENGTH_SHORT).show();
                        showcompany();
                    }
                    else {
                        Toast.makeText(Companymgmt.this, "Update Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        delet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean res = companymgmthelper.delete(cmpname.get(pos));
                if(res == true)
                {
                    Toast.makeText(Companymgmt.this, "Delete Successful", Toast.LENGTH_SHORT).show();
                    showcompany();
                }
                else
                    Toast.makeText(Companymgmt.this, "Delete Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
