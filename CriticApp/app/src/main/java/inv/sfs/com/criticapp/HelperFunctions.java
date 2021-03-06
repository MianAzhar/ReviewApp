package inv.sfs.com.criticapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.ArrayList;

import inv.sfs.com.criticapp.Models.Restaurant;

/**
 * Created by iosdev-1 on 8/29/17.
 */

public class HelperFunctions{

    public static Integer PROXIMITY_RADIUS = 8047; //5 miles
    public static Integer PROXIMITY_RADIUS_ONE_MILE = 1000; //5 miles
    public static Integer PROXIMITY_RADIUS_25_MILES = 40234; //5 miles
    public static String API_KEY = "AIzaSyB34u6YAzpHBnicod6dsRERE9wZVr7JW4Y";
    public static String places ="restaurant|cafe|meal_takeaway|meal_delivery|food";

    public static double LEVEL_1 = 0;
    public static double LEVEL_2 = 0.111111111111111;
    public static double LEVEL_3 = 0.222222222222222;
    public static double LEVEL_4 = 0.333333333333334;
    public static double LEVEL_5 = 0.444444444444445;
    public static double LEVEL_6 = 0.5;
    public static double LEVEL_7 = 0.555555555555556;



    public static String getUrlFilter(double latitude,double longitude){

        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location="+latitude+","+longitude);
        googlePlaceUrl.append("&radius="+PROXIMITY_RADIUS);
        googlePlaceUrl.append("&rankby=distance");
        //googlePlaceUrl.append("&type="+"restaurant");
        googlePlaceUrl.append("&type="+filterString(StorageHelper.filters_list));
        googlePlaceUrl.append("&keyword="+filterString(StorageHelper.filters_list));
        //googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key="+API_KEY);//here key is diff then previous google map key so here use google places api key
        return googlePlaceUrl.toString();
    }


    public static String filterString(ArrayList<String> list){
        String result = "";
        for(int i = 0; i < list.size(); i++){
            result = result +list.get(i) +"|";
        }
        return result;
    }


    public static String getUrl(double latitude,double longitude, String next_pg_token, String searchText, Integer radius){

        if(searchText == null)
            searchText = "";

        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location="+latitude+","+longitude);
        googlePlaceUrl.append("&radius="+radius);
        //googlePlaceUrl.append("&type="+"restaurant");
        googlePlaceUrl.append("&type="+places);
        if(!searchText.isEmpty()){
            searchText = searchText.trim();
            searchText = searchText.replace(" ", "+");
            googlePlaceUrl.append("&keyword="+searchText);
        }else{
            //googlePlaceUrl.append("&type="+places);
            googlePlaceUrl.append("&keyword="+places);
        }

        //googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key="+API_KEY);//here key is diff then previous google map key so here use google places api key
        googlePlaceUrl.append("&pagetoken="+next_pg_token);//here key is diff then previous google map key so here use google places api key
        return googlePlaceUrl.toString();
    }

    public static Bitmap getMarkerBitmapFromView(@DrawableRes int resId, Context context, String rating){

        View customMarkerView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);
        LinearLayout markerImageView = (LinearLayout) customMarkerView.findViewById(R.id.parent_lay);
        markerImageView.setBackgroundResource(resId);

        TextView rating_value = (TextView) customMarkerView.findViewById(R.id.rating_value);
        rating_value.setText(rating);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }

    public static Integer getIndex(ArrayList<Restaurant> restaurants_list, String value){
        for(int i = 0; i < restaurants_list.size() ;i++){
            if(restaurants_list.get(i).restaurant_name.equals(value)){
                return i;
            }
         }
        return 00000;
    }

    public static ArrayList<Restaurant> searchLocation(ArrayList<Restaurant> restaurants_list, String value){
        ArrayList<Restaurant> restaurants_searched_list = new ArrayList<Restaurant>();
        for(int i = 0; i < restaurants_list.size() ;i++){
            if(restaurants_list.get(i).restaurant_name.equalsIgnoreCase(value)){
                restaurants_searched_list.add(restaurants_list.get(i));
            }
        }
        if(restaurants_searched_list.size() == 0){
            return null;
        }else{
            return restaurants_searched_list;
        }

    }

    public static double getSimpleRating(double rating){
        return Math.floor(rating);
    }

    public static double getRankedRating(double rating){
        if(ParseUser.getCurrentUser() == null)
            return rating;

        int rank = ParseUser.getCurrentUser().getNumber("rank") == null ? 1 : ParseUser.getCurrentUser().getInt("rank");

        switch (rank){
            case 1:
                return LEVEL_1 + rating;
            case 2:
                return LEVEL_2 + rating;
            case 3:
                return LEVEL_3 + rating;
            case 4:
                return LEVEL_4 + rating;
            case 5:
                return LEVEL_5 + rating;
            case 6:
                return LEVEL_6 + rating;
            case 7:
                return LEVEL_7 + rating;
            default:
                return rating;
        }
    }
}
