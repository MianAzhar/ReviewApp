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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
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
    ProgressBar progress;
    Dialog dialog;
    Button start_date, end_date;
    Integer position;
    public List<ParseObject> fullreviews_ParseObjects;
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
        fullreviews_ParseObjects = StorageHelper.restaurants_generic_list.get(position).reviews;

        //Getting Over all Ratings..
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


        start_date = (Button) dialog.findViewById(R.id.start_date);
        end_date = (Button) dialog.findViewById(R.id.end_date);
        start_date.setOnClickListener(this);
        end_date.setOnClickListener(this);
        //progress = (ProgressBar) getView().findViewById(R.id.progress);
        //be_a_critic_lay = (LinearLayout) getView().findViewById(R.id.be_a_critic_lay);
        //be_a_critic_lay.setOnClickListener(this);
        reviews_lv = (ListView)  getView().findViewById(R.id.reviews);
       // container_layout = (LinearLayout) getView().findViewById(R.id.container_layout);
       // container_layout.getBackground().setAlpha(80);

        //progress.setScaleY(3f);

        //rating_bar = (RatingBar) getView().findViewById(R.id.rating_bar);
        //rating_bar.setRating((float) 3.5);
        reviews_lv = (ListView) getView().findViewById(R.id.reviews);
        resturantreviewAdapter adapter = new resturantreviewAdapter(getActivity(), restaurant_name, rating_value, comments, position, ratingsCount);
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
       /* if(v.getId() == be_a_critic_lay.getId()){
            if(preference.getBoolObject("user_logged_in")){
                addReviewfrag addreview = new addReviewfrag();
                android.support.v4.app.FragmentTransaction trans1 = getActivity().getSupportFragmentManager().beginTransaction();
                trans1.replace(R.id.frame_container,addreview).addToBackStack(null).commit();
            }else{
                Toast.makeText(getActivity(), "Please Login To Add Review", Toast.LENGTH_SHORT).show();
            }
        }else*/if(v.getId() == start_date.getId()){
            Calendar mcurrentTime = Calendar.getInstance();
            int year = mcurrentTime.get(Calendar.YEAR);
            int month = mcurrentTime.get(Calendar.MONTH);
            int date = mcurrentTime.get(Calendar.DATE);
            DatePickerDialog mdiDialog =new DatePickerDialog(getActivity(),new DatePickerDialog.OnDateSetListener(){
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    //Toast.makeText(getActivity(),year+ " "+monthOfYear+" "+dayOfMonth, Toast.LENGTH_LONG).show();
                    start_date.setText(year+"/" +monthOfYear+"/"+dayOfMonth);
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
                    //Toast.makeText(getActivity(),year+ " "+monthOfYear+" "+dayOfMonth, Toast.LENGTH_LONG).show();
                    end_date.setText(year+"/" +monthOfYear+"/"+dayOfMonth);
                }
            }, year, month, date);
            mdiDialog.show();
        }
    }
}
