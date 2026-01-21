package com.whosin.app.ui.activites.auth;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.internal.OnConnectionFailedListener;
import com.google.android.gms.tasks.Task;

public class GoogleLogin implements OnConnectionFailedListener {

    private static final String TAG = GoogleLogin.class.getSimpleName();
    public static final int RC_SIGN_IN = 9007;


    private Fragment fragment;

    private Activity activity;

    private GoogleSignInClient googleApiClient;

    private GoogleListener googleListener;

    public void setGoogleListener(GoogleListener googleListener) {
        this.googleListener = googleListener;
    }

    public GoogleLogin(Fragment fragment) {
        this.fragment = fragment;
        initCredentials();
    }

    public GoogleLogin(Activity activity) {
        this.activity = activity;
        initCredentials();
    }

    private void initCredentials() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("965402678086-atvsnh1c0gj2jofqt55864sjsnonl8nv.apps.googleusercontent.com")
                .requestEmail()
                .build();
        if (fragment != null) {
            googleApiClient = GoogleSignIn.getClient(fragment.getActivity(), googleSignInOptions);
        } else {
            googleApiClient = GoogleSignIn.getClient(activity, googleSignInOptions);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        googleListener.onConnectionFailed(connectionResult.getErrorCode(), connectionResult.getErrorMessage());
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                googleListener.onLoginSuccess(account);
            } catch (ApiException e) {
                e.printStackTrace();
                googleListener.onFailed(e);
            }

        }
    }

    public void signIn() {
        Intent signInIntent = googleApiClient.getSignInIntent();
        if (activity != null) {
            activity.startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    }

    public void signOut() {
        googleApiClient.signOut();
    }


    public interface GoogleListener {

        void onConnectionFailed(int errorCode, String errorMessage);

        void onLoginSuccess(GoogleSignInAccount task);

        void onFailed(ApiException status);
    }
}