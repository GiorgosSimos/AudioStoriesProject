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
        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();
        boolean emailEmpty = userEmail.isEmpty();
        boolean passwordEmpty = userPassword.isEmpty();

        if (!emailEmpty && !passwordEmpty) { // Credentials are both set
            mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                showMessage(getString(R.string.success_title), getString(R.string.success_signup_description));
                                user = mAuth.getCurrentUser();
                                finish();
                            } else {
                                showMessage("Error", task.getException().getLocalizedMessage());
                            }
                        }
                    });
        } else {
            showErrorMessages(emailEmpty, passwordEmpty);
        }
    }

    private void showErrorMessages(boolean emailEmpty, boolean passwordEmpty) {
        String errorMessage;
        if (emailEmpty && passwordEmpty) {
            errorMessage = getString(R.string.error_email_password_empty);
        } else if (emailEmpty) {
            errorMessage = getString(R.string.error_email_empty);
        } else {
            errorMessage = getString(R.string.error_password_empty);
        }
        showMessage(getString(R.string.error_title), errorMessage);
    }


    void showMessage(String title, String message){
        new AlertDialog.Builder(this).
                setTitle(title).
                setMessage(message).
                setCancelable(true).
                show();
    }

    public void goSignIn(View view) {
        Intent intent = new Intent(SignUpActivity.this, WelcomeActivity.class);
        startActivity(intent);
    }
}