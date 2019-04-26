package com.example.klaud.tvandmoviedetective.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.klaud.tvandmoviedetective.Adapters.FriendsAdapter;
import com.example.klaud.tvandmoviedetective.Adapters.MyFriendAdapter;
import com.example.klaud.tvandmoviedetective.Items.FriendsItem;
import com.example.klaud.tvandmoviedetective.MainActivity;
import com.example.klaud.tvandmoviedetective.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Friends extends Fragment {
    public static Context ctx;
    public static RecyclerView recycler, myFriendsRec;
    public static FriendsAdapter adapter;
    public static MyFriendAdapter adapter2;
    public static ArrayList<FriendsItem> items = new ArrayList<>();
    public static ArrayList<FriendsItem> myFriendsItems = new ArrayList<>();
    public static String maiil;
    static DataSnapshot data = null;
    static Boolean isVisibleFragment = false;
    ArrayList<Map<String, String>> friendsActivity = new ArrayList<>();
    ListView recentFriendsActivityLv;
    ArrayList<String> friends = new ArrayList<>();
    ArrayList<String> friendsEmails = new ArrayList<>();
    SimpleAdapter simpleAdapter;

    public static void searchResult(String query) {
        items.clear();
        if (data != null) {
            for (DataSnapshot ds : data.getChildren()) {
                if (ds.hasChild("settings/private") && ds.child("settings/private").getValue().toString().equals("false")
                        && ds.hasChild("settings/nickname")
                        && ds.child("settings/nickname").getValue().toString().toLowerCase().contains(query.toLowerCase())
                        && !ds.getKey().toLowerCase().equals(maiil.toLowerCase())) {

                    items.add(new FriendsItem(ds.getKey(), ds.child("settings/nickname").getValue().toString()));
                } else if (ds.hasChild("settings/private") && ds.child("settings/private").getValue().toString().equals("false")
                        && ds.hasChild("settings/nickname") && ds.child("settings/nickname").getValue().toString().toLowerCase().equals("")
                        && ds.getKey().split("@")[0].toLowerCase().contains(query.toLowerCase())
                        && !ds.getKey().toLowerCase().equals(maiil.toLowerCase())) {

                    items.add(new FriendsItem(ds.getKey(), ds.getKey().split("@")[0]));
                }

            }
            adapter.notifyDataSetChanged();
            recycler.invalidate();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity.editor.putString("prev class", MainActivity.prefs.getString("class", ""));
        MainActivity.editor.putString("class", "Friends");
        MainActivity.editor.apply();
        return inflater.inflate(R.layout.friends_layout, container, false);
    }

    @Override
    public void onPause() {
        super.onPause();
        MainActivity.editor.putString("search", "");
        items.clear();
        MainActivity.editor.apply();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Friends");
        ctx = getContext();
        isVisibleFragment = true;
        maiil = MainActivity.prefs.getString("login", "").replace(".", "_");

        recentFriendsActivityLv = view.findViewById(R.id.recent_activity_lv);
        String[] from = {"text", "date"};
        int[] to = {android.R.id.text1, android.R.id.text2};

        simpleAdapter = new SimpleAdapter(ctx, friendsActivity, android.R.layout.simple_list_item_2, from, to);
        recentFriendsActivityLv.setAdapter(simpleAdapter);

        recycler = (RecyclerView) getView().findViewById(R.id.friends_recycler);
        adapter = new FriendsAdapter(getContext(), items, getFragmentManager(), getActivity());

        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this.getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        myFriendsRec = getView().findViewById(R.id.my_friedns_recycler);
        adapter2 = new MyFriendAdapter(ctx, myFriendsItems, getFragmentManager(), getActivity());
        myFriendsRec.setAdapter(adapter2);
        myFriendsRec.setLayoutManager(new LinearLayoutManager(this.getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference("users/");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myFriendsItems.clear();
                data = dataSnapshot;
                myFriendsRec.setVisibility(View.VISIBLE);
                friends.clear();
                friendsEmails.clear();
                for (DataSnapshot ds : data.child(maiil + "/settings/friends").getChildren()) {
                    Log.d("Friends", ds.getKey() + " " + ds.getValue().toString());
                    myFriendsItems.add(new FriendsItem(ds.getKey(), ds.getValue().toString()));
                    friendsEmails.add(ds.getKey());
                    friends.add(ds.getValue().toString());

                }
                ArrayList<String> activity = new ArrayList<>();

                for (DataSnapshot friend : dataSnapshot.getChildren()) { // all users
                    if (friend.hasChild("recent")) {
                        if (friendsEmails.contains(friend.getKey())) {
                            int index = friendsEmails.indexOf(friend.getKey());
                            for (DataSnapshot recent : friend.child("recent").getChildren()) { // all recent activities

                                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                                Long dateInMilisecs = Long.decode(recent.getKey());
                                Date currDate = new Date(dateInMilisecs);

                                activity.add(sdf.format(currDate) + ">" + friends.get(index) + recent.getValue().toString());

                            }
                        }
                    }
                }
                Collections.sort(activity);
                friendsActivity.clear();

                for (String act : activity){

                    HashMap<String, String> pair = new HashMap<>();
                    String[] separateAct = act.split(">");
                    pair.put("text", separateAct[1]);
                    pair.put("date", separateAct[0]);

                    friendsActivity.add(pair);
                }


                simpleAdapter.notifyDataSetChanged();
                recentFriendsActivityLv.invalidate();

                adapter2.notifyDataSetChanged();
                myFriendsRec.invalidate();
                if (myFriendsItems.size() == 0){
                    myFriendsRec.setVisibility(View.GONE);
                    Toast.makeText(ctx, "You have no friends. :'( \nClick to magnifying glass to find one.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
