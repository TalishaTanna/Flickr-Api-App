package com.flickagram;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

import static androidx.core.content.FileProvider.getUriForFile;

public class DetailActivity extends AppCompatActivity {

    Button share_link;
    Button share_image;
    private ViewPager2 viewPager;
    private SwipeImageAdapter adapter2;
    ConnectivityManager cm;
    int itemInserted=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        viewPager = findViewById(R.id.view_pager);
        int position = getIntent().getIntExtra("position",0);
        cm = (ConnectivityManager) getSystemService(DetailActivity.CONNECTIVITY_SERVICE);
        adapter2 = new SwipeImageAdapter(this, haveNetworkConnection());

        viewPager.setAdapter(adapter2);
        viewPager.setCurrentItem(position);
        share_link = (Button) findViewById(R.id.share_link);
        share_image = (Button) findViewById(R.id.share_image);
        if(haveNetworkConnection()==1){
            share_link.setEnabled(true);
        }
        else{
            share_link.setEnabled(false);
        }

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (!MainActivity.isLoading && MainActivity.flickrPhoto.size()-1 == position && haveNetworkConnection()==1) {

                    viewPager.post(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.isLoading=true;
                            MainActivity.flickrPhoto.add(null);
                            itemInserted=MainActivity.flickrPhoto.size() - 1;
                            adapter2.notifyItemInserted(MainActivity.flickrPhoto.size() - 1);
                            MainActivity.pageNumber=MainActivity.pageNumber+1;
                            MainActivity.currentSize=MainActivity.flickrPhoto.size();
                            (new MainActivity()).getPhotos(1);
                        }
                    });
                }
                viewPager.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter2.notifyDataSetChanged();
                    }
                });
            }


            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                share_link.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent share = new Intent(android.content.Intent.ACTION_SEND);
                        share.setType("text/plain");
                        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                        share.putExtra(Intent.EXTRA_TEXT, MainActivity.flickrPhotolink.get(position));
                        share.putExtra(Intent.EXTRA_SUBJECT, "Share Via");
                        startActivity(Intent.createChooser(share, MainActivity.flickrPhotolink.get(position)));
                    }
                });
                share_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String item = MainActivity.flickrPhoto.get(position);
                        String file_split[]=item.split("/");
                        String file_id=file_split[file_split.length-1];
                        File imagePath = new File(getApplicationContext().getFilesDir(), "Flickagram");
                        File newFile = new File(imagePath, file_id);
                        //File newFile = new File(getApplicationContext().getFilesDir() + "/Flickagram"+ File.separator + file_id);
                        Uri contentUri = getUriForFile(getApplicationContext(), "com.flickagram.provider", newFile);
                        // new Intent
                        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.setType("image/jpeg");
                        intent.putExtra(Intent.EXTRA_STREAM, contentUri);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                share_link.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent share = new Intent(android.content.Intent.ACTION_SEND);
                        share.setType("text/plain");
                        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                        share.putExtra(Intent.EXTRA_TEXT, MainActivity.flickrPhotolink.get(position));
                        share.putExtra(Intent.EXTRA_SUBJECT, "Share Via");
                        startActivity(Intent.createChooser(share, MainActivity.flickrPhotolink.get(position)));
                    }
                });
                share_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String item = MainActivity.flickrPhoto.get(position);
                        String file_split[]=item.split("/");
                        String file_id=file_split[file_split.length-1];
                        File imagePath = new File(getApplicationContext().getFilesDir(), "Flickagram");
                        File newFile = new File(imagePath, file_id);

                        Uri contentUri = getUriForFile(getApplicationContext(), "com.flickagram.provider", newFile);
                        // new Intent
                        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.setType("image/jpeg");
                        intent.putExtra(Intent.EXTRA_STREAM, contentUri);
                        startActivity(intent);
                    }
                });
            }
        });

    }
    public int haveNetworkConnection() {

        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                     return 1;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                     return 1;
        }
        return 0;
    }


}