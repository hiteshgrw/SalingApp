package com.example.saleservice;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.saleservice.classes.userdetails;
import com.example.saleservice.helper.schoolmgmthelper;
import com.example.saleservice.helper.userhelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class dwnlduserdata extends AppCompatActivity {
    private Spinner schoolname;
    private schoolmgmthelper schoolhelp;
    private userhelper userhelp;
    private ListView listView;
    private List<String> schoolnlist,usernlist;
    private userdetails userclass;
    private List<userdetails> userlist;
    private StorageReference storageReference;
    private ProgressDialog dialog;
    private Integer userid,iid;
    private String useruid;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialsetup();
    }
    public void initialsetup()
    {
        setContentView(R.layout.activity_showalluser);
        schoolname = findViewById(R.id.spshusrschname);
        listView = findViewById(R.id.showuserlistview);
        schoolhelp = new schoolmgmthelper(dwnlduserdata.this);
        userhelp = new userhelper(dwnlduserdata.this);
        schoolnlist = new ArrayList<>();
        usernlist = new ArrayList<>();
        userlist = new ArrayList<>();
        dialog = new ProgressDialog(this);
        dialog.setMessage("Download in process.....");
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
                            iid = schoolhelp.getid(schoolname.getSelectedItem().toString());
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
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(dwnlduserdata.this,android.R.layout.simple_list_item_1,usernlist);
                            listView.setAdapter(adapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    if(position == 0)
                                    {
                                        Toast.makeText(dwnlduserdata.this, "Admin details already exists,Try other user", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        dialog.show();
                                        useruid = userlist.get(position).getUid();
                                        userid = userlist.get(position).getId();
                                        downloadfiles();
                                    }
                                }
                            });
                        }
                        else {
                            Toast.makeText(dwnlduserdata.this, "No user exists", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }catch (Exception e)
                    {
                        Toast.makeText(dwnlduserdata.this, "Database failed", Toast.LENGTH_SHORT).show();
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
    public void checkcreatedir()
    {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.SaleServiceDB/User"+userid+"/Bookdata";
        String path1 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.SaleServiceDB/User"+userid+"/Schooldata";
        File dir = new File(path);
        File dir1 = new File(path1);
        if(!dir.exists())
            dir.mkdirs();
        if(!dir1.exists())
            dir1.mkdirs();
    }
    public void downloadfiles()
    {
        checkcreatedir();
        File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/.SaleServiceDB/User"+userid+"/Bookdata/directory.db");
        final File path3 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/.SaleServiceDB/User"+userid+"/Schooldata/School"+iid+".db");
        final File path5 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/.SaleServiceDB/User"+userid+"/Schooldata/invoicedet.db");
        if(path.exists()) {
            path.delete();
//            Toast.makeText(this, "File deleted", Toast.LENGTH_SHORT).show();
        }
        if(path3.exists())
            path3.delete();
        if(path5.exists())
            path5.delete();
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference(useruid).child("Userdata");
        storageReference.child("directory.db").getFile(Uri.fromFile(path)).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                if (!task.isSuccessful()) {
                    dialog.dismiss();
                    Toast.makeText(dwnlduserdata.this, "Download Failed file 1", Toast.LENGTH_SHORT).show();
                } else {
                    storageReference.child("School" + iid + ".db").getFile(Uri.fromFile(path3)).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                            if (!task.isSuccessful()) {
                                dialog.dismiss();
                                Toast.makeText(dwnlduserdata.this, "Download Failed file 3", Toast.LENGTH_SHORT).show();
                            } else {
                                storageReference.child("invoicedet.db").getFile(Uri.fromFile(path5)).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                                        if (!task.isSuccessful()) {
                                            dialog.dismiss();
                                            Toast.makeText(dwnlduserdata.this, "Download Failed file 5", Toast.LENGTH_SHORT).show();
                                        } else {
                                            dialog.dismiss();
                                            Toast.makeText(dwnlduserdata.this, "Download Successful", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
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
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(dwnlduserdata.this, android.R.layout.simple_spinner_item, schoolnlist);
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
