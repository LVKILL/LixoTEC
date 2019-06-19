package com.example.rauber.lixotec.Fragments;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rauber.lixotec.Activities.LoginActivity;
import com.example.rauber.lixotec.Model.Endereco;
import com.example.rauber.lixotec.Model.Usuario;
import com.example.rauber.lixotec.R;
import com.example.rauber.lixotec.RetrofitAPIs.CEPAPI;
import com.example.rauber.lixotec.RetrofitAPIs.RetroClass;
import com.example.rauber.lixotec.SharedPreferences.SystemPreferences;
import com.example.rauber.lixotec.ViewModel.EnderecoViewModel;
import com.example.rauber.lixotec.ViewModel.UsuarioViewModel;
import com.example.rauber.lixotec.viacep.ViaCEP;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterAddress extends Fragment {
    private String fantasyName, socialReason, CNPJ;
    private String name, cpf, email, phone, username, password, userType;
    private String city = "";
    private SystemPreferences systemPreferences;
    private boolean isNewUser = false;
    private Context fragmentContext;
    private UsuarioViewModel usuarioViewModel;
    private EnderecoViewModel enderecoViewModel;
    private Button registerButton;
    private Context context;
    private EditText fieldCEP;
    private EditText fieldStreet;
    private EditText fieldNeighborhood;
    private EditText fieldState;
    private EditText fieldNumber;
    private EditText fieldAddressComplement;
    private static ProgressBar progressCircle;
    private RetroClass retroClass;
    private int INVALIDADDRESS;
    public RegisterAddress() {
        // Required empty public constructor
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("tag","registeraddress destruido");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_register_address, null, false);
        systemPreferences = new SystemPreferences(rootView.getContext());
        context = rootView.getContext();
        retroClass = new RetroClass();
        if(getArguments() != null){
            userType = getArguments().getString("USERTYPE");
            if(userType.equals("2")) {
                name = getArguments().getString("NAME");
                cpf = getArguments().getString("CPF");
                Log.v("tag",""+name+" "+cpf);
            }
            if(userType.equals("3")) {
                fantasyName = getArguments().getString("FANTASYNAME");
                socialReason = getArguments().getString("SOCIALREASON");
                CNPJ = getArguments().getString("CNPJ");
                Log.v("tag",""+fantasyName+" "+socialReason+" "+CNPJ);
            }
            email = getArguments().getString("EMAIL");
            phone = getArguments().getString("PHONE");
            username = getArguments().getString("USERNAME");
            password = getArguments().getString("PASSWORD");
            isNewUser = getArguments().getBoolean("ISNEWUSER");
        }else {
            userType = "4";
        }

        fragmentContext = rootView.getContext();

        usuarioViewModel = ViewModelProviders.of(this).get(UsuarioViewModel.class);
        enderecoViewModel = ViewModelProviders.of(this).get(EnderecoViewModel.class);

        registerButton = rootView.findViewById(R.id.ButtonRegister);
        progressCircle = rootView.findViewById(R.id.ProgressCircle);
        fieldCEP = rootView.findViewById(R.id.EditTextCEP);
        fieldStreet = rootView.findViewById(R.id.EditTextStreet);
        fieldNeighborhood = rootView.findViewById(R.id.EditTextNeighborhood);
        fieldState = rootView.findViewById(R.id.EditTextCityState);
        fieldNumber = rootView.findViewById(R.id.EditTextNumberr);
        fieldAddressComplement = rootView.findViewById(R.id.EditTextComplement);

        fieldCEP.setOnFocusChangeListener((View v, boolean hasFocus) -> {

                if (!hasFocus) {
                    if (!(fieldCEP.length() < 8)) {
                        blockAddressFields();
                        progressCircle.setVisibility(View.VISIBLE);
                        validateCep(fieldCEP.getText().toString().trim());
                    } else {

                    }
                }
        });

        registerButton.setOnClickListener((View v) -> {

                if (chechkFields()) {
                    confirmDataDialog(rootView.getContext());
                }else{
                    Toast.makeText(fragmentContext, "Preencha corretamente o cadastro", Toast.LENGTH_LONG).show();
                }
        });
        subscribeObserver();
        return rootView;
    }

    private boolean chechkFields(){
        boolean result;
        
        if(fieldCEP.getText().toString().equals("")
                ||fieldStreet.getText().toString().equals("")
                ||fieldNumber.getText().toString().equals("")
                ||fieldState.getText().toString().equals("")){
            result = false;
        }else{
            result = true;
        }
        return result;

    }

    private void subscribeObserver(){
        usuarioViewModel.observeRegisterResponse().observe(this, (@Nullable String s) -> {
                if(s != null){
                    Log.v("tag"," response subscriveObserrver() in RegisterAddress "+s);
                    progressCircle.setVisibility(View.INVISIBLE);
                    responseDialog(context, s);
                }
        });
    }

    public void blockAddressFields(){
        fieldState.setEnabled(false);
        fieldNeighborhood.setEnabled(false);
        fieldStreet.setEnabled(false);
    }

    public void freeAddressFields(){
        fieldState.setEnabled(true);
        fieldNeighborhood.setEnabled(true);
        fieldStreet.setEnabled(true);
    }

    public void setAddressFields(String jsonCEP){
        String street = "";
        String neighborhood = "";
        String state ="";
        try{
            Log.v("tag","json received cep "+jsonCEP);
            JSONObject jsonObject = new JSONObject(jsonCEP);
            street = jsonObject.getString("logradouro");
            neighborhood = jsonObject.getString("bairro");
            state = jsonObject.getString("uf");
            city = jsonObject.getString("localidade");
        }catch (JSONException je){
            Log.v("ErrorTAG",""+je.getMessage());
        }finally {
            if(!city.equals("Foz do Iguaçu")){
                String message = "Insira um CEP de Foz do Iguaçu";
                dialogInvalidCep(context, message);
                INVALIDADDRESS = 1;
                progressCircle.setVisibility(View.GONE);
                fieldStreet.setText("");
                fieldNeighborhood.setText("");
                fieldState.setText("");
            }else {
                progressCircle.setVisibility(View.GONE);
                fieldStreet.setText(street);
                fieldNeighborhood.setText(neighborhood);
                fieldState.setText(state);
                INVALIDADDRESS = 0;
            }
        }

    }

    public void validateCep(String cep){

        Retrofit retrofit = retroClass.getRetroClassCep();
        CEPAPI cepAPIService = retrofit.create(CEPAPI.class);
        cepAPIService.validateCEP(cep).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()){
                    setAddressFields(response.body());
                }else{
                    freeAddressFields();
                    progressCircle.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("ErrorTAG",""+t.getMessage());
                freeAddressFields();
                progressCircle.setVisibility(View.GONE);
            }
        });

    }
    public String createJsonData() {
        String result = "";
        JSONObject jsonObject = new JSONObject();
        try {
            if (userType.equals("2")) {
                jsonObject.put("username",username.toUpperCase());
                jsonObject.put("password",password.toUpperCase());
                jsonObject.put("nome", name);
                jsonObject.put("cpf",cpf);
                jsonObject.put("telefone",phone);
                jsonObject.put("email",email);
                jsonObject.put("acao","inserirpessoa");

            } else if (userType.equals("3")) {
                jsonObject.put("username",username.toUpperCase());
                jsonObject.put("password",password.toUpperCase());
                jsonObject.put("nomefantasia",fantasyName);
                jsonObject.put("razaosocial",socialReason);
                jsonObject.put("cnpj",CNPJ);
                jsonObject.put("telefone",phone);
                jsonObject.put("email",email);
                jsonObject.put("acao","inserirempresa");

            } else if (userType.equals("4")){
                jsonObject.put("idusuario",systemPreferences.getUserId());
                jsonObject.put("acao","inserirendereco");
            }

            jsonObject.put("tipousuario",userType);
            jsonObject.put("cep",fieldCEP.getText().toString().trim());
            jsonObject.put("logradouro",fieldStreet.getText().toString());
            jsonObject.put("bairro",fieldNeighborhood.getText().toString());
            jsonObject.put("cidade","fdi");
            jsonObject.put("numero",fieldNumber.getText().toString());
            jsonObject.put("complemento",fieldAddressComplement.getText());

            if(systemPreferences.getUserType() == 1){
                jsonObject.put("ispontocoleta","1");
                Log.v("tag","json object new add "+jsonObject.toString());
            }


            result = jsonObject.toString();

        }catch (JSONException je){
            Log.v("tag erro criação json",""+je.getMessage());
        }
        return result;
    }

    public void confirmDataDialog(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Confirme seu endereço");

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View layout = inflater.inflate(R.layout.alertdialog_confirm_data_person, null);

        EditText fieldCEP = layout.findViewById(R.id.EditTextConfirmCep);
        EditText fieldStreet = layout.findViewById(R.id.EditTextConfirmStreet);
        EditText fieldNeighborhood = layout.findViewById(R.id.EditTextConfirmNeighborhood);
        EditText fieldState = layout.findViewById(R.id.EditTextConfirmState);
        Button buttonAgree = layout.findViewById(R.id.ButtonAgree);
        Button buttonDisagree = layout.findViewById(R.id.ButtonDisagree);

        fieldCEP.setText(this.fieldCEP.getText().toString().trim());
        fieldStreet.setText(this.fieldStreet.getText().toString());
        fieldNeighborhood.setText(this.fieldNeighborhood.getText().toString());
        fieldState.setText(this.fieldState.getText().toString());

        builder.setView(layout);
        AlertDialog alertDialog = builder.create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        buttonAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressCircle.setVisibility(View.VISIBLE);
                if (INVALIDADDRESS == 0) {
                    String jsonData = createJsonData();
                    usuarioViewModel.registerWithJson(jsonData);
                    if (userType.equals("4")) {
                        progressCircle.setVisibility(View.VISIBLE);
                    }
                    alertDialog.dismiss();
                }else{
                    String message = "Seu CEP está incorreto.";
                    dialogInvalidCep(context, message);
                }
            }

        });

        buttonDisagree.setOnClickListener((View v) -> {
                 alertDialog.dismiss();
        });

        alertDialog.show();
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
        buttonAgree.setOnClickListener((View v) ->  {
                Intent loginScreen = new Intent(context, LoginActivity.class);
                startActivity(loginScreen);
                getActivity().finish();
        });

        alertDialog.show();


    }

    private void dialogInvalidCep(Context context, String response) {
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
                alertDialog.dismiss();

        });

        alertDialog.show();


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

}