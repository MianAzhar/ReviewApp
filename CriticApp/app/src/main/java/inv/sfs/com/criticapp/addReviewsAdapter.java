package inv.sfs.com.criticapp;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
        View rowView=inflater.inflate(R.layout.addreviewslayout, null,true);

        TextView name = (TextView) rowView.findViewById(R.id.review_against);
        EditText comments_tv = (EditText) rowView.findViewById(R.id.comments_tv);
        final TextView tv_one = (TextView) rowView.findViewById(R.id.tv_one);
        final TextView tv_two = (TextView) rowView.findViewById(R.id.tv_two);
        final TextView tv_three = (TextView) rowView.findViewById(R.id.tv_three);
        final TextView tv_four = (TextView) rowView.findViewById(R.id.tv_four);
        final TextView tv_five = (TextView) rowView.findViewById(R.id.tv_five);
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

        return rowView;
    }


}
