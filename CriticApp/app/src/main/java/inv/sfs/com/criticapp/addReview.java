package inv.sfs.com.criticapp;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class addReview extends AppCompatActivity implements View.OnClickListener {

    ListView add_review_lv;
    Button instant_btn;
    public ArrayList<String> review_against =new ArrayList<String>();
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(this.getResources().getColor(R.color.app_basic_color)));
        getSupportActionBar().setTitle("Add Review");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            review_against.add("Service:");
            review_against.add("Location:");
            review_against.add("Main Greeting:");
            review_against.add("Sitting:");
            review_against.add("Wait Time:");
            review_against.add("Table Quality:");
            review_against.add("Menu Selection:");
            review_against.add("Taste Of Food:");


        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.customdialogue);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        instant_btn = (Button) findViewById(R.id.instant_btn);
        instant_btn.setOnClickListener(this);

        add_review_lv = (ListView) findViewById(R.id.add_review_lv);
        addReviewsAdapter adapter = new addReviewsAdapter(this, review_against);
        add_review_lv.setAdapter(adapter);
        add_review_lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id){
                // TODO Auto-generated method stub
                String pos = String.valueOf(position);

            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        menu.clear();
        inflater.inflate(R.menu.main, menu);
        menu.findItem(R.id.next).setVisible(true);
        this.invalidateOptionsMenu();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.next:
                Intent i = new Intent(this , submitReview.class);
                startActivity(i);
                return true;
         }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == instant_btn.getId()){
            dialog.show();
        }
    }
}
