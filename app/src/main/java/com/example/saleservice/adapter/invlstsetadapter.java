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
import com.example.saleservice.classes.invlstsetmapper;

import java.util.List;

public class invlstsetadapter extends ArrayAdapter<invlstsetmapper> {
    private Context mcontext;
    private Integer mres;
    private Integer lastposition = -1;
    static class Viewholder{
        TextView setname;
        TextView nofitem;
        TextView totalpr;
    }
    public invlstsetadapter(@NonNull Context context, int resource, @NonNull List<invlstsetmapper> objects) {
        super(context, resource, objects);
        mcontext = context;
        mres = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String sname = getItem(position).getName();
        String sitem = getItem(position).getItems();
        Double sprice = getItem(position).getTprice();
        invlstsetmapper mapp = new invlstsetmapper(sname,sitem,sprice);
        final View result;
        Viewholder viewholder;
        if(convertView == null)
        {
            LayoutInflater inflater = LayoutInflater.from(mcontext);
            convertView = inflater.inflate(mres,parent,false);
            viewholder = new Viewholder();
            viewholder.setname = convertView.findViewById(R.id.stdsetname);
            viewholder.nofitem = convertView.findViewById(R.id.stdnoofitem);
            viewholder.totalpr = convertView.findViewById(R.id.stdtotalprice);
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
        viewholder.setname.setText(mapp.getName());
        viewholder.nofitem.setText(mapp.getItems());
        viewholder.totalpr.setText("Rs " + mapp.getTprice());
        return convertView;
    }
}
