package inv.sfs.com.criticapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import inv.sfs.com.criticapp.Models.FullReviewModel;
import inv.sfs.com.criticapp.Models.Rating;
import inv.sfs.com.criticapp.Models.Restaurant;

import static android.app.Activity.RESULT_OK;

/**
 * Created by iosdev-1 on 8/9/17.
 */

public class addReviewsAdapter  extends ArrayAdapter<String> {


    private final Activity context;
    private final ArrayList<String> name_;
    private final ArrayList<String> comments_;
    private ArrayList<Rating> ratings;
    private final ArrayList<Rating> editRatingObject_;
    private final FullReviewModel fullReviewModel;
    private final Restaurant restaurant;
    private final Boolean editMode_;
    TransparentProgressDialog pd;
    float finalRating_value = 0;
    public ImageView image_iv;
    private static final int SELECT_PICTURE = 1;
    public int averageRating;
    ParseObject tempFullReview;
    public String instant_zero_comment_st;
    public Dialog dialog;


    public addReviewsAdapter(Activity context, ArrayList<String> name, ArrayList<String> comments, Restaurant restaurant,Boolean editMode,ArrayList<Rating> editRatingObject,ParseObject fullReviewModel) {
        super(context, R.layout.addreviewslayout, name);
        // TODO Auto-generated constructor stub

        this.editMode_ = editMode;
        this.context=context;
        this.name_=name;
        this.comments_ = comments;
        this.ratings = new ArrayList<>();
        this.editRatingObject_ = editRatingObject;
        pd = new TransparentProgressDialog(context, R.drawable.loader);

        this.restaurant = restaurant;
        this.fullReviewModel = new FullReviewModel();
        this.fullReviewModel.parseObject = fullReviewModel;

        if(fullReviewModel != null){
            this.fullReviewModel.delivery_time = fullReviewModel.get("delivery_time").toString();
            this.fullReviewModel.recommend_to_others = fullReviewModel.getBoolean("recommend_to_others");
            this.fullReviewModel.comments = fullReviewModel.getString("comments");
            this.fullReviewModel.servers_name = fullReviewModel.getString("servers_name");
        }

        if(editMode_ && !fullReviewModel.getBoolean("instant_zero")){
            this.ratings = editRatingObject_;
        }else{
            for(int i = 0; i < name_.size(); i++){
                Rating temp = new Rating();
                temp.title = name_.get(i);
                this.ratings.add(temp);
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public View getView(final int position, View view, ViewGroup parent){
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView;
        final Button yes_btn, no_btn;

        if(position == 0){
            rowView= inflater.inflate(R.layout.instantzerolayout, null,true);
            Button instant_btn,submit_instant_zero_btn;
            LinearLayout view_review_extra_lay,share_lay;
            TextView restaurant_name,criticScore;
            RatingBar rating_bar;
            ImageView facebook_iv,twitter_iv;


            dialog = new Dialog(getContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.customdialogue);
            Window window = dialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            submit_instant_zero_btn = (Button) dialog.getWindow().findViewById(R.id.submit_instant_zero_btn);
            final EditText instant_zero_comment = (EditText) dialog.getWindow().findViewById(R.id.instant_zero_comment);

            instant_btn = (Button) rowView.findViewById(R.id.instant_btn);
            view_review_extra_lay = (LinearLayout) rowView.findViewById(R.id.view_review_extra_lay);
            //share_lay = (LinearLayout) rowView.findViewById(R.id.share_lay);
            restaurant_name = (TextView) rowView.findViewById(R.id.restaurant_name);
            criticScore = (TextView) rowView.findViewById(R.id.criticScore);
            rating_bar = (RatingBar) rowView.findViewById(R.id.rating_bar);
            facebook_iv = (ImageView) rowView.findViewById(R.id.facebook_iv);
            twitter_iv = (ImageView) rowView.findViewById(R.id.twitter_iv);

            restaurant_name.setText(StorageHelper.reataurant_name_st);
            criticScore.setText(StorageHelper.total_rating_st);
            rating_bar.setRating(StorageHelper.total_rating_stars_float);
            rating_bar.setFocusable(false);
            rating_bar.setEnabled(false);

            LayerDrawable stars = (LayerDrawable) rating_bar.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

            rating_bar.setOnTouchListener(new View.OnTouchListener(){
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });


            if(StorageHelper.uiBlock){
                //StorageHelper.uiBlock = false;
                instant_btn.setVisibility(View.GONE);
                view_review_extra_lay.setVisibility(View.VISIBLE);
            }else{
                instant_btn.setVisibility(View.VISIBLE);
                view_review_extra_lay.setVisibility(View.GONE);
            }

            instant_btn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    dialog.show();
                }
            });

            submit_instant_zero_btn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if(instant_zero_comment.getText().toString().equals("")){
                        instant_zero_comment.setError("Add comment to submit!!");
                        instant_zero_comment.requestFocus();
                    }else{
                        instant_zero_comment_st = instant_zero_comment.getText().toString();
                        doWorkInstantZero();
                    }
                }
            });

            /*twitter_iv.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setClassName("com.twitter.android",
                            "com.twitter.android.PostActivity");
                    shareIntent.setType("text/*");
                    String text = "https://play.google.com/store/apps/details?id=inv.sfs.com.disasterresourse \n " +
                            "ParseUser.getCurrentUser().get(\"name\") + \" reviewed \" + StorageHelper.reataurant_name_st + \" and gave it a \" +\n" +
                            "StorageHelper.total_rating_st +\" score.\"";
                    shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);
                    context.startActivityForResult(shareIntent, 9);
                }
            });*/

            facebook_iv.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    ShareLinkContent content = new ShareLinkContent.Builder()
                            .setContentUrl(Uri.parse(" https://play.google.com/store/apps/details?id=inv.sfs.com.disasterresourse"))
                            .setQuote(ParseUser.getCurrentUser().get("name") + " reviewed " + StorageHelper.reataurant_name_st + " and gave it a " +
                                    StorageHelper.total_rating_st +" score.")
                            .build();

                    ShareDialog shareDialog = new ShareDialog(context);
                    shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);
                }
            });


        } else if(position == name_.size() - 1){
            rowView= inflater.inflate(R.layout.submit_review_bottom_layout, null,true);

            yes_btn = (Button) rowView.findViewById(R.id.yes_btn);
            no_btn = (Button) rowView.findViewById(R.id.no_btn);
            final EditText overAllComments = (EditText)rowView.findViewById(R.id.overallComments);
            final EditText serversName = (EditText)rowView.findViewById(R.id.serverName);
            final Button submitBtn = (Button)rowView.findViewById(R.id.submit);
            final LinearLayout be_a_critic_lay = (LinearLayout)rowView.findViewById(R.id.be_a_critic_lay);
            final LinearLayout submitRevierContainor = (LinearLayout) rowView.findViewById(R.id.submitRevierContainor);
            image_iv = (ImageView)rowView.findViewById(R.id.image_iv);

            if(StorageHelper.uiBlock){

                overAllComments.setFocusable(false);
                serversName.setFocusable(false);

                be_a_critic_lay.setVisibility(View.GONE);
                be_a_critic_lay.setFocusable(false);
                be_a_critic_lay.setEnabled(false);
                be_a_critic_lay.setFocusableInTouchMode(false);

                submitBtn.setVisibility(View.GONE);
                submitBtn.setFocusable(false);
                submitBtn.setEnabled(false);
                submitBtn.setFocusableInTouchMode(false);

                yes_btn.setFocusable(false);
                yes_btn.setEnabled(false);
                yes_btn.setFocusableInTouchMode(false);

                no_btn.setFocusable(false);
                no_btn.setEnabled(false);
                no_btn.setFocusableInTouchMode(false);

                image_iv.setFocusable(false);
                image_iv.setEnabled(false);
                image_iv.setFocusableInTouchMode(false);

            }


            image_iv.setImageBitmap(StorageHelper.bitmapImageFile);
            if(StorageHelper.bitmapImageFile != null){
                image_iv.getLayoutParams().height = 800;
                image_iv.getLayoutParams().width = 800;
            }

            if(fullReviewModel.comments != null)
                overAllComments.setText(fullReviewModel.comments);

            if(fullReviewModel.servers_name != null)
                serversName.setText(fullReviewModel.servers_name);

            overAllComments.addTextChangedListener(new TextWatcher(){
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after){

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count){

                }

                @Override
                public void afterTextChanged(Editable s){
                    fullReviewModel.comments = s.toString();
                }
            });

            serversName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    fullReviewModel.servers_name = s.toString();
                }
            });

            submitBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    saveReview();
                }
            });


            be_a_critic_lay.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    context.startActivityForResult(Intent.createChooser(intent,
                            "Select Picture"), SELECT_PICTURE);
                }
            });

            if(fullReviewModel.recommend_to_others){
                yes_btn.setBackground(getContext().getResources().getDrawable(R.drawable.standard_btn));
                no_btn.setBackground(getContext().getResources().getDrawable(R.drawable.search_bg));
                yes_btn.setTextColor(getContext().getResources().getColor(R.color.white));
                no_btn.setTextColor(getContext().getResources().getColor(R.color.black));
            } else {
                no_btn.setBackground(getContext().getResources().getDrawable(R.drawable.standard_btn));
                yes_btn.setBackground(getContext().getResources().getDrawable(R.drawable.search_bg));
                no_btn.setTextColor(getContext().getResources().getColor(R.color.white));
                yes_btn.setTextColor(getContext().getResources().getColor(R.color.black));
            }

            yes_btn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    fullReviewModel.recommend_to_others = true;
                    yes_btn.setBackground(getContext().getResources().getDrawable(R.drawable.standard_btn));
                    no_btn.setBackground(getContext().getResources().getDrawable(R.drawable.search_bg));
                    yes_btn.setTextColor(getContext().getResources().getColor(R.color.white));
                    no_btn.setTextColor(getContext().getResources().getColor(R.color.black));
                }
            });

            no_btn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    fullReviewModel.recommend_to_others = false;
                    no_btn.setBackground(getContext().getResources().getDrawable(R.drawable.standard_btn));
                    yes_btn.setBackground(getContext().getResources().getDrawable(R.drawable.search_bg));
                    no_btn.setTextColor(getContext().getResources().getColor(R.color.white));
                    yes_btn.setTextColor(getContext().getResources().getColor(R.color.black));
                }
            });

        }else{
            rowView = inflater.inflate(R.layout.addreviewslayout, null,true);

            LinearLayout stars_layout = (LinearLayout) rowView.findViewById(R.id.stars_layout);
            LinearLayout stars_layout_overall_apeal = (LinearLayout) rowView.findViewById(R.id.stars_layout_overall_apeal);
            TextView name = (TextView) rowView.findViewById(R.id.review_against);
            EditText comments_tv = (EditText) rowView.findViewById(R.id.comments_tv);
            final TextView tv_one = (TextView) rowView.findViewById(R.id.tv_one);
            final TextView tv_two = (TextView) rowView.findViewById(R.id.tv_two);
            final TextView tv_three = (TextView) rowView.findViewById(R.id.tv_three);
            final TextView tv_four = (TextView) rowView.findViewById(R.id.tv_four);
            final TextView tv_five = (TextView) rowView.findViewById(R.id.tv_five);
            final RatingBar rating_bar_overall_appeal = (RatingBar) rowView.findViewById(R.id.rating_bar_overall_appeal);


            if(StorageHelper.uiBlock){

                stars_layout.setFocusable(false);
                stars_layout_overall_apeal.setFocusable(false);
                name.setFocusable(false);
                comments_tv.setFocusable(false);
                tv_one.setFocusable(false);
                tv_one.setEnabled(false);
                tv_one.setFocusableInTouchMode(false);

                tv_two.setFocusable(false);
                tv_two.setEnabled(false);
                tv_two.setFocusableInTouchMode(false);

                tv_three.setFocusable(false);
                tv_three.setEnabled(false);
                tv_three.setFocusableInTouchMode(false);

                tv_four.setFocusable(false);
                tv_four.setEnabled(false);
                tv_four.setFocusableInTouchMode(false);

                tv_five.setFocusable(false);
                tv_five.setEnabled(false);
                tv_five.setFocusableInTouchMode(false);

                rating_bar_overall_appeal.setFocusable(false);
                rating_bar_overall_appeal.setEnabled(false);


            }



            rating_bar_overall_appeal.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener(){
                public void onRatingChanged(RatingBar ratingBar, float rating,
                                            boolean fromUser){
                    finalRating_value = rating;
                    Rating temp = ratings.get(position);
                    temp.rated_value = (int)rating;
                }
            });


            /*if(position == 17){
                stars_layout.setVisibility(View.GONE);
                stars_layout_overall_apeal.setVisibility(View.VISIBLE);
            }*/

            if(position == name_.size() -2){
                stars_layout.setVisibility(View.GONE);
                //name.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            } else {
                TextView currentView;
                Rating temp = ratings.get(position);


                if(position == 17){
                    stars_layout.setVisibility(View.GONE);
                    stars_layout_overall_apeal.setVisibility(View.VISIBLE);

                    if(temp.rated_value == 5)
                        rating_bar_overall_appeal.setRating(5);
                    else if(temp.rated_value == 4)
                        rating_bar_overall_appeal.setRating(4);
                    else if(temp.rated_value == 3)
                        rating_bar_overall_appeal.setRating(3);
                    else if(temp.rated_value == 2)
                        rating_bar_overall_appeal.setRating(2);
                    else if(temp.rated_value == 1)
                        rating_bar_overall_appeal.setRating(1);
                    else if(temp.rated_value == 0)
                        rating_bar_overall_appeal.setRating(0);
                    else
                        rating_bar_overall_appeal.setRating(0);

                }else{

                    if(temp.rated_value == 5)
                        currentView = tv_five;
                    else if(temp.rated_value == 4)
                        currentView = tv_four;
                    else if(temp.rated_value == 3)
                        currentView = tv_three;
                    else if(temp.rated_value == 2)
                        currentView = tv_two;
                    else
                        currentView = tv_one;

                    currentView.setBackground(getContext().getResources().getDrawable(R.drawable.circle_red));
                    currentView.setTextColor(getContext().getResources().getColor(R.color.white));
                }
            }
            try {
                name.setText(name_.get(position));
                comments_tv.setHint(comments_.get(position));

                if(position == name_.size() -2)
                    comments_tv.setText(fullReviewModel.delivery_time);
                else {
                    Rating temp = ratings.get(position);
                    comments_tv.setText(temp.comment);
                }
            }catch (Exception e){
            }

            comments_tv.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s){
                    if(position == name_.size() -2)
                        fullReviewModel.delivery_time = s.toString();
                    else {
                        Rating temp = ratings.get(position);
                        temp.comment = s.toString();
                    }
                }
            });

            tv_one.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    Rating temp = ratings.get(position);
                    temp.rated_value = 1;
                    tv_one.setBackground(getContext().getResources().getDrawable(R.drawable.circle_red));
                    tv_one.setTextColor(getContext().getResources().getColor(R.color.white));

                    tv_two.setBackground(getContext().getResources().getDrawable(R.drawable.hollow_circle));
                    tv_two.setTextColor(getContext().getResources().getColor(R.color.app_basic_color));
                    //tv_two.setBackgroundTintList(getContext().getResources().getColor(R.color.app_basic_color));

                    tv_three.setBackground(getContext().getResources().getDrawable(R.drawable.hollow_circle));
                    tv_three.setTextColor(getContext().getResources().getColor(R.color.app_basic_color));

                    tv_four.setBackground(getContext().getResources().getDrawable(R.drawable.hollow_circle));
                    tv_four.setTextColor(getContext().getResources().getColor(R.color.app_basic_color));

                    tv_five.setBackground(getContext().getResources().getDrawable(R.drawable.hollow_circle));
                    tv_five.setTextColor(getContext().getResources().getColor(R.color.app_basic_color));

                }
            });

            tv_two.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    Rating temp = ratings.get(position);
                    temp.rated_value = 2;
                    tv_two.setBackground(getContext().getResources().getDrawable(R.drawable.circle_red));
                    tv_two.setTextColor(getContext().getResources().getColor(R.color.white));

                    tv_one.setBackground(getContext().getResources().getDrawable(R.drawable.hollow_circle));
                    tv_one.setTextColor(getContext().getResources().getColor(R.color.app_basic_color));

                    tv_three.setBackground(getContext().getResources().getDrawable(R.drawable.hollow_circle));
                    tv_three.setTextColor(getContext().getResources().getColor(R.color.app_basic_color));

                    tv_four.setBackground(getContext().getResources().getDrawable(R.drawable.hollow_circle));
                    tv_four.setTextColor(getContext().getResources().getColor(R.color.app_basic_color));

                    tv_five.setBackground(getContext().getResources().getDrawable(R.drawable.hollow_circle));
                    tv_five.setTextColor(getContext().getResources().getColor(R.color.app_basic_color));

                }
            });


            tv_three.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    Rating temp = ratings.get(position);
                    temp.rated_value = 3;
                    tv_three.setBackground(getContext().getResources().getDrawable(R.drawable.circle_red));
                    tv_three.setTextColor(getContext().getResources().getColor(R.color.white));

                    tv_one.setBackground(getContext().getResources().getDrawable(R.drawable.hollow_circle));
                    tv_one.setTextColor(getContext().getResources().getColor(R.color.app_basic_color));

                    tv_two.setBackground(getContext().getResources().getDrawable(R.drawable.hollow_circle));
                    tv_two.setTextColor(getContext().getResources().getColor(R.color.app_basic_color));

                    tv_four.setBackground(getContext().getResources().getDrawable(R.drawable.hollow_circle));
                    tv_four.setTextColor(getContext().getResources().getColor(R.color.app_basic_color));

                    tv_five.setBackground(getContext().getResources().getDrawable(R.drawable.hollow_circle));
                    tv_five.setTextColor(getContext().getResources().getColor(R.color.app_basic_color));

                }
            });

            tv_four.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    Rating temp = ratings.get(position);
                    temp.rated_value = 4;
                    tv_four.setBackground(getContext().getResources().getDrawable(R.drawable.circle_red));
                    tv_four.setTextColor(getContext().getResources().getColor(R.color.white));

                    tv_one.setBackground(getContext().getResources().getDrawable(R.drawable.hollow_circle));
                    tv_one.setTextColor(getContext().getResources().getColor(R.color.app_basic_color));

                    tv_two.setBackground(getContext().getResources().getDrawable(R.drawable.hollow_circle));
                    tv_two.setTextColor(getContext().getResources().getColor(R.color.app_basic_color));

                    tv_three.setBackground(getContext().getResources().getDrawable(R.drawable.hollow_circle));
                    tv_three.setTextColor(getContext().getResources().getColor(R.color.app_basic_color));

                    tv_five.setBackground(getContext().getResources().getDrawable(R.drawable.hollow_circle));
                    tv_five.setTextColor(getContext().getResources().getColor(R.color.app_basic_color));

                }
            });

            tv_five.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    Rating temp = ratings.get(position);
                    temp.rated_value = 5;
                    tv_five.setBackground(getContext().getResources().getDrawable(R.drawable.circle_red));
                    tv_five.setTextColor(getContext().getResources().getColor(R.color.white));

                    tv_one.setBackground(getContext().getResources().getDrawable(R.drawable.hollow_circle));
                    tv_one.setTextColor(getContext().getResources().getColor(R.color.app_basic_color));

                    tv_two.setBackground(getContext().getResources().getDrawable(R.drawable.hollow_circle));
                    tv_two.setTextColor(getContext().getResources().getColor(R.color.app_basic_color));

                    tv_three.setBackground(getContext().getResources().getDrawable(R.drawable.hollow_circle));
                    tv_three.setTextColor(getContext().getResources().getColor(R.color.app_basic_color));

                    tv_four.setBackground(getContext().getResources().getDrawable(R.drawable.hollow_circle));
                    tv_four.setTextColor(getContext().getResources().getColor(R.color.app_basic_color));
                }
            });
        }
        return rowView;
    }

    private void saveReview(){
        doWork();
        /*
        ParseQuery<ParseObject> restaurantQuery = new ParseQuery<>("Restaurant");
        restaurantQuery.whereEqualTo("place_id", restaurant.PlaceId);

        restaurantQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e == null){
                    Toast.makeText(context, "Restaurant Found", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Restaurant Not found", Toast.LENGTH_LONG).show();
                    doWork();
                }
            }
        });
        */
    }

    private void doWork(){
        if(restaurant.parseObject != null){
            pd.show();

            Toast.makeText(context, "Restaurant Saved", Toast.LENGTH_LONG).show();
            final ParseObject fullReviewObj;
            if(fullReviewModel.parseObject == null)
                fullReviewObj = new ParseObject("FullReview");
            else
                fullReviewObj = fullReviewModel.parseObject;

            fullReviewObj.put("restaurantId", restaurant.parseObject);
            fullReviewObj.put("userId", ParseUser.getCurrentUser());
            fullReviewObj.put("delivery_time", fullReviewModel.delivery_time);
            fullReviewObj.put("recommend_to_others", fullReviewModel.recommend_to_others);
            fullReviewObj.put("comments", fullReviewModel.comments);
            fullReviewObj.put("servers_name", fullReviewModel.servers_name);
            fullReviewObj.put("overall_Rating", finalRating_value);


            int sum = 0;

            for(int i = 0; i < ratings.size() - 2; i++){
                sum += ratings.get(i).rated_value;
            }

            averageRating = sum;
            //fullReviewModel.averageRating = sum / (ratings.size() - 2);
            fullReviewObj.put("averageRating", sum);

            fullReviewObj.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {

                        tempFullReview = fullReviewObj;
                        Toast.makeText(context, "FullReview Saved", Toast.LENGTH_LONG).show();

                        ParseUser currentUser = ParseUser.getCurrentUser();
                        currentUser.setACL(new ParseACL(currentUser));
                        currentUser.saveInBackground();

                        ArrayList<ParseObject> allReviews = new ArrayList<>();

                        for (int i = 0; i < ratings.size() - 2; i++) {
                            Rating rating = ratings.get(i);
                            ParseObject temp = new ParseObject("Rating");

                            if(rating.parseObject == null)
                                temp = new ParseObject("Rating");
                            else
                                temp = rating.parseObject;


                            temp.put("userId", ParseUser.getCurrentUser());
                            temp.put("restaurantId", restaurant.parseObject);
                            temp.put("fullReviewId", fullReviewObj);

                            if(i == 17)
                                temp.put("rated_value", finalRating_value);
                            else
                                temp.put("rated_value", rating.rated_value);

                            temp.put("title", rating.title);
                            temp.put("comments", rating.comment);
                            allReviews.add(temp);
                        }

                        ParseObject.saveAllInBackground(allReviews, new SaveCallback(){
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Toast.makeText(context, "Review Saved", Toast.LENGTH_LONG).show();
                                    pd.dismiss();
                                    reloadFragment();
                                    /*Intent i = new Intent(getContext(), MainActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    context.startActivity(i);*/

                                } else {
                                    pd.dismiss();
                                    Toast.makeText(context, "Error Review saving", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    } else {
                        pd.dismiss();
                        Toast.makeText(context, "Error saving fullReview", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            pd.show();

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

            restaurantObj.saveInBackground(new SaveCallback(){
                @Override
                public void done(ParseException e){
                    if (e == null) {
                        restaurant.parseObject = restaurantObj;
                        Toast.makeText(context, "Restaurant Saved", Toast.LENGTH_LONG).show();
                        final ParseObject fullReviewObj = new ParseObject("FullReview");

                        fullReviewObj.put("restaurantId", restaurantObj);
                        fullReviewObj.put("userId", ParseUser.getCurrentUser());
                        fullReviewObj.put("delivery_time", fullReviewModel.delivery_time);
                        fullReviewObj.put("recommend_to_others", fullReviewModel.recommend_to_others);
                        fullReviewObj.put("comments", fullReviewModel.comments);
                        fullReviewObj.put("servers_name", fullReviewModel.servers_name);
                        fullReviewObj.put("overall_Rating", finalRating_value);
                        if(StorageHelper.parseImageFile != null)
                        fullReviewObj.put("Image", StorageHelper.parseImageFile);

                        int sum = 0;

                        for(int i = 0; i < ratings.size() - 2; i++){
                            sum += ratings.get(i).rated_value;
                        }

                        //fullReviewModel.averageRating = sum / (ratings.size() - 2);
                        fullReviewObj.put("averageRating", sum);

                        fullReviewObj.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Toast.makeText(context, "FullReview Saved", Toast.LENGTH_LONG).show();

                                    ParseUser currentUser = ParseUser.getCurrentUser();
                                    currentUser.setACL(new ParseACL(currentUser));
                                    currentUser.saveInBackground();

                                    ArrayList<ParseObject> allReviews = new ArrayList<>();

                                    for (int i = 0; i < ratings.size() - 2; i++) {
                                        Rating rating = ratings.get(i);
                                        ParseObject temp = new ParseObject("Rating");

                                        temp.put("userId", ParseUser.getCurrentUser());
                                        temp.put("restaurantId", restaurantObj);
                                        temp.put("fullReviewId", fullReviewObj);
                                        if(i == 17)
                                            temp.put("rated_value", finalRating_value);
                                        else
                                            temp.put("rated_value", rating.rated_value);
                                        temp.put("title", rating.title);
                                        temp.put("comments", rating.comment);

                                        allReviews.add(temp);
                                    }

                                    ParseObject.saveAllInBackground(allReviews, new SaveCallback(){
                                        @Override
                                        public void done(ParseException e){
                                            if (e == null) {
                                                Toast.makeText(context, "Review Saved", Toast.LENGTH_LONG).show();
                                                pd.dismiss();
                                                Intent i = new Intent(getContext(), MainActivity.class);
                                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                context.startActivity(i);
                                            } else {
                                                pd.dismiss();
                                                Toast.makeText(context, "Error Review saving", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                } else {
                                    pd.dismiss();
                                    Toast.makeText(context, "Error saving fullReview", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    } else {
                        pd.dismiss();
                        Toast.makeText(context, "Error saving restaurant", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }


    private void doWorkInstantZero(){

        pd.show();
        if(restaurant.parseObject != null){
            pd.show();
            Toast.makeText(context, "Restaurant Saved", Toast.LENGTH_LONG).show();
            final ParseObject fullReviewObj;
            if(fullReviewModel.parseObject == null)
                fullReviewObj = new ParseObject("FullReview");
            else
                fullReviewObj = fullReviewModel.parseObject;

            fullReviewObj.put("restaurantId", restaurant.parseObject);
            fullReviewObj.put("userId", ParseUser.getCurrentUser());
            fullReviewObj.put("delivery_time", "00");
            fullReviewObj.put("recommend_to_others", false);
            fullReviewObj.put("comments", instant_zero_comment_st);
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

                        Toast.makeText(context, "Instant Zero Saved!!!!", Toast.LENGTH_SHORT).show();
                        reloadFragment();
                        /*Intent i = new Intent(getContext(), MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);*/
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
                        fullReviewObj.put("comments", instant_zero_comment_st);
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
                                    Toast.makeText(context, "Instant Zero Saved!!!!", Toast.LENGTH_SHORT).show();
                                    reloadFragment();
                                    /*Intent i = new Intent(getContext(), MainActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(i);*/
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

    public void reloadFragment(){

        if(StorageHelper.backtoUserInvites){
            StorageHelper.backtoUserInvites = false;
            userInvites userinvites = new userInvites();
            android.support.v4.app.FragmentTransaction trans1 = ((AppCompatActivity) getContext()).getSupportFragmentManager()
                    .beginTransaction();
            trans1.replace(R.id.frame_container,userinvites).addToBackStack(null).commit();

        }else{
            StorageHelper.shareReview = true;
            StorageHelper.uiBlock = true;
            Bundle bundle = new Bundle();
            bundle.putString("reataurant_name_st" , this.restaurant.restaurant_name);

            bundle.putFloat("total_rating_stars_float" , finalRating_value);
            bundle.putString("total_rating_st" , String.valueOf(averageRating));
            bundle.putParcelable("fullReview" , tempFullReview);
            addReviewfrag addreview = new addReviewfrag();
            addreview.setArguments(bundle);
            android.support.v4.app.FragmentTransaction trans1 = ((AppCompatActivity) getContext()).getSupportFragmentManager()
                    .beginTransaction();
            trans1.replace(R.id.frame_container,addreview).addToBackStack(null).commit();
        }
    }
}
