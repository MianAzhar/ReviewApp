package inv.sfs.com.criticapp;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import inv.sfs.com.criticapp.Models.FullReviewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class reviewDetailsfrag extends Fragment implements View.OnClickListener {

    ListView reviews_lv;
    public ArrayList<String> restaurant_name =new ArrayList<String>();
    public ArrayList<Float> rating_value=new ArrayList<Float>();
    public ArrayList<String> comments =new ArrayList<String>();
    LinearLayout container_layout, be_a_critic_lay;
    RatingBar rating_bar;
    PrefrencesHelper preference;
    SeekBar seekBar;
    Dialog dialog;
    Button start_date, end_date, filter_btn;
    Integer position;
    Date startDate, endDate;
    int ratingFiler = -1;
    public ArrayList<ParseObject> fullreviews_ParseObjects;
    //public List<Integer> ratingsCount = Arrays.asList(0, 0, 0, 0, 0);
    public List<Integer> ratingsCount = new ArrayList<>();

    public reviewDetailsfrag(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_review_detailsfrag, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("Critic Reviews");

        ratingsCount.add(0);
        ratingsCount.add(0);
        ratingsCount.add(0);
        ratingsCount.add(0);
        ratingsCount.add(0);
        preference = PrefrencesHelper.getInstance(getActivity());
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.filterdialogue);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        position = Integer.valueOf(getArguments().getString("position"));
        fullreviews_ParseObjects = (ArrayList<ParseObject>) StorageHelper.restaurants_generic_list.get(position).reviews;

        seekBar = dialog.findViewById(R.id.seekBar);

        start_date = dialog.findViewById(R.id.start_date);
        end_date = dialog.findViewById(R.id.end_date);
        filter_btn = dialog.findViewById(R.id.filter_btn);

        start_date.setOnClickListener(this);
        end_date.setOnClickListener(this);
        filter_btn.setOnClickListener(this);

        reviews_lv = getView().findViewById(R.id.reviews);

        startDate = new Date();
        startDate.setDate(1);
        endDate = new Date();

        for(int i = 0; i < fullreviews_ParseObjects.size(); i++ ){

            if(fullreviews_ParseObjects.get(i).get("overall_Rating") != null){
                int overall_rating_temp = (int) fullreviews_ParseObjects.get(i).get("overall_Rating");

                if(overall_rating_temp == 1){
                    ratingsCount.add(0 , ratingsCount.get(0) + 1);
                }else if(overall_rating_temp == 2){
                    ratingsCount.add(1 , ratingsCount.get(1) + 1);

                }else if(overall_rating_temp == 3){
                    ratingsCount.add(2 , ratingsCount.get(2) + 1);

                }else if(overall_rating_temp == 4){
                    ratingsCount.add(3 , ratingsCount.get(3) + 1);

                }else if(overall_rating_temp == 5){
                    ratingsCount.add(4 , ratingsCount.get(4) + 1);
                }
            }
        }

        getRatingCount();
    }

    public void getRatingCount(){
        fullreviews_ParseObjects = new ArrayList<>();

        if(ratingFiler == -1){
            fullreviews_ParseObjects = (ArrayList<ParseObject>) StorageHelper.restaurants_generic_list.get(position).reviews;
        } else {
            for(int i = 0; i < StorageHelper.restaurants_generic_list.get(position).reviews.size(); i++){
                ParseObject temp = StorageHelper.restaurants_generic_list.get(position).reviews.get(i);

                if(temp.getCreatedAt().after(startDate) && temp.getCreatedAt().before(endDate) && temp.getInt("overall_Rating") == ratingFiler ){
                    fullreviews_ParseObjects.add(temp);
                }
            }
        }

        populateAdapter();
    }

    public void populateAdapter(){
        resturantreviewAdapter adapter = new resturantreviewAdapter(getActivity(), restaurant_name, rating_value, fullreviews_ParseObjects, position, ratingsCount);
        reviews_lv.setAdapter(adapter);
        reviews_lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int pos, long id){
                if(pos == 0){
                    return;
                }else{
                    // TODO Auto-generated method stub
                    String pos1 = String.valueOf(position);
                    StorageHelper.uiBlock = true;
                    StorageHelper.isNewReview = false;
                    Bundle bundle = new Bundle();

                    ParseObject tempFullReview = StorageHelper.restaurants_generic_list.get(position).reviews.get(pos - 1);
                    if(ParseUser.getCurrentUser()  != null){
                        ParseUser temp = tempFullReview.getParseUser("userId");
                        if(ParseUser.getCurrentUser().getObjectId().equals(temp.getObjectId())){
                            StorageHelper.shareReview = true;
                        }else{
                            StorageHelper.shareReview = false;
                        }
                    }else{
                        StorageHelper.shareReview = false;
                    }

                    bundle.putString("reataurant_name_st" , StorageHelper.restaurants_generic_list.get(position).restaurant_name);
                    bundle.putString("total_rating_st" , tempFullReview.get("averageRating").toString());
                    if(tempFullReview.getNumber("overall_Rating") != null)
                        bundle.putFloat("total_rating_stars_float" ,  tempFullReview.getNumber("overall_Rating").floatValue());
                    bundle.putParcelable("fullReview" , tempFullReview);
                    addReviewfrag addreview = new addReviewfrag();
                    addreview.setArguments(bundle);
                    android.support.v4.app.FragmentTransaction trans1 = ((AppCompatActivity) getContext()).getSupportFragmentManager()
                            .beginTransaction();
                    trans1.replace(R.id.frame_container,addreview).addToBackStack(null).commit();
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
                /*allReviewsfrag allreviews = new allReviewsfrag();
                android.support.v4.app.FragmentTransaction trans1 = getActivity().getSupportFragmentManager().beginTransaction();
                trans1.replace(R.id.frame_container,allreviews).addToBackStack(null).commit();*/

                return  true;
        }
        return super.onOptionsItemSelected(item);
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

            ratingFiler = seekBar.getProgress();

            getRatingCount();
        }
    }
}
