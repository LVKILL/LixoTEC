package com.example.rauber.lixotec.Activities;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.Update;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class ColetasPoinsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private EnderecoViewModel enderecoViewModel;
    private RecyclerView recyclerView;
    private List<Endereco> addressList = new ArrayList<>();
    private UserAddressAdapter userAddressAdapter;
    private SystemPreferences systemPreferences;
    private Context context;
    private ProgressBar progressBar;
    private TextView textViewSelectAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_address);
        textViewSelectAddress = findViewById(R.id.TextViewSelectAddress);
        enderecoViewModel = ViewModelProviders.of(this).get(EnderecoViewModel.class);
        context = this;
        systemPreferences = new SystemPreferences(this);

        textViewSelectAddress.setText("Clique no ponto de coleta para ver sua localização");

        progressBar = findViewById(R.id.ProgressCircle);
        progressBar.setVisibility(View.VISIBLE);

        recyclerView = findViewById(R.id.RecyclerViewUserAddress);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton newAddressButton = findViewById(R.id.fab);

        newAddressButton.setOnClickListener((View v) -> {

                Intent registerScreen = new Intent(ColetasPoinsActivity.this, RegisterActivity.class);
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


        String json = createColetaPointsJson();
        if(!json.equals("")){
            enderecoViewModel.loadColetaPoints(json);
        }

            userAddressAdapter.setOnItemClickListener((Endereco endereco) -> {
                    String street = endereco.getLogradouro() + ", "+endereco.getNumero() +", "+endereco.getBairro();
                    String location = Uri.encode( street );
                    String geo = "geo:0,0?q="+location;

                    Uri geoUri = Uri.parse(geo);
                    Intent intent = new Intent(Intent.ACTION_VIEW, geoUri);
                    intent.setPackage( "com.google.android.apps.maps" );
                    startActivity(intent);

            });

        if(systemPreferences.getUserType() != 1){
            newAddressButton.hide();
        }else{
            navigationView.getMenu().findItem(R.id.nav_perfil).setTitle("Administrador");
        }

        if(systemPreferences.getUserType() == 1) {
            ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    if (direction == ItemTouchHelper.RIGHT) {
                        if (userAddressAdapter.getEnderecos(viewHolder.getAdapterPosition()) != null) {
                            progressBar.setVisibility(View.VISIBLE);
                            Endereco endereco = userAddressAdapter.getEnderecos(viewHolder.getAdapterPosition());
                            String json = createJsonForDeleteAddress(endereco.getIdEndereco());
                            if (!json.equals("")) {
                                enderecoViewModel.deleteColetaPoint(json);
                                userAddressAdapter.delete(viewHolder.getAdapterPosition());
                                userAddressAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                }
            };
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
            itemTouchHelper.attachToRecyclerView(recyclerView);

            observeDeleteColetaPointMessage();

        }
         publishObserver();
    }

    private void observeDeleteColetaPointMessage(){
        enderecoViewModel.getDeleteColetaPointMessage().observe(this, (String s) -> {
            if(s != null){
                String[] codeAndMessage = decodeJsonMessage(s);
                if((!codeAndMessage[0].isEmpty())
                        && (!codeAndMessage[1].isEmpty())){
                    dialogMessage(context, codeAndMessage);
                }
            }
            progressBar.setVisibility(View.INVISIBLE);
        });
    }

    private String[] decodeJsonMessage(String s) {
        String[] codeAndMessage = new String[2];
        try{
            JSONArray jsonArray = new JSONArray(s);
            for(int i = 0; i < jsonArray.length() ; i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                codeAndMessage[0] = jsonObject.getString("codigo");
                codeAndMessage[1] = jsonObject.getString("mensagem");
            }


        }catch (JSONException je){
            Log.v("tag","Error on decodeJsonMessage() in ColetasPoinsActivity "+je);
        }
        return codeAndMessage;
    }

    private String createColetaPointsJson() {
        String json = "";
        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("tipo_consulta","pontosdecoleta");
            json = jsonObject.toString();
        }catch (JSONException je){
            Log.v("tag","error on createColetaPointsJson() in ColetasPoinsActivity"+je.getMessage());
        }

        return json;
    }

    private void publishObserver(){
        enderecoViewModel.observeColetaPoints().observe(this, (@Nullable String s) -> {
                if(s != null)
                    convertAndApplyJson(s);
        });
    }

    private void convertAndApplyJson(String json) {
       try {
           JSONArray jsonArray = new JSONArray(json);

            if(jsonArray.length() > 1) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = (jsonArray.getJSONObject(i));
                    int idEndereco = Integer.parseInt(jsonObject.getString("id_endereco"));
                    int idUsuario = Integer.parseInt(jsonObject.getString("id_usuario"));
                    String logradouro = jsonObject.getString("logradouro");
                    String numero = jsonObject.getString("numero");
                    String cep = jsonObject.getString("cep");
                    String bairro = jsonObject.getString("bairro");
                    String complemento = jsonObject.getString("complemento");

                    boolean isPontoColeta = (Integer.parseInt(jsonObject.getString("is_ponto_coleta")) == 1);
                    addressList.add(new Endereco(idEndereco, idUsuario, logradouro, numero, cep, bairro, isPontoColeta, complemento));
                }
            }else{
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                if(jsonObject.getString("codigo").equals("600")){
                    dialogError(context, jsonObject.getString("mensagem"));
                }
            }
       }catch (JSONException je){
           Log.v("tag","Error in convertAndApplyJson() in ColetaPoinsActivity" + je.getMessage());
       }finally {
           progressBar.setVisibility(View.INVISIBLE);
           userAddressAdapter.setEnderecos(addressList);
       }

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();

        if (id == R.id.nav_agendar) {
            if(systemPreferences.getUserType() != 1) {
                Intent screenAgendar = new Intent(ColetasPoinsActivity.this, AgendarActivity.class);
                startActivity(screenAgendar);
                finish();
            }else{
                Intent screenColetas = new Intent(ColetasPoinsActivity.this, ColetasActivity.class);
                startActivity(screenColetas);
                finish();
            }

        } else if (id == R.id.nav_consultar_pontos) {
            Toast.makeText(context, "Você já está na tela dos pontos de coleta.", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_consultar_historico) {
            if(systemPreferences.getUserType() != 1){

                Intent screenHistory = new Intent(ColetasPoinsActivity.this, HistoryColetasActivity.class);
                startActivity(screenHistory);
                finish();
            }else{
                Intent screenHistory = new Intent(ColetasPoinsActivity.this, AdminColetasHistoryActivity.class);
                startActivity(screenHistory);
                finish();
            }

        }else if(id == R.id.nav_sair){

            Intent loginScreen = new Intent(ColetasPoinsActivity.this, LoginActivity.class);
            systemPreferences.setAlreadyLogged(false);
            systemPreferences.removePreferences();
            startActivity(loginScreen);
            finish();

        }else if(id == R.id.nav_perfil){
            if(systemPreferences.getUserType() != 1) {
                Intent screenPerfil = new Intent(ColetasPoinsActivity.this, UpdateUserActivity.class);
                startActivity(screenPerfil);
                finish();
            }
        }else if(id == R.id.nav_menu_inicial){
            Intent screenMain = new Intent(ColetasPoinsActivity.this, MainActivity.class);
            startActivity(screenMain);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private String createJsonForDeleteAddress(int addressId){
        String json = "";

        try{

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("acao","excluirpontocoleta");
            jsonObject.put("idendereco",addressId);
            json = jsonObject.toString();

        }catch (JSONException je){

            Log.v("tag","Error on createJsonForDeleteAddress() in ColetasPoinsActivity "+je.getMessage());
        }

        return json;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent mainScreen = new Intent(ColetasPoinsActivity.this, MainActivity.class);
        startActivity(mainScreen);
        finish();

    }
    private void dialogError(Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
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

    private void dialogMessage(Context context, String[] codeAndMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View layout = getLayoutInflater().inflate(R.layout.alert_dialog_mensagem_padrao, null);

        TextView textViewMessage = layout.findViewById(R.id.TextViewMessage);
        Button buttonAgree = layout.findViewById(R.id.ButtonAgree);
        builder.setView(layout);

        String code = codeAndMessage[0];
        String message = codeAndMessage[1];

        AlertDialog alertDialog = builder.create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        textViewMessage.setText(message);

        buttonAgree.setOnClickListener((View v) -> {
                if(code.equals("400")){
                    Intent refreshScreen = new Intent(ColetasPoinsActivity.this, ColetasPoinsActivity.class);
                    startActivity(refreshScreen);
                    finish();
                }else {
                    alertDialog.dismiss();
                }
        });
        alertDialog.show();
    }
}
