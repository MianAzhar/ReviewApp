package inv.sfs.com.criticapp;


import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import inv.sfs.com.criticapp.Models.Restaurant;


/**
 * A simple {@link Fragment} subclass.
 */
public class myCriticReviewsUpdated extends Fragment implements View.OnClickListener {


    LinearLayout invite_critics_lay, my_reviews_lay, status_reports_lay;
    int reviewsCount;
    TextView reviews_count_tv, restaurant_name_tv, criticScore_tv;
    RatingBar rating_bar;
    Restaurant currentRestaurant;

    public myCriticReviewsUpdated() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_critic_reviews_updated, container, false);

        restaurant_name_tv = view.findViewById(R.id.restaurant_name);
        criticScore_tv = view.findViewById(R.id.criticScore_tv);
        rating_bar = view.findViewById(R.id.rating_bar1);

        invite_critics_lay = view.findViewById(R.id.invite_critics_lay);
        invite_critics_lay.setOnClickListener(this);

        reviews_count_tv = view.findViewById(R.id.reviews_count_tv);

        my_reviews_lay = view.findViewById(R.id.my_reviews_lay);
        my_reviews_lay.setOnClickListener(this);

        status_reports_lay = view.findViewById(R.id.status_reports_lay);
        status_reports_lay.setOnClickListener(this);

        // stars = (LayerDrawable) rating_bar.getProgressDrawable();
        //stars.getDrawable(2).setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("My Critic Score");

        getRestaurantDetails();

        LayerDrawable stars = (LayerDrawable) rating_bar.getProgressDrawable();
        //stars.getDrawable(2).setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

        ParseQuery<ParseObject> query = new ParseQuery<>("FullReview");
        query.whereEqualTo("restaurantId", ParseUser.getCurrentUser().get("restaurant"));

        query.countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                if(e == null){
                    reviewsCount = count;
                    reviews_count_tv.setText(String.valueOf(reviewsCount));
                } else{
                    Log.d("QueryError", e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    public void getRestaurantDetails(){
        final ParseObject restaurant = ParseUser.getCurrentUser().getParseObject("restaurant");

        restaurant.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                currentRestaurant = new Restaurant();
                currentRestaurant.parseObject = restaurant;
                currentRestaurant.restaurant_name = restaurant.getString("name");

                restaurant_name_tv.setText(currentRestaurant.restaurant_name);

                ParseQuery<ParseObject> parseQuery = new ParseQuery<>("FullReview");
                parseQuery.whereEqualTo("restaurantId", restaurant);
                parseQuery.include("restaurantId");
                parseQuery.include("userId");
                parseQuery.setLimit(1000);

                parseQuery.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        int sum = 0;
                        int starSum = 0;
                        for(int i = 0; i < objects.size(); i++){
                            sum += objects.get(i).getInt("averageRating");
                            if(objects.get(i).getNumber("overall_Rating") != null)
                                starSum += objects.get(i).getNumber("overall_Rating").floatValue();
                        }

                        if(objects.size() == 0){
                            currentRestaurant.avgRating = 0;
                            currentRestaurant.starRating = 0;
                        }else{
                            currentRestaurant.avgRating = sum / objects.size();
                            currentRestaurant.starRating = starSum / objects.size();
                        }
                        currentRestaurant.reviews = objects;

                        criticScore_tv.setText(String.valueOf(currentRestaurant.avgRating));

                        rating_bar.setRating(currentRestaurant.starRating);
                        LayerDrawable stars = (LayerDrawable) rating_bar.getProgressDrawable();
                        stars.getDrawable(2).setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                    }
                });
            }
        });
    }


    @Override
    public void onClick(View view) {

        if(view.getId() == invite_critics_lay.getId()){
            inviteCriticsBadge criticsBadge = new inviteCriticsBadge();
            android.support.v4.app.FragmentTransaction trans1 = getActivity().getSupportFragmentManager().beginTransaction();
            trans1.replace(R.id.frame_container,criticsBadge).addToBackStack(null).commit();
        } else if(view.getId() == my_reviews_lay.getId()) {
            criticScores scores = new criticScores();
            android.support.v4.app.FragmentTransaction trans1 = getActivity().getSupportFragmentManager().beginTransaction();
            trans1.replace(R.id.frame_container,scores).addToBackStack(null).commit();
        } else if(view.getId() == status_reports_lay.getId()){
            //Toast.makeText(getActivity(), "Reports coming soon.", Toast.LENGTH_SHORT).show();

            reports scores = new reports();
            android.support.v4.app.FragmentTransaction trans1 = getActivity().getSupportFragmentManager().beginTransaction();
            trans1.replace(R.id.frame_container,scores).addToBackStack(null).commit();

        }

    }
}
