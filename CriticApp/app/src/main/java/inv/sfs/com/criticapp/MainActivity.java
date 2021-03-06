package inv.sfs.com.criticapp;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.Volley;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import inv.sfs.com.criticapp.Models.Constants;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    PrefrencesHelper preference;
    private static final int SELECT_PICTURE = 1;
    Uri selectedImageUri;
    ParseFile parse_image_file = null;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("The Critics View");
        preference = PrefrencesHelper.getInstance(this);

        StorageHelper.requestQueue = Volley.newRequestQueue(getApplicationContext());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //------------- Getting Navigation Top -----//
        View navigation_header =  navigationView.getHeaderView(0);
        TextView user_name = (TextView) navigation_header.findViewById(R.id.user_name);
        TextView email_add = (TextView) navigation_header.findViewById(R.id.email);

        /*
        ImageView rankstar1iv = (ImageView) navigation_header.findViewById(R.id.rankstar1iv);
        ImageView rankstar2iv = (ImageView) navigation_header.findViewById(R.id.rankstar2iv);
        ImageView rankstar3iv = (ImageView) navigation_header.findViewById(R.id.rankstar3iv);
        ImageView rankstar4iv = (ImageView) navigation_header.findViewById(R.id.rankstar4iv);
        ImageView rankstar5iv = (ImageView) navigation_header.findViewById(R.id.rankstar5iv);
        */

        if(ParseUser.getCurrentUser() != null){

            TextView rank_tv = navigation_header.findViewById(R.id.userRank_tv);

            try {
                int user_rank = (int) ParseUser.getCurrentUser().get("rank");

                if(user_rank == 1){
                    rank_tv.setText(R.string.rank_1);
                }else if(user_rank == 2){
                    rank_tv.setText(R.string.rank_2);
                }else if(user_rank == 3){
                    rank_tv.setText(R.string.rank_3);
                }else if(user_rank == 4){
                    rank_tv.setText(R.string.rank_4);
                }else if(user_rank == 5){
                    rank_tv.setText(R.string.rank_5);
                }else if(user_rank == 6){
                    rank_tv.setText(R.string.rank_6);
                }else if(user_rank == 7){
                    rank_tv.setText(R.string.rank_7);
                }
            }catch (Exception e){
                rank_tv.setText(R.string.rank_1);
             }

        } else {
            LinearLayout rank_lay = navigation_header.findViewById(R.id.rank_lay);
            rank_lay.setVisibility(View.GONE);
        }

        try{
            email_add.setText(ParseUser.getCurrentUser().getEmail());
            user_name.setText(ParseUser.getCurrentUser().get("name").toString());
        }catch (Exception e){
        }

        //--- For without Login ---//
        Menu menu =navigationView.getMenu();

        if(preference.getBoolObject("user_logged_in")){

            MenuItem invite_owner = menu.findItem(R.id.invite_owner);
            invite_owner.setVisible(true);

            MenuItem invite_users = menu.findItem(R.id.invite_users);
            invite_users.setVisible(false);

            MenuItem my_reviews = menu.findItem(R.id.my_reviews);
            my_reviews.setVisible(true);

            MenuItem my_events = menu.findItem(R.id.my_events);
            my_events.setVisible(false);

            MenuItem reports = menu.findItem(R.id.reports);
            reports.setVisible(false);

            MenuItem score = menu.findItem(R.id.score);
            score.setVisible(false);

            MenuItem invite_critics = menu.findItem(R.id.invite_critics);
            invite_critics.setVisible(false);

            MenuItem logout = menu.findItem(R.id.logout);
            logout.setVisible(true);

            MenuItem be_critic = menu.findItem(R.id.be_critic);
            be_critic.setVisible(true);

            MenuItem admin_portal = menu.findItem(R.id.admin_portal);
            admin_portal.setVisible(false);

        }else if (preference.getBoolObject("admin_logged_in")){
            MenuItem invite_owner = menu.findItem(R.id.invite_owner);
            invite_owner.setVisible(true);

            MenuItem invite_users = menu.findItem(R.id.invite_users);
            invite_users.setVisible(false);

            MenuItem my_reviews = menu.findItem(R.id.my_reviews);
            my_reviews.setVisible(true);

            MenuItem my_events = menu.findItem(R.id.my_events);
            my_events.setVisible(false);

            MenuItem reports = menu.findItem(R.id.reports);
            reports.setVisible(false);

            MenuItem score = menu.findItem(R.id.score);
            score.setVisible(true);

            MenuItem invite_critics = menu.findItem(R.id.invite_critics);
            invite_critics.setVisible(false);

            MenuItem logout = menu.findItem(R.id.logout);
            logout.setVisible(true);

            MenuItem be_critic = menu.findItem(R.id.be_critic);
            be_critic.setVisible(true);

            MenuItem admin_portal = menu.findItem(R.id.admin_portal);
            admin_portal.setVisible(false);

            MenuItem top_ten = menu.findItem(R.id.top_ten);
            top_ten.setVisible(true);

            MenuItem map_view = menu.findItem(R.id.map_view);
            map_view.setVisible(true);


        }else{
            MenuItem invite_owner = menu.findItem(R.id.invite_owner);
            invite_owner.setVisible(false);

            MenuItem invite_users = menu.findItem(R.id.invite_users);
            invite_users.setVisible(false);

            MenuItem my_reviews = menu.findItem(R.id.my_reviews);
            my_reviews.setVisible(false);

            MenuItem my_events = menu.findItem(R.id.my_events);
            my_events.setVisible(false);

            MenuItem reports = menu.findItem(R.id.reports);
            reports.setVisible(false);

            MenuItem score = menu.findItem(R.id.score);
            score.setVisible(false);

            MenuItem invite_critics = menu.findItem(R.id.invite_critics);
            invite_critics.setVisible(false);

            MenuItem logout = menu.findItem(R.id.logout);
            logout.setVisible(false);
        }
            if(StorageHelper.alternative_login){

                home dashboard_fragment = new home();
                android.support.v4.app.FragmentTransaction trans1 = this.getSupportFragmentManager().beginTransaction();
                trans1.replace(R.id.frame_container,dashboard_fragment).addToBackStack(Constants.MAP_FRAGMENT);

                StorageHelper.alternative_login = false;
                StorageHelper.uiBlock = false;
                StorageHelper.isNewReview = false;
                Bundle bundle = new Bundle();
                bundle.putString("position" , StorageHelper.alternative_login_position);
                reviewDetailsfrag addreview = new reviewDetailsfrag();
                addreview.setArguments(bundle);
                //android.support.v4.app.FragmentTransaction ft = ((AppCompatActivity) this).getSupportFragmentManager()
                 //       .beginTransaction();
                trans1.replace(R.id.frame_container,addreview).addToBackStack(null).commit();
            }else{
                home dashboard_fragment = new home();
                android.support.v4.app.FragmentTransaction trans1 = this.getSupportFragmentManager().beginTransaction();
                trans1.replace(R.id.frame_container,dashboard_fragment).addToBackStack(Constants.MAP_FRAGMENT).commit();
            }
            //finishAffinity();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE){
                selectedImageUri = data.getData();

                try {
                    Bitmap bitmapImage =decodeBitmap(selectedImageUri);
                    //Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    StorageHelper.bitmapImageFile = bitmapImage;
                    //image_iv.setImageBitmap(bitmapImage);
                    /*event_image_iv.setImageBitmap(bitmapImage);
                    event_image_iv.getLayoutParams().height = 800;
                    event_image_iv.getLayoutParams().width = 800;
                    event_image_iv.setBackground(null);*/

                    //--------- Generating Parse File ------------//

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, bos);
                    byte[] bitmapdata = bos.toByteArray();
                    parse_image_file = new ParseFile("loc_image.png", bitmapdata);
                    //parse_image_file = new ParseFile(new File(selectedImageUri.getPath()));
                    //parse_image_file.saveInBackground();
                    StorageHelper.parseImageFile =  parse_image_file;

                } catch (FileNotFoundException e){
                    e.printStackTrace();
                    Log.d("Result" , "Could not load image. Please try again");
                    // Toast.makeText(this, "Could not load image. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }



    public Bitmap decodeBitmap(Uri selectedImage) throws FileNotFoundException{
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(this.getContentResolver().openInputStream(selectedImage), null, o);

        final int REQUIRED_SIZE = 500;

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
        return BitmapFactory.decodeStream(this.getContentResolver().openInputStream(selectedImage), null, o2);
    }


    @Override
    public void onBackPressed(){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            Log.i("MainActivity", "popping backstack");
            fm.popBackStack();
        }else if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item){
         int id = item.getItemId();

        if (id == R.id.top_ten){
            StorageHelper.topTen = true;
            allReviewsfrag allReviews = new allReviewsfrag();
            android.support.v4.app.FragmentTransaction trans1 = this.getSupportFragmentManager().beginTransaction();
            trans1.replace(R.id.frame_container,allReviews).addToBackStack(null).commit();
         } else if (id == R.id.nav_send){

        }else if(id == R.id.invite_owner){
            userInvites dashboard_fragment = new userInvites();
            android.support.v4.app.FragmentTransaction trans1 = this.getSupportFragmentManager().beginTransaction();
            trans1.replace(R.id.frame_container,dashboard_fragment).addToBackStack(null).commit();
         }else if(id == R.id.logout){

            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
            installation.remove("user");
            installation.saveInBackground();

            preference.setBoolObject("admin_logged_in" , false);
            preference.setBoolObject("user_logged_in" , false);
            ParseUser.getCurrentUser().logOut();

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }else if(id == R.id.invite_critics){

            inviteCriticsUpdated inviteCritics = new inviteCriticsUpdated();
            android.support.v4.app.FragmentTransaction trans1 = this.getSupportFragmentManager().beginTransaction();
            trans1.replace(R.id.frame_container,inviteCritics).addToBackStack(null).commit();


            /*inviteCriticsfrag addreview = new inviteCriticsfrag();
            android.support.v4.app.FragmentTransaction trans1 = this.getSupportFragmentManager().beginTransaction();
            trans1.replace(R.id.frame_container,addreview).addToBackStack(null).commit();*/
        }else if(id == R.id.map_view){
            home dashboard_fragment = new home();
            android.support.v4.app.FragmentTransaction trans1 = this.getSupportFragmentManager().beginTransaction();
            trans1.replace(R.id.frame_container,dashboard_fragment).addToBackStack(null).commit();

        }else if(id == R.id.be_critic){
            if(preference.getBoolObject("user_logged_in")){
                home dashboard_fragment = new home();
                android.support.v4.app.FragmentTransaction trans1 = this.getSupportFragmentManager().beginTransaction();
                trans1.replace(R.id.frame_container,dashboard_fragment).addToBackStack(null).commit();
            }else if(preference.getBoolObject("admin_logged_in")){
                home dashboard_fragment = new home();
                android.support.v4.app.FragmentTransaction trans1 = this.getSupportFragmentManager().beginTransaction();
                trans1.replace(R.id.frame_container,dashboard_fragment).addToBackStack(null).commit();
            }else{
                Intent i = new Intent(this,login.class);
                startActivity(i);
            }

        }else if(id == R.id.admin_portal){
            Intent i = new Intent(this,loginAdmin.class);
            startActivity(i);
        }else if(id == R.id.my_reviews){
            myReviews myreviews = new myReviews();
            android.support.v4.app.FragmentTransaction trans1 = this.getSupportFragmentManager().beginTransaction();
            trans1.replace(R.id.frame_container,myreviews).addToBackStack(null).commit();

        }else if(id == R.id.reports){
            reports Reports = new reports();
            android.support.v4.app.FragmentTransaction trans1 = this.getSupportFragmentManager().beginTransaction();
            trans1.replace(R.id.frame_container,Reports).addToBackStack(null).commit();
        }else if(id == R.id.score){
            myCriticReviewsUpdated myreviews = new myCriticReviewsUpdated();
            android.support.v4.app.FragmentTransaction trans1 = this.getSupportFragmentManager().beginTransaction();
            trans1.replace(R.id.frame_container,myreviews).addToBackStack(null).commit();
            /*
            criticScores scores = new criticScores();
            android.support.v4.app.FragmentTransaction trans1 = this.getSupportFragmentManager().beginTransaction();
            trans1.replace(R.id.frame_container,scores).addToBackStack(null).commit();
            */
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
