package inv.sfs.com.criticapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class inviteCriticsfrag extends Fragment implements View.OnClickListener {

    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;

    LinearLayout map_view_lay, list_view_lay;
    ImageView map_view_img, list_view_image;
    TextView map_view_text , list_view_text;

    public inviteCriticsfrag(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
         return inflater.inflate(R.layout.fragment_invite_criticsfrag, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("Select Restaurant");


        map_view_lay = (LinearLayout) getView().findViewById(R.id.map_view_lay);
        list_view_lay = (LinearLayout) getView().findViewById(R.id.list_view_lay);

        map_view_img = (ImageView) getView().findViewById(R.id.mapview_img);
        list_view_image = (ImageView) getView().findViewById(R.id.list_view_image);

        map_view_text = (TextView) getView().findViewById(R.id.map_view_text);
        list_view_text = (TextView) getView().findViewById(R.id.list_view_text);

        map_view_lay.setOnClickListener(this);
        map_view_img.setOnClickListener(this);
        map_view_text.setOnClickListener(this);

        list_view_lay.setOnClickListener(this);
        list_view_image.setOnClickListener(this);
        list_view_text.setOnClickListener(this);


        viewPager = (ViewPager) getView().findViewById(R.id.pager);
        mAdapter = new TabsPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(mAdapter);
        viewPager.setCurrentItem(0);


        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position){

                if(position == 0){
                    map_view_lay.setBackgroundColor(getResources().getColor(R.color.app_basic_color));
                    list_view_lay.setBackgroundColor(getResources().getColor(R.color.light_bg));
                }else if(position == 1){
                    list_view_lay.setBackgroundColor(getResources().getColor(R.color.app_basic_color));
                    map_view_lay.setBackgroundColor(getResources().getColor(R.color.light_bg));
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

    }




    @Override
    public void onClick(View v) {

        if(v.getId() == map_view_img.getId() || v.getId() == map_view_lay.getId() || v.getId() == map_view_text.getId()){
            viewPager.setCurrentItem(0);
            map_view_lay.setBackgroundColor(getResources().getColor(R.color.app_basic_color));
            list_view_lay.setBackgroundColor(getResources().getColor(R.color.light_bg));
        }else if(v.getId() == list_view_image.getId() || v.getId() == list_view_lay.getId() || v.getId() == list_view_text.getId()){
            viewPager.setCurrentItem(1);
            list_view_lay.setBackgroundColor(getResources().getColor(R.color.app_basic_color));
            map_view_lay.setBackgroundColor(getResources().getColor(R.color.light_bg));
        }
    }

    public class TabsPagerAdapter extends FragmentPagerAdapter {

        public TabsPagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int index){

             switch (index){
                case 0:
                     return new mapviewSubFragment();
                case 1:
                    return new restaurantsListSubFragment();
                default:
                    return new mapviewSubFragment();
            }
        }

        @Override
        public int getCount(){
            return 2;
        }
    }


}
