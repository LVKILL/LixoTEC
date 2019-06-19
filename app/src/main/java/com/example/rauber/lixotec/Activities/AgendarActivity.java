package com.example.rauber.lixotec.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.rauber.lixotec.R;
import com.example.rauber.lixotec.SharedPreferences.SystemPreferences;

public class AgendarActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private SystemPreferences systemPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agendar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        systemPreferences = new SystemPreferences(this);

        Button returnButton = findViewById(R.id.ButtonReturn);
        Button agreeButton = findViewById(R.id.ButtonAgree);


        returnButton.setOnClickListener((View v) ->{
                Intent mainScreen = new Intent(AgendarActivity.this, MainActivity.class);
                startActivity(mainScreen);
                finish();
        });

        agreeButton.setOnClickListener((View v)-> {
                 Intent addressScreen = new Intent(AgendarActivity.this, SelectAddressActivity.class);
                startActivity(addressScreen);
                finish();
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();

        if (id == R.id.nav_agendar) {

            Toast.makeText(this, "Você já está na tela de agendamento", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_consultar_pontos) {

            Intent screenPoints = new Intent(AgendarActivity.this, ColetasPoinsActivity.class);
            startActivity(screenPoints);
            finish();

        } else if (id == R.id.nav_consultar_historico) {

            if(systemPreferences.getUserType() != 1){
                Intent screenHistory = new Intent(AgendarActivity.this, HistoryColetasActivity.class);
                startActivity(screenHistory);
                finish();
            }else{
                Intent screenHistory = new Intent(AgendarActivity.this, AdminColetasHistoryActivity.class);
                startActivity(screenHistory);
                finish();
            }

        }else if(id == R.id.nav_sair){

            Intent loginScreen = new Intent(AgendarActivity.this, LoginActivity.class);
            systemPreferences.setAlreadyLogged(false);
            systemPreferences.removePreferences();
            startActivity(loginScreen);
            finish();

        }else if(id == R.id.nav_menu_inicial){

            Intent mainScreen = new Intent(AgendarActivity.this, MainActivity.class);
            startActivity(mainScreen);
            finish();

        }else if(id == R.id.nav_perfil){
            Intent screenPerfil = new Intent(AgendarActivity.this, UpdateUserActivity.class);
            startActivity(screenPerfil);
            finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent mainScreen = new Intent(AgendarActivity.this, MainActivity.class);
        startActivity(mainScreen);
        finish();
    }
}
