package com.example.rauber.lixotec.Activities;

import android.app.AlertDialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rauber.lixotec.Adapters.UserAddressAdapter;
import com.example.rauber.lixotec.Model.Endereco;
import com.example.rauber.lixotec.R;
import com.example.rauber.lixotec.SharedPreferences.SystemPreferences;
import com.example.rauber.lixotec.ViewModel.EnderecoViewModel;
import com.example.rauber.lixotec.ViewModel.UsuarioViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SelectAddressActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private EnderecoViewModel enderecoViewModel;
    private RecyclerView recyclerView;
    private List<Endereco> addressList = new ArrayList<>();
    private UserAddressAdapter userAddressAdapter;
    private SystemPreferences systemPreferences;
    private Context context;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_address);
        enderecoViewModel = ViewModelProviders.of(this).get(EnderecoViewModel.class);
        context = this;
        systemPreferences = new SystemPreferences(this);
        progressBar = findViewById(R.id.ProgressCircle);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView = findViewById(R.id.RecyclerViewUserAddress);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton newAddressButton = findViewById(R.id.fab);

        newAddressButton.setOnClickListener((View v) -> {
                Intent registerScreen = new Intent(SelectAddressActivity.this, RegisterActivity.class);
                registerScreen.putExtra("REGISTERTYPE","ADDRESS");
                startActivity(registerScreen);
                finish();
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        userAddressAdapter = new UserAddressAdapter(enderecoViewModel, this);
        recyclerView.setAdapter(userAddressAdapter);


        userAddressAdapter.setOnItemClickListener((Endereco endereco) -> {
                String address = endereco.getLogradouro() + ", "+ endereco.getNumero() + " - " + endereco.getBairro() + ", " + endereco.getCEP();
                String addressId = Integer.toString(endereco.getIdEndereco());
                Log.v("tag","address id = "+addressId);
                dialogMessage(context, address, addressId);
        });

        loadUserAddress();
        observeUserAddress();

    }

    private void loadUserAddress(){
        String json = createJsonForAddressList();
        enderecoViewModel.loadAddress(json);
    }

    private void observeUserAddress(){
        enderecoViewModel.getUserAddresses().observe(this, (String s)->{
            if(s != null){
                decodeAdressJson(s);
                if(addressList.size() > 0) {
                    userAddressAdapter.setEnderecos(addressList);
                    progressBar.setVisibility(View.INVISIBLE);
                }else{
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void decodeAdressJson(String s) {
        addressList = new ArrayList<>();

        try{
            JSONArray jsonArray = new JSONArray(s);
            if(jsonArray.length() >= 1) {
              if(!jsonArray.getJSONObject(0).getString("codigo").equals("600")) {
                  for (int i = 0; i < jsonArray.length(); i++) {
                      JSONObject jsonObject = jsonArray.getJSONObject(i);
                      String result = jsonObject.getString("resultado");
                      if (result.equals("1")) {
                          int idEndereco = Integer.parseInt(jsonObject.getString("id_endereco"));
                          String street = jsonObject.getString("logradouro");
                          String number = jsonObject.getString("numero");
                          String cep = jsonObject.getString("cep");
                          String neighborhood = jsonObject.getString("bairro");
                          String complement = jsonObject.getString("complemento");
                          boolean isPontoColeta = (jsonObject.getString("is_ponto_coleta").equals("1"));

                          if (jsonObject.getString("id_usuario") != null) {
                              int idUsuario = Integer.parseInt(jsonObject.getString("id_usuario"));
                              Endereco address = new Endereco(idEndereco, idUsuario, street, number, cep, neighborhood, isPontoColeta, complement);
                              addressList.add(address);
                          } else {
                              Endereco address = new Endereco(idEndereco, 0, street, number, cep, neighborhood, isPontoColeta, complement);
                              addressList.add(address);
                          }
                      }
                  }
              }else{
                  dialogError(context, jsonArray.getJSONObject(0).getString("mensagem"));
              }
           }
        }catch(JSONException je){
            Log.v("tag",""+je.getMessage());
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

    private void dialogMessage(Context context, String message, String addressId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View layout = getLayoutInflater().inflate(R.layout.alert_dialog_mensagem_padrao, null);
        Log.v("tag","adddress id 2 "+addressId);
        TextView textViewMessage = layout.findViewById(R.id.TextViewMessage);
        Button buttonAgree = layout.findViewById(R.id.ButtonAgree);
        builder.setView(layout);
        AlertDialog alertDialog = builder.create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        textViewMessage.setText(message);
        buttonAgree.setOnClickListener((View v) -> {

                Intent selectDateTimeScreen = new Intent(SelectAddressActivity.this, SelectDateTimeActivity.class);
                Log.v("tag","adddress id 3 "+addressId);
                selectDateTimeScreen.putExtra("addressId",addressId);
                startActivity(selectDateTimeScreen);
                alertDialog.dismiss();
                finish();
        });

        alertDialog.show();

    }


    private String[] buildFullAddress(Endereco endereco){
        String[] fullAddress = new String[5];
        fullAddress[0] =  endereco.getCEP();
        fullAddress[1] = endereco.getLogradouro();
        fullAddress[2] = endereco.getNumero();
        fullAddress[3] = endereco.getBairro();
        fullAddress[4] = String.valueOf(endereco.getIdEndereco());
        return fullAddress;
    }

    private String createJsonForAddressList(){
        String json = "";

        try{

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("tipo_consulta","enderecousuario");
            jsonObject.put("id_usuario",systemPreferences.getUserId());
            json = jsonObject.toString();

        }catch (JSONException je){
            Log.v("TAG",""+je.getMessage());
        }

        return json;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();

        if (id == R.id.nav_agendar) {

            onBackPressed();

        } else if (id == R.id.nav_consultar_pontos) {

            Intent screenPoints = new Intent(SelectAddressActivity.this, ColetasPoinsActivity.class);
            startActivity(screenPoints);
            finish();

        } else if (id == R.id.nav_consultar_historico) {

            if(systemPreferences.getUserType() != 1){
                Intent screenHistory = new Intent(SelectAddressActivity.this, HistoryColetasActivity.class);
                startActivity(screenHistory);
                finish();
            }else{
                Intent screenHistory = new Intent(SelectAddressActivity.this, AdminColetasHistoryActivity.class);
                startActivity(screenHistory);
                finish();
            }

        }else if(id == R.id.nav_sair){

            Intent loginScreen = new Intent(SelectAddressActivity.this, LoginActivity.class);
            systemPreferences.setAlreadyLogged(false);
            systemPreferences.removePreferences();
            startActivity(loginScreen);
            finish();

        }else if(id == R.id.nav_perfil){

            Intent screenPerfil = new Intent(SelectAddressActivity.this, UpdateUserActivity.class);
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
        Intent screenAgendar = new Intent(SelectAddressActivity.this, AgendarActivity.class);
        startActivity(screenAgendar);
        finish();

    }
}
