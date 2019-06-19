package com.example.rauber.lixotec.Fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rauber.lixotec.FragmentChangeListener;
import com.example.rauber.lixotec.R;
import com.example.rauber.lixotec.ViewModel.UsuarioViewModel;
import com.github.pinball83.maskededittext.MaskedEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.InputMismatchException;

public class RegisterPerson extends Fragment {
    static String name, cpf, email, phone, username, password, confirmPassword;
    int userType;
    private UsuarioViewModel usuarioViewModel;
    TextInputLayout textInputLayoutCompleteName;
    TextInputLayout textInputLayoutCPF;
    TextInputLayout textInputLayoutEmail;
    TextInputLayout textInputLayoutPhone;
    TextInputLayout textInputLayoutUsername;
    TextInputLayout textInputLayoutPassword;
    TextInputLayout textInputLayoutConfirmPassword;
    EditText fieldName;
    MaskedEditText fieldCPF;
    EditText fieldEmail;
    MaskedEditText fieldPhone;
    EditText fieldUsername;
    EditText fieldPassword;
    EditText fieldConfirmPassword;
    private Context context;
    private int INVALIDUSERNAME = 0;
    private int INVALIDNAME = 0;
    private int INVALIDPASSWORD = 0;
    private int INVALIDEMAIL = 0;
    private int INVALIDCPF = 0;
    private int INVALIDPHONE = 0;

