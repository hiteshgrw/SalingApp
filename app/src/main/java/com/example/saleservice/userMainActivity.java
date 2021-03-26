package com.example.saleservice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.saleservice.classes.schoolfirebaseclass;
import com.example.saleservice.helper.Companymgmthelper;
import com.example.saleservice.helper.bookdatabasehelper;
import com.example.saleservice.helper.schoolcompanymap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class userMainActivity extends AppCompatActivity {
    private TextView emailid;
    private CardView cardcreateinvoice;
    private CardView cardshowinvoice;
    private Integer getschid,upflag;
    private schoolcompanymap companyhelp;
    private Companymgmthelper companymgmthelper;
    private bookdatabasehelper bdbhelper;
    private schoolfirebaseclass frclass;
    private AlertDialog dialog1;
    private StorageReference storageReference;
    private ProgressDialog progressDialog4,dialog4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);
        viewsetup();
    }
    public void viewsetup(){
        emailid = findViewById(R.id.tvuseremailview);
        cardcreateinvoice = findViewById(R.id.clusercrtinvoice);
        cardshowinvoice = findViewById(R.id.clusershowinvoice);
        emailid.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        checkdir();
        cardcreateinvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(userMainActivity.this,userinvcreate.class));
            }
        });
        cardshowinvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(userMainActivity.this,userinvshow.class));
            }
        });
    }
    public void checkdir()
    {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.SaleServiceDB/Bookdata/directory.db";
//        String path1 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.SaleServiceDB/Schooldata";
//        String path2 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SalesInvoice";
        File dir = new File(path);
//        File dir1 = new File(path1);
//        File dr2 = new File(path2);
        if(!dir.exists())
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Please Confirm");
            builder.setMessage("Data is not downloaded,Do you want to download it now ?");
            builder.setCancelable(false);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog1.dismiss();
                    progressDialog4 = new ProgressDialog(userMainActivity.this);
                    progressDialog4.setMessage("Download in process....");
                    progressDialog4.show();
                    FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists())
                            {
                                frclass = dataSnapshot.getValue(schoolfirebaseclass.class);
                                getschid = frclass.getId();
                                downloadfiles();
                            }
                            else {
                                Toast.makeText(userMainActivity.this, "No data found", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            dialog1 = builder.create();
            dialog1.setCancelable(false);
            dialog1.setCanceledOnTouchOutside(false);
            dialog1.show();
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
                    progressDialog4.dismiss();
                    Toast.makeText(userMainActivity.this, "Download Failed file 1", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else {
                    storageReference.child("School"+getschid+".db").getFile(Uri.fromFile(path3)).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                            if (!task.isSuccessful()) {
                                progressDialog4.dismiss();
                                Toast.makeText(userMainActivity.this, "Download Failed file 2", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                companyhelp = new schoolcompanymap(userMainActivity.this, "School" + getschid);
                                companymgmthelper = new Companymgmthelper(userMainActivity.this);
                                List<String> cmplist = new ArrayList<>();
                                cmplist.clear();
                                cmplist = companyhelp.display();
                                for (int i = 0; i < cmplist.size(); i++) {
                                    Integer cpid = companymgmthelper.getid(cmplist.get(i));
                                    bdbhelper = new bookdatabasehelper(userMainActivity.this, "School" + getschid, "Company" + cpid + "bookdet");
                                    bdbhelper.setquan0();
                                }
                                progressDialog4.dismiss();
                                Toast.makeText(userMainActivity.this, "Download Successful", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
    public void fileupload(final Integer schid)
    {
        File file = new File(Environment.getExternalStorageDirectory() + "/.SaleServiceDB/Schooldata/School"+schid+".db");
        Uri path = FileProvider.getUriForFile(userMainActivity.this,BuildConfig.APPLICATION_ID + ".provider",file);
        storageReference.child("Userdata").child("School"+schid+".db").putFile(path).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (!task.isSuccessful()) {
                    dialog4.dismiss();
                    Toast.makeText(userMainActivity.this, "File Upload Failed 1", Toast.LENGTH_LONG).show();
                } else {
                    File file2 = new File(Environment.getExternalStorageDirectory() + "/.SaleServiceDB/Bookdata/directory.db");
                    Uri path2 = FileProvider.getUriForFile(userMainActivity.this, BuildConfig.APPLICATION_ID + ".provider", file2);
                    storageReference.child("Userdata").child("directory.db").putFile(path2).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (!task.isSuccessful()) {
                                dialog4.dismiss();
                                Toast.makeText(userMainActivity.this, "File Upload Failed 1", Toast.LENGTH_SHORT).show();
                            } else {
                                File file4 = new File(Environment.getExternalStorageDirectory() + "/.SaleServiceDB/Schooldata/invoicedet.db");
                                Uri path4 = FileProvider.getUriForFile(userMainActivity.this, BuildConfig.APPLICATION_ID + ".provider", file4);
                                storageReference.child("Userdata").child("invoicedet.db").putFile(path4).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        if (!task.isSuccessful()) {
                                            dialog4.dismiss();
                                            Toast.makeText(userMainActivity.this, "File Upload Failed 3", Toast.LENGTH_SHORT).show();
                                        } else {
                                            dialog4.dismiss();
                                            Toast.makeText(userMainActivity.this, "File upload Successful", Toast.LENGTH_SHORT).show();
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
    public void refileupload(final Integer schid)
    {
        File file = new File(Environment.getExternalStorageDirectory() + "/.SaleServiceDB/Schooldata/School"+schid+".db");
        Uri path = FileProvider.getUriForFile(userMainActivity.this,BuildConfig.APPLICATION_ID + ".provider",file);
        storageReference.child("Userdata").child("School"+schid+".db").putFile(path).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (!task.isSuccessful()) {
                    dialog4.dismiss();
                    Toast.makeText(userMainActivity.this, "File Upload Failed 1" + schid, Toast.LENGTH_LONG).show();
                } else {
                    File file2 = new File(Environment.getExternalStorageDirectory() + "/.SaleServiceDB/Bookdata/directory.db");
                    Uri path2 = FileProvider.getUriForFile(userMainActivity.this, BuildConfig.APPLICATION_ID + ".provider", file2);
                    storageReference.child("Userdata").child("directory.db").putFile(path2).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (!task.isSuccessful()) {
                                dialog4.dismiss();
                                Toast.makeText(userMainActivity.this, "File Upload Failed 2", Toast.LENGTH_SHORT).show();
                            } else {
                                File file4 = new File(Environment.getExternalStorageDirectory() + "/.SaleServiceDB/Schooldata/invoicedet.db");
                                Uri path4 = FileProvider.getUriForFile(userMainActivity.this, BuildConfig.APPLICATION_ID + ".provider", file4);
                                storageReference.child("Userdata").child("invoicedet.db").putFile(path4).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        if (!task.isSuccessful()) {
                                            dialog4.dismiss();
                                            Toast.makeText(userMainActivity.this, "File Upload Failed 3", Toast.LENGTH_SHORT).show();
                                        } else {
                                            dialog4.dismiss();
                                            Toast.makeText(userMainActivity.this, "File upload Successful", Toast.LENGTH_SHORT).show();
                                            progressDialog4 = new ProgressDialog(userMainActivity.this);
                                            progressDialog4.setMessage("Download in process....");
                                            progressDialog4.show();
                                            FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()) {
                                                        frclass = dataSnapshot.getValue(schoolfirebaseclass.class);
                                                        getschid = frclass.getId();
                                                        downloadfiles();
                                                    } else {
                                                        Toast.makeText(userMainActivity.this, "No data found", Toast.LENGTH_SHORT).show();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                }
                                            });
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
    public void filelogoutupload(final Integer schid)
    {
        File file = new File(Environment.getExternalStorageDirectory() + "/.SaleServiceDB/Schooldata/School"+schid+".db");
        Uri path = FileProvider.getUriForFile(userMainActivity.this,BuildConfig.APPLICATION_ID + ".provider",file);
        storageReference.child("Userdata").child("School"+schid+".db").putFile(path).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (!task.isSuccessful()) {
                    dialog4.dismiss();
                    Toast.makeText(userMainActivity.this, "File Upload Failed 1" + schid, Toast.LENGTH_LONG).show();
                } else {
                    File file2 = new File(Environment.getExternalStorageDirectory() + "/.SaleServiceDB/Bookdata/directory.db");
                    Uri path2 = FileProvider.getUriForFile(userMainActivity.this, BuildConfig.APPLICATION_ID + ".provider", file2);
                    storageReference.child("Userdata").child("directory.db").putFile(path2).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (!task.isSuccessful()) {
                                dialog4.dismiss();
                                Toast.makeText(userMainActivity.this, "File Upload Failed 2", Toast.LENGTH_SHORT).show();
                            } else {
                                File file4 = new File(Environment.getExternalStorageDirectory() + "/.SaleServiceDB/Schooldata/invoicedet.db");
                                Uri path4 = FileProvider.getUriForFile(userMainActivity.this, BuildConfig.APPLICATION_ID + ".provider", file4);
                                storageReference.child("Userdata").child("invoicedet.db").putFile(path4).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        if (!task.isSuccessful()) {
                                            dialog4.dismiss();
                                            Toast.makeText(userMainActivity.this, "File Upload Failed 3", Toast.LENGTH_SHORT).show();
                                        } else {
                                            dialog4.dismiss();
                                            Toast.makeText(userMainActivity.this, "File upload Successful", Toast.LENGTH_SHORT).show();
                                            File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/.SaleServiceDB/Bookdata/directory.db");
                                            final File path3 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/.SaleServiceDB/Schooldata/School" + schid + ".db");
                                            final File path5 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/.SaleServiceDB/Schooldata/invoicedet.db");
                                            if (path.exists()) {
                                                path.delete();
//            Toast.makeText(this, "File delete", Toast.LENGTH_SHORT).show();
                                            }
                                            if (path3.exists())
                                                path3.delete();
                                            if (path5.exists())
                                                path5.delete();
                                            FirebaseAuth.getInstance().signOut();
                                            finish();
                                            startActivity(new Intent(userMainActivity.this, loginpage.class));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.usermenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.userreloaddata:{
                AlertDialog.Builder buildersh = new AlertDialog.Builder(this);
                buildersh.setTitle("Please Confirm :");
                buildersh.setMessage("Re - Downloading the data will reset the data to initial form. Do you want to continue ?");
                buildersh.setCancelable(false);
                buildersh.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog4 = new ProgressDialog(userMainActivity.this);
                        dialog4.setMessage("Upload in process.....");
                        dialog4.setCanceledOnTouchOutside(false);
                        dialog4.show();
                        FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists())
                                {
                                    frclass = dataSnapshot.getValue(schoolfirebaseclass.class);
                                    getschid = frclass.getId();
                                    storageReference = FirebaseStorage.getInstance().getReference(FirebaseAuth.getInstance().getUid());
                                    refileupload(getschid);
                                }
                                else {
                                    Toast.makeText(userMainActivity.this, "No data found", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });
                buildersh.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = buildersh.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setCancelable(false);
                alertDialog.show();
                break;
            }
            case R.id.useruploaddata:{
                dialog4 = new ProgressDialog(this);
                dialog4.setMessage("Upload in process.....");
                dialog4.setCanceledOnTouchOutside(false);
                dialog4.show();
                FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists())
                        {
                            frclass = dataSnapshot.getValue(schoolfirebaseclass.class);
                            getschid = frclass.getId();
                            storageReference = FirebaseStorage.getInstance().getReference(FirebaseAuth.getInstance().getUid());
                            fileupload(getschid);
                        }
                        else {
                            Toast.makeText(userMainActivity.this, "No data found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                break;
            }
            case R.id.userlogout:{
                dialog4 = new ProgressDialog(this);
                dialog4.setMessage("Upload in process.....");
                dialog4.show();
                FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists())
                        {
                            frclass = dataSnapshot.getValue(schoolfirebaseclass.class);
                            getschid = frclass.getId();
                            storageReference = FirebaseStorage.getInstance().getReference(FirebaseAuth.getInstance().getUid());
                            filelogoutupload(getschid);
                        }
                        else {
                            Toast.makeText(userMainActivity.this, "No data found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                break;
            }
            case R.id.resetdatadwnld:{
                dialog4 = new ProgressDialog(this);
                dialog4.setMessage("Download in process.....");
                dialog4.setCanceledOnTouchOutside(false);
                dialog4.show();
                FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists())
                        {
                            frclass = dataSnapshot.getValue(schoolfirebaseclass.class);
                            getschid = frclass.getId();
                            downloadresetfiles(getschid);
                        }
                        else {
                            Toast.makeText(userMainActivity.this, "No data found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
        return super.onOptionsItemSelected(item);
    }
    public void downloadresetfiles(final Integer iid)
    {
        checkcreatedir();
        final File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/.SaleServiceDB/Bookdata/directory.db");
        final File path3 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/.SaleServiceDB/Schooldata/School"+iid+".db");
        final File path5 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/.SaleServiceDB/Schooldata/invoicedet.db");
        if(path.exists()) {
            path.delete();
//            Toast.makeText(this, "File deleted", Toast.LENGTH_SHORT).show();
        }
       if(path3.exists())
            path3.delete();
       if(path5.exists())
           path5.delete();
       final StorageReference storageReference1 = FirebaseStorage.getInstance().getReference(FirebaseAuth.getInstance().getUid()).child("Userdata");
       storageReference1.child("directory.db").getFile(Uri.fromFile(path)).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                if(!task.isSuccessful()){
                    dialog4.dismiss();
                    Toast.makeText(userMainActivity.this, "Download Failed file 1", Toast.LENGTH_SHORT).show();
                }
                else {
                    storageReference1.child("School"+iid+".db").getFile(Uri.fromFile(path3)).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                            if(!task.isSuccessful())
                            {
                                dialog4.dismiss();
                                Toast.makeText(userMainActivity.this, "Download Failed file 2", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                storageReference1.child("invoicedet.db").getFile(Uri.fromFile(path5)).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                                        if(!task.isSuccessful()){
                                            dialog4.dismiss();
                                            Toast.makeText(userMainActivity.this, "Download Failed file 3", Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            dialog4.dismiss();
                                            Toast.makeText(userMainActivity.this, "Download Successful", Toast.LENGTH_SHORT).show();
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
}