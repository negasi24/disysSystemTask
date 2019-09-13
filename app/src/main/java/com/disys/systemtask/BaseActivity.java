package com.disys.systemtask;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;

import com.disys.systemtask.model.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Deepan on 1/11/2018.
 */

public class BaseActivity extends AppCompatActivity {

    public static User user;

    @VisibleForTesting
    public ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void setUserDetails(User user)
    {
        this.user=user;
    }
    public User getUser()
    {
        return user;
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }
    public Date convertStringtoDate(String dtStart) {
        Date date = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        try {
            date = format.parse(dtStart);
            System.out.println("converted date" + date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    public void closeKeyPad() {

        try {
            InputMethodManager inputManager =
                    (InputMethodManager) this.
                            getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(
                    this.getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception ey) {


        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}