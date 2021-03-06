package com.example.klaud.tvandmoviedetective.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.klaud.tvandmoviedetective.Adapters.CastAdapter;
import com.example.klaud.tvandmoviedetective.Items.CastItem;
import com.example.klaud.tvandmoviedetective.Others.ArrayCast;
import com.example.klaud.tvandmoviedetective.Others.Cast;
import com.example.klaud.tvandmoviedetective.Others.Crew;
import com.example.klaud.tvandmoviedetective.MainActivity;
import com.example.klaud.tvandmoviedetective.Pic;
import com.example.klaud.tvandmoviedetective.R;
import com.example.klaud.tvandmoviedetective.SendNotification;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class SeriesDetails extends Fragment {
    public static ImageView seriesPoster;
    public static Context ctx;
    TextView tv, genresTv, lastAirTv, ratingTv, seasonsTv, episodesCountTv,
            networkTv, genresTitleTv, inProductionTv, myRatingTitleTv, lastAirDateTitleTv, networkTitleTv,
            ratingTitleTv;
    ScrollView sv;
    Integer Id = 0;
    //ListView castLv;
    Integer numOfSeasons = 0;
    JSONObject jsonSeries = null;
    ArrayCast castAndCrew = null;
    //SimpleAdapter adapter;
    //ArrayList<Map<String, String>> pairs = new ArrayList<Map<String, String>>();
    Button episodesButt, addToFavouriteButton, removeFromFavourite;
    String title, poster_path, televisons;
    TreeMap<Integer, Integer> seasonsAndEpisodes = new TreeMap<>();
    RatingBar ratingBar;
    DatabaseReference dbRef;
    DataSnapshot data;
    Boolean run = true;
    ProgressDialog progressDialog;
    static Boolean first;
    public ArrayList<CastItem> castItem = new ArrayList<>();
    public CastAdapter castAdapter;
    public RecyclerView castRecycler;

    AsyncTask<String, Integer, String> getJsonString = new AsyncTask<String, Integer, String>() {
        @Override
        protected void onPreExecute() {
            progressDialog.show();
            if (Looper.myLooper() == null) Looper.prepare();
        }

        @Override
        protected String doInBackground(String... params) {
            String result;
            String inputLine;
            try {
                URL myUrl = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                while ((inputLine = reader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }
                reader.close();
                streamReader.close();
                result = stringBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
                result = null;
            }
            //while (run==true){}
            return result;
        }

        protected void onPostExecute(String result) {
            try {
                jsonSeries = new JSONObject(result);
                String patt = "https://image.tmdb.org/t/p/original%s";

                JSONArray telev = jsonSeries.getJSONArray("networks");

                Boolean in_production = jsonSeries.getBoolean("in_production");
                if (in_production) inProductionTv.setText("Yes");
                else inProductionTv.setText("No");

                for (int i = 0; i < telev.length(); i++) {
                    televisons += telev.getJSONObject(i).getString("name") + " • ";
                }

                JSONArray seasons = jsonSeries.getJSONArray("seasons");
                for (int j = 0; j < seasons.length(); j++) {
                    JSONObject season = seasons.getJSONObject(j);
                    seasonsAndEpisodes.put(season.getInt("season_number"), season.getInt("episode_count"));
                }
                televisons = televisons.substring(0, televisons.length() - 3);
                televisons = televisons.replace("null", "");

                JSONArray genres = jsonSeries.getJSONArray("genres");
                String stringGenres = "";
                for (int i = 0; i < genres.length(); i++) {
                    stringGenres += genres.getJSONObject(i).getString("name") + " | ";
                }
                genresTv.setText(stringGenres);

                final float scale = getContext().getResources().getDisplayMetrics().density;
                int numLines = genresTv.getLineCount();
                int pixels;
                if (numLines > 1) {
                    pixels = (int) ((genresTv.getLineCount() * 24) * scale + 0.5f);
                    genresTv.getLayoutParams().height = pixels;
                    genresTitleTv.getLayoutParams().height = pixels;
                }

                networkTv.setText(televisons);
                int voteAng = (((int) jsonSeries.getDouble("vote_average")) * 10);
                if (voteAng > 0) {
                    //Toast.makeText(ctx, "rating: "+voteAng, Toast.LENGTH_SHORT).show();
                    ratingTv.setText(voteAng + "%");
                } else {
                    ratingTitleTv.setVisibility(View.GONE);
                    ratingTv.setVisibility(View.GONE);
                }

                numLines = networkTv.getLineCount();
                if (numLines > 1) {
                    pixels = (int) ((networkTv.getLineCount() * 24) * scale + 0.5f);
                    networkTv.getLayoutParams().height = pixels;
                    networkTitleTv.getLayoutParams().height = pixels;
                }

                String date = jsonSeries.getString("last_air_date");
                String[] parsedDate = date.split("-");

                if (!date.equals("null"))
                    lastAirTv.setText(parsedDate[2] + "." + parsedDate[1] + "." + parsedDate[0]);
                else {
                    lastAirTv.setVisibility(View.GONE);
                    lastAirDateTitleTv.setVisibility(View.GONE);
                }

                episodesCountTv.setText(jsonSeries.getString("number_of_episodes"));

                if (jsonSeries.getString("overview").equals(""))
                    tv.setText("Overview is not available.");
                else tv.setText(jsonSeries.getString("overview"));
                title = jsonSeries.getString("name");
                poster_path = jsonSeries.getString("poster_path");
                getActivity().setTitle(title);
                Pic image = new Pic();
                numOfSeasons = jsonSeries.getJSONArray("seasons").length();
                seasonsTv.setText(numOfSeasons + "");

                if (jsonSeries.getString("backdrop_path").equals("null")) {
                    seriesPoster.setBackgroundResource(R.drawable.no_backdrop);
                } else
                    image.execute(String.format(patt, jsonSeries.getString("backdrop_path")), "series");


                progressDialog.dismiss();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    AsyncTask<String, Integer, String> getJsonCast = new AsyncTask<String, Integer, String>() {
        @Override
        protected void onPreExecute() {
            if (Looper.myLooper() == null) Looper.prepare();
        }

        @Override
        protected String doInBackground(String... params) {
            String result;
            String inputLine;
            try {
                URL myUrl = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(15000);
                connection.connect();
                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                while ((inputLine = reader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }
                reader.close();
                streamReader.close();
                result = stringBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
                result = null;
            }
            return result;
        }

        protected void onPostExecute(String result) {
            //pairs.clear();
            castItem.clear();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            castAndCrew = gson.fromJson(result, ArrayCast.class);
            for (Cast c : castAndCrew.cast) {
                if (!c.character.contains("uncredited") && !c.character.contains("On-Set")) {
                    //HashMap<String, String> item = new HashMap<String, String>();
                    //item.put("actor", c.name);
                    //item.put("role", c.character);
                    castItem.add(new CastItem(c.name, c.character));
                    //pairs.add(item);
                }
            }
            for (Crew c : castAndCrew.crew) {
                //HashMap<String, String> item = new HashMap<String, String>();
                //item.put("actor", c.name);
                //item.put("role", c.job);
                //pairs.add(item);
                castItem.add(new CastItem(c.name, c.job));
            }
            //adapter.notifyDataSetChanged();
            castAdapter.notifyDataSetChanged();
            castRecycler.invalidate();
            //castLv.invalidate();
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity.editor.putString("prev class", MainActivity.prefs.getString("class", ""));
        MainActivity.editor.putString("class", "SeriesDetail");
        MainActivity.editor.apply();
        return inflater.inflate(R.layout.series_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //getActivity().setTitle("Series Details");
        ctx = getContext();
        tv = view.findViewById(R.id.textView40);

        //Toast.makeText(ctx, "prev class: "+ MainActivity.prefs.getString("prev class",""), Toast.LENGTH_SHORT).show();

        String maiil = MainActivity.prefs.getString("login", "").replace(".", "_");

        progressDialog = new ProgressDialog(ctx);
        progressDialog.setTitle("Please wait");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setMax(100);

        genresTv = view.findViewById(R.id.series_genres_tv);
        lastAirTv = view.findViewById(R.id.series_last_air_tv);
        ratingTv = view.findViewById(R.id.series_rating_tv);
        seasonsTv = view.findViewById(R.id.series_season_count_tv);
        episodesCountTv = view.findViewById(R.id.series_episodes_count_tv);
        networkTv = view.findViewById(R.id.series_network_tv);
        genresTitleTv = view.findViewById(R.id.textView12);
        inProductionTv = view.findViewById(R.id.in_production_tv);
        myRatingTitleTv = view.findViewById(R.id.textView24);
        lastAirDateTitleTv = view.findViewById(R.id.textView14);
        ratingTitleTv = view.findViewById(R.id.textView16);
        networkTitleTv = view.findViewById(R.id.network_title_tv);
        first = true;

        ratingBar = view.findViewById(R.id.series_rating_bar);
        ratingBar.setVisibility(View.GONE);
        myRatingTitleTv.setVisibility(View.GONE);

        //castLv = view.findViewById(R.id.listVCast5);
        //castLv.setFocusable(false);
        sv = view.findViewById(R.id.scrollView20);
        addToFavouriteButton = view.findViewById(R.id.button7);
        removeFromFavourite = view.findViewById(R.id.remove_from_favourite);
        removeFromFavourite.setVisibility(View.INVISIBLE);

        castRecycler = (RecyclerView) getView().findViewById(R.id.cast_series_recycler);
        castAdapter = new CastAdapter(getContext(), castItem);
        castRecycler.setAdapter(castAdapter);
        castRecycler.setLayoutManager(new LinearLayoutManager(this.getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));

        sv.setOnTouchListener((vie, event) -> {
            tv.getParent().requestDisallowInterceptTouchEvent(false);
            return false;
        });
        castRecycler.setOnTouchListener((vie, event) -> {
            castRecycler.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });

        removeFromFavourite.setOnClickListener(click -> {
            FirebaseDatabase.getInstance().getReference("/users/" + maiil + "/series/" + Id).removeValue();
        });

        FirebaseDatabase dab = FirebaseDatabase.getInstance();
        dbRef = dab.getReference("users/" + maiil + "/series");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //ratingBar.setRating(0);
                data = dataSnapshot;
                run = false;
                if (data.hasChild(Id + "/rating")) {

                    String rating = data.child(Id + "/rating").getValue().toString();
                    if (!rating.equals("")) {
                        ratingBar.setRating(Float.valueOf(rating));
                    }
                }
                first = false;
                if (data.hasChild(Id + "")) {
                    addToFavouriteButton.setVisibility(View.INVISIBLE);
                    ratingBar.setVisibility(View.VISIBLE);
                    myRatingTitleTv.setVisibility(View.VISIBLE);
                    removeFromFavourite.setVisibility(View.VISIBLE);
                } else {
                    removeFromFavourite.setVisibility(View.INVISIBLE);
                    addToFavouriteButton.setVisibility(View.VISIBLE);
                    ratingBar.setVisibility(View.GONE);
                    myRatingTitleTv.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        ratingBar.setOnRatingBarChangeListener((rat, num, user) -> {
            if (first == true) {
                return;
            }

            FirebaseDatabase db = FirebaseDatabase.getInstance();
            Toast.makeText(ctx, "You rated " + title + " " + num, Toast.LENGTH_SHORT).show();
            DatabaseReference dbRef = db.getReference("/users/" + maiil + "/series/" + Id);
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("name", title);
            childUpdates.put("poster_path", poster_path);
            childUpdates.put("rating", "" + num);
            dbRef.updateChildren(childUpdates);

            dbRef = db.getReference("/users/" + maiil + "/recent/");
            childUpdates = new HashMap<>();
            childUpdates.put(System.currentTimeMillis() + "", " rated " + title + " " + num);
            dbRef.updateChildren(childUpdates);

            if (MainActivity.sendNotifToFriends == false){
                Toast.makeText(ctx, "Sending messages switched off", Toast.LENGTH_SHORT).show();
                return;
            }

            //MainActivity.subscribe("test");

            String id = String.valueOf(Id);
            String isMovie = "false";
            String frieds_name = MainActivity.nickname;
            String rate = String.valueOf(num);
            String regId = "/topics/" + MainActivity.editMail(MainActivity.mail);


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
                //Log.d("messageJSON", "something went wrong");
            }

        });

        addToFavouriteButton.setOnClickListener(click -> {

            Toast.makeText(ctx, title + " added to your favourite tv show.", Toast.LENGTH_SHORT).show();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference dbRef = database.getReference("users/" +
                    MainActivity.prefs.getString("login", "").replace(".", "_")
                    + "/series/" + Id);
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("name", title);
            childUpdates.put("poster_path", poster_path);
            childUpdates.put("networks", televisons);

            dbRef.updateChildren(childUpdates);

            dbRef = database.getReference("/users/" + maiil + "/recent/");
            childUpdates = new HashMap<>();
            childUpdates.put(System.currentTimeMillis() + "", " mark " + title + " as his/her favourite show");
            dbRef.updateChildren(childUpdates);
        });
        //pairs.clear();
        seriesPoster = view.findViewById(R.id.imageView20);
        //String[] from = {"actor", "role"};// symbolické mená riadkov
        //int[] to = {android.R.id.text1, android.R.id.text2};
        //adapter = new SimpleAdapter(getContext(), pairs, android.R.layout.simple_list_item_2, from, to);
        //castLv.setAdapter(adapter);

        episodesButt = view.findViewById(R.id.button6);
        episodesButt.setOnClickListener(click -> {
            Fragment fragment = null;
            fragment = new Episodes();
            Bundle bundle = new Bundle();
            bundle.putString("id", Id + "");
            bundle.putString("title", title);
            bundle.putString("poster_path", poster_path);
            bundle.putInt("seasons", numOfSeasons);
            bundle.putString("networks", televisons);

            fragment.setArguments(bundle);
            if (fragment != null) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.commit();
            }
            DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String movieID = bundle.getString("id", "");
            MainActivity.editor.putString("idSeriesBP", movieID);
            MainActivity.editor.apply();
            Id = Integer.valueOf(movieID);

            //MainActivity.editor.putInt("id",Id);
            String pattern = "https://api.themoviedb.org/3/tv/%d?api_key=1a9919c2a864cb40ce1e4c34f3b9e2c4&language=en-US&";
            String pattern2 = "https://api.themoviedb.org/3/tv/%d/credits?api_key=1a9919c2a864cb40ce1e4c34f3b9e2c4&language=en-US";
            getJsonString.execute(String.format(pattern, Id));
            getJsonCast.execute(String.format(pattern2, Id));
        }
    }
}
