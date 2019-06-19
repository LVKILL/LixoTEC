package com.example.rauber.lixotec.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.rauber.lixotec.R;

public class PreRegistrerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_register);
        Button buttonRegisterPerson = findViewById(R.id.ButtonRegisterPerson);
        Button buttonRegisterCompany = findViewById(R.id.ButtonRegisterCompany);

        buttonRegisterPerson.setOnClickListener((View v) -> {
                Intent registerPersonScreen = new Intent(PreRegistrerActivity.this, RegisterActivity.class);
                registerPersonScreen.putExtra("REGISTERTYPE","PERSON");
                startActivity(registerPersonScreen);
                finish();

        });

        buttonRegisterCompany.setOnClickListener((View v) -> {

                Intent registerCompanyScreen = new Intent(PreRegistrerActivity.this, RegisterActivity.class);
                registerCompanyScreen.putExtra("REGISTERTYPE","COMPANY");
                startActivity(registerCompanyScreen);
                finish();
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent logInScreen = new Intent(PreRegistrerActivity.this, LoginActivity.class);
        startActivity(logInScreen);
        finish();
    }
}
