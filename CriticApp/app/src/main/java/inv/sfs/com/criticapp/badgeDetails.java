package inv.sfs.com.criticapp;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SendCallback;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import inv.sfs.com.criticapp.Models.Promotion;
import inv.sfs.com.criticapp.Models.PromotionTemplate;


/**
 * A simple {@link Fragment} subclass.
 */
public class badgeDetails extends Fragment implements View.OnClickListener {

    PromotionTemplate promotionTemplate;
    Promotion promotion;
    TextView discountText_tv, promotionText_tv, discountText_middle_tv, promotionText_middle_tv, expiryDate_tv;
    LinearLayout admin_note_lay;
    TextView coupon_code_tv;
    Calendar mcurrentTime;
    Dialog inviteFilterDialogue;
    RadioGroup sendInvites_rg;
    public int cBoxint = 0;

    Button send_invite_btn;

    TransparentProgressDialog pd;

    public badgeDetails() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_badge_details, container, false);

        discountText_tv = view.findViewById(R.id.discountText_tv);
        promotionText_tv = view.findViewById(R.id.promotionText_tv);
        discountText_middle_tv = view.findViewById(R.id.discountText_middle_tv);
        promotionText_middle_tv = view.findViewById(R.id.promotionText_middle_tv);
        expiryDate_tv = view.findViewById(R.id.expiryDate_tv);
        admin_note_lay = view.findViewById(R.id.admin_note_lay);
        coupon_code_tv = view.findViewById(R.id.coupon_code_tv);

        expiryDate_tv.setOnClickListener(this);

        send_invite_btn = view.findViewById(R.id.send_invite_btn);

        send_invite_btn.setOnClickListener(this);

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("Invite Critics");


        pd = new TransparentProgressDialog(getContext(), R.drawable.loader);
        inviteFilterDialogue = new Dialog(getActivity());
        inviteFilterDialogue.setTitle("Scores");
        inviteFilterDialogue.setContentView(R.layout.sendinvites_filter_dialog);
        Window window1 = inviteFilterDialogue.getWindow();
        window1.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //inviteFilterDialogue.show();

        sendInvites_rg = (RadioGroup) inviteFilterDialogue.getWindow().findViewById(R.id.sendInvites_rg);
        Button sendInvite = (Button) inviteFilterDialogue.getWindow().findViewById(R.id.send_btn);
        sendInvite.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                sendInvite();
            }
        });

        Button cancelBtn = (Button) inviteFilterDialogue.getWindow().findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inviteFilterDialogue.hide();
            }
        });


        sendInvites_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId){

                RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();

                if (isChecked){

                    //String cboxText = checkedRadioButton.getText().toString();

                    if(checkedId == R.id.all_rb){
                        cBoxint = 0;
                    }else if(checkedId == R.id.onestar_rb){
                        cBoxint = 1;
                    }else if(checkedId == R.id.twostar_rb){
                        cBoxint = 2;
                    }else if(checkedId == R.id.threestar_rb){
                        cBoxint = 3;
                    }else if(checkedId == R.id.fourstar_rb){
                        cBoxint = 4;
                    }else if(checkedId == R.id.fivestar_rb){
                        cBoxint = 5;
                    }else if(checkedId == R.id.sixstar_rb){
                        cBoxint = 6;
                    }else if(checkedId == R.id.sevenstar_rb){
                        cBoxint = 7;
                    }
                }
            }
        });

        promotion = new Promotion();

        Bundle bundle = getArguments();

        try{
            promotionTemplate = (PromotionTemplate) bundle.getSerializable("promotion");
        } catch (Exception ex){
            promotionTemplate = null;
        }

        if(promotionTemplate == null){
            actionBar.setTitle("Restaurant Invite");
            ParseObject obj = StorageHelper.notificationTempObj.promotionId;

            discountText_tv.setText(obj.getString("discountText"));
            promotionText_tv.setText(obj.getString("promotionText"));
            discountText_middle_tv.setText(obj.getString("discountText"));
            promotionText_middle_tv.setText(obj.getString("promotionText"));

            mcurrentTime = Calendar.getInstance();
            mcurrentTime.setTime(obj.getDate("expirationDate"));

            DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
            expiryDate_tv.setText("Expires: " + df.format(mcurrentTime.getTime()));
            send_invite_btn.setVisibility(View.GONE);
            admin_note_lay.setVisibility(View.GONE);
            coupon_code_tv.setText(StorageHelper.notificationTempObj.parseObject.getString("couponNumber"));
        } else {
            discountText_tv.setText(promotionTemplate.discountText);
            promotionText_tv.setText(promotionTemplate.promotionText);
            discountText_middle_tv.setText(promotionTemplate.discountText);
            promotionText_middle_tv.setText(promotionTemplate.promotionText);

            mcurrentTime = Calendar.getInstance();
            mcurrentTime.set(Calendar.HOUR_OF_DAY, 23);
            mcurrentTime.set(Calendar.MINUTE, 59);
            mcurrentTime.set(Calendar.SECOND, 59);

            mcurrentTime.add(Calendar.DATE, 7);

            SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy", Locale.US);

            DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
            expiryDate_tv.setText("Expires: " + df.format(mcurrentTime.getTime()));
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        menu.clear();
        inflater.inflate(R.menu.main, menu);

        if(promotionTemplate != null)
            menu.findItem(R.id.delete).setVisible(true);

        getActivity().invalidateOptionsMenu();

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                getActivity().finish();
                break;
            case R.id.delete:
                new AlertDialog.Builder(getActivity())
                        .setTitle("Confirm")
                        .setMessage("Are you sure want to delete this coupon?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                pd.show();

                                ParseObject object = ParseObject.createWithoutData("PromotionTemplates", promotionTemplate.objectId);

                                object.deleteInBackground(new DeleteCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if(e == null){
                                            pd.dismiss();
                                            getActivity().getSupportFragmentManager().popBackStack();
                                        } else {
                                            pd.dismiss();
                                            Toast.makeText(getActivity(), "Error while deleting. Try again.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }})
                        .setNegativeButton("No", null).show();
                return  true;
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View view) {
        if(view.getId() == expiryDate_tv.getId()){
            if(promotionTemplate == null)
                return;

            int year = mcurrentTime.get(Calendar.YEAR);
            int month = mcurrentTime.get(Calendar.MONTH);
            int date = mcurrentTime.get(Calendar.DATE);
            DatePickerDialog mdiDialog =new DatePickerDialog(getActivity(),new DatePickerDialog.OnDateSetListener(){
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    mcurrentTime.set(year, monthOfYear,dayOfMonth);
                    DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);

                    expiryDate_tv.setText("Expires: " + df.format(mcurrentTime.getTime()));

                }
            }, year, month, date);
            mdiDialog.show();
        } else if(view.getId() == send_invite_btn.getId()){
            inviteFilterDialogue.show();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sendInvite(){

            inviteFilterDialogue.hide();
            pd.show();
            ParseObject obj = new ParseObject("Promotions");
            obj.put("restaurantId", ParseUser.getCurrentUser().get("restaurant"));
            obj.put("promotionText", promotionTemplate.promotionText);
            obj.put("expirationDate" , mcurrentTime.getTime());
            obj.put("user" , ParseUser.getCurrentUser());
            obj.put("discountText", promotionTemplate.discountText);

            promotion.parseObject = obj;

            obj.saveInBackground(new SaveCallback(){
                public void done(ParseException e){
                    if (e == null){
                        //pd.dismiss();
                        getUsers();
                    }else{
                        pd.dismiss();
                        Log.d("Result" , "Some Error While Adding");
                        e.printStackTrace();
                    }
                }
            });
    }

    public void getUsers(){
        ParseGeoPoint geoPoint = ParseUser.getCurrentUser().getParseGeoPoint("lastknownlocation");

        ParseQuery<ParseObject> parseQuery = new ParseQuery<>("_User");
        parseQuery.whereWithinMiles("lastknownlocation" , geoPoint , 20);
        parseQuery.whereNotEqualTo("objectId" , ParseUser.getCurrentUser().getObjectId());
        if(cBoxint != 0){
            parseQuery.whereEqualTo("rank" , cBoxint);
        }
        parseQuery.setLimit(1000);

        parseQuery.findInBackground(new FindCallback<ParseObject>(){
            @Override
            public void done(final List<ParseObject> list, ParseException e){
                if (e == null){
                    ParseQuery<ParseObject> extrasQuery = new ParseQuery<ParseObject>("Extras");

                    extrasQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject object, ParseException e) {
                            if(e == null){
                                String couponNumber = object.getString("couponNumber");

                                int couponInt = Integer.parseInt(couponNumber);

                                ArrayList<ParseObject> notification = new ArrayList<ParseObject>();
                                for (int i = 0; i < list.size(); i++){
                                    ParseObject parseObject = list.get(i);
                                    ParseObject tempObj = new ParseObject("Notifications");
                                    tempObj.put("userId" ,parseObject);
                                    tempObj.put("promotionId" , promotion.parseObject);
                                    tempObj.put("couponNumber", String.format("%08d", couponInt));
                                    notification.add(tempObj);
                                    couponInt++;
                                }

                                object.put("couponNumber", String.format("%08d", couponInt));
                                object.saveInBackground();
                                try {
                                    ParseObject.saveAll(notification);
                                } catch (ParseException e1) {
                                    e1.printStackTrace();
                                }
                                sendNotifications(list);
                            } else {
                                Log.d("Result", "Wrong");
                                pd.dismiss();
                            }
                        }
                    });


                } else {
                    Log.d("Result", "Wrong");
                    pd.dismiss();
                }
            }
        });
    }

    public void sendNotifications(List <ParseObject> objects){

        ParseQuery<ParseInstallation> pushQuery = ParseInstallation.getQuery();
        pushQuery.whereContainedIn("user" , objects);

        ParseObject currUser =  ParseUser.getCurrentUser().getParseObject("restaurant");

        try{
            currUser.fetchIfNeeded();
        }catch (Exception e){
        }

        currUser.get("name").toString();
        String message = currUser.get("name").toString();
        ParsePush push = new ParsePush();
        push.setQuery(pushQuery);
        push.setMessage(message);
        push.sendInBackground(new SendCallback(){
            @Override
            public void done(ParseException e){

                if(e == null){
                    pd.dismiss();
                    Toast.makeText(getActivity(), "Invite sent", Toast.LENGTH_SHORT).show();
                    inviteSuccess success = new inviteSuccess();
                    android.support.v4.app.FragmentTransaction trans1 = getActivity().getSupportFragmentManager().beginTransaction();
                    trans1.replace(R.id.frame_container,success).addToBackStack(null).commit();
                }else{

                }
            }
        });
    }
}
