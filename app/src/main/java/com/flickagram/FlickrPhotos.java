package com.flickagram;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlickrPhotos {

    private String page, pages, perpage, total;
    private List<FlickrPhoto> photo;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public List<FlickrPhoto> getFlickrPhotos() {
        return photo;
    }
}
