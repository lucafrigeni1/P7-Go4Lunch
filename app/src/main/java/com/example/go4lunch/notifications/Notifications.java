package com.example.go4lunch.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import com.example.go4lunch.R;
import com.example.go4lunch.models.Restaurant;
import com.example.go4lunch.models.Worker;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.WorkerParameters;

import static com.example.go4lunch.viewmodel.RestaurantDataRepository.restaurantsCollectionReference;
import static com.example.go4lunch.viewmodel.WorkerDataRepository.currentUserId;
import static com.example.go4lunch.viewmodel.WorkerDataRepository.workersCollectionReference;

public class Notifications extends androidx.work.Worker {

    public static String channelId = "channel id";

    public Notifications(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    public static void launchWorker(Context context){
        Date date = Calendar.getInstance().getTime();
        DateFormat hourFormat = new SimpleDateFormat("HHmm", Locale.FRENCH);
        int currentTime = Integer.parseInt(hourFormat.format(date));

        WorkRequest workRequest;

        if (currentTime > 1200){
            workRequest = new OneTimeWorkRequest.Builder(Notifications.class)
                    .setInitialDelay(2160 - currentTime * 60 / 100, TimeUnit.MINUTES)
                    .build();
        } else {
            workRequest = new OneTimeWorkRequest.Builder(Notifications.class)
                    .setInitialDelay(720 - currentTime * 60 / 100, TimeUnit.MINUTES)
                    .build();
        }
        WorkManager.getInstance(context).enqueue(workRequest);
    }

    @NonNull
    @Override
    public Result doWork() {
        getCurrentUser(this.getApplicationContext());
        return Result.success();
    }

    public void getCurrentUser(Context context) {
        workersCollectionReference
                .document(currentUserId).get()
                .addOnCompleteListener(task -> {
                    Worker worker = task.getResult().toObject(Worker.class);
                    if (worker.getRestaurant() != null) {
                        getRestaurant(context, worker);
                    }
                });
    }

    public void getRestaurant(Context context, Worker worker) {
        restaurantsCollectionReference
                .document(worker.getRestaurant().getId()).get()
                .addOnCompleteListener(task -> {
                    Restaurant restaurant = task.getResult().toObject(Restaurant.class);
                    sendVisualNotification(context, restaurant, worker);
                });
    }

    public void sendVisualNotification(Context context, Restaurant restaurant, Worker currentUser) {
        List<String> participantsList = new ArrayList<>();
        String participants;

        Gson gson = new Gson() ;
        Log.e( "0", gson.toJson(currentUser));
        Log.e( "1", gson.toJson(restaurant));
        Log.e( "2", String.valueOf(restaurant.getWorkerList().size()));

        for (Worker worker : restaurant.getWorkerList()) {
            if (!worker.getId().equals(currentUser.getId())) {
                participantsList.add(worker.getName());
            }
        }

        Log.e( "3", String.valueOf(participantsList.size()));
        Log.e( "4", String.valueOf(restaurant.getWorkerList().size()));

        String SEPARATOR = ",";
        StringBuilder stringBuilder = new StringBuilder();

        for (String name : participantsList){
            stringBuilder.append(name);
            stringBuilder.append(SEPARATOR);
        }
        //stringBuilder.setLength(stringBuilder.length()-1);
        String names = stringBuilder.toString();

        if (names != null && !names.isEmpty()) {
            participants = R.string.coworkers + names;
        } else
            participants = String.valueOf(R.string.nobody);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(restaurant.getName());
        inboxStyle.addLine(restaurant.getAddress());
        inboxStyle.addLine(participants);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.ic_baseline_dinner_dining_24)
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentText(restaurant.getAddress())
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
}
