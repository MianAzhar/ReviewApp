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
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseFile;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class addInvite extends Fragment implements View.OnClickListener {



    TextView select_date;
    ImageView select_photo;
    private static final int SELECT_PICTURE = 1;
    Uri selectedImageUri;
    ParseFile parse_image_file = null;

    public addInvite(){
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_invite, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("Invite");

        select_photo = (ImageView) getView().findViewById(R.id.select_photo);
        select_date = (TextView) getView().findViewById(R.id.select_date);
        select_date.setOnClickListener(this);
        select_photo.setOnClickListener(this);

       /* image_iv.setImageBitmap(StorageHelper.bitmapImageFile);
        if(StorageHelper.bitmapImageFile != null){
            image_iv.getLayoutParams().height = 800;
            image_iv.getLayoutParams().width = 800;
        }*/
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View view){

        if(view.getId() == select_date.getId()){

            Calendar mcurrentTime = Calendar.getInstance();
            int year = mcurrentTime.get(Calendar.YEAR);
            int month = mcurrentTime.get(Calendar.MONTH);
            int date = mcurrentTime.get(Calendar.DATE);
            DatePickerDialog mdiDialog =new DatePickerDialog(getActivity(),new DatePickerDialog.OnDateSetListener(){
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    //Toast.makeText(getActivity(),year+ " "+monthOfYear+" "+dayOfMonth, Toast.LENGTH_LONG).show();
                    select_date.setText(year+"/" +monthOfYear+"/"+dayOfMonth);
                }
            }, year, month, date);
            mdiDialog.show();
        }else if(view.getId() == select_photo.getId()){
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,
                    "Select Picture"), SELECT_PICTURE);
        }
    }


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

}
