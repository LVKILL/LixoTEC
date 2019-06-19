package com.example.rauber.lixotec.Activities;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.rauber.lixotec.R;
import com.example.rauber.lixotec.ViewModel.ColetaViewModel;
import com.google.gson.JsonObject;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView textViewDateHour;
    private TextView textViewName;
    private TextView textViewLocation;
    private TextView textViewComplement;
    private Button buttonOpenMap;
    private Button buttonMarkAsDone;
    private ImageView imageViewColeta;
    private String dateHour;
    private String name;
    private String location;
    private String locationForIntent;
    private String complement;
    private String imagePath;
    private int coletaId;
    private ColetaViewModel coletaViewModel;
    private Context context;
    private ProgressBar progressCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        textViewDateHour = findViewById(R.id.TextViewDateHour);
        textViewName = findViewById(R.id.TextViewName);
        textViewLocation = findViewById(R.id.TextViewLocation);
        textViewComplement = findViewById(R.id.TextViewComplemento);
        imageViewColeta = findViewById(R.id.ImageViewColeta);
        buttonOpenMap = findViewById(R.id.ButtonOpenMap);
        buttonMarkAsDone = findViewById(R.id.ButtonMarkAsDone);
        coletaViewModel = ViewModelProviders.of(this).get(ColetaViewModel.class);
        buttonMarkAsDone.setOnClickListener(this);
        buttonOpenMap.setOnClickListener(this);
        context = this;
        progressCircle = findViewById(R.id.ProgressCircle);
        receiveDataFromIntent();

        if(imagePath != null){
            if(!imagePath.equals("")){
                progressCircle.setVisibility(View.VISIBLE);
                String urlImage = "https://ambientesvirtuais.com/lixotec/upload/"+imagePath;
                Log.v("tag","url = "+urlImage);

                Picasso.with(this).setLoggingEnabled(true);
                Picasso.with(this)
                        .load(urlImage)
                        .fit()
                        .centerCrop()
                        .into(imageViewColeta, new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.v("tag","Success on requesting image!");
                                progressCircle.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onError() {
                                Log.v("tag","Error on requesting image!");
                                progressCircle.setVisibility(View.INVISIBLE);
                            }
                        });

            }
        }

        observePerformColetaResponse();
    }

    private void observePerformColetaResponse(){
        coletaViewModel.getPerformColetaResponse().observe(this, (@Nullable String s) -> {
                if(s != null){
                    convertResponse(s);
                }

        });
    }

    private void convertResponse(String s) {
        String message = "";

        try{
            JSONArray jsonArray = new JSONArray(s);
            for(int i = 0 ; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String mensagem = jsonObject.getString("mensagem");
                message = mensagem;
            }


        }catch (JSONException je){
            Log.v("tag","error n convertResponse() in DetailsActivity "+je);
        }finally{
            progressCircle.setVisibility(View.INVISIBLE);
            responseDialog(context, message);
        }

    }

    private void receiveDataFromIntent() {
        if(getIntent().getExtras() != null){
            dateHour = getIntent().getStringExtra("DATEHOUR");
            name = getIntent().getStringExtra("NAME");
            location = getIntent().getStringExtra("ADDRESSTOSCREEN");
            locationForIntent = getIntent().getStringExtra("ADDRESSTOINTENT");
            complement = getIntent().getStringExtra("COMPLEMENT");
            imagePath = getIntent().getStringExtra("IMAGEPATH");
            coletaId = getIntent().getExtras().getInt("IDCOLETA");
            bindData();
        }


    }

    private void bindData() {
        textViewDateHour.setText(dateHour);
        textViewName.setText(name);
        textViewLocation.setText(location);
        textViewComplement.setText(complement);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.ButtonOpenMap:
                String geo = "geo:0,0?q="+locationForIntent;
                Uri geoUri = Uri.parse(geo);
                Intent intent = new Intent(Intent.ACTION_VIEW, geoUri);
                intent.setPackage( "com.google.android.apps.maps" );
                startActivity(intent);
                break;
            case R.id.ButtonMarkAsDone:
                String jsonMarkColetaAsDone = generateJsonPerfomColeta();
                if(!jsonMarkColetaAsDone.equals("")){
                    progressCircle.setVisibility(View.VISIBLE);
                    coletaViewModel.performColeta(jsonMarkColetaAsDone);
                }
                break;

        }
    }

    private void responseDialog(Context context, String response) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View layout = getLayoutInflater().inflate(R.layout.alert_dialog_mensagem_padrao, null);

        TextView textViewMessage = layout.findViewById(R.id.TextViewMessage);
        Button buttonAgree = layout.findViewById(R.id.ButtonAgree);
        builder.setView(layout);
        AlertDialog alertDialog = builder.create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        textViewMessage.setText(response);
        buttonAgree.setOnClickListener((View v) -> {
                Intent mainScreen = new Intent(context, MainActivity.class);
                startActivity(mainScreen);
                finish();
        });

        alertDialog.show();


    }

    private String generateJsonPerfomColeta(){
        String result = "";

        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("acao", "marcarrealizada");
            jsonObject.put("idcoleta", coletaId);
            result = jsonObject.toString();

        }catch (JSONException je){
            Log.v("tag","Error on generateJsonPerformColetA() in DetailsActivity "+je.getMessage());
        }
        return result;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent coletasScreen = new Intent(DetailsActivity.this, ColetasActivity.class);
        startActivity(coletasScreen);
        finish();
    }

    public void loadPic(String path){

    }
}
