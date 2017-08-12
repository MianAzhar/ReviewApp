package inv.sfs.com.criticapp;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class allReviews extends AppCompatActivity {

    ListView reviews_list;
    public ArrayList<String> restaurant_name =new ArrayList<String>();
    public ArrayList<Float> rating_value=new ArrayList<Float>();
    public ArrayList<Integer> rating_count=new ArrayList<Integer>();
    public ArrayList<String> address =new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_reviews);

         getSupportActionBar().setBackgroundDrawable(new ColorDrawable(this.getResources().getColor(R.color.app_basic_color)));
         getSupportActionBar().setTitle("Reviews");
         getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        for(int i =0; i < 7 ; i++){
            restaurant_name.add("Restaurant Name");
            rating_value.add((float) 5);
            rating_count.add(5);
            address.add("Fri Chicks Wapda Town Round About Lahore Pakistan");
        }

        reviews_list = (ListView) findViewById(R.id.reviews_list);
        reviewslistAdapter adapter = new reviewslistAdapter(this, restaurant_name, rating_value,rating_count, address);
        reviews_list.setAdapter(adapter);
        reviews_list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id){
                // TODO Auto-generated method stub
                String pos = String.valueOf(position);

                Intent i = new Intent(getApplicationContext() , reviewdetails.class);
                startActivity(i);

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

}
