package inv.sfs.com.criticapp;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by iosdev-1 on 8/11/17.
 */

public class userinvitesadapter extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> name_;
    public final ArrayList<String> address_;

    public userinvitesadapter(Activity context, ArrayList<String> name ,ArrayList<String> address ) {
        super(context, R.layout.userinviteslayout, name);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.name_=name;

        this.address_ = address;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.userinviteslayout, null,true);

        TextView name = (TextView) rowView.findViewById(R.id.name);
        TextView address = (TextView) rowView.findViewById(R.id.address);

        try {
            name.setText(name_.get(position));
            address.setText(address_.get(position));
        }catch (Exception e){
        }

        return rowView;
    }



}
