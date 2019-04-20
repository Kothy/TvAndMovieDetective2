package com.example.klaud.tvandmoviedetective.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Switch;

import com.example.klaud.tvandmoviedetective.MainActivity;
import com.example.klaud.tvandmoviedetective.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Settings extends Fragment {
    public static Context ctx;
    public EditText et;
    Button sendButton, clearRecent;
    Switch privateAccSwit, receiveNotifSwitch, sendNotifSwitch;
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
        privateAccSwit = view.findViewById(R.id.private_acc_switch);
        clearRecent = view.findViewById(R.id.clear_recent_button);

        numberPicker = view.findViewById(R.id.numberPicker);
        sendNotifSwitch = view.findViewById(R.id.send_notif_switch);
        receiveNotifSwitch = view.findViewById(R.id.receive_not_switch);
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
        //Toast.makeText(ctx, "prev class: "+ MainActivity.prefs.getString("prev class",""), Toast.LENGTH_SHORT).show();

        sendButton.setOnClickListener(click -> {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            String mail = MainActivity.mail.replace(".", "_");
            DatabaseReference dbRef = database.getReference("users/" + mail + "/settings");
            //Toast.makeText(ctx, et.getText()+" "+privateAccSwit.isChecked(), Toast.LENGTH_SHORT).show();
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("nickname", et.getText().toString());
            childUpdates.put("private", privateAccSwit.isChecked() + "");
            childUpdates.put("days_before", numberPicker.getValue() + "");
            childUpdates.put("send_notif", sendNotifSwitch.isChecked() + "");
            childUpdates.put("receive_notif", receiveNotifSwitch.isChecked() + "");

            MainActivity.editor.putString("receive_notif", receiveNotifSwitch.isChecked() + "").apply();
            MainActivity.editor.putString("send_notif", sendNotifSwitch.isChecked() + "").apply();
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
                        privateAccSwit.setChecked(false);
                    } else {
                        privateAccSwit.setChecked(true);
                    }
                }
                if (dataSnapshot.hasChild("days_before")) {
                    int days = Integer.valueOf(dataSnapshot.child("days_before").getValue().toString());
                    numberPicker.setValue(days);
                } else numberPicker.setValue(2);

                if (dataSnapshot.hasChild("send_notif")) {
                    if (dataSnapshot.child("send_notif").getValue().equals("false")) {
                        sendNotifSwitch.setChecked(false);
                    } else {
                        sendNotifSwitch.setChecked(true);
                    }
                }

                if (dataSnapshot.hasChild("receive_notif")) {
                    if (dataSnapshot.child("receive_notif").getValue().equals("false")) {
                        receiveNotifSwitch.setChecked(false);
                    } else {
                        receiveNotifSwitch.setChecked(true);
                    }
                }
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
