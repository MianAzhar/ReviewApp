package inv.sfs.com.criticapp;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

public class filter extends AppCompatActivity implements View.OnClickListener{

    Button done_btn;
    CheckBox rest_cbox,cafe_cbox,meal_takeaway_cbox,meal_delivery_cbox;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Filter");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(this.getResources().getColor(R.color.app_basic_color)));

        done_btn = (Button) findViewById(R.id.done_btn);
        done_btn.setOnClickListener(this);

        rest_cbox = (CheckBox) findViewById(R.id.rest_cbox);
        cafe_cbox = (CheckBox) findViewById(R.id.cafe_cbox);
        meal_takeaway_cbox = (CheckBox) findViewById(R.id.meal_takeaway_cbox);
        meal_delivery_cbox = (CheckBox) findViewById(R.id.meal_delivery_cbox);
        StorageHelper.filters_list.clear();


        meal_delivery_cbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked){

                 if(isChecked){
                    if(StorageHelper.filters_list.indexOf("meal_delivery") == -1){
                        StorageHelper.filters_list.add("meal_delivery");
                    }
                }else{
                    if(StorageHelper.filters_list.indexOf("meal_delivery") != -1){
                        StorageHelper.filters_list.remove("meal_delivery");
                    }
                }
            }
        });

        meal_takeaway_cbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked){

                if(isChecked){
                    if(StorageHelper.filters_list.indexOf("meal_takeaway") == -1){
                        StorageHelper.filters_list.add("meal_takeaway");
                    }
                 }else{
                    if(StorageHelper.filters_list.indexOf("meal_takeaway") != -1){
                       StorageHelper.filters_list.remove("meal_takeaway");
                    }
                }
            }
        });

        cafe_cbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked){

                if(isChecked){
                    if(StorageHelper.filters_list.indexOf("cafe") == -1){
                        StorageHelper.filters_list.add("cafe");
                    }
                }else{
                    if(StorageHelper.filters_list.indexOf("cafe") != -1){
                        StorageHelper.filters_list.remove("cafe");
                    }
                }
            }
        });

        rest_cbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked){

                 if(isChecked){
                    if(StorageHelper.filters_list.indexOf("restaurant") == -1){
                        StorageHelper.filters_list.add("restaurant");
                    }
                }else{
                    if(StorageHelper.filters_list.indexOf("restaurant") != -1){
                        StorageHelper.filters_list.remove("restaurant");
                    }
                }

            }
        });
    }


    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        menu.clear();
        inflater.inflate(R.menu.main, menu);
        menu.findItem(R.id.done).setVisible(true);
        this.invalidateOptionsMenu();
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.done:
                StorageHelper.filter_results = true;
                Intent i = new Intent(this , MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v){
        if(v.getId() == done_btn.getId()){
            StorageHelper.filter_results = true;
            Intent i = new Intent(this , MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
    }
}
