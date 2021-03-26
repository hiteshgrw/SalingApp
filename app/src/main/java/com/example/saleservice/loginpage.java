package com.example.saleservice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.saleservice.classes.schoolfirebaseclass;
import com.example.saleservice.helper.Companymgmthelper;
import com.example.saleservice.helper.bookdatabasehelper;
import com.example.saleservice.helper.schoolcompanymap;
import com.example.saleservice.helper.userclasshelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class loginpage extends AppCompatActivity {
    private TextInputLayout emailfield,passwordfield;
    private Button login;
    private TextView attemptsleft;
    private int count = 5;
//    private ImageView unlockpassword;
    private ImageView imageView;
    private int check = 0;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private Integer getschid;
    private schoolfirebaseclass frclass;
    private Integer flag = 0;
    private userclasshelper usermap;
    private schoolcompanymap companyhelp;
    private Companymgmthelper companymgmthelper;
    private bookdatabasehelper bdbhelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginpage);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null)
        {
            if(user.getEmail().equals("anilagarwal8410@gmail.com")) {
                finish();
                startActivity(new Intent(loginpage.this, splashscreen.class));
            }
            else {
                finish();
                startActivity(new Intent(loginpage.this, userMainActivity.class));
            }
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CONTACTS}, PackageManager.PERMISSION_GRANTED);
            initialsetup();
        }
    }
    public void initialsetup()
    {
        emailfield = findViewById(R.id.etloginemail);
        passwordfield = findViewById(R.id.etloginpass);
        login = findViewById(R.id.btlogin);
        imageView = findViewById(R.id.imageView);
        imageView.setClipToOutline(true);
        attemptsleft = findViewById(R.id.tvattempts);
        attemptsleft.setText("");
//        unlockpassword = findViewById(R.id.unlockpassword);
        progressDialog = new ProgressDialog(this);
        usermap = new userclasshelper(this);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginsetup();
            }
        });
//        unlockpassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(check==0) {
//                    passwordfield.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//                    unlockpassword.setImageResource(R.drawable.ic_passlock);
//                    check=1;
//                }
//                else {
//                    passwordfield.setTransformationMethod(PasswordTransformationMethod.getInstance());
//                    unlockpassword.setImageResource(R.drawable.ic_passopen);
//                    check=0;
//                }
//            }
//        });
    }
    private boolean validator(TextInputLayout textInputLayout)
    {
        if(textInputLayout.getEditText().getText().toString().isEmpty())
        {
            textInputLayout.setError("Field cannot be empty");
            textInputLayout.getEditText().setCompoundDrawablesRelativeWithIntrinsicBounds(textInputLayout.getId() == emailfield.getId()? R.drawable.ic_action_show:R.drawable.ic_passlock,0,R.drawable.ic_error,0);
            return false;
        }
        else{
            textInputLayout.setError(null);
            textInputLayout.getEditText().setCompoundDrawablesRelativeWithIntrinsicBounds(textInputLayout.getId() == emailfield.getId()? R.drawable.ic_action_show:R.drawable.ic_passlock,0,0,0);
            return true;
        }
    }
    private void loginsetup(){
        progressDialog.setMessage("Wait, Login is still in progress....");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        boolean eres = validator(emailfield);
        boolean pres = validator(passwordfield);
        final String email = emailfield.getEditText().getText().toString();
        String password = passwordfield.getEditText().getText().toString();
        if(eres && pres){
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
            {
                emailfield.setError("Email ID is not valid");
                emailfield.getEditText().setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_action_show,0,R.drawable.ic_error,0);
            }
            else {
                emailfield.setError(null);
                emailfield.getEditText().setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_action_show,0,0,0);
                progressDialog.show();
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (email.equals("anilagarwal8410@gmail.com")) {
                                progressDialog.dismiss();

                                Toast.makeText(loginpage.this, "Login Successful", Toast.LENGTH_LONG).show();
                                finish();
                                startActivity(new Intent(loginpage.this, MainPage.class));
                            } else {
//                            Toast.makeText(loginpage.this, FirebaseAuth.getInstance().getUid(), Toast.LENGTH_LONG).show();
                                FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            deleteDatabase("localschool.db");
                                            frclass = dataSnapshot.getValue(schoolfirebaseclass.class);
//                                        Toast.makeText(loginpage.this, "Id = " + frclass.getId(), Toast.LENGTH_SHORT).show();
                                            usermap.insert(frclass.getId(), frclass.getName());
                                            getschid = frclass.getId();
                                            downloadfiles();
                                        } else {
                                            Toast.makeText(loginpage.this, "not found", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(loginpage.this, "Login Failed", Toast.LENGTH_SHORT).show();
                            emailfield.getEditText().setText("");
                            passwordfield.getEditText().setText("");
                            count--;
                            if (count == 0) {
                                login.setEnabled(false);
                            }
                            attemptsleft.setText("No of attempts left : " + count);
                        }
                    }
                });
            }
        }
    }
    public void downloadfiles()
    {
        checkcreatedir();
        File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/.SaleServiceDB/Bookdata/directory.db");
        final File path3 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/.SaleServiceDB/Schooldata/School"+getschid+".db");
        if(path.exists()) {
            path.delete();
//            Toast.makeText(this, "File deleted", Toast.LENGTH_SHORT).show();
        }
        if(path3.exists())
            path3.delete();
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference(FirebaseAuth.getInstance().getUid());
        storageReference.child("directory.db").getFile(Uri.fromFile(path)).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                if(!task.isSuccessful())
                {
                    progressDialog.dismiss();
                    Toast.makeText(loginpage.this, "Download Failed file 1", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else {
                    storageReference.child("School"+getschid+".db").getFile(Uri.fromFile(path3)).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) { if(!task.isSuccessful()){
                            progressDialog.dismiss();
                            Toast.makeText(loginpage.this, "Download Failed file 2", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else {
                            companyhelp = new schoolcompanymap(loginpage.this,"School"+getschid);
                            companymgmthelper = new Companymgmthelper(loginpage.this);
                            List<String> cmplist = new ArrayList<>();
                            cmplist.clear();
                            cmplist = companyhelp.display();
                            for (int i = 0; i <cmplist.size() ; i++) {
                                Integer cpid = companymgmthelper.getid(cmplist.get(i));
                                bdbhelper = new bookdatabasehelper(loginpage.this,"School"+getschid,"Company"+cpid+"bookdet");
                                bdbhelper.setquan0();
                            }
                            progressDialog.dismiss();
                            Toast.makeText(loginpage.this, "Download Successful", Toast.LENGTH_SHORT).show();
                            Toast.makeText(loginpage.this, "Login Successful", Toast.LENGTH_LONG).show();
                            finish();
                            startActivity(new Intent(loginpage.this, userMainActivity.class));
                        }
                        }
                    });
                }
            }
        });
    }
    public void checkcreatedir()
    {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.SaleServiceDB/Bookdata";
        String path1 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.SaleServiceDB/Schooldata";
        String path2 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SalesInvoice";
        File dir = new File(path);
        File dir1 = new File(path1);
        File dr2 = new File(path2);
        if(!dir.exists())
            dir.mkdirs();
        if(!dir1.exists())
            dir1.mkdirs();
        if(!dr2.exists())
            dr2.mkdirs();
    }
}
