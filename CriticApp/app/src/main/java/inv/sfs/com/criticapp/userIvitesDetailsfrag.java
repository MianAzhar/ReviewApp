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

import java.util.List;

import inv.sfs.com.criticapp.Models.Notification;
import inv.sfs.com.criticapp.Models.Restaurant;


/**
 * A simple {@link Fragment} subclass.
 */
public class userIvitesDetailsfrag extends Fragment implements View.OnClickListener{

    TextView name_tv,address_tv,avgRating_tv,custom_month,custom_date,promo_text,rsvp_text;
    Button rsvp_btn, decline_btn;
    TransparentProgressDialog pd;
    Boolean alreadyGivenReview = false;
    Dialog dialog;
    Button add_review,cancel;
    ImageView promo_image;
    LinearLayout buttons_containor;

    public userIvitesDetailsfrag(){

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_ivites_detailsfrag, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("User Invites");

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

        decline_btn = (Button) getView().findViewById(R.id.decline);
        rsvp_btn = (Button) getView().findViewById(R.id.rsvp);
        decline_btn.setOnClickListener(this);
        rsvp_btn.setOnClickListener(this);

        pd = new TransparentProgressDialog(getContext(), R.drawable.loader);
        name_tv = (TextView) getView().findViewById(R.id.name);
        address_tv = (TextView) getView().findViewById(R.id.address);
        avgRating_tv = (TextView) getView().findViewById(R.id.avgRating);
        custom_month = (TextView) getView().findViewById(R.id.custom_month);
        custom_date = (TextView) getView().findViewById(R.id.custom_date);
        promo_text = (TextView) getView().findViewById(R.id.promo_text);
        promo_image = (ImageView) getView().findViewById(R.id.promo_image);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String name = bundle.getString("restaurant_name");
            String vicinity = bundle.getString("vicinity");
            String avgRating = bundle.getString("avgRating");
            String month = bundle.getString("month");
            String day = bundle.getString("day");

            name_tv.setText(name);
            address_tv.setText(vicinity);
            avgRating_tv.setText(avgRating);
            custom_month.setText(month);
            custom_date.setText(day);
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
                    StorageHelper.notificationTempObj.parseObject.save();
                    buttons_containor.setVisibility(View.GONE);
                    rsvp_text.setVisibility(View.GONE);
                    populateFields();
                } catch (ParseException e) {
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
                        Toast.makeText(getActivity(), "Already Given Review Against This Rest", Toast.LENGTH_SHORT).show();
                        alreadyGivenReview = true;
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
        promo_text.setText(StorageHelper.notificationTempObj.promotionId.getString("promotionText"));

        try{
            if(StorageHelper.notificationTempObj.promotionId.getParseFile("promotionImage").getUrl() != null){
                Picasso.with(getActivity())
                        .load(StorageHelper.notificationTempObj.promotionId.getParseFile("promotionImage").getUrl())
                        .fit()
                        .into(promo_image);
               /* FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(500, 500);
                layoutParams.gravity = Gravity.CENTER;
                profile_img.setLayoutParams(layoutParams);*/
            }

        }catch (Exception e){
        }
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
            Bundle bundle = new Bundle();
            addReviewfrag addreview = new addReviewfrag();
            bundle.putString("position" , String.valueOf( StorageHelper.restaurants_generic_list.size()-1));
            addreview.setArguments(bundle);
            android.support.v4.app.FragmentTransaction ft = ((AppCompatActivity) getContext()).getSupportFragmentManager()
                    .beginTransaction();
            ft.replace(R.id.frame_container,addreview).addToBackStack(null).commit();
        }

    }
}
