package com.hackdevelopers.shahrukhkhansongs;

public class Category {
    private String label, id;

    public Category() {
    }

    public Category(String label, String id) {
        this.label = label;
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
