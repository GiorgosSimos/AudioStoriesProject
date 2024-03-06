package com.unipi.mobile_dev.audiostoriesproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StatisticsActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
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
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.statistics);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.library) {
                Intent intentLibrary = new Intent(getApplicationContext(), LibraryActivity.class);
                startActivity(intentLibrary);
                overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                finish();
                return true;
            } else if (itemId == R.id.music_player) {
                Intent intentPlayer = new Intent(getApplicationContext(), MediaPlayerActivity.class);
                intentPlayer.putExtra("ImageViewName","imageViewSnowWhite");// Default selection of Story 1
                startActivity(intentPlayer);
                overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                finish();
                return true;
            } else if (itemId == R.id.statistics) {
                return true;
            } else {
                return false;
            }
        });

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
                showMessage("Story with the most shows: ","Title: "+title+"\nNumber of shows: "+max);
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
                showMessage("Story with the less shows: ","Title: "+title+"\nNumber of shows: "+min);
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
    public void go3(View view){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US"); // English (United States)

        startActivityForResult(intent,123);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 123 && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                String command = result.get(0); // Get the first recognized command
                // Process the command
                processCommand(command);
            }
        }
    }

    private void processCommand(String command) {
        // Handle the recognized command here
        // For example, you can use a switch statement to perform different actions based on the command
        switch (command.toLowerCase()) {
            case "all statistics":
                showAllShows();
                break;
            case "most favorite":
                winner();
                break;
            case "less favorite":
                loser();
                break;
            default:
                showMessage("Error","Unrecognized command");
                break;
        }
    }

    void showMessage(String title, String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }
}