package com.example.rauber.lixotec.Activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rauber.lixotec.Adapters.ColetasAdapter;
import com.example.rauber.lixotec.Adapters.UserAddressAdapter;
import com.example.rauber.lixotec.Model.Coleta;
import com.example.rauber.lixotec.Model.Endereco;
import com.example.rauber.lixotec.R;
import com.example.rauber.lixotec.ViewModel.ColetaViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ColetasActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView recyclerView;
    private ColetasAdapter coletasAdapter;
    private ProgressBar progressCircle;
    private TextView textViewLabelColetas;
    private TextView textViewScreenLabel;
    private Button buttonToday;
    private Button buttonTomorrow;
    private Button buttonAnotherDay;
    private int selectedDay;
    private int selectedMonth;
    private int selectedYear;
    private ColetaViewModel coletaViewModel;
    private DatePicker datePicker;
    private List<Coleta> coletas;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coletas);
        datePicker = new DatePicker(this);
        buttonToday = findViewById(R.id.ButtonToday);
        buttonTomorrow = findViewById(R.id.ButtonTomorrow);
        buttonAnotherDay = findViewById(R.id.ButtonAnotherDay);
        textViewScreenLabel = findViewById(R.id.TextViewSelectDateTime);
        context = this;
        recyclerView = findViewById(R.id.RecyclerViewColetas);
        textViewLabelColetas = findViewById(R.id.TextViewLabelColetas);
        coletaViewModel = ViewModelProviders.of(this).get(ColetaViewModel.class);
        buttonToday.setOnClickListener(this);
        buttonTomorrow.setOnClickListener(this);
        buttonAnotherDay.setOnClickListener(this);
        coletasAdapter = new ColetasAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        progressCircle = findViewById(R.id.ProgressCircle);
        progressCircle.setVisibility(View.INVISIBLE);

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if(direction == ItemTouchHelper.RIGHT){

                    Coleta coleta = coletasAdapter.getColetas(viewHolder.getAdapterPosition());

                    Endereco endereco = coleta.getEndereco();
                    String superAddress = endereco.getLogradouro() + ", " + endereco.getNumero() + " - " + endereco.getBairro() + ", " + endereco.getCEP();
                    String addressForIntent = endereco.getLogradouro() + ", "+ endereco.getNumero() + " - " + endereco.getBairro();

                    Intent detailScreen = new Intent(ColetasActivity.this, DetailsActivity.class);
                    detailScreen.putExtra("DATEHOUR", coleta.getDataHora());
                    detailScreen.putExtra("NAME",coleta.getNome());
                    detailScreen.putExtra("ADDRESSTOSCREEN",superAddress);
                    detailScreen.putExtra("ADDRESSTOINTENT",addressForIntent);
                    detailScreen.putExtra("COMPLEMENT", verificaComplemento(endereco.getComplemento()));
                    detailScreen.putExtra("IMAGEPATH",coleta.getColetaImagem());
                    detailScreen.putExtra("IDCOLETA", coleta.getIdColeta());
                    startActivity(detailScreen);
                    finish();

                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        getColetaByDay();
    }

    private String verificaComplemento(String complemento) {
        if(complemento.equals("")){
            return "";
        }else{
            return complemento;
        }
    }

    private void getColetaByDay(){
        coletaViewModel.getColetaByDay().observe(this, (@Nullable String s)-> {
                if( s != null ){
                    Log.v("tag","received json in getColetaByDay() "+s);
                    convertJsonResponse(s);
                }
        });
    }

    private void convertJsonResponse(String json){

        coletas = new ArrayList<>();
        try{

            JSONArray jsonArray = new JSONArray(json);

            if(!jsonArray.getJSONObject(0).getString("codigo").equals("600")) {

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String codigo = jsonObject.getString("codigo");
                    int quantidade = Integer.parseInt(jsonObject.getString("quantidade"));

                    if (codigo.equals("200") && quantidade > 0) {

                        String nome = jsonObject.getString("nome");
                        int idColeta = Integer.parseInt(jsonObject.getString("id_coleta"));
                        int idUsuario = Integer.parseInt(jsonObject.getString("id_coleta"));
                        int idEndereco = Integer.parseInt(jsonObject.getString("id_endereco"));
                        String dataHora = jsonObject.getString("data_hora");
                        String imageName = jsonObject.getString("imagem");
                        boolean realizada = (Integer.parseInt(jsonObject.getString("realizada")) == 1);
                        String logradouro = jsonObject.getString("logradouro");
                        String bairro = jsonObject.getString("bairro");
                        String cep = jsonObject.getString("cep");
                        String numero = jsonObject.getString("numero");
                        Coleta coleta = new Coleta(idColeta, idUsuario, dataHora, realizada, imageName);
                        boolean isPontoColeta = (Integer.parseInt(jsonObject.getString("is_ponto_coleta")) == 1);
                        String complemento = jsonObject.getString("complemento");
                        coleta.setEndereco(new Endereco(idEndereco, idUsuario, logradouro, numero, cep, bairro, isPontoColeta, complemento));
                        coleta.setNome(nome);
                        coletas.add(coleta);
                        if (textViewLabelColetas.getVisibility() == View.INVISIBLE)
                            textViewLabelColetas.setVisibility(View.VISIBLE);
                        String coletasQuantityResult = quantidade + " " + getResources().getString(R.string.text_view_label_coletas);
                        textViewLabelColetas.setText(coletasQuantityResult);

                    } else if (codigo.equals("200") && quantidade == 0) {

                        if (textViewLabelColetas.getVisibility() == View.INVISIBLE)
                            textViewLabelColetas.setVisibility(View.VISIBLE);
                        textViewLabelColetas.setText(getResources().getString(R.string.text_view_label_coletas_empty));
                    }

                }
            }else{
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                dialogError(context, jsonObject.getString("mensagem"));
            }
        }catch(JSONException je){
            Log.v("TAG","Error on convertJsonResponse in ColetasActivity "+je.getMessage());
        }finally{
            if(coletas.size() > 0) {
                coletasAdapter.setColetas(coletas);
                recyclerView.setAdapter(coletasAdapter);
                recyclerView.setVisibility(View.VISIBLE);
            }
            if(progressCircle.getVisibility() == View.VISIBLE){
                progressCircle.setVisibility(View.INVISIBLE);
            }
        }

    }

    private void dialogError(Context context, String message){
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

    private void searchColetaByDay(String json){
        coletaViewModel.searchColetaByDay(json);
    }

    private String generateSearchJson(String data){
        String result = "";
        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("acao","coletasDiaEspecifico");
            jsonObject.put("data",data);

            result = jsonObject.toString();

        }catch(JSONException je){
            Log.v("TAG","error on generateSearchJson() in ColetasActivity "+je.getMessage());
        }

        return result;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent mainScreen = new Intent(ColetasActivity.this, MainActivity.class);
        startActivity(mainScreen);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.ButtonToday:
                if(progressCircle.getVisibility() == View.INVISIBLE){
                    progressCircle.setVisibility(View.VISIBLE);
                }
                String dataToday = datePicker.getYear() + "-" + (datePicker.getMonth()+1) + "-" + datePicker.getDayOfMonth();
                searchColetaByDay(generateSearchJson(dataToday));
                break;

            case R.id.ButtonTomorrow:
                if(progressCircle.getVisibility() == View.INVISIBLE){
                    progressCircle.setVisibility(View.VISIBLE);
                }
                String dataTomorrow = datePicker.getYear() + "-" + (datePicker.getMonth()+1) + "-" + (datePicker.getDayOfMonth() + 1);
                searchColetaByDay(generateSearchJson(dataTomorrow));
                break;

            case R.id.ButtonAnotherDay:
                if(progressCircle.getVisibility() == View.INVISIBLE){
                    progressCircle.setVisibility(View.VISIBLE);
                }
                dialogData(this);

                break;

        }
    }

    private void dialogData(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = getLayoutInflater().inflate(R.layout.dialog_date, null);
        final DatePicker datePicker1;
        datePicker1 = (DatePicker) view.findViewById(R.id.datePicker);
        builder.setView(view);

        builder.setPositiveButton("OK",(DialogInterface dialog, int which) -> {
                selectedDay = datePicker1.getDayOfMonth();
                selectedMonth = datePicker1.getMonth()+1;
                selectedYear = datePicker1.getYear();
                String fulldate = selectedYear + "-" + selectedMonth + "-" + selectedDay;
                searchColetaByDay(generateSearchJson(fulldate));
        });

        builder.show();
    }
}
