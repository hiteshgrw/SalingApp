package com.example.saleservice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.saleservice.helper.schoolmgmthelper;
import com.example.saleservice.helper.userhelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.example.saleservice.classes.userdetails;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class showalluser extends AppCompatActivity {
    private Spinner schoolname;
    private schoolmgmthelper schoolhelp;
    private userhelper userhelp;
    private ListView listView;
    private List<String> schoolnlist,usernlist;
    private userdetails userclass;
    private List<userdetails> userlist;
    private StorageReference storageReference;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialsetup();
    }
    public void initialsetup()
    {
        setContentView(R.layout.activity_showalluser);
        schoolname = findViewById(R.id.spshusrschname);
        listView = findViewById(R.id.showuserlistview);
        schoolhelp = new schoolmgmthelper(showalluser.this);
        userhelp = new userhelper(showalluser.this);
        schoolnlist = new ArrayList<>();
        usernlist = new ArrayList<>();
        userlist = new ArrayList<>();
        dialog = new ProgressDialog(this);
        dialog.setMessage("Upload in process.....");
        schoolsetup();
        schoolname.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0)
                {
                    try {
                        final Cursor cursor = userhelp.display();
                        if(cursor.getCount()>0)
                        {
                            final Integer iid = schoolhelp.getid(schoolname.getSelectedItem().toString());
                            userlist.clear();
                            usernlist.clear();
                            while (cursor.moveToNext())
                            {
                                if(cursor.getInt(4) == iid)
                                {
                                    userclass = new userdetails(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getInt(4));
                                    userlist.add(userclass);
                                    usernlist.add(cursor.getString(1));
                                }
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(showalluser.this,android.R.layout.simple_list_item_1,usernlist);
                            listView.setAdapter(adapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    if(position == 0)
                                    {
                                        Toast.makeText(showalluser.this, "Admin details cannot be uploaded,Try other user", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        dialog.show();
                                        storageReference = FirebaseStorage.getInstance().getReference(userlist.get(position).getUid());
                                        fileupload(iid);
                                    }
                                }
                            });
                        }
                        else {
                            Toast.makeText(showalluser.this, "No user exists", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }catch (Exception e)
                    {
                        Toast.makeText(showalluser.this, "Database failed", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                else {
                    listView.setAdapter(null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    public void fileupload(final Integer schid)
    {
        File file = new File(Environment.getExternalStorageDirectory() + "/.SaleServiceDB/Schooldata/School"+schid+".db");
        Uri path = FileProvider.getUriForFile(showalluser.this,BuildConfig.APPLICATION_ID + ".provider",file);
        storageReference.child("School"+schid+".db").putFile(path).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(!task.isSuccessful()) {
                    dialog.dismiss();
                    Toast.makeText(showalluser.this, "File 1 Upload Failed", Toast.LENGTH_SHORT).show();
                }
                else {
                    File file2 = new File(Environment.getExternalStorageDirectory() + "/.SaleServiceDB/Bookdata/directory.db");
                    Uri path2 = FileProvider.getUriForFile(showalluser.this,BuildConfig.APPLICATION_ID + ".provider",file2);
                    storageReference.child("directory.db").putFile(path2).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) { if(!task.isSuccessful()) {
                            dialog.dismiss();
                            Toast.makeText(showalluser.this, "File 2 Upload Failed", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            dialog.dismiss();
                            Toast.makeText(showalluser.this, "File Upload Successful", Toast.LENGTH_SHORT).show();
                        }
                        }
                    });
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
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(showalluser.this, android.R.layout.simple_spinner_item, schoolnlist);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                schoolname.setAdapter(adapter);
            } else {
                Toast.makeText(this, "No schools, Create school first", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
