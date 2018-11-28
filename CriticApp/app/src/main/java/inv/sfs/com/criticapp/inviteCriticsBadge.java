package inv.sfs.com.criticapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import inv.sfs.com.criticapp.Models.PromotionTemplate;


/**
 * A simple {@link Fragment} subclass.
 */
public class inviteCriticsBadge extends Fragment implements View.OnClickListener {

    ListView coupon_list_lv;
    ArrayList<PromotionTemplate> data;
    TransparentProgressDialog pd;

    public inviteCriticsBadge(){
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("Invite Critics");

        getCoupons();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_invite_critics_badge, container, false);

        coupon_list_lv = view.findViewById(R.id.coupon_list_lv);
        pd = new TransparentProgressDialog(getActivity(),R.drawable.loader);

        return view;
    }

    public void getCoupons(){
        pd.show();
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("PromotionTemplates");

        query.whereEqualTo("userId", ParseUser.getCurrentUser());

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                data = new ArrayList<>();

                for(int i = 0; i < objects.size(); i++){
                    PromotionTemplate temp = new PromotionTemplate();

                    ParseObject current = objects.get(i);

                    temp.discountText = current.getString("discountText");
                    temp.promotionText = current.getString("promotionText");
                    temp.expirationDate = current.getDate("expirationDate");
                    temp.objectId = current.getObjectId();
                    data.add(temp);
                }

                final CouponListAdapter adapter = new CouponListAdapter(getActivity(), data);

                coupon_list_lv.setAdapter(adapter);
                pd.dismiss();
                coupon_list_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if(i > 0 && i < adapter.getCount()){
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("promotion", data.get(i - 1));

                            badgeDetails badgedetails = new badgeDetails();

                            badgedetails.setArguments(bundle);
                            android.support.v4.app.FragmentTransaction trans1 = getActivity().getSupportFragmentManager().beginTransaction();
                            trans1.replace(R.id.frame_container,badgedetails).addToBackStack(null).commit();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();

        getCoupons();
    }

    @Override
    public void onClick(View view) {
        /*
        if(view.getId() == discount_lay.getId()){
            badgeDetails badgedetails = new badgeDetails();
            android.support.v4.app.FragmentTransaction trans1 = getActivity().getSupportFragmentManager().beginTransaction();
            trans1.replace(R.id.frame_container,badgedetails).addToBackStack(null).commit();
        }
        */
    }
}
