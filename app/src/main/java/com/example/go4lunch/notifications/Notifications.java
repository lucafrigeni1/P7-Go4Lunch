package com.example.go4lunch.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;

import com.example.go4lunch.R;
import com.example.go4lunch.models.Restaurant;
import com.example.go4lunch.models.Worker;
import com.example.go4lunch.ui.activity.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import androidx.core.app.NotificationCompat;

import static com.example.go4lunch.viewmodel.RestaurantDataRepository.restaurantsCollectionReference;
import static com.example.go4lunch.viewmodel.WorkerDataRepository.currentUserId;
import static com.example.go4lunch.viewmodel.WorkerDataRepository.workersCollectionReference;

public class Notifications extends FirebaseMessagingService {

    private final int NOTIFICATION_ID = 007;
    private final String NOTIFICATION_TAG = "GO4LUNCH";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            this.getCurrentUser();
        }
    }

    private void sendVisualNotification(Restaurant restaurant) {

        // 1 - Create an Intent that will be shown when user will click on the Notification
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        getCurrentUser();
        String name = restaurant.getName();
        String address = restaurant.getAddress();
        //String participants = restaurant.getWorkerList() + "is joining";

        // 2 - Create a Style for the Notification
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(name);
        inboxStyle.addLine(address);
        //inboxStyle.addLine(participants);

        // 3 - Create a Channel (Android 8)
        String channelId = "channel id";

        // 4 - Build a Notification object
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_baseline_dinner_dining_24)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(address)
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentIntent(pendingIntent)
                        .setStyle(inboxStyle);

        // 5 - Add the Notification to the Notification Manager and show it.
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // 6 - Support Version >= Android 8
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Message provenant de Firebase";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        // 7 - Show notification
        notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notificationBuilder.build());
    }

    private void getCurrentUser() {
        workersCollectionReference
                .document(currentUserId).get()
                .addOnCompleteListener(task -> {
                    Worker worker = task.getResult().toObject(Worker.class);
                    getRestaurant(worker);
                });
    }

    private void getRestaurant(Worker worker) {
        restaurantsCollectionReference
                .document(worker.getRestaurantId()).get()
                .addOnCompleteListener(task -> {
                   Restaurant restaurant = task.getResult().toObject(Restaurant.class);
                   sendVisualNotification(restaurant);
                });
    }
}
