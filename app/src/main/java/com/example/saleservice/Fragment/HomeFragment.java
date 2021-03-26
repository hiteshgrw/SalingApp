package com.example.saleservice.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CpuUsageInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.saleservice.R;
import com.example.saleservice.classes.barclass;
import com.example.saleservice.classes.netstockclass;
import com.example.saleservice.helper.Companymgmthelper;
import com.example.saleservice.helper.bookdatabasehelper;
import com.example.saleservice.helper.invoicenomap;
import com.example.saleservice.helper.mainuserinvoicenomap;
import com.example.saleservice.helper.schoolcompanymap;
import com.example.saleservice.helper.schoolmgmthelper;
import com.example.saleservice.helper.tempstock;
import com.example.saleservice.helper.userbdbhelper;
import com.example.saleservice.helper.userhelper;
import com.example.saleservice.stockschoolviewer;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private userhelper userhelp;
    private mainuserinvoicenomap invnohelp;
    private invoicenomap invhelp;
    private schoolmgmthelper schoolhelp;
    private schoolcompanymap companyhelp;
    private Companymgmthelper companymgmthelper;
    private tempstock temphelper;
    private bookdatabasehelper bdbdhelper;
    private userbdbhelper userstockhelper;
    private Integer schid,usname,cpid;
    private BarChart barChart;
    private ArrayList<BarEntry> barentrylist;
    private ArrayList<String> labellist;
    private Double purvalue,salevalue;
    private List<barclass> barclassList;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.navhomepage,container,false);
        PieChart pieChart = view.findViewById(R.id.piechart);
        barChart = view.findViewById(R.id.barchart);
        List<PieEntry> list = new ArrayList<>();
        barentrylist = new ArrayList<>();
        labellist = new ArrayList<>();
        barclassList = new ArrayList<>();
        Integer counter = 0;
        userhelp = new userhelper(container.getContext());
        schoolhelp = new schoolmgmthelper(container.getContext());
        try {
            Cursor cursor1 = schoolhelp.display();
            if(cursor1.getCount()>0)
            {
                while (cursor1.moveToNext())
                {
                    invhelp = new invoicenomap(container.getContext(),"School"+cursor1.getInt(0));
                    counter = counter + invhelp.display().getCount();
                }
            }
        }catch (Exception e)
        {
//            Toast.makeText(container.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
        list.clear();
        list.add(new PieEntry(counter,"Admin"));
        try {
            Cursor cursor = userhelp.display();
            if(cursor.getCount()>0)
            {
                while (cursor.moveToNext())
                {
                    Log.e("Tagg",cursor.getString(1));

                    try {
                        if(!cursor.getString(1).equals("admin"))
                        {
                            invnohelp = new mainuserinvoicenomap(container.getContext(),"School"+cursor.getInt(4),cursor.getInt(0));
                            list.add(new PieEntry(invnohelp.display().getCount(),cursor.getString(1)));
                        }
                    } catch (Exception e) {
//                        Toast.makeText(container.getContext(),cursor.getString(1) + "Not found" , Toast.LENGTH_SHORT).show();
                    }
                }
            }
            PieDataSet dataSet = new PieDataSet(list,"");
            dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
            PieData data = new PieData(dataSet);
            data.setValueTextSize(16f);
            pieChart.getDescription().setEnabled(false);
            pieChart.setHoleRadius(70f);
            pieChart.setCenterText("User Wise Invoice Dist.");
            pieChart.animateY(2000);
            pieChart.setData(data);
            pieChart.invalidate();
        } catch (Exception e) {
            Toast.makeText(container.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        try {
            barentrylist.clear();
            labellist.clear();
            barclassList.clear();
            Integer count = 0;
            Cursor cursor1 = schoolhelp.display();
            if(cursor1.getCount()>0)
            {
                while (cursor1.moveToNext())
                {
                    schid = cursor1.getInt(0);
                    labellist.add(cursor1.getString(1)+" Purchase");
                    labellist.add(cursor1.getString(1)+" Sale");
                    container.getContext().deleteDatabase("booktempdata.db");
                    databasecreator(container);
                    purvalue = 0.0;
                    salevalue = 0.0;
                    companymgmthelper = new Companymgmthelper(container.getContext());
                    companyhelp = new schoolcompanymap(container.getContext(),"School"+schid);
                    List<String> cmplist = new ArrayList<>();
                    cmplist.clear();
                    cmplist = companyhelp.display();
                    for (int i = 0; i < cmplist.size() ; i++) {
                        cpid = companymgmthelper.getid(cmplist.get(i));
                        temphelper = new tempstock(container.getContext(), "Company" + cpid);
                        Cursor tempcursor = temphelper.display();
                        if (tempcursor.getCount() > 0) {
                            while (tempcursor.moveToNext()) {
                                purvalue = purvalue + tempcursor.getDouble(1)*tempcursor.getInt(2);
                                salevalue = salevalue + tempcursor.getDouble(1)*tempcursor.getInt(3);
                            }
                        }
                    }
                    barentrylist.add(new BarEntry(count,purvalue.intValue()));
                    barclassList.add(new barclass(cursor1.getString(1),purvalue,salevalue));
                    barclassList.add(new barclass(cursor1.getString(1),purvalue,salevalue));
                    count++;
                    barentrylist.add(new BarEntry(count,salevalue.intValue()));
                    count++;
                }
                BarDataSet barDataSet = new BarDataSet(barentrylist,"School Sales");
                barDataSet.setValueTextSize(10f);
                barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                Description description = new Description();
                description.setText("Schools");
                barChart.setDescription(description);
                BarData barData = new BarData(barDataSet);
                barChart.setData(barData);
                XAxis xAxis = barChart.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(labellist));
                xAxis.setTextSize(12f);
                xAxis.setPosition(XAxis.XAxisPosition.TOP);
                xAxis.setDrawGridLines(false);
                xAxis.setDrawAxisLine(false);
                xAxis.setGranularity(1f);
                xAxis.setLabelCount(labellist.size());
                xAxis.setLabelRotationAngle(270);
                barChart.animateY(2000);
                barChart.invalidate();
                barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                    @Override
                    public void onValueSelected(Entry e, Highlight h) {
                        Integer index = barChart.getBarData().getDataSetForEntry(e).getEntryIndex((BarEntry)e);
                        AlertDialog.Builder builder = new AlertDialog.Builder(container.getContext());
                        builder.setCancelable(true);
                        View mview = LayoutInflater.from(container.getContext()).inflate(R.layout.barclicker,null);
                        TextView slname = mview.findViewById(R.id.bcschname);
                        TextView pur = mview.findViewById(R.id.bcnetpuramt);
                        TextView sal = mview.findViewById(R.id.bcnetsaleamt);
                        TextView profit = mview.findViewById(R.id.bcnetprofitamt);
                        slname.setText(barclassList.get(index).getName());
                        pur.setText("Rs "+barclassList.get(index).getPr());
                        sal.setText("Rs "+barclassList.get(index).getSale());
                        profit.setText("Rs "+(barclassList.get(index).getPr() - barclassList.get(index).getSale()));
                        builder.setView(mview);
                        AlertDialog alertDialog = builder.create();
                        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alertDialog.show();
                    }

                    @Override
                    public void onNothingSelected() {

                    }
                });
            }
            else {
                Toast.makeText(container.getContext(), "No schools", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            Toast.makeText(container.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return view;
    }
    public void databasecreator(@Nullable ViewGroup container)
    {
        adminsetupdata(container);
        userhelp = new userhelper(container.getContext());
        try {
            Cursor usrcursor = userhelp.display();
            if(usrcursor.getCount()>0)
            {
                while (usrcursor.moveToNext())
                {
                    if(usrcursor.getInt(4)==schid && !usrcursor.getString(1).equals("admin"))
                    {
                        usname = usrcursor.getInt(0);
                        companyhelp = new schoolcompanymap(container.getContext(),"School"+schid);
                        List<String> cmplist = new ArrayList<>();
                        cmplist.clear();
                        cmplist = companyhelp.display();
                        for (int i = 0; i <cmplist.size() ; i++) {
                            cpid = companymgmthelper.getid(cmplist.get(i));
                            try {
                                userstockhelper = new userbdbhelper(container.getContext(),"School" + schid, "Company" + cpid + "bookdet",usname);
                                Cursor bookcursor = userstockhelper.display();
                                if(bookcursor.getCount()>0)
                                {
                                    while (bookcursor.moveToNext())
                                    {
                                        temphelper = new tempstock(container.getContext(),"Company"+cpid);
                                        try {
                                            boolean res = temphelper.insert(bookcursor.getString(0),bookcursor.getDouble(1),bookcursor.getInt(2),bookcursor.getInt(3));
                                            if(!res)
                                            {
                                                Integer oldpur = temphelper.getpurstock(bookcursor.getString(0));
                                                Integer oldsale = temphelper.getsalestock(bookcursor.getString(0));
                                                temphelper.update(bookcursor.getString(0),oldpur,oldsale+bookcursor.getInt(3),bookcursor.getDouble(1));
                                            }
                                        } catch (Exception e) {
                                            Integer oldpur = temphelper.getpurstock(bookcursor.getString(0));
                                            Integer oldsale = temphelper.getsalestock(bookcursor.getString(0));
                                            temphelper.update(bookcursor.getString(0),oldpur,oldsale+bookcursor.getInt(3),bookcursor.getDouble(1));
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                Toast.makeText(container.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            Toast.makeText(container.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void adminsetupdata(@Nullable ViewGroup container)
    {
        usname = 1;
        companymgmthelper = new Companymgmthelper(container.getContext());
        try
        {
            companyhelp = new schoolcompanymap(container.getContext(),"School"+schid);
//            Toast.makeText(container.getContext(), "school"+schid, Toast.LENGTH_SHORT).show();
            List<String> cmplist = new ArrayList<>();
            cmplist.clear();
            cmplist = companyhelp.display();
            for (int i = 0; i <cmplist.size() ; i++) {
                cpid = companymgmthelper.getid(cmplist.get(i));
                try {
                    bdbdhelper = new bookdatabasehelper(container.getContext(), "School" + schid, "Company" + cpid + "bookdet");
                    Cursor bookcursor = bdbdhelper.display();
                    if(bookcursor.getCount()>0)
                    {
                        while (bookcursor.moveToNext())
                        {
                            temphelper = new tempstock(container.getContext(),"Company"+cpid);
//                             Toast.makeText(container.getContext(), bookcursor.getString(0)+"entry", Toast.LENGTH_SHORT).show();
                            boolean res = temphelper.insert(bookcursor.getString(0),bookcursor.getDouble(1),bookcursor.getInt(2),bookcursor.getInt(3));
//                             Toast.makeText(container.getContext(), bookcursor.getString(0) + res, Toast.LENGTH_SHORT).show();
                            if(!res)
                            {
                                Integer oldpur = temphelper.getpurstock(bookcursor.getString(0));
                                Integer oldsale = temphelper.getsalestock(bookcursor.getString(0));
//                                 Toast.makeText(container.getContext(), bookcursor.getString(0) + oldpur + "item" , Toast.LENGTH_SHORT).show();
                                temphelper.update(bookcursor.getString(0),oldpur+bookcursor.getInt(2),oldsale+bookcursor.getInt(3),bookcursor.getDouble(1));
                            }
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(container.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
        catch (Exception e)
        {
            Toast.makeText(container.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
