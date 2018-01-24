package inv.sfs.com.criticapp;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import inv.sfs.com.criticapp.Models.Restaurant;

/**
 * Created by iosdev-1 on 8/7/17.
 */

public class resturantreviewAdapter  extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> name_;
    private final ArrayList<Float> rating_value_;
    public final ArrayList<String> address_;
    public final List<Integer> ratingsCount_;
    public final Integer position_;
    public final Restaurant currentRestaurant;

    public String reataurant_name_st;
    public String total_rating_st;
    public float total_rating_stars_float;


    public resturantreviewAdapter(Activity context, ArrayList<String> name ,ArrayList<Float> rating_value,ArrayList<String> address, int position, List<Integer> ratingsCount ) {
        super(context, R.layout.restaurantsreviewlayout, name);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.name_=name;
        this.rating_value_=rating_value;
        this.address_ = address;
        this.position_ = position;
        this.ratingsCount_ = ratingsCount;
        currentRestaurant = StorageHelper.restaurants_generic_list.get(position_);
    }

    @Override
    public int getCount(){
        return currentRestaurant.reviews.size() + 1;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView = null;

        LinearLayout be_a_critic_lay = null;
        RatingBar rating_bar_top = null;
        TextView restaurant_name = null;

        if(position == 0){
            rowView=inflater.inflate(R.layout.criticscoretoplayout, null,true);
            be_a_critic_lay = (LinearLayout) rowView.findViewById(R.id.be_a_critic_lay);
            restaurant_name = (TextView) rowView.findViewById(R.id.restaurant_name);
            rating_bar_top = (RatingBar) rowView.findViewById(R.id.rating_bar);

            TextView criticScore = (TextView)rowView.findViewById(R.id.criticScore);

            criticScore.setText(String.valueOf(currentRestaurant.avgRating));
            float star = ((float)currentRestaurant.avgRating) / 90 * 5;
            rating_bar_top.setRating(star);
            restaurant_name.setText(StorageHelper.restaurants_generic_list.get(position_).restaurant_name);
            total_rating_stars_float =  star;
            reataurant_name_st = StorageHelper.restaurants_generic_list.get(position_).restaurant_name;
            total_rating_st = String.valueOf(currentRestaurant.avgRating);

            ProgressBar progress_1 = (ProgressBar)rowView.findViewById(R.id.progress_1);
            ProgressBar progress_2 = (ProgressBar)rowView.findViewById(R.id.progress_2);
            ProgressBar progress_3 = (ProgressBar)rowView.findViewById(R.id.progress_3);
            ProgressBar progress_4 = (ProgressBar)rowView.findViewById(R.id.progress_4);
            ProgressBar progress_5 = (ProgressBar)rowView.findViewById(R.id.progress_5);

            TextView percentage_1 = (TextView)rowView.findViewById(R.id.percentage_1);
            TextView percentage_2 = (TextView)rowView.findViewById(R.id.percentage_2);
            TextView percentage_3 = (TextView)rowView.findViewById(R.id.percentage_3);
            TextView percentage_4 = (TextView)rowView.findViewById(R.id.percentage_4);
            TextView percentage_5 = (TextView)rowView.findViewById(R.id.percentage_5);

            Integer total_reviews = ratingsCount_.get(0) + ratingsCount_.get(1) + ratingsCount_.get(2) + ratingsCount_.get(3)
                    + ratingsCount_.get(4);

            if(total_reviews == 0){
                progress_1.setProgress(0);
                progress_2.setProgress(0);
                progress_3.setProgress(0);
                progress_4.setProgress(0);
                progress_5.setProgress(0);

                percentage_1.setText("0%");
                percentage_2.setText("0%");
                percentage_3.setText("0%");
                percentage_4.setText("0%");
                percentage_5.setText("0%");
            }else{
                float one_rating =  (ratingsCount_.get(0)/ total_reviews) * 100;
                float two_rating =  (ratingsCount_.get(1)/ total_reviews) * 100;
                float three_rating =  (ratingsCount_.get(2)/ total_reviews) * 100;
                float four_rating =  (ratingsCount_.get(3)/ total_reviews) * 100;
                float five_rating =  (ratingsCount_.get(4)/ total_reviews) * 100;


                progress_1.setProgress((int) one_rating);
                progress_2.setProgress((int) two_rating);
                progress_3.setProgress((int) three_rating);
                progress_4.setProgress((int) four_rating);
                progress_5.setProgress((int) five_rating);

                percentage_1.setText(Float.toString(one_rating) + "%");
                percentage_2.setText(Float.toString(two_rating)+ "%");
                percentage_3.setText(Float.toString(three_rating)+ "%");
                percentage_4.setText(Float.toString(four_rating)+ "%");
                percentage_5.setText(Float.toString(five_rating)+ "%");
            }

            be_a_critic_lay.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    PrefrencesHelper  preference = PrefrencesHelper.getInstance(getContext());
                    if(preference.getBoolObject("user_logged_in")){

                        StorageHelper.uiBlock = false;
                        Bundle bundle = new Bundle();
                        bundle.putString("position" , position_.toString());
                        addReviewfrag addreview = new addReviewfrag();
                        addreview.setArguments(bundle);
                        android.support.v4.app.FragmentTransaction ft = ((AppCompatActivity) getContext()).getSupportFragmentManager()
                                .beginTransaction();
                        ft.replace(R.id.frame_container,addreview).addToBackStack(null).commit();
                    }else{
                        Intent i = new Intent(getContext(), login.class);
                        getContext().startActivity(i);
                       //Toast.makeText(getContext(), "Please Login", Toast.LENGTH_SHORT).show();
                    }
                }
            });

         }else{
           rowView=inflater.inflate(R.layout.restaurantsreviewlayout, null,true);
            TextView name = (TextView) rowView.findViewById(R.id.name);
            RatingBar rating_bar = (RatingBar) rowView.findViewById(R.id.rating_bar);
            TextView comments = (TextView) rowView.findViewById(R.id.comments);
            TextView score = (TextView)rowView.findViewById(R.id.userScore);
            LinearLayout review_lay = (LinearLayout) rowView.findViewById(R.id.review_lay);

            try{
                ParseObject review = currentRestaurant.reviews.get(position - 1);
               // review = review.fetch();
                ParseObject userObj = review.getParseObject("userId");
                name.setText(userObj.getString("name"));
                float star = ((float)review.getInt("averageRating")) / 90 * 5;
                rating_bar.setRating(star);
                comments.setText(review.getString("comments"));
                score.setText(String.valueOf(review.getInt("averageRating")));
            }catch (Exception e){
            }
        }


        return rowView;
    }


}
