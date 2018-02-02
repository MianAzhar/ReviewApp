package inv.sfs.com.criticapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.parse.ParseFile;
import com.parse.ParseObject;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import inv.sfs.com.criticapp.Models.Notification;
import inv.sfs.com.criticapp.Models.Restaurant;

/**
 * Created by iosdev-1 on 8/16/17.
 */

public class StorageHelper {

    public static String is_admin_login;
    public static ArrayList<Restaurant> restaurants_generic_list =new ArrayList<Restaurant>();
    public static ArrayList<Restaurant> searched_restaurant_list =new ArrayList<Restaurant>();
    public static ArrayList<Restaurant> backup_restaurant_list = new ArrayList<Restaurant>();
    public static ArrayList<String> filters_list = new ArrayList<String>();
    public static Boolean filter_results = false;
    public static ParseFile parseImageFile = null;
    public static Bitmap bitmapImageFile;
    public static Boolean uiBlock = false;
    public static Boolean topTen = false;
    public static Boolean shareReview = false;
    public static ParseObject restaurant_TempObj = null;
    public static Notification notificationTempObj = null;
    public static Boolean backtoUserInvites = false;
    public static double latitude, longitude;
    public static boolean rest_searched = false;
    public static boolean alternative_login = false;
    public static String alternative_login_position;

    //As instant zero Is in ListView Now
    public static String reataurant_name_st;
    public static String total_rating_st;
    public static float total_rating_stars_float;


    public static ArrayList<Restaurant> sortforTop10(ArrayList<Restaurant> array){

        Restaurant temp = new Restaurant();
        for(int i = 0; i < array.size()-1; i++ ){

            for (int j = i + 1; j < array.size(); j++) {
                if(array.get(i).avgRating < array.get(j).avgRating){
                    temp = array.get(j);
                    array.set(j , array.get(i));
                    array.set(i ,temp);
                }
            }

        }
        return array;
    }



    public final static List<Integer> Colors = Arrays.asList(
            0xfffef1b5,
            0xffFC8EAC,
            0xffFFDF00,
            0xffFF7F00,
            0xffFF0000,
            0xff008000,
            0xff0000FF,
            0xffE5E4E2,
            0xffC0C0C0,
            0xffFFFF00,
            0xff5D8AA8,
            0xff2f70e1,
            0xff53d76a,
            0xffddaa3b,
            0xffe5000f,
            0xfffce6c9,
            0xffdeb697,
            0xff462d1d);

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Calendar toCalendar(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }


    public static String convet_month_shortform(String month){
        String month_number;

        if(month.equals("January")){
            return month_number = "Jan";
        }
        if(month.equals("February") ){
            return month_number = "Feb";
        }
        if(month.equals("March") ){
            return month_number = "Mar";
        }
        if(month.equals("April") ){
            return month_number = "Apr";
        }
        if(month.equals("May") ){
            return month_number = "May";
        }
        if(month.equals("June") ){
            return month_number = "Jun";
        }
        if(month.equals("July") ){
            return month_number = "Jul";
        }
        if(month.equals("August") ){
            return month_number = "Aug";
        }
        if(month.equals("September")){
            return month_number = "Sep";
        }
        if(month.equals("October")){
            return month_number = "Oct";
        }
        if(month.equals("November")){
            return month_number = "Nov";
        }
        if(month.equals("December") ){
            return month_number = "Dec";
        }
        return "dummy";
    }

    public static String convet_month(String month){
        String month_number;

        if(month.equals("0")){
            return month_number = "January";
        }
        if(month.equals("1") ){
            return month_number = "February";
        }
        if(month.equals("2") ){
            return month_number = "March";
        }
        if(month.equals("3") ){
            return month_number = "April";
        }
        if(month.equals("4") ){
            return month_number = "May";
        }
        if(month.equals("5") ){
            return month_number = "June";
        }
        if(month.equals("6") ){
            return month_number = "July";
        }
        if(month.equals("7") ){
            return month_number = "August";
        }
        if(month.equals("8")){
            return month_number = "September";
        }
        if(month.equals("9")){
            return month_number = "October";
        }
        if(month.equals("10")){
            return month_number = "November";
        }
        if(month.equals("11") ){
            return month_number = "December";
        }
        return "dummy";
    }

}
