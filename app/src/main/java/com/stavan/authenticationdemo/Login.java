package com.stavan.authenticationdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

public class Login extends AppCompatActivity {

    TextView newAccount, forgot;
    Button loginBtn;
    EditText loginEmailInput, loginPasswordInput;
    ProgressBar progressBar;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        newAccount = findViewById(R.id.newAccount);
        forgot = findViewById(R.id.forgot);
        loginBtn = findViewById(R.id.loginBtn);
        loginEmailInput = findViewById(R.id.loginEmailInput);
        loginPasswordInput = findViewById(R.id.loginPasswordInput);
        progressBar = findViewById(R.id.progressBar2);
        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() != null) {
            if(!auth.getCurrentUser().isEmailVerified()) {
                emailVerification(auth.getCurrentUser());
                return;
            }
            startActivity(new Intent(getApplicationContext(), WelcomePage.class));
            finish();
        }

        newAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
                finish();
            }
        });

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, ResetPass.class));
                finish();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = loginEmailInput.getText().toString().trim();
                String pass = loginPasswordInput.getText().toString().trim();

                if(TextUtils.isEmpty(email)) {
                    loginEmailInput.setError("Email is Required");
                    return;
                }
                if(TextUtils.isEmpty(pass)) {
                    loginPasswordInput.setError("Password is Required");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {

                            FirebaseUser user = auth.getCurrentUser();

                            if(!user.isEmailVerified()) {
                                emailVerification(user);
                            }
                            else {
                                Toast.makeText(Login.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), WelcomePage.class));
                                finish();
                            }
                        }
                        else {
                            Toast.makeText(Login.this, "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

    }

    private void emailVerification(FirebaseUser user) {
        user.sendEmailVerification();
        Toast.makeText(Login.this, "Email verification link sent to your email id. Please verify your account!", Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);
    }
}