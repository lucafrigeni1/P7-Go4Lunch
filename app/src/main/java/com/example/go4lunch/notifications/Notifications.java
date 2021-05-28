package com.example.go4lunch.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import com.example.go4lunch.R;
import com.example.go4lunch.models.Restaurant;
import com.example.go4lunch.models.Worker;
import com.google.gson.Gson;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.WorkerParameters;

import static com.example.go4lunch.viewmodel.RestaurantDataRepository.restaurantsCollectionReference;
import static com.example.go4lunch.viewmodel.WorkerDataRepository.currentUserId;
import static com.example.go4lunch.viewmodel.WorkerDataRepository.workersCollectionReference;

public class Notifications extends androidx.work.Worker {

    public static String channelId = "channel id";
    Worker currentUser;
    Restaurant choice;
    List<String> participants;

    public Notifications(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        getCurrentUser(this.getApplicationContext());
        return Result.success();
    }

    public void sendVisualNotification(Context context) {
        //for (Worker worker : choice.getWorkerList()) {
        //    //if (!worker.equals(currentUser)) {
        //        participants.add(worker.getName());
        //    //}
        //}

        String name = choice.getName();
        String address = choice.getAddress();
        String coworkers;
        if (participants != null && !participants.isEmpty()) {
            coworkers = "coworkers :" + participants;
        } else
            coworkers = "";


        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(name);
        inboxStyle.addLine(address);
        if (!coworkers.isEmpty()) {
            inboxStyle.addLine(coworkers);
        }

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.ic_baseline_dinner_dining_24)
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentText(address)
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        //.setContentIntent(pendingIntent)
                        .setStyle(inboxStyle);

        int NOTIFICATION_ID = 007;
        String NOTIFICATION_TAG = "GO4LUNCH";

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId,
                    "notification",
                    NotificationManager.IMPORTANCE_HIGH);


            notificationManager.createNotificationChannel(mChannel);

        }
        notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notificationBuilder.build());
    }

    public void setNotificationChannel(){
    }

    public void getCurrentUser(Context context) {
        workersCollectionReference
                .document(currentUserId).get()
                .addOnCompleteListener(task -> {
                    currentUser = task.getResult().toObject(Worker.class);
                    getRestaurant(context);
                });
    }


    public void getRestaurant(Context context) {
        restaurantsCollectionReference
                .document(currentUser.getRestaurantId()).get()
                .addOnCompleteListener(task -> {
                    choice = task.getResult().toObject(Restaurant.class);
                    sendVisualNotification(context);
                });
    }


}
