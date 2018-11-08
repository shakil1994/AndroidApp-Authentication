package com.example.shakil.androidfirebaseauthgoogle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class LoggedActivity extends AppCompatActivity {

    TextView txt_logged_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged);

        txt_logged_email = findViewById(R.id.txt_email);
        if (getIntent() != null){
            txt_logged_email.setText(getIntent().getStringExtra("email"));
        }
    }
}
