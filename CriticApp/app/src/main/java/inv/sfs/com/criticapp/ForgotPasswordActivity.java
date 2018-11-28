package inv.sfs.com.criticapp;

import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    EditText email_et;
    Button sendEmail_btn;
    TextView confirm_message;
    TransparentProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(this.getResources().getColor(R.color.app_basic_color)));
        getSupportActionBar().setTitle("Reset Password");

        pd = new TransparentProgressDialog(this, R.drawable.loader);

        email_et = findViewById(R.id.forgot_password_email);

        sendEmail_btn = findViewById(R.id.reset_password_btn);
        sendEmail_btn.setOnClickListener(this);

        confirm_message = findViewById(R.id.message_tv);
        confirm_message.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == sendEmail_btn.getId()){
            String email = email_et.getText().toString();

            email_et.setError(null);

            if(email.equals("")){
                email_et.setError("Enter Email");
                email_et.findFocus();
                return;
            }else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                email_et.setError("Invalid Email Address");
                email_et.findFocus();
                return;
            }

            pd.show();

            ParseUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
                @Override
                public void done(ParseException e) {
                    if(e == null){
                        pd.dismiss();
                        confirm_message.setVisibility(View.VISIBLE);
                    } else {
                        pd.dismiss();
                        Toast.makeText(ForgotPasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();;
                    }
                }
            });
        }
    }
}
