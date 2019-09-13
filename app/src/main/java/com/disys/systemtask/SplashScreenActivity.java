package com.disys.systemtask;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.disys.systemtask.model.User;
import com.disys.systemtask.utility.SessionSharPref;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SplashScreenActivity extends BaseActivity {
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;

    Context mContext;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private GoogleSignInClient mGoogleSignInClient;
    Button btnSignIn;
    TextView tvMsg;
    ProgressBar progressBar;

    SessionSharPref sessionManager;

    String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mContext = this;
        sessionManager = new SessionSharPref(mContext);



        initAuthentication();
        initGoogleClient();
        init();



    }




    private void init() {
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        tvMsg = (TextView) findViewById(R.id.tvMsg);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        btnSignIn.setVisibility(View.GONE);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnSignIn.getText().toString().equals(getResources().getString(R.string.google_signin))) {


                    signIn();

                } else {
                    if (isNetworkAvailable()) {
                        {
                            btnSignIn.setText(getResources().getString(R.string.google_signin));
                            tvMsg.setText("Please Sign In");
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Connect the internet and try again", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }

    private void initAuthentication() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void initGoogleClient() {
        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.


        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

    }

    private void updateUI(FirebaseUser currentUser) {
        if (!isNetworkAvailable()) {
            btnSignIn.setVisibility(View.VISIBLE);
            btnSignIn.setText("Retry");
            progressBar.setVisibility(View.GONE);
            tvMsg.setText("Please check the internet");


        }
        else if (currentUser == null) {


            btnSignIn.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            tvMsg.setText("Please Sign In");



        } else {

            User user=new User();
            user.setUserName(currentUser.getEmail());
            user.setPassword(currentUser.getUid());
            sessionManager.saveUserDate(user);

            redirecPage(currentUser);
        }


    }

    private void redirecPage(FirebaseUser currentUser) {



        startActivity(new Intent(SplashScreenActivity.this, RequestToWhomItMayConcernActivity.class));



        try {

        } catch (Exception ey) {
            mAuth.signOut();
            Toast.makeText(mContext, "Please try again later", Toast.LENGTH_SHORT).show();
        }
    }

    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        }
    }

//
//    private void getUserDetails(final FirebaseUser user) {
//        DocumentReference docRef = Firestore.getInstance().collection(Utility.COLLECTION_USER).document(user.getUid());
//        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                User user1 = documentSnapshot.toObject(User.class);
//
//                if (user1 == null) {
//                    System.out.println("Issues 01 - user does not exist");
//                    addNewUser(user.getDisplayName(), user.getEmail(), user.getPhotoUrl().toString(), user.getUid(), user);
//
//                } else {
//                    updateUI(user);
//                    System.out.println("Issues 01 - user already exist" + documentSnapshot.getId());
//                }
//
//            }
//        });
//    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            updateUI(user);

                         //   getUserDetails(user);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication Failed", Toast.LENGTH_LONG).show();
                            //   Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                            hideProgressDialog();
                        }

                        // [START_EXCLUDE]

                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_google]

    // [START signin]
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


}
