package com.example.klaud.tvandmoviedetective.Service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.example.klaud.tvandmoviedetective.MainActivity;
import com.example.klaud.tvandmoviedetective.MovieReceiver;
import com.example.klaud.tvandmoviedetective.R;
import com.example.klaud.tvandmoviedetective.SeriesReceiver;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {



    public void setIntent(String text, String notTitle, String id, String title, String rate, String isMovie, String friend) {

        if (isMovie.equals("true")){
            Intent resultIntent = new Intent(this, MovieReceiver.class);
            resultIntent.setData(Uri.parse(resultIntent.toUri(Intent.URI_INTENT_SCHEME)));
            resultIntent.putExtra("id", id);
            resultIntent.putExtra("title", title);
            resultIntent.putExtra("rate", rate);
            resultIntent.putExtra("is_movie", isMovie);
            resultIntent.putExtra("friend_name", friend);

            int uniqueInt = (int) (System.currentTimeMillis() & 0xfffffff);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.ctx, uniqueInt, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            sendNotif(text, notTitle, pendingIntent);

        } else {
            Intent resultIntent = new Intent(this, SeriesReceiver.class);

            resultIntent.putExtra("id", id);
            resultIntent.putExtra("title", title);
            resultIntent.putExtra("rate", rate);
            resultIntent.putExtra("is_movie", isMovie);
            resultIntent.putExtra("friend_name", friend);


            int uniqueInt = (int) (System.currentTimeMillis() & 0xfffffff);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.ctx, uniqueInt, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            sendNotif(text, notTitle, pendingIntent);
        }

    }

    public void sendNotif(String notTitle, String text, PendingIntent pendingIntent){
        createNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.ctx, "9999")
                .setSmallIcon(R.drawable.ic_search_black_24dp)
                .setContentTitle(notTitle)
                .setContentText(text)
                //.addAction(R.drawable.ic_movie_black_24dp, "Details", pendingIntent)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        Random rnd = new Random();
        Integer randomId = rnd.nextInt((10000000 - 1) + 1) + 1;

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.ctx);
        notificationManager.notify(randomId, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("9999", "NotifChannel", importance);
            channel.setDescription("Blah Blah Notification");
            NotificationManager notificationManager = MainActivity.notificationManager;
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        final String TAG = "TestMess";

        if (remoteMessage.getData().size() > 0) {
            //Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            String id = remoteMessage.getData().get("id");
            String rate = remoteMessage.getData().get("rate");
            String title = remoteMessage.getData().get("title");
            String friend = remoteMessage.getData().get("friends_name");
            String isMovie = remoteMessage.getData().get("is_movie");
            setIntent(friend +" rated "+ title + " " + rate + " points " ,title, id, title, rate, isMovie, friend);
        }
    }

    @Override
    public void onNewToken (String token){
        MainActivity.editor.putString("FToken", token).apply();
    }
}
