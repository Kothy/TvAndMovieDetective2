package com.example.klaud.tvandmoviedetective.Adapters;

import android.app.Activity;
import android.content.Context;
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
import android.widget.TextView;

import com.example.klaud.tvandmoviedetective.R;
import com.example.klaud.tvandmoviedetective.Fragments.SeriesDetails;
import com.example.klaud.tvandmoviedetective.Items.SeriesItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SeriesAdapter extends RecyclerView.Adapter<SeriesAdapter.ViewHolder> {
    public Boolean resize = false;
    private LayoutInflater inflater;
    private ArrayList<SeriesItem> items;
    private Context contex;
    private FragmentManager fm;
    private Activity activity;

    public SeriesAdapter(Context ctx, ArrayList<SeriesItem> imageModelArrayList, FragmentManager fm, Activity activity) {
        this.contex = ctx;
        this.inflater = LayoutInflater.from(ctx);
        this.items = imageModelArrayList;
        this.fm = fm;
        this.activity = activity;
    }

    public SeriesAdapter(Context ctx, ArrayList<SeriesItem> imageModelArrayList, FragmentManager fm, Activity activity, boolean boo) {
        this.contex = ctx;
        this.inflater = LayoutInflater.from(ctx);
        this.items = imageModelArrayList;
        this.fm = fm;
        this.activity = activity;
        this.resize = boo;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.recycler_item_layout, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (items.get(position).getPoster_path().equals("null")) {
            //Toast.makeText(contex, "poster path je null", Toast.LENGTH_SHORT).show();
            holder.iv.setImageResource(items.get(position).getImage_drawable());
        } else {
            String url = String.format("https://image.tmdb.org/t/p/w300%s", items.get(position).getPoster_path());
            Picasso.get().load(url).into(holder.iv);
        }

        holder.time.setText(items.get(position).getName());
        holder.parentLayout.setOnClickListener(click -> {

            Fragment fragment = null;
            fragment = new SeriesDetails();
            Bundle bundle = new Bundle();
            bundle.putString("id", items.get(position).getId().toString());
            fragment.setArguments(bundle);
            if (fragment != null) {
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.commit();
            }
            DrawerLayout drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
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
