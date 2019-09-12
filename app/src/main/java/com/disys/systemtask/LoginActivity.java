package com.disys.systemtask;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.disys.systemtask.model.User;
import com.disys.systemtask.utility.Credential;
import com.disys.systemtask.utility.SessionSharPref;

import com.jakewharton.rxbinding2.widget.RxTextView;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;

/*
-> LoginActivity is for login purpose.
-> User should submit atlease once in request to whom it may concern form for authenticate the login.
-> Implemented RXJava for form validation
*/

public class LoginActivity extends AppCompatActivity {

    EditText etUserName, etPassword;
    Button btnSignIn;

    Observable<Boolean> observable;

    SessionSharPref sessionSharPref;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Create a reference for custom shared pref class for get user data which is stored in local storage
        sessionSharPref = new SessionSharPref(this);

        //Initiallize the class and other reference of control
        init();
    }

    @Override
    public void onResume() {
        super.onResume();

    }


    //Save user data to shared preferecen for further usage and authentication locally.
    // it will override the data when happen another login.
    private void saveUserData(String UserName, String Password) {
        User user = new User();

        //Encript the username and password for security purpose by using simple method.
        byte[] UNdata = UserName.getBytes(StandardCharsets.UTF_8);
        String UNbase64 = Base64.encodeToString(UNdata, Base64.DEFAULT);
        user.setUserName(UNbase64);

        byte[] PWdata = Password.getBytes(StandardCharsets.UTF_8);
        String PWbase64 = Base64.encodeToString(PWdata, Base64.DEFAULT);
        user.setPassword(PWbase64);

        sessionSharPref.saveUserDate(user);

    }


    //Intiallize the controls and events
    private void init() {
        etUserName = (EditText) findViewById(R.id.et_user_name);
        etPassword = (EditText) findViewById(R.id.et_password);

        btnSignIn = (Button) findViewById(R.id.btn_sigin);

        //implement the click listener event for sigin button.
        // This button will be enabled once validate the form data and authenticate successfully.
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Save the user data after successfull login
                saveUserData(etUserName.getText().toString().trim(), etPassword.getText().toString().trim());

                startActivity(new Intent(LoginActivity.this, RequestToWhomItMayConcernActivity.class));
            }
        });

        //Initiallize the observable for mobile number edittext chanage event using RX java.
        Observable<String> mobObservable = RxTextView.textChanges(etUserName).skip(1).map(new Function<CharSequence, String>() {
            @Override
            public String apply(CharSequence charSequence) throws Exception {
                return charSequence.toString();
            }
        });
        //Initiallize the observable for password edittext chanage event using RX java.
        Observable<String> passwordObservable = RxTextView.textChanges(etPassword).skip(1).map(new Function<CharSequence, String>() {
            @Override
            public String apply(CharSequence charSequence) throws Exception {
                return charSequence.toString();
            }
        });

        //Combile the two observable for validate the data
        observable = Observable.combineLatest(mobObservable, passwordObservable, new BiFunction<String, String, Boolean>() {
            @Override
            public Boolean apply(String s, String s2) throws Exception {
                return isValidForm(s, s2);
            }
        });

        //Subscribe the observable
        observable.subscribe(new DisposableObserver<Boolean>() {
            @Override
            public void onNext(Boolean aBoolean) {
                //update the submit button depend on validation result(true/false)
                updateButton(aBoolean);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

    }

    //enable the submit button once validate and authenticate the user credential
    public void updateButton(boolean valid) {
        if (valid)
            btnSignIn.setEnabled(true);
        else
            btnSignIn.setEnabled(false);
    }

    //valida the form data is null or empty
    //login credential authentication
    public boolean isValidForm(String userName, String password) {
        boolean validUN = !userName.isEmpty();

        if (!validUN) {
            etUserName.setError("Please enter user name.");
        }


        boolean validPass = !password.isEmpty();
        if (!validPass) {
            etPassword.setError("Please enter password");
        }

        boolean validAuthencation = false;
        if (validUN && validPass) {
            HashMap<String, String> sampleCredential = Credential.getCredntials();

            String passStr = sampleCredential.get(userName);


            if (passStr == null)
                etUserName.setError("Invalid user name");
            else {
                validAuthencation = passStr.equals(password);
                if (!validAuthencation) {
                    if(password.length()>5)
                    etPassword.setError("Invalid password");
                }
            }
        }

        return validUN && validPass && validAuthencation;
    }

}
