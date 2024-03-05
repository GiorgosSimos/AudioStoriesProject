package com.unipi.mobile_dev.audiostoriesproject;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;


public class WelcomeActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    private DatabaseReference languageRef;
    EditText email,password;
    FirebaseAuth mAuth;
    FirebaseUser user;
    String language = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        sharedPreferences = getSharedPreferences("com.unipi.mobile_dev.audiostoriesproject", Context.MODE_PRIVATE);
        email = findViewById(R.id.editTextEmailAddress);
        password = findViewById(R.id.editTextPassword);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        languageRef = FirebaseDatabase.getInstance().getReference("Language");
        languageRef.setValue("");
        Locale defaultLocale = Locale.getDefault();
        language = defaultLocale.getLanguage();
    }

    public void goSignIn(View view) {
        if (!email.getText().toString().isEmpty() && !password.getText().toString().isEmpty()){
            mAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                showMessage("Success","User signed in successfully!");
                                SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
                                prefsEditor.putString("UserType", email.getText().toString());
                                prefsEditor.apply();
                                Intent intent = new Intent(WelcomeActivity.this, LibraryActivity.class);
                                startActivity(intent);
                            }else {
                                showMessage("Error",task.getException().getLocalizedMessage());
                            }
                        }
                    });

        }else {
            if ("de".equals(language)){
                showMessage("Fehler", "Bitte geben Sie fie Informationen an!");
            } else if ("it".equals(language)) {
                showMessage("Errore", "Si prega di fornire le informazioni!");
            } else {// Default:English
                showMessage("Error","Please provide the information!");
            }

        }
    }

    public void goSignUp(View view){
        Intent intent = new Intent(WelcomeActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    public void visitor(View view) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putString("UserType", "Visitor");
        prefsEditor.apply();
        Intent intent = new Intent(this, LibraryActivity.class);
        startActivity(intent);
    }


    void showMessage(String title, String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }
}