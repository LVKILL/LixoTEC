package com.example.rauber.lixotec.Activities;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rauber.lixotec.R;
import com.example.rauber.lixotec.SharedPreferences.SystemPreferences;
import com.example.rauber.lixotec.ViewModel.UsuarioViewModel;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextPassword;
    private ProgressBar progressCircle;
    private SystemPreferences systemPreferences;
    private UsuarioViewModel usuarioViewModel;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        systemPreferences = new SystemPreferences(this);
        usuarioViewModel = ViewModelProviders.of(this).get(UsuarioViewModel.class);
        context = this;

        if(!systemPreferences.getAlreadyLogged()) {
            Button logInButton = findViewById(R.id.ButtonLogIn);
            editTextUsername = findViewById(R.id.EditTextUsername);
            editTextPassword = findViewById(R.id.EditTextPassword);
            progressCircle = findViewById(R.id.ProgressCircle);
            TextView preRegisterTextView = findViewById(R.id.TextViewRegister);


            preRegisterTextView.setOnClickListener((View v) -> {
                    Intent preRegisterScreen = new Intent(LoginActivity.this, PreRegistrerActivity.class);
                    startActivity(preRegisterScreen);
                    finish();

            });

            logInButton.setOnClickListener((View v) -> {
                    progressCircle.setVisibility(View.VISIBLE);
                    attemptLogin();
            });

        }else{
            Intent mainScreen = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(mainScreen);
            finish();
        }

        subscribeObservers();
    }

    private void subscribeObservers(){
        usuarioViewModel.observeLogin().observe(this, (String s) -> {
                Log.v("tag","vallue of string S subscriveObservers() "+s.toString());
                if(s != null){
                    if(systemPreferences.getUserId() > 0){
                        progressCircle.setVisibility(View.GONE);
                        Intent mainScreen = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(mainScreen);
                        finish();
                    }else{
                        getMessageFromjson(s);
                    }
                }else{
                    progressCircle.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, "Ocorreu um erro ao realizar login, verifique suas credenciais!", Toast.LENGTH_SHORT).show();
                }
        });
    }

    private void getMessageFromjson(String s) {
        String messageReturned = "";

        try{
            Log.v("tag","getMessageFromJson json = "+s);
            JSONArray jsonArray = new JSONArray(s);

            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String message = jsonObject.getString("mensagem");
                messageReturned = message;

            }


        }catch (JSONException je){
            Log.v("tag","Error on getMessageFromJson in LoginActivity "+je.getMessage());
        }finally{
            progressCircle.setVisibility(View.GONE);
            dialogMessage(context, messageReturned);
        }

    }

    private void dialogMessage(Context context, String message) {
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

    private void attemptLogin(){
        if (editTextUsername.getText().toString().trim().equals("") ||
                editTextPassword.getText().toString().trim().equals("")) {
            progressCircle.setVisibility(View.GONE);
            Toast.makeText(LoginActivity.this, "Usuario e/ou senha inválidos", Toast.LENGTH_SHORT).show();

        } else {
            usuarioViewModel.authenticateWithJson(createLoginJson());
            }
    }

    private String createLoginJson(){
          String result = "";
          try{
              JSONObject jsonObject = new JSONObject();
              jsonObject.put("username",editTextUsername.getText().toString().trim().toUpperCase());
              jsonObject.put("password",editTextPassword.getText().toString().trim().toUpperCase());
              result = jsonObject.toString();
          }catch (JSONException je){
              Log.v("TAG erro JSON",""+je);
          }

          return result;
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
