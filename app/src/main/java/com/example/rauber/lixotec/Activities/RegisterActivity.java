package com.example.rauber.lixotec.Activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.rauber.lixotec.FragmentChangeListener;
import com.example.rauber.lixotec.Fragments.RegisterAddress;
import com.example.rauber.lixotec.Fragments.RegisterAdmin;
import com.example.rauber.lixotec.Fragments.RegisterCompany;
import com.example.rauber.lixotec.Fragments.RegisterPerson;
import com.example.rauber.lixotec.R;
import com.example.rauber.lixotec.SharedPreferences.SystemPreferences;

import java.util.List;

public class RegisterActivity extends AppCompatActivity implements FragmentChangeListener {

    Fragment firstFragment;
    Fragment secondFragment;
    String registerType;
    private SystemPreferences systemPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_screen);
        systemPreferences = new SystemPreferences(this);
        registerType = getIntent().getStringExtra("REGISTERTYPE");
        switch (registerType) {
            case "PERSON":
                RegisterPerson registerPerson = new RegisterPerson();
                firstFragment = registerPerson;
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, registerPerson)
                        .commit();
                break;
            case "COMPANY":
                RegisterCompany registerCompany = new RegisterCompany();
                firstFragment = registerCompany;
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, registerCompany, registerCompany.toString())
                        .commit();
                break;
            case "ADDRESS":
                RegisterAddress registerAddress = new RegisterAddress();
                firstFragment = registerAddress;
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, registerAddress, registerAddress.toString())
                        .commit();
                break;
            case "ADMIN":
                RegisterAdmin registerAdmin = new RegisterAdmin();
                firstFragment = registerAdmin;
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, registerAdmin, registerAdmin.toString())
                        .commit();
                default:
//                        Intent preRegisterScreen = new Intent(RegisterActivity.this, PreRegistrerActivity.class);
//                        startActivity(preRegisterScreen);
//                        finish();
                Log.v("tag", "caiu no default hehe");
        }

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {

            }
        });


    }


    @Override
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        secondFragment = fragment;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment, fragment.toString());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        List<Fragment> fragmentsList = getSupportFragmentManager().getFragments();
        for (int i = 0; i < fragmentsList.size(); i++) {
            Fragment fragmentInList = fragmentsList.get(i);
            if (fragmentInList.equals(firstFragment)) {
                if (registerType.equals("ADDRESS")) {
                    if(systemPreferences.getUserType() == 1){
                        Intent screenColetaPoints = new Intent(RegisterActivity.this, ColetasPoinsActivity.class);
                        getSupportFragmentManager().beginTransaction().detach(firstFragment).commit();
                        startActivity(screenColetaPoints);
                        finish();
                    }else {
                        Intent selectAddressScreen = new Intent(RegisterActivity.this, SelectAddressActivity.class);
                        startActivity(selectAddressScreen);
                        getSupportFragmentManager().beginTransaction().detach(firstFragment).commit();
                        finish();
                    }
                } else {
                    Intent preRegisterScreen = new Intent(RegisterActivity.this, PreRegistrerActivity.class);
                    startActivity(preRegisterScreen);
                    getSupportFragmentManager().beginTransaction().detach(firstFragment).commit();
                    finish();
                }
            } else {
                super.onBackPressed();
            }
        }
    }

}
