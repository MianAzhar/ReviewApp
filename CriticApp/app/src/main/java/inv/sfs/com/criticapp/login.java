package inv.sfs.com.criticapp;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class login extends AppCompatActivity implements View.OnClickListener {


    Button signup_btn, login_btn;
    PrefrencesHelper preference;
    EditText username_et , password_et;
    String username_st , password_st;
    TransparentProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(this.getResources().getColor(R.color.app_basic_color)));
        getSupportActionBar().setTitle("Login");

        preference = PrefrencesHelper.getInstance(this);
        pd = new TransparentProgressDialog(this, R.drawable.loader);
        username_et = (EditText) findViewById(R.id.username);
        password_et = (EditText) findViewById(R.id.password);


        signup_btn = (Button) findViewById(R.id.signup);
        signup_btn.setOnClickListener(this);

        login_btn = (Button) findViewById(R.id.login_btn);
        login_btn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == signup_btn.getId()){
            Intent i = new Intent(this , signUp.class);
            startActivity(i);
        }else if(v.getId() == login_btn.getId()){

            if(validateFields()){
                pd.show();
                ParseUser.logInInBackground(username_et.getText().toString(), password_et.getText().toString(), new LogInCallback() {
                    public void done(ParseUser user, ParseException e){
                        if (user != null){
                            pd.dismiss();
                            preference.setBoolObject("user_logged_in" , true);
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);
                        } else{
                            pd.dismiss();
                            Toast.makeText(login.this, "Invalid Username Password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }
    public boolean validateFields(){
        username_st = username_et.getText().toString();
        password_st = username_et.getText().toString();

        if(username_st.equals("")){
            username_et.setError("Enter username");
            return false;
        }else if(password_st.equals("")){
            password_et.setError("Enter username");
            return false;
        }else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(username_st).matches()) {
            username_et.setError("Invalid Email Address");
            return false;
        }
        return true;
    }
}
