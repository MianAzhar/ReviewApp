package inv.sfs.com.criticapp;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    PrefrencesHelper preference;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("The Critics Review");
        preference = PrefrencesHelper.getInstance(this);

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
            invite_users.setVisible(true);

            MenuItem my_reviews = menu.findItem(R.id.my_reviews);
            my_reviews.setVisible(true);

            MenuItem my_events = menu.findItem(R.id.my_events);
            my_events.setVisible(true);

            MenuItem reports = menu.findItem(R.id.reports);
            reports.setVisible(false);

            MenuItem score = menu.findItem(R.id.score);
            score.setVisible(false);

            MenuItem invite_critics = menu.findItem(R.id.invite_critics);
            invite_critics.setVisible(true);

            MenuItem logout = menu.findItem(R.id.logout);
            logout.setVisible(true);

            MenuItem be_critic = menu.findItem(R.id.be_critic);
            be_critic.setVisible(true);

            MenuItem admin_portal = menu.findItem(R.id.admin_portal);
            admin_portal.setVisible(false);



        }else if (preference.getBoolObject("admin_logged_in")){
            MenuItem invite_owner = menu.findItem(R.id.invite_owner);
            invite_owner.setVisible(false);

            MenuItem invite_users = menu.findItem(R.id.invite_users);
            invite_users.setVisible(false);

            MenuItem my_reviews = menu.findItem(R.id.my_reviews);
            my_reviews.setVisible(false);

            MenuItem my_events = menu.findItem(R.id.my_events);
            my_events.setVisible(true);

            MenuItem reports = menu.findItem(R.id.reports);
            reports.setVisible(true);

            MenuItem score = menu.findItem(R.id.score);
            score.setVisible(true);

            MenuItem invite_critics = menu.findItem(R.id.invite_critics);
            invite_critics.setVisible(true);

            MenuItem logout = menu.findItem(R.id.logout);
            logout.setVisible(true);

            MenuItem be_critic = menu.findItem(R.id.be_critic);
            be_critic.setVisible(false);

            MenuItem admin_portal = menu.findItem(R.id.admin_portal);
            admin_portal.setVisible(false);

            MenuItem top_ten = menu.findItem(R.id.top_ten);
            top_ten.setVisible(false);

            MenuItem map_view = menu.findItem(R.id.map_view);
            map_view.setVisible(false);


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

        home dashboard_fragment = new home();
        android.support.v4.app.FragmentTransaction trans1 = this.getSupportFragmentManager().beginTransaction();
        trans1.replace(R.id.frame_container,dashboard_fragment).addToBackStack(null).commit();

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
            allReviewsfrag allReviews = new allReviewsfrag();
            android.support.v4.app.FragmentTransaction trans1 = this.getSupportFragmentManager().beginTransaction();
            trans1.replace(R.id.frame_container,allReviews).addToBackStack(null).commit();
         } else if (id == R.id.nav_send){

        }else if(id == R.id.invite_users){
            userInvites dashboard_fragment = new userInvites();
            android.support.v4.app.FragmentTransaction trans1 = this.getSupportFragmentManager().beginTransaction();
            trans1.replace(R.id.frame_container,dashboard_fragment).addToBackStack(null).commit();
         }else if(id == R.id.logout){
            preference.setBoolObject("admin_logged_in" , false);
            preference.setBoolObject("user_logged_in" , false);
            ParseUser.getCurrentUser().logOut();

            //finishAffinity();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }else if(id == R.id.invite_critics){
            inviteCriticsfrag addreview = new inviteCriticsfrag();
            android.support.v4.app.FragmentTransaction trans1 = this.getSupportFragmentManager().beginTransaction();
            trans1.replace(R.id.frame_container,addreview).addToBackStack(null).commit();
        }else if(id == R.id.map_view){
            home dashboard_fragment = new home();
            android.support.v4.app.FragmentTransaction trans1 = this.getSupportFragmentManager().beginTransaction();
            trans1.replace(R.id.frame_container,dashboard_fragment).addToBackStack(null).commit();

        }else if(id == R.id.be_critic){
            if(preference.getBoolObject("user_logged_in")){
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
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
