package com.disys.systemtask;

import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.Loader;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.disys.systemtask.model.User;
import com.disys.systemtask.network.ReqWhomItMayConcernLoader;
import com.disys.systemtask.utility.SessionSharPref;
import com.disys.systemtask.utility.Utilities;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.HashMap;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.subscribers.DisposableSubscriber;

import static android.util.Patterns.EMAIL_ADDRESS;

/*
-> RequestToWhomItMayConcernActivity is for submit the user data
-> Implemented RX java for form validation
-> Used Okhttp3 libery for access the API
 */

public class RequestToWhomItMayConcernActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<HashMap<String, String>>, View.OnClickListener {

    private static final String TAG = RequestToWhomItMayConcernActivity.class.getName();

    EditText etEid, etName, etIdBarhNo, etEmail, etUnifiedNo, etMobile;

    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    Button btnSumite;

    private DisposableSubscriber<Boolean> _disposableObserver = null;
    private Flowable<CharSequence> idObservable;
    private Flowable<CharSequence> barahNoObservable;
    private Flowable<CharSequence> emailObservable;
    private Flowable<CharSequence> unifiedNoObservable;
    private Flowable<CharSequence> mobObservable;
    private Flowable<CharSequence> cNameObservable;

    SYSApplication myApplication;

    SessionSharPref sessionSharPref;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_to_whom_it_may_concern);

        myApplication = (SYSApplication) getApplication();

        sessionSharPref = new SessionSharPref(this);

        init();
        initFirebaseRemoteConfig();
    }


    private void initFirebaseRemoteConfig()
    {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        FirebaseRemoteConfigSettings remoteConfigSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(true)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(remoteConfigSettings);

        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        fetchRemoteValue();
    }

    private void fetchRemoteValue()
    {
        // cache expiration in seconds
        long cacheExpiration = 3600;

//expire the cache immediately for development mode.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 10*60*1000;
        }

