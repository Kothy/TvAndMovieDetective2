package com.example.klaud.tvandmoviedetective.Asyncs;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Environment;

import com.example.klaud.tvandmoviedetective.Fragments.MoviesResultSearch;
import com.example.klaud.tvandmoviedetective.Fragments.TvSeriesResultSearch;
import com.example.klaud.tvandmoviedetective.MainActivity;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FillInfoForSearchSeries extends AsyncTask<String, String, String> {
    ProgressDialog pd;

    @Override
    protected String doInBackground(String... strings) {
        try (BufferedReader br = new BufferedReader(new FileReader(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                        "/Detective/" + "tv_series_ids_" + strings[1] + ".json"))) {
            String line = br.readLine();
            while (line != null) {
                line = br.readLine();
                if (line != null) {
                    line = line.toLowerCase();
                    MainActivity.series.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strings[0];
    }

    @Override
    protected void onPostExecute(String result) {
        pd.dismiss();
        try {
            TvSeriesResultSearch.doMySearch(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPreExecute() {
        pd = new ProgressDialog(MoviesResultSearch.ctx);
        pd.setTitle("Please wait");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCancelable(false);
        pd.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        pd.setMax(100);
        pd.show();
    }
}
