package com.example.klaud.tvandmoviedetective.Holders;

import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.klaud.tvandmoviedetective.R;
import com.example.klaud.tvandmoviedetective.Others.Season;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

public class SeriesViewHolder extends GroupViewHolder {
    private TextView mTextView;
    private ImageView arrow;

    public SeriesViewHolder(View itemView) {
        super(itemView);

        mTextView = itemView.findViewById(R.id.textView);
        arrow = itemView.findViewById(R.id.arrow);
    }

    public void bind(Season season) {
        mTextView.setText(season.getTitle());
    }

    @Override
    public void expand() {
        animateExpand();
    }

    @Override
    public void collapse() {
        animateCollapse();
    }

    private void animateExpand() {
        RotateAnimation rotate =
                new RotateAnimation(360, 180, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.setAnimation(rotate);
    }

    private void animateCollapse() {
        RotateAnimation rotate =
                new RotateAnimation(180, 360, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.setAnimation(rotate);
    }
}
