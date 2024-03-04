package com.unipi.mobile_dev.audiostoriesproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

import java.util.Locale;

public class LibraryActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    // Retrieve system's default locale language
    Locale defaultLocale = Locale.getDefault();
    String lan = defaultLocale.getLanguage();
    DrawerLayout drawerLayout;
    ImageView burger_menu;
    LinearLayout home, language, about, contact, login_logout;
    String userType ="";

    BottomNavigationView bottomNavigationView;
    TextView textSnowWhite, textKingMidas, textShoemaker, textTortoise, textRat, userInfo, login_logout_text;
    private DatabaseReference languageRef;
    String selectedLanguage = "";
    FirebaseDatabase database;
    DatabaseReference reference;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        sharedPreferences = getSharedPreferences("com.unipi.mobile_dev.audiostoriesproject", MODE_PRIVATE);
        // Link options of hamburger menu
        drawerLayout = findViewById(R.id.drawerLayout);
        burger_menu = findViewById(R.id.burger_menu);
        userInfo = findViewById(R.id.user_info);
        home = findViewById(R.id.home);
        language = findViewById(R.id.language);
        about = findViewById(R.id.about);
        contact = findViewById(R.id.contact);
        login_logout = findViewById(R.id.login_logout);
        login_logout_text = findViewById(R.id.login_logout_text);
        userType = sharedPreferences.getString("UserType", "default_value");
        if (userType.equals("Visitor")){
            login_logout_text.setText("Sign In / Sign Up");
        } else {
            login_logout_text.setText("Logout");
        }
        userInfo.setText(userType);
        showMessage("Language",lan);

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
                checkLanguage();
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
        login_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userType.equals("Visitor")) {
                    Toast.makeText(LibraryActivity.this, "Please sign in or sign up", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LibraryActivity.this, "Logout Successfull", Toast.LENGTH_SHORT).show();
                }
                redirectActivity(LibraryActivity.this, WelcomeActivity.class);
                SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
                prefsEditor.clear();
                prefsEditor.apply();
            }
        });

        // Retrieve extra information from the Intent
        Intent intent = getIntent();
        userType = intent.getStringExtra("UserType");
        if (userType != null && userType.equals("Visitor")) {
            if (userType.equals("Visitor")) {
                login_logout_text.setText("Sign In / Sign Up");
            } else {
                login_logout_text.setText("Logout");
            }
            userInfo.setText(userType);

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
                    intentPlayer.putExtra("ImageViewName", "imageViewSnowWhite");// Default selection of Story 1
                    startActivity(intentPlayer);
                    overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                    finish();
                    return true;
                } else if (itemId == R.id.statistics) {
                    if (!userType.equals("Visitor")) {
                        Intent intentStats = new Intent(getApplicationContext(), StatisticsActivity.class);
                        startActivity(intentStats);
                        overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                        finish();
                    } else {
                        if(lan.equals("de")){
                            Toast.makeText(LibraryActivity.this, "Nur für eingeloggte Benutzer verfügbar!", Toast.LENGTH_SHORT).show();
                        }else if (lan.equals("it")){
                            Toast.makeText(LibraryActivity.this, "Disponibile solo per gli utenti registrati!", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(LibraryActivity.this, "Available only for logged in users!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    return true;

                } else {
                    return false;
                }
            });
        }
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

    private void checkLanguage(){
        reference = FirebaseDatabase.getInstance().getReference();
        languageRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String language = snapshot.getValue(String.class);
                showLanguageDialog();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
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