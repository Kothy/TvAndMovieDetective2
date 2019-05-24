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

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private ArrayList<CastItem> items;

    public CastAdapter(Context ctx, ArrayList<CastItem> imageModelArrayList) {
        this.inflater = LayoutInflater.from(ctx);
        this.items = imageModelArrayList;
    }

    @Override
    public CastAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.cast_item, parent, false);

        CastAdapter.ViewHolder holder = new CastAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(CastAdapter.ViewHolder holder, int position) {

        holder.name.setText(items.get(position).name);
        holder.role.setText(items.get(position).role);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView role;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.actorName);
            role = itemView.findViewById(R.id.actorRole);
        }
    }
}
