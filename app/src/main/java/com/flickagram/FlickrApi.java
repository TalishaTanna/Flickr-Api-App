package com.flickagram;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FlickrApi {

    String BASE_URL = "https://api.flickr.com/services/";

    @GET("rest/")
    Call<PhotosResponse> getflickrjson(@Query("method") String method,@Query("api_key") String apiKey,@Query("per_page")String perPage,@Query("format") String format,@Query("media") String media,@Query("nojsoncallback") String nojsoncallback,@Query("page") int page,@Query("tags") String tags);

}
