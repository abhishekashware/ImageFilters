package com.example.ImageFilters.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.ImageFilters.R;
import com.example.ImageFilters.ViewModels.EditImageViewModel;
import com.google.android.material.button.MaterialButton;

import java.io.File;

public class OnBoardingActivity extends AppCompatActivity {
    MaterialButton importButton,openCamera,savedImages;
    private static final int REQUEST_CODE_PICK_IMAGE = 1;
    Uri tempImageUri;
    public static final String KEY_IMAGE_URI = "imageUri";
    public static final String CAMERA_URI="bitmap";
    private static final int CAMERA_R_C=3;

    private EditImageViewModel editImageViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);

        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        editImageViewModel = new ViewModelProvider(this).get(EditImageViewModel.class);

        importButton=findViewById(R.id.upload);
        openCamera=findViewById(R.id.openCamera);

        savedImages=findViewById(R.id.savedImages);

        savedImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(OnBoardingActivity.this,SavedImagesActivity.class);
                startActivity(intent);
            }
        });
        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ActivityCompat.checkSelfPermission(OnBoardingActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 4);
                    }
                }else {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivityIfNeeded(intent, REQUEST_CODE_PICK_IMAGE);
                }
            }
        });

        openCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ActivityCompat.checkSelfPermission(OnBoardingActivity.this,Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, 2);
                    }
                }else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    tempImageUri=createTempImage();
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,tempImageUri);
                    startActivityIfNeeded(intent, CAMERA_R_C);
                }
            }
        });
    }
    private Uri createTempImage(){
        File dir=new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),"Temp Images");
        if(!dir.exists()){
            boolean a=dir.mkdirs();
        }
        String fileName="IMG_"+System.currentTimeMillis()+".jpg";
        File file=new File(dir,fileName);
        return FileProvider.getUriForFile(this,getPackageName()+".provider",file);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQUEST_CODE_PICK_IMAGE && resultCode==RESULT_OK){
            Intent intent=new Intent(this,EditImageActivity.class);
            intent.putExtra(KEY_IMAGE_URI,data.getData());
            startActivity(intent);
        }
        if(requestCode==CAMERA_R_C && resultCode==RESULT_OK){
            Intent intent=new Intent(this,EditImageActivity.class);
            intent.putExtra(CAMERA_URI,tempImageUri);
            startActivity(intent);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if((requestCode==2) && (grantResults[0]==PackageManager.PERMISSION_GRANTED)){
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            tempImageUri=createTempImage();
            intent.putExtra(MediaStore.EXTRA_OUTPUT,tempImageUri);
            startActivityIfNeeded(intent, CAMERA_R_C);
        }

        if((requestCode==4 )&& (grantResults[0]==PackageManager.PERMISSION_GRANTED)) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityIfNeeded(intent, REQUEST_CODE_PICK_IMAGE);
        }

    }
    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}