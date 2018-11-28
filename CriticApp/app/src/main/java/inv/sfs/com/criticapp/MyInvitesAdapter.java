package inv.sfs.com.criticapp;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import inv.sfs.com.criticapp.Models.Notification;
import inv.sfs.com.criticapp.Models.Promotion;

/**
 * Created by Mian Azhar on 2/15/2018.
 */

public class MyInvitesAdapter extends ArrayAdapter<Promotion> {

    private final Activity context;
    public final ArrayList<Promotion> notifications_list_;

    public MyInvitesAdapter(Activity context, ArrayList<Promotion> notifications_list){
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

        Calendar calender_obj = StorageHelper.toCalendar(notifications_list_.get(position).expirationDate);
        String Month = Integer.toString(calender_obj.get(java.util.Calendar.MONTH));
        String Day = Integer.toString(calender_obj.get(java.util.Calendar.DAY_OF_MONTH));

        try {
            custom_month.setText(StorageHelper.convet_month_shortform(StorageHelper.convet_month(Month)));
            custom_date.setText(Day);

        }catch (Exception e){
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
        }
        return rowView;
    }
}
