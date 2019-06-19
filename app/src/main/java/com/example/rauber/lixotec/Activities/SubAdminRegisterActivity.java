package com.example.rauber.lixotec.Activities;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
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
import com.example.rauber.lixotec.ViewModel.UsuarioViewModel;
import com.github.pinball83.maskededittext.MaskedEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SubAdminRegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextUsername;
    private EditText editTextPassword;
    private EditText editTextEmail;
    private MaskedEditText editTextPhone;
    private ProgressBar progressCircle;
    private Button buttonRegister;
    private UsuarioViewModel usuarioViewModel;
    private int AVAIABLEUSERNAME;
    private Context context;
    private static final int MINFIELDSIZE = 5;
    private int AVAIABLEPASSWORD;
    private int AVAIABLEEMAIL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_admin_register);
        editTextUsername = findViewById(R.id.EditTextUsername);
        editTextPassword = findViewById(R.id.EditTextPassword);
        editTextEmail = findViewById(R.id.EditTextEmail);
        editTextPhone = findViewById(R.id.EditTextNumber);
        progressCircle = findViewById(R.id.ProgressCircle);
        buttonRegister = findViewById(R.id.ButtonRegister);
        buttonRegister.setOnClickListener(this);
        usuarioViewModel = ViewModelProviders.of(this).get(UsuarioViewModel.class);
        context = this;

        editTextUsername.setOnFocusChangeListener((View v, boolean hasFocus) -> {
                if(!hasFocus){
                    progressCircle.setVisibility(View.VISIBLE);
                    String json = generateJsonCheckUsername();
                    if(!json.equals("")){
                        usuarioViewModel.checkUsername(json);

                    }else{
                        progressCircle.setVisibility(View.INVISIBLE);
                        Toast.makeText(SubAdminRegisterActivity.this, "Ocorreu um erro", Toast.LENGTH_LONG).show();
                    }
                }
        });

        editTextPassword.setOnFocusChangeListener((View v, boolean hasFocus) -> {

                if(!hasFocus){
                   if(editTextPassword.getText().length() < MINFIELDSIZE) {
                       Toast.makeText(context, "A senha deve possuir 5 ou mais caracteres", Toast.LENGTH_LONG).show();
                        AVAIABLEPASSWORD = 0;
                   }else{
                       AVAIABLEPASSWORD = 1;
                   }
                }
        });

        editTextEmail.setOnFocusChangeListener((View v, boolean hasFocus) -> {
                if(!hasFocus){
                    if(!editTextEmail.getText().toString().contains("@")){
                        Toast.makeText(context, "O email está inválido", Toast.LENGTH_LONG).show();
                        AVAIABLEEMAIL = 0;
                    }else{
                        AVAIABLEEMAIL = 1;
                    }
                }
        });


        observeCheckUsername();
        observeRegisterResponse();
    }

    private void observeRegisterResponse(){
        usuarioViewModel.observeRegisterResponse().observe(this, (@Nullable String s) -> {
                if ( s != null){
                    String[] response = new String[1];
                    response[0] = s;
                    secondDialogMessage(context, response);
                    progressCircle.setVisibility(View.INVISIBLE);

                }
        });
    }

    private String generateJsonCheckUsername() {

        String result = "";

        try{

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("tipo_consulta","consultarusername");
            jsonObject.put("username",editTextUsername.getText().toString().trim());
            result = jsonObject.toString();
        }catch (JSONException je){
            Log.v("tag","Error in generateJsoCheckUsername "+je.getMessage());
        }

        return result;
    }

    private void observeCheckUsername(){
        usuarioViewModel.getCheckUsername().observe(this, (@Nullable String s) -> {
                decodeJsonCheckUsername(s);
        });
    }

    private void decodeJsonCheckUsername(String json) {
        String[] jsonResponseAsArray = new String[2];
        try{
            JSONArray jsonArray = new JSONArray(json);
            for(int i = 0 ; i < jsonArray.length() ; i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                jsonResponseAsArray[0] = jsonObject.getString("codigo");
                jsonResponseAsArray[1] = jsonObject.getString("mensagem");
            }
        }catch (JSONException je){
            Log.v("tag","Error on decodeJsonCheckUsername() in SubAdminRegisterActivity "+je.getMessage());
        }finally {
            progressCircle.setVisibility(View.GONE);
            if(jsonResponseAsArray[0].equals("201"))
                AVAIABLEUSERNAME = 1;
            else if(jsonResponseAsArray[0].equals("600"))
                dialogError(context, jsonResponseAsArray[1]);
            else
                AVAIABLEUSERNAME = 0;
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

    private void dialogMessage(Context context, String[] codeAndMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View layout = getLayoutInflater().inflate(R.layout.alert_dialog_mensagem_padrao, null);

        TextView textViewMessage = layout.findViewById(R.id.TextViewMessage);
        Button buttonAgree = layout.findViewById(R.id.ButtonAgree);
          builder.setView(layout);
        AlertDialog alertDialog = builder.create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        textViewMessage.setText(codeAndMessage[0]);
        buttonAgree.setOnClickListener((View v) -> {
                createJsonInsertSubAdmin();
                progressCircle.setVisibility(View.VISIBLE);
                alertDialog.dismiss();
        });

        alertDialog.show();


    }

    private void secondDialogMessage(Context context, String[] message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View layout = getLayoutInflater().inflate(R.layout.alert_dialog_mensagem_padrao, null);

        TextView textViewMessage = layout.findViewById(R.id.TextViewMessage);
        Button buttonAgree = layout.findViewById(R.id.ButtonAgree);
        builder.setView(layout);
        AlertDialog alertDialog = builder.create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        textViewMessage.setText(message[0]);
        buttonAgree.setOnClickListener((View v) -> {
                Intent mainScreen = new Intent(SubAdminRegisterActivity.this, MainActivity.class);
                startActivity(mainScreen);
                alertDialog.dismiss();
                finish();
        });

        alertDialog.show();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ButtonRegister:
                if(verifyDataFields()){
                    String[] codeAndMessage = new String[1];
                    codeAndMessage[0] = "Deseja proseguir com o cadastro de Sub Administrador?";
                    dialogMessage(context, codeAndMessage);
                }
                break;
        }
    }

    private boolean verifyDataFields() {
        String invalidFields = "";
        if(AVAIABLEUSERNAME == 0){
            invalidFields = "Username inválido,";
        }
        if(AVAIABLEPASSWORD == 0){
            invalidFields += " Senha inválida,";
        }
        if(AVAIABLEEMAIL == 0){
            invalidFields += " Email inválido";
        }

        if(invalidFields.equals("")){
            return true;
        }else{
            Toast.makeText(context, invalidFields, Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private void createJsonInsertSubAdmin(){
        String json = "";
        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("tipousuario", "1");
            jsonObject.put("acao","inserirsubadm");
            jsonObject.put("login",editTextUsername.getText().toString().trim());
            jsonObject.put("senha",editTextPassword.getText().toString().trim());
            jsonObject.put("email",editTextEmail.getText().toString().trim());
            jsonObject.put("telefone",editTextPhone.getUnmaskedText().trim());
            jsonObject.put("isSubAdmin","1");
            json = jsonObject.toString();
        }catch (JSONException je){
            Log.v("TAG","Error in createJsonInsertSubAdmin() on SubAdminRegisterActivity " + je.getMessage());
        }finally {
            usuarioViewModel.registerWithJson(json);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent mainScreen = new Intent(SubAdminRegisterActivity.this, MainActivity.class);
        startActivity(mainScreen);
        finish();
    }
}
