package com.whosin.business.service.manager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.whosin.business.R;
import com.whosin.business.comman.Utils;
import com.whosin.business.ui.activites.comman.SplashActivity;

public class DialogManager {

    private static DialogManager instance;

    private Context applicationContext;

    private DialogManager(Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    public static synchronized DialogManager getInstance(Context context) {
        if (instance == null) {
            instance = new DialogManager(context.getApplicationContext());
        }
        return instance;
    }

    public void showRestartAppDialog(String type) {

        String message;

        switch (type) {
            case "complimentary":
                message = Utils.getLangValue("account_upgraded_complimentary");
                break;

            case "promoter":
                message = Utils.getLangValue("account_upgraded_promoter");
                break;

            case "subadmin-remove":
                message = Utils.getLangValue("account_revoked_subadmin");
                break;

            case "subadmin-approve":
                message =  Utils.getLangValue("account_upgraded_subadmin");
                break;
            default:
                message = Utils.getLangValue("account_updated");
                break;
        }

        new Handler(Looper.getMainLooper()).post(() -> {
            Activity activity = ActivityTrackerManager.getInstance().getCurrentActivity();
            if (activity != null && !activity.isFinishing()) {
                LayoutInflater inflater = LayoutInflater.from(activity);
                View customView = inflater.inflate(R.layout.acoount_upgrad_dialog_ui, null);

                TextView dialogMessage = customView.findViewById(R.id.tvDescription);
                TextView dialogMainTitle = customView.findViewById(R.id.tvTitle);
                TextView dialogButton = customView.findViewById(R.id.dialogButton);

                dialogButton.setText(Utils.getLangValue("restart"));
                dialogMainTitle.setText(Utils.getLangValue("account_upgrade_title"));

                dialogMessage.setText(message);


                // Create and show the dialog
                AlertDialog dialog = new AlertDialog.Builder(activity)
                        .setView(customView)
                        .setCancelable(false)
                        .create();

                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                }

                dialog.show();

                dialogButton.setOnClickListener(view -> {
                    dialog.dismiss();
                    // Restart the app
                    Intent restartIntent = new Intent(activity, SplashActivity.class);
                    restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(restartIntent);
                    System.exit(0);
                });
            }
        });
    }
}

