package inv.sfs.com.criticapp;

import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;

public class termsndConditions extends AppCompatActivity {

    TextView terms_tv;
    WebView terms_wv;

    String text = "<p style=\"text-align: center;\"><strong>TERMS AND CONDITIONS</strong></p>\n" +
            "<p><strong>1. Introduction</strong></p>\n" +
            "<p>These Website Standard Terms and Conditions written on this webpage shall manage your use of this website. These Terms will be applied fully and affect to your use of this Website. By using this Website, you agreed to accept all terms and conditions written in here. You must not use this Website if you disagree with any of these Website Standard Terms and Conditions.</p>\n" +
            "<p>Minors or people below 18 years old are not allowed to use this Website.</p>\n" +
            "<p><strong>2. Intellectual Property Rights</strong></p>\n" +
            "<p>Other than the content you own, under these Terms, The Critics View and/or its licensors own all the intellectual property rights and materials contained in this Website.</p>\n" +
            "<p>You are granted limited license only for purposes of viewing the material contained on this Website.</p>\n" +
            "<p><strong>3. Restrictions</strong></p>\n" +
            "<ul>\n" +
            "<li>You are specifically restricted from all of the following</li>\n" +
            "<li>publishing any Website material in any other media;</li>\n" +
            "<li>selling, sublicensing and/or otherwise commercializing any Website material;</li>\n" +
            "<li>publicly performing and/or showing any Website material;</li>\n" +
            "<li>using this Website in any way that is or may be damaging to this Website;</li>\n" +
            "<li>using this Website in any way that impacts user access to this Website;</li>\n" +
            "<li>using this Website contrary to applicable laws and regulations, or in any way may cause harm to the Website, or to any person or business entity;</li>\n" +
            "<li>engaging in any data mining, data harvesting, data extracting or any other similar activity in relation to this Website;</li>\n" +
            "<li>using this Website to engage in any advertising or marketing.</li>\n" +
            "</ul>\n" +
            "<p>Certain areas of this Website are restricted from being access by you and The Critics View may further restrict access by you to any areas of this Website, at any time, in absolute discretion. Any user ID and password you may have for this Website are confidential and you must maintain confidentiality as well.</p>\n" +
            "<p><strong>4. Your Content</strong></p>\n" +
            "<p>In these Website Standard Terms and Conditions, &ldquo;Your Content&rdquo; shall mean any audio, video text, images or other material you choose to display on this Website. By displaying Your Content, you grant The Critics View a nonexclusive, worldwide irrevocable, sub licensable license to use, reproduce, adapt, publish, translate and distribute it in any and all media.</p>\n" +
            "<p>Your Content must be your own and must not be invading any third party&rsquo;s rights. The Critics View reserves the right to remove any of Your Content from this Website at any time without notice.</p>\n" +
            "<p><strong>5. No warranties</strong></p>\n" +
            "<p>This Website is provided &ldquo;as is,&rdquo; with all faults, and The Critics View express no representations or warranties, of any kind related to this Website or the materials contained on this Website. Also, nothing contained on this Website shall be interpreted as advising you.</p>\n" +
            "<p><strong>6. Limitation of liability</strong></p>\n" +
            "<p>In no event shall The Critics View, nor any of its officers, directors and employees, shall be held liable for anything arising out of or in any way connected with your use of this Website whether such liability is under contract. The Critics View, including its officers, directors and employees shall not be held liable for any indirect, consequential or special liability arising out of or in any way related to your use of this Website.</p>\n" +
            "<p><strong>7. Indemnification</strong></p>\n" +
            "<p>You hereby indemnify to the fullest extent The Critics View from and against any and/or all liabilities, costs, demands, causes of action, damages and expenses arising in any way related to your breach of any of the provisions of these Terms.</p>\n" +
            "<p><strong>8. Severability</strong></p>\n" +
            "<p>If any provision of these Terms is found to be invalid under any applicable law, such provisions shall be deleted without affecting the remaining provisions herein.</p>\n" +
            "<p><strong>9. Variation of Terms</strong></p>\n" +
            "<p>The Critics View is permitted to revise these Terms at any time as it sees fit, and by using this Website you are expected to review these Terms on a regular basis.</p>\n" +
            "<p><strong>10. Assignment</strong></p>\n" +
            "<p>The Critics View is allowed to assign, transfer, and subcontract its rights and/or obligations under these Terms without any notification. However, you are not allowed to assign, transfer, or subcontract any of your rights and/or obligations under these Terms.</p>\n" +
            "<p><strong>11. Entire Agreement</strong></p>\n" +
            "<p>These Terms constitute the entire agreement between The Critics View and you in relation to your use of this Website, and supersede all prior agreements and understandings.</p>\n" +
            "<p><strong>12. Governing Law &amp; Jurisdiction</strong></p>\n" +
            "<p>These Terms will be governed by and interpreted in accordance with the laws of the State of Texas, and you submit to the nonexclusive jurisdiction of the state and federal courts located in Texas for the resolution of any disputes.</p>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termsnd_conditions);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(this.getResources().getColor(R.color.app_basic_color)));
        getSupportActionBar().setTitle("Terms and Conditions");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*
        terms_tv = findViewById(R.id.terms_tv);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            terms_tv.setText(Html.fromHtml(text,Html.FROM_HTML_MODE_LEGACY));
        } else {
            terms_tv.setText(Html.fromHtml(text));
        }
        */
        terms_wv = findViewById(R.id.terms_wv);

        terms_wv.loadData(text, "text/html; charset=utf-8", "UTF-8");
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
