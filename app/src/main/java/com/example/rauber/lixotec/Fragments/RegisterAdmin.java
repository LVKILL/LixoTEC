package com.example.rauber.lixotec.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.rauber.lixotec.R;
import com.github.pinball83.maskededittext.MaskedEditText;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterAdmin extends Fragment {

    private EditText fieldUsername;
    private EditText fieldPassword;
    private EditText fieldconfirmPassword;
    private EditText fieldEmail;
    private MaskedEditText fieldPhone;

    public RegisterAdmin() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_register_admin, null, false);

        fieldUsername = rootView.findViewById(R.id.EditTextUsername);
        fieldPassword = rootView.findViewById(R.id.EditTextPassword);
        fieldconfirmPassword = rootView.findViewById(R.id.EditTextConfirmPassword);
        fieldEmail = rootView.findViewById(R.id.EditTextEmail);
        fieldPhone = rootView.findViewById(R.id.EditTextPhone);






        return rootView;
    }

}
