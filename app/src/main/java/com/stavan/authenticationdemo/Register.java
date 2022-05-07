package com.stavan.authenticationdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    TextView existingAccount;
    Button registerBtn;
    EditText nameInput, emailInput, passwordInput, numberInput;
    ProgressBar progressBar;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        existingAccount = findViewById(R.id.existingAccount);
        registerBtn = findViewById(R.id.registerBtn);
        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        numberInput = findViewById(R.id.numberInput);
        progressBar = findViewById(R.id.progressBar);
        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), WelcomePage.class));
            finish();
        }

        existingAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = nameInput.getText().toString().trim();
                String email = emailInput.getText().toString().trim();
                String pass = passwordInput.getText().toString().trim();
                String number = numberInput.getText().toString().trim();

                if(TextUtils.isEmpty(email)) {
                    emailInput.setError("Email is Required");
                    return;
                }
                if(TextUtils.isEmpty(pass)) {
                    passwordInput.setError("Password is Required");
                    return;
                }

                if(pass.length() < 6) {
                    passwordInput.setError("Password must be at least 6 characters long");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {

                            User user = new User(name, email, number);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(auth.getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.d("DBAdd", "User " + name + " added to database");
                                }
                            });

                            auth.getCurrentUser().sendEmailVerification();
                            Toast.makeText(Register.this, "Email verification link sent to your email id. Please verify your account!", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);

                            auth.signOut();

                            Intent intent = new Intent(Register.this, Login.class);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Toast.makeText(Register.this, "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

    }
}