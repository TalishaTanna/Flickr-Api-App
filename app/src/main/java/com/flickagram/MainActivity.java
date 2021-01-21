package com.flickagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity{

    RecyclerView recyclerView;
    Context context;
    static RecyclerViewAdapter recyclerViewAdapter;
    static List<String> flickrPhoto = new ArrayList<>();
    static List<String> flickrPhototitle = new ArrayList<>();
    static List<String> flickrPhotolink = new ArrayList<>();
    static int pageNumber=1;
    static boolean isLoading = false;
    ConnectivityManager cm;
    DatabaseHelper mDatabaseHelper;
    static int currentSize=0;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);

        cm = (ConnectivityManager) getSystemService(MainActivity.CONNECTIVITY_SERVICE);
        recyclerViewAdapter = new RecyclerViewAdapter(getApplicationContext(), haveNetworkConnection());
        recyclerView.setAdapter(recyclerViewAdapter);
        if(haveNetworkConnection()==1){
            getPhotos(0);
        }
        else{
            mDatabaseHelper = new DatabaseHelper(getApplicationContext());
            Cursor data = mDatabaseHelper.getData();
            data.moveToFirst();
            while(!data.isAfterLast()) {
                flickrPhoto.add(data.getString(data.getColumnIndex("Image_path"))); //add the item
                flickrPhototitle.add(data.getString(data.getColumnIndex("Image_title")));
                flickrPhotolink.add(data.getString(data.getColumnIndex("Image_link")));
                data.moveToNext();
            }
            initAdapter();
            initScrollListener();
        }

    }

    public void getPhotos(int checkView) {
        Call<PhotosResponse> call = RetrofitClient.getInstance().getMyApi().getflickrjson("flickr.photos.search","64c0f179f8aec0444033c8b2c57a7db0","5","json","photos","1",pageNumber,"nature");
        call.enqueue(new Callback<PhotosResponse>() {

            @Override
            public void onResponse(Call<PhotosResponse> call, Response<PhotosResponse> response) {

                FlickrPhotos flickrPhotos = response.body().getPhotos();
                List<FlickrPhoto> flickrPhotoList = flickrPhotos.getFlickrPhotos();

                for (int i = 0; i < flickrPhotoList.size(); i++) {
                    MainActivity.flickrPhoto.add("http://farm"+flickrPhotoList.get(i).getFarm()+".staticflickr.com/"+flickrPhotoList.get(i).getServer()+"/"+flickrPhotoList.get(i).getId()+"_"+flickrPhotoList.get(i).getSecret()+".jpg");
                    MainActivity.flickrPhototitle.add(flickrPhotoList.get(i).getTitle());
                    MainActivity.flickrPhotolink.add("http://farm"+flickrPhotoList.get(i).getFarm()+".staticflickr.com/"+flickrPhotoList.get(i).getServer()+"/"+flickrPhotoList.get(i).getId()+"_"+flickrPhotoList.get(i).getSecret()+".jpg");
                }
                if(MainActivity.pageNumber==1 && checkView==0){
                    initAdapter();
                    initScrollListener();
                }
                else if(recyclerViewAdapter!=null){
                    MainActivity.flickrPhoto.remove(currentSize - 1);
                    //int scrollPosition = MainActivity.flickrPhoto.size();
                    recyclerViewAdapter.notifyItemRemoved(currentSize);
                    //recyclerViewAdapter.notifyDataSetChanged();
                    isLoading = false;
                }
            }

            @Override
            public void onFailure(Call<PhotosResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void initAdapter() {
        recyclerViewAdapter = new RecyclerViewAdapter(getApplicationContext(), haveNetworkConnection());
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2, GridLayoutManager.VERTICAL, false));
    }

    private void initScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                if (!isLoading) {
                    if (gridLayoutManager != null && gridLayoutManager.findLastCompletelyVisibleItemPosition() == MainActivity.flickrPhoto.size() - 1) {
                        //bottom of list!
                        loadMore();
                        isLoading = true;
                    }
                }
            }
        });


    }

    public void loadMore() {

        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                MainActivity.flickrPhoto.add(null);
                recyclerViewAdapter.notifyItemInserted(MainActivity.flickrPhoto.size() - 1);
                MainActivity.pageNumber=MainActivity.pageNumber+1;
                currentSize=flickrPhoto.size();
                getPhotos(0);

            }
        });
    }

    public int haveNetworkConnection() {

        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                {
                    return 1;
                }

            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                {
                    return 1;
                }
        }
        return 0;
    }
}