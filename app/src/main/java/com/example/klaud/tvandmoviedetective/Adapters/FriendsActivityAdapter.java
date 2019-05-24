package com.example.klaud.tvandmoviedetective.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.klaud.tvandmoviedetective.Items.CastItem;
import com.example.klaud.tvandmoviedetective.R;

import java.util.ArrayList;

public class FriendsActivityAdapter extends RecyclerView.Adapter<FriendsActivityAdapter.ViewHolder>{
    private LayoutInflater inflater;
    private ArrayList<CastItem> items;

    public FriendsActivityAdapter(Context ctx, ArrayList<CastItem> imageModelArrayList) {
        this.inflater = LayoutInflater.from(ctx);
        this.items = imageModelArrayList;
    }

    @Override
    public FriendsActivityAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.friends_activity_item, parent, false);

        FriendsActivityAdapter.ViewHolder holder = new FriendsActivityAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(FriendsActivityAdapter.ViewHolder holder, int position) {

        holder.name.setText(items.get(position).name);
        holder.date.setText(items.get(position).role);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView date;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.activityName);
            date = itemView.findViewById(R.id.activityDate);
        }
    }
}
