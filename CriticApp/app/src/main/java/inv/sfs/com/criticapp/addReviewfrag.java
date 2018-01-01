package inv.sfs.com.criticapp;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;

import inv.sfs.com.criticapp.Models.FullReviewModel;
import inv.sfs.com.criticapp.Models.Rating;
import inv.sfs.com.criticapp.Models.Restaurant;


/**
 * A simple {@link Fragment} subclass.
 */
public class addReviewfrag extends Fragment implements View.OnClickListener {


    ListView add_review_lv;
    Button instant_btn,submit_instant_zero_btn;
    public ArrayList<String> review_against =new ArrayList<String>();
    public ArrayList<String> comments =new ArrayList<String>();
    Dialog dialog;
    Integer restaurantPosition;
    EditText instant_zero_comment;
    Restaurant restaurant;
    FullReviewModel fullReviewModel;
    TransparentProgressDialog pd;

    public addReviewfrag(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        setHasOptionsMenu(true);
         return inflater.inflate(R.layout.fragment_add_reviewfrag, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("Critic Review");

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
        review_against.add("Delivery Time");
        review_against.add("null");


        //Comments ArrayList//

        comments.add("Comment: Busy, Remote, Small");
        comments.add("Comment: Friendly, Grumpy");
        comments.add("Comment: Short Wait, Long Wait");
        comments.add("Comment: small, wobbly, nice");
        comments.add("Comment: Lots of Options, Limited");
        comments.add("Comment: Great, Expensive");
        comments.add("Comment: Friendly, Horrible");
        comments.add("Comment: Fast, Long wait, wrong");
        comments.add("Comment: Cooked wrong, Write");
        comments.add("Comment: Looked Great, Sloppy");
        comments.add("Comment: Yummy, Nasty, Great");
        comments.add("Comment: Too Loud, Nice, Quite");
        comments.add("Comment: Too Dark at my table");
        comments.add("Comment: Perfect, Too Hot, Cold");
        comments.add("Comment: Clean, Smelly, Dirty");
        comments.add("Comment: Clean, Smelly, Dirty");
        comments.add("Comment: Long Wait, Quick");
        comments.add("Comment: Great Place, Love It");
        comments.add("Comment: 30min etc.");
        comments.add("null");

        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.customdialogue);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        submit_instant_zero_btn = (Button) dialog.getWindow().findViewById(R.id.submit_instant_zero_btn);
        submit_instant_zero_btn.setOnClickListener(this);
        pd = new TransparentProgressDialog(getContext(), R.drawable.loader);
        instant_zero_comment = (EditText) dialog.getWindow().findViewById(R.id.instant_zero_comment);




        instant_btn = (Button) getView().findViewById(R.id.instant_btn);
        instant_btn.setOnClickListener(this);

        restaurantPosition = Integer.valueOf(getArguments().getString("position"));

        add_review_lv = (ListView) getView().findViewById(R.id.add_review_lv);
        addReviewsAdapter adapter = new addReviewsAdapter(getActivity(), review_against,comments, StorageHelper.restaurants_generic_list.get(restaurantPosition));
        add_review_lv.setAdapter(adapter);
        add_review_lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id){
                // TODO Auto-generated method stub
                String pos = String.valueOf(position);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){

        menu.clear();
        inflater.inflate(R.menu.main, menu);
        menu.findItem(R.id.next).setVisible(true);
        getActivity().invalidateOptionsMenu();
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){

            case android.R.id.home:
                getActivity().finish();
                break;
            case R.id.next:
                submitReviewfrag submitreview = new submitReviewfrag();
                android.support.v4.app.FragmentTransaction trans1 = getActivity().getSupportFragmentManager().beginTransaction();
                trans1.replace(R.id.frame_container,submitreview).addToBackStack(null).commit();
                return  true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v){
        if(v.getId() == instant_btn.getId()){
            dialog.show();
        }else if(v.getId() == submit_instant_zero_btn.getId()){
            if(instant_zero_comment.getText().equals("")){
                Toast.makeText(getContext(), "Add comment to submit!!", Toast.LENGTH_SHORT).show();
            }else{
                doWork();
            }
        }
    }

    private void doWork(){


        pd.show();
        restaurant = StorageHelper.restaurants_generic_list.get(restaurantPosition);
        if(restaurant.parseObject != null){
            Toast.makeText(getContext(), "Restaurant Saved", Toast.LENGTH_LONG).show();
            final ParseObject fullReviewObj = new ParseObject("FullReview");

            fullReviewObj.put("restaurantId", restaurant.parseObject);
            fullReviewObj.put("userId", ParseUser.getCurrentUser());
            fullReviewObj.put("delivery_time", "00");
            fullReviewObj.put("recommend_to_others", false);
            fullReviewObj.put("comments", instant_zero_comment.getText().toString());
            fullReviewObj.put("averageRating", 0);
            fullReviewObj.put("instant_zero", true);

            fullReviewObj.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(getContext(), "FullReview Saved", Toast.LENGTH_LONG).show();

                        ParseUser currentUser = ParseUser.getCurrentUser();
                        currentUser.setACL(new ParseACL(currentUser));
                        currentUser.saveInBackground();
                        dialog.dismiss();
                        pd.dismiss();
                    } else {
                        dialog.dismiss();
                        pd.dismiss();
                        Toast.makeText(getContext(), "Error saving fullReview", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            final ParseObject restaurantObj = new ParseObject("Restaurant");
            restaurantObj.put("place_id", restaurant.PlaceId);
            restaurantObj.put("id", restaurant.ID);
            restaurantObj.put("latitude", restaurant.latitude);
            restaurantObj.put("longitude", restaurant.longitude);
            restaurantObj.put("name", restaurant.restaurant_name);
            restaurantObj.put("vicinity", restaurant.vicinity);
            restaurantObj.put("icon_url", restaurant.icon_url);

            ParseGeoPoint geoPoint = new ParseGeoPoint(restaurant.latitude, restaurant.longitude);
            restaurantObj.put("location", geoPoint);

            restaurantObj.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        restaurant.parseObject = restaurantObj;
                        Toast.makeText(getContext(), "Restaurant Saved", Toast.LENGTH_LONG).show();
                        final ParseObject fullReviewObj = new ParseObject("FullReview");

                        fullReviewObj.put("restaurantId", restaurantObj);
                        fullReviewObj.put("userId", ParseUser.getCurrentUser());
                        fullReviewObj.put("delivery_time", "00");
                        fullReviewObj.put("recommend_to_others", false);
                        fullReviewObj.put("comments", instant_zero_comment.getText().toString());
                        fullReviewObj.put("averageRating", 0);
                        fullReviewObj.put("instant_zero", true);

                        fullReviewObj.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Toast.makeText(getContext(), "FullReview Saved", Toast.LENGTH_LONG).show();

                                    ParseUser currentUser = ParseUser.getCurrentUser();
                                    currentUser.setACL(new ParseACL(currentUser));
                                    currentUser.saveInBackground();
                                    dialog.dismiss();
                                    pd.dismiss();
                                } else {
                                    dialog.dismiss();
                                    pd.dismiss();
                                    Toast.makeText(getContext(), "Error saving fullReview", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "Error saving restaurant", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
