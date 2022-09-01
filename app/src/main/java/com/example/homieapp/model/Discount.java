package com.example.homieapp.model;

public class Discount {
    private String id;
    private String name;
    private String description;
    private String percent;
    private String image;

    public Discount() {
    }

    public Discount(String id, String name, String description, String percent, String image) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.percent = percent;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
