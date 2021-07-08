package com.example.ImageFilters.repositories;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;

import androidx.core.content.FileProvider;

import com.example.ImageFilters.interfaces.EditImageInterface;
import com.example.ImageFilters.Models.ImageFilter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageColorMatrixFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;

public class EditImageRepo implements EditImageInterface {
    private Context context;
    public EditImageRepo(Context context) {
        this.context=context;
    }

    @Override
    public Bitmap prepareImagePreview(Uri imageUri) {
        InputStream inputStream=getInputStream(imageUri);
        if(inputStream==null){
            return null;
        }
        Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
        int width=context.getResources().getDisplayMetrics().widthPixels;
        int height=((bitmap.getHeight()*width)/bitmap.getWidth());
        return Bitmap.createScaledBitmap(bitmap,width,height,false);
    }


    @Override
    public List<ImageFilter> getImageFilters(Bitmap bitmap) {
        GPUImage gpuImage= new GPUImage(context);
        gpuImage.setImage(bitmap);
        ArrayList<ImageFilter> imageFilters=new ArrayList<>();
        // Normal
        GPUImageFilter filter=new GPUImageFilter();
        gpuImage.setFilter(filter);
        imageFilters.add(new ImageFilter("Normal",filter,gpuImage.getBitmapWithFilterApplied()));



        // Retro
        float[] colorMatrix =new float[]{
                1.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.2f, 0.0f,
                0.1f, 0.1f, 1.0f, 0.0f,
                1.0f, 0.0f, 0.0f, 1.0f
        };
        GPUImageColorMatrixFilter matrixFilter=new GPUImageColorMatrixFilter(1.0f, colorMatrix);
        gpuImage.setFilter(matrixFilter);
        imageFilters.add(new ImageFilter("Retro", filter, gpuImage.getBitmapWithFilterApplied()));


        // Just
        GPUImageColorMatrixFilter matrixFilter2=new GPUImageColorMatrixFilter(0.9f,  new float[] {
                0.4f, 0.6f, 0.5f, 0.0f,
                0.0f, 0.4f, 1.0f, 0.0f,
                0.05f, 0.1f, 0.4f, 0.4f,
                1.0f, 1.0f, 1.0f, 1.0f
        } );
        gpuImage.setFilter(matrixFilter2);
        imageFilters.add(
                   new ImageFilter(
                            "Just",
                            filter,
                            gpuImage.getBitmapWithFilterApplied()
                    )
            );


        // Hume
        GPUImageColorMatrixFilter matrixFilter3=new GPUImageColorMatrixFilter(1.0f, new float[]{
                1.25f, 0.0f, 0.2f, 0.0f,
                0.0f, 1.0f, 0.2f, 0.0f,
                0.0f, 0.3f, 1.0f, 0.3f,
                0.0f, 0.0f, 0.0f, 1.0f
        });
        gpuImage.setFilter(matrixFilter3);
        imageFilters.add(new ImageFilter("Hume", filter, gpuImage.getBitmapWithFilterApplied()));

        // Desert
        GPUImageColorMatrixFilter matrixFilter4=new GPUImageColorMatrixFilter(1.0f, new float[]{
                0.6f, 0.4f, 0.2f, 0.05f,
                0.0f, 0.8f, 0.3f, 0.05f,
                0.3f, 0.3f, 0.5f, 0.08f,
                0.0f, 0.0f, 0.0f, 1.0f
        });
        gpuImage.setFilter(matrixFilter4);
        imageFilters.add(
                new ImageFilter(
                     "Desert",
                   filter,
                  gpuImage.getBitmapWithFilterApplied()
                )
        );


        // Old Times
        GPUImageColorMatrixFilter matrixFilter5=new GPUImageColorMatrixFilter(1.0f, new float[]{
                1.0f, 0.05f, 0.0f, 0.0f,
                -0.2f, 1.1f, -0.2f, 0.11f,
                0.2f, 0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f
        });
        gpuImage.setFilter(matrixFilter5);
        imageFilters.add(
                new ImageFilter(
                        "Old Times",
                        filter,
                        gpuImage.getBitmapWithFilterApplied()
                )
        );

        return imageFilters;
    }

    private InputStream getInputStream(Uri uri){
        try {
            return context.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void saveFile(File file,Bitmap bitmap){
        try {
            FileOutputStream fileOutputStream=new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
    @Override
    public Uri savedFilteredImage(Bitmap bitmap) {
        try{
            File mediaStorageDirectory=new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),"Saved Images");
            if(!mediaStorageDirectory.exists()){
               boolean a= mediaStorageDirectory.mkdirs();
            }
            String fileName="IMG_"+System.currentTimeMillis()+".jpg";
            File file=new File(mediaStorageDirectory,fileName);
            saveFile(file,bitmap);
            return FileProvider.getUriForFile(context,context.getPackageName()+".provider",file);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
