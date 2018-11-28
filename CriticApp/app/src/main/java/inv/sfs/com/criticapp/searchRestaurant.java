package inv.sfs.com.criticapp;

import android.*;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import inv.sfs.com.criticapp.Models.FullReviewModel;
import inv.sfs.com.criticapp.Models.Restaurant;

public class searchRestaurant extends AppCompatActivity implements View.OnClickListener {


    ListView restaurant_select_list;
    EditText search_et;
    LinearLayout search_lay;
    public static ArrayList<Restaurant> restaurants_list = new ArrayList<Restaurant>();
    TransparentProgressDialog pd;
    HelperFunctions helperfunctions;
    JSONObject api_response;
    public static RequestQueue myrequests;
    double latitude, longitude;
    String next_pg_token = "";
    String searchText = null;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_restaurant);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(this.getResources().getColor(R.color.app_basic_color)));
        getSupportActionBar().setTitle("Select Your Restaurant");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        search_et = findViewById(R.id.search_et);
        search_lay = findViewById(R.id.search_layout);

        search_lay.setOnClickListener(this);

        search_et.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    callApi();
                    return true;
                }
                return false;
            }
        });

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        helperfunctions = new HelperFunctions();
        pd = new TransparentProgressDialog(this, R.drawable.loader);
        restaurant_select_list = (ListView) findViewById(R.id.restaurant_select_list);
        getLastKnownLocation();
        callApi();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        menu.findItem(R.id.add).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){

            case android.R.id.home:
                finish();
                break;
            case R.id.add:
                Intent intent = new Intent(this, AddLocationActivity.class);
                startActivityForResult(intent, 123);

                return  true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 123){
            if(resultCode == Activity.RESULT_OK){
                int pos = data.getIntExtra("result" , 0);

                Intent returnIntent = new Intent();
                returnIntent.putExtra("result",pos);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }

        }


    }

    public void getLastKnownLocation(){
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers){
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
            Toast.makeText(this, "Cannot get your location", Toast.LENGTH_SHORT).show();
        } else {
            latitude = bestLocation.getLatitude();
            longitude = bestLocation.getLongitude();
        }
    }

    public void callApi(){
        restaurants_list.clear();
        StorageHelper.searched_restaurant_list.clear();
        pd.show();
        myrequests = Volley.newRequestQueue(getApplicationContext());

        searchText = search_et.getText().toString();

        String url = helperfunctions.getUrl(latitude, longitude, next_pg_token, searchText,HelperFunctions.PROXIMITY_RADIUS_25_MILES);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response){
                try{
                    api_response = response;
                    PlotLocations(response);
                } catch (JSONException e){
                    Toast.makeText(getApplicationContext(), "Some Error 1", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Toast.makeText(getApplication(), "Some Error 2", Toast.LENGTH_SHORT).show();
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
            restaurants_list.add(temp_restaurant);
        }
        pd.dismiss();
        StorageHelper.searched_restaurant_list = restaurants_list;
        populateAdapter();
    }


    public void populateAdapter(){

        final search_restaurant_list_adapter adapter = new search_restaurant_list_adapter(this, restaurants_list);
        restaurant_select_list.setAdapter(adapter);
        restaurant_select_list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id){
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result",position);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == search_lay.getId()){
            callApi();
        }
    }
}

class search_restaurant_list_adapter extends ArrayAdapter<Restaurant> {
    private final Activity context;
    private final ArrayList<Restaurant> restaurants_name_list_;

    public search_restaurant_list_adapter(Activity context, ArrayList<Restaurant> restaurants_name_list){
        super(context, R.layout.search_rest_list, restaurants_name_list);
        // TODO Auto-generated constructor stub

        this.context = context;
        this.restaurants_name_list_ = restaurants_name_list;
    }

    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.search_rest_list, null,true);

        TextView name = (TextView) rowView.findViewById(R.id.rest_name);
        name.setText(restaurants_name_list_.get(position).restaurant_name);

        return rowView;
    }
}

