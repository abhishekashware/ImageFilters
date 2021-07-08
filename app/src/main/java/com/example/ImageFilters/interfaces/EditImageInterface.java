package com.example.ImageFilters.interfaces;

import android.graphics.Bitmap;
import android.net.Uri;

import com.example.ImageFilters.Models.ImageFilter;

import java.util.List;


public interface EditImageInterface {
    Bitmap prepareImagePreview(Uri imageUri);
    List<ImageFilter> getImageFilters(Bitmap bitmap);
    Uri savedFilteredImage(Bitmap bitmap);
}
