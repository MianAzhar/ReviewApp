package inv.sfs.com.criticapp;

import java.util.ArrayList;

import inv.sfs.com.criticapp.Models.Restaurant;

/**
 * Created by iosdev-1 on 8/16/17.
 */

public class StorageHelper {

    public static String is_admin_login;
    public static ArrayList<Restaurant> restaurants_generic_list =new ArrayList<Restaurant>();
    public static ArrayList<String> filters_list = new ArrayList<String>();
    public static Boolean filter_results = false;



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

}
