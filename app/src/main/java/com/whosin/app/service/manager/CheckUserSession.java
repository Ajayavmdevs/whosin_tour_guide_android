package com.whosin.app.service.manager;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.whosin.app.R;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Preferences;
import com.whosin.app.comman.Utils;
import com.whosin.app.ui.activites.auth.AuthenticationActivity;

public class CheckUserSession {

    public static void checkSessionAndProceed(Activity activity, Runnable onSessionValid) {
        SessionManager.shared.requestCheckSession(activity, (isExpired, errorMessage) -> {
            if (isExpired) {
                handleSessionExpired(activity, errorMessage);
            } else {
                if (onSessionValid != null) onSessionValid.run();
            }
        });
    }

    private static void handleSessionExpired(Activity activity, String message) {
        Graphics.showAlertDialogWithOkButton(activity, activity.getString(R.string.app_name), message, confirmed -> {
            if (confirmed) {
                if (isPermanentBan(message)) {
                    signOutGoogleIfNeeded(activity);
                    logoutAndRedirectToLogin(activity);
                } else {
                    SessionManager.shared.logout(activity, (success, logoutError) -> {
                        if (!Utils.isNullOrEmpty(logoutError)) {
                            Toast.makeText(activity, logoutError, Toast.LENGTH_SHORT).show();
                        } else {
                            logoutAndRedirectToLogin(activity);
                        }
                    });
                }
            }
        });
    }

    private static boolean isPermanentBan(String message) {
        return "Your account has been permanently banned. You will now be logged out.".equals(message);
    }

    private static void signOutGoogleIfNeeded(Activity activity) {
        int isUserGoogleLogin = Preferences.shared.getInt("isUserGoogleLogin");
        if (isUserGoogleLogin == 1) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(activity, gso);
            googleSignInClient.signOut();
        }
        SessionManager.shared.clearSessionData(activity);
    }

    private static void logoutAndRedirectToLogin(Activity activity) {
        Intent intent = new Intent(activity, AuthenticationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }
}
