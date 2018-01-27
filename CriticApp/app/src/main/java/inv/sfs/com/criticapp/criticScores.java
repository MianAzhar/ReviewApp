package inv.sfs.com.criticapp;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import inv.sfs.com.criticapp.Models.FullReviewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class criticScores extends Fragment {


    TransparentProgressDialog pd;
    ListView my_critics_list;
    FullReviewModel localFullReviews;
    public ArrayList<FullReviewModel> fullReviews_list = new ArrayList<FullReviewModel>();

    public criticScores(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_critic_scores, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        pd = new TransparentProgressDialog(getActivity(), R.drawable.loader);
        my_critics_list = (ListView) getView().findViewById(R.id.my_critics);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("My Critics Review");
        getReviews();
    }

    public void getReviews(){

        pd.show();
        ParseQuery<ParseObject> parseQuery = new ParseQuery<>("FullReview");
        parseQuery.whereEqualTo("restaurantId", ParseUser.getCurrentUser().get("restaurant"));
        parseQuery.include("userId");
        parseQuery.include("restaurantId");
        parseQuery.setLimit(1000);

        parseQuery.findInBackground(new FindCallback<ParseObject>(){
            @Override
            public void done(List<ParseObject> list, ParseException e){
                if (e == null){
                    if (!list.isEmpty()){
                        for (int i = 0; i < list.size(); i++){
                            ParseObject parseObject = list.get(i);
                            localFullReviews = new FullReviewModel();
                            localFullReviews.parseObject = parseObject;
                            localFullReviews.userObj = parseObject.getParseUser("userId");

                            if(parseObject.get("overall_Rating") != null)
                                localFullReviews.overall_Rating = (int) parseObject.get("overall_Rating");
                            else
                                localFullReviews.overall_Rating = 0;


                            if(parseObject.get("averageRating") != null)
                                localFullReviews.averageRating = parseObject.getInt("averageRating");
                            else
                                localFullReviews.averageRating = 0;


                            localFullReviews.delivery_time = parseObject.getString("delivery_time");
                            localFullReviews.recommend_to_others = parseObject.getBoolean("recommend_to_others");
                            localFullReviews.comments = parseObject.getString("comments");
                            localFullReviews.servers_name = parseObject.getString("servers_name");
                            localFullReviews.restaurantObj = parseObject.getParseObject("restaurantId");

                            fullReviews_list.add(localFullReviews);
                        }
                        pd.dismiss();
                        populateAdapter();
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


    public void populateAdapter(){
        final my_critics_reviews_list_adapter adapter = new my_critics_reviews_list_adapter(getActivity(), fullReviews_list);
        my_critics_list.setAdapter(adapter);
        my_critics_list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id){

                StorageHelper.shareReview = true;
                StorageHelper.uiBlock = true;
                Bundle bundle = new Bundle();
                bundle.putString("reataurant_name_st" , fullReviews_list.get(position).userObj.get("name").toString());
                //bundle.putString("reataurant_name_st" , fullReviews_list.get(position).restaurantObj.get("name").toString());
                bundle.putFloat("total_rating_stars_float" , fullReviews_list.get(position).overall_Rating);
                bundle.putString("total_rating_st" , String.valueOf(fullReviews_list.get(position).averageRating));
                bundle.putParcelable("fullReview" , fullReviews_list.get(position).parseObject);
                addReviewfrag addreview = new addReviewfrag();
                addreview.setArguments(bundle);
                android.support.v4.app.FragmentTransaction trans1 = ((AppCompatActivity) getContext()).getSupportFragmentManager()
                        .beginTransaction();
                trans1.replace(R.id.frame_container,addreview).addToBackStack(null).commit();
            }
        });
    }

}


class my_critics_reviews_list_adapter extends ArrayAdapter<FullReviewModel> {
    private final Activity context;
    private final ArrayList<FullReviewModel> reviews_list_;

    public my_critics_reviews_list_adapter(Activity context, ArrayList<FullReviewModel> reviews_list){
        super(context, R.layout.restaurantsreviewlayout, reviews_list);
        // TODO Auto-generated constructor stub

        this.context = context;
        this.reviews_list_ = reviews_list;
    }

    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.restaurantsreviewlayout, null, true);

        TextView userScore = (TextView) rowView.findViewById(R.id.userScore);
        TextView name = (TextView) rowView.findViewById(R.id.name);
        TextView comments = (TextView) rowView.findViewById(R.id.comments);
        RatingBar rating_bar = (RatingBar) rowView.findViewById(R.id.rating_bar);

        userScore.setText(Integer.toString(reviews_list_.get(position).averageRating));
        comments.setText(reviews_list_.get(position).comments);
        rating_bar.setRating(reviews_list_.get(position).overall_Rating);
        if(reviews_list_.get(position).restaurantObj != null)
            name.setText(reviews_list_.get(position).userObj.getString("name"));
        else
            name.setText("Null it is");

        return rowView;
    }
}
