package com.example.saleservice.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.saleservice.Booklistcreation;
import com.example.saleservice.Classmgmt;
import com.example.saleservice.Companymgmt;
import com.example.saleservice.R;
import com.example.saleservice.ordercreator;
import com.example.saleservice.schoolmgmt;
import com.google.firebase.auth.FirebaseAuth;

public class CreateFragment extends Fragment {
    private CardView cardschool;
    private CardView cardclass;
    private CardView cardcompany;
    private CardView cardbooklist;
    private CardView cardorder;
    private TextView emailid;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.navcreatepage,container,false);
        cardschool = view.findViewById(R.id.clschool);
        cardclass = view.findViewById(R.id.clclass);
        cardcompany = view.findViewById(R.id.clcrtcmpname);
        cardbooklist = view.findViewById(R.id.clcrtbooklist);
        emailid = view.findViewById(R.id.tvemailview);
        cardorder = view.findViewById(R.id.clcrtorder);
        emailid.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        cardschool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(container.getContext(), schoolmgmt.class));
            }
        });
        cardclass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(container.getContext(), Classmgmt.class));
            }
        });
        cardcompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(container.getContext(), Companymgmt.class));
            }
        });
        cardbooklist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(container.getContext(), Booklistcreation.class));
            }
        });
        cardorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(container.getContext(), ordercreator.class));
            }
        });
        return view;
    }
}
