package com.example.rauber.lixotec.Activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.rauber.lixotec.Adapters.HistoryColetasAdapter;
import com.example.rauber.lixotec.R;
import com.example.rauber.lixotec.SharedPreferences.SystemPreferences;
import com.example.rauber.lixotec.ViewModel.ColetaViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HistoryColetasActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ColetaViewModel coletaViewModel;
    private RecyclerView recyclerView;
    private HistoryColetasAdapter adapter;
    private SystemPreferences systemPreferences;
    private List<String> coletas;
    private ProgressBar progressCircle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_coletas);
        coletaViewModel = ViewModelProviders.of(this).get(ColetaViewModel.class);
        recyclerView = findViewById(R.id.RecyclerView);
        progressCircle = findViewById(R.id.ProgressCircle);
        coletas = new ArrayList<>();
        systemPreferences = new SystemPreferences(this);
        adapter = new HistoryColetasAdapter();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(adapter);

        progressCircle.setVisibility(View.VISIBLE);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        loadColetaHistory();
        observeGetColetaHistory();

    }

    private void observeGetColetaHistory(){
        coletaViewModel.getColetasHistory().observe(this, (String s) -> {
            if ( s != null){
                decodeReceivedJson(s);
                if(coletas.size() > 0) {

                    adapter.setLista(coletas);
                    adapter.notifyDataSetChanged();
                }
                    progressCircle.setVisibility(View.INVISIBLE);
            }else{
                Toast.makeText(this, "Ocorreu um erro", Toast.LENGTH_SHORT).show();
                progressCircle.setVisibility(View.INVISIBLE);
            }

        });
    }

    private void loadColetaHistory(){
        coletaViewModel.loadColetasHistory(createColetasHistoryJson());
    }

    private String createColetasHistoryJson(){
        String result = "";
        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("tipo_consulta","historicodecoletas");
            jsonObject.put("id_usuario", systemPreferences.getUserId());
            result = jsonObject.toString();
        }catch (JSONException je){
            Log.v("tag","Error in createColetasHistoryJson() on HistoryColetasActivity");
        }

        return result;
    }

    private void decodeReceivedJson(String json){

        try{
            JSONArray jsonArray = new JSONArray(json);
            for(int i = 0 ; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String[] DateAndHour = jsonObject.getString("data").split(" ");

                String[] breakDate = DateAndHour[0].split("-");
                String day = breakDate[2];
                String month = breakDate[1];
                String year = breakDate[0];

                String[] breakHour = DateAndHour[1].split(":");
                String seconds = breakHour[2];
                String minutes = breakHour[1];
                String hour = breakHour[0];

                String status = "";

                if (jsonObject.getString("realizada").equals("0")){
                    status = "Agendada";
                }else {
                    status = "Realizada";
                }
                String date = day + "/" + month + "/" + year;
                String hours = hour + ":" + minutes;

                String coletaData = date + ";" + hours + ";" + status;

                coletas.add(coletaData);
            }
        }catch (JSONException je){
            Log.v("tag","Error on decodeReceivedJson() in HistoryColetasActivity "+je.getMessage());
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();

        if (id == R.id.nav_agendar) {

            Intent coletasScreen = new Intent(HistoryColetasActivity.this, AgendarActivity.class);
            startActivity(coletasScreen);
            finish();

        } else if (id == R.id.nav_consultar_pontos) {

            Intent screenPoints = new Intent(HistoryColetasActivity.this, ColetasPoinsActivity.class);
            startActivity(screenPoints);
            finish();

        } else if (id == R.id.nav_consultar_historico) {

            Toast.makeText(this, "Você já está na tela de Histórico.", Toast.LENGTH_SHORT).show();

        }else if(id == R.id.nav_sair){

            Intent loginScreen = new Intent(HistoryColetasActivity.this, LoginActivity.class);
            systemPreferences.setAlreadyLogged(false);
            systemPreferences.removePreferences();
            startActivity(loginScreen);
            finish();

        }else if(id == R.id.nav_menu_inicial){

            Intent mainScreen = new Intent(HistoryColetasActivity.this, MainActivity.class);
            startActivity(mainScreen);
            finish();

        }else if(id == R.id.nav_perfil){
            Intent screenPerfil = new Intent(HistoryColetasActivity.this, UpdateUserActivity.class);
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
        Intent mainScreen = new Intent(HistoryColetasActivity.this, MainActivity.class);
        startActivity(mainScreen);
        finish();
    }
}
