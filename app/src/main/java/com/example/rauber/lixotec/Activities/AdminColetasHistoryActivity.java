package com.example.rauber.lixotec.Activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
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

public class AdminColetasHistoryActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private Button buttonAnotherDay;
    private DatePicker datePicker;
    private RecyclerView recyclerView;
    private HistoryColetasAdapter adapter;
    private ProgressBar progressCircle;
    private ColetaViewModel coletaViewModel;
    private List<String> coletas;
    private SystemPreferences systemPreferences;
    private Context context;
    private int selectedDay;
    private int actualDay;
    private int selectedMonth;
    private int actualMonth;
    private int actualYear;
    private int selectedYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_coletas_history);
        buttonAnotherDay = findViewById(R.id.ButtonAnotherDay);
        datePicker = new DatePicker(this);
        recyclerView = findViewById(R.id.RecyclerView);
        adapter = new HistoryColetasAdapter();
        progressCircle = findViewById(R.id.ProgressCircle);
        buttonAnotherDay.setOnClickListener(this);
        coletaViewModel = ViewModelProviders.of(this).get(ColetaViewModel.class);
        coletas = new ArrayList<>();
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        systemPreferences = new SystemPreferences(this);
        recyclerView.setAdapter(adapter);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        alterMenuAdmin(navigationView.getMenu());

        observeGetColetaHistory();
    }

    private void alterMenuAdmin(Menu menu) {
        menu.findItem(R.id.nav_perfil).setTitle("Administrador");
        menu.findItem(R.id.nav_agendar).setTitle("Agenda de Coletas");
        menu.findItem(R.id.nav_consultar_pontos).setTitle("Cadastrar/Editar Pontos de Coleta");
        menu.findItem(R.id.nav_consultar_historico).setTitle("Consultar Histórico");
    }

    private void observeGetColetaHistory(){
        coletaViewModel.getColetasHistory().observe(this, (String s) -> {
            if ( s != null){
                Log.v("tag","json receied "+s);
                decodeReceivedJson(s);

                if(coletas.size() > 0) {

                    adapter.setLista(coletas);
                    adapter.notifyDataSetChanged();
                }else{
                    adapter.clearList(coletas);
                }
                progressCircle.setVisibility(View.INVISIBLE);

            }else{
                Toast.makeText(this, "Ocorreu um erro", Toast.LENGTH_SHORT).show();
                progressCircle.setVisibility(View.INVISIBLE);
            }

        });
    }

    private void decodeReceivedJson(String json){
        if(coletas.size() > 0){
            adapter.clearList(coletas);
        }

        try{
            JSONArray jsonArray = new JSONArray(json);

            if(jsonArray.length() > 1) {
                for (int i = 0; i < jsonArray.length(); i++) {
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

                    if (jsonObject.getString("realizada").equals("0")) {
                        status = "Agendada";
                    } else {
                        status = "Realizada";
                    }
                    String date = day + "/" + month + "/" + year;
                    String hours = hour + ":" + minutes;

                    String coletaData = date + ";" + hours + ";" + status;

                    coletas.add(coletaData);
                }
            }else{
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                if(jsonObject.getString("codigo").equals("600")){
                    dialogError(context, jsonObject.getString("mensagem"));
                }
            }
        }catch (JSONException je){
            Log.v("tag","Error on decodeReceivedJson() in AdminHistoryColetasActivity "+je.getMessage());
        }
    }

    private void dialogError(Context context, String message){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        View layout = getLayoutInflater().inflate(R.layout.alert_dialog_mensagem_padrao, null);

        TextView textViewMessage = layout.findViewById(R.id.TextViewMessage);
        Button buttonAgree = layout.findViewById(R.id.ButtonAgree);
        builder.setView(layout);
        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        textViewMessage.setText(message);
        buttonAgree.setOnClickListener((View v) -> {
            alertDialog.dismiss();

        });

        alertDialog.show();

    }

    private void dialogDate(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = getLayoutInflater().inflate(R.layout.dialog_date, null);
        final DatePicker datePicker1;
        datePicker1 = (DatePicker) view.findViewById(R.id.datePicker);
        builder.setView(view);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                progressCircle.setVisibility(View.VISIBLE);
                
                selectedDay = datePicker1.getDayOfMonth();
                selectedMonth = datePicker1.getMonth() + 1;
                selectedYear = datePicker1.getYear();
                actualDay = datePicker.getDayOfMonth();
                actualMonth = datePicker.getMonth() + 1;
                actualYear = datePicker.getYear();

                if(selectedDay <= actualDay && selectedMonth <= actualMonth){
                    String json = createJsonForHistory();
                    Log.v("tag","json = "+json);
                    coletaViewModel.loadColetasHistory(json);

                }else{
                    progressCircle.setVisibility(View.INVISIBLE);
                    Toast.makeText(context, "Selecione uma data atual ou anterios àde hoje.", Toast.LENGTH_SHORT).show();
                }

            }
        });
        builder.show();
    }

    private String createJsonForHistory() {
        String json = "";
        String dateJson = selectedYear + "-" + selectedMonth + "-" + selectedDay;
        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("tipo_consulta","historicodecoletaspordia");
            jsonObject.put("data",dateJson);
            json = jsonObject.toString();
        }catch (JSONException je){
            Log.v("tag","Error in createJsonForHistory() in AdminColetasHistoryActivity "+je.getMessage());
        }

        return json;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ButtonAnotherDay:
                dialogDate(this);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent mainScreen = new Intent(AdminColetasHistoryActivity.this, MainActivity.class);
        startActivity(mainScreen);
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();

        if (id == R.id.nav_agendar) {

            Intent coletasScreen = new Intent(AdminColetasHistoryActivity.this, ColetasActivity.class);
            startActivity(coletasScreen);
            finish();

        } else if (id == R.id.nav_consultar_pontos) {

            Intent screenPoints = new Intent(AdminColetasHistoryActivity.this, ColetasPoinsActivity.class);
            startActivity(screenPoints);
            finish();

        } else if (id == R.id.nav_consultar_historico) {

            Toast.makeText(this, "Você já está na tela de Histórico.", Toast.LENGTH_SHORT).show();

        }else if(id == R.id.nav_sair){

            Intent loginScreen = new Intent(AdminColetasHistoryActivity.this, LoginActivity.class);
            systemPreferences.setAlreadyLogged(false);
            systemPreferences.removePreferences();
            startActivity(loginScreen);
            finish();

        }else if(id == R.id.nav_menu_inicial){

            Intent mainScreen = new Intent(AdminColetasHistoryActivity.this, MainActivity.class);
            startActivity(mainScreen);
            finish();

        }else if(id == R.id.nav_perfil){
            Intent screenPerfil = new Intent(AdminColetasHistoryActivity.this, SubAdminRegisterActivity.class);
            startActivity(screenPerfil);
            finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
