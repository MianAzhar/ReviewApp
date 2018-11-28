package inv.sfs.com.criticapp;


import android.app.Dialog;
import android.icu.util.Calendar;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

import inv.sfs.com.criticapp.Models.Notification;
import inv.sfs.com.criticapp.Models.Restaurant;


/**
 * A simple {@link Fragment} subclass.
 */
public class userIvitesDetailsfrag extends Fragment implements View.OnClickListener{

    TextView name_tv,address_tv,avgRating_tv,custom_month,custom_date,rsvp_text, discountText_tv, promotionText_tv;
    TextView invite_message_tv;
    Button rsvp_btn, decline_btn;
    TransparentProgressDialog pd;
    Boolean alreadyGivenReview = false;
    Dialog dialog;
    Button add_review,cancel;

    LinearLayout buttons_containor;

    public userIvitesDetailsfrag(){

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_ivites_detailsfrag, container, false);

        decline_btn = view.findViewById(R.id.decline);
        rsvp_btn = view.findViewById(R.id.rsvp);
        decline_btn.setOnClickListener(this);
        rsvp_btn.setOnClickListener(this);

        pd = new TransparentProgressDialog(getContext(), R.drawable.loader);
        name_tv = view.findViewById(R.id.name);
        address_tv = view.findViewById(R.id.address);
        avgRating_tv = view.findViewById(R.id.avgRating);
        custom_month = view.findViewById(R.id.custom_month);
        custom_date = view.findViewById(R.id.custom_date);

        discountText_tv = view.findViewById(R.id.discountText_tv);
        promotionText_tv = view.findViewById(R.id.promotionText_tv);

        invite_message_tv = view.findViewById(R.id.invite_message_tv);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("Restaurant Invites");

        buttons_containor = (LinearLayout) getView().findViewById(R.id.buttons_containor);
        rsvp_text = (TextView) getView().findViewById(R.id.rsvp_text);

