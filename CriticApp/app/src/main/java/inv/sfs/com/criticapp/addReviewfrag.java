package inv.sfs.com.criticapp;


import android.*;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.widget.ShareDialog;
import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import inv.sfs.com.criticapp.Models.FullReviewModel;
import inv.sfs.com.criticapp.Models.Rating;
import inv.sfs.com.criticapp.Models.Restaurant;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class addReviewfrag extends Fragment implements View.OnClickListener {

    private static final int SELECT_PICTURE = 1, REQUEST_STORAGE_CODE = 100;
    Uri selectedImageUri;
    ParseFile parse_image_file = null;
    ListView add_review_lv;
    Button instant_btn,submit_instant_zero_btn;
    public ArrayList<String> review_against =new ArrayList<String>();
    public ArrayList<String> comments =new ArrayList<String>();
    Dialog dialog, helpDialog;
    Integer restaurantPosition;
    EditText instant_zero_comment;
    Restaurant restaurant;
    FullReviewModel fullReviewModel;
    TransparentProgressDialog pd;
    Boolean editMode = false;
    ParseObject fullReviewObject = null;
    public ArrayList<Rating> editRatingObject = new ArrayList<Rating>();
    addReviewsAdapter adapter;
    LinearLayout view_review_extra_lay,share_lay;
    public String reataurant_name_st;
    public String total_rating_st;
    public float total_rating_stars_float;
    TextView restaurant_name_tv,criticScore_tv;
    RatingBar rating_bar_rb;


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

        StorageHelper.bitmapImageFile = null;
        StorageHelper.parseImageFile = null;

        review_against.add("null");
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
        comments.add("null");
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

        share_lay = (LinearLayout) getView().findViewById(R.id.share_lay);
        restaurant_name_tv = (TextView) getView().findViewById(R.id.restaurant_name);
        criticScore_tv = (TextView) getView().findViewById(R.id.criticScore);
        rating_bar_rb = (RatingBar) getView().findViewById(R.id.rating_bar);

        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.customdialogue);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        submit_instant_zero_btn = (Button) dialog.getWindow().findViewById(R.id.submit_instant_zero_btn);
        submit_instant_zero_btn.setOnClickListener(this);
        pd = new TransparentProgressDialog(getContext(), R.drawable.loader);
        instant_zero_comment = (EditText) dialog.getWindow().findViewById(R.id.instant_zero_comment);
        view_review_extra_lay = (LinearLayout) getView().findViewById(R.id.view_review_extra_lay);
        instant_btn = (Button) getView().findViewById(R.id.instant_btn);
        instant_btn.setOnClickListener(this);
        share_lay.setOnClickListener(this);

        try{
            reataurant_name_st = getArguments().getString("reataurant_name_st");
            total_rating_st  = getArguments().getString("total_rating_st");
            total_rating_stars_float = getArguments().getFloat("total_rating_stars_float");


            StorageHelper.reataurant_name_st = reataurant_name_st;
            StorageHelper.total_rating_st = total_rating_st;
            StorageHelper.total_rating_stars_float = total_rating_stars_float;

            restaurant_name_tv.setText(reataurant_name_st);
            criticScore_tv.setText(total_rating_st);
            rating_bar_rb.setRating(total_rating_stars_float);
            getArguments().get("fullReview");
        }catch (Exception e){
        }

        if(StorageHelper.shareReview){
            //StorageHelper.shareReview = false;
            share_lay.setVisibility(View.GONE);
        }

        if(!StorageHelper.uiBlock){
            helpDialog = new Dialog(getActivity());
            //helpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            helpDialog.setTitle("Scores");
            helpDialog.setContentView(R.layout.help_dialog);
            Window window1 = dialog.getWindow();
            window1.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            helpDialog.show();
        }

        if(StorageHelper.uiBlock){
            instant_btn.setVisibility(View.GONE);
            view_review_extra_lay.setVisibility(View.GONE);
        }else{
            instant_btn.setVisibility(View.GONE);
            view_review_extra_lay.setVisibility(View.GONE);
        }

        if(getArguments().getString("position") != null){
            restaurantPosition = Integer.valueOf(getArguments().getString("position"));
        }else{
            restaurantPosition = - 1;
            fullReviewObject = getArguments().getParcelable("fullReview");
        }

        if(restaurantPosition == -1){
            getFullRatingRestaurant();
        }else{
            checkforEditReview();
        }

    }

    @Override
    public void onResume(){
        Log.e("DEBUG", "onResume of HomeFragment");
        if(adapter != null){
            adapter.notifyDataSetChanged();
        }
        super.onResume();
    }

    public void checkforEditReview(){

        int reviewsListSize = StorageHelper.restaurants_generic_list.get(restaurantPosition).reviews.size();
        for(int i = 0; i < reviewsListSize; i++){
            ParseObject userStoredId = (ParseObject) StorageHelper.restaurants_generic_list.get(restaurantPosition).reviews.get(i).get("userId");
            String currentUserId = ParseUser.getCurrentUser().getObjectId();

            if(userStoredId.getObjectId().equals(currentUserId)){
                Date date = new Date();
                ParseObject fullReview = StorageHelper.restaurants_generic_list.get(restaurantPosition).reviews.get(i);
                Date createdAt = fullReview.getCreatedAt();

                long diff = date.getTime() - createdAt.getTime();

                long days = diff / (24 * 60 * 60 * 1000);

                if(days > 1){
                    continue;
                } else {
                    editMode = true;
                    fullReviewObject = (ParseObject) StorageHelper.restaurants_generic_list.get(restaurantPosition).reviews.get(i);
                    Toast.makeText(getContext(), "Edit Mode", Toast.LENGTH_SHORT).show();
                    getFullRatingRestaurant();
                    return;
                }
            }
        }
        populateAdapter();
    }


    public void getFullRatingRestaurant(){

        pd.show();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Rating");
        query.whereEqualTo("fullReviewId", fullReviewObject);
        query.findInBackground(new FindCallback<ParseObject>(){
            @Override
            public void done(List<ParseObject> list, ParseException e){
                if (e == null) {
                    if (!list.isEmpty()){

                        Rating tempObject;
                        for (int i = 0; i < list.size(); i++){
                            tempObject = new Rating();
                            ParseObject parseObject = list.get(i);
                            tempObject.parseObject = parseObject;
                            tempObject.comment = parseObject.getString("comments");
                            tempObject.rated_value = parseObject.getNumber("rated_value").doubleValue();
                            tempObject.title = parseObject.get("title").toString();
                            editRatingObject.add(tempObject);
                        }

                        tempObject = new Rating();
                        tempObject.title = "Delivery Time";
                        tempObject.comment = "Comment: 30min etc.";
                        editRatingObject.add(tempObject);

                        tempObject = new Rating();
                        tempObject.title = "null";
                        tempObject.comment = "null";
                        editRatingObject.add(tempObject);

                        pd.dismiss();
                        populateAdapter();
                    }else{
                        Log.d("Result" , "Empty Class");
                        populateAdapter();
                        pd.dismiss();
                    }
                } else {
                    Log.d("Result" , "Some Exception");
                    pd.dismiss();
                }
            }
        });
    }

    public void populateAdapter(){
        add_review_lv = (ListView) getView().findViewById(R.id.add_review_lv);

        ArrayList<Rating> newList = new ArrayList<>();

        if(editRatingObject.size() > 0){
            for(int i = 0; i < review_against.size(); i++){
                String str = review_against.get(i);

                for(int j = 0; j < editRatingObject.size(); j++){
                    Rating temp = editRatingObject.get(j);

                    if(temp.title.equals(str)){
                        newList.add(temp);
                        break;
                    }
                }
            }
        }

        if(restaurantPosition == -1){
            Restaurant rest = new Restaurant();
            ParseObject obj = fullReviewObject.getParseObject("restaurantId");
            rest.avgRating = obj.getInt("avgRating");
            rest.restaurant_name = obj.getString("name");
            rest.parseObject = obj;

            adapter = new addReviewsAdapter(getActivity(), review_against,comments, rest,true,newList,fullReviewObject, this);

        }else{
            adapter = new addReviewsAdapter(getActivity(), review_against,comments, StorageHelper.restaurants_generic_list.get(restaurantPosition),editMode,newList,fullReviewObject, this);
        }

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

    public boolean hasPermission(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(getActivity(), "External Storage permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_STORAGE_CODE);
            }

            return false;
        } else {
            // Permission has already been granted
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode)
        {
            case REQUEST_STORAGE_CODE:
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(getContext(),"Permission Granted. Now you can share your score." , Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getContext(),"Permission Denied" , Toast.LENGTH_LONG).show();
                }
        }
    }

    public Uri getImageUri(){
        if(!hasPermission())
            return null;

        View rowView = add_review_lv.getChildAt(0);
        View shareView = rowView.findViewById(R.id.shareLyout);
        View appLinkView = rowView.findViewById(R.id.appLinkLayout);
        shareView.setVisibility(View.GONE);
        //appLinkView.setVisibility(View.VISIBLE);
        rowView.setDrawingCacheEnabled(true);
        rowView.buildDrawingCache();
        Bitmap cache = rowView.getDrawingCache();
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(Environment.getExternalStorageDirectory() + "/criticImage.png");
            cache.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

            shareView.setVisibility(View.VISIBLE);
            appLinkView.setVisibility(View.GONE);

            File media = new File(Environment.getExternalStorageDirectory() + "/criticImage.png");
            //Uri uri = Uri.fromFile(media);

            Uri uri = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".inv.sfs.com.criticapp.provider", media);

            return uri;

        } catch (Exception e) {
            shareView.setVisibility(View.VISIBLE);
            // TODO: handle exception
            e.printStackTrace();
            return null;
        } finally {
            rowView.destroyDrawingCache();
        }
    }

    public void convertViewToImage(){
        if(!hasPermission())
            return;

        if(!isAppInstalled("com.instagram.android")){
            Toast.makeText(getContext(), "Instagram is not installed", Toast.LENGTH_SHORT).show();
            return;
        }

        View rowView = add_review_lv.getChildAt(0);
        View shareView = rowView.findViewById(R.id.shareLyout);
        View appLinkView = rowView.findViewById(R.id.appLinkLayout);
        shareView.setVisibility(View.GONE);
        //appLinkView.setVisibility(View.VISIBLE);
        rowView.setDrawingCacheEnabled(true);
        rowView.buildDrawingCache();
        Bitmap cache = rowView.getDrawingCache();
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(Environment.getExternalStorageDirectory() + "/criticImage.png");
            cache.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

            shareView.setVisibility(View.VISIBLE);
            appLinkView.setVisibility(View.GONE);

            File media = new File(Environment.getExternalStorageDirectory() + "/criticImage.png");
            //Uri uri = Uri.fromFile(media);

            Uri uri = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".inv.sfs.com.criticapp.provider", media);

            Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setType("image/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM,uri);
            shareIntent.putExtra(Intent.EXTRA_TITLE, "The Critics View");
            shareIntent.setPackage("com.instagram.android");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(shareIntent);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            rowView.destroyDrawingCache();
        }

    }

    private boolean isAppInstalled(String packageName) {
        PackageManager pm = getActivity().getPackageManager();
        boolean installed = false;
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){

        menu.clear();
        inflater.inflate(R.menu.main, menu);
        menu.findItem(R.id.next).setVisible(false);
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
        }else if(v.getId() == share_lay.getId()){

            ShareLinkContent content = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(" https://play.google.com/store/apps/details?id=inv.sfs.com.disasterresourse"))
                    .setQuote(ParseUser.getCurrentUser().get("name") + " reviewed " + reataurant_name_st + " and gave it a " +
                            total_rating_st +" score.")
                    .build();

            ShareDialog shareDialog = new ShareDialog(getActivity());
            shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);
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
                        Intent i = new Intent(getContext(), MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
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
                                    Intent i = new Intent(getContext(), MainActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(i);
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
