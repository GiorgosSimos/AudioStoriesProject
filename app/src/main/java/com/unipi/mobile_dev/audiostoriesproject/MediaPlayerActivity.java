package com.unipi.mobile_dev.audiostoriesproject;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
import java.util.Locale;
import java.util.Objects;

public class MediaPlayerActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;

    Locale defaultLocale = Locale.getDefault();
    String lan = defaultLocale.getLanguage();

    BottomNavigationView bottomNavigationView;
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
        sharedPreferences = getSharedPreferences("com.unipi.mobile_dev.audiostoriesproject", MODE_PRIVATE);
        storyImage = findViewById(R.id.imageViewMainPic);
        title = findViewById(R.id.textViewTitle);
        author = findViewById(R.id.textViewAuthor);
        year = findViewById(R.id.textViewYear);
        playButton = findViewById(R.id.buttonPlay);
        stopButton = findViewById(R.id.buttonStop);
        fairyTail = findViewById(R.id.editTextText);
        storageReference = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance();

        String userType = sharedPreferences.getString("UserType", "");

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.music_player);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.library) {
                Intent intentLibrary = new Intent(getApplicationContext(), LibraryActivity.class);
                startActivity(intentLibrary);
                overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                finish();
                stop();
                return true;
            } else if (itemId == R.id.music_player) {
                return true;
            } else if (itemId == R.id.statistics) {
                if (!userType.equals("Visitor")) {
                    stop();
                    Intent intentStats = new Intent(getApplicationContext(), StatisticsActivity.class);
                    startActivity(intentStats);
                    overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                    finish();
                } else {
                    if(lan.equals("de")){// German
                        showMessage("Statistiken", "Verfügbar nur für angemeldete Benutzer!");
                    }else if (lan.equals("it")){// Italian
                        showMessage("Statistiche", "Disponibile solo per gli utenti loggati!");
                    }else{// Default: English
                        showMessage("Statistics", "Available only for logged in users!");
                    }
                }
                return true;

            } else {
                return false;
            }
        });

        imageViewName = getIntent().getStringExtra("ImageViewName");
        //   textViewName = getIntent().getStringExtra("TextViewName");
        mytts = new MyTts(this);

        loadStories();

    }
    private void loadStories() {
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
            case "imageViewCinderella":
                reference = database.getReference("Story6");
                icon = "cinderella.jpg";
                currentId = 6;
                type = "jpg";
                break;
        }
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                title.setText("Title: " + Objects.requireNonNull(snapshot.child("Title").getValue()).toString());
                author.setText("Author: " + Objects.requireNonNull(snapshot.child("Author").getValue()).toString());
                year.setText("Year: " + Objects.requireNonNull(snapshot.child("Year").getValue()).toString());
                try {
                    File file = File.createTempFile("temp", type);
                    StorageReference imageRef = storageReference.child(icon);
                    imageRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            storyImage.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
                        }
                    });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showMessage("Error", "Failed to read story details");
            }
        });
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
                title.setText(title.getHint() + ": " + Objects.requireNonNull(snapshot.child("Title").getValue()).toString());
                author.setText(author.getHint() + ": " + Objects.requireNonNull(snapshot.child("Author").getValue()).toString());
                year.setText(year.getHint() + ": " + Objects.requireNonNull(snapshot.child("Year").getValue()).toString());
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
        if (currentId % 6 != 0){
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
            reference = database.getReference("Story6");
            currentId = 6;
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
