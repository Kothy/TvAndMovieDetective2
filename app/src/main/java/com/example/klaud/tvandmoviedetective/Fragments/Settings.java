package com.example.klaud.tvandmoviedetective.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.Toast;

import com.example.klaud.tvandmoviedetective.MainActivity;
import com.example.klaud.tvandmoviedetective.R;
import com.example.klaud.tvandmoviedetective.SendNotification;
import com.example.klaud.tvandmoviedetective.Service.MyFirebaseMessagingService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Settings extends Fragment {
    public static Context ctx;
    public EditText et;
    Button sendButton, clearRecent, sendNotif;
    Switch swit;
    DataSnapshot data;
    NumberPicker numberPicker;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity.editor.putString("prev class", MainActivity.prefs.getString("class", ""));
        MainActivity.editor.putString("class", "Settings");
        MainActivity.editor.apply();
        return inflater.inflate(R.layout.settings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Settings");
        ctx = getContext();
        et = view.findViewById(R.id.editText);
        sendButton = view.findViewById(R.id.button9);
        swit = view.findViewById(R.id.switch1);
        clearRecent = view.findViewById(R.id.clear_recent_button);
        sendNotif = view.findViewById(R.id.send_notif_button);

        numberPicker = view.findViewById(R.id.numberPicker);
        numberPicker.setMaxValue(30);
        numberPicker.setMinValue(1);

        numberPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            //numberOfDays.setText(newVal+"");
        });

        if (MainActivity.prefs.getString("login", "").equals("kada11@azet.sk")) {
            clearRecent.setVisibility(View.VISIBLE);
        } else clearRecent.setVisibility(View.INVISIBLE);

        clearRecent.setOnClickListener(click -> {
            if (data != null) {
                for (DataSnapshot ds : data.getChildren()) {
                    if (ds.hasChild("recent")) {

                        ArrayList<DataSnapshot> children = new ArrayList<>();
                        for (DataSnapshot child : ds.child("recent").getChildren()) {
                            children.add(child);
                        }

                        while (children.size() > 5) {
                            children.get(0).getRef().removeValue();
                            children.remove(0);
                        }
                    }
                }
            }

        });

        sendNotif.setOnClickListener(click -> {

            String title = "Avengers";
            String id = "9999";
            String isMovie = "true";
            String frieds_name = "Sonka";
            String rate = "5.0";
            String regId = MainActivity.prefs.getString("FToken", "");
            regId = "topics/test";
            try {
                JSONObject mainJson = new JSONObject();
                JSONObject data = new JSONObject();
                mainJson.put("data", data);
                mainJson.put("to", regId);

                data.put("id", id);
                data.put("title", title);
                data.put("friends_name", frieds_name);
                data.put("is_movie", isMovie);
                data.put("rate", rate);

                SendNotification sendN = new SendNotification();
                sendN.execute(mainJson.toString());

                Log.d("messageJSON", mainJson.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("messageJSON", "something went wrong");
            }
        });


        //Toast.makeText(ctx, "prev class: "+ MainActivity.prefs.getString("prev class",""), Toast.LENGTH_SHORT).show();

        sendButton.setOnClickListener(click -> {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            String mail = MainActivity.mail.replace(".", "_");
            DatabaseReference dbRef = database.getReference("users/" + mail + "/settings");
            //Toast.makeText(ctx, et.getText()+" "+swit.isChecked(), Toast.LENGTH_SHORT).show();
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("nickname", et.getText().toString());
            childUpdates.put("private", swit.isChecked() + "");
            childUpdates.put("days_before", numberPicker.getValue() + "");
            dbRef.updateChildren(childUpdates);
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String mail = MainActivity.mail.replace(".", "_");
        DatabaseReference dbRef = database.getReference("users/" + mail + "/settings");

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("nickname") && !dataSnapshot.child("nickname").getValue().toString().equals("")) {
                    et.setText(dataSnapshot.child("nickname").getValue().toString());
                }
                if (dataSnapshot.hasChild("private")) {
                    if (dataSnapshot.child("private").getValue().equals("false")) {
                        swit.setChecked(false);
                    } else {
                        swit.setChecked(true);
                    }
                }
                if (dataSnapshot.hasChild("days_before")) {
                    int days = Integer.valueOf(dataSnapshot.child("days_before").getValue().toString());
                    numberPicker.setValue(days);
                } else numberPicker.setValue(2);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        dbRef = database.getReference("users/");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                data = dataSnapshot;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
