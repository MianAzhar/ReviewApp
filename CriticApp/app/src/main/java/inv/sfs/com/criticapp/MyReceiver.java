package inv.sfs.com.criticapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by applepc on 27/01/2018.
 */

public class MyReceiver extends ParsePushBroadcastReceiver{

    public String notificationTitle,notificationContent,uri;

    @Override
    public void onReceive(Context context, Intent intent){



        try {
            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
            notificationContent = json.getString("alert").toString();
        } catch (JSONException e){
            e.printStackTrace();
        }
        //Toast.makeText(context, notificationContent , Toast.LENGTH_SHORT).show();

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class), 0);

        Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.logo_croped);
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.logo_croped)
                .setLargeIcon(icon)
                .setContentTitle(notificationContent)
                .setContentText("Review Us and Get a Discount Coupon")
                .setAutoCancel(true)
                .setVibrate(new long[] { 0, 1000 })
                .setLights(Color.RED, 3000, 3000)
                .setSound(uri)
                .setContentIntent(contentIntent);

        //mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify("ss", 1, mBuilder.build());
    }
}
