package inv.sfs.com.criticapp;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.twitter.sdk.android.core.Twitter;

import io.fabric.sdk.android.Fabric;

/**
 * Created by applepc on 27/01/2018.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate(){
        super.onCreate();
        Twitter.initialize(this);

        //Original One
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("Z79YDHNg46TBXsFvBtHZBy0BI05cc5R5N4AjyXdJ") // should correspond to APP_ID env variable
                .clientKey("m4Tb42TkP1XuxfviSaTy7XW5Lqq5d9t5FALraQN3")  // set explicitly unless clientKey is explicitly configured on Parse server
                .server("https://parseapi.back4app.com/").build());

        //Updated One..
        /*Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("LkuLFnRz0coIrVxKcCkGpcu7X8xHK54nVWpE3uMy") // should correspond to APP_ID env variable
                .clientKey("u4WHdrEJMouPS4nknfdoQIADtTAKPfrDkL3bLqqp")  // set explicitly unless clientKey is explicitly configured on Parse server
                .server("https://parseapi.back4app.com/").build());*/



        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("GCMSenderId", "906152729781");
        installation.saveInBackground();
        FacebookSdk.sdkInitialize(this);
        ParseFacebookUtils.initialize(this);
        Fabric.with(this, new Crashlytics());
    }


}
