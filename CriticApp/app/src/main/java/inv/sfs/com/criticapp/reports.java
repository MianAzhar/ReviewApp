package inv.sfs.com.criticapp;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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
import java.util.Date;
import java.util.List;

import inv.sfs.com.criticapp.Models.FullReviewModel;
import inv.sfs.com.criticapp.Models.Rating;


/**
 * A simple {@link Fragment} subclass.
 */
public class reports extends Fragment implements OnChartValueSelectedListener, View.OnClickListener{


    TransparentProgressDialog pd;
    private PieChart mChart;
    double subTotal = 0;
    public Rating local_rating;
    public ArrayList<Rating> rating_values_lis = new ArrayList<Rating>();
    public ArrayList<Double> percentages = new ArrayList<>();
    public ArrayList<Double> ratedValue = new ArrayList<>();
    public ArrayList<String> review_against =new ArrayList<>();
    float total=0;

    Dialog dialog;
    Button start_date, end_date, filter_btn;
    LinearLayout ratingbar_layout;
    Date startDate, endDate;
    TextView rating_tv;

    public ListView listView;
    public ReportsAdapter reportsAdapter;

    public reports(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        // Inflate the layout for this fragment

        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_reports, container, false);

        listView = view.findViewById(R.id.reports_lv);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("Reports");

