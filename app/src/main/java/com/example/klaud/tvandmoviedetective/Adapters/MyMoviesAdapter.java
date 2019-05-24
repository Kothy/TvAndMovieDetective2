package com.example.klaud.tvandmoviedetective.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.klaud.tvandmoviedetective.MainActivity;
import com.example.klaud.tvandmoviedetective.Fragments.MovieDetail;
import com.example.klaud.tvandmoviedetective.Items.MovieItem;
import com.example.klaud.tvandmoviedetective.Fragments.MyMovies;
import com.example.klaud.tvandmoviedetective.Fragments.MyMoviesWatched;
import com.example.klaud.tvandmoviedetective.R;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyMoviesAdapter extends RecyclerView.Adapter<MyMoviesAdapter.ViewHolder> {

    boolean resize = false;
    private LayoutInflater inflater;
    private ArrayList<MovieItem> items;
    private Context contex;
    private FragmentManager fm;
    private Activity activity;
    private int position;

    public MyMoviesAdapter(Context ctx, ArrayList<MovieItem> imageModelArrayList, FragmentManager fm, Activity activity) {
        this.contex = ctx;
        this.inflater = LayoutInflater.from(ctx);
        this.items = imageModelArrayList;
        this.fm = fm;
        this.activity = activity;
    }

    public MyMoviesAdapter(Context ctx, ArrayList<MovieItem> imageModelArrayList, FragmentManager fm, Activity activity, boolean boo) {
        this.contex = ctx;
        this.inflater = LayoutInflater.from(ctx);
        this.items = imageModelArrayList;
        this.fm = fm;
        this.activity = activity;
        resize = boo;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_item_layout, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (items.get(position).getPoster_path() != null && items.get(position).getPoster_path().equals("null")) {
            holder.iv.setImageResource(items.get(position).getImage_drawable());
        } else {
            String url = String.format("https://image.tmdb.org/t/p/w300%s", items.get(position).getPoster_path());
            Picasso.get().load(url).into(holder.iv);
        }

        holder.time.setText(items.get(position).getName());
        holder.parentLayout.setOnClickListener(click -> {
            Fragment fragment = null;
            fragment = new MovieDetail();
            Bundle bundle = new Bundle();
            bundle.putString("id", items.get(position).getId().toString());
            bundle.putString("title", items.get(position).getName());

            fragment.setArguments(bundle);
            if (fragment != null) {
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.commit();
            }
            DrawerLayout drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        });

        holder.parentLayout.setOnLongClickListener((click) -> {
            String mail = MainActivity.mail.replace(".", "_");

            AlertDialog.Builder builder = new AlertDialog.Builder(contex);
            builder.setMessage("Are you sure you want to remove this item ?")
                    .setCancelable(false)

                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            FirebaseDatabase.getInstance().getReference("/users/" + mail + "/movies/" + items.get(position).getId()).removeValue();
                            items.remove(position);
                            notifyDataSetChanged();
                            if (MyMovies.recycler != null) MyMovies.recycler.invalidate();
                            if (MyMoviesWatched.recycler != null)
                                MyMoviesWatched.recycler.invalidate();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();

            return true;
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView time;
        ImageView iv;
        ConstraintLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            time = (TextView) itemView.findViewById(R.id.tvTitle);
            iv = (ImageView) itemView.findViewById(R.id.itemImage);
            parentLayout = (ConstraintLayout) itemView.findViewById(R.id.const_parent_lay55);
            if (resize) {
                iv.getLayoutParams().height = 180;
                iv.getLayoutParams().width = 160;
            }

        }
    }
}
