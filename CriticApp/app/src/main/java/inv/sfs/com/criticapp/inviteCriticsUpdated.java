package inv.sfs.com.criticapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;


/**
 * A simple {@link Fragment} subclass.
 */
public class inviteCriticsUpdated extends Fragment implements View.OnClickListener {


    Button setup_btn;

    public inviteCriticsUpdated(){
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_invite_critics_updated, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("Invite Critics");

        setup_btn = (Button) getView().findViewById(R.id.setup_btn);
        setup_btn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        if(view.getId() == setup_btn.getId()){
            addInvite addinvite = new addInvite();
            android.support.v4.app.FragmentTransaction trans1 = getActivity().getSupportFragmentManager().beginTransaction();
            trans1.replace(R.id.frame_container,addinvite).addToBackStack(null).commit();

        }
    }
}