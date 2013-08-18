package com.hastagqq.app;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.hastagqq.app.util.Constants;

/**
 * @author avendael
 */
public class GcmIntentService extends IntentService {
    private static final String TAG = GcmIntentService.class.getSimpleName();

    public static final int NOTIFICATION_ID = 1;

    public GcmIntentService() {
        super(Constants.SENDER_ID);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "::onHandleIntent() -- START");
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (extras != null && !extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                sendNotification(getString(R.string.msg_view_news));
                Log.i(TAG, "::onHandleIntent() " + extras.toString());
            }
        }

        GcmBroadcastReceiver.completeWakefulIntent(intent);
        Log.d(TAG, "::onHandleIntent() -- END");
    }

    private void sendNotification(String msg) {
        Log.d(TAG, "::sendNotification() -- START");
        Log.d(TAG, "::sendNotification() -- message " + msg);
        NotificationManager notificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                 .setSmallIcon(R.drawable.ic_launcher)
                 .setContentTitle(getString(R.string.msg_new_news))
                 .setStyle(new NotificationCompat.BigTextStyle()
                         .bigText(msg))
                 .setContentText(msg);

        builder.setContentIntent(contentIntent);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
        Log.d(TAG, "::sendNotification() -- END");
    }
}
