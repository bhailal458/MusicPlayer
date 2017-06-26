package com.example.sparken02.musicplayer;

/**
 * Created by sparken02 on 23/6/17.
 */

public class Model {
    String title;
    String path;
    String size;

    public Model() {
    }

    public Model(String title, String path, String size) {
        this.title = title;
        this.path = path;
        this.size = size;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }


}
