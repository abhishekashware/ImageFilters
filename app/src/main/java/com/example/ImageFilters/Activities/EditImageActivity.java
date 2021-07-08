package com.example.ImageFilters.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ImageFilters.Models.ImageFilter;
import com.example.ImageFilters.R;
import com.example.ImageFilters.ViewModels.EditImageViewModel;
import com.example.ImageFilters.adapters.FiltersAdapter;
import com.example.ImageFilters.listeners.ImageFilterListener;
import com.theartofdev.edmodo.cropper.CropImage;

import io.reactivex.Observable;

public class EditImageActivity extends AppCompatActivity implements ImageFilterListener {
    private EditImageViewModel editImageViewModel;
    private ProgressBar progressBar,filtersProgressBar;
    private ImageView imagePreview;
    private MutableLiveData<Bitmap> filteredBM=new MutableLiveData<>();
    private Bitmap originalBM;
    private ImageView back;
    private ImageView saveImage,rotateLeft,rotateRight,undo,crop;
    private RecyclerView filtersRec;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);

        editImageViewModel = new ViewModelProvider(EditImageActivity.this).get(EditImageViewModel.class);
        progressBar = findViewById(R.id.previewProgressBar);
        back=findViewById(R.id.back);
        rotateLeft=findViewById(R.id.rotateLeft);
        rotateRight=findViewById(R.id.rotateRight);
        undo=findViewById(R.id.undo);
        crop=findViewById(R.id.crop);
        filtersProgressBar=findViewById(R.id.filtersProgressBar);
        imagePreview=findViewById(R.id.imagePreview);
        saveImage=findViewById(R.id.imageSave);
        filtersRec=findViewById(R.id.filtersRecycler);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.HORIZONTAL);
        filtersRec.setLayoutManager(manager);
        //observers
        editImageViewModel.uiState.observe(this, it->{
            if(it!=null){
                    progressBar.setVisibility(it.isLoading?View.VISIBLE:View.GONE);
                if(it.bitmap!=null) {
                    filteredBM.setValue(it.bitmap);
                    originalBM = it.bitmap;
                    imagePreview.setVisibility(View.VISIBLE);
                    editImageViewModel.loadImageFilters(originalBM);
                }
            }
            else{
                Toast.makeText(this,"Unable to prepare preview",Toast.LENGTH_SHORT).show();
            }
        });

        editImageViewModel.imageFiltersUIState.observe(this, it->{
            if(it!=null){
                filtersProgressBar.setVisibility(it.isLoading?View.VISIBLE:View.GONE);
                if(it.imageFilters!=null){
                    FiltersAdapter adapter=new FiltersAdapter(it.imageFilters,EditImageActivity.this);
                    filtersRec.setAdapter(adapter);
                }
            }else{
                Toast.makeText(this,"Unable to load Filters",Toast.LENGTH_SHORT).show();
            }
        });

        filteredBM.observe(this,bitmap->{
            imagePreview.setImageBitmap(bitmap);
        });
        editImageViewModel.saveFilteredImageUiState.observe(this, it->{
            if(it!=null){
                if(it.isLoading){
                    saveImage.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                }else {
                    saveImage.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }

            }
        });
        //displaying preview
        Uri imageUri=getIntent().getParcelableExtra(OnBoardingActivity.KEY_IMAGE_URI);
        Uri imageUriC=getIntent().getParcelableExtra(OnBoardingActivity.CAMERA_URI);

        if(imageUri!=null) {
            editImageViewModel.prepareImagePreview(imageUri);
        }
        else{
            imageUriC = getIntent().getParcelableExtra(OnBoardingActivity.CAMERA_URI);
               editImageViewModel.prepareImagePreview(imageUriC);
        }
        //listeners
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        saveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(EditImageActivity.this).setTitle("Confirmation").setMessage("Do you want to save this image?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Uri uri= editImageViewModel.saveFilteredImage(filteredBM.getValue());
                        Toast.makeText(EditImageActivity.this,"Saved to path : "+uri.getPath(),Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).setNegativeButton("No",null).show();
            }
        });
        imagePreview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                imagePreview.setImageBitmap(originalBM);
                return false;
            }
        });

        imagePreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePreview.setImageBitmap(filteredBM.getValue());
            }
        });


        rotateLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rotateBM(-90);
            }
        });
        rotateRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rotateBM(90);
            }
        });
        final Uri temp=imageUriC;
        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageUri!=null) {
                    editImageViewModel.prepareImagePreview(imageUri);
                }
                else{
                    editImageViewModel.prepareImagePreview(temp);
                }
            }
        });
        crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageUri!=null) {
                   CropImage.activity(imageUri).start(EditImageActivity.this);
                }
                else{
                    CropImage.activity(temp).start(EditImageActivity.this);
                }
            }
        });

    }

    @Override
    public void onFilterSelected(ImageFilter filter) {
        if(filter!=null){
          filteredBM.setValue(filter.bitmap);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK) {
                if (result.getUri() != null) {
                    editImageViewModel.prepareImagePreview(result.getUri());
                }
            }
        }
    }

    public void rotateBM(float degree){
       Matrix matrix = new Matrix();
        imagePreview.invalidate();
        BitmapDrawable drawable=(BitmapDrawable) imagePreview.getDrawable();
        Bitmap bitmap=drawable.getBitmap();
       matrix.postRotate(degree);
       Bitmap rotated=Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
       filteredBM.setValue(rotated);
    }

}