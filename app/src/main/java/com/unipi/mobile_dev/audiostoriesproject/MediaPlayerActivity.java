package com.unipi.mobile_dev.audiostoriesproject;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class MediaPlayerActivity extends AppCompatActivity {
    ImageView storyImage;
    Button playButton,stopButton;
    TextView title, author, year;
    EditText fairyTail;
    StorageReference storageReference;
    FirebaseDatabase database;
    DatabaseReference reference;
    MyTts mytts;
    String icon,type;
    String imageViewName;
    int currentId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        storyImage = findViewById(R.id.imageViewMainPic);
        title = findViewById(R.id.textViewTitle);
        author = findViewById(R.id.textViewAuthor);
        year = findViewById(R.id.textViewYear);
        playButton = findViewById(R.id.buttonPlay);
        stopButton = findViewById(R.id.buttonStop);
        fairyTail = findViewById(R.id.editTextText);
        storageReference = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance();

        imageViewName = getIntent().getStringExtra("ImageViewName");
     //   textViewName = getIntent().getStringExtra("TextViewName");
        mytts = new MyTts(this);

        switch(imageViewName){
             case "imageViewSnowWhite":
                reference = database.getReference("Story1");
                icon = "snow_white_rose_red.jpg";
                currentId = 1;
                type = "jpg";
                 break;
             case "imageViewMidas":
                reference = database.getReference("Story2");
                icon = "kingmidas.png";
                currentId = 2;
                type = "png";
                break;
            case "imageViewShoemaker":
                reference = database.getReference("Story3");
                icon = "elves_shoemaker.jpg";
                currentId = 3;
                type = "jpg";
                break;
            case "imageViewTortoise":
                reference = database.getReference("Story4");
                icon = "tortoise_and_rabbit.jpg";
                currentId = 4;
                type = "jpg";
                break;
            case "imageViewRat":
                reference = database.getReference("Story5");
                icon = "poshrat.png";
                currentId = 5;
                type = "png";
                break;
        }
   /*    switch(textViewName){
            case "textViewSnowWhite":
                reference = database.getReference("Story1");
                icon = "snow_white_rose_red.jpg";
                currentId = 1;
                type = "jpg";
                break;
            case "textViewMidas":
                reference = database.getReference("Story2");
                icon = "kingmidas.png";
                currentId = 2;
                type = "png";
                break;
            case "textViewShoemaker":
                reference = database.getReference("Story3");
                icon = "elves_shoemaker.jpg";
                currentId = 3;
                type = "jpg";
                break;
            case "textViewTortoise":
                reference = database.getReference("Story4");
                icon = "tortoise_and_rabbit.jpg";
                currentId = 4;
                type = "jpg";
                break;
            case "textViewRat":
                reference = database.getReference("Story5");
                icon = "poshrat.png";
                currentId = 5;
                type = "png";
                break;
        }   */

    }



    public void readStory() {
        stopButton.setVisibility(View.VISIBLE);
        playButton.setVisibility(View.GONE);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                fairyTail.setText(snapshot.child("Description").getValue().toString());
                try{
                    File file = File.createTempFile("temp",type);
                    StorageReference imageRef = storageReference.child(icon);
                    imageRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            storyImage.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
                        }
                    });
                }catch (IOException e){
                    throw new RuntimeException(e);
                }
                title.setText("Title: "+Objects.requireNonNull(snapshot.child("Title").getValue()).toString());
                author.setText("Author: "+Objects.requireNonNull(snapshot.child("Author").getValue()).toString());
                year.setText("Year: "+Objects.requireNonNull(snapshot.child("Year").getValue()).toString());
                mytts.speak(Objects.requireNonNull(snapshot.child("Description").getValue()).toString());

                int shows = Integer.parseInt(snapshot.child("Shows").getValue().toString());
                reference.child("Shows").setValue(shows+1);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showMessage("Error", "Failed to read story");
            }
        });
    }
    public void readStory(View view) {
        readStory();
    }

    public void nextStory(){
        if (currentId % 5 != 0){
            currentId++;
            reference = database.getReference("Story"+currentId);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    icon = snapshot.child("Image").getValue(String.class);
                    String[] parts = icon.split("\\.");
                    if (parts.length > 1) {
                        // The last part of the split string is the file extension
                        type = parts[parts.length - 1];
                    } else {
                        // Handle the case where the file extension cannot be determined
                        showMessage("Error", "Failed to determine image type");
                        return;
                    }
                    readStory();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    showMessage("Error", "Failed to retrieve information from Firebase");
                }
            });
        } else {
            reference = database.getReference("Story1");
            currentId = 1;
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    icon = snapshot.child("Image").getValue(String.class);
                    String[] parts = icon.split("\\.");
                    if (parts.length > 1) {
                        // The last part of the split string is the file extension
                        type = parts[parts.length - 1];
                    } else {
                        // Handle the case where the file extension cannot be determined
                        showMessage("Error", "Failed to determine image type");
                        return;
                    }
                    readStory();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    showMessage("Error", "Failed to retrieve information from Firebase");
                }
            });
        }
    }

    public void previousStory(){
        if (currentId != 1){
            currentId --;
            reference = database.getReference("Story"+currentId);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    icon = snapshot.child("Image").getValue(String.class);
                    String[] parts = icon.split("\\.");
                    if (parts.length > 1) {
                        // The last part of the split string is the file extension
                        type = parts[parts.length - 1];
                    } else {
                        // Handle the case where the file extension cannot be determined
                        showMessage("Error", "Failed to determine image type");
                        return;
                    }
                    readStory();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    showMessage("Error", "Failed to retrieve information from Firebase");
                }
            });
        } else {
            reference = database.getReference("Story5");
            currentId = 5;
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    icon = snapshot.child("Image").getValue(String.class);
                    String[] parts = icon.split("\\.");
                    if (parts.length > 1) {
                        // The last part of the split string is the file extension
                        type = parts[parts.length - 1];
                    } else {
                        // Handle the case where the file extension cannot be determined
                        showMessage("Error", "Failed to determine image type");
                        return;
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    showMessage("Error", "Failed to retrieve information from Firebase");
                }
            });
        }
        readStory();
    }
    private void showMessage(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .show();
    }
    public void stop() {
        mytts.stop(); // Stop the text-to-speech engine
        stopButton.setVisibility(View.INVISIBLE); // Hide the button
        playButton.setVisibility(View.VISIBLE);
    }
    public void stopAndPlayNext(View view){
        mytts.stop(); // Stop the text-to-speech engine
        nextStory();
    }
    public void stopAndPlayPrevious(View view){
        mytts.stop();
        previousStory();

    }
    public void stop(View view){
        stop();
    }
}
