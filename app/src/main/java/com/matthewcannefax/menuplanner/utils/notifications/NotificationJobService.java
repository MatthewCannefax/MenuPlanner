package com.matthewcannefax.menuplanner.utils.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.recipe.menuList.MenuListActivity;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class NotificationJobService extends JobService {

    NotificationManager mNotifyManager;

    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";


    public void createNotificationChannel(){
        mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(
                    PRIMARY_CHANNEL_ID,
                    "THIS NEEDS TO CHANGE",
                    NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("THIS ALSO NEEDS TO CHANGE!!");

            mNotifyManager.createNotificationChannel(notificationChannel);
        }
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        createNotificationChannel();

        PendingIntent contentPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MenuListActivity.class), PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setContentTitle(getApplicationContext().getString(R.string.almost_dinner_time))
                .setContentText(getApplicationContext().getString(R.string.whats_for_dinner))
                .setContentIntent(contentPendingIntent)
                .setSmallIcon(R.drawable.almost_time)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true);
        //look into more info to add to the builder

        mNotifyManager.notify(0, builder.build());

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }
}
