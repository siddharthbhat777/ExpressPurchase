package com.project.myapplication.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;

import com.project.myapplication.R;

public class Settings extends AppCompatActivity {

    SwitchCompat switchTheme;
    SharedPreferences sharedPreferences = null;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        switchTheme = findViewById(R.id.night_switch_button);

        //Status Bar Color
        getWindow().setStatusBarColor(ContextCompat.getColor(Settings.this, R.color.purple));

        sharedPreferences = getSharedPreferences("night", 0);
        boolean booleanValue = sharedPreferences.getBoolean("night_mode", true);

        if (booleanValue) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            switchTheme.setChecked(true);
        }

        switchTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    switchTheme.setChecked(true);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("night_mode", true);
                    editor.apply();
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    switchTheme.setChecked(false);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("night_mode", false);
                    editor.apply()                                      ;
                }
            }
        });

        findViewById(R.id.preferencesCardView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.this, Preferences.class);
                startActivity(intent);
            }
        });
    }
}