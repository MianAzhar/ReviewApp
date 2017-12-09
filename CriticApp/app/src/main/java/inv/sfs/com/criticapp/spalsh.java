package inv.sfs.com.criticapp;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.parse.Parse;

public class spalsh extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalsh);
        getSupportActionBar().hide();


        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("Z79YDHNg46TBXsFvBtHZBy0BI05cc5R5N4AjyXdJ") // should correspond to APP_ID env variable
                .clientKey("m4Tb42TkP1XuxfviSaTy7XW5Lqq5d9t5FALraQN3")  // set explicitly unless clientKey is explicitly configured on Parse server
                .server("https://parseapi.back4app.com/").build());


        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable(){
            @Override
            public void run() {
                //Intent i = new Intent(getApplicationContext() , main_pg.class);
                //startActivity(i);
                Intent i = new Intent(getApplicationContext() , MainActivity.class);
                startActivity(i);

            }
         }, 2000L);

    }
}
