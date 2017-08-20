package inv.sfs.com.criticapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Toast;

import java.util.ArrayList;


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

    public reviewDetailsfrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_review_detailsfrag, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("Critic Scores");

        preference = PrefrencesHelper.getInstance(getActivity());
        for(int i =0; i < 7 ; i++){
            restaurant_name.add("Reviewer Name");
            rating_value.add((float) 3.5);
            comments.add("Good Taste a very nice place to be enjoyed it..");
        }

        progress = (ProgressBar) getView().findViewById(R.id.progress);
        be_a_critic_lay = (LinearLayout) getView().findViewById(R.id.be_a_critic_lay);
        be_a_critic_lay.setOnClickListener(this);
        reviews_lv = (ListView)  getView().findViewById(R.id.reviews);
        container_layout = (LinearLayout) getView().findViewById(R.id.container_layout);
        container_layout.getBackground().setAlpha(80);

        //progress.setScaleY(3f);
        rating_bar = (RatingBar) getView().findViewById(R.id.rating_bar);
        rating_bar.setRating((float) 3.5);
        reviews_lv = (ListView) getView().findViewById(R.id.reviews);
        resturantreviewAdapter adapter = new resturantreviewAdapter(getActivity(), restaurant_name, rating_value, comments);
        reviews_lv.setAdapter(adapter);
        reviews_lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id){
                // TODO Auto-generated method stub
                String pos = String.valueOf(position);
                //Intent i = new Intent(getApplicationContext() , reviewdetails.class);
                //startActivity(i);
            }
        });

    }

    @Override
    public void onClick(View v){
        if(v.getId() == be_a_critic_lay.getId()){
            if(preference.getBoolObject("user_logged_in")){
                addReviewfrag addreview = new addReviewfrag();
                android.support.v4.app.FragmentTransaction trans1 = getActivity().getSupportFragmentManager().beginTransaction();
                trans1.replace(R.id.frame_container,addreview).addToBackStack(null).commit();
            }else{
                Toast.makeText(getActivity(), "Please Login", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
