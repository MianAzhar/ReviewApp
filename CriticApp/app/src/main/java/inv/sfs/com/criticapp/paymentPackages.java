package inv.sfs.com.criticapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import inv.sfs.com.criticapp.Models.Restaurant;

public class paymentPackages extends AppCompatActivity {

    public static int STRIPE_CODE = 33;
    Button selectbtnMonthly, selectBtnYearly;
    String email;
    int amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_packages);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(this.getResources().getColor(R.color.app_basic_color)));
        getSupportActionBar().setTitle("Payment Packages");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        selectbtnMonthly = (Button) findViewById(R.id.monthlyBtn);
        selectBtnYearly = (Button) findViewById(R.id.yearlyBtn);

        email = getIntent().getStringExtra("email");

        selectbtnMonthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplication(), stripePayment.class);
                amount = 10;
                i.putExtra("amount", 10);
                i.putExtra("email", email);
                startActivityForResult(i, STRIPE_CODE);
            }
        });

        selectBtnYearly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplication(), stripePayment.class);
                amount = 120;
                i.putExtra("amount", 120);
                i.putExtra("email", email);
                startActivityForResult(i, STRIPE_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == STRIPE_CODE){
            if(resultCode == Activity.RESULT_OK){
                Intent returnIntent = new Intent();
                returnIntent.putExtra("customerId",data.getStringExtra("customerId"));
                returnIntent.putExtra("chargeId",data.getStringExtra("chargeId"));
                returnIntent.putExtra("amount", amount);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        }
    }
}
