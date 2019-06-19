package com.example.rauber.lixotec.Activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.rauber.lixotec.Model.Empresa;
import com.example.rauber.lixotec.Model.Pessoa;
import com.example.rauber.lixotec.Model.Usuario;
import com.example.rauber.lixotec.R;
import com.example.rauber.lixotec.SharedPreferences.SystemPreferences;
import com.example.rauber.lixotec.ViewModel.UsuarioViewModel;
import com.github.pinball83.maskededittext.MaskedEditText;

import org.json.JSONException;
import org.json.JSONObject;

public class UpdateUserActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextCompleteName;
    private MaskedEditText editTextCPF;
    private EditText editTextEmail;
    private MaskedEditText editTextPhone;
    private MaskedEditText editTextCnpj;
    private Button buttonUpdate;
    private SystemPreferences systemPreferences;
    private UsuarioViewModel usuarioViewModel;
    private String standardEmail;
    private String standardPhone;
    private Usuario masterUser;
    private ProgressBar progressCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_update_infos);

        editTextCompleteName = findViewById(R.id.EditTextCompleteName);
        editTextCPF = findViewById(R.id.EditTextCPF);
        editTextCnpj = findViewById(R.id.EditTextCNPJ);
        editTextEmail = findViewById(R.id.EditTextEmail);
        editTextPhone = findViewById(R.id.EditTextNumber);
        buttonUpdate = findViewById(R.id.ButtonUpdate);
        buttonUpdate.setOnClickListener(this);
        progressCircle = findViewById(R.id.ProgressCircle);
        progressCircle.setVisibility(View.VISIBLE);


        systemPreferences = new SystemPreferences(this);

        if(systemPreferences.getUserType() != 1) {

            usuarioViewModel = ViewModelProviders.of(this).get(UsuarioViewModel.class);
            usuarioViewModel.loadUser(systemPreferences.getUserId());

            if(systemPreferences.getUserType() == 2){
                editTextCPF.setVisibility(View.VISIBLE);
                usuarioViewModel.loadUserPerson(systemPreferences.getUserId());
                observePerson();
            }else{
                editTextCnpj.setVisibility(View.VISIBLE);
                usuarioViewModel.loadCompany(systemPreferences.getUserId());
                observeCompany();
            }
            observeUser();
        }
    }

    private void observePerson(){
        usuarioViewModel.getUserPessoa().observe(this, (@Nullable Pessoa pessoa) -> {

                if(pessoa != null){
                    editTextCompleteName.setText(pessoa.getNomeCompleto());
                    editTextCompleteName.setEnabled(false);
                    editTextCPF.setMaskedText(pessoa.getCPF());
                    editTextCPF.setEnabled(false);
                }
        });
    }

    private void observeCompany(){
        usuarioViewModel.getCompany().observe(this, (@Nullable Empresa empresa) -> {

                if(empresa != null){
                    editTextCompleteName.setText(empresa.getNomeFantasia());
                    editTextCompleteName.setEnabled(false);
                    editTextCnpj.setMaskedText(empresa.getCNPJ());
                    editTextCnpj.setEnabled(false);
                }
        });
    }

    private void observeAdmin(){

    }

    private void observeUser(){
        usuarioViewModel.getUser().observe(this, (@Nullable Usuario usuario) -> {
                if(usuario != null){
                    progressCircle.setVisibility(View.INVISIBLE);
                    masterUser = usuario;
                    editTextEmail.setText(usuario.getEmail());
                    standardEmail = usuario.getEmail();

                    editTextPhone.setMaskedText(usuario.getPhone());
                    standardPhone = usuario.getPhone();
                }
        });
    }

    private boolean verifyChanges(){
        String email = editTextEmail.getText().toString();
        String phone = editTextPhone.getUnmaskedText();
        Log.v("tag",""+phone);
        if( !email.equals(standardEmail) || !phone.equals(standardPhone)){
            return true;
        }else {
            return false;
        }
    }

    private void createAndSubmitUpdateJson(){
        String jsonUpdate = "";

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("idusuario",systemPreferences.getUserId());
            jsonObject.put("telefone",masterUser.getPhone());
            jsonObject.put("email",masterUser.getEmail());
            jsonObject.put("acao","alterardadosusuario");
            jsonUpdate = jsonObject.toString();
        }catch(JSONException je){
            Log.v("tag", "error on createAndSubmitUpdateJson() "+je.getMessage());
        }
        if(!jsonUpdate.equals(""))
            usuarioViewModel.updateUserOnWebservice(jsonUpdate);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ButtonUpdate:
                if(verifyChanges()){
                    String changedMail = editTextEmail.getText().toString();
                    String changedPhone = editTextPhone.getUnmaskedText();
                    masterUser.setEmail(changedMail);
                    masterUser.setPhone(changedPhone);
                    usuarioViewModel.updateUser(masterUser);
                    createAndSubmitUpdateJson();
                    Intent mainScreen = new Intent(UpdateUserActivity.this, MainActivity.class);
                    startActivity(mainScreen);
                    finish();
                }else{
                    Intent mainScreen = new Intent(UpdateUserActivity.this, MainActivity.class);
                    startActivity(mainScreen);
                    finish();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent mainScreen = new Intent(UpdateUserActivity.this, MainActivity.class);
        startActivity(mainScreen);
        finish();
    }
}
