package com.example.klaud.tvandmoviedetective.Service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.klaud.tvandmoviedetective.MainActivity;
import com.example.klaud.tvandmoviedetective.R;
import com.google.firebase.database.DataSnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class BackService extends Worker {

    public BackService(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    public void sendNotification(String text, String title) {
        createNotificationChannel();
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(MainActivity.ctx, "99999")
                .setSmallIcon(R.drawable.ic_search_black_24dp)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        Random rnd = new Random();
        Integer randomId = rnd.nextInt((10000000 - 1) + 1) + 1;

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(MainActivity.ctx);
        notificationManager.notify(randomId, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("99999",
                    "NotifChannel2", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Blah Blah Notification");
            NotificationManager notificationManager = MainActivity.notificationManager;
            notificationManager.createNotificationChannel(channel);
        }
    }

    @NonNull
    @Override
    public Result doWork() {
        if (MainActivity.dataSnap != null) {
            if (MainActivity.dataSnap.hasChild("movies")) {
                DataSnapshot movies = MainActivity.dataSnap.child("movies");
                for (DataSnapshot movie : movies.getChildren()) {
                    if (movie.hasChild("status") && movie.child("status").getValue().toString().equals("want")) {
                        if (movie.hasChild("release_date")) {
                            Long movieMillisec = Long.decode(movie.child("release_date").getValue().toString());

                            //setIntent("SENT FROM BACKSERVICE", movie.child("title").getValue().toString());

                            if (movieMillisec > System.currentTimeMillis()) {
                                int days_before = MainActivity.getDaysBefore();
                                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

                                Date currDate = new Date(System.currentTimeMillis());
                                Date movieDate = new Date(movieMillisec);

                                Calendar c2 = Calendar.getInstance();
                                c2.setTime(currDate);
                                c2.add(Calendar.DATE, + days_before);
                                currDate = c2.getTime();

                                Log.d("POKUS", sdf.format(movieDate) + " = " + sdf.format(currDate));

                                if (sdf.format(movieDate).equals(sdf.format(currDate))) {

                                    sendNotification("In theatres in " + days_before
                                                    + " days. SENT FROM BACKSERVICE",
                                            movie.child("title").getValue().toString());
                                }
                            }
                        }
                    }
                }
            }
            Log.d("POKUS", "Worker's job is done.");
            return Result.SUCCESS;
        } else {
            Log.d("POKUS", "Worker's job is done.");
            return Result.RETRY;
        }
    }
}

