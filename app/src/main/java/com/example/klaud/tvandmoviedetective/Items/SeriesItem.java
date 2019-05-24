package com.example.klaud.tvandmoviedetective.Items;

public class SeriesItem {
    public String release_date;
    public String network;
    public String lastSeen = null;
    private String name;
    private int image_drawable;
    private int id;
    private String poster_path;
    private Boolean empty = false;

    public SeriesItem(String nam, int img, int idd) {
        name = nam;
        image_drawable = img;
        id = idd;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public Boolean getEmpty() { return empty; }

    public void setEmptyTrue(){ empty = true; }

    public void setEmptyFalse(){ empty = false; }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int i) {
        id = i;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImage_drawable() {
        return image_drawable;
    }

    public void setImage_drawable(int image_drawable) {
        this.image_drawable = image_drawable;
    }
}