// fetch
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            // task successful. Activate the fetched data
                            mFirebaseRemoteConfig.activateFetched();

                            sessionSharPref.saveSecretCode(mFirebaseRemoteConfig.getString("consumerKey"),mFirebaseRemoteConfig.getString("consumerSecret"));

                        } else {
                            //task failed
                        }
                    }
                });




    }

    //Intiallize the controls and events
    private void init() {
        etEid = (EditText) findViewById(R.id.et_eid);
        etName = (EditText) findViewById(R.id.et_customer_name);
        etIdBarhNo = (EditText) findViewById(R.id.et_id_barah_no);
        etEmail = (EditText) findViewById(R.id.et_email_id);
        etUnifiedNo = (EditText) findViewById(R.id.et_unified_no);
        etMobile = (EditText) findViewById(R.id.et_mobile_no);

        btnSumite = (Button) findViewById(R.id.btn_submit);
        btnSumite.setOnClickListener(this);

        //Implement onclick listener for textview
        //This is for skip this activity
        ((TextView) findViewById(R.id.tvSkip)).setOnClickListener(this);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Submitting Data... Please wait...");
        dialog.setCancelable(false);

        //Initiallize the observables
        initObservables();
        //Combine the latest events
        combineLatestEvents();
    }


    private void initObservables() {
        idObservable = RxTextView.textChanges(etEid).skip(1).toFlowable(BackpressureStrategy.LATEST);
        cNameObservable = RxTextView.textChanges(etName).skip(1).toFlowable(BackpressureStrategy.LATEST);
        barahNoObservable = RxTextView.textChanges(etIdBarhNo).skip(1).toFlowable(BackpressureStrategy.LATEST);
        emailObservable = RxTextView.textChanges(etEmail).skip(1).toFlowable(BackpressureStrategy.LATEST);
        mobObservable = RxTextView.textChanges(etMobile).skip(1).toFlowable(BackpressureStrategy.LATEST);
        unifiedNoObservable = RxTextView.textChanges(etUnifiedNo).skip(1).toFlowable(BackpressureStrategy.LATEST);
    }


    private void combineLatestEvents() {

        _disposableObserver =
                new DisposableSubscriber<Boolean>() {
                    @Override
                    public void onNext(Boolean formValid) {
                        updateButton(formValid);
                    }

                    @Override
                    public void onError(Throwable e) {

                        // Timber.e(e, "there was an error");
                    }

                    @Override
                    public void onComplete() {
                        //  Timber.d("completed");
                    }
                };

        Flowable.combineLatest(
                idObservable,
                cNameObservable,
                barahNoObservable,
                emailObservable,
                mobObservable,
                unifiedNoObservable,
                (s1, s2, s3, s4, s5, s6) -> {

                    return isValidForm(s1.toString(), s2.toString(), s3.toString(), s4.toString(), s5.toString(), s6.toString());
                })
                .subscribe(_disposableObserver);
    }

    public void updateButton(boolean valid) {
        if (valid)
            btnSumite.setEnabled(true);
        else
            btnSumite.setEnabled(false);
    }

    //Validate the form data
    //Implemented empty value checking only. don't know about the other field constraints.
    public boolean isValidForm(String eId, String customerName, String idBarahNo, String emailId, String mobileNo, String unifiedNo) {

        boolean validEid = !eId.isEmpty();

        if (!validEid) {
            etEid.setError("Please enter valid emirates ID number.");
        }


        boolean validCName = !customerName.isEmpty();
        if (!validCName) {
            etName.setError("Please enter customer's name");
        }

        boolean validIdBarahNo = !idBarahNo.isEmpty();
        if (!validIdBarahNo) {
            etIdBarhNo.setError("Please enter  Idbarah number");
        }


        boolean validEmailId = !emailId.isEmpty() && EMAIL_ADDRESS.matcher(emailId).matches();
        if (!validEmailId) {
            etEmail.setError("Please enter valid email address.");
        }

        boolean validMob = !mobileNo.isEmpty();
//&& (mobileNo.length() < 10 || mobileNo.length() > 10)
        if (!validMob) {
            etMobile.setError("Please enter valid mobile no.");
        }

        boolean validUnifiedNo = !unifiedNo.isEmpty();

        if (!validUnifiedNo) {
            etUnifiedNo.setError("Please enter unified no.");
        }

        return validEid && validCName && validEmailId && validIdBarahNo && validMob && validUnifiedNo;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        //Dispose the Disposable Subscriber
        _disposableObserver.dispose();

        //Dismis the progress dialog after destroy the activity while showing dialog.
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    //Dismis the progress dialog after not visible the activity to user while showing dialog.
    @Override
    public void onPause() {
        super.onPause();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    //After validate the form data. get the form data and return as hasmap data
    private HashMap<String, String> getFormValue() {
        HashMap<String, String> data = new HashMap<String, String>();

        data.put("eid", etEid.getText().toString().trim());
        data.put("name", etName.getText().toString().trim());
        data.put("idbarahno", etIdBarhNo.getText().toString().trim());
        data.put("emailaddress", etEmail.getText().toString().trim());
        data.put("unifiednumber", etUnifiedNo.getText().toString().trim());
        data.put("mobileno", etMobile.getText().toString().trim());

        return data;
    }

    @Override
    public Loader<HashMap<String, String>> onCreateLoader(int i, Bundle bundle) {

        //call the network loader for establish the connection between the mobile and server through API
        //call request to whoe it may concert api and pass form data as hasmap type
        return new ReqWhomItMayConcernLoader(this, getFormValue(), null);

    }


    @Override
    public void onLoadFinished(Loader<HashMap<String, String>> loader, HashMap<String, String> data) {
        if (data != null && !data.isEmpty()) {

            try {
                setData(data);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Utilities.showToast(getApplicationContext(), data.get(getResources().getString(R.string.status_msg)));
        }
    }

    //handle the success and failuer response code
    private void setData(HashMap<String, String> data) {

        if (dialog.isShowing()) {
            dialog.dismiss();
        }

        //response code is 200
        if (data.get(getResources().getString(R.string.response_code)).equals("200")) {

            if (data.get("success").equals("true")) {
                Utilities.showToast(getApplicationContext(), data.get("message"));

                redirect();
            } else { // this is not successfull response even after get the response code 200.
                Utilities.showToast(getApplicationContext(), data.get("message"));

            }
        } else if (data.get(getResources().getString(R.string.response_code)).equals("500")) { //Internal server error
            Utilities.showToast(getApplicationContext(), "Internal Server Error. Please try after sometimes or contact admin");
        } else {
            //Display the error response
            Utilities.showToast(getApplicationContext(), data.get(getResources().getString(R.string.status_msg)));
        }

    }


    //Redirec the screen to news activity
    private void redirect() {

        startActivity(new Intent(RequestToWhomItMayConcernActivity.this, NewsActivity.class));
    }

    @Override
    public void onLoaderReset(Loader<HashMap<String, String>> loader) {

    }

    //Onclick listernet interface method
    //implemented the click event for form submit button
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_submit:
                //call api
                callAPI();
                break;

            case R.id.tvSkip:
                redirect();
                break;
        }

    }


    private void callAPI() {
        //check wheather the network is available or not
        if (myApplication.checkNetwork()) {
            dialog.show();
            getLoaderManager().initLoader(0, null, this).forceLoad();
        } else {

            //redirec the screen to oppsactivity for notify that establish the internet connection
            Intent i = new Intent(this, OopsActivity.class);
            startActivity(i);
            finish();
        }
    }

}
