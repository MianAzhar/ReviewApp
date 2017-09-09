package inv.sfs.com.criticapp;


import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.LOCATION_SERVICE;
import static inv.sfs.com.criticapp.R.drawable.map;


/**
 * A simple {@link Fragment} subclass.
 */
public class home extends Fragment implements View.OnClickListener, OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerClickListener {


    private static final Location TODO = null;
    private GoogleMap mMap;
    MarkerOptions marker;
    public static final int REQUEST_LOCATION_CODE = 99;
    public static RequestQueue myrequests;
    double latitude, longitude;
    TransparentProgressDialog pd;
    HelperFunctions helperfunctions;
    private static View view;
    LocationManager locationManager;
    public static ArrayList<restaurants> restaurants_list = new ArrayList<restaurants>();
    ImageView search_icon;
    EditText search_text;
    String search_text_st;
    LinearLayout search_lay;
    ImageView refresh_iv;
    JSONObject api_response;
    String next_pg_token = "";
    String searchText = null;
    int i = 0;

    public home(){
        //Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);

        if (view != null){
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try{
            view = inflater.inflate(R.layout.fragment_home, container, false);
        } catch (InflateException e){
        /* map is already there, just return view as it is */
        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        pd = new TransparentProgressDialog(getActivity(), R.drawable.loader);
        helperfunctions = new HelperFunctions();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkLocationPermission();
        }
        search_icon = (ImageView) getActivity().findViewById(R.id.search_icon);
        search_icon.setOnClickListener(this);
        search_text = (EditText) getActivity().findViewById(R.id.search_text);
        search_lay = (LinearLayout) getActivity().findViewById(R.id.search_lay);
        refresh_iv = (ImageView) getActivity().findViewById(R.id.refresh_iv);
        refresh_iv.setOnClickListener(this);
        search_lay.setOnClickListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        if(haveNetworkConnection()){
            getLastKnownLocation();
            if(StorageHelper.filter_results){
                StorageHelper.filter_results = false;
                getFilteredRestaurants();
            }else{
                getAllRestaurants();
            }
        }else{
            Toast.makeText(getActivity(), "Enable Internet to Use app", Toast.LENGTH_SHORT).show();
        }

        search_text.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search_text_st = search_text.getText().toString();
                    if(search_text_st.equals("")){
                        Toast.makeText(getActivity(), "Enter Text", Toast.LENGTH_SHORT).show();
                    }else{
                        searchText = search_text_st;
                        mMap.clear();
                        getAllRestaurants();
                    }
                    return true;
                }
                return false;
            }
        });
    }


    public void getLastKnownLocation(){
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers){
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
            }
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null){
                continue;
            }
            if (bestLocation == null
                    || l.getAccuracy() < bestLocation.getAccuracy()){
                // ALog.d("found best last known location: %s", l);
                bestLocation = l;
            }
        }
        if (bestLocation == null){
            Toast.makeText(getActivity(), "Cannot get your location", Toast.LENGTH_SHORT).show();
        } else {
            latitude = bestLocation.getLatitude();
            longitude = bestLocation.getLongitude();
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){

        menu.clear();
        inflater.inflate(R.menu.main, menu);
        menu.findItem(R.id.list).setVisible(true);
        getActivity().invalidateOptionsMenu();
        super.onCreateOptionsMenu(menu, inflater);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        /*if(mMap == null){
            getAllRestaurants();
        }*/
    }
    //---- String Response ------//
    public void getAllRestaurants(){
        restaurants_list.clear();
        pd.show();
        myrequests = Volley.newRequestQueue(getActivity());
        String url = helperfunctions.getUrl(latitude, longitude, next_pg_token, searchText);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response){
                try{
                    api_response = response;
                    PlotLocations(response);
                } catch (JSONException e){
                    Toast.makeText(getActivity(), "Some Error", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Toast.makeText(getActivity(), "Some Error", Toast.LENGTH_SHORT).show();
                pd.dismiss();
                Log.e("LOG", error.toString());
            }
        });
        myrequests.add(jsonObjectRequest);
    }

    //---- Get Filtered Restaurants-----//
    public void getFilteredRestaurants(){
        restaurants_list.clear();
        pd.show();
        myrequests = Volley.newRequestQueue(getActivity());
        String url = helperfunctions.getUrlFilter(latitude, longitude);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response){
                try{
                    PlotLocations(response);
                } catch (JSONException e){
                    Toast.makeText(getActivity(), "Some Error", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Toast.makeText(getActivity(), "Some Error", Toast.LENGTH_SHORT).show();
                pd.dismiss();
                Log.e("LOG", error.toString());
            }
        });
        myrequests.add(jsonObjectRequest);
    }

    public void PlotLocations(JSONObject response) throws JSONException {
        JSONArray results = response.getJSONArray("results");

        for (int i = 0; i < results.length(); i++) {
            JSONObject temp_Obj = results.getJSONObject(i);
            JSONObject geometry = temp_Obj.getJSONObject("geometry");
            JSONObject location = geometry.getJSONObject("location");

            restaurants temp_restaurant = new restaurants();
            temp_restaurant.latitude = Double.valueOf(location.getString("lat"));
            temp_restaurant.longitude = Double.valueOf(location.getString("lng"));
            temp_restaurant.ID = temp_Obj.getString("id");
            temp_restaurant.PlaceId = temp_Obj.getString("place_id");
            temp_restaurant.restaurant_name = temp_Obj.getString("name");
            temp_restaurant.icon_url = temp_Obj.getString("icon");
            temp_restaurant.vicinity = temp_Obj.getString("vicinity");
            restaurants_list.add(temp_restaurant);
        }
        PlotMap();
    }

    public void PlotMap(){
        for (int i = 0; i < restaurants_list.size(); i++){
            marker = new MarkerOptions().position(new LatLng(restaurants_list.get(i).latitude, restaurants_list.get(i).longitude)).title(restaurants_list.get(i).restaurant_name);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(restaurants_list.get(i).latitude, restaurants_list.get(i).longitude)));
            marker.icon(BitmapDescriptorFactory.fromBitmap(HelperFunctions.getMarkerBitmapFromView(R.drawable.marker_bg, getContext(), "10")));
            mMap.addMarker(marker);
        }
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12.0f));
        //StorageHelper.restaurants_generic_list.clear();
        StorageHelper.restaurants_generic_list = restaurants_list;
        pd.dismiss();
        //currentLatLong();
    }


    @Override
    public void onMapReady(GoogleMap googleMap){
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);

        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            //bulidGoogleApiClient();
             mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onClick(View v){
        if(v.getId() == search_icon.getId() || v.getId() == search_lay.getId()){
           search_text_st = search_text.getText().toString();
            if(search_text_st.equals("")){
                Toast.makeText(getActivity(), "Empty String", Toast.LENGTH_SHORT).show();
            }else{
                searchText = search_text_st;
                mMap.clear();
                getAllRestaurants();
             }
         }else if(v.getId() == refresh_iv.getId()){
            mMap.clear();
            searchText="";
            getLastKnownLocation();
            getAllRestaurants();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode)
        {
            case REQUEST_LOCATION_CODE:
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=  PackageManager.PERMISSION_GRANTED) {
                        Object client = null;
                        if(client == null){
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                    getLastKnownLocation();
                    getAllRestaurants();
                }
                else{
                    Toast.makeText(getContext(),"Permission Denied" , Toast.LENGTH_LONG).show();
                }
        }
    }

    public boolean checkLocationPermission(){
        if(ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)  != PackageManager.PERMISSION_GRANTED ) {
            if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION)){
                requestPermissions(new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
             } else {
               requestPermissions(new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
             }
            return false;
        }
        else
            return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                getActivity().finish();
                break;
            case R.id.list:
                allReviewsfrag allreviews = new allReviewsfrag();
                android.support.v4.app.FragmentTransaction trans1 = getActivity().getSupportFragmentManager().beginTransaction();
                trans1.replace(R.id.frame_container,allreviews).addToBackStack(null).commit();
                return  true;
        }
        return super.onOptionsItemSelected(item);
    }

   @Override
    public void onLocationChanged(Location location){
       double longitude = location.getLongitude();
       double latitude = location.getLatitude();
    }

    @Override
    public boolean onMarkerClick(Marker marker){HelperFunctions.getIndex(StorageHelper.restaurants_generic_list,marker.getTitle());
        marker.getTitle();
        Bundle bundle = new Bundle();
        bundle.putString("position" , String.valueOf(HelperFunctions.getIndex(StorageHelper.restaurants_generic_list,marker.getTitle())));
        reviewDetailsfrag reviewdetails = new reviewDetailsfrag();
        reviewdetails.setArguments(bundle);
        android.support.v4.app.FragmentTransaction trans1 = getActivity().getSupportFragmentManager().beginTransaction();
        trans1.replace(R.id.frame_container,reviewdetails).addToBackStack(null).commit();
        return false;
    }

    public boolean haveNetworkConnection(){
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }


}