        startDate = new Date();
        startDate.setDate(1);
        endDate = new Date();

        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.filterdialogue);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        ratingbar_layout = dialog.findViewById(R.id.ratingbar_layout);
        ratingbar_layout.setVisibility(View.GONE);

        rating_tv = dialog.findViewById(R.id.rating_tv);
        rating_tv.setVisibility(View.GONE);

        start_date = dialog.findViewById(R.id.start_date);
        end_date = dialog.findViewById(R.id.end_date);
        filter_btn = dialog.findViewById(R.id.filter_btn);
        filter_btn.setOnClickListener(this);
        start_date.setOnClickListener(this);
        end_date.setOnClickListener(this);

        review_against.clear();

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
        //mChart = (PieChart) getView().findViewById(R.id.chart1);


        getRating();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v){
        if(v.getId() == start_date.getId()){
            Calendar mcurrentTime = Calendar.getInstance();
            int year = mcurrentTime.get(Calendar.YEAR);
            int month = mcurrentTime.get(Calendar.MONTH);
            int date = mcurrentTime.get(Calendar.DATE);
            DatePickerDialog mdiDialog =new DatePickerDialog(getActivity(),new DatePickerDialog.OnDateSetListener(){
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    start_date.setText(year+"/" + (monthOfYear + 1) +"/"+dayOfMonth);
                    startDate.setDate(dayOfMonth);
                    startDate.setMonth(monthOfYear);
                    startDate.setYear(year - 1900);
                }
            }, year, month, date);
            mdiDialog.show();
        }else if(v.getId() == end_date.getId()){
            Calendar mcurrentTime = Calendar.getInstance();
            int year = mcurrentTime.get(Calendar.YEAR);
            int month = mcurrentTime.get(Calendar.MONTH);
            int date = mcurrentTime.get(Calendar.DATE);
            DatePickerDialog mdiDialog =new DatePickerDialog(getActivity(),new DatePickerDialog.OnDateSetListener(){
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    end_date.setText(year+"/" + (monthOfYear + 1) +"/"+dayOfMonth);
                    endDate.setDate(dayOfMonth);
                    endDate.setMonth(monthOfYear);
                    endDate.setYear(year - 1900);
                }
            }, year, month, date);
            mdiDialog.show();
        } else if(v.getId() == filter_btn.getId()){
            dialog.hide();
            getRating();
        }
    }

    public void getRating(){

        pd.show();
        ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Rating");
        parseQuery.whereEqualTo("restaurantId", ParseUser.getCurrentUser().get("restaurant"));

        parseQuery.whereGreaterThanOrEqualTo("createdAt", startDate);
        parseQuery.whereLessThanOrEqualTo("createdAt", endDate);

        parseQuery.include("userId");
        parseQuery.setLimit(1000);

        rating_values_lis.clear();

        parseQuery.findInBackground(new FindCallback<ParseObject>(){
            @Override
            public void done(List<ParseObject> list, ParseException e){
                if (e == null){
                    //if (!list.isEmpty()){
                        for (int i = 0; i < list.size(); i++){
                            //subTotal = list.size() * 5;
                            ParseObject parseObject = list.get(i);
                            local_rating = new Rating();
                            local_rating.parseObject = parseObject;
                            local_rating.title = parseObject.getString("title");
                            local_rating.rated_value = (int) parseObject.get("rated_value");
                            rating_values_lis.add(local_rating);
                        }

                        StorageHelper.ratingArrayList = rating_values_lis;

                        pd.dismiss();
                        getaverageRating();
                        //calculations();
                        //populateAdapter();
                    /*
                    } else {
                        Log.d("Result", "Empty");
                        pd.dismiss();
                    }*/
                } else {
                    Log.d("Result", "Wrong");
                    pd.dismiss();
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){

        menu.clear();
        inflater.inflate(R.menu.main, menu);
        menu.findItem(R.id.filter).setVisible(true);
        getActivity().invalidateOptionsMenu();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){

            case android.R.id.home:
                getActivity().finish();
                break;
            case R.id.filter:
                dialog.show();

                return  true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getaverageRating(){

        pd.show();
        ParseQuery<ParseObject> parseQuery = new ParseQuery<>("FullReview");
        parseQuery.whereEqualTo("restaurantId", ParseUser.getCurrentUser().get("restaurant"));

        parseQuery.whereGreaterThanOrEqualTo("createdAt", startDate);
        parseQuery.whereLessThanOrEqualTo("createdAt", endDate);

        parseQuery.setLimit(1000);

        parseQuery.findInBackground(new FindCallback<ParseObject>(){
            @Override
            public void done(List<ParseObject> list, ParseException e){
                if (e == null){
                    //if (!list.isEmpty()){
                        int average_total = 0;
                        for (int i = 0; i < list.size(); i++){
                            ParseObject parseObject = list.get(i);
                            average_total += (int) parseObject.get("averageRating");
                        }
                        pd.dismiss();
                        subTotal = average_total;
                        if(list.size() > 0)
                            total = (float) average_total / (float) list.size();
                        else
                            total = 0;
                        calculations();
                        /*
                    } else {
                        Log.d("Result", "Empty");
                        pd.dismiss();
                    }*/
                } else {
                    Log.d("Result", "Wrong");
                    pd.dismiss();
                }
            }
        });
    }

    public void calculations(){
        percentages.clear();
        for(int a = 0; a < 18; a++ ){
            double tempRtaing = 0;
            double percentage = 0;
            int count = 0;
            for (int b = 0; b < rating_values_lis.size(); b++){
               if(review_against.get(a).equals(rating_values_lis.get(b).title)){
                   tempRtaing = tempRtaing + rating_values_lis.get(b).rated_value;
                   count++;
               }
            }
            ratedValue.add(tempRtaing);

            if(count > 0){
                percentage = tempRtaing / count;
            }

            /*
            if(subTotal > 0)
                percentage = tempRtaing / subTotal * 100;
            else
                percentage = 0f;
            */
            percentages.add(percentage);
        }
        populateAdapter();
    }

    private void populateAdapter(){
        reportsAdapter = new ReportsAdapter(getActivity(), review_against, percentages, total);

        listView.setAdapter(reportsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0)
                    return;

                Bundle args = new Bundle();

                args.putString("title", review_against.get(i - 1));
                args.putDouble("percent", percentages.get(i - 1));
                args.putInt("color", StorageHelper.Colors.get(i - 1));

                ReportsDetailFragment detailFragment = new ReportsDetailFragment();
                detailFragment.setArguments(args);
                android.support.v4.app.FragmentTransaction trans1 = getActivity().getSupportFragmentManager().beginTransaction();
                trans1.replace(R.id.frame_container,detailFragment).addToBackStack(null).commit();
            }
        });
    }

    private void setData(){

        int temp_total = (int) total;
        mChart.setCenterText( String.valueOf(temp_total));
        mChart.setCenterTextSize(70f);
        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        for (int i = 0; i < review_against.size(); i++){
            entries.add(new PieEntry(percentages.get(i).floatValue(),
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

        data.setDrawValues(false);

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
