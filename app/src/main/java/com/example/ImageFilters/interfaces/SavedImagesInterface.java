package com.example.ImageFilters.interfaces;

import android.graphics.Bitmap;
import android.util.Pair;

import java.io.File;
import java.util.List;

public interface SavedImagesInterface {
     List<Pair<File, Bitmap>> loadSavedImages();
}
