package com.example.saleservice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.saleservice.classes.userdetails;
import com.example.saleservice.helper.schoolmgmthelper;
import com.example.saleservice.helper.userhelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class resetuserpass extends AppCompatActivity {
    private Spinner schoolname;
    private schoolmgmthelper schoolhelp;
    private userhelper userhelp;
    private ListView listView;
    private List<String> schoolnlist,usernlist;
    private userdetails userclass;
    private List<userdetails> userlist;
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
        schoolhelp = new schoolmgmthelper(resetuserpass.this);
        userhelp = new userhelper(resetuserpass.this);
        schoolnlist = new ArrayList<>();
        usernlist = new ArrayList<>();
        userlist = new ArrayList<>();
        dialog = new ProgressDialog(this);
        dialog.setMessage("Update in process.....");
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
                            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(resetuserpass.this,android.R.layout.simple_list_item_1,usernlist);
                            listView.setAdapter(adapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                                    if(position == 0)
                                    {
                                        Toast.makeText(resetuserpass.this, "Admin password cannot be changed,Try other user", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        final AlertDialog alertDialog;
                                        AlertDialog.Builder builder = new AlertDialog.Builder(resetuserpass.this);
                                        View mview = getLayoutInflater().inflate(R.layout.passwordreset,null);
                                        final EditText newpass = mview.findViewById(R.id.etresetpass);
                                        builder.setView(mview);
                                        builder.setCancelable(false);
                                        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(final DialogInterface dialog1, int which) {
                                                final String password = newpass.getText().toString();
                                                dialog.show();
                                                AuthCredential authCredential = EmailAuthProvider.getCredential(userlist.get(position).getEmail(),userlist.get(position).getPass());
                                                FirebaseAuth.getInstance().signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                        if(task.isSuccessful()) {
                                                            FirebaseAuth.getInstance().getCurrentUser().updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if(task.isSuccessful())
                                                                    {
                                                                        userhelp.updatepass(userlist.get(position).getId(),password);
                                                                        userlist.get(position).setPass(password);
                                                                        FirebaseAuth.getInstance().signInWithEmailAndPassword("anilagarwal8410@gmail.com", "bimla@4149").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                                                if(!task.isSuccessful())
                                                                                {
                                                                                    dialog.dismiss();
                                                                                    Toast.makeText(resetuserpass.this, "Password Updated Successfully", Toast.LENGTH_SHORT).show();
                                                                                    finish();
                                                                                    startActivity(new Intent(resetuserpass.this,loginpage.class));
                                                                                }
                                                                                else {
                                                                                    dialog.dismiss();
                                                                                    Toast.makeText(resetuserpass.this, "Password Updated Successfully", Toast.LENGTH_SHORT).show();
                                                                                    listView.setAdapter(adapter);
                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                    else
                                                                        Toast.makeText(resetuserpass.this, "Password Reset Failed", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        }
                                                        else {
                                                            Toast.makeText(resetuserpass.this, "Failed", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog1, int which) {
                                                dialog1.dismiss();
                                            }
                                        });
                                        alertDialog = builder.create();
                                        alertDialog.setCanceledOnTouchOutside(false);
                                        alertDialog.show();
                                    }
                                }
                            });
                        }
                        else {
                            Toast.makeText(resetuserpass.this, "No user exists", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }catch (Exception e)
                    {
                        Toast.makeText(resetuserpass.this, "Database failed", Toast.LENGTH_SHORT).show();
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
    public void schoolsetup() {
        try {
            Cursor cursor = schoolhelp.display();
            if (cursor.getCount() > 0) {
                schoolnlist.clear();
                schoolnlist.add(0, "Choose School Name");
                while (cursor.moveToNext()) {
                    schoolnlist.add(cursor.getString(1));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(resetuserpass.this, android.R.layout.simple_spinner_item, schoolnlist);
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
