package inv.sfs.com.criticapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.Calendar;
import java.util.List;

import inv.sfs.com.criticapp.Models.FullReviewModel;
import inv.sfs.com.criticapp.Models.Restaurant;

public class signupAdmin extends AppCompatActivity implements View.OnClickListener{

    public static int PAYMENT_CODE = 32;

    String customerId;
    int amountPaid;

    EditText ownername_et,email_et, phone_no_et, password_et, confirm_pass_et, location_address_et;
    String ownername_st, email_st, phone_no_st, password_st, confirm_pass_st, location_address_st;
    Button register_account_btn;
    Restaurant temp_Restaurant;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    TransparentProgressDialog pd;
    ParseObject obj;
    PrefrencesHelper preference;
    TextView error_tv;
    TextView terms_tv, terms_error_tv;
    CheckBox termscbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_admin);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(this.getResources().getColor(R.color.app_basic_color)));
        getSupportActionBar().setTitle("Owner Sign up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        error_tv = (TextView) findViewById(R.id.error_tv);
        ownername_et = (EditText) findViewById(R.id.ownername);
        email_et = (EditText) findViewById(R.id.email);
        phone_no_et = (EditText) findViewById(R.id.phone_no);
        password_et = (EditText) findViewById(R.id.password);
        confirm_pass_et = (EditText) findViewById(R.id.confirm_pass);
        location_address_et = (EditText) findViewById(R.id.location_address);
        terms_tv = (TextView) findViewById(R.id.terms_tv);
        terms_error_tv = findViewById(R.id.terms_error_tv);
        termscbox = findViewById(R.id.terms_checked);

        terms_tv.setOnClickListener(this);

        preference = PrefrencesHelper.getInstance(this);
        location_address_et = (EditText) findViewById(R.id.location_address);
        register_account_btn = (Button) findViewById(R.id.register_account_btn);
        register_account_btn.setOnClickListener(this);
        pd = new TransparentProgressDialog(this, R.drawable.loader);

        location_address_et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus){
                if (hasFocus) {
                    location_address_et.clearFocus();
                    Intent i = new Intent(getApplicationContext(), searchRestaurant.class);
                    startActivityForResult(i, 1);
                } else {
                    //Toast.makeText(getApplicationContext(), "Lost the focus", Toast.LENGTH_LONG).show();
                }
            }
        });
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
    public void onClick(View view){

        if(view.getId() == register_account_btn.getId()){

            if(validateFields()){
                pd.show();
                ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Restaurant");
                parseQuery.whereEqualTo("place_id", temp_Restaurant.PlaceId);
                parseQuery.setLimit(1000);

                parseQuery.findInBackground(new FindCallback<ParseObject>(){
                    @Override
                    public void done(List<ParseObject> list, ParseException e){
                        if (e == null){
                            if (!list.isEmpty()){
                                ParseObject parseObject = list.get(0);

                                if(parseObject.get("admin") == null){
                                    obj = parseObject;
                                    //createAccount();
                                    getPayment();
                                }else{
                                    error_tv.setVisibility(View.VISIBLE);
                                }
                                pd.dismiss();
                            } else {
                                obj = null;
                                //createRestaurant();
                                getPayment();
                                Log.d("Result", "Empty");
                                pd.dismiss();
                            }
                        } else {
                            Log.d("Result", "Wrong");
                            pd.dismiss();
                        }
                    }
                });
            }
        } else if(view.getId() == terms_tv.getId()){
            Intent i = new Intent(this, termsndConditions.class);
            startActivity(i);
        }
    }

    public void createAccount(){
        pd.show();
        final ParseUser user = new ParseUser();
        user.setUsername(email_et.getText().toString());
        user.setPassword(password_et.getText().toString());
        user.setEmail(email_et.getText().toString());
        user.put("phone" , phone_no_et.getText().toString());
        user.put("name" , ownername_et.getText().toString());
        user.put("restaurant" , obj);
        user.put("isAdmin" , true);
        user.put("customerId", customerId);
        user.put("rank", 1);

        Calendar cal = Calendar.getInstance();
        if(amountPaid == 10)
            cal.add(Calendar.MONTH, 1);
        else
            cal.add(Calendar.YEAR, 1);

        user.put("subscriptionExpiry", cal.getTime());

        user.signUpInBackground(new SignUpCallback(){
            public void done(ParseException e){
                if (e == null){
                    try {
                        obj.put("admin",user);
                        obj.save();
                    }catch (Exception e1){
                        Log.d("Result" , "Error saving restaurant");
                    }
                    pd.dismiss();
                    preference.setBoolObject("admin_logged_in" , true);
                    ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                    installation.put("user", ParseUser.getCurrentUser());
                    installation.saveInBackground();
                    Intent i = new Intent(signupAdmin.this , MainActivity.class);
                    startActivity(i);
                } else{
                    pd.dismiss();
                    email_et.setError("Email already exists");
                }
            }
        });
    }


    public void getPayment(){

        Intent i = new Intent(this, paymentPackages.class);
        i.putExtra("email", email_et.getText().toString());
        startActivityForResult(i, PAYMENT_CODE);

    }



    public void createRestaurant(){
        pd.show();
        //----- Add Restaurant ------//
        obj = new ParseObject("Restaurant");
        if(temp_Restaurant.icon_url != null)
            obj.put("icon_url", temp_Restaurant.icon_url);
        obj.put("name", temp_Restaurant.restaurant_name);
        obj.put("latitude", temp_Restaurant.latitude);
        obj.put("longitude", temp_Restaurant.longitude);
        if(temp_Restaurant.PlaceId != null)
            obj.put("place_id", temp_Restaurant.PlaceId);
        ParseGeoPoint geo = new ParseGeoPoint();
        geo.setLatitude(temp_Restaurant.latitude);
        geo.setLongitude(temp_Restaurant.longitude);
        obj.put("location" , geo);
        obj.put("vicinity" , temp_Restaurant.vicinity);
        if(temp_Restaurant.ID != null)
            obj.put("id" , temp_Restaurant.ID);

        obj.saveInBackground(new SaveCallback(){
            public void done(ParseException e){
                if (e == null){
                    pd.dismiss();
                    Log.d("Result" , "Restaurant Added");
                    createAccount();
                } else {
                    pd.dismiss();
                    Log.d("Result" , "Some Error While Adding");
                    e.printStackTrace();
                }
            }
        });
    }

    public boolean validateFields(){
        email_st = email_et.getText().toString();
        password_st = password_et.getText().toString();
        confirm_pass_st = confirm_pass_et.getText().toString();
        location_address_st = location_address_et.getText().toString();
        phone_no_st = phone_no_et.getText().toString();
        ownername_st = ownername_et.getText().toString();

        if(ownername_st.equals("")){
            ownername_et.setError("Enter Name");
            ownername_et.requestFocus();
            return false;
        } else if(email_st.equals("")){
            email_et.setError("Enter Email");
            email_et.requestFocus();
            return false;
        }else if(password_st.equals("")){
            password_et.setError("Enter Password");
            password_et.requestFocus();
            return false;
        }else if(confirm_pass_st.equals("")){
            confirm_pass_et.setError("Enter Confirm Password");
            confirm_pass_et.requestFocus();
            return false;
        }else if(location_address_st.equals("")){
            location_address_et.setError("Enter Location");
            return false;
        }else if(phone_no_st.equals("")){
            phone_no_et.setError("Enter Phone");
            phone_no_et.requestFocus();
            return false;
        }else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email_st).matches()){
            email_et.setError("Invalid Email Address");
            email_et.requestFocus();
            return false;
        }else if(!password_st.equals(confirm_pass_st)){
            confirm_pass_et.setError("Password Mismatch");
            confirm_pass_et.requestFocus();
            return false;
        }  else if(!termscbox.isChecked()){
            terms_error_tv.setVisibility(View.VISIBLE);
            return false;
        }

        terms_error_tv.setVisibility(View.GONE);
        return true;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        if (requestCode == 1){
            if(resultCode == Activity.RESULT_OK){
                error_tv.setVisibility(View.GONE);
                int position = data.getIntExtra("result" , 0);
                temp_Restaurant = new Restaurant();
                temp_Restaurant = StorageHelper.searched_restaurant_list.get(position);
                location_address_et.setText(temp_Restaurant.restaurant_name);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        } else if(requestCode == PAYMENT_CODE){
            if(resultCode == Activity.RESULT_OK){
                customerId = data.getStringExtra("customerId");
                amountPaid = data.getIntExtra("amount", 0);

                if(obj == null){
                    createRestaurant();
                } else {
                    createAccount();
                }
            }
        }
    }
}
