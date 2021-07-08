package com.example.ImageFilters.Models;


import android.graphics.Bitmap;

import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;

public class ImageFilter {
    public String filterName;
    public GPUImageFilter filter;
    public Bitmap bitmap;

    public ImageFilter(String filterName, GPUImageFilter filter, Bitmap bitmap) {
        this.filterName = filterName;
        this.filter = filter;
        this.bitmap = bitmap;
    }
}
