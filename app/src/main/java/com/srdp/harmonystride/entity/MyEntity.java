package com.srdp.harmonystride.entity;

public class MyEntity {
    private String name;
    private int imageId;
    public MyEntity(String name, int imageId) {
        this.name = name;
        this.imageId = imageId;
    }
    public String getName() {
        return name;
    }
    public int getImageId() {
        return imageId;
    }
}
