package inv.sfs.com.criticapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.MPPointF;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import inv.sfs.com.criticapp.Models.FullReviewModel;
import inv.sfs.com.criticapp.Models.Rating;
import inv.sfs.com.criticapp.Models.Restaurant;

/**
 * Created by Mian Azhar on 2/24/2018.
 */

public class ReportsAdapter  extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> titles;
    private final ArrayList<Double> percentages;
    private ArrayList<Rating> ratings;
    private float totalScore;

    TransparentProgressDialog pd;

    //public int averageRating;

    public ReportsAdapter(Activity context, ArrayList<String> titles, ArrayList<Double> percentages, float totalScore) {
        super(context, R.layout.promotion_item, titles);

        this.context=context;
        this.titles = titles;
        this.percentages = percentages;
        this.ratings = new ArrayList<>();
        this.totalScore = totalScore;

        pd = new TransparentProgressDialog(context, R.drawable.loader);
    }

    @Override
    public int getCount(){
        return 19;
    }

    @Override
    public int getItemViewType(int position){
        if(position == 0)
            return 0;
        else
            return 1;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public View getView(final int position, View view, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(position == 0){
            view = inflater.inflate(R.layout.chart_item, parent, false);

            BarChart mChart;
            mChart = view.findViewById(R.id.chart1);

            mChart.setDrawValueAboveBar(true);
            mChart.getDescription().setEnabled(false);

            mChart.setPinchZoom(false);

            XAxis xl = mChart.getXAxis();
            xl.setPosition(XAxis.XAxisPosition.BOTTOM);
            //xl.setTypeface(mTfLight);
            xl.setDrawAxisLine(true);
            xl.setDrawGridLines(false);
            xl.setGranularity(10f);

            YAxis yl = mChart.getAxisLeft();
            //yl.setTypeface(mTfLight);
            yl.setDrawAxisLine(true);
            yl.setDrawGridLines(true);
            yl.setAxisMinimum(0f); // this replaces setStartAtZero(true)
//        yl.setInverted(true);

            YAxis yr = mChart.getAxisRight();
            //yr.setTypeface(mTfLight);
            yr.setDrawAxisLine(true);
            yr.setDrawGridLines(false);
            yr.setAxisMinimum(0f); // this replaces setStartAtZero(true)
//        yr.setInverted(true);

            mChart.setFitBars(true);
            mChart.animateY(1000);


            //mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

            Legend l = mChart.getLegend();
            l.setEnabled(false);

            setData(mChart);

            /*
            PieChart mChart;
            mChart = view.findViewById(R.id.chart1);

            mChart.setUsePercentValues(true);
            mChart.getDescription().setEnabled(false);
            mChart.setExtraOffsets(5, 5, 5, 10);
            mChart.setDragDecelerationFrictionCoef(0.95f);
            mChart.setDrawHoleEnabled(true);
            mChart.setHoleColor(Color.RED);

            mChart.setTransparentCircleColor(Color.RED);
            mChart.setTransparentCircleAlpha(110);
            mChart.setHoleRadius(60f);
            mChart.setTransparentCircleRadius(41f);
            mChart.setDrawCenterText(true);

            //mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

            Legend l = mChart.getLegend();
            l.setEnabled(false);

            mChart.setDrawEntryLabels(false);
            mChart.setRotationEnabled(false);
            mChart.setDrawMarkers(false);

            mChart.setEntryLabelColor(Color.WHITE);
            mChart.setEntryLabelTextSize(0f);
            mChart.setTouchEnabled(false);

            mChart.setCenterText( String.format("%.0f", totalScore));
            mChart.setCenterTextSize(70f);
            mChart.setCenterTextColor(Color.WHITE);

            setData(mChart);
            */

        } else {

            view = inflater.inflate(R.layout.promotion_item, parent, false);

            TextView percentage_tv, title_tv;
            LinearLayout percentageLayout, titleLayout;

            percentageLayout = view.findViewById(R.id.left_layout);
            titleLayout = view.findViewById(R.id.discount_lay);
            percentage_tv = view.findViewById(R.id.discountText_tv);
            title_tv = view.findViewById(R.id.promotionText_tv);

            title_tv.setText(titles.get(position - 1));
            percentage_tv.setText(String.format("%.1f", percentages.get(position - 1)));

            percentageLayout.setBackgroundTintList(ColorStateList.valueOf(StorageHelper.Colors.get(position - 1)));
            titleLayout.setBackgroundTintList(ColorStateList.valueOf(StorageHelper.Colors.get(position - 1)));
        }

        return view;
    }

    /*
    @RequiresApi(api = Build.VERSION_CODES.N)
    public View getView(final int position, View view, ViewGroup parent){
        if(position == 0){
            final ChartVH viewHolder;

            if(view == null){
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                view = inflater.inflate(R.layout.chart_item, parent, false);

                viewHolder = new ChartVH();

                viewHolder.chart = view.findViewById(R.id.chart1);
                viewHolder.position = position;
                view.setTag(viewHolder);

            } else {
                viewHolder = (ChartVH) view.getTag();
            }

            PieChart mChart = viewHolder.chart;

            mChart.setUsePercentValues(true);
            mChart.getDescription().setEnabled(false);
            mChart.setExtraOffsets(5, 5, 5, 10);
            mChart.setDragDecelerationFrictionCoef(0.95f);
            mChart.setDrawHoleEnabled(true);
            mChart.setHoleColor(Color.RED);

            mChart.setTransparentCircleColor(Color.RED);
            mChart.setTransparentCircleAlpha(110);
            mChart.setHoleRadius(60f);
            mChart.setTransparentCircleRadius(41f);
            mChart.setDrawCenterText(true);

            mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

            Legend l = mChart.getLegend();
            l.setEnabled(false);

            mChart.setDrawEntryLabels(false);
            mChart.setRotationEnabled(false);
            mChart.setDrawMarkers(false);

            mChart.setEntryLabelColor(Color.WHITE);
            mChart.setEntryLabelTextSize(0f);
            mChart.setTouchEnabled(false);
            mChart.setCenterText( String.valueOf(totalScore));
            mChart.setCenterTextSize(70f);

            setData(mChart);

        } else {
            final ViewHolder viewHolder;

            if(view == null){
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                view = inflater.inflate(R.layout.promotion_item, parent, false);

                viewHolder = new ViewHolder();

                viewHolder.percentageLayout = view.findViewById(R.id.left_layout);
                viewHolder.titleLayout = view.findViewById(R.id.discount_lay);
                viewHolder.percentage_tv = view.findViewById(R.id.discountText_tv);
                viewHolder.title_tv = view.findViewById(R.id.promotionText_tv);

                viewHolder.position = position;

                view.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            viewHolder.title_tv.setText(titles.get(position - 1));
            viewHolder.percentage_tv.setText(percentages.get(position - 1).toString());

        }

        return view;
    }
    */

    private void setData(BarChart mChart){
        ArrayList<BarEntry> entries = new ArrayList<>();

        float spaceForBar = 5f;

        for (int i = 0; i < titles.size(); i++) {
            entries.add(new BarEntry(i, percentages.get(i).floatValue(),
                    context.getResources().getDrawable(R.drawable.star)));
        }

        mChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(titles));
        mChart.getXAxis().setEnabled(false);

        BarDataSet set1;


        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet)mChart.getData().getDataSetByIndex(0);
            set1.setValues(entries);
            set1.setColors(StorageHelper.Colors);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(entries, "DataSet 1");
            set1.setColors(StorageHelper.Colors);
            set1.setDrawIcons(false);

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(7f);
            //data.setValueTypeface(mTfLight);
            //data.setBarWidth(barWidth);
            mChart.setData(data);
        }
    }

    /*
    private void setData(PieChart mChart){
        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        for (int i = 0; i < titles.size(); i++){
            entries.add(new PieEntry(percentages.get(i).floatValue(),
                    titles.get(i),
                    context.getResources().getDrawable(R.drawable.star)));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Election Results");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(1f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);
        dataSet.setColors(StorageHelper.Colors);
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(8f);
        data.setValueTextColor(Color.WHITE);

        data.setDrawValues(false);

        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);
        mChart.invalidate();
    }
    */

    static class ViewHolder{
        LinearLayout percentageLayout;
        LinearLayout titleLayout;
        TextView percentage_tv;
        TextView title_tv;
        int position;
    }

    static class ChartVH{
        PieChart chart;
        int position;
    }

}
