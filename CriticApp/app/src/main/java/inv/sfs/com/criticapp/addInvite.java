package inv.sfs.com.criticapp;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SendCallback;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import inv.sfs.com.criticapp.Models.FullReviewModel;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class addInvite extends Fragment implements View.OnClickListener {


    ParseObject obj;
    EditText discount_text_et;
    TextView expiryDate_tv;
    Button create_coupon;
    EditText comments_et;
    String comments_st;
    TransparentProgressDialog pd;
    Calendar mcurrentTime;

    public addInvite(){
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_invite, container, false);

        mcurrentTime = Calendar.getInstance();
        comments_et = view.findViewById(R.id.comments_et);
        create_coupon = view.findViewById(R.id.send_invite);
        discount_text_et = view.findViewById(R.id.discount_text_et);
        expiryDate_tv = view.findViewById(R.id.expiryDate_tv);
        expiryDate_tv.setOnClickListener(this);
        create_coupon.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("Create New Coupon");

        pd = new TransparentProgressDialog(getContext(), R.drawable.loader);


       /* image_iv.setImageBitmap(StorageHelper.bitmapImageFile);
        if(StorageHelper.bitmapImageFile != null){
            image_iv.getLayoutParams().height = 800;
            image_iv.getLayoutParams().width = 800;
        }*/
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View view){
        /*
        if(view.getId() == select_date.getId()){

            mcurrentTime = Calendar.getInstance();
            int year = mcurrentTime.get(Calendar.YEAR);
            int month = mcurrentTime.get(Calendar.MONTH);
            int date = mcurrentTime.get(Calendar.DATE);
            DatePickerDialog mdiDialog =new DatePickerDialog(getActivity(),new DatePickerDialog.OnDateSetListener(){
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    //Toast.makeText(getActivity(),year+ " "+monthOfYear+" "+dayOfMonth, Toast.LENGTH_LONG).show();
                    select_date.setText(year+"/" +monthOfYear+"/"+dayOfMonth);
                    mcurrentTime.set(year, monthOfYear,dayOfMonth);
                }
            }, year, month, date);
            mdiDialog.show();
        }else if(view.getId() == select_photo.getId()){
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,
                    "Select Picture"), SELECT_PICTURE);
        }else */
        if(view.getId() == create_coupon.getId()){

            if(validate()){

                pd.show();
                obj = new ParseObject("PromotionTemplates");
                obj.put("restaurantId", ParseUser.getCurrentUser().get("restaurant"));
                obj.put("promotionText", comments_et.getText().toString());
                obj.put("userId" , ParseUser.getCurrentUser());
                obj.put("discountText", discount_text_et.getText().toString());

                obj.saveInBackground(new SaveCallback(){
                    public void done(ParseException e){
                        if (e == null){
                            pd.dismiss();
                            getActivity().getSupportFragmentManager().popBackStackImmediate();
                            //getUsers();
                        }else{
                            pd.dismiss();
                            Log.d("Result" , "Some Error While Adding");
                            e.printStackTrace();
                        }
                    }
                });
            }
        }else if(view.getId() == expiryDate_tv.getId()){
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
        }
    }

    /*
    public void getUsers(){
        ParseGeoPoint geoPoint = ParseUser.getCurrentUser().getParseGeoPoint("lastknownlocation");

        ParseQuery<ParseObject> parseQuery = new ParseQuery<>("_User");
        parseQuery.whereWithinMiles("lastknownlocation" , geoPoint , 20);
        parseQuery.whereNotEqualTo("objectId" , ParseUser.getCurrentUser().getObjectId());
        parseQuery.setLimit(1000);

        parseQuery.findInBackground(new FindCallback<ParseObject>(){
            @Override
            public void done(List<ParseObject> list, ParseException e){
                if (e == null){
                    if (!list.isEmpty()){

                        ArrayList <ParseObject> notification = new ArrayList<ParseObject>();
                        for (int i = 0; i < list.size(); i++){
                            ParseObject parseObject = list.get(i);
                            ParseObject tempObj = new ParseObject("Notifications");
                            tempObj.put("userId" ,parseObject);
                            tempObj.put("promotionId" , obj);
                            notification.add(tempObj);

                        }
                        try {
                            ParseObject.saveAll(notification);
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                        sendNotifications(list);
                    } else{
                        Toast.makeText(getActivity(), "Error getting Users", Toast.LENGTH_SHORT).show();
                        Log.d("Result", "Empty");
                        pd.dismiss();
                    }
                } else {
                    Log.d("Result", "Wrong");
                    pd.dismiss();
                }
            }
        });
    }

    public void sendNotifications(List <ParseObject> objects){

        ParseQuery pushQuery = ParseInstallation.getQuery();
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
    */

    public boolean validate(){

        comments_st = comments_et.getText().toString();
        String discount_text = discount_text_et.getText().toString();

        if(comments_st.equals("")){
            comments_et.setError("Enter Text");
            comments_et.requestFocus();
            return false;
        } else if(discount_text.equals("")){
            discount_text_et.setError("Enter discount");
            discount_text_et.requestFocus();
            return false;
        }
        return true;
    }

    /*
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){

        if (resultCode == RESULT_OK){
            if (requestCode == SELECT_PICTURE){
                selectedImageUri = data.getData();

                try {
                    Bitmap bitmapImage =decodeBitmap(selectedImageUri);
                    select_photo.setImageBitmap(bitmapImage);
                    select_photo.setImageBitmap(bitmapImage);
                    select_photo.getLayoutParams().height = 750;
                    select_photo.getLayoutParams().width = 1500;
                    select_photo.setBackground(null);

                    //--------- Generating Parse File ------------//

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmapImage.compress(Bitmap.CompressFormat.PNG, 0, bos);
                    byte[] bitmapdata = bos.toByteArray();
                    parse_image_file = new ParseFile("loc_image.png", bitmapdata);
                    //StorageHelper.parseImageFile =  parse_image_file;

                } catch (FileNotFoundException e){
                    e.printStackTrace();
                    Log.d("Result" , "Could not load image. Please try again");
                }
            }
        }
    }


    public Bitmap decodeBitmap(Uri selectedImage) throws FileNotFoundException{
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(selectedImage), null, o);

        final int REQUIRED_SIZE = 100;

        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(selectedImage), null, o2);
    }
    */
}
