package inv.sfs.com.criticapp;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class signUp extends AppCompatActivity implements View.OnClickListener {


    EditText username_et, email_et, phone_no_et, password_et, confirm_pass_et;
    String username_st, email_st, phone_no_st, password_st, confirm_pass_st;
    Button register_btn;
    TransparentProgressDialog pd;
    PrefrencesHelper preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(this.getResources().getColor(R.color.app_basic_color)));
        getSupportActionBar().setTitle("Sign up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preference = PrefrencesHelper.getInstance(this);
        pd = new TransparentProgressDialog(this,R.drawable.refresh_black);
        username_et = (EditText) findViewById(R.id.username);
        email_et = (EditText) findViewById(R.id.email);
        phone_no_et = (EditText) findViewById(R.id.phone_no);
        password_et = (EditText) findViewById(R.id.password);
        confirm_pass_et = (EditText) findViewById(R.id.confirm_pass);

        register_btn = (Button) findViewById(R.id.register);
        register_btn.setOnClickListener(this);

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
    public void onClick(View v) {

        if(v.getId() == register_btn.getId()){
            if(validateFields()){
                pd.show();
                ParseUser user = new ParseUser();
                user.setUsername(email_et.getText().toString());
                user.setPassword(password_et.getText().toString());
                user.setEmail(email_et.getText().toString());
                user.put("phone" , phone_no_et.getText().toString());
                user.put("name" , username_et.getText().toString());
                user.signUpInBackground(new SignUpCallback(){
                    public void done(ParseException e){
                        if (e == null){
                            pd.dismiss();
                            preference.setBoolObject("user_logged_in" , true);
                            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                            installation.put("user", ParseUser.getCurrentUser());
                            installation.saveInBackground();
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);
                        } else{
                            pd.dismiss();
                            email_et.setError("Username already Exists");
                        }
                    }
                });
            }
        }


    }

    public boolean validateFields(){
        username_st = username_et.getText().toString();
        email_st = email_et.getText().toString();
        phone_no_st = phone_no_et.getText().toString();
        password_st = password_et.getText().toString();
        confirm_pass_st = confirm_pass_et.getText().toString();

        if(username_st.equals("")){
            username_et.setError("Enter Username");
            return false;
        }else if(email_st.equals("")){
            email_et.setError("Enter Email");
            return false;
        }else if(phone_no_st.equals("")){
            phone_no_et.setError("Enter PhoneNumber");
            return false;
        }else if(password_st.equals("")){
            password_et.setError("Enter PhoneNumber");
            return false;
        }else if(confirm_pass_st.equals("")){
            confirm_pass_et.setError("Enter PhoneNumber");
            return false;
        }else if(!password_st.equals(confirm_pass_st)){
            confirm_pass_et.setError("Password Mismatch");
            return false;
        }else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email_st).matches()) {
            email_et.setError("Invalid Email Address");
            return false;
        }
        return true;
    }



}
