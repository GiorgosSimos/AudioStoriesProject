package com.unipi.mobile_dev.audiostoriesproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class MediaPlayerActivity extends AppCompatActivity {
    ImageView storyImage;
    TextView title, author, year;
    EditText fairyTail;
    StorageReference storageReference;
    FirebaseDatabase database;
    DatabaseReference reference1, reference2;
    MyTts mtts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        storyImage = findViewById(R.id.imageView5);
        title = findViewById(R.id.textView5);
        author = findViewById(R.id.textView6);
        year = findViewById(R.id.textView7);
        fairyTail = findViewById(R.id.editTextText);
        storageReference = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance();
        reference1 = database.getReference("Story1");
        reference2 = database.getReference("Story2");
        mtts = new MyTts(this);
    }

    public void readStory(View view) {
        reference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                fairyTail.setText(snapshot.child("Story").getValue().toString());
                showMessage("Story",fairyTail.toString());

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
