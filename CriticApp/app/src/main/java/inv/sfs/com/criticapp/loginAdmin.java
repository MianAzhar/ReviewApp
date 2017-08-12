package inv.sfs.com.criticapp;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class loginAdmin extends AppCompatActivity implements View.OnClickListener {

    Button signup_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_admin);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(this.getResources().getColor(R.color.app_basic_color)));
        getSupportActionBar().setTitle("Login");
        signup_btn = (Button) findViewById(R.id.signup);
        signup_btn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == signup_btn.getId()){
            Intent i = new Intent(this , signupAdmin.class);
            startActivity(i);

        }
    }
}
