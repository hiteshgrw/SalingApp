package com.example.saleservice.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.saleservice.R;
import com.example.saleservice.shownetstocklayout;
import com.example.saleservice.showschoolstock;
import com.example.saleservice.stockcollectviewer;
import com.example.saleservice.stockschoolviewer;

public class StockFragment extends Fragment {
    private CardView cardshowschoolstock;
    private CardView cardshownetstock;
    private CardView cardshowentire;
    private CardView cardnuschool;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.navstock,container,false);
        cardshowschoolstock = view.findViewById(R.id.clshowschstock);
        cardshownetstock = view.findViewById(R.id.clshownetstock);
        cardnuschool = view.findViewById(R.id.clshownonuserschstock);
        cardshowentire = view.findViewById(R.id.clshownusstock);
        cardshowschoolstock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(container.getContext(), showschoolstock.class));
            }
        });
        cardshownetstock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(container.getContext(), shownetstocklayout.class));
            }
        });
        cardnuschool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(container.getContext(), stockschoolviewer.class));
            }
        });
        cardshowentire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(container.getContext(), stockcollectviewer.class));
            }
        });
        return view;
    }
}
