package com.example.saleservice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.saleservice.R;
import com.example.saleservice.classes.invoiceshowclass;
import com.example.saleservice.classes.stockclass;

import java.util.List;

public class stockadapter extends ArrayAdapter<stockclass> {
    private Context mcontext;
    private Integer mres;
    private Integer lastposition = -1;
    static  class Viewholder{
        TextView bookname;
        TextView bookpquan;
        TextView bookprice;
        TextView booksquan;
    }
    public stockadapter(@NonNull Context context, int resource, @NonNull List<stockclass> objects) {
        super(context, resource, objects);
        mcontext = context;
        mres = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String name = getItem(position).getName();
        Integer pquant = getItem(position).getPquan();
        Double prc = getItem(position).getPrice();
        Integer squant = getItem(position).getSquan();
        stockclass stocklayout = new stockclass(name,prc,pquant,squant);
        final View result;
        Viewholder viewholder;
        if(convertView == null)
        {
            LayoutInflater inflater = LayoutInflater.from(mcontext);
            convertView = inflater.inflate(mres,parent,false);
            viewholder = new Viewholder();
            viewholder.bookname = convertView.findViewById(R.id.stockvbname);
            viewholder.bookpquan = convertView.findViewById(R.id.stockvbpquan);
            viewholder.bookprice = convertView.findViewById(R.id.stockvbprice);
            viewholder.booksquan = convertView.findViewById(R.id.stockvbsquan);
            result = convertView;
            convertView.setTag(viewholder);
        }
        else {
            viewholder = (Viewholder) convertView.getTag();
            result = convertView;
        }
        Animation animation = AnimationUtils.loadAnimation(mcontext,(position > lastposition) ? R.anim.load_down_anim : R.anim.load_up_anim);
        result.startAnimation(animation);
        lastposition = position;
        viewholder.bookname.setText(stocklayout.getName());
        viewholder.bookpquan.setText("Purchsed " + stocklayout.getPquan() +" Pcs" );
        viewholder.bookprice.setText("Rs "+stocklayout.getPrice());
        viewholder.booksquan.setText("Sold " + stocklayout.getSquan() + " Pcs" );
        return convertView;
    }
}
