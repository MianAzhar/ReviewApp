package inv.sfs.com.criticapp;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class spalsh extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalsh);
        getSupportActionBar().hide();

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
