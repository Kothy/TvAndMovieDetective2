package com.example.klaud.tvandmoviedetective;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class SeriesReceiver  extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        MainActivity.editor.putString("openFrag", "series");
        MainActivity.editor.putString("openFragTitle",intent.getStringExtra("title"));
        MainActivity.editor.putString("openFragId", intent.getStringExtra("id"));
        MainActivity.editor.apply();

        Intent newInt = new Intent(MainActivity.ctx, MainActivity.class);
        context.startActivity(newInt);

        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(it);
    }
}
