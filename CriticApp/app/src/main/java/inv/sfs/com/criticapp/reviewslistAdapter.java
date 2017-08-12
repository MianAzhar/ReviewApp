package inv.sfs.com.criticapp;

import android.app.Activity;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by iosdev-1 on 8/7/17.
 */

public class reviewslistAdapter  extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> name_;
    private final ArrayList<Float> rating_value_;
    private final ArrayList<Integer> reviews_count_;
    public final ArrayList<String> address_;



    public reviewslistAdapter(Activity context, ArrayList<String> name ,ArrayList<Float> rating_value,ArrayList<Integer> reviews_count,ArrayList<String> address ) {
        super(context, R.layout.reviewslistlayout, name);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.name_=name;
        this.rating_value_=rating_value;
        this.reviews_count_=reviews_count;
        this.address_ = address;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.reviewslistlayout, null,true);

         TextView name = (TextView) rowView.findViewById(R.id.name);
         RatingBar rating_bar = (RatingBar) rowView.findViewById(R.id.rating_bar);
         TextView reviews_count = (TextView) rowView.findViewById(R.id.reviews_count);
         TextView address = (TextView) rowView.findViewById(R.id.address);

        try {
            name.setText(name_.get(position));
            rating_bar.setRating(rating_value_.get(position));
            reviews_count.setText(reviews_count_.get(position));
            address.setText(address_.get(position));
        }catch (Exception e){
        }

        return rowView;
    }
}

