package inv.sfs.com.criticapp;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by iosdev-1 on 8/18/17.
 */

public class PrefrencesHelper{

    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;
    private static final String PREF_NAME = "inv.sfs.com.criticapp";

    private static PrefrencesHelper singleton = new PrefrencesHelper();

    private PrefrencesHelper(){

    }

    public static PrefrencesHelper getInstance(Context context){
        if(sharedPreferences != null){
         }else{
            sharedPreferences =  context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            editor = sharedPreferences.edit();
        }
        return singleton;
    }

    public static void setStringObject(String KeyVal, String value){
        editor.putString(KeyVal , value);
        editor.commit();
    }

    public static String getStringObject(String keyVal){
         String val = sharedPreferences.getString(keyVal , "nill");
         return val;
    }

    public static void setBoolObject(String KeyVal, Boolean value){
        editor.putBoolean(KeyVal , value);
        editor.commit();
    }

    public static Boolean getBoolObject(String keyVal){
        Boolean val = sharedPreferences.getBoolean(keyVal , false);
        return val;
    }

}
