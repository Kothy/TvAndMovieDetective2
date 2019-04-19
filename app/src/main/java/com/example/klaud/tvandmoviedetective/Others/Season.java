package com.example.klaud.tvandmoviedetective.Others;

import com.example.klaud.tvandmoviedetective.Storing.Episode;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class Season extends ExpandableGroup<Episode> {
    public Season(String title, List<Episode> items) {
        super(title, items);
    }
}
