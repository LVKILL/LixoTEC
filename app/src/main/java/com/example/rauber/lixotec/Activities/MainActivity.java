package com.example.rauber.lixotec.Activities;

import android.app.AlertDialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rauber.lixotec.Model.Usuario;
import com.example.rauber.lixotec.R;
import com.example.rauber.lixotec.SharedPreferences.SystemPreferences;
import com.example.rauber.lixotec.ViewModel.EnderecoViewModel;
import com.example.rauber.lixotec.ViewModel.UsuarioViewModel;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private EnderecoViewModel enderecoViewModel;
    private UsuarioViewModel usuarioViewModel;
    private SystemPreferences systemPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        enderecoViewModel = ViewModelProviders.of(this).get(EnderecoViewModel.class);
        usuarioViewModel = ViewModelProviders.of(this).get(UsuarioViewModel.class);
        systemPreferences = new SystemPreferences(this);

        LinearLayout layoutFirstMainButton = findViewById(R.id.LayoutFirstButton);
        LinearLayout layoutSecondMainButton = findViewById(R.id.LayoutSecondButton);
        LinearLayout layoutThirdMainButton = findViewById(R.id.LayoutThirdButton);
        LinearLayout layourFourthMainButton = findViewById(R.id.LayoutFourthButton);
        LinearLayout layoutFifthMainButton = findViewById(R.id.LayoutFifthButton);
        TextView textViewWelcome = findViewById(R.id.TextViewWelcomeMessage);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        if(systemPreferences.getUserType() != 1) {
            String welcomeMesasge = getResources().getString(R.string.text_view_welcome_message) + " " + systemPreferences.getName();
            textViewWelcome.setText(welcomeMesasge);
            textViewWelcome.setVisibility(View.VISIBLE);

            if (systemPreferences.getUserType() != 3) {
                layoutFifthMainButton.setVisibility(View.GONE);
            }

            layoutFirstMainButton.setOnClickListener(this);
            layoutSecondMainButton.setOnClickListener(this);
            layourFourthMainButton.setOnClickListener(this);
            layoutThirdMainButton.setOnClickListener(this);
            layoutFifthMainButton.setOnClickListener(this);

        }else{
            TextView textViewAgendarColeta = findViewById(R.id.TextViewAgendarColeta);
            TextView textViewColetaPoints = findViewById(R.id.TextViewColetaPoints);
            TextView textViewConsultColetas = findViewById(R.id.TextViewConsultColetas);
            TextView textViewUpdateRegister = findViewById(R.id.TextViewUpdateRegister);

            textViewAgendarColeta.setText(getResources().getString(R.string.text_view_coleta_book));
            textViewColetaPoints.setText(getResources().getString(R.string.text_view_coleta_points));
            textViewConsultColetas.setText(getResources().getString(R.string.text_view_consult_coletas));

            alterMenuAdmin(navigationView.getMenu());

            textViewUpdateRegister.setText(getResources().getString(R.string.text_view_new_subadmin));

            String welcomeMessage = getResources().getString(R.string.text_view_welcome_message) + " Administrador";
            textViewWelcome.setText(welcomeMessage);
            textViewWelcome.setVisibility(View.VISIBLE);

            layoutFifthMainButton.setVisibility(View.INVISIBLE);

            layoutFirstMainButton.setOnClickListener((View v)-> {
                    Intent agendaColetasScreen = new Intent(MainActivity.this, ColetasActivity.class);
                    startActivity(agendaColetasScreen);
                    finish();

            });

            layoutSecondMainButton.setOnClickListener((View v) -> {
                    Intent coletaPointsScreen = new Intent(MainActivity.this, ColetasPoinsActivity.class);
                    startActivity(coletaPointsScreen);
                    finish();

            });

            layoutThirdMainButton.setOnClickListener((View v) -> {
                    Intent coletasHistoryScreen = new Intent(MainActivity.this, AdminColetasHistoryActivity.class);
                    startActivity(coletasHistoryScreen);
                    finish();

            });

            layourFourthMainButton.setOnClickListener((View v) -> {
                    Intent registerSubAdminScreen = new Intent(MainActivity.this, SubAdminRegisterActivity.class);
                    startActivity(registerSubAdminScreen);
                    finish();
            });
        }


    }

    private void alterMenuAdmin(Menu menu) {
        menu.findItem(R.id.nav_perfil).setTitle("Administrador");
        menu.findItem(R.id.nav_agendar).setTitle("Agenda de Coletas");
        menu.findItem(R.id.nav_consultar_pontos).setTitle("Cadastrar/Editar Pontos de Coleta");
        menu.findItem(R.id.nav_consultar_historico).setTitle("Consultar Histórico");
    }

    private void alterMenuUser(Menu menu){

    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.LayoutFirstButton:
                Intent screenAgendar = new Intent(MainActivity.this, AgendarActivity.class);
                startActivity(screenAgendar);
                finish();
                break;
            case R.id.LayoutSecondButton:
                Intent coletaPointsScreen = new Intent(MainActivity.this, ColetasPoinsActivity.class);
                startActivity(coletaPointsScreen);
                finish();
                break;
            case R.id.LayoutThirdButton:
                Intent coletasHistoryScreen = new Intent(MainActivity.this, HistoryColetasActivity.class);
                startActivity(coletasHistoryScreen);
                finish();
                break;
            case R.id.LayoutFourthButton:
                Intent screenUpdate = new Intent(MainActivity.this, UpdateUserActivity.class);
                startActivity(screenUpdate);
                finish();
                break;
            case R.id.LayoutFifthButton:
                prepareDialogCertificado();
                break;
        }
    }

    private String createJsonForAddressList(int userId){
        String json = "";

        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("tipo_consulta","enderecousuario");
            jsonObject.put("id_usuario",userId);
            json = jsonObject.toString();
        }catch (JSONException je){
            Log.v("TAG",""+je.getMessage());
        }

        return json;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else {
            finish();
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_logout){
            Intent loginScreen = new Intent(MainActivity.this, LoginActivity.class);
            systemPreferences.setAlreadyLogged(false);
            systemPreferences.removePreferences();
            startActivity(loginScreen);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_agendar) {

           if(systemPreferences.getUserType() != 1){
               Intent screenAgendar = new Intent(MainActivity.this, AgendarActivity.class);
               startActivity(screenAgendar);
               finish();
           }else{
               Intent screenColetas = new Intent(MainActivity.this, ColetasActivity.class);
               startActivity(screenColetas);
               finish();
           }

        } else if (id == R.id.nav_consultar_pontos) {

                Intent screenPoints = new Intent(MainActivity.this, ColetasPoinsActivity.class);
                startActivity(screenPoints);
                finish();

        } else if (id == R.id.nav_consultar_historico) {

                if(systemPreferences.getUserType() != 1){
                    Intent screenHistory = new Intent(MainActivity.this, HistoryColetasActivity.class);
                    startActivity(screenHistory);
                    finish();
                }else{
                    Intent screenHistory = new Intent(MainActivity.this, AdminColetasHistoryActivity.class);
                    startActivity(screenHistory);
                    finish();
                }

        }else if(id == R.id.nav_sair){

            Intent loginScreen = new Intent(MainActivity.this, LoginActivity.class);
            systemPreferences.setAlreadyLogged(false);
            systemPreferences.removePreferences();
            startActivity(loginScreen);
            finish();

        }else if(id == R.id.nav_perfil){

            if(systemPreferences.getUserType() != 1){
                Intent screenPerfil = new Intent(MainActivity.this, UpdateUserActivity.class);
                startActivity(screenPerfil);
                finish();
            }

        }else if(id == R.id.nav_menu_inicial){
            Toast.makeText(this, "Você já está no Menu Inicial", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void prepareDialogCertificado(){
        String mensagem = "Obrigado por utilizar nossos serviços."+ "\n";
        mensagem += "Para solicitar o certificado, envie um email com os seguintes dados "+"\n";
        mensagem += "-Nome e CPF" + "\n";
        mensagem += "-Endereço completo"+ "\n";
        mensagem += "-Razão Social" + "\n";
        mensagem += "-Telefone " + "\n";
        mensagem += "-CNPJ" + "\n";
        mensagem += "Para o seguinte email : " + "lixoteccentrodecoleta@gmail.com";

        dialogCertificado(this, mensagem);
    }

    private void dialogCertificado(Context context, String message){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        View layout = getLayoutInflater().inflate(R.layout.alert_dialog_mensagem_padrao, null);

        TextView textViewMessage = layout.findViewById(R.id.TextViewMessage);
        Button buttonAgree = layout.findViewById(R.id.ButtonAgree);
        builder.setView(layout);
        AlertDialog alertDialog = builder.create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        textViewMessage.setText(message);
        buttonAgree.setOnClickListener((View v) -> {
            alertDialog.dismiss();
        });

        alertDialog.show();

    }


}