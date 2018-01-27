package inv.sfs.com.criticapp;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class loginAdmin extends AppCompatActivity implements View.OnClickListener{

    Button signup_btn, login_btn;
    PrefrencesHelper preference;
    EditText email_et, pass_et;
    String username_st, password_st;
    TransparentProgressDialog pd;
    TextView auth_error;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_admin);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(this.getResources().getColor(R.color.app_basic_color)));
        getSupportActionBar().setTitle("Owner Login");
        signup_btn = (Button) findViewById(R.id.signup);
        signup_btn.setOnClickListener(this);
        login_btn = (Button) findViewById(R.id.login_btn);
        email_et = (EditText) findViewById(R.id.email_et);
        pass_et = (EditText) findViewById(R.id.pass_et);
        login_btn.setOnClickListener(this);
        preference = PrefrencesHelper.getInstance(this);
        pd = new TransparentProgressDialog(this, R.drawable.loader);
        auth_error = (TextView) findViewById(R.id.auth_error);
    }

    @Override
    public void onClick(View v){
        if(v.getId() == signup_btn.getId()){
            Intent i = new Intent(this , signupAdmin.class);
            startActivity(i);
        }else if(v.getId() == login_btn.getId()){

            if(validateFields()){
                pd.show();
                ParseUser.logInInBackground(email_et.getText().toString(), pass_et.getText().toString(), new LogInCallback() {
                    public void done(ParseUser user, ParseException e){
                        if (user != null){

                            if(user.get("restaurant") != null){
                                auth_error.setVisibility(View.GONE);
                                pd.dismiss();
                                preference.setBoolObject("admin_logged_in" , true);
                                Intent i = new Intent(loginAdmin.this , MainActivity.class);
                                startActivity(i);
                            }else{
                                pd.dismiss();
                                auth_error.setVisibility(View.VISIBLE);
                                ParseUser.logOut();

                            }


                        } else{
                            pd.dismiss();
                            Toast.makeText(loginAdmin.this, "Invalid Username Password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    public boolean validateFields(){
        username_st = email_et.getText().toString();
        password_st = pass_et.getText().toString();

        if(username_st.equals("")){
            email_et.setError("Enter Email");
            return false;
        }else if(password_st.equals("")){
            pass_et.setError("Enter Password");
            return false;
        }else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(username_st).matches()){
            email_et.setError("Invalid Email Address");
            return false;
        }
        return true;
    }
}
