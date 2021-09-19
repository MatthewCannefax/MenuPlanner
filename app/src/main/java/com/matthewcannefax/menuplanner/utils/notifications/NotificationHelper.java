package com.matthewcannefax.menuplanner.utils.notifications;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import androidx.annotation.RequiresApi;

import java.util.Calendar;
import java.util.Date;

public class NotificationHelper {

    public static final int JOB_ID = 0;

    private static final int NOTIFICATION_HOUR = 15; //3 o'clock
    private Context mContext;
    JobScheduler mScheduler;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public NotificationHelper(Context context){
        mContext = context;
        mScheduler = (JobScheduler)mContext.getSystemService(Context.JOB_SCHEDULER_SERVICE);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void scheduleJob(){

        if (isJobRunning()) {
            ComponentName serviceName = new ComponentName(mContext.getPackageName(),
                    NotificationJobService.class.getName());

            JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, serviceName);
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
            builder.setMinimumLatency(getNotificationTime());

            mScheduler.schedule(builder.build());
        }
    }

    private long getNotificationTime(){
        long returnTime = 0;

        Calendar cal = Calendar.getInstance();
        Date time = cal.getTime();
        int currentHour = cal.get(Calendar.HOUR_OF_DAY);

        if(currentHour < NOTIFICATION_HOUR){
            //the difference of hours between the notification time (3 oclock) and the current time
            int diff = NOTIFICATION_HOUR - currentHour;

            //convert the difference into milliseconds
            long seconds = diff * 3600;
            returnTime = seconds * 1000;
        }else{
            //the difference between the current time and the notification time
            int diff = currentHour - NOTIFICATION_HOUR;

            //subtract the difference from 24 hours so the notification will be scheduled for the next day
            diff = 24 - diff;
            long seconds = diff *3600;
            returnTime = seconds * 1000;
        }

        return returnTime;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private boolean isJobRunning(){
        boolean hasBeenScheduled = false;
        for(JobInfo jobInfo : mScheduler.getAllPendingJobs()){
            if(jobInfo.getId() == JOB_ID){
                hasBeenScheduled = true;
                break;
            }
        }
        return hasBeenScheduled;
    }

}
