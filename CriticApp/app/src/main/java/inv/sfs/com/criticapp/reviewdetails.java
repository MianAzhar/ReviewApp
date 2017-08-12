package inv.sfs.com.criticapp;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;

import java.util.ArrayList;

public class reviewdetails extends AppCompatActivity implements View.OnClickListener {


    ListView reviews_lv;
    public ArrayList<String> restaurant_name =new ArrayList<String>();
    public ArrayList<Float> rating_value=new ArrayList<Float>();
    public ArrayList<String> comments =new ArrayList<String>();
    LinearLayout container_layout, be_a_critic_lay;
    RatingBar rating_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviewdetails);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(this.getResources().getColor(R.color.app_basic_color)));
        getSupportActionBar().setTitle("Restaurant Name");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        for(int i =0; i < 7 ; i++){
            restaurant_name.add("Reviewer Name");
            rating_value.add((float) 3.5);
            comments.add("Good Taste a very nice place to be enjoyed it..");
        }

        be_a_critic_lay = (LinearLayout) findViewById(R.id.be_a_critic_lay);
        be_a_critic_lay.setOnClickListener(this);
        reviews_lv = (ListView) findViewById(R.id.reviews);
        container_layout = (LinearLayout) findViewById(R.id.container_layout);
        container_layout.getBackground().setAlpha(80);

        rating_bar = (RatingBar) findViewById(R.id.rating_bar);
        rating_bar.setRating((float) 3.5);
        reviews_lv = (ListView) findViewById(R.id.reviews);
        resturantreviewAdapter adapter = new resturantreviewAdapter(this, restaurant_name, rating_value, comments);
        reviews_lv.setAdapter(adapter);
        reviews_lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id){
                // TODO Auto-generated method stub
                String pos = String.valueOf(position);
                //Intent i = new Intent(getApplicationContext() , reviewdetails.class);
                //startActivity(i);
            }
        });
    }

    @Override
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
        if(v.getId() == be_a_critic_lay.getId()){
            Intent i = new Intent(this, addReview.class);
            startActivity(i);
        }
    }
}
