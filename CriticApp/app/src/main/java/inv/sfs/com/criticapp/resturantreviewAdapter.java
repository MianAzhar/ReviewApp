package inv.sfs.com.criticapp;

import android.app.Activity;
import android.app.FragmentTransaction;
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
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.util.ArrayList;

import inv.sfs.com.criticapp.Models.Restaurant;

/**
 * Created by iosdev-1 on 8/7/17.
 */

public class resturantreviewAdapter  extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> name_;
    private final ArrayList<Float> rating_value_;
    public final ArrayList<String> address_;
    public final Integer position_;
    public final Restaurant currentRestaurant;

    public resturantreviewAdapter(Activity context, ArrayList<String> name ,ArrayList<Float> rating_value,ArrayList<String> address, int position ) {
        super(context, R.layout.restaurantsreviewlayout, name);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.name_=name;
        this.rating_value_=rating_value;
        this.address_ = address;
        this.position_ = position;
        currentRestaurant = StorageHelper.restaurants_generic_list.get(position_);
    }

    @Override
    public int getCount() {
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

            be_a_critic_lay.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    PrefrencesHelper  preference = PrefrencesHelper.getInstance(getContext());
                    if(preference.getBoolObject("user_logged_in")){

                        Bundle bundle = new Bundle();
                        bundle.putString("position" , position_.toString());
                         addReviewfrag addreview = new addReviewfrag();
                        addreview.setArguments(bundle);
                         android.support.v4.app.FragmentTransaction ft = ((AppCompatActivity) getContext()).getSupportFragmentManager()
                                .beginTransaction();
                        ft.replace(R.id.frame_container,addreview).addToBackStack(null).commit();
                    }else{
                        Toast.makeText(getContext(), "Please Login", Toast.LENGTH_SHORT).show();
                    }
                }
            });

         }else{
           rowView=inflater.inflate(R.layout.restaurantsreviewlayout, null,true);
            TextView name = (TextView) rowView.findViewById(R.id.name);
            RatingBar rating_bar = (RatingBar) rowView.findViewById(R.id.rating_bar);
            TextView comments = (TextView) rowView.findViewById(R.id.comments);
            TextView score = (TextView)rowView.findViewById(R.id.userScore);
            try {
                ParseObject review = currentRestaurant.reviews.get(position - 1);
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
