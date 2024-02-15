package com.unipi.mobile_dev.audiostoriesproject;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
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
    TextView title, author, year;
    EditText fairyTail;
    StorageReference storageReference;
    FirebaseDatabase database;
    DatabaseReference reference;
    MyTts mytts;
    String icon,type;
    String imageViewName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        storyImage = findViewById(R.id.imageViewMainPic);
        title = findViewById(R.id.textViewTitle);
        author = findViewById(R.id.textViewAuthor);
        year = findViewById(R.id.textViewYear);
        fairyTail = findViewById(R.id.editTextText);
        storageReference = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance();
       // reference1 = database.getReference("Story2");
       // reference2 = database.getReference("Story2");
       // reference3 = database.getReference("Story3");
      // reference4 = database.getReference("Story4");
        imageViewName = getIntent().getStringExtra("ImageViewName");
        mytts = new MyTts(this);
        switch(imageViewName){
             case "imageViewSnowWhite":
                reference = database.getReference("Story5");
                icon = "snow_white_rose_red.jpg";
                type = "jpg";
                 break;
             case "imageViewMidas":
                reference = database.getReference("Story3");
                icon = "kingmidas.png";
                type = "png";
                break;
            case "imageViewShoemaker":
                reference = database.getReference("Story4");
                icon = "elves_shoemaker.jpg";
                type = "jpg";
                break;
            case "imageViewTortoise":
                reference = database.getReference("Story1");
                icon = "tortoise_and_rabbit.jpg";
                type = "jpg";
                break;
            case "imageViewRat":
                reference = database.getReference("Story2");
                icon = "poshrat.png";
                type = "png";
                break;
        }

    }

    public void readStory(View view) {

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
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

                title.setText(Objects.requireNonNull(snapshot.child("Title").getValue()).toString());
                author.setText(Objects.requireNonNull(snapshot.child("Author").getValue()).toString());
                year.setText(Objects.requireNonNull(snapshot.child("Year").getValue()).toString());
                mytts.speak(Objects.requireNonNull(snapshot.child("Description").getValue()).toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showMessage("Error", "Failed to read story");
            }
        });
    }

    private void showMessage(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .show();
    }
}
