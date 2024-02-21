package com.unipi.mobile_dev.audiostoriesproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class LibraryActivity extends AppCompatActivity {
    TextView textSnowWhite,textKingMidas,textShoemaker,textTortoise,textRat;
    private DatabaseReference languageRef;
    String selectedLanguage = "";
    FirebaseDatabase database;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        textSnowWhite = findViewById(R.id.textViewSnowWhite);
        textKingMidas = findViewById(R.id.textViewMidas);
        textShoemaker = findViewById(R.id.textViewShoemaker);
        textTortoise = findViewById(R.id.textViewTortoise);
        textRat = findViewById(R.id.textViewRat);
        database = FirebaseDatabase.getInstance();
        languageRef = FirebaseDatabase.getInstance().getReference("Language");

        reference = FirebaseDatabase.getInstance().getReference();
        languageRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String language = snapshot.getValue(String.class);
                if (TextUtils.isEmpty(language)) {
                    showLanguageDialog();
                }else{
                    fillTitles(language);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled
            }
        });

    }
    private void showLanguageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Language");
        String[] languages = {"English", "German", "Italian"};
        int checkedItem = -1; // No item selected by default

        builder.setSingleChoiceItems(languages, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                selectedLanguage = languages[i];
                // Save the selected language preference to Realtime Database
               // languageRef = FirebaseDatabase.getInstance().getReference("Language");
                languageRef.setValue(selectedLanguage);
                dialogInterface.dismiss();

                fillTitles(selectedLanguage);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void fillTitles(String language) {
        DatabaseReference languagesPref = reference.child("All_languages").child(language);
        languagesPref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String snowTitle = snapshot.child("snow_title").getValue(String.class);
                String midasTitle = snapshot.child("midas_title").getValue(String.class);
                String shoemakerTitle = snapshot.child("shoemaker_title").getValue(String.class);
                String tortoiseTitle = snapshot.child("tortoise_title").getValue(String.class);
                String ratTitle = snapshot.child("rat_title").getValue(String.class);

                // Set the retrieved titles to the corresponding TextViews
                textSnowWhite.setText(snowTitle);
                textKingMidas.setText(midasTitle);
                textShoemaker.setText(shoemakerTitle);
                textTortoise.setText(tortoiseTitle);
                textRat.setText(ratTitle);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showMessage("Error", "Failed to read story titles");
            }
        });
    }

    public void navigateMediaPlayer(View view) {
        ImageView imageView = (ImageView) view;
        String imageViewName = getResources().getResourceEntryName(imageView.getId());
        Intent intent = new Intent(this,MediaPlayerActivity.class);
        intent.putExtra("ImageViewName",imageViewName);
        startActivity(intent);
    }
    private void showMessage(String title, String name) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage("ImageView Name: " + name)
                .setCancelable(true)
                .show();
    }

    public void settings(View view){
        showMessage("Hey" ,"Hamburger is going to be done!!");
    }

    public void viewStatistics(View view) {
        Intent intent = new Intent(this, StatisticsActivity.class);
        startActivity(intent);
    }

}