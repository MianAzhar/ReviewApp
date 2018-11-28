package inv.sfs.com.criticapp;

import android.*;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import inv.sfs.com.criticapp.Models.Restaurant;

import static inv.sfs.com.criticapp.HelperFunctions.API_KEY;

public class AddLocationActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {

    Spinner restaurant_category_sp;
    TextView warningText;
    Button add_restaurant_btn, search_btn;
    EditText restaurant_name_et, restaurant_address_et;
    SupportMapFragment mapFragment;
    private GoogleMap mMap;

    LatLng geoPoints;

    TransparentProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(this.getResources().getColor(R.color.app_basic_color)));
        getSupportActionBar().setTitle("Add Restaurant");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pd = new TransparentProgressDialog(this, R.drawable.loader);

        add_restaurant_btn = findViewById(R.id.add_restaurant_btn);
        add_restaurant_btn.setOnClickListener(this);

        warningText = findViewById(R.id.warning_tv);

        search_btn = findViewById(R.id.search_restaurant_btn);
        search_btn.setOnClickListener(this);

        restaurant_name_et = findViewById(R.id.restaurant_name_et);
        restaurant_address_et = findViewById(R.id.address_et);

        restaurant_address_et.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                add_restaurant_btn.setVisibility(View.GONE);
                search_btn.setVisibility(View.VISIBLE);
                warningText.setVisibility(View.GONE);
            }
        });

        restaurant_category_sp = findViewById(R.id.restaurant_category_sp);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.restaurant_categories, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        restaurant_category_sp.setAdapter(adapter);

        mapFragment = (SupportMapFragment) this.getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    public LatLng getLocationFromAddress(String strAddress){

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress,5);

            Toast.makeText(this, "Addresses: " + address.size(), Toast.LENGTH_SHORT).show();

            if (address == null) {
                Toast.makeText(this, "No location", Toast.LENGTH_SHORT).show();
                return null;
            }
            Address location=address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

            return p1;
        } catch (Exception ex){
            Toast.makeText(this, "Error getting location", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public void getAddress(){

        pd.show();

        if(StorageHelper.requestQueue == null)
            StorageHelper.requestQueue = Volley.newRequestQueue(getApplicationContext());

        final String searchText = restaurant_address_et.getText().toString();

        String url = "https://maps.google.com/maps/api/geocode/json?address="
                + Uri.encode(searchText) + "&sensor=true&key="+API_KEY;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject jsonObject){
                pd.dismiss();
                try{
                    if (jsonObject.getString("status").equals("OK")) {
                        jsonObject = jsonObject.getJSONArray("results")
                                .getJSONObject(0);
                        jsonObject = jsonObject.getJSONObject("geometry");
                        jsonObject = jsonObject.getJSONObject("location");
                        String lat = jsonObject.getString("lat");
                        String lng = jsonObject.getString("lng");

                        LatLng position = new LatLng(Double.valueOf(lat),
                                Double.valueOf(lng));

                        MarkerOptions marker = new MarkerOptions().position(position).title(restaurant_name_et.getText().toString());
                        //marker.icon(BitmapDescriptorFactory.fromBitmap(HelperFunctions.getMarkerBitmapFromView(R.drawable.marker_bg, getContext(), String.valueOf(restaurants_list.get(i).avgRating))));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
                        mMap.addMarker(marker);

                        geoPoints = position;

                        search_btn.setVisibility(View.GONE);
                        add_restaurant_btn.setVisibility(View.VISIBLE);
                        warningText.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(getApplicationContext(), "Error in response", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e){
                    pd.dismiss();
                    Toast.makeText(getApplicationContext(), "Error getting location", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Toast.makeText(getApplication(), "Some Error 2", Toast.LENGTH_SHORT).show();

                Log.e("LOG", error.toString());
            }
        });
        StorageHelper.requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == add_restaurant_btn.getId()){
            if(!validateForm())
                return;

            Restaurant newRestaurant = new Restaurant();

            newRestaurant.restaurant_name = restaurant_name_et.getText().toString();
            newRestaurant.vicinity = restaurant_address_et.getText().toString();
            newRestaurant.category = restaurant_category_sp.getSelectedItem().toString();
            newRestaurant.latitude = geoPoints.latitude;
            newRestaurant.longitude = geoPoints.longitude;

            StorageHelper.searched_restaurant_list.add(newRestaurant);

            Intent returnIntent = new Intent();
            returnIntent.putExtra("result",StorageHelper.searched_restaurant_list.size() - 1);
            setResult(Activity.RESULT_OK,returnIntent);

            finish();

        } else if(view.getId() == search_btn.getId()){
            if(!validateForm())
                return;

            getAddress();
        }
    }



    public boolean validateForm(){
        String name = restaurant_name_et.getText().toString();

        if(name.length() < 1){
            restaurant_name_et.setError("Enter restaurant name");
            restaurant_name_et.requestFocus();

            return false;
        }

        String address = restaurant_address_et.getText().toString();

        if(address.length() < 1){
            restaurant_address_et.setError("Enter restaurant address");
            restaurant_address_et.requestFocus();

            return false;
        }

        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if(googleMap != null)
            mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            mMap.setMyLocationEnabled(true);
        }

        if(StorageHelper.restaurants_generic_list.size() != 0){
            mMap.animateCamera(CameraUpdateFactory.zoomTo(12.0f));
        }
    }
}
