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
        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();

        if (!userEmail.isEmpty() && !userPassword.isEmpty()){
            mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                if ("de".equals(language)){
                                    showMessage("Erfolg", "Benutzer erfolgreich angemeldet!");
                                } else if ("it".equals(language)) {
                                    showMessage("Successo", "Utente accesso con successo!");
                                } else {// Default:English
                                    showMessage("Success","User signed in successfully!");
                                }
                                saveUserType(userEmail);
                                navigateToLibraryActivity();
                            }else {
                                showMessage("Error",task.getException().getLocalizedMessage());
                            }
                        }
                    });

        }else {
            if (userEmail.isEmpty() && userPassword.isEmpty()){
                if ("de".equals(language)){
                    showMessage("Fehler", "Die E-Mail und das Passwort dürfen nicht leer sein!");
                } else if ("it".equals(language)) {
                    showMessage("Errore", "Email e password non possono essere vuoti!");
                } else {// Default:English
                    showMessage("Error","Email and password cannot be empty!");
                }
            } else if (userEmail.isEmpty()) {
                if ("de".equals(language)){
                    showMessage("Fehler", "Die E-Mail darf nicht leer sein!");
                } else if ("it".equals(language)) {
                    showMessage("Errore", "Email non può essere vuota!");
                } else {// Default:English
                    showMessage("Error","Email cannot be empty!");
                }
            } else {
                if ("de".equals(language)){
                    showMessage("Fehler", "Das Passwort darf nicht leer sein!");
                } else if ("it".equals(language)) {
                    showMessage("Errore", "La password non può essere vuota!");
                } else {// Default:English
                    showMessage("Error","Password cannot be empty!");
                }
            }

        }
    }

    public void goSignUp(View view){
        Intent intent = new Intent(WelcomeActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    public void visitor(View view) {
        saveUserType("Visitor");
        navigateToLibraryActivity();
    }


    void showMessage(String title, String message){
        new AlertDialog.Builder(this).
                setTitle(title).
                setMessage(message).
                setCancelable(true).
                show();
    }


    private void saveUserType(String userEmail) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("UserType", userEmail);
        editor.apply();
    }

    private void navigateToLibraryActivity() {
        Intent intent = new Intent(WelcomeActivity.this, LibraryActivity.class);
        startActivity(intent);
    }
}