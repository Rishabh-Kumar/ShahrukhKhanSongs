package com.hackdevelopers.shahrukhkhansongs;

public class Song {

    private String name, thumbnail, id, year, key;
    private long clicks;

    public Song() {
    }

    public Song(String name, String thumbnail, String id, String year, String key, long clicks) {
        this.name = name;
        this.thumbnail = thumbnail;
        this.id = id;
        this.year = year;
        this.key = key;
        this.clicks = clicks;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public long getClicks() {
        return clicks;
    }

    public void setClicks(long clicks) {
        this.clicks = clicks;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
