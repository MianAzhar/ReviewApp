package inv.sfs.com.criticapp;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.LogInCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.Arrays;
import java.util.List;

import static inv.sfs.com.criticapp.StorageHelper.latitude;
import static inv.sfs.com.criticapp.StorageHelper.longitude;

public class login extends AppCompatActivity implements View.OnClickListener {


    Button signup_btn, login_btn, facebook_btn;
    PrefrencesHelper preference;
    EditText username_et , password_et;
    String username_st , password_st;
    TransparentProgressDialog pd;
    TextView forgotPassword;


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

        forgotPassword = findViewById(R.id.forgot_password_tv);
        forgotPassword.setOnClickListener(this);

        signup_btn = (Button) findViewById(R.id.signup);
        signup_btn.setOnClickListener(this);

        login_btn = (Button) findViewById(R.id.login_btn);
        login_btn.setOnClickListener(this);

        facebook_btn = findViewById(R.id.login_facebook_btn);
        facebook_btn.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
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

                            if(StorageHelper.latitude != 0){

                                ParseGeoPoint geoPoint = new ParseGeoPoint(StorageHelper.latitude, StorageHelper.longitude);
                                ParseUser currentUser = ParseUser.getCurrentUser();

                                currentUser.put("lastknownlocation",geoPoint);

                                ParseACL acl = new ParseACL();
                                acl.setPublicReadAccess(true);
                                acl.setWriteAccess(currentUser,true);
                                currentUser.setACL(acl);

                                currentUser.saveInBackground();
                            }

                            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                            installation.put("user", ParseUser.getCurrentUser());
                            installation.saveInBackground();

                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                        } else{
                            pd.dismiss();
                            Toast.makeText(login.this, "Invalid Username Password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } else if(v.getId() == facebook_btn.getId()){
            final List<String> permissions = Arrays.asList("public_profile", "email");

            pd.show();

            ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException err) {
                    if (user == null) {
                        pd.dismiss();
                        Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                    } else if (user.isNew()) {
                        Log.d("MyApp", "User signed up and logged in through Facebook!");
                        getUserDetailFromFB();
                    } else {
                        Log.d("MyApp", "User logged in through Facebook!");
                        pd.dismiss();

                        preference.setBoolObject("user_logged_in" , true);

                        if(StorageHelper.latitude != 0){

                            ParseGeoPoint geoPoint = new ParseGeoPoint(StorageHelper.latitude, StorageHelper.longitude);
                            ParseUser currentUser = ParseUser.getCurrentUser();

                            currentUser.put("lastknownlocation",geoPoint);

                            ParseACL acl = new ParseACL();
                            acl.setPublicReadAccess(true);
                            acl.setWriteAccess(currentUser,true);
                            currentUser.setACL(acl);

                            currentUser.saveInBackground();
                        }

                        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                        installation.put("user", ParseUser.getCurrentUser());
                        installation.saveInBackground();

                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    }
                }
            });
        } else if(v.getId() == forgotPassword.getId()){
            Intent i = new Intent(this , ForgotPasswordActivity.class);
            startActivity(i);
        }
    }

    void getUserDetailFromFB(){
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),new GraphRequest.GraphJSONObjectCallback(){
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                ParseUser user = ParseUser.getCurrentUser();
                try{

                    user.put("name", object.getString("name"));
                    if(object.getString("email") != null)
                        user.put("email", object.getString("email"));
                }catch(JSONException e){
                    e.printStackTrace();
                }

                if(StorageHelper.latitude != 0){
                    ParseGeoPoint geoPoint = new ParseGeoPoint(StorageHelper.latitude, StorageHelper.longitude);

                    user.put("lastknownlocation",geoPoint);

                    ParseACL acl = new ParseACL();
                    acl.setPublicReadAccess(true);
                    acl.setWriteAccess(user,true);
                    user.setACL(acl);
                }

                user.put("rank", 1);

                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null){
                            pd.dismiss();
                            preference.setBoolObject("user_logged_in" , true);

                            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                            installation.put("user", ParseUser.getCurrentUser());
                            installation.saveInBackground();

                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                        } else {
                            pd.dismiss();
                            Toast.makeText(login.this, "Error: Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields","name,email");
        request.setParameters(parameters);
        request.executeAsync();
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
