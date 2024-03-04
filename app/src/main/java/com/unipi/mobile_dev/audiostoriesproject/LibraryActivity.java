package com.unipi.mobile_dev.audiostoriesproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LibraryActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ImageView burger_menu;
    LinearLayout home, language, about, contact, logout;

    BottomNavigationView bottomNavigationView;
    TextView textSnowWhite, textKingMidas, textShoemaker, textTortoise, textRat, userInfo;
    private DatabaseReference languageRef;
    String selectedLanguage = "";
    FirebaseDatabase database;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        // Link options of hamburger menu
        drawerLayout = findViewById(R.id.drawerLayout);
        burger_menu = findViewById(R.id.burger_menu);
        userInfo = findViewById(R.id.user_info);
        home = findViewById(R.id.home);
        language = findViewById(R.id.language);
        about = findViewById(R.id.about);
        contact = findViewById(R.id.contact);
        logout = findViewById(R.id.logout);
        // Retrieve extra information from the Intent
        Intent intent = getIntent();
        String userType = intent.getStringExtra("UserType");
        if (userType != null && userType.equals("Visitor")){
            logout.setVisibility(View.GONE);
        }
        userInfo.setText(userType);

        burger_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer(drawerLayout);
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });
        language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLanguageDialog();
            }
        });
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(LibraryActivity.this, AboutActivity.class);
            }
        });
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(LibraryActivity.this, ContactActivity.class);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LibraryActivity.this, "Logout Successfull", Toast.LENGTH_SHORT).show();
                redirectActivity(LibraryActivity.this, WelcomeActivity.class);
            }
        });

        textSnowWhite = findViewById(R.id.textViewSnowWhite);
        textKingMidas = findViewById(R.id.textViewMidas);
        textShoemaker = findViewById(R.id.textViewShoemaker);
        textTortoise = findViewById(R.id.textViewTortoise);
        textRat = findViewById(R.id.textViewRat);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.library);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.library) {
                return true;
            } else if (itemId == R.id.music_player) {
                Intent intentPlayer = new Intent(getApplicationContext(), MediaPlayerActivity.class);
                intentPlayer.putExtra("ImageViewName","imageViewSnowWhite");// Default selection of Story 1
                startActivity(intentPlayer);
                overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                finish();
                return true;
            } else if (itemId == R.id.statistics) {
                if (userType != null && !userType.equals("Visitor")) {
                    Intent intentStats = new Intent(getApplicationContext(), StatisticsActivity.class);
                    startActivity(intentStats);
                    overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                    finish();
                } else {
                    Toast.makeText(LibraryActivity.this, "Available only for logged in users!", Toast.LENGTH_SHORT).show();
                }
                return true;

            } else {
                return false;
            }
        });
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

    public static void openDrawer(DrawerLayout drawerLayout){
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public static void closeDrawer(DrawerLayout drawerLayout){
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public static void redirectActivity(Activity activity, Class secondActivity){
        Intent intent = new Intent(activity, secondActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
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

    public void viewStatistics(View view) {
        Intent intent = new Intent(this, StatisticsActivity.class);
        startActivity(intent);
    }

}