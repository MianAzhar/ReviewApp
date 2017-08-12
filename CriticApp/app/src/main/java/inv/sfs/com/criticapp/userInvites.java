package inv.sfs.com.criticapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class userInvites extends Fragment {

    ListView user_invites;
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        for(int i =0; i < 7 ; i++){
            restaurant_name.add("Restaurant Name");
            address.add("Fri Chicks Wapda Town");
        }

        user_invites = (ListView) getActivity().findViewById(R.id.user_invites);
        userinvitesadapter adapter = new userinvitesadapter(getActivity(), restaurant_name,address);
        user_invites.setAdapter(adapter);
        user_invites.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id){
                // TODO Auto-generated method stub
                String pos = String.valueOf(position);

            }
        });

    }


}
