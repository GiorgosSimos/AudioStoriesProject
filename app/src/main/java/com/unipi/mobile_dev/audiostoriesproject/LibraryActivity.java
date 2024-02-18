package com.unipi.mobile_dev.audiostoriesproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class LibraryActivity extends AppCompatActivity {
    int imageViewId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

    }

    public void navigateMediaPlayer(View view) {
        ImageView imageView = (ImageView) view;
      //  TextView textView = (TextView) view ;
      //  String textViewName = getResources().getResourceEntryName(textView.getId());
        String imageViewName = getResources().getResourceEntryName(imageView.getId());
        Intent intent = new Intent(this,MediaPlayerActivity.class);
        intent.putExtra("ImageViewName",imageViewName);
       // intent.putExtra("TextViewName",textViewName);
        startActivity(intent);
    }

    private void showMessage(String title, String name) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage("ImageView Name: " + name)
                .setCancelable(true)
                .show();
    }

    public void viewStatistics(View view) {
        Intent intent = new Intent(this, StatisticsActivity.class);
        startActivity(intent);
    }

}