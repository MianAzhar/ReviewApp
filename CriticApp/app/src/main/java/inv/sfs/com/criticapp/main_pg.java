package inv.sfs.com.criticapp;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class main_pg extends AppCompatActivity implements View.OnClickListener {


    Button be_a_critic_btn, critic_reviews_btn,admin_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pg);


        getSupportActionBar().hide();
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(this.getResources().getColor(R.color.app_basic_color)));
        //getSupportActionBar().setTitle("Dashboard");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        be_a_critic_btn = (Button) findViewById(R.id.be_a_critic);
        critic_reviews_btn = (Button) findViewById(R.id.critic_reviews);
        admin_btn = (Button) findViewById(R.id.admin);

        be_a_critic_btn.setOnClickListener(this);
        critic_reviews_btn.setOnClickListener(this);
        admin_btn.setOnClickListener(this);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v){
        if(v.getId() == critic_reviews_btn.getId()){
            Intent i = new Intent(main_pg.this , MainActivity.class);
            startActivity(i);
        }else if(v.getId() == be_a_critic_btn.getId()){
            Intent i = new Intent(this , login.class);
            startActivity(i);
        }else if(v.getId() == admin_btn.getId()){
            Intent i = new Intent(this , loginAdmin.class);
            startActivity(i);
        }
    }
}
