package com.android.dejaphoto;

import java.util.List;
import java.util.Map;

public class User {

    List<Object> friends;
    Map<String, Object> photos;

    public User() {
    }

    public User(List<Object> friends, Map<String, Object> photos) {
        this.friends = friends;
        this.photos = photos;
    }

    public List<Object> getFriends() {
        return friends;
    }

    public Map<String, Object> getPhotos() {
        return photos;
    }

}
