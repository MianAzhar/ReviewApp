package inv.sfs.com.criticapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class inviteCriticsfrag extends Fragment {


    ListView restaurants_list;
    ListView reviews_list;
    public ArrayList<String> restaurant_name =new ArrayList<String>();
    public ArrayList<Float> rating_value=new ArrayList<Float>();
    public ArrayList<Integer> rating_count=new ArrayList<Integer>();
    public ArrayList<String> address =new ArrayList<String>();


    public inviteCriticsfrag() {
     }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         return inflater.inflate(R.layout.fragment_invite_criticsfrag, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("Select Restaurant");


        for(int i =0; i < 7 ; i++){
            restaurant_name.add("Restaurant Name");
            rating_value.add((float) 5);
            rating_count.add(5);
            address.add("Fri Chicks Wapda Town Round About Lahore Pakistan");
        }

        restaurants_list = (ListView) getView().findViewById(R.id.restaurants_list);
        reviewslistAdapter adapter = new reviewslistAdapter(getActivity(), restaurant_name, rating_value,rating_count, address);
        restaurants_list.setAdapter(adapter);
        restaurants_list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id){
                // TODO Auto-generated method stub
                String pos = String.valueOf(position);
                inviteCriticsExtendedfrag inviteExtended = new inviteCriticsExtendedfrag();
                android.support.v4.app.FragmentTransaction trans1 = getActivity().getSupportFragmentManager().beginTransaction();
                trans1.replace(R.id.frame_container,inviteExtended).addToBackStack(null).commit();

            }
        });
    }

}
