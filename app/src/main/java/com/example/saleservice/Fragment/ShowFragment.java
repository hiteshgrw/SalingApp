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

import com.example.saleservice.R;
import com.example.saleservice.Showbooklist;
import com.example.saleservice.phnno;
import com.example.saleservice.showclass;
import com.example.saleservice.shownetstocklayout;
import com.example.saleservice.showschoolstock;
import com.example.saleservice.stockcollectviewer;
import com.google.firebase.auth.FirebaseAuth;

public class ShowFragment extends Fragment {
    private CardView cardshowclass;
    private CardView cardshowbooklist;
//    private CardView cardshowschoolstock;
//    private CardView cardshownetstock;
    private CardView cardshowcontact;
//    private CardView cardshowentire;
    private TextView emailid;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.navshowpage,container,false);
        cardshowclass = view.findViewById(R.id.clshowclass);
        cardshowbooklist = view.findViewById(R.id.clshowbooklist);
//        cardshowschoolstock = view.findViewById(R.id.clshowschstock);
//        cardshownetstock = view.findViewById(R.id.clshownetstock);
        cardshowcontact = view.findViewById(R.id.clshowcontact);
//        cardshowentire = view.findViewById(R.id.clshownusstock);
        emailid = view.findViewById(R.id.tvemailview);
        emailid.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        cardshowclass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(container.getContext(), showclass.class));
            }
        });

        cardshowbooklist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(container.getContext(), Showbooklist.class));
            }
        });
//        cardshowschoolstock.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(container.getContext(), showschoolstock.class));
//            }
//        });
//        cardshownetstock.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(container.getContext(), shownetstocklayout.class));
//            }
//        });
        cardshowcontact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(container.getContext(), phnno.class));
            }
        });
//        cardshowentire.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(container.getContext(), stockcollectviewer.class));
//            }
//        });
        return view;
    }
}
