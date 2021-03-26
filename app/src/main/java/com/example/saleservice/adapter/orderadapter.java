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
import com.example.saleservice.classes.orderlayout;

import java.util.List;

public class orderadapter extends ArrayAdapter<orderlayout> {
    private Context mcontext;
    private Integer mres;
    private Integer lastposition = -1;
    static  class Viewholder{
        TextView bookname;
        TextView bookcmp;
        TextView bookquan;
    }
    public orderadapter(@NonNull Context context, int resource, @NonNull List<orderlayout> objects) {
        super(context, resource, objects);
        mcontext = context;
        mres = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String name = getItem(position).getName();
        String cmp = getItem(position).getCmpname();
        Integer quant = getItem(position).getQuantity();
        orderlayout orderlayout1 = new orderlayout(name,cmp,quant);
        final View result;
        Viewholder viewholder;
        if(convertView == null)
        {
            LayoutInflater inflater = LayoutInflater.from(mcontext);
            convertView = inflater.inflate(mres,parent,false);
            viewholder = new Viewholder();
            viewholder.bookname = convertView.findViewById(R.id.olbookname);
            viewholder.bookcmp = convertView.findViewById(R.id.olcmpname);
            viewholder.bookquan = convertView.findViewById(R.id.olquant);
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
        viewholder.bookname.setText(orderlayout1.getName());
        viewholder.bookcmp.setText(orderlayout1.getCmpname());
        viewholder.bookquan.setText(orderlayout1.getQuantity() + " Pcs");
        return convertView;
    }
}
