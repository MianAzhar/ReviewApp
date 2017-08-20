package inv.sfs.com.criticapp;


import android.app.Dialog;
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
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class addReviewfrag extends Fragment implements View.OnClickListener {


    ListView add_review_lv;
    Button instant_btn;
    public ArrayList<String> review_against =new ArrayList<String>();
    public ArrayList<String> comments =new ArrayList<String>();
    Dialog dialog;

    public addReviewfrag(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
         return inflater.inflate(R.layout.fragment_add_reviewfrag, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("Critic Review");

        review_against.add("Location");
        review_against.add("Main Greeting");
        review_against.add("Seating Wait Time");
        review_against.add("Table Quality");
        review_against.add("Menu Selection");
        review_against.add("Price Range");
        review_against.add("Server Attitude");
        review_against.add("Food Delivery");
        review_against.add("Order Accuracy");
        review_against.add("Food Planting");
        review_against.add("Taste Of Food");
        review_against.add("Noise Level");
        review_against.add("Lighting Level");
        review_against.add("Climate Comfort");
        review_against.add("Refill Service");
        review_against.add("Restroom Quality");
        review_against.add("Bill Service");
        review_against.add("Overall Appeal");

        //Comments ArrayList//

        comments.add("Comment: Busy, Remote, Small");
        comments.add("Comment: Friendly, Grumpy");
        comments.add("Comment: Short Wait, Long Wait");
        comments.add("Comment: small, wobbly, nice");
        comments.add("Comment: Lots of Options, Limited");
        comments.add("Comment: Great, Expensive");
        comments.add("Comment: Friendly, Horrible");
        comments.add("Comment: Fast, Long wait, wrong");
        comments.add("Comment: Cooked wrong, Write");
        comments.add("Comment: Looked Great, Sloppy");
        comments.add("Comment: Yummy, Nasty, Great");
        comments.add("Comment: Too Loud, Nice, Quite");
        comments.add("Comment: Too Dark at my table");
        comments.add("Comment: Perfect, Too Hot, Cold");
        comments.add("Comment: Clean, Smelly, Dirty");
        comments.add("Comment: Clean, Smelly, Dirty");
        comments.add("Comment: Long Wait, Quick");
        comments.add("Comment: Great Place, Love It");

        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.customdialogue);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        instant_btn = (Button) getView().findViewById(R.id.instant_btn);
        instant_btn.setOnClickListener(this);

        add_review_lv = (ListView) getView().findViewById(R.id.add_review_lv);
        addReviewsAdapter adapter = new addReviewsAdapter(getActivity(), review_against,comments);
        add_review_lv.setAdapter(adapter);
        add_review_lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id){
                // TODO Auto-generated method stub
                String pos = String.valueOf(position);

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){

        menu.clear();
        inflater.inflate(R.menu.main, menu);
        menu.findItem(R.id.next).setVisible(true);
        getActivity().invalidateOptionsMenu();
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){

            case android.R.id.home:
                getActivity().finish();
                break;
            case R.id.next:
                submitReviewfrag submitreview = new submitReviewfrag();
                android.support.v4.app.FragmentTransaction trans1 = getActivity().getSupportFragmentManager().beginTransaction();
                trans1.replace(R.id.frame_container,submitreview).addToBackStack(null).commit();
                return  true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v){
        if(v.getId() == instant_btn.getId()){
            dialog.show();
        }
    }

}
