package inv.sfs.com.criticapp;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import inv.sfs.com.criticapp.Models.FullReviewModel;
import inv.sfs.com.criticapp.Models.Rating;


/**
 * A simple {@link Fragment} subclass.
 */
public class reports extends Fragment implements OnChartValueSelectedListener{


    TransparentProgressDialog pd;
    private PieChart mChart;
    int subTotal = 0;
    public Rating local_rating;
    public ArrayList<Rating> rating_values_lis = new ArrayList<Rating>();
    public ArrayList<Float> percentages = new ArrayList<Float>();
    public ArrayList<Integer> ratedValue = new ArrayList<Integer>();
    public ArrayList<String> review_against =new ArrayList<String>();
    float total=0;

    public reports(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reports, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("Reports");


        // Populating Array....
        review_against.add("Location");
        review_against.add("Main Greeting");
        review_against.add("Seating Wait Time");
        review_against.add("Table Quality");
        review_against.add("Menu Selection");
        review_against.add("Price Range");
        review_against.add("Server Attitude");
        review_against.add("Food Delivery");
        review_against.add("Order Accuracy");
        review_against.add("Food Plating");
        review_against.add("Taste Of Food");
        review_against.add("Noise Level");
        review_against.add("Lighting Level");
        review_against.add("Climate Comfort");
        review_against.add("Refill Service");
        review_against.add("Restroom Quality");
        review_against.add("Bill Service");
        review_against.add("Overall Appeal");


        pd = new TransparentProgressDialog(getContext(), R.drawable.loader);
        mChart = (PieChart) getView().findViewById(R.id.chart1);
        mChart.setUsePercentValues(true);
        mChart.getDescription().setEnabled(false);
        mChart.setExtraOffsets(5, 5, 5, 10);
        mChart.setDragDecelerationFrictionCoef(0.95f);
        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColor(Color.RED);

        mChart.setTransparentCircleColor(Color.RED);
        mChart.setTransparentCircleAlpha(110);
        mChart.setHoleRadius(40f);
        mChart.setTransparentCircleRadius(41f);
        mChart.setDrawCenterText(true);
        mChart.setRotationAngle(0);
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(true);
        mChart.setOnChartValueSelectedListener(this);
        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

        Legend l = mChart.getLegend();
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setWordWrapEnabled(true);
        l.setDrawInside(true);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);


        mChart.setEntryLabelColor(Color.WHITE);
        mChart.setEntryLabelTextSize(8f);

        getRating();
    }


    public void getRating(){

        pd.show();
        ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Rating");
        parseQuery.whereEqualTo("restaurantId", ParseUser.getCurrentUser().get("restaurant"));
        parseQuery.setLimit(1000);

        parseQuery.findInBackground(new FindCallback<ParseObject>(){
            @Override
            public void done(List<ParseObject> list, ParseException e){
                if (e == null){
                    if (!list.isEmpty()){
                        for (int i = 0; i < list.size(); i++){
                            subTotal = list.size() * 5;
                            ParseObject parseObject = list.get(i);
                            local_rating = new Rating();
                            local_rating.title = parseObject.getString("title");
                            local_rating.rated_value = (int) parseObject.get("rated_value");
                            rating_values_lis.add(local_rating);
                        }
                        pd.dismiss();
                        getaverageRating();
                        calculations();
                        //populateAdapter();
                    } else {
                        Log.d("Result", "Empty");
                        pd.dismiss();
                    }
                } else {
                    Log.d("Result", "Wrong");
                    pd.dismiss();
                }
            }
        });
    }


    public void getaverageRating(){

        pd.show();
        ParseQuery<ParseObject> parseQuery = new ParseQuery<>("FullReview");
        parseQuery.whereEqualTo("restaurantId", ParseUser.getCurrentUser().get("restaurant"));
        parseQuery.setLimit(1000);

        parseQuery.findInBackground(new FindCallback<ParseObject>(){
            @Override
            public void done(List<ParseObject> list, ParseException e){
                if (e == null){
                    if (!list.isEmpty()){
                        int average_total = 0;
                        for (int i = 0; i < list.size(); i++){
                            ParseObject parseObject = list.get(i);
                            average_total = (int) parseObject.get("averageRating");
                        }
                        pd.dismiss();
                        total = (float) average_total / (float) list.size();
                        calculations();
                    } else {
                        Log.d("Result", "Empty");
                        pd.dismiss();
                    }
                } else {
                    Log.d("Result", "Wrong");
                    pd.dismiss();
                }
            }
        });
    }

    public void calculations(){
        for(int a = 0; a < 18; a++ ){
            int tempRtaing = 0;
            Float percentage;
            for (int b = 0; b < rating_values_lis.size(); b++){
               if(review_against.get(a).equals(rating_values_lis.get(b).title)){
                   tempRtaing = tempRtaing + rating_values_lis.get(b).rated_value;
               }
            }
            ratedValue.add(tempRtaing);
            percentage = Float.valueOf( (float)tempRtaing / (float) subTotal * 100);
            percentages.add(percentage);
        }
        setData();
    }

    private void setData(){

        int temp_total = (int) total;
        mChart.setCenterText( String.valueOf(temp_total));
        mChart.setCenterTextSize(70f);
        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        for (int i = 0; i < review_against.size(); i++){
            entries.add(new PieEntry(percentages.get(i),
                    review_against.get(i),
                    getResources().getDrawable(R.drawable.star)));
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
        //data.setValueTypeface(mTfLight);
        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);
        mChart.invalidate();
    }

    @Override
    public void onValueSelected(Entry entry, Highlight highlight) {
    }

    @Override
    public void onNothingSelected(){
    }
}
