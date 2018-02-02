package inv.sfs.com.criticapp;

import android.app.Activity;
import inv.sfs.com.criticapp.Models.Notification;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by iosdev-1 on 8/11/17.
 */

public class userinvitesadapter extends ArrayAdapter<Notification>{

    private final Activity context;
    public final ArrayList<Notification> notifications_list_;

    public userinvitesadapter(Activity context, ArrayList<Notification> notifications_list){
        super(context, R.layout.userinviteslayout, notifications_list);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.notifications_list_ = notifications_list;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.userinviteslayout, null,true);

        TextView name = (TextView) rowView.findViewById(R.id.name);
        TextView address = (TextView) rowView.findViewById(R.id.address);
        TextView rating_value = (TextView) rowView.findViewById(R.id.rating_value);
        TextView custom_month = (TextView) rowView.findViewById(R.id.custom_month);
        TextView custom_date = (TextView) rowView.findViewById(R.id.custom_date);


        Calendar calender_obj = StorageHelper.toCalendar((Date) notifications_list_.get(position).promotionId.get("expirationDate"));
        String Month = Integer.toString(calender_obj.get(java.util.Calendar.MONTH));
        String Day = Integer.toString(calender_obj.get(java.util.Calendar.DAY_OF_MONTH));

        try {
            custom_month.setText(StorageHelper.convet_month_shortform(StorageHelper.convet_month(Month)));
            custom_date.setText(Day);
            address.setText(notifications_list_.get(position).promotionId.getParseObject("restaurantId").get("name").toString());
            name.setText(notifications_list_.get(position).promotionId.getParseObject("restaurantId").get("name").toString());
            address.setText(notifications_list_.get(position).promotionId.getParseObject("restaurantId").get("vicinity").toString());
            String val = String.valueOf(notifications_list_.get(position).restaurant.avgRating);
            rating_value.setText(val);
        }catch (Exception e){
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
        }
        return rowView;
    }
}
