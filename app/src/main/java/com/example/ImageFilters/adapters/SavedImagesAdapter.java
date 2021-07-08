package com.example.ImageFilters.adapters;

import android.graphics.Bitmap;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ImageFilters.R;

import java.io.File;
import java.util.List;

public class SavedImagesAdapter  extends  RecyclerView.Adapter<SavedImagesAdapter.ViewHolder>{

    public List<Pair<File, Bitmap>> savedImages;

    public SavedImagesAdapter(List<Pair<File, Bitmap>> savedImages) {
        this.savedImages = savedImages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.item_container_image,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pair<File,Bitmap> image=savedImages.get(position);
        holder.imageView.setImageBitmap(image.second);
    }

    @Override
    public int getItemCount() {
        return savedImages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.image);
        }
    }

}
