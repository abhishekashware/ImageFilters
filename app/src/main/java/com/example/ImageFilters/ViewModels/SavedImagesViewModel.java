package com.example.ImageFilters.ViewModels;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.Pair;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ImageFilters.repositories.SavedImagesRepo;

import java.io.File;
import java.util.List;

public class SavedImagesViewModel  extends AndroidViewModel {

    private final SavedImagesRepo repo;

    public SavedImagesViewModel(Application application) {
        super(application);
        this.repo = new SavedImagesRepo(application.getApplicationContext());
    }

    private MutableLiveData<SavedImagesDataState> savedImagesDataState=new MutableLiveData<>();
    public LiveData<SavedImagesDataState> savedImagesUiState=savedImagesDataState;

    public void loadSavedImages(){
        emitSavedImagesUiState(true,null);
        List<Pair<File,Bitmap>> images=repo.loadSavedImages();
        if(images!=null){
           emitSavedImagesUiState(false,images);
        }else {
            emitSavedImagesUiState(false,null);
        }
    }
    private void emitSavedImagesUiState(Boolean isLoading,List<Pair<File,Bitmap>> savedImages){
        SavedImagesDataState dataState=new SavedImagesDataState(isLoading,savedImages);
        savedImagesDataState.postValue(dataState);
    }



    public class SavedImagesDataState{
        public Boolean isLoading;
        public List<Pair<File, Bitmap>> savedImages;

        public SavedImagesDataState(Boolean isLoading, List<Pair<File, Bitmap>> savedImages) {
            this.isLoading = isLoading;
            this.savedImages = savedImages;
        }
    }
}