        //Custom Dialogue Box..
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.givereviewdialogue);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        add_review = (Button) dialog.getWindow().findViewById(R.id.add_review);
        add_review.setOnClickListener(this);

        cancel = (Button) dialog.getWindow().findViewById(R.id.cancel);
        cancel.setOnClickListener(this);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String name = bundle.getString("restaurant_name");
            String vicinity = bundle.getString("vicinity");
            String avgRating = bundle.getString("avgRating");
            String month = bundle.getString("month");
            String day = bundle.getString("day");

            String discountText = bundle.getString("discountText");
            String promotionText = bundle.getString("promotionText");

            name_tv.setText(name);
            address_tv.setText(vicinity);
            avgRating_tv.setText(avgRating);
            custom_month.setText(month);
            custom_date.setText(day);

            discountText_tv.setText(discountText);
            promotionText_tv.setText(promotionText);

            //invite_message_tv.setText("We invite you to give us a review and as a reward for your review we are offering " + discountText);
        }

        try{
            if(StorageHelper.notificationTempObj.isAccepted){
                // Hiding RsVP and Decline Button..
                buttons_containor.setVisibility(View.GONE);
                rsvp_text.setVisibility(View.GONE);
                populateFields();
            }
        }catch (Exception e){

        }

        checkifReviewalreadyGiven();
    }

    @Override
    public void onClick(View view){
        if(view.getId() == rsvp_btn.getId()){
            if(alreadyGivenReview){
                try {
                    StorageHelper.notificationTempObj.parseObject.put("isAccepted" ,true);
                    StorageHelper.notificationTempObj.parseObject.put("isDeclined" ,false);
                    StorageHelper.notificationTempObj.parseObject.saveInBackground();
                    buttons_containor.setVisibility(View.GONE);
                    rsvp_text.setVisibility(View.GONE);
                    populateFields();

                    try{
                        updateRank();
                    }catch (Exception e){
                    }


                } catch (Exception e){
                    e.printStackTrace();
                }
            }else{
                dialog.show();
            }

        }else if(view.getId() == add_review.getId()){
            dialog.hide();
            movetoAddReviewFragment();
        }else if(view.getId() == cancel.getId()){
            dialog.hide();
        }else if(view.getId() == decline_btn.getId()){
            try {
                StorageHelper.notificationTempObj.parseObject.put("isAccepted" ,false);
                StorageHelper.notificationTempObj.parseObject.put("isDeclined" ,true);
                StorageHelper.notificationTempObj.parseObject.save();
                //getActivity().getFragmentManager().beginTransaction().remove(this).commit();
                //getActivity().getFragmentManager().popBackStack();
                getActivity().getSupportFragmentManager().popBackStack();

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public void checkifReviewalreadyGiven(){
        pd.show();
        ParseQuery<ParseObject> parseQuery = new ParseQuery<>("FullReview");
        parseQuery.whereEqualTo("userId", ParseUser.getCurrentUser());
        parseQuery.whereEqualTo("restaurantId",StorageHelper.restaurant_TempObj);
        parseQuery.setLimit(1000);

        parseQuery.findInBackground(new FindCallback<ParseObject>(){
            @Override
            public void done(List<ParseObject> list, ParseException e){
                if (e == null){
                    if (!list.isEmpty()){
                        for(int i = 0; i < list.size(); i++){
                            ParseObject obj = list.get(i);
                            Date date = new Date();

                            Date createdAt = obj.getCreatedAt();

                            long diff = date.getTime() - createdAt.getTime();

                            long days = diff / (24 * 60 * 60 * 1000);

                            if(days > 1){
                                continue;
                            } else {
                                if(getActivity() != null){
                                    Toast.makeText(getContext(), "Already Given Review Against This Rest", Toast.LENGTH_SHORT).show();
                                    alreadyGivenReview = true;
                                }
                                pd.dismiss();
                                return;
                            }
                        }

                        Toast.makeText(getActivity(), "Not Given Review Against This Rest", Toast.LENGTH_SHORT).show();
                        alreadyGivenReview = false;
                        pd.dismiss();
                    } else{
                        alreadyGivenReview = false;
                        Toast.makeText(getActivity(), "Not Given Review Against This Rest", Toast.LENGTH_SHORT).show();
                        Log.d("Result", "Empty");
                        pd.dismiss();
                    }
                } else{
                    Log.d("Result", "Wrong");
                    pd.dismiss();
                }
            }
        });
    }

    public void populateFields(){

        badgeDetails badgedetails = new badgeDetails();
        android.support.v4.app.FragmentTransaction trans1 = getActivity().getSupportFragmentManager().beginTransaction();
        trans1.replace(R.id.frame_container,badgedetails).addToBackStack(null).commit();

        //promo_text.setText(StorageHelper.notificationTempObj.promotionId.getString("promotionText"));
        /*
        try{
            if(StorageHelper.notificationTempObj.promotionId.getParseFile("promotionImage").getUrl() != null){
                Picasso.with(getActivity())
                        .load(StorageHelper.notificationTempObj.promotionId.getParseFile("promotionImage").getUrl())
                        .fit()
                        .into(promo_image);
            }

        }catch (Exception e){
        }
        */
    }

    public void movetoAddReviewFragment(){
        Boolean contain = false;
        int position = -1;
        for (int i = 0; i < StorageHelper.restaurants_generic_list.size(); i++){

            if(StorageHelper.restaurants_generic_list.get(i).parseObject != null){
                String objectID = (String) StorageHelper.restaurants_generic_list.get(i).parseObject.getObjectId();
                if (objectID.equals(StorageHelper.restaurant_TempObj.getObjectId())){
                    contain = true;
                    position = i;
                    break;
                }
            }
        }

        if(contain){
            StorageHelper.backtoUserInvites = true;
            StorageHelper.uiBlock = false;
            StorageHelper.isNewReview = false;
            Bundle bundle = new Bundle();
            addReviewfrag addreview = new addReviewfrag();
            bundle.putString("position" , String.valueOf(position));
            addreview.setArguments(bundle);
            android.support.v4.app.FragmentTransaction ft = ((AppCompatActivity) getContext()).getSupportFragmentManager()
                    .beginTransaction();
            ft.replace(R.id.frame_container,addreview).addToBackStack(null).commit();
        }else if(!contain){
            StorageHelper.backtoUserInvites = true;
            Restaurant tempRestaurant = new Restaurant();
            tempRestaurant.restaurant_name = StorageHelper.restaurant_TempObj.getString("name");
            tempRestaurant.vicinity = StorageHelper.restaurant_TempObj.getString("vicinity");
            tempRestaurant.parseObject = StorageHelper.restaurant_TempObj;
            StorageHelper.restaurants_generic_list.add(tempRestaurant);

            StorageHelper.uiBlock = false;
            StorageHelper.isNewReview = false;
            Bundle bundle = new Bundle();
            addReviewfrag addreview = new addReviewfrag();
            bundle.putString("position" , String.valueOf( StorageHelper.restaurants_generic_list.size()-1));
            addreview.setArguments(bundle);
            android.support.v4.app.FragmentTransaction ft = ((AppCompatActivity) getContext()).getSupportFragmentManager()
                    .beginTransaction();
            ft.replace(R.id.frame_container,addreview).addToBackStack(null).commit();
        }

    }

    public void updateRank(){

        ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Notifications");
        parseQuery.whereEqualTo("userId", ParseUser.getCurrentUser());
        parseQuery.whereEqualTo("isAccepted",true);
        parseQuery.setLimit(1000);

        parseQuery.findInBackground(new FindCallback<ParseObject>(){
            @Override
            public void done(List<ParseObject> list, ParseException e){
                if (e == null){
                    if (!list.isEmpty()){

                        int user_current_rank = 1;
                        int updated_rank = 1;
                            try{
                                user_current_rank = (int) ParseUser.getCurrentUser().get("rank");
                                updated_rank = user_current_rank;
                            }catch (Exception ex){
                            }

                          if(user_current_rank == 1 && list.size() >= 10){
                                updated_rank++;
                          }else if(user_current_rank == 2 && list.size() >= 20){
                                updated_rank++;
                          }else if(user_current_rank == 3 && list.size() >= 30){
                                updated_rank++;
                          }else if(user_current_rank == 4 && list.size() >= 40){
                                updated_rank++;
                          }else if(user_current_rank == 5 && list.size() >= 50){
                                updated_rank++;
                         }

                         ParseUser.getCurrentUser().put("rank" ,updated_rank);
                        try {
                            ParseUser.getCurrentUser().save();
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }

                    } else{

                    }
                } else{
                    Log.d("Result", "Wrong");
                    pd.dismiss();
                }
            }
        });
    }

}
