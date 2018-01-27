package inv.sfs.com.criticapp;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseInstallation;

/**
 * Created by applepc on 27/01/2018.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate(){
        super.onCreate();
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("Z79YDHNg46TBXsFvBtHZBy0BI05cc5R5N4AjyXdJ") // should correspond to APP_ID env variable
                .clientKey("m4Tb42TkP1XuxfviSaTy7XW5Lqq5d9t5FALraQN3")  // set explicitly unless clientKey is explicitly configured on Parse server
                .server("https://parseapi.back4app.com/").build());

        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("GCMSenderId", "906152729781");
        installation.saveInBackground();
        FacebookSdk.sdkInitialize(this);
    }


}
