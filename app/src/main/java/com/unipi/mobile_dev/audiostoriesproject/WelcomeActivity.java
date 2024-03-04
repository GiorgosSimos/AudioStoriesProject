package com.unipi.mobile_dev.audiostoriesproject;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class WelcomeActivity extends AppCompatActivity {
    private DatabaseReference languageRef;
    EditText email,password;
    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        email = findViewById(R.id.editTextEmailAddress);
        password = findViewById(R.id.editTextPassword);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        languageRef = FirebaseDatabase.getInstance().getReference("Language");
        languageRef.setValue("");
    }

    public void goSignIn(View view) {
        if (!email.getText().toString().isEmpty() && !password.getText().toString().isEmpty()){
            mAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                showMessage("Success","User signed in successfully!");
                                Intent intent = new Intent(WelcomeActivity.this, LibraryActivity.class);
                                intent.putExtra("UserType", email.getText().toString());
                                startActivity(intent);
                            }else {
                                showMessage("Error",task.getException().getLocalizedMessage());
                            }
                        }
                    });

        }else {
            showMessage("Error","Please provide the information!");
        }
    }

    public void goSignUp(View view){
        Intent intent = new Intent(WelcomeActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    public void visitor(View view) {
        Intent intent = new Intent(this, LibraryActivity.class);
        intent.putExtra("UserType", "Visitor");
        startActivity(intent);
    }


    void showMessage(String title, String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }
}