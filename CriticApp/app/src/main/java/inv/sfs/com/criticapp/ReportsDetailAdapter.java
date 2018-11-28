package inv.sfs.com.criticapp;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.MPPointF;
import com.parse.ParseUser;

import java.util.ArrayList;

import inv.sfs.com.criticapp.Models.Rating;

/**
 * Created by Mian Azhar on 2/24/2018.
 */

public class ReportsDetailAdapter  extends ArrayAdapter<Rating> {

    private final Activity context;
    private final ArrayList<Rating> ratings;
    private final ArrayList<Integer> percentage;
    private double totalScore;
    private final ArrayList<Integer> colors = new ArrayList<>();
    private final String title;

    TransparentProgressDialog pd;

    //public int averageRating;

    public ReportsDetailAdapter(Activity context, ArrayList<Rating> ratings, double totalScore, ArrayList<Integer> percentage, int color, String title) {
        super(context, R.layout.promotion_item, ratings);

        this.context=context;
        this.ratings = ratings;
        this.percentage = percentage;
        this.totalScore = totalScore;
        this.colors.add(color);
        this.title = title;

        pd = new TransparentProgressDialog(context, R.drawable.loader);
    }

    @Override
    public int getCount(){
        return ratings.size() + 2;
    }

    @Override
    public int getItemViewType(int position){
        if(position == 0)
            return 0;
        else if(position == 1)
            return 1;
        else
            return 2;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public View getView(final int position, View view, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(position == 0){
            view = inflater.inflate(R.layout.chart_item, parent, false);

            BarChart barChart = view.findViewById(R.id.chart1);
            barChart.setVisibility(View.GONE);

            PieChart mChart;
            mChart = view.findViewById(R.id.pieChart);
            mChart.setVisibility(View.VISIBLE);
            mChart.setUsePercentValues(false);
            mChart.getDescription().setEnabled(false);
            mChart.setExtraOffsets(5, 5, 5, 10);
            mChart.setDragDecelerationFrictionCoef(0.95f);
            mChart.setDrawHoleEnabled(true);
            //mChart.setHoleColor(Color.RED);

            mChart.setTransparentCircleColor(Color.WHITE);
            mChart.setTransparentCircleAlpha(110);
            mChart.setHoleRadius(50f);
            mChart.setTransparentCircleRadius(60f);
            mChart.setDrawCenterText(true);

            //mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

            Legend l = mChart.getLegend();
            l.setEnabled(false);

            mChart.setDrawEntryLabels(true);
            mChart.setRotationEnabled(false);
            mChart.setDrawMarkers(true);

            mChart.setEntryLabelColor(Color.WHITE);
            mChart.setEntryLabelTextSize(20f);
            mChart.setTouchEnabled(true);

            mChart.setCenterText( String.format("%.1f", totalScore));
            mChart.setCenterTextSize(70f);
            mChart.setCenterTextColor(Color.BLACK);

            setData(mChart);

        } else if(position == 1) {

            view = inflater.inflate(R.layout.promotion_item, parent, false);

            TextView percentage_tv, title_tv;
            LinearLayout percentageLayout, titleLayout;

            percentageLayout = view.findViewById(R.id.left_layout);
            titleLayout = view.findViewById(R.id.discount_lay);
            percentage_tv = view.findViewById(R.id.discountText_tv);
            title_tv = view.findViewById(R.id.promotionText_tv);

            title_tv.setText(title);
            percentage_tv.setText(String.format("%.1f", totalScore));

            percentageLayout.setBackgroundTintList(ColorStateList.valueOf(colors.get(0)));
            titleLayout.setBackgroundTintList(ColorStateList.valueOf(colors.get(0)));
        } else {
            //view = inflater.inflate(R.layout.reports_user_info, parent, false);
            view = inflater.inflate(R.layout.restaurantsreviewlayout, parent, false);


            RatingBar rating_bar = view.findViewById(R.id.rating_bar);
            rating_bar.setVisibility(View.GONE);


            TextView ratedValue_tv = view.findViewById(R.id.userScore);
            TextView username_tv = view.findViewById(R.id.name);
            TextView comment_tv = view.findViewById(R.id.comments);

            Rating rating = ratings.get(position - 2);

            ratedValue_tv.setText(String.valueOf(rating.rated_value));

            if(rating.comment == null){
                rating.comment = "(No comment given by user)";
            }

            if(rating.comment.isEmpty())
                comment_tv.setText("(No comment given by user)");
            else
                comment_tv.setText(rating.comment);

            ParseUser user = rating.parseObject.getParseUser("userId");

            username_tv.setText(user.getString("name"));
        }

        return view;
    }

    private void setData(PieChart mChart){
        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.

        for(int i = 0; i < percentage.size(); i++){
            if(percentage.get(i) > 0)
                entries.add(new PieEntry(percentage.get(i),
                    "" + (i + 1),
                    context.getResources().getDrawable(R.drawable.star)));
        }



        PieDataSet dataSet = new PieDataSet(entries, "Reviews Data");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(5f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        dataSet.setColors(StorageHelper.Colors);
        PieData data = new PieData(dataSet);

        data.setValueTextSize(8f);
        data.setValueTextColor(Color.WHITE);

        data.setDrawValues(false);

        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);
        mChart.invalidate();
    }

}
