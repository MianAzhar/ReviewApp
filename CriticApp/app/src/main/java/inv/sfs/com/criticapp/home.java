package inv.sfs.com.criticapp;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import inv.sfs.com.criticapp.Models.Restaurant;

import static android.content.Context.LOCATION_SERVICE;


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
    public static ArrayList<Restaurant> restaurants_list = new ArrayList<Restaurant>();
    ImageView search_icon;
    EditText search_text;
    String search_text_st;
    LinearLayout search_lay;
    ImageView refresh_iv;
    JSONObject api_response;
    String next_pg_token = "";
    String searchText = null;
    int i = 0;
    Boolean back_pressed = false;
    private MarshMallowPermission marshMallowPermission;
    private boolean shouldFilter = false;
    SupportMapFragment mapFragment;
    Dialog dialog;
    Button indian_btn,chinese_btn, singaporean_btn, western_btn, malay_btn, halal_btn, hotpot_btn,
    bbq_btn, thai_btn, fruits_btn, korean_btn, vietnamese_btn;
    TextView skip_tv, next_tv;
    ArrayList<String> selectedRestaurants = new ArrayList<String>();


    public home(){

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


        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("The Critics View");

        searchText = "";

        marshMallowPermission = new MarshMallowPermission(getActivity());
        pd = new TransparentProgressDialog(getActivity(), R.drawable.loader);
        helperfunctions = new HelperFunctions();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkLocationPermission();
            if(marshMallowPermission.checkPermissionForExternalStorage()){
                marshMallowPermission.requestPermissionForExternalStorage(MarshMallowPermission.EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE_BY_LOAD_PROFILE);
            }
        }


        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.selectresttype);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCanceledOnTouchOutside(false);
        search_icon = (ImageView) getActivity().findViewById(R.id.search_icon);
        search_icon.setOnClickListener(this);
        search_text = (EditText) getActivity().findViewById(R.id.search_text);
        search_lay = (LinearLayout) getActivity().findViewById(R.id.search_lay);
        refresh_iv = (ImageView) getActivity().findViewById(R.id.refresh_iv);
        indian_btn = (Button) dialog.findViewById(R.id.indian);
        chinese_btn = (Button) dialog.findViewById(R.id.chinese);
        singaporean_btn = (Button) dialog.findViewById(R.id.singaporian);
        western_btn = (Button) dialog.findViewById(R.id.western);
        malay_btn = (Button) dialog.findViewById(R.id.malay);
        halal_btn = (Button) dialog.findViewById(R.id.halal);
        hotpot_btn = (Button) dialog.findViewById(R.id.hotpot);
        bbq_btn = (Button) dialog.findViewById(R.id.bbq);
        fruits_btn = (Button) dialog.findViewById(R.id.fruits);
        korean_btn = (Button) dialog.findViewById(R.id.korean);
        vietnamese_btn = (Button) dialog.findViewById(R.id.vietnamese);
        thai_btn = (Button) dialog.findViewById(R.id.thai);
        skip_tv = (TextView) dialog.findViewById(R.id.skip_tv);
        next_tv = (TextView) dialog.findViewById(R.id.next_tv);

        refresh_iv.setOnClickListener(this);
        search_lay.setOnClickListener(this);
        indian_btn.setOnClickListener(this);
        chinese_btn.setOnClickListener(this);
        singaporean_btn.setOnClickListener(this);
        western_btn.setOnClickListener(this);
        malay_btn.setOnClickListener(this);
        halal_btn.setOnClickListener(this);
        hotpot_btn.setOnClickListener(this);
        bbq_btn.setOnClickListener(this);
        thai_btn.setOnClickListener(this);
        fruits_btn.setOnClickListener(this);
        korean_btn.setOnClickListener(this);
        vietnamese_btn.setOnClickListener(this);
        skip_tv.setOnClickListener(this);
        next_tv.setOnClickListener(this);

        mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        //Check if Restaurants are already there Or Not

        if(StorageHelper.restaurants_generic_list.size() == 0 || StorageHelper.shouldReloadData) {
            if (haveNetworkConnection()) {
                getLastKnownLocation();
                if (StorageHelper.filter_results) {
                    StorageHelper.filter_results = false;
                    getFilteredRestaurants();
                } else {
                    getAllRestaurants();
                }
            } else {
                Toast.makeText(getActivity(), "Enable Internet to Use app", Toast.LENGTH_SHORT).show();
            }
        } else if(StorageHelper.shouldReloadData){

        }


        search_text.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    SearchRestaurants();
                    return true;
                }
                return false;
            }
        });


        this.getView().setFocusableInTouchMode(true);
        this.getView().requestFocus();
        this.getView().setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event){

                //This is the filter
                if (event.getAction()!=KeyEvent.ACTION_DOWN)
                    return true;
                switch (keyCode){
                    case KeyEvent.KEYCODE_BACK :
                        // Toast.makeText(getActivity(), "1", Toast.LENGTH_SHORT).show();
                        backPressed();
                        break;
                    case KeyEvent.KEYCODE_2 :
                        //Toast.makeText(getActivity(), "2", Toast.LENGTH_SHORT).show();
                        break;
                    case KeyEvent.KEYCODE_3 :
                        //Toast.makeText(getActivity(), "3", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        }
        );

       // dialog.show();
    }

    private void backPressed(){
        if(!back_pressed){
            Toast.makeText(getActivity(), "Press Again To Exit", Toast.LENGTH_SHORT).show();
            back_pressed = true;
            android.os.Handler mHandler = new android.os.Handler();
            mHandler.postDelayed(new Runnable(){
                @Override
                public void run() {
                    back_pressed = false;
                }
            }, 2000L);
        }else{
            getActivity().finishAffinity();
        }
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

            StorageHelper.latitude = latitude;
            StorageHelper.longitude = longitude;
            /*
            if(mMap != null){
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(StorageHelper.latitude, StorageHelper.longitude)));
            }
            */

            if(ParseUser.getCurrentUser() != null){

                ParseGeoPoint geoPoint = new ParseGeoPoint(latitude, longitude);
                ParseUser currentUser = ParseUser.getCurrentUser();

                currentUser.put("lastknownlocation",geoPoint);

                ParseACL acl = new ParseACL();
                acl.setPublicReadAccess(true);
                acl.setWriteAccess(currentUser,true);
                currentUser.setACL(acl);

                currentUser.saveInBackground();
            }
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        menu.clear();
        inflater.inflate(R.menu.main, menu);
        menu.findItem(R.id.list).setVisible(true);
        getActivity().invalidateOptionsMenu();

        super.onCreateOptionsMenu(menu, inflater);
    }
    //---- String Response ------//
    public void getAllRestaurants(){
        StorageHelper.shouldReloadData = false;
        shouldFilter = false;
        restaurants_list.clear();
        pd.show();
        myrequests = Volley.newRequestQueue(getActivity());
        Integer radius = HelperFunctions.PROXIMITY_RADIUS_25_MILES;

        if(searchText == null)
            searchText = "";

        if(searchText.isEmpty()){
            //radius = HelperFunctions.PROXIMITY_RADIUS_ONE_MILE;
            radius = 500;
        }


        String url = helperfunctions.getUrl(StorageHelper.latitude, StorageHelper.longitude, next_pg_token, searchText, radius);
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

    @Override
    public void onResume(){

        if(StorageHelper.rest_searched){
            search_text.setText("");
            StorageHelper.rest_searched = false;
            StorageHelper.restaurants_generic_list.clear();
            for (int i = 0 ; i < StorageHelper.backup_restaurant_list.size();i++){
                StorageHelper.restaurants_generic_list.add(StorageHelper.backup_restaurant_list.get(i));
            }
            StorageHelper.backup_restaurant_list.clear();
            if(mMap != null){
                mMap.clear();
            }
           //PlotMap();
        }
        //Toast.makeText(getContext(), "On Resume Called", Toast.LENGTH_SHORT).show();
        Log.e("DEBUG", "onResume of HomeFragment");
        super.onResume();
    }

    @Override
    public void onPause(){
        //Toast.makeText(getContext(), "On Pause Called", Toast.LENGTH_SHORT).show();
        Log.e("DEBUG", "OnPause of HomeFragment");
        super.onPause();
    }


    //---- Get Filtered Restaurants-----//
    public void getFilteredRestaurants(){
        restaurants_list.clear();
        shouldFilter = true;
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

    public void PlotLocations(JSONObject response) throws JSONException{
        JSONArray results = response.getJSONArray("results");

        for (int i = 0; i < results.length(); i++){
            JSONObject temp_Obj = results.getJSONObject(i);
            JSONObject geometry = temp_Obj.getJSONObject("geometry");
            JSONObject location = geometry.getJSONObject("location");

            Restaurant temp_restaurant = new Restaurant();
            temp_restaurant.latitude = Double.valueOf(location.getString("lat"));
            temp_restaurant.longitude = Double.valueOf(location.getString("lng"));

            if (temp_Obj.has("rating")){
                temp_restaurant.rating_google = Double.valueOf(temp_Obj.getString("rating"));
            }else{
                temp_restaurant.rating_google = Double.valueOf("0.0");
            }

            temp_restaurant.ID = temp_Obj.getString("id");
            temp_restaurant.PlaceId = temp_Obj.getString("place_id");
            temp_restaurant.restaurant_name = temp_Obj.getString("name");
            temp_restaurant.icon_url = temp_Obj.getString("icon");
            temp_restaurant.vicinity = temp_Obj.getString("vicinity");
            temp_restaurant.avgRating = 0;
            getParseRestaurant(temp_restaurant);
            restaurants_list.add(temp_restaurant);
        }

        getPrivateRestaurants();
    }

    public void getPrivateRestaurants(){
        ParseGeoPoint geoPoint = new ParseGeoPoint(StorageHelper.latitude, StorageHelper.longitude);

        ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Restaurant");
        parseQuery.whereDoesNotExist("id");
        parseQuery.whereWithinMiles("location", geoPoint, 5);

        if(shouldFilter){
            parseQuery.whereContainedIn("category", StorageHelper.filters_list);
        }

        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    for(int i = 0; i < objects.size(); i++){
                        ParseObject current = objects.get(i);
                        Restaurant temp = new Restaurant();
                        temp.parseObject = current;
                        temp.restaurant_name = current.getString("name");
                        temp.vicinity = current.getString("vicinity");
                        temp.longitude = current.getDouble("longitude");
                        temp.latitude = current.getDouble("latitude");

                        restaurants_list.add(temp);
                    }

                    PlotMap();
                } else {
                    PlotMap();
                }
            }
        });
    }

    public void getParseRestaurant(final Restaurant restaurant){
        ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Restaurant");
        parseQuery.whereEqualTo("place_id", restaurant.PlaceId);

        parseQuery.findInBackground(new FindCallback<ParseObject>(){
            @Override
            public void done(List<ParseObject> objects, ParseException e){
                if(e == null){
                    if(objects.size() > 0){
                        restaurant.parseObject = objects.get(0);
                        getReviews(restaurant);
                    }
                }
            }
        });
    }

    public void getReviews(final Restaurant restaurant){
        ParseQuery<ParseObject> parseQuery = new ParseQuery<>("FullReview");
        parseQuery.whereEqualTo("restaurantId", restaurant.parseObject);
        parseQuery.include("restaurantId");
        parseQuery.include("userId");
        parseQuery.setLimit(1000);

        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                int sum = 0;
                int starSum = 0;
                for(int i = 0; i < objects.size(); i++){
                    sum += objects.get(i).getInt("averageRating");
                    if(objects.get(i).getNumber("overall_Rating") != null)
                        starSum += objects.get(i).getNumber("overall_Rating").floatValue();
                }

                if(objects.size() == 0){
                    restaurant.avgRating = 0;
                    restaurant.starRating = 0;
                }else{
                    restaurant.avgRating = sum / objects.size();
                    restaurant.starRating = starSum / objects.size();
                }
                restaurant.reviews = objects;
                PlotMap();
            }
        });
    }

    public void PlotMap(){
        if(mMap == null){
            return;
        }
        for (int i = 0; i < restaurants_list.size(); i++){
            marker = new MarkerOptions().position(new LatLng(restaurants_list.get(i).latitude, restaurants_list.get(i).longitude)).title(restaurants_list.get(i).restaurant_name);
            marker.icon(BitmapDescriptorFactory.fromBitmap(HelperFunctions.getMarkerBitmapFromView(R.drawable.marker_bg, getContext(), String.valueOf(restaurants_list.get(i).avgRating))));
            mMap.addMarker(marker);
        }

        if(!searchText.isEmpty() && restaurants_list.size() > 0){
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(restaurants_list.get(0).latitude, restaurants_list.get(0).longitude)));
        }

        mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
        StorageHelper.restaurants_generic_list = restaurants_list;
        pd.dismiss();
    }

    @Override
    public void onMapReady(GoogleMap googleMap){
        mMap = googleMap;

        boolean success = googleMap.setMapStyle(new MapStyleOptions(getResources()
                .getString(R.string.style_json)));

        if (!success) {
            Toast.makeText(getActivity(), "Map failed", Toast.LENGTH_SHORT).show();
        }

        mMap.setOnMarkerClickListener(this);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));

        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            //bulidGoogleApiClient();
             mMap.setMyLocationEnabled(true);
        }

        if(StorageHelper.restaurants_generic_list.size() != 0){
            PlotMap();
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(StorageHelper.latitude, StorageHelper.longitude)));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
        }
    }

    @Override
    public void onClick(View v){
        if(v.getId() == search_icon.getId() || v.getId() == search_lay.getId()){
            SearchRestaurants();
        }else if(v.getId() == refresh_iv.getId()){
            mMap.clear();

            searchText="";
            //getLastKnownLocation();

            LatLng currentLoc = mMap.getCameraPosition().target;

            StorageHelper.longitude = currentLoc.longitude;
            StorageHelper.latitude = currentLoc.latitude;

            getAllRestaurants();
        }else if(v.getId() == indian_btn.getId()){

            if(!selectedRestaurants.contains("indian")){
                selectedRestaurants.add("indian");
                indian_btn.setBackgroundColor(getResources().getColor(R.color.grey));
                indian_btn.setTextColor(getResources().getColor(R.color.red_dark));
            }else if(selectedRestaurants.contains("indian")){
                selectedRestaurants.remove("indian");
                indian_btn.setBackgroundColor(getResources().getColor(R.color.red_dark));
                indian_btn.setTextColor(getResources().getColor(R.color.white));
            }
        }else if(v.getId() == chinese_btn.getId()){

            if(!selectedRestaurants.contains("chinese")){
                selectedRestaurants.add("chinese");
                chinese_btn.setBackgroundColor(getResources().getColor(R.color.grey));
                chinese_btn.setTextColor(getResources().getColor(R.color.red_dark));
            }else if(selectedRestaurants.contains("chinese")){
                selectedRestaurants.remove("chinese");
                chinese_btn.setBackgroundColor(getResources().getColor(R.color.red_dark));
                chinese_btn.setTextColor(getResources().getColor(R.color.white));
            }
        }else if(v.getId() == singaporean_btn.getId()){

            if(!selectedRestaurants.contains("singaporean")){
                selectedRestaurants.add("singaporean");
                singaporean_btn.setBackgroundColor(getResources().getColor(R.color.grey));
                singaporean_btn.setTextColor(getResources().getColor(R.color.red_dark));
            }else if(selectedRestaurants.contains("singaporean")){
                selectedRestaurants.remove("singaporean");
                singaporean_btn.setBackgroundColor(getResources().getColor(R.color.red_dark));
                singaporean_btn.setTextColor(getResources().getColor(R.color.white));
            }

        }else if(v.getId() == western_btn.getId()){


            if(!selectedRestaurants.contains("western")){
                selectedRestaurants.add("western");
                western_btn.setBackgroundColor(getResources().getColor(R.color.grey));
                western_btn.setTextColor(getResources().getColor(R.color.red_dark));
            }else if(selectedRestaurants.contains("western")){
                selectedRestaurants.remove("western");
                western_btn.setBackgroundColor(getResources().getColor(R.color.red_dark));
                western_btn.setTextColor(getResources().getColor(R.color.white));
            }

        }else if(v.getId() == malay_btn.getId()){


            if(!selectedRestaurants.contains("malay")){
                selectedRestaurants.add("malay");
                malay_btn.setBackgroundColor(getResources().getColor(R.color.grey));
                malay_btn.setTextColor(getResources().getColor(R.color.red_dark));
            }else if(selectedRestaurants.contains("malay")){
                selectedRestaurants.remove("malay");
                malay_btn.setBackgroundColor(getResources().getColor(R.color.red_dark));
                malay_btn.setTextColor(getResources().getColor(R.color.white));
            }

        }else if(v.getId() == halal_btn.getId()){


            if(!selectedRestaurants.contains("halal")){
                selectedRestaurants.add("halal");
                halal_btn.setBackgroundColor(getResources().getColor(R.color.grey));
                halal_btn.setTextColor(getResources().getColor(R.color.red_dark));
            }else if(selectedRestaurants.contains("halal")){
                selectedRestaurants.remove("halal");
                halal_btn.setBackgroundColor(getResources().getColor(R.color.red_dark));
                halal_btn.setTextColor(getResources().getColor(R.color.white));
            }


        }else if(v.getId() == hotpot_btn.getId()){

            if(!selectedRestaurants.contains("hotpot")){
                selectedRestaurants.add("hotpot");
                hotpot_btn.setBackgroundColor(getResources().getColor(R.color.grey));
                hotpot_btn.setTextColor(getResources().getColor(R.color.red_dark));
            }else if(selectedRestaurants.contains("hotpot")){
                selectedRestaurants.remove("hotpot");
                hotpot_btn.setBackgroundColor(getResources().getColor(R.color.red_dark));
                hotpot_btn.setTextColor(getResources().getColor(R.color.white));
            }


        }else if(v.getId() == bbq_btn.getId()){

            if(!selectedRestaurants.contains("bbq")){
                selectedRestaurants.add("bbq");
                bbq_btn.setBackgroundColor(getResources().getColor(R.color.grey));
                bbq_btn.setTextColor(getResources().getColor(R.color.red_dark));
            }else if(selectedRestaurants.contains("bbq")){
                selectedRestaurants.remove("bbq");
                bbq_btn.setBackgroundColor(getResources().getColor(R.color.red_dark));
                bbq_btn.setTextColor(getResources().getColor(R.color.white));
            }


        }else if(v.getId() == thai_btn.getId()){


            if(!selectedRestaurants.contains("thai")){
                selectedRestaurants.add("thai");
                thai_btn.setBackgroundColor(getResources().getColor(R.color.grey));
                thai_btn.setTextColor(getResources().getColor(R.color.red_dark));
            }else if(selectedRestaurants.contains("thai")){
                selectedRestaurants.remove("thai");
                thai_btn.setBackgroundColor(getResources().getColor(R.color.red_dark));
                thai_btn.setTextColor(getResources().getColor(R.color.white));
            }


        }else if(v.getId() == fruits_btn.getId()){

            if(!selectedRestaurants.contains("fruits")){
                selectedRestaurants.add("fruits");
                fruits_btn.setBackgroundColor(getResources().getColor(R.color.grey));
                fruits_btn.setTextColor(getResources().getColor(R.color.red_dark));
            }else if(selectedRestaurants.contains("fruits")){
                selectedRestaurants.remove("fruits");
                fruits_btn.setBackgroundColor(getResources().getColor(R.color.red_dark));
                fruits_btn.setTextColor(getResources().getColor(R.color.white));
            }


        }else if(v.getId() == korean_btn.getId()){

            if(!selectedRestaurants.contains("korean")){
                selectedRestaurants.add("korean");
                korean_btn.setBackgroundColor(getResources().getColor(R.color.grey));
                korean_btn.setTextColor(getResources().getColor(R.color.red_dark));
            }else if(selectedRestaurants.contains("korean")){
                selectedRestaurants.remove("korean");
                korean_btn.setBackgroundColor(getResources().getColor(R.color.red_dark));
                korean_btn.setTextColor(getResources().getColor(R.color.white));
            }


        }else if(v.getId() == vietnamese_btn.getId()){

            if(!selectedRestaurants.contains("vietnamese")){
                selectedRestaurants.add("vietnamese");
                vietnamese_btn.setBackgroundColor(getResources().getColor(R.color.grey));
                vietnamese_btn.setTextColor(getResources().getColor(R.color.red_dark));
            }else if(selectedRestaurants.contains("vietnamese")){
                selectedRestaurants.remove("vietnamese");
                vietnamese_btn.setBackgroundColor(getResources().getColor(R.color.red_dark));
                vietnamese_btn.setTextColor(getResources().getColor(R.color.white));
            }

        }else if(v.getId() == skip_tv.getId()){
            dialog.dismiss();

        }else if(v.getId() == next_tv.getId()){
            dialog.dismiss();
        }
    }

    public void SearchRestaurants(){
        search_text_st = search_text.getText().toString();
        if(search_text_st.equals("")){
            Toast.makeText(getActivity(), "Please enter keyword", Toast.LENGTH_SHORT).show();
        }else{
            for (int i = 0 ; i < StorageHelper.restaurants_generic_list.size();i++){
                StorageHelper.backup_restaurant_list.add(StorageHelper.restaurants_generic_list.get(i));
            }
            StorageHelper.rest_searched = true;
            searchText = search_text_st;
            mMap.clear();
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

                        checkGPS();

                        mMap.setMyLocationEnabled(true);
                    }
                    mapFragment.getMapAsync(this);
                    getLastKnownLocation();
                    getAllRestaurants();
                }
                else{
                    Toast.makeText(getContext(),"Permission Denied" , Toast.LENGTH_LONG).show();
                }

            case MarshMallowPermission.EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE_BY_LOAD_PROFILE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {

                }
        }
    }

    public void checkLocationPermission(){
        if(ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)  != PackageManager.PERMISSION_GRANTED ) {
            if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION)){
                requestPermissions(new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
             } else {
                requestPermissions(new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
             }
        } else {
            checkGPS();
        }
    }

    public void checkGPS(){
        int off = 0;
        try {
            off = Settings.Secure.getInt(getActivity().getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        if(off==0){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("GPS Disabled");
            builder.setMessage("Gps is disabled, in order to use the application properly you need to enable GPS of your device");
            builder.setPositiveButton("Enable GPS", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent onGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(onGPS);
                }
            });
            Dialog dialog = builder.create();
            dialog.show();
        }
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
        //StorageHelper.longitude = location.getLongitude();
        //StorageHelper.latitude = location.getLatitude();
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