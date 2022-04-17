package com.example.hacktj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StartingPage extends AppCompatActivity implements View.OnClickListener{

    private Button registerButton;
    private Button loginButton;
    public TextView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logo = findViewById(R.id.startingPageLogo);
        registerButton = findViewById(R.id.startingPageRegister);
        loginButton = findViewById(R.id.startingPageLogin);

        registerButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.startingPageLogin:
                startActivity(new Intent(this, LoginPage.class));
                break;
            case R.id.startingPageRegister:
                startActivity(new Intent(this, RegisterPage.class));
                break;
        }
    }
}