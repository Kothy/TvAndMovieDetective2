package com.example.klaud.tvandmoviedetective.Listeners;

import android.view.View;
import android.widget.AdapterView;

import com.example.klaud.tvandmoviedetective.Fragments.Theatres;
import com.example.klaud.tvandmoviedetective.GetHTMLTreeProgram;

public class CustomOnItemSelectedListenerTheatres implements AdapterView.OnItemSelectedListener {
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position > 0) {
            //Toast.makeText(parent.getContext(), "OnItemSelectedListener: " + parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
            GetHTMLTreeProgram program = new GetHTMLTreeProgram();
            program.execute(Theatres.urlForTheatres.get(position));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
