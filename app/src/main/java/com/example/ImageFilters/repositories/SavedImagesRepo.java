package com.example.ImageFilters.repositories;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Pair;

import com.example.ImageFilters.interfaces.SavedImagesInterface;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SavedImagesRepo implements SavedImagesInterface {
    private Context context;

    public SavedImagesRepo(Context context) {
        this.context = context;
    }

    @Override
    public List<Pair<File, Bitmap>> loadSavedImages() {
        List<Pair<File,Bitmap>> savedImages=new ArrayList<>();
        File dir=new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),"Saved Images");
        if(!dir.exists()){
            boolean a=dir.mkdirs();
        }
        if(dir.listFiles()!=null) {
            for (File file : dir.listFiles()) {
                savedImages.add(new Pair<>(file, getPreviewBitmap(file)));
            }
        }
        return savedImages;
    }
    private Bitmap getPreviewBitmap(File file){
        Bitmap originalBitmap= BitmapFactory.decodeFile(file.getAbsolutePath());
        int width=150;
        int height=(originalBitmap.getHeight()*width)/originalBitmap.getWidth();
        return Bitmap.createScaledBitmap(originalBitmap,width, height, false);
    }
}
