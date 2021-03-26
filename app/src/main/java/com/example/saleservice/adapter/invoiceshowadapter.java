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
import com.example.saleservice.classes.booklistlayout;
import com.example.saleservice.classes.invoiceshowclass;

import java.util.List;

public class invoiceshowadapter extends ArrayAdapter<invoiceshowclass> {
    private Context mcontext;
    private Integer mres;
    private Integer lastposition = -1;
    static  class Viewholder{
        TextView bookname;
        TextView bookcname;
        TextView bookquan;
        TextView bookprice;
        TextView bookdis;
        TextView booktotal;
    }
    public invoiceshowadapter(@NonNull Context context, int resource, @NonNull List<invoiceshowclass> objects) {
        super(context, resource, objects);
        mcontext = context;
        mres = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String name = getItem(position).getName();
        String cname = getItem(position).getCmpname();
        Integer quant = getItem(position).getQuanity();
        Double prc = getItem(position).getPrice();
        Double dis = getItem(position).getDis();
        Double total = getItem(position).getTotal();
        invoiceshowclass invoicelayout = new invoiceshowclass(name,cname,quant,prc,dis,total);
        final View result;
        Viewholder viewholder;
        if(convertView == null)
        {
            LayoutInflater inflater = LayoutInflater.from(mcontext);
            convertView = inflater.inflate(mres,parent,false);
            viewholder = new Viewholder();
            viewholder.bookname = convertView.findViewById(R.id.invvlbname);
            viewholder.bookcname = convertView.findViewById(R.id.invvlcname);
            viewholder.bookquan = convertView.findViewById(R.id.invvlbquan);
            viewholder.bookprice = convertView.findViewById(R.id.invvlbprice);
            viewholder.bookdis = convertView.findViewById(R.id.invvlbdis);
            viewholder.booktotal = convertView.findViewById(R.id.invvlbtotal);
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
        viewholder.bookname.setText(invoicelayout.getName());
        viewholder.bookcname.setText(invoicelayout.getCmpname());
        viewholder.bookquan.setText(invoicelayout.getQuanity() + " Pcs");
        viewholder.bookprice.setText("Rs "+invoicelayout.getPrice());
        viewholder.bookdis.setText(invoicelayout.getDis()+ " %");
        viewholder.booktotal.setText("Rs "+invoicelayout.getTotal());
        return convertView;
    }
}
