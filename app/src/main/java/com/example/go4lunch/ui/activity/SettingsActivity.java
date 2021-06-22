package com.example.go4lunch.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import com.example.go4lunch.R;
import com.google.android.material.switchmaterial.SwitchMaterial;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    SwitchMaterial notificationSwitch;
    ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        findViewById();
        setNotificationSwitch();
        setBackBtn();
    }

    public void findViewById() {
        notificationSwitch = findViewById(R.id.notification_switch);
        backBtn = findViewById(R.id.back_button);
    }

    public void setNotificationSwitch() {
    }

    public void setBackBtn() {
        backBtn.setOnClickListener(v -> startMainActivity());
    }

    public void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
