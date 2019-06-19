package com.example.rauber.lixotec.Activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Picture;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rauber.lixotec.Adapters.FreeTimeAdapter;
import com.example.rauber.lixotec.R;
import com.example.rauber.lixotec.SharedPreferences.SystemPreferences;
import com.example.rauber.lixotec.ViewModel.ColetaViewModel;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.util.Calendar.DATE;

public class SelectDateTimeActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener  {

    private Button buttonToday;
    private Button buttonTomorrow;
    private Button buttonAnotherDay;
    private Button buttonContinue;
    private RecyclerView recyclerViewFreeTimes;
    private String dataSelecionada;
    private String horaSelecionada;
    private String fullAddress;
    private String fullDate;
    private int selectedDay;
    private int selectedMonth;
    private int selectedYear;
    private int webserviceError;
    private String addressId;
    private TextView labelFreeTimes;
    private Context context;
    private ColetaViewModel coletaViewModel;
    private String dateForJson;
    private List<String> hours;
    private List<String> alreadySelectedHours;
    private List<String> freeHours;
    private DatePicker datePicker;
    private FreeTimeAdapter freeTimeAdapter;
    private String selectedHour;
    private SystemPreferences systemPreferences;
    private ProgressBar progressCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_date_time);
        context = this;
        datePicker = new DatePicker(this);
        buttonToday = findViewById(R.id.ButtonToday);
        buttonTomorrow = findViewById(R.id.ButtonTomorrow);
        buttonAnotherDay = findViewById(R.id.ButtonAnotherDay);
        buttonContinue = findViewById(R.id.ButtonContinue);
        labelFreeTimes = findViewById(R.id.TextViewLabelFreeTimes);
        systemPreferences = new SystemPreferences(this);
        recyclerViewFreeTimes = findViewById(R.id.RecyclerViewFreeTimes);
        coletaViewModel = ViewModelProviders.of(this).get(ColetaViewModel.class);
        progressCircle = findViewById(R.id.ProgressCircle);

        if(getIntent().getExtras() != null) {
            addressId = getIntent().getExtras().getString("addressId");
            fullAddress = getIntent().getExtras().getString("fullAddress");
            Log.v("tag", "id endereco " + addressId);
        }

        configureHoursList();
        recyclerViewFreeTimes.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerViewFreeTimes.setHasFixedSize(true);
        freeTimeAdapter = new FreeTimeAdapter(context);
        recyclerViewFreeTimes.setAdapter(freeTimeAdapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        buttonToday.setOnClickListener(this);
        buttonTomorrow.setOnClickListener(this);
        buttonAnotherDay.setOnClickListener(this);
        buttonContinue.setOnClickListener(this);

        setAdapterClickListener(freeTimeAdapter);
        subscriveObserver();
    }

    private void setAdapterClickListener(FreeTimeAdapter fta){
        fta.setOnItemClickListener((String string) -> {
                selectedHour = string;
                if(buttonContinue.getVisibility() != View.VISIBLE){
                    buttonContinue.setVisibility(View.VISIBLE);
                }
        });
    }

    private void configureHoursList() {
        hours = new ArrayList<>();
        hours.add("14h30");
        hours.add("15h30");
        hours.add("16h30");
    }

    public void subscriveObserver(){
        coletaViewModel.observeHours().observe(this, (String s) -> {
                if(s != null) {
                    decodeJson(s);

                    if (webserviceError != 1) {
                        compareLists();
                        progressCircle.setVisibility(View.INVISIBLE);
                            if(labelFreeTimes.getVisibility() == View.INVISIBLE)
                                labelFreeTimes.setVisibility(View.VISIBLE);

                            if (freeHours.size() > 0) {
                                recyclerViewFreeTimes.setAdapter(freeTimeAdapter);
                                freeTimeAdapter.setFreeTimes(freeHours);

                                if (recyclerViewFreeTimes.getVisibility() == View.INVISIBLE)
                                    recyclerViewFreeTimes.setVisibility(View.VISIBLE);
                                } else {
                                    String emptyHours = "Não existe horário disponivel para a data " + (selectedDay + "/" + selectedMonth + "/" + selectedYear);
                                    labelFreeTimes.setText(emptyHours);
                                    if (recyclerViewFreeTimes.getVisibility() == View.VISIBLE)
                                        recyclerViewFreeTimes.setVisibility(View.INVISIBLE);
                                }
                    }else{
                        progressCircle.setVisibility(View.INVISIBLE);
                        if(recyclerViewFreeTimes.getVisibility() == View.VISIBLE)
                            recyclerViewFreeTimes.setVisibility(View.INVISIBLE);
                        if(labelFreeTimes.getVisibility() == View.VISIBLE)
                            labelFreeTimes.setVisibility(View.INVISIBLE);
                    }
                }else{

                }
        });
    }

    private void compareLists(){
        freeHours = new ArrayList<>();
        for(int i = 0 ; i < hours.size() ; i++){
            if(!alreadySelectedHours.contains(hours.get(i))){
                freeHours.add(hours.get(i));
            }
        }

    }

    public void decodeJson(String s){
        alreadySelectedHours = new ArrayList<>();

        try{
            JSONArray jsonArray = new JSONArray(s);

            if(!jsonArray.getJSONObject(0).getString("codigo").equals("600")) {

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String hoursMinutesSeconds = jsonObject.getString("hora");
                    String separatedHoursMinutesSeconds[] = hoursMinutesSeconds.split(":");
                    String hours = separatedHoursMinutesSeconds[0];
                    String minutes = separatedHoursMinutesSeconds[1];
                    String hoursMinutes = hours + "h" + minutes;
                    alreadySelectedHours.add(hoursMinutes);
                }
                webserviceError = 0;
            }else{
                webserviceError = 1;
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                dialogError(context, jsonObject.getString("mensagem"));
            }

        }catch (JSONException je){
                Log.v("tag",""+je);
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
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();

        if (id == R.id.nav_agendar) {

            Intent agendarScreen = new Intent(SelectDateTimeActivity.this, AgendarActivity.class);
            startActivity(agendarScreen);
            finish();

        } else if (id == R.id.nav_consultar_pontos) {

            Intent screenPoints = new Intent(SelectDateTimeActivity.this, ColetasPoinsActivity.class);
            startActivity(screenPoints);
            finish();

        } else if (id == R.id.nav_consultar_historico) {

            if(systemPreferences.getUserType() != 1){
                Intent screenHistory = new Intent(SelectDateTimeActivity.this, HistoryColetasActivity.class);
                startActivity(screenHistory);
                finish();
            }else{
                Intent screenHistory = new Intent(SelectDateTimeActivity.this, AdminColetasHistoryActivity.class);
                startActivity(screenHistory);
                finish();
            }

        }else if(id == R.id.nav_sair){

            Intent loginScreen = new Intent(SelectDateTimeActivity.this, LoginActivity.class);
            systemPreferences.setAlreadyLogged(false);
            systemPreferences.removePreferences();
            startActivity(loginScreen);
            finish();

        }else if(id == R.id.nav_menu_inicial){

            Intent mainScreen = new Intent(SelectDateTimeActivity.this, MainActivity.class);
            startActivity(mainScreen);
            finish();

        }else if(id == R.id.nav_perfil){

            Intent perfilScreen = new Intent(SelectDateTimeActivity.this, UpdateUserActivity.class);
            startActivity(perfilScreen);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ButtonToday:
                progressCircle.setVisibility(View.VISIBLE);
                selectedDay = datePicker.getDayOfMonth();
                selectedMonth = datePicker.getMonth() + 1;
                selectedYear = datePicker.getYear();
                fullDate = context.getResources().getString(R.string.text_view_free_times_label) + " " + (selectedDay + "/" + selectedMonth + "/" + selectedYear);
                labelFreeTimes.setText(fullDate);
                if(labelFreeTimes.getVisibility() == View.INVISIBLE) {
                    labelFreeTimes.setVisibility(View.VISIBLE);
                }
                if(buttonContinue.getVisibility() == View.VISIBLE)
                    buttonContinue.setVisibility(View.INVISIBLE);
                dateForJson = selectedYear + "/" + selectedMonth + "/" + selectedDay;
                attemptHours();
                break;
            case R.id.ButtonTomorrow:
                progressCircle.setVisibility(View.VISIBLE);
                selectedDay = datePicker.getDayOfMonth() + 1;
                selectedMonth = datePicker.getMonth() + 1;
                selectedYear = datePicker.getYear();
                fullDate = context.getResources().getString(R.string.text_view_free_times_label) + " " + (selectedDay + "/" + selectedMonth + "/" + selectedYear);
                labelFreeTimes.setText(fullDate);
                if(labelFreeTimes.getVisibility() == View.INVISIBLE)
                    labelFreeTimes.setVisibility(View.VISIBLE);
                if(buttonContinue.getVisibility() == View.VISIBLE)
                    buttonContinue.setVisibility(View.INVISIBLE);
                dateForJson = selectedYear + "/" + selectedMonth + "/" + selectedDay;
                attemptHours();
                break;
            case R.id.ButtonAnotherDay:
                progressCircle.setVisibility(View.VISIBLE);
                dialogData();
                if(buttonContinue.getVisibility() == View.VISIBLE)
                    buttonContinue.setVisibility(View.INVISIBLE);
                break;
            case R.id.ButtonContinue:
                Intent pictureScreen = new Intent(SelectDateTimeActivity.this, PictureScreen.class);
                pictureScreen.putExtra("addressId",addressId);
                pictureScreen.putExtra("pickedHour",selectedHour);
                pictureScreen.putExtra("selectedDate",dateForJson);
                pictureScreen.putExtra("fullAddress",fullAddress);
                startActivity(pictureScreen);
                finish();
                break;
        }
    }

    private String createJsonHours(String dateForJson) {
        String json = "";

        try{
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("data",String.valueOf(dateForJson));
            jsonObject.put("acao","horadisponivel");
            json = jsonObject.toString();

            Log.v("tag","jsonobject"+json);

        }catch (JSONException je){
            Log.v("tag","JsonException : "+je);
        }
    return json;
    }

    private void attemptHours(){
        if(selectedDay > 0 && selectedMonth > 0 && selectedYear > 0){
            coletaViewModel.seekHours(createJsonHours(dateForJson));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent selectAddressScreen = new Intent(SelectDateTimeActivity.this, SelectAddressActivity.class);
        startActivity(selectAddressScreen);
        finish();
    }

    public void dialogData(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = getLayoutInflater().inflate(R.layout.dialog_date, null);
        final DatePicker datePicker1;
        datePicker1 = (DatePicker) view.findViewById(R.id.datePicker);
        builder.setView(view);
        builder.setPositiveButton("OK",(DialogInterface dialog, int which) -> {
                selectedDay = datePicker1.getDayOfMonth();
                if((selectedDay - datePicker.getDayOfMonth()) <= 7 && (selectedDay - datePicker.getDayOfMonth()) >= 0) {

                    if((selectedMonth - (datePicker.getMonth() + 1) >= 0)){

                        labelFreeTimes.setTextColor(context.getResources().getColor(R.color.text_white_color));

                        selectedMonth = (datePicker1.getMonth() + 1);
                        selectedYear = datePicker1.getYear();
                        dataSelecionada = selectedDay + "/" + selectedMonth + "/" + selectedYear;
                        String labelFreeTimesString = (context.getResources().getString(R.string.text_view_free_times_label) + " " + dataSelecionada);

                        if (labelFreeTimes.getVisibility() != View.VISIBLE) {
                            labelFreeTimes.setText(labelFreeTimesString);

                            if (recyclerViewFreeTimes.getVisibility() == View.INVISIBLE)
                                recyclerViewFreeTimes.setVisibility(View.VISIBLE);

                            if (labelFreeTimes.getCurrentTextColor() == context.getResources().getColor(R.color.wrong_date_indicator)) {
                                labelFreeTimes.setTextColor(context.getResources().getColor(R.color.text_white_color));
                            }

                            labelFreeTimes.setVisibility(View.VISIBLE);
                            labelFreeTimes.setGravity(View.TEXT_ALIGNMENT_CENTER);
                        } else {
                            labelFreeTimes.setText(labelFreeTimesString);

                            if (recyclerViewFreeTimes.getVisibility() == View.INVISIBLE)
                                recyclerViewFreeTimes.setVisibility(View.VISIBLE);

                            if (labelFreeTimes.getCurrentTextColor() == context.getResources().getColor(R.color.wrong_date_indicator)) {
                                labelFreeTimes.setTextColor(context.getResources().getColor(R.color.text_white_color));
                            }

                            labelFreeTimes.setGravity(View.TEXT_ALIGNMENT_CENTER);
                        }

                        dateForJson = selectedYear + "/" + selectedMonth + "/" + selectedDay;
                        attemptHours();
                    }else{
                        if(labelFreeTimes.getVisibility() != View.VISIBLE){
                            labelFreeTimes.setText("Você não pode selecionar uma data anterior à de hoje!");
                            labelFreeTimes.setTextColor(context.getResources().getColor(R.color.wrong_date_indicator));
                            labelFreeTimes.setVisibility(View.VISIBLE);
                            labelFreeTimes.setGravity(View.TEXT_ALIGNMENT_CENTER);
                            if(recyclerViewFreeTimes.getVisibility() == View.VISIBLE)
                                recyclerViewFreeTimes.setVisibility(View.INVISIBLE);
                            progressCircle.setVisibility(View.INVISIBLE);
                        }else{
                            labelFreeTimes.setText("Você não pode selecionar uma data anterior à de hoje!");
                            labelFreeTimes.setTextColor(context.getResources().getColor(R.color.wrong_date_indicator));
                            labelFreeTimes.setGravity(View.TEXT_ALIGNMENT_CENTER);
                            if(recyclerViewFreeTimes.getVisibility() == View.VISIBLE)
                                recyclerViewFreeTimes.setVisibility(View.INVISIBLE);
                            progressCircle.setVisibility(View.INVISIBLE);
                        }
                    }
                }else{
                    if(labelFreeTimes.getVisibility() != View.VISIBLE){
                        labelFreeTimes.setText("Você deve selecionar uma data de no máximo 7 dias a partir do dia de hoje!");
                        labelFreeTimes.setTextColor(context.getResources().getColor(R.color.wrong_date_indicator));
                        labelFreeTimes.setVisibility(View.VISIBLE);
                        labelFreeTimes.setGravity(View.TEXT_ALIGNMENT_CENTER);
                        if(recyclerViewFreeTimes.getVisibility() == View.VISIBLE)
                            recyclerViewFreeTimes.setVisibility(View.INVISIBLE);
                        if(progressCircle.getVisibility() == View.VISIBLE){
                            progressCircle.setVisibility(View.INVISIBLE);
                        }
                    }else{
                        labelFreeTimes.setText("Você deve selecionar uma data de no máximo 7 dias a partir do dia de hoje!");
                        labelFreeTimes.setTextColor(context.getResources().getColor(R.color.wrong_date_indicator));
                        labelFreeTimes.setGravity(View.TEXT_ALIGNMENT_CENTER);
                        if(recyclerViewFreeTimes.getVisibility() == View.VISIBLE)
                            recyclerViewFreeTimes.setVisibility(View.INVISIBLE);
                        if(progressCircle.getVisibility() == View.VISIBLE){
                            progressCircle.setVisibility(View.INVISIBLE);
                        }
                    }
                }
        });
        builder.setNegativeButton("Cancelar", (DialogInterface dialog, int which) -> {
                progressCircle.setVisibility(View.INVISIBLE);
                dialog.dismiss();

        });
        builder.show();

    }
}
