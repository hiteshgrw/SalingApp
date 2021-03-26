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
import com.example.saleservice.classes.netstockclass;

import java.util.List;

public class netstockadapter extends ArrayAdapter<netstockclass> {
    private Context mcontext;
    private Integer mres;
    private Integer lastposition = -1;
    static  class Viewholder{
        TextView bookname;
        TextView companyname;
        TextView bookpquan;
        TextView bookprice;
        TextView booksquan;
    }
    public netstockadapter(@NonNull Context context, int resource, @NonNull List<netstockclass> objects) {
        super(context, resource, objects);
        mcontext = context;
        mres = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String name = getItem(position).getName();
        String cname = getItem(position).getCname();
        Integer pquant = getItem(position).getPquan();
        Double prc = getItem(position).getPrice();
        Integer squant = getItem(position).getSquan();
        netstockclass stocklayout = new netstockclass(name,cname,prc,pquant,squant);
        final View result;
        Viewholder viewholder;
        if(convertView == null)
        {
            LayoutInflater inflater = LayoutInflater.from(mcontext);
            convertView = inflater.inflate(mres,parent,false);
            viewholder = new Viewholder();
            viewholder.bookname = convertView.findViewById(R.id.nstockvbname);
            viewholder.companyname = convertView.findViewById(R.id.nstockvcname);
            viewholder.bookpquan = convertView.findViewById(R.id.nstockvbpquan);
            viewholder.bookprice = convertView.findViewById(R.id.nstockvbprice);
            viewholder.booksquan = convertView.findViewById(R.id.nstockvbsquan);
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
        viewholder.companyname.setText(stocklayout.getCname());
        viewholder.bookpquan.setText("Purchsed " + stocklayout.getPquan() +" Pcs" );
        viewholder.bookprice.setText("Rs "+stocklayout.getPrice());
        viewholder.booksquan.setText("Sold " + stocklayout.getSquan() + " Pcs" );
        return convertView;
    }
}
