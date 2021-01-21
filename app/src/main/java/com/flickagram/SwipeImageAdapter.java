package com.flickagram;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class SwipeImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    String fileUri="";
    MainActivity mainActivity = new MainActivity();
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    DatabaseHelper mDatabaseHelper;
    int networkAvailable=0;
    public SwipeImageAdapter(Context context, int networkAvailable)
    {
        this.context = context;
        this.mDatabaseHelper = new DatabaseHelper(context);
        this.networkAvailable = networkAvailable;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewpager_item,
                    parent, false);
            return new ImageSwiper(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewpager_item_loading, parent, false);
            return new LoadingViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ImageSwiper) {
            populateItems((ImageSwiper) holder, position);
        } else if (holder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) holder, position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return MainActivity.flickrPhoto.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    private void populateItems(ImageSwiper holder, int position) {
        String item = MainActivity.flickrPhoto.get(position);
        String itemTitle = MainActivity.flickrPhototitle.get(position);
        String file_split[]=item.split("/");
        String file_id=file_split[file_split.length-1];

        if(networkAvailable==1)
        {
            holder.textView.setText(itemTitle);
            Glide.with(this.context)
                    .load(item)
                    .into(holder.imageView);
            Glide.with(this.context).asBitmap().load(item).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                    try {
                        File mydir = new File(context.getFilesDir() + "/Flickagram");
                        if (!mydir.exists()) {
                            mydir.mkdirs();
                        }
                        fileUri = mydir.getAbsolutePath() + File.separator + file_id;
                        if (!new File(mydir.getAbsolutePath().toString() + File.separator + file_id).exists()) {
                            FileOutputStream outputStream = new FileOutputStream(fileUri);

                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                            boolean insertData = mDatabaseHelper.addData(file_id,mydir.getAbsolutePath().toString() + File.separator + file_id, itemTitle,item);
                            outputStream.flush();
                            outputStream.close();
                        }
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onLoadCleared(Drawable placeholder) {
                }
            });
        }
        else{
            holder.textView.setText(itemTitle);
            File imgFile = new  File(item);
            if(imgFile.exists()){

                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                holder.imageView.setImageBitmap(myBitmap);

            };
        }
    }
    private void showLoadingView(LoadingViewHolder viewHolder, int position) {
        //ProgressBar would be displayed
    }

    @Override
    public int getItemCount() {
        return MainActivity.flickrPhoto == null ? 0 : MainActivity.flickrPhoto.size();
    }

    class ImageSwiper extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;
        public ImageSwiper(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image_detail);
            textView = itemView.findViewById(R.id.title_detail);
        }


    }

     class LoadingViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

}
