package inv.sfs.com.criticapp;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by iosdev-1 on 8/9/17.
 */

public class addReviewsAdapter  extends ArrayAdapter<String> {


    private final Activity context;
    private final ArrayList<String> name_;
    private final ArrayList<String> comments_;



    public addReviewsAdapter(Activity context, ArrayList<String> name, ArrayList<String> comments ) {
        super(context, R.layout.addreviewslayout, name);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.name_=name;
        this.comments_ = comments;

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView;
        final Button yes_btn, no_btn;

        if(position == name_.size() - 1){
            rowView= inflater.inflate(R.layout.submit_review_bottom_layout, null,true);

            yes_btn = (Button) rowView.findViewById(R.id.yes_btn);
            no_btn = (Button) rowView.findViewById(R.id.no_btn);

            yes_btn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    yes_btn.setBackground(getContext().getResources().getDrawable(R.drawable.standard_btn));
                    no_btn.setBackground(getContext().getResources().getDrawable(R.drawable.search_bg));
                    yes_btn.setTextColor(getContext().getResources().getColor(R.color.white));
                    no_btn.setTextColor(getContext().getResources().getColor(R.color.black));
                }
            });

            no_btn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    no_btn.setBackground(getContext().getResources().getDrawable(R.drawable.standard_btn));
                    yes_btn.setBackground(getContext().getResources().getDrawable(R.drawable.search_bg));
                    no_btn.setTextColor(getContext().getResources().getColor(R.color.white));
                    yes_btn.setTextColor(getContext().getResources().getColor(R.color.black));
                }
            });

        }else{
            rowView = inflater.inflate(R.layout.addreviewslayout, null,true);

            LinearLayout stars_layout = (LinearLayout) rowView.findViewById(R.id.stars_layout);
            TextView name = (TextView) rowView.findViewById(R.id.review_against);
            EditText comments_tv = (EditText) rowView.findViewById(R.id.comments_tv);
            final TextView tv_one = (TextView) rowView.findViewById(R.id.tv_one);
            final TextView tv_two = (TextView) rowView.findViewById(R.id.tv_two);
            final TextView tv_three = (TextView) rowView.findViewById(R.id.tv_three);
            final TextView tv_four = (TextView) rowView.findViewById(R.id.tv_four);
            final TextView tv_five = (TextView) rowView.findViewById(R.id.tv_five);

            if(position == name_.size() -2){
                stars_layout.setVisibility(View.GONE);
                //name.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            try {
                name.setText(name_.get(position));
                comments_tv.setHint(comments_.get(position));
            }catch (Exception e){
            }

            tv_one.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    tv_one.setBackground(getContext().getResources().getDrawable(R.drawable.circle_red));
                    tv_one.setTextColor(getContext().getResources().getColor(R.color.white));

                    tv_two.setBackground(getContext().getResources().getDrawable(R.drawable.hollow_circle));
                    tv_two.setTextColor(getContext().getResources().getColor(R.color.app_basic_color));
                    //tv_two.setBackgroundTintList(getContext().getResources().getColor(R.color.app_basic_color));

                    tv_three.setBackground(getContext().getResources().getDrawable(R.drawable.hollow_circle));
                    tv_three.setTextColor(getContext().getResources().getColor(R.color.app_basic_color));

                    tv_four.setBackground(getContext().getResources().getDrawable(R.drawable.hollow_circle));
                    tv_four.setTextColor(getContext().getResources().getColor(R.color.app_basic_color));

                    tv_five.setBackground(getContext().getResources().getDrawable(R.drawable.hollow_circle));
                    tv_five.setTextColor(getContext().getResources().getColor(R.color.app_basic_color));

                }
            });

            tv_two.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    tv_two.setBackground(getContext().getResources().getDrawable(R.drawable.circle_red));
                    tv_two.setTextColor(getContext().getResources().getColor(R.color.white));

                    tv_one.setBackground(getContext().getResources().getDrawable(R.drawable.hollow_circle));
                    tv_one.setTextColor(getContext().getResources().getColor(R.color.app_basic_color));

                    tv_three.setBackground(getContext().getResources().getDrawable(R.drawable.hollow_circle));
                    tv_three.setTextColor(getContext().getResources().getColor(R.color.app_basic_color));

                    tv_four.setBackground(getContext().getResources().getDrawable(R.drawable.hollow_circle));
                    tv_four.setTextColor(getContext().getResources().getColor(R.color.app_basic_color));

                    tv_five.setBackground(getContext().getResources().getDrawable(R.drawable.hollow_circle));
                    tv_five.setTextColor(getContext().getResources().getColor(R.color.app_basic_color));

                }
            });


            tv_three.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    tv_three.setBackground(getContext().getResources().getDrawable(R.drawable.circle_red));
                    tv_three.setTextColor(getContext().getResources().getColor(R.color.white));

                    tv_one.setBackground(getContext().getResources().getDrawable(R.drawable.hollow_circle));
                    tv_one.setTextColor(getContext().getResources().getColor(R.color.app_basic_color));

                    tv_two.setBackground(getContext().getResources().getDrawable(R.drawable.hollow_circle));
                    tv_two.setTextColor(getContext().getResources().getColor(R.color.app_basic_color));

                    tv_four.setBackground(getContext().getResources().getDrawable(R.drawable.hollow_circle));
                    tv_four.setTextColor(getContext().getResources().getColor(R.color.app_basic_color));

                    tv_five.setBackground(getContext().getResources().getDrawable(R.drawable.hollow_circle));
                    tv_five.setTextColor(getContext().getResources().getColor(R.color.app_basic_color));

                }
            });

            tv_four.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    tv_four.setBackground(getContext().getResources().getDrawable(R.drawable.circle_red));
                    tv_four.setTextColor(getContext().getResources().getColor(R.color.white));

                    tv_one.setBackground(getContext().getResources().getDrawable(R.drawable.hollow_circle));
                    tv_one.setTextColor(getContext().getResources().getColor(R.color.app_basic_color));

                    tv_two.setBackground(getContext().getResources().getDrawable(R.drawable.hollow_circle));
                    tv_two.setTextColor(getContext().getResources().getColor(R.color.app_basic_color));

                    tv_three.setBackground(getContext().getResources().getDrawable(R.drawable.hollow_circle));
                    tv_three.setTextColor(getContext().getResources().getColor(R.color.app_basic_color));

                    tv_five.setBackground(getContext().getResources().getDrawable(R.drawable.hollow_circle));
                    tv_five.setTextColor(getContext().getResources().getColor(R.color.app_basic_color));

                }
            });

            tv_five.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    tv_five.setBackground(getContext().getResources().getDrawable(R.drawable.circle_red));
                    tv_five.setTextColor(getContext().getResources().getColor(R.color.white));

                    tv_one.setBackground(getContext().getResources().getDrawable(R.drawable.hollow_circle));
                    tv_one.setTextColor(getContext().getResources().getColor(R.color.app_basic_color));

                    tv_two.setBackground(getContext().getResources().getDrawable(R.drawable.hollow_circle));
                    tv_two.setTextColor(getContext().getResources().getColor(R.color.app_basic_color));

                    tv_three.setBackground(getContext().getResources().getDrawable(R.drawable.hollow_circle));
                    tv_three.setTextColor(getContext().getResources().getColor(R.color.app_basic_color));

                    tv_four.setBackground(getContext().getResources().getDrawable(R.drawable.hollow_circle));
                    tv_four.setTextColor(getContext().getResources().getColor(R.color.app_basic_color));
                }
            });
        }
        return rowView;
    }
}
