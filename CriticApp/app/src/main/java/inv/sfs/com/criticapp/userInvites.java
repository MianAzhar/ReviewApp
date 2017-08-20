package inv.sfs.com.criticapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class userInvites extends Fragment{

    public ListView user_invites;
    public ArrayList<String> restaurant_name =new ArrayList<String>();
    public ArrayList<String> address =new ArrayList<String>();

    public userInvites() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_user_invites, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("User Invites");


        for(int i =0; i < 7 ; i++){
            restaurant_name.add("Restaurant Name");
            address.add("Fri Chicks Wapda Town");
        }

        user_invites = (ListView) getActivity().findViewById(R.id.user_invites);
        pupulateAdapter();
    }

    private void pupulateAdapter(){
        userinvitesadapter adapter = new userinvitesadapter(getActivity(), restaurant_name,address);
        user_invites.setAdapter(adapter);
        user_invites.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id){
                // TODO Auto-generated method stub
                String pos = String.valueOf(position);


                userIvitesDetailsfrag userinvitedetails = new userIvitesDetailsfrag();
                android.support.v4.app.FragmentTransaction trans1 = getActivity().getSupportFragmentManager().beginTransaction();
                trans1.replace(R.id.frame_container,userinvitedetails).addToBackStack(null).commit();

            }
        });
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){

        menu.clear();
        inflater.inflate(R.menu.main, menu);
        menu.findItem(R.id.invite_critics).setVisible(true);
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
