package com.example.klaud.tvandmoviedetective;

import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SendNotification extends AsyncTask<String, String, String> {
    @Override
    protected String doInBackground(String... strings) {

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");

        RequestBody body = RequestBody.create(mediaType, strings[0]);
        Request request = new Request.Builder()
                .url("https://android.googleapis.com/gcm/send")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "key=AIzaSyA6lAr4jnly1EtHpNfB1i_8Z12EcUvAudg")
                .build();
        try {
            Response response = client.newCall(request).execute();
            Log.d("FNotif", response.body().string());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "done";
    }
}
