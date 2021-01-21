package com.flickagram;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class PhotosResponse {
    private FlickrPhotos photos;

    private String stat;

    private Map<String, Object> additionalProperties = new HashMap<String, Object>();


    public FlickrPhotos getPhotos() {
        return photos;
    }
}
