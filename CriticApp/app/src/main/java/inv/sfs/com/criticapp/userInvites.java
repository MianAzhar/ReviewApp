package inv.sfs.com.criticapp;


import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import inv.sfs.com.criticapp.Models.FullReviewModel;
import inv.sfs.com.criticapp.Models.Notification;
import inv.sfs.com.criticapp.Models.Restaurant;


/**
 * A simple {@link Fragment} subclass.
 */
public class userInvites extends Fragment{


    TransparentProgressDialog pd;
    public ListView user_invites;
    public ArrayList<String> restaurant_name =new ArrayList<String>();
    public ArrayList<String> address =new ArrayList<String>();
    public Notification notification;
    public ArrayList<Notification> notifications_list = new ArrayList<Notification>();
    public ArrayList<Integer> avgRating = new ArrayList<Integer>();
    public userinvitesadapter adapter;

    public userInvites(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_user_invites, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("Owner Invites");

        pd = new TransparentProgressDialog(getContext(), R.drawable.loader);

        for(int i =0; i < 7 ; i++){
            restaurant_name.add("Restaurant Name");
            address.add("Fri Chicks Wapda Town");
        }


        user_invites = (ListView) getActivity().findViewById(R.id.user_invites);
        receiveData();
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void receiveData(){

        notifications_list.clear();
        Calendar mcurrentTime;
        mcurrentTime = Calendar.getInstance();
        ParseQuery<ParseObject> promotions = new ParseQuery<ParseObject>("Promotions");
        promotions.whereGreaterThanOrEqualTo("expirationDate" ,mcurrentTime.getTime());

        pd.show();
        ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Notifications");
        parseQuery.whereEqualTo("userId", ParseUser.getCurrentUser());
        parseQuery.whereNotEqualTo("isDeclined", true);
        parseQuery.orderByDescending("createdAt");
        parseQuery.include("userId");
        parseQuery.include("promotionId");
        parseQuery.include("promotionId.restaurantId");
        parseQuery.whereMatchesQuery("promotionId" , promotions);
        parseQuery.setLimit(1000);

        parseQuery.findInBackground(new FindCallback<ParseObject>(){
            @Override
            public void done(List<ParseObject> list, ParseException e){
                if (e == null){
                    if (!list.isEmpty()){
                        for (int i = 0; i < list.size(); i++){
                            ParseObject parseObject = list.get(i);
                            notification = new Notification();
                            notification.parseObject = parseObject;
                            notification.promotionId = (ParseObject) parseObject.get("promotionId");
                            notification.couponNumber = parseObject.getString("couponNumber");
                            notification.isAccepted = (Boolean) parseObject.get("isAccepted");
                            notification.isDeclined = (Boolean) parseObject.get("isDeclined");
                            notification.restaurant.parseObject = parseObject.getParseObject("promotionId").getParseObject("restaurantId");
                            notifications_list.add(notification);
                        }
                        pd.dismiss();
                        populateAdapter();
                        getAverageRatings();
                    } else {
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


    public void getAverageRatings(){

        for(int j = 0; j < notifications_list.size(); j++ ){

        ParseQuery<ParseObject> parseQuery = new ParseQuery<>("FullReview");
        parseQuery.whereEqualTo("restaurantId", notifications_list.get(j).restaurant.parseObject);
        parseQuery.setLimit(1000);

            final int finalJ = j;
            parseQuery.findInBackground(new FindCallback<ParseObject>(){
            @Override
            public void done(List<ParseObject> list, ParseException e){
                if (e == null){
                    if (!list.isEmpty()){
                        ParseObject parseObject = list.get(0);
                        notifications_list.get(finalJ).restaurant.avgRating = (int) parseObject.get("averageRating");
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.d("Result", "Empty");
                    }
                } else {
                    Log.d("Result", "Wrong");
                }
            }
        });
        }
    }

    private void populateAdapter(){
        adapter = new userinvitesadapter(getActivity(),notifications_list);
        user_invites.setAdapter(adapter);
        user_invites.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id){
                // TODO Auto-generated method stub
                String pos = String.valueOf(position);

                //Sending Notification Object to next fragment..
                StorageHelper.notificationTempObj = new Notification();
                StorageHelper.notificationTempObj.promotionId = notifications_list.get(position).promotionId;
                StorageHelper.notificationTempObj.restaurant = notifications_list.get(position).restaurant;
                StorageHelper.notificationTempObj.user = notifications_list.get(position).user;
                StorageHelper.notificationTempObj.isDeclined = notifications_list.get(position).isDeclined;
                StorageHelper.notificationTempObj.isAccepted = notifications_list.get(position).isAccepted;
                StorageHelper.notificationTempObj.parseObject = notifications_list.get(position).parseObject;
                StorageHelper.notificationTempObj.couponNumber = notifications_list.get(position).couponNumber;

                java.util.Calendar calender_obj = StorageHelper.toCalendar((Date) notifications_list.get(position).promotionId.get("expirationDate"));
                String Month = Integer.toString(calender_obj.get(java.util.Calendar.MONTH));
                String Day = Integer.toString(calender_obj.get(java.util.Calendar.DAY_OF_MONTH));


                Bundle bundle = new Bundle();
                bundle.putString("restaurant_name" , notifications_list.get(position).promotionId.getParseObject("restaurantId").get("name").toString());
                bundle.putString("vicinity" ,notifications_list.get(position).promotionId.getParseObject("restaurantId").get("vicinity").toString());
                bundle.putString("avgRating", String.valueOf(notifications_list.get(position).restaurant.avgRating));
                bundle.putString("month", StorageHelper.convet_month_shortform(StorageHelper.convet_month(Month)));
                bundle.putString("day", Day);
                bundle.putString("discountText", notifications_list.get(position).promotionId.getString("discountText"));
                bundle.putString("promotionText", notifications_list.get(position).promotionId.getString("promotionText"));
                StorageHelper.restaurant_TempObj =  notifications_list.get(position).restaurant.parseObject;

                if(StorageHelper.notificationTempObj.isAccepted == null)
                    StorageHelper.notificationTempObj.isAccepted = false;

                if(StorageHelper.notificationTempObj.isAccepted){
                    badgeDetails badgedetails = new badgeDetails();
                    android.support.v4.app.FragmentTransaction trans1 = getActivity().getSupportFragmentManager().beginTransaction();
                    trans1.replace(R.id.frame_container,badgedetails).addToBackStack(null).commit();
                } else {
                    userIvitesDetailsfrag userinvitedetails = new userIvitesDetailsfrag();
                    android.support.v4.app.FragmentTransaction trans1 = getActivity().getSupportFragmentManager().beginTransaction();
                    userinvitedetails.setArguments(bundle);
                    trans1.replace(R.id.frame_container,userinvitedetails).addToBackStack(null).commit();
                }
            }
        });
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){

        menu.clear();
        inflater.inflate(R.menu.main, menu);
        menu.findItem(R.id.invite_critics).setVisible(false);
        getActivity().invalidateOptionsMenu();
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {

            case android.R.id.home:
                getActivity().finish();
                break;
            case R.id.invite_critics:

                inviteCriticsfrag addreview = new inviteCriticsfrag();
                android.support.v4.app.FragmentTransaction trans1 = getActivity().getSupportFragmentManager().beginTransaction();
                trans1.replace(R.id.frame_container,addreview).addToBackStack(null).commit();

                return  true;
        }
        return super.onOptionsItemSelected(item);
    }
}
