package inv.sfs.com.criticapp;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class submitReview extends AppCompatActivity implements View.OnClickListener {

    Button yes_btn, no_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_review);


        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(this.getResources().getColor(R.color.app_basic_color)));
        getSupportActionBar().setTitle("Add Review");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        yes_btn = (Button) findViewById(R.id.yes_btn);
        no_btn = (Button) findViewById(R.id.no_btn);
        yes_btn.setOnClickListener(this);
        no_btn.setOnClickListener(this);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.next:

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == yes_btn.getId()){
            yes_btn.setBackground(getResources().getDrawable(R.drawable.standard_btn));
            no_btn.setBackground(getResources().getDrawable(R.drawable.search_bg));
            yes_btn.setTextColor(getResources().getColor(R.color.white));
            no_btn.setTextColor(getResources().getColor(R.color.black));
        }else if(v.getId() == no_btn.getId()){
            no_btn.setBackground(getResources().getDrawable(R.drawable.standard_btn));
            yes_btn.setBackground(getResources().getDrawable(R.drawable.search_bg));
            no_btn.setTextColor(getResources().getColor(R.color.white));
            yes_btn.setTextColor(getResources().getColor(R.color.black));
        }
    }
}
