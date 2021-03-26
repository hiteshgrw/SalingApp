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

import java.util.List;

public class invblladapter extends ArrayAdapter<booklistlayout> {
    private Context mcontext;
    private Integer mres;
    private Integer lastposition = -1;
    static  class Viewholder{
        TextView bookname;
        TextView bookquan;
        TextView bookprice;
    }
    public invblladapter(@NonNull Context context, int resource, @NonNull List<booklistlayout> objects) {
        super(context, resource, objects);
        mcontext = context;
        mres = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String name = getItem(position).getName();
        Integer quant = getItem(position).getQuantity();
        Double prc = getItem(position).getPrice();
        String cname = getItem(position).getCname();
        booklistlayout bll = new booklistlayout(name,cname,prc,quant);
        final View result;
        Viewholder viewholder;
        if(convertView == null)
        {
            LayoutInflater inflater = LayoutInflater.from(mcontext);
            convertView = inflater.inflate(mres,parent,false);
            viewholder = new Viewholder();
            viewholder.bookname = convertView.findViewById(R.id.inblltvbookname);
            viewholder.bookquan = convertView.findViewById(R.id.inblltvbookquan);
            viewholder.bookprice = convertView.findViewById(R.id.inblltvbookprice);
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
        viewholder.bookname.setText(bll.getName());
        viewholder.bookquan.setText(bll.getQuantity() + " Pcs");
        viewholder.bookprice.setText("Rs "+bll.getPrice());
        return convertView;
    }
}
