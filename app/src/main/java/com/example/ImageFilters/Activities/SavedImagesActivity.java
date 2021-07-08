package com.example.ImageFilters.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Pair;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ImageFilters.R;
import com.example.ImageFilters.ViewModels.SavedImagesViewModel;
import com.example.ImageFilters.adapters.SavedImagesAdapter;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.util.List;

public class SavedImagesActivity extends AppCompatActivity {

    RecyclerView savedImages;
    List<Pair<File, Bitmap>> list;
    ProgressBar progressBar;
    SavedImagesViewModel viewModel;
    TextView noSI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        noSI=findViewById(R.id.noSavedImages);
        savedImages=findViewById(R.id.savedImagesRecycler);
        progressBar=findViewById(R.id.progressBar);
        viewModel=new ViewModelProvider(this).get(SavedImagesViewModel.class);

        //layout manager
        GridLayoutManager manager=new GridLayoutManager(this,3);
        savedImages.setLayoutManager(manager);




        //observers
        viewModel.savedImagesUiState.observe(this,it->{
            if(it!=null){
                progressBar.setVisibility(it.isLoading?View.VISIBLE:View.GONE);
                if(it.savedImages!=null){
                    list=it.savedImages;
                    //adapter
                    SavedImagesAdapter adapter=new SavedImagesAdapter(list);
                    savedImages.setAdapter(adapter);
                    savedImages.setVisibility(View.VISIBLE);
                    if(list.size()==0){
                        noSI.setVisibility(View.VISIBLE);
                    }else{
                        noSI.setVisibility(View.INVISIBLE);
                    }
                }
            }else{
                Toast.makeText(this,"Error Occurred",Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.loadSavedImages();
    }
    @Override
    protected void onResume() {
        super.onResume();
        viewModel.loadSavedImages();
    }


}