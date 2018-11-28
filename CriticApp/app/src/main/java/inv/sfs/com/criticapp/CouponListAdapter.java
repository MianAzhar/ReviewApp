package inv.sfs.com.criticapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import inv.sfs.com.criticapp.Models.PromotionTemplate;
import inv.sfs.com.criticapp.Models.Restaurant;

/**
 * Created by Mian Azhar on 2/19/2018.
 */

public class CouponListAdapter  extends ArrayAdapter<PromotionTemplate> {

    private final Activity context;
    private final ArrayList<PromotionTemplate> data;

    public CouponListAdapter(Activity context, ArrayList<PromotionTemplate> data) {
        super(context, R.layout.restaurantsreviewlayout, data);

        this.context=context;
        this.data = data;

    }

    @Override
    public int getCount(){
        return data.size() + 2;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView = null;

        if(position == 0){
            rowView=inflater.inflate(R.layout.admin_help_text, null,true);

        } else if(position == data.size() + 1) {
            rowView=inflater.inflate(R.layout.create_new_coupon_layout, null,true);
            Button createCoupon = rowView.findViewById(R.id.create_coupon_btn);

            createCoupon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addInvite badgedetails = new addInvite();
                    android.support.v4.app.FragmentTransaction trans1 = ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction();
                    trans1.replace(R.id.frame_container,badgedetails).addToBackStack(null).commit();
                }
            });
        }else {
            rowView=inflater.inflate(R.layout.promotion_item, null,true);
            TextView discountTv = rowView.findViewById(R.id.discountText_tv);
            TextView promotionTv = rowView.findViewById(R.id.promotionText_tv);

            try{
                PromotionTemplate promotion = data.get(position - 1);

                discountTv.setText(promotion.discountText);
                promotionTv.setText(promotion.promotionText);

            }catch (Exception e){
            }
        }

        return rowView;
    }


}