    public RegisterPerson() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_register_person, container, false);
        textInputLayoutCompleteName = rootView.findViewById(R.id.TextInputCompleteName);
        textInputLayoutCPF = rootView.findViewById(R.id.TextInputLayoutCPF);
        textInputLayoutEmail = rootView.findViewById(R.id.TextInputLayoutEmail);
        textInputLayoutPhone = rootView.findViewById(R.id.TextInputLayoutPhone);
        textInputLayoutUsername = rootView.findViewById(R.id.TextInputLayoutUsername);
        textInputLayoutPassword = rootView.findViewById(R.id.TextInputLayoutPassword);
        textInputLayoutConfirmPassword = rootView.findViewById(R.id.TextInputLayoutConfirmPassword);
        usuarioViewModel = ViewModelProviders.of(this).get(UsuarioViewModel.class);
        fieldName = rootView.findViewById(R.id.EditTextCompleteName);
        fieldCPF = rootView.findViewById(R.id.EditTextCPF);
        fieldEmail = rootView.findViewById(R.id.EditTextEmail);
        fieldPhone = rootView.findViewById(R.id.EditTextPhone);
        fieldUsername = rootView.findViewById(R.id.EditTextUsername);
        fieldPassword = rootView.findViewById(R.id.EditTextPassword);
        fieldConfirmPassword = rootView.findViewById(R.id.EditTextConfirmPassword);
        context = rootView.getContext();
        userType = 2;

        Button nextRegisterStep = rootView.findViewById(R.id.ButtonRegister);

        fieldUsername.setOnFocusChangeListener((View v, boolean hasFocus) -> {
            if(hasFocus){

            }else{
                usuarioViewModel.checkUsername(generateJsonCheckUsername());
            }
        });

        fieldConfirmPassword.setOnFocusChangeListener((View v, boolean hasFocus) -> {

                if(hasFocus){

                }else {
                    if(fieldConfirmPassword.getText().toString().equals("") && fieldPassword.getText().toString().equals("")){

                    }else {
                        if (validatePassword()) {
                            textInputLayoutConfirmPassword.setErrorEnabled(false);
                        } else {
                            textInputLayoutConfirmPassword.setError("As senhas não são compatíveis");
                        }
                    }

                }
        });

        fieldCPF.setOnFocusChangeListener((View v, boolean hasFocus) -> {

                if(hasFocus){

                }else{
                    if(!isCPF(fieldCPF.getUnmaskedText().toString())){
                        textInputLayoutCPF.setError("CPF Inválido");
                        fieldCPF.setRequired(true);
                    }else{
                        textInputLayoutCPF.setErrorEnabled(false);
                    }
                }
        });

        nextRegisterStep.setOnClickListener((View v) -> {

                if (validateNotNullFields()) {
                    Log.v("tag",""+validateNotNullFields());
                    Bundle userInfos = new Bundle();
                    userInfos.putString("NAME", fieldName.getText().toString().trim());
                    userInfos.putString("CPF", fieldCPF.getUnmaskedText().trim());
                    userInfos.putString("EMAIL", fieldEmail.getText().toString().trim());
                    userInfos.putString("PHONE", fieldPhone.getUnmaskedText().trim());
                    userInfos.putString("USERNAME", fieldUsername.getText().toString().trim());
                    userInfos.putString("PASSWORD", fieldPassword.getText().toString().trim());
                    userInfos.putString("USERTYPE",String.valueOf(userType));
                    userInfos.putBoolean("ISNEWUSER", true);
                    showOtherFragment(userInfos);
                }
        });
        observeCheckUsername();
        return rootView;
    }

    private void observeCheckUsername(){
        usuarioViewModel.getCheckUsername().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if ( s != null){
                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                    decodeUsernameCheckJson(s);
                }
            }
        });
    }

    private void decodeUsernameCheckJson(String json){
        try{
            JSONArray jsonArray = new JSONArray(json);

            JSONObject jsonObject = jsonArray.getJSONObject(0);;

            if(jsonObject.getString("codigo").equals("201")){
                fieldUsername.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_free_username, 0);
            }else if(jsonObject.getString("codigo").equals("200")){
                fieldUsername.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_used_userrname, 0);

            }else if(jsonObject.getString("codigo").equals("400")){

            }else{

            }

        }catch (JSONException je){
            Log.v("tag","error on decodeUsernameCheckJson() in RegisterPerson "+je.getMessage());
        }
    }

    public void showOtherFragment(Bundle bundle) {
        RegisterAddress registerAddress = new RegisterAddress();
        registerAddress.setArguments(bundle);
        FragmentChangeListener fc = (FragmentChangeListener) getActivity();
        fc.replaceFragment(registerAddress);
    }

    private String generateJsonCheckUsername() {
        String result = "";

        try{

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("tipo_consulta","consultarusername");
            jsonObject.put("username",fieldUsername.getText().toString().trim());
            result = jsonObject.toString();

        }catch (JSONException je){
            Log.v("tag","Error in generateJsoCheckUsername "+je.getMessage());
        }

        return result;
    }

    public boolean validatePassword() {
        String password = fieldPassword.getText().toString().toUpperCase().trim();
        String confirmPassword = fieldConfirmPassword.getText().toString().toUpperCase().trim();
        if (!(password.equals("") || confirmPassword.equals(""))) {
            return password.equals(confirmPassword);
        } else {
            return false;
        }
    }

    private void validateFields(){
        if(fieldName.getText().toString().equals(""))
            INVALIDNAME = 1;
        if(fieldUsername.getText().toString().equals(""))
            INVALIDUSERNAME = 1;
        if(fieldPassword.getText().toString().equals(fieldConfirmPassword.getText().toString())){
            if(fieldPassword.getText().toString().equals(""))
                INVALIDPASSWORD = 1;
        }
        if(fieldEmail.getText().toString().equals("")){
            INVALIDEMAIL = 1;
        }else{
            if(!fieldEmail.getText().toString().contains("@"))
                INVALIDEMAIL = 1;
        }
        if(!isCPF(fieldCPF.getText().toString()))
            INVALIDCPF = 1;
        
        if(fieldPhone.getUnmaskedText().toString().equals(""))
            INVALIDPHONE = 1;

    }

    public boolean validateNotNullFields() {
        String invalidFields = "";
        if (fieldName.getText().toString().trim().equals(""))
            invalidFields += "name;";
        if (fieldCPF.getText().toString().trim().equals(""))
            invalidFields += "cpf;";
        if (fieldEmail.getText().toString().trim().equals(""))
            invalidFields += "email;";
        if (fieldPhone.getText().toString().trim().equals(""))
            invalidFields += "phone;";
        if (fieldUsername.getText().toString().trim().equals(""))
            invalidFields += "username;";
        if (fieldPassword.getText().length() < 4) {
            invalidFields += "password;";
        }
        String[] invalidFieldsArray = invalidFields.split(";");
        if (invalidFieldsArray.length > 1) {
            for (int i = 0; i < invalidFieldsArray.length; i++) {
                switch (invalidFieldsArray[i]) {
                    case "name":
                        textInputLayoutCompleteName.setError("Seu nome não pode ser vazio.");
                        break;
                    case "cpf":
                        textInputLayoutCPF.setError("Seu CPF está inválido.");
                        break;
                    case "email":
                        textInputLayoutEmail.setError("Seu email não pode ser vazio.");
                        break;
                    case "phone":
                        textInputLayoutPhone.setError("Seu número de celular não pode ser vazio.");
                        break;
                    case "username":
                        textInputLayoutUsername.setError("Seu nome de acesso não pode ser vazio.");
                        break;
                    case "password":
                        textInputLayoutPassword.setError("Sua senha deve possuir ao menos 4 caracteres");
                        break;
                }
            }
        }
        return invalidFields.equals("");
    }

    public static boolean isCPF(String CPF) {
        // considera-se erro CPF's formados por uma sequencia de numeros iguais
        if (CPF.equals("00000000000") ||
                CPF.equals("11111111111") ||
                CPF.equals("22222222222") || CPF.equals("33333333333") ||
                CPF.equals("44444444444") || CPF.equals("55555555555") ||
                CPF.equals("66666666666") || CPF.equals("77777777777") ||
                CPF.equals("88888888888") || CPF.equals("99999999999") ||
                (CPF.length() != 11))
            return(false);

        char dig10, dig11;
        int sm, i, r, num, peso;

        // "try" - protege o codigo para eventuais erros de conversao de tipo (int)
        try {
            // Calculo do 1o. Digito Verificador
            sm = 0;
            peso = 10;
            for (i=0; i<9; i++) {
                // converte o i-esimo caractere do CPF em um numero:
                // por exemplo, transforma o caractere '0' no inteiro 0
                // (48 eh a posicao de '0' na tabela ASCII)
                num = (int)(CPF.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11))
                dig10 = '0';
            else dig10 = (char)(r + 48); // converte no respectivo caractere numerico

            // Calculo do 2o. Digito Verificador
            sm = 0;
            peso = 11;
            for(i=0; i<10; i++) {
                num = (int)(CPF.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11))
                dig11 = '0';
            else dig11 = (char)(r + 48);

            // Verifica se os digitos calculados conferem com os digitos informados.
            if ((dig10 == CPF.charAt(9)) && (dig11 == CPF.charAt(10)))
                return(true);
            else return(false);
        } catch (InputMismatchException erro) {
            return(false);
        }
    }

}
