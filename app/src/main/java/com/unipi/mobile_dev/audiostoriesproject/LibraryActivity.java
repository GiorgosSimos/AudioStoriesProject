package com.unipi.mobile_dev.audiostoriesproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LibraryActivity extends AppCompatActivity {
    private DatabaseReference languageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        languageRef = FirebaseDatabase.getInstance().getReference("Language");
        languageRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String language = snapshot.getValue(String.class);
                if (TextUtils.isEmpty(language)) {
                    showLanguageDialog();
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
                String selectedLanguage = languages[i];

                // Save the selected language preference to Realtime Database
                languageRef = FirebaseDatabase.getInstance().getReference("Language");
                languageRef.setValue(selectedLanguage);

                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
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