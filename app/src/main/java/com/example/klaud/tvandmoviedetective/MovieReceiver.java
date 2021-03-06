package com.example.klaud.tvandmoviedetective;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;

public class MovieReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        MainActivity.editor.putString("openFrag", "movie");
        MainActivity.editor.putString("openFragTitle",intent.getStringExtra("title"));
        MainActivity.editor.putString("openFragId", intent.getStringExtra("id"));
        MainActivity.editor.apply();

        Intent newInt = new Intent(MainActivity.ctx, MainActivity.class);
        context.startActivity(newInt);

        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(it);

    }
}
