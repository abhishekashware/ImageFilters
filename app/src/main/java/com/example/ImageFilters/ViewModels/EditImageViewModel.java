package com.example.ImageFilters.ViewModels;

import android.app.Application;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Pair;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ImageFilters.Models.ImageFilter;
import com.example.ImageFilters.repositories.EditImageRepo;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class EditImageViewModel extends AndroidViewModel {

    private EditImageRepo repo;
    private MutableLiveData<ImagePreviewDataState> imagePreviewState=new MutableLiveData<>();
    public LiveData<ImagePreviewDataState> uiState= imagePreviewState;

    public MutableLiveData<SaveFilteredImageDataState> saveFilteredImageDataState =new MutableLiveData<>();
    public  LiveData<SaveFilteredImageDataState> saveFilteredImageUiState = saveFilteredImageDataState;

    private MutableLiveData<ImageFiltersDataState> imageFiltersState=new MutableLiveData<>();
    public LiveData<ImageFiltersDataState> imageFiltersUIState= imageFiltersState;

    public EditImageViewModel(Application application) {
        super(application);
        this.repo=new EditImageRepo(getApplication().getApplicationContext());
    }

    public void loadImageFilters(Bitmap bitmap){
          SetFilters filters=new SetFilters();
          filters.execute(bitmap);
    }

    public Bitmap getPreviewImage(Bitmap bitmap){
            int previewWidth=150;
            int previewHeight=bitmap.getHeight()*previewWidth/bitmap.getWidth();
            Bitmap.createScaledBitmap(bitmap,previewWidth,previewHeight,false);
        return bitmap;
    }


    public void prepareImagePreview(Uri uri){
              SetImage setImage=new SetImage();
              setImage.execute(uri);
    }



    public Uri saveFilteredImage(Bitmap bitmap){
            SaveImage saveImage=new SaveImage();
        try {
            return saveImage.execute(bitmap).get();
        } catch (Exception e){
            e.printStackTrace();
        }
       return null;
    }

    //UI states
    private void emitImageFiltersUIState(Boolean isL,List<ImageFilter> imageFilters){
        ImageFiltersDataState dataState=new ImageFiltersDataState(isL,imageFilters);
        imageFiltersState.postValue(dataState);
    }
    private void emitUiState(Boolean isL,Bitmap bitmap){
        ImagePreviewDataState dataState=new ImagePreviewDataState(isL,bitmap);
        imagePreviewState.setValue(dataState);
    }

    private void emitSavedFilteredImageUiState(Boolean isL,Uri uri){
        SaveFilteredImageDataState dataState=new SaveFilteredImageDataState(isL,uri);
        saveFilteredImageDataState.postValue(dataState);
    }



    //models
    public class ImagePreviewDataState{
        public Boolean isLoading;
        public Bitmap bitmap;

        public ImagePreviewDataState(Boolean isLoading, Bitmap bitmap) {
            this.isLoading = isLoading;
            this.bitmap = bitmap;
        }
    }

    public class ImageFiltersDataState{
        public Boolean isLoading;
        public List<ImageFilter> imageFilters;

        public ImageFiltersDataState(Boolean isLoading, List<ImageFilter> imageFilters) {
            this.isLoading = isLoading;
            this.imageFilters=imageFilters;
        }
    }
    public class SaveFilteredImageDataState{
        public Boolean isLoading;
        public Uri uri;

        public SaveFilteredImageDataState(Boolean isLoading, Uri uri) {
            this.isLoading = isLoading;
            this.uri = uri;
        }
    }

    public class SetImage extends AsyncTask<Uri,Void,Void>{
        Bitmap bitmap;
        @Override
        protected void onPreExecute() {
            emitUiState(true,null);
        }

        @Override
        protected Void doInBackground(Uri... uris) {
            bitmap=repo.prepareImagePreview(uris[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            emitUiState(false, bitmap);
        }
    }


    public class SetFilters extends AsyncTask<Bitmap,Void,Void>{
        List<ImageFilter> filters;
        @Override
        protected void onPreExecute() {
            emitImageFiltersUIState(true,null);
        }

        @Override
        protected Void doInBackground(Bitmap... bitmaps) {
            filters = repo.getImageFilters(getPreviewImage(bitmaps[0]));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            emitImageFiltersUIState(false, filters);
        }
    }

    public class SaveImage extends AsyncTask<Bitmap,Void,Uri>{
        Uri uri;
        @Override
        protected void onPreExecute() {
            emitSavedFilteredImageUiState(true,null);
        }

        @Override
        protected Uri doInBackground(Bitmap... bitmaps) {
            uri = repo.savedFilteredImage(bitmaps[0]);
            return uri;
        }

        @Override
        protected void onPostExecute(Uri uri) {
            emitSavedFilteredImageUiState(false, uri);
        }
    }


}
