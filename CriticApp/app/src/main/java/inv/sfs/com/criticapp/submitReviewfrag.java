package inv.sfs.com.criticapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class submitReviewfrag extends Fragment implements View.OnClickListener {


    Button yes_btn, no_btn;
    public submitReviewfrag(){
     }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_submit_reviewfrag, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("Add Review");

        yes_btn = (Button) getView().findViewById(R.id.yes_btn);
        no_btn = (Button) getView().findViewById(R.id.no_btn);
        //yes_btn.setOnClickListener(this);
        //no_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == yes_btn.getId()){
            yes_btn.setBackground(getResources().getDrawable(R.drawable.standard_btn));
            no_btn.setBackground(getResources().getDrawable(R.drawable.search_bg));
            yes_btn.setTextColor(getResources().getColor(R.color.white));
            no_btn.setTextColor(getResources().getColor(R.color.black));
        }else if(v.getId() == no_btn.getId()){
            no_btn.setBackground(getResources().getDrawable(R.drawable.standard_btn));
            yes_btn.setBackground(getResources().getDrawable(R.drawable.search_bg));
            no_btn.setTextColor(getResources().getColor(R.color.white));
            yes_btn.setTextColor(getResources().getColor(R.color.black));
        }
    }
}
