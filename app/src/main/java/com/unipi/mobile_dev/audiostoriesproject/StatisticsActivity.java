package com.unipi.mobile_dev.audiostoriesproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StatisticsActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference reference;
    TextView textView1,textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        textView1 = findViewById(R.id.textViewAllStats);
        textView2 = findViewById(R.id.textViewWinner);

    }

    public void winner(){
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int max = Integer.MIN_VALUE;
                String title = "";
                for(DataSnapshot storySnapshot : snapshot.getChildren()){
                    Integer shows = storySnapshot.child("Shows").getValue(Integer.class);
                    if(shows != null && shows>max){
                        max = shows;
                        title = storySnapshot.child("Title").getValue(String.class);
                    }
                }
                showMessage("Story with the most shows: ",title);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void winner(View view){
        winner();
    }

    public void loser(){
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int min = Integer.MAX_VALUE;
                String title = "";
                for(DataSnapshot storySnapshot : snapshot.getChildren()){
                    Integer shows = storySnapshot.child("Shows").getValue(Integer.class);
                    if(shows != null && shows<min){
                        min = shows;
                        title =storySnapshot.child("Title").getValue().toString(); //TO DO: save the title of the story
                    }
                }
                showMessage("Less popular Story : ",title);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void loser(View view){
        loser();
    }
    public void showAllShows() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                StringBuilder allShowsStringBuilder = new StringBuilder();
                for (DataSnapshot storySnapshot : snapshot.getChildren()) {
                    String title = storySnapshot.child("Title").getValue(String.class);
                    Integer shows = storySnapshot.child("Shows").getValue(Integer.class);
                    if (title != null && shows != null) {
                        String showInfo = "Title: " + title +"\nShows: " + shows + "\n\n";
                        allShowsStringBuilder.append(showInfo);
                    }
                }
                showMessage("All Statistics", allShowsStringBuilder.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled
            }
        });
    }

    public void showAllShows(View view) {
        showAllShows();
    }
    public void previousActivity(View view){
        Intent intent = new Intent(this,LibraryActivity.class);
        startActivity(intent);
    }

    void showMessage(String title, String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }
}