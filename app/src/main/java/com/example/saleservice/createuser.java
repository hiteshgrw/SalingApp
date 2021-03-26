package com.example.saleservice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.saleservice.classes.schoolfirebaseclass;
import com.example.saleservice.helper.schoolmgmthelper;
import com.example.saleservice.helper.userhelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class createuser extends AppCompatActivity {
    private Spinner schoolname;
    private EditText email;
    private EditText pass;
    private Button create;
    private schoolmgmthelper schoolhelp;
    private List<String> schoolnlist;
    private userhelper userhelp;
    private ProgressDialog dialog;
    private StorageReference storageReference;
    private schoolfirebaseclass frclass;
    private Integer schid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewsetup();
    }
    public void viewsetup()
    {
        setContentView(R.layout.activity_createuser);
        schoolname = findViewById(R.id.spuserschname);
        email = findViewById(R.id.etcuuseremail);
        pass = findViewById(R.id.etcuuserpass);
        create = findViewById(R.id.btcreateuser);
        schoolhelp = new schoolmgmthelper(this);
        userhelp = new userhelper(this);
        schoolnlist = new ArrayList<>();
        dialog = new ProgressDialog(this);
        dialog.setMessage("Creation in process....");
        schoolsetup();
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(schoolname.getSelectedItemPosition() == 0 || email.getText().toString().isEmpty() || pass.getText().toString().isEmpty())
                {
                    Toast.makeText(createuser.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                }
                else {
                    dialog.show();
                    final String emailid  = email.getText().toString();
                    final String password = pass.getText().toString();
                    schid = schoolhelp.getid(schoolname.getSelectedItem().toString());
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailid,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                userhelp.insert(emailid,FirebaseAuth.getInstance().getUid(),password,schid);
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getUid());
                                frclass = new schoolfirebaseclass(schoolname.getSelectedItem().toString(),schid);
                                databaseReference.setValue(frclass);
                                storageReference = FirebaseStorage.getInstance().getReference(FirebaseAuth.getInstance().getUid());
                                FirebaseAuth.getInstance().signOut();
                                FirebaseAuth.getInstance().signInWithEmailAndPassword("anilagarwal8410@gmail.com","bimla@4149").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task1) {
                                        if(!task1.isSuccessful())
                                        {
                                            fileupload();
                                            Toast.makeText(createuser.this, "Access Failed", Toast.LENGTH_SHORT).show();
                                            finish();
                                            startActivity(new Intent(createuser.this,loginpage.class));
                                        }
                                    }
                                });
                                fileupload();
                                Toast.makeText(createuser.this, "User created successfully", Toast.LENGTH_SHORT).show();
                                schoolname.setSelection(0);
                                email.setText("");
                                pass.setText("");
                            }
                            else {
                                dialog.dismiss();
                                if(userhelp.existscheck(emailid))
                                    Toast.makeText(createuser.this, "User already exists", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(createuser.this, "User Creation Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
    public void fileupload()
    {
        File file = new File(Environment.getExternalStorageDirectory() + "/.SaleServiceDB/Schooldata/School"+schid+".db");
        Uri path = FileProvider.getUriForFile(createuser.this,BuildConfig.APPLICATION_ID + ".provider",file);
        storageReference.child("School"+schid+".db").putFile(path).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(!task.isSuccessful())
                    Toast.makeText(createuser.this, "File 1 Upload Failed", Toast.LENGTH_SHORT).show();
                else {
                    File file2 = new File(Environment.getExternalStorageDirectory() + "/.SaleServiceDB/Bookdata/directory.db");
                    Uri path2 = FileProvider.getUriForFile(createuser.this,BuildConfig.APPLICATION_ID + ".provider",file2);
                    storageReference.child("directory.db").putFile(path2).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) { if(!task.isSuccessful()) {
                                        dialog.dismiss();
                                        Toast.makeText(createuser.this, "File 2 Upload Faled", Toast.LENGTH_SHORT).show();
                                    }
                                        else {
                                            dialog.dismiss();
                                            Toast.makeText(createuser.this, "File Upload Successful", Toast.LENGTH_SHORT).show();
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
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(createuser.this, android.R.layout.simple_spinner_item, schoolnlist);
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
