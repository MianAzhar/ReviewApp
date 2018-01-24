package inv.sfs.com.criticapp;


import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;

import inv.sfs.com.criticapp.Models.Restaurant;


/**
 * A simple {@link Fragment} subclass.
 */
public class allReviewsfrag extends Fragment {

    ListView reviews_list;
    public ArrayList<String> restaurant_name =new ArrayList<String>();
    public ArrayList<String> address =new ArrayList<String>();
    public ArrayList<Restaurant> top10SortedRestaurants;
    public ArrayList<Restaurant> top10SortedRestaurantsSublist;

    public allReviewsfrag(){
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_all_reviewsfrag, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("Critic Reviews");

        top10SortedRestaurants = StorageHelper.sortforTop10(StorageHelper.restaurants_generic_list);
        reviews_list = (ListView) getView().findViewById(R.id.reviews_list);
        top10SortedRestaurantsSublist = new ArrayList<Restaurant>(top10SortedRestaurants.subList(0, 10));
        if(StorageHelper.topTen){
            StorageHelper.topTen = false;
            reviewslistAdapter adapter = new reviewslistAdapter(getActivity(), top10SortedRestaurantsSublist);
            reviews_list.setAdapter(adapter);
        } else{
            reviewslistAdapter adapter = new reviewslistAdapter(getActivity(), StorageHelper.restaurants_generic_list);
            reviews_list.setAdapter(adapter);
        }

        reviews_list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id){
                // TODO Auto-generated method stub
                String pos = String.valueOf(position);
               //Toast.makeText(getActivity(), pos, Toast.LENGTH_SHORT).show();
                Bundle bundle = new Bundle();
                bundle.putString("position" , pos);
                reviewDetailsfrag reviewdetails = new reviewDetailsfrag();
                reviewdetails.setArguments(bundle);
                android.support.v4.app.FragmentTransaction trans1 = getActivity().getSupportFragmentManager().beginTransaction();
                trans1.replace(R.id.frame_container,reviewdetails).addToBackStack(null).commit();
             }
        });
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){

        menu.clear();
        inflater.inflate(R.menu.main, menu);
        menu.findItem(R.id.filter).setVisible(true);
        menu.findItem(R.id.mapView).setVisible(false);
        getActivity().invalidateOptionsMenu();
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){

            case android.R.id.home:
                getActivity().finish();
                break;
            case R.id.filter:
                Intent i = new Intent(getActivity(), filter.class);
                startActivity(i);
                /*FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                submitReviewfrag submitreview = new submitReviewfrag();
                ft.replace(R.id.frame_container, submitreview);
                ft.commit();*/
                break;
            case R.id.mapView:
                home homepg = new home();
                android.support.v4.app.FragmentTransaction trans1 = getActivity().getSupportFragmentManager().beginTransaction();
                trans1.replace(R.id.frame_container,homepg).addToBackStack(null).commit();


                return  true;
        }
        return super.onOptionsItemSelected(item);
    }


}
