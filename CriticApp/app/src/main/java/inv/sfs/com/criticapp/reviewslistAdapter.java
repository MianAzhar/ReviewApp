package inv.sfs.com.criticapp;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

import inv.sfs.com.criticapp.Models.Restaurant;

/**
 * Created by iosdev-1 on 8/7/17.
 */

public class reviewslistAdapter  extends ArrayAdapter<Restaurant>{

    private final Activity context;
    private final ArrayList<Restaurant> restaurants_list_;

    public reviewslistAdapter(Activity context, ArrayList<Restaurant> restaurants_list) {
        super(context, R.layout.reviewslistlayout, restaurants_list);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.restaurants_list_=restaurants_list;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.reviewslistlayout, null,true);

        TextView avgRating = (TextView)rowView.findViewById(R.id.avgRatingTV);
        TextView name = (TextView) rowView.findViewById(R.id.name);
        RatingBar rating_bar = (RatingBar) rowView.findViewById(R.id.rating_bar);
        TextView reviews_count = (TextView) rowView.findViewById(R.id.reviews_count);
        TextView address = (TextView) rowView.findViewById(R.id.address);

        try {
            avgRating.setText(String.valueOf(restaurants_list_.get(position).avgRating));
            name.setText(restaurants_list_.get(position).restaurant_name);
            //float star = ((float)restaurants_list_.get(position).avgRating) / 90 * 5;
            rating_bar.setRating(restaurants_list_.get(position).starRating);
            reviews_count.setText(String.valueOf(restaurants_list_.get(position).reviews.size()));
            if(restaurants_list_.get(position).vicinity.length() >50){
                restaurants_list_.get(position).vicinity = restaurants_list_.get(position).vicinity.substring(0,50);
                restaurants_list_.get(position).vicinity = restaurants_list_.get(position).vicinity+"...";
            }
            address.setText(restaurants_list_.get(position).vicinity);
        }catch (Exception e){
        }

        return rowView;
    }
}

