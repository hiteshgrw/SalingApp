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
import com.example.saleservice.invoicecreation;
import com.example.saleservice.invoiceshow;
import com.google.firebase.auth.FirebaseAuth;

public class AddFragment extends Fragment {
    private CardView cardcreateinvoice;
    private CardView cardshowinvoice;
    private TextView emailid;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.navaddpage,container,false);
        cardcreateinvoice = view.findViewById(R.id.clcrtinvoice);
        cardshowinvoice = view.findViewById(R.id.clshowinvoice);
        emailid = view.findViewById(R.id.tvemailview);
        emailid.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        cardcreateinvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(container.getContext(), invoicecreation.class));
            }
        });
        cardshowinvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(container.getContext(), invoiceshow.class));
            }
        });
        return view;
    }
}
