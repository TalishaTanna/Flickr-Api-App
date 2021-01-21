package com.flickagram;

public class FlickrPhoto {

    private String id;
    private String owner;
    private String secret;
    private String server;
    private String farm;
    private String title;
    private String ispublic;
    private String isfriend;
    private String isfamily;

    public FlickrPhoto(String id, String owner, String secret, String server, String farm, String title, String ispublic, String isfriend, String isfamily) {
        this.id = id;
        this.owner = owner;
        this.secret = secret;
        this.server = server;
        this.farm = farm;
        this.title = title;
        this.ispublic = ispublic;
        this.isfriend = isfriend;
        this.isfamily = isfamily;
    }

    public String getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public String getSecret() {
        return secret;
    }

    public String getServer() {
        return server;
    }

    public String getFarm() {
        return farm;
    }

    public String getTitle() {
        return title;
    }

    public String getIspublic() {
        return ispublic;
    }

    public String getIsfriend() {
        return isfriend;
    }

    public String getIsfamily() {
        return isfamily;
    }
}

