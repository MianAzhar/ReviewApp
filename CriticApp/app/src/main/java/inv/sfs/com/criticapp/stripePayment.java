package inv.sfs.com.criticapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;

import java.util.HashMap;

import inv.sfs.com.criticapp.Models.Constants;

public class stripePayment extends AppCompatActivity implements View.OnClickListener {

    Button paystripebtn;
    EditText expet,creditcardNo, cvc_et, name_et;

    String chargeId = "", email;

    int amount = 0;
    TransparentProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stripe_payment);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(this.getResources().getColor(R.color.app_basic_color)));
        getSupportActionBar().setTitle("Payment");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        email = getIntent().getStringExtra("email");

        pd = new TransparentProgressDialog(this, R.drawable.loader);

        cvc_et = findViewById(R.id.cvc_et);
        creditcardNo = (EditText) findViewById(R.id.creditcardNo);
        expet = (EditText) findViewById(R.id.expet);
        paystripebtn = (Button) findViewById(R.id.paystripebtn);
        paystripebtn.setOnClickListener(this);

        expet.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0 && (editable.length() % 3) == 0) {
                    final char c = editable.charAt(editable.length() - 1);
                    if ('/' == c) {
                        editable.delete(editable.length() - 1, editable.length());
                    }
                }
                if (editable.length() > 0 && (editable.length() % 3) == 0) {
                    char c = editable.charAt(editable.length() - 1);
                    if (Character.isDigit(c) && TextUtils.split(editable.toString(), String.valueOf("/")).length <= 2) {
                        editable.insert(editable.length() - 1, String.valueOf("/"));
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        creditcardNo.addTextChangedListener(new FourDigitCardFormatWatcher());

        amount = getIntent().getIntExtra("amount", 10);
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == paystripebtn.getId()){
            if(!validateForm())
                return;

            makePayment();
        }
    }

    public boolean validateForm(){
        if(creditcardNo.getText().toString().isEmpty()){
            creditcardNo.setError("Please enter card no.");
            creditcardNo.requestFocus();

            return false;
        }

        if(cvc_et.getText().toString().isEmpty()){
            cvc_et.setError("Please enter cvc");
            cvc_et.requestFocus();

            return false;
        }

        if(expet.getText().toString().isEmpty()){
            expet.setError("Please enter expiration date");
            expet.requestFocus();

            return false;
        }

        String exp = expet.getText().toString();

        if(exp.split("/").length < 2){
            expet.setError("Invalid expiration date");
            expet.requestFocus();

            return false;
        }

        String month = exp.split("/")[0];
        String year = exp.split("/")[1];

        Card card = new Card(creditcardNo.getText().toString(), Integer.parseInt(month), Integer.parseInt(year) + 2000, cvc_et.getText().toString());

        if(!card.validateNumber()){
            creditcardNo.setError("Invalid card no.");
            creditcardNo.requestFocus();

            return false;
        }

        if(!card.validateCVC()){
            cvc_et.setError("Invalid CVC");
            cvc_et.requestFocus();

            return false;
        }

        if(!card.validateExpiryDate()){
            expet.setError("Invalid expiration date");
            expet.requestFocus();

            return false;
        }

        return true;
    }

    private void makePayment(){
        pd.show();
        String exp = expet.getText().toString();

        String month = exp.split("/")[0];
        String year = exp.split("/")[1];
        final Card card = new Card(creditcardNo.getText().toString(), Integer.parseInt(month), Integer.parseInt(year) + 2000, cvc_et.getText().toString());

        final Stripe stripe = new Stripe(this, Constants.STRIPE_KEY);
        stripe.createToken(
                card,
                new TokenCallback() {
                    public void onSuccess(Token token) {
                        HashMap<String, Object> params = new HashMap<>();
                        params.put("amount", amount);
                        params.put("email", email);
                        params.put("stripeToken", token.getId());

                        // Send token to your server
                        ParseCloud.callFunctionInBackground("doPayment", params, new FunctionCallback<Object>() {
                            @Override
                            public void done(Object object, ParseException e) {
                                if(e == null){
                                    chargeId = ((HashMap)object).get("id").toString();

                                    createCustomer(card);
                                } else {
                                    pd.dismiss();
                                    Toast.makeText(stripePayment.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    public void onError(Exception error) {
                        pd.dismiss();
                        // Show localized error message
                        Toast.makeText(stripePayment.this,
                                error.getLocalizedMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    private void createCustomer(Card card){
        final Stripe stripe = new Stripe(this, Constants.STRIPE_KEY);
        stripe.createToken(
                card,
                new TokenCallback() {
                    public void onSuccess(Token token) {
                        HashMap<String, Object> params = new HashMap<>();
                        params.put("email", email);
                        params.put("stripeToken", token.getId());

                        // Send token to your server
                        ParseCloud.callFunctionInBackground("createCustomer", params, new FunctionCallback<Object>() {
                            @Override
                            public void done(Object object, ParseException e) {
                                if(e == null){
                                    pd.dismiss();
                                    String customerId = ((HashMap)object).get("id").toString();

                                    Intent returnIntent = new Intent();
                                    returnIntent.putExtra("customerId", customerId);
                                    returnIntent.putExtra("chargeId", chargeId);
                                    setResult(Activity.RESULT_OK, returnIntent);
                                    finish();
                                } else {
                                    pd.dismiss();
                                    Toast.makeText(stripePayment.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    public void onError(Exception error) {
                        pd.dismiss();
                        // Show localized error message
                        Toast.makeText(stripePayment.this,
                                error.getLocalizedMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }
}
