package com.example.go4lunch.ui.activity;

import android.os.Bundle;

import com.example.go4lunch.R;
import com.google.android.material.switchmaterial.SwitchMaterial;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    SwitchMaterial languageSwitch;
    SwitchMaterial notificationSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        findViewById();
    }

    public void findViewById(){
        languageSwitch = findViewById(R.id.language_switch);
        notificationSwitch = findViewById(R.id.notification_switch);
    }
}
