package com.example.go4lunch.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;

import com.example.go4lunch.R;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Locale;

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

    public void findViewById(){
        notificationSwitch = findViewById(R.id.notification_switch);
        backBtn = findViewById(R.id.back_button);
    }

    public void setNotificationSwitch() {
    }

    public void setBackBtn() {
        backBtn.setOnClickListener(v -> startMainActivity());
    }

    public void startMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}