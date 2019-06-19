package com.example.rauber.lixotec.Fragments;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.rauber.lixotec.FragmentChangeListener;
import com.example.rauber.lixotec.R;
import com.github.pinball83.maskededittext.MaskedEditText;

public class RegisterCompany extends Fragment {

    int userType;
    TextInputLayout textInputLayoutFantasyName;
    TextInputLayout textInputLayoutSocialReason;
    TextInputLayout textInputLayoutCNPJ;
    TextInputLayout textInputLayoutEmail;
    TextInputLayout textInputLayoutPhone;
    TextInputLayout textInputLayoutUsername;
    TextInputLayout textInputLayoutPassword;
    TextInputLayout textInputLayoutConfirmPassword;
    EditText fieldFantasyName;
    EditText fieldSocialReason;
    MaskedEditText fieldCNPJ;
    EditText fieldEmail;
    MaskedEditText fieldPhone;
    EditText fieldUsername;
    EditText fieldPassword;
    EditText fieldConfirmPassword;

    public RegisterCompany() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_register_company, container, false);
        Button nextRegisterStep = rootView.findViewById(R.id.ButtonRegister);
        textInputLayoutFantasyName = rootView.findViewById(R.id.TextInputLayoutFantasyName);
        textInputLayoutSocialReason = rootView.findViewById(R.id.TextInputLayoutSocialReason);
        textInputLayoutCNPJ = rootView.findViewById(R.id.TextInputLayoutCNPJ);
        textInputLayoutEmail = rootView.findViewById(R.id.TextInputLayoutEmail);
        textInputLayoutPhone = rootView.findViewById(R.id.TextInputLayoutUsername);
        textInputLayoutUsername = rootView.findViewById(R.id.TextInputLayoutUsername);
        textInputLayoutPassword = rootView.findViewById(R.id.TextInputLayoutPassword);
        textInputLayoutConfirmPassword = rootView.findViewById(R.id.TextInputLayoutConfirmPassword);

        fieldFantasyName = rootView.findViewById(R.id.EditTextFantasyName);
        fieldSocialReason = rootView.findViewById(R.id.EditTextSocialReason);
        fieldCNPJ = rootView.findViewById(R.id.EditTextCNPJ);
        fieldEmail = rootView.findViewById(R.id.EditTextEmail);
        fieldPhone = rootView.findViewById(R.id.EditTextPhone);
        fieldUsername = rootView.findViewById(R.id.EditTextUsername);
        fieldPassword = rootView.findViewById(R.id.EditTextPassword);
        fieldConfirmPassword = rootView.findViewById(R.id.EditTextConfirmPassword);

        userType = 3;

        nextRegisterStep.setOnClickListener((View v) -> {

                if (validateNotNullFields()) {
                    Log.v("tag",""+validateNotNullFields());
                    Bundle userInfos = new Bundle();
                    userInfos.putString("FANTASYNAME", fieldFantasyName.getText().toString());
                    userInfos.putString("SOCIALREASON", fieldSocialReason.getText().toString());
                    userInfos.putString("CNPJ", fieldCNPJ.getUnmaskedText().trim());
                    userInfos.putString("EMAIL", fieldEmail.getText().toString().trim());
                    userInfos.putString("PHONE", fieldPhone.getUnmaskedText().trim());
                    userInfos.putString("USERNAME", fieldUsername.getText().toString().trim());
                    userInfos.putString("PASSWORD", fieldPassword.getText().toString().trim());
                    userInfos.putString("USERTYPE",String.valueOf(userType));
                    userInfos.putBoolean("ISNEWUSER", true);
                    showOtherFragment(userInfos);
                }
        });


        return rootView;
    }


    public void showOtherFragment(Bundle bundle) {
        RegisterAddress registerAddress = new RegisterAddress();
        registerAddress.setArguments(bundle);
        FragmentChangeListener fc = (FragmentChangeListener) getActivity();
        fc.replaceFragment(registerAddress);
    }

    public boolean validateNotNullFields() {
        String invalidFields = "";
        if (fieldFantasyName.getText().toString().trim().equals(""))
            invalidFields += "fantasyname;";
        if (fieldSocialReason.getText().toString().trim().equals(""))
            invalidFields += "socialreason;";
        if (fieldCNPJ.getText().toString().trim().equals(""))
            invalidFields += "cnpj;";
        if (fieldEmail.getText().toString().trim().equals(""))
            invalidFields += "email;";
        if (fieldPhone.getText().toString().trim().equals(""))
            invalidFields += "phone;";
        if (fieldUsername.getText().toString().trim().equals(""))
            invalidFields += "username;";
        if (fieldPassword.getText().toString().trim().equals("")) {
            invalidFields += "password;";
        }
        String[] invalidFieldsArray = invalidFields.split(";");
        if (invalidFieldsArray.length > 1) {
            for (int i = 0; i < invalidFieldsArray.length; i++) {
                switch (invalidFieldsArray[i]) {
                    case "fantasyname":
                        textInputLayoutFantasyName.setError("Nome fantasia não pode ser vazio");
                        break;
                    case "socialreason":
                        textInputLayoutSocialReason.setError("A Razão social não pode ser vazia");
                        break;
                    case "cnpj":
                        textInputLayoutCNPJ.setError("Seu CNPJ está inválido.");
                        break;
                    case "email":
                        textInputLayoutEmail.setError("Seu email não pode ser vazio.");
                        break;
                    case "phone":
                        textInputLayoutPhone.setError("Seu número de celualr não pode ser vazio.");
                        break;
                    case "username":
                        textInputLayoutUsername.setError("Seu nome de acesso não pode ser vazio.");
                        break;
                    case "password":
                        textInputLayoutPassword.setError("Sua senha de acesso não pode ser vazia");
                        break;
                }
            }
        }
        return invalidFields.equals("");
    }

}
