package com.unipi.mobile_dev.audiostoriesproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class SignUpActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser user;
    EditText email, password;
    Intent intent;
    String language = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        email = findViewById(R.id.editTextEmailAddress);
        password = findViewById(R.id.editTextPassword);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        Locale defaultLocale = Locale.getDefault();
        language = defaultLocale.getLanguage();
    }

    public void signUp(View view) {
        if (!email.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
            mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                if ("de".equals(language)){
                                    showMessage("Fehler", "Bitte geben Sie fie Informationen an!");
                                } else if ("it".equals(language)) {
                                    showMessage("Errore", "Si prega di fornire le informazioni!");
                                } else {// Default:English
                                    showMessage("Success", "User profile created!");
                                }
                                user = mAuth.getCurrentUser();
                                finish();
                            } else {
                                showMessage("Error", task.getException().getLocalizedMessage());
                            }
                        }
                    });
        } else {
            if ("de".equals(language)){
                showMessage("Fehler", "Bitte geben Sie fie Informationen an!");
            } else if ("it".equals(language)) {
                showMessage("Errore", "Si prega di fornire le informazioni!");
            } else {// Default:English
                showMessage("Error","Please provide the information!");
            }
        }
    }


    void showMessage(String title, String message){
        new AlertDialog.Builder(this).
                setTitle(title).
                setMessage(message).
                setCancelable(true).
                show();
    }
}