package com.project.myapplication.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.card.MaterialCardView;
import com.project.myapplication.R;
import com.project.myapplication.databinding.ActivityWalletBinding;

public class WalletActivity extends AppCompatActivity {
    ActivityWalletBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWalletBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.rs500.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected(binding.rs500, binding.cv500, binding.amountEditText);
                deselect(binding.rs1000, binding.cv1000);
                deselect(binding.rs2000, binding.cv2000);

            }
        });

        binding.rs1000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected(binding.rs1000, binding.cv1000, binding.amountEditText);
                deselect(binding.rs500, binding.cv500);
                deselect(binding.rs2000, binding.cv2000);
            }
        });

        binding.rs2000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected(binding.rs2000, binding.cv2000, binding.amountEditText);
                deselect(binding.rs1000, binding.cv1000);
                deselect(binding.rs500, binding.cv500);
            }
        });
    }

    private void deselect(Button btn, MaterialCardView cv) {
        cv.setCardBackgroundColor(getResources().getColor(R.color.white));
        btn.setTextColor(getResources().getColor(R.color.black));
    }

    private void selected(Button button, MaterialCardView cv, EditText editText) {
        cv.setCardBackgroundColor(getResources().getColor(R.color.profile_text_color));
        button.setTextColor(getResources().getColor(R.color.white));
        String amount = button.getText().toString().substring(1);
        editText.setText(amount);
    }
}