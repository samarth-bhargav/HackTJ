package com.example.hacktj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginPage extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private TextView logo;
    private EditText editEmail;
    private EditText editPassword;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        editEmail = findViewById(R.id.loginPageEmail);
        editPassword = findViewById(R.id.loginPagePassword);
        loginButton = findViewById(R.id.loginPageButton);
        logo = findViewById(R.id.loginPageLogo);

        loginButton.setOnClickListener(this);
        logo.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.loginPageLogo:
                startActivity(new Intent(LoginPage.this, StartingPage.class));
                break;
            case R.id.loginPageButton:
                loginUser();
                break;
        }
    }
    public void loginUser(){
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString();

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(LoginPage.this, "Welcome", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginPage.this, HomePage.class));
                        }
                        else{
                            Toast.makeText(LoginPage.this, "Please Check your Credentials", Toast.LENGTH_SHORT).show();
                            editEmail.setText("");
                            editPassword.setText("");
                        }
                    }
                });
    }
}