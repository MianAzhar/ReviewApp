package inv.sfs.com.criticapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import inv.sfs.com.criticapp.Models.Rating;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReportsDetailFragment extends Fragment {

    ListView details_lv;

    ArrayList<Rating> data;
    ArrayList<Integer> percentages;

    public ReportsDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reports_detail, container, false);

        details_lv = view.findViewById(R.id.reports_details_lv);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle bundle){
        super.onActivityCreated(bundle);

        Bundle args = getArguments();

        String title = args.getString("title");
        double percent = args.getDouble("percent", 0f);
        int color = args.getInt("color");

        percentages = new ArrayList<>();
        percentages.add(0);
        percentages.add(0);
        percentages.add(0);
        percentages.add(0);
        percentages.add(0);

        data = new ArrayList<>();

        if(StorageHelper.ratingArrayList != null){
            for(int i = 0; i < StorageHelper.ratingArrayList.size(); ++i){
                Rating temp = StorageHelper.ratingArrayList.get(i);
                if(temp.title.equals(title)){
                    data.add(temp);

                    int index = (int)HelperFunctions.getSimpleRating(temp.rated_value);

                    percentages.set(index - 1, percentages.get(index - 1) + 1);
                }
            }
        }

        ReportsDetailAdapter adapter = new ReportsDetailAdapter(getActivity(), data, percent, percentages, color, title);

        details_lv.setAdapter(adapter);
    }

}
