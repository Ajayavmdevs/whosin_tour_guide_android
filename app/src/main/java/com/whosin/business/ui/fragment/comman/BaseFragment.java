package com.whosin.business.ui.fragment.comman;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

import com.whosin.business.R;
import com.whosin.business.service.manager.TranslationManager;
import com.whosin.business.ui.activites.comman.BetterActivityResult;

import java.util.HashMap;
import java.util.Map;


public abstract class BaseFragment extends Fragment {

    private AlertDialog dialog = null;
    protected Context context;
    protected final BetterActivityResult<Intent, ActivityResult> activityLauncher = BetterActivityResult.registerActivityForResult(this);

    // --------------------------------------
    // region Life Cycle
    // --------------------------------------

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(getLayoutRes(), container, false);
        context = view.getContext();
        initUi(view);

        setListeners();
        populateData(true);
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(TranslationManager.updateLocale(context));
    }


    // endregion
    // --------------------------------------
    // region Protected
    // --------------------------------------

    // endregion
    // --------------------------------------
    // region Public
    // --------------------------------------

    public void showProgress() {
        if (getActivity() == null || getActivity().isFinishing()) { return; }
        if (dialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setCancelable(false);
            builder.setView( R.layout.layout_loading_dialog);
            dialog = builder.create();
            dialog.getWindow().setBackgroundDrawable( new ColorDrawable(android.graphics.Color.TRANSPARENT ));
        }
        dialog.show();
    }

    public void showProgress(String msg) {
        if (this.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
            if (getActivity() == null || getActivity().isFinishing()) {
                return;
            }
            if (dialog == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setCancelable(false);
                builder.setView( R.layout.layout_loading_dialog);
                dialog = builder.create();
                dialog.getWindow().setBackgroundDrawable( new ColorDrawable(android.graphics.Color.TRANSPARENT ));
            }
            dialog.show();
        }
    }

    public void hideProgress() {
        if (dialog != null && getActivity() != null && !getActivity().isFinishing()) {
            dialog.dismiss();
        }
    }


    // endregion
    // --------------------------------------
    // region Abstract
    // --------------------------------------

    public abstract void initUi(View view);

    public abstract void setListeners();

    public abstract void populateData(boolean getDataFromServer);

    public abstract int getLayoutRes();

    protected void setTranslatedText(View view, String key) {
        if (view != null && key != null) {
            String translated = TranslationManager.shared.get(key);

            if (translated != null) {
                if (view instanceof TextView) {
                    if (view instanceof EditText) {
                        ((EditText) view).setHint(translated);
                    } else {
                        ((TextView) view).setText(translated);
                    }
                }
            }
        }
    }

    protected void setTranslatedTexts(Map<View, String> map) {
        for (Map.Entry<View, String> entry : map.entrySet()) {
            setTranslatedText(entry.getKey(), entry.getValue());
        }
    }

    protected Map<View, String> getTranslationMap() {
        return new HashMap<>();
    }

    protected void applyTranslations() {
        Map<View, String> map = getTranslationMap();
        if (map == null) return;

        for (Map.Entry<View, String> entry : map.entrySet()) {
            setTranslatedText(entry.getKey(), entry.getValue());
        }
    }


    protected String getValue(String key){
        return TranslationManager.shared.get(key);
    }

    protected String setValue(String key, String value) {
        String template = TranslationManager.shared.get(key);
        if (template == null) return "";
        return template.replaceAll("\\{.*?\\}", value);
    }

    protected String setValue(String key, String... values) {
        String template = TranslationManager.shared.get(key);
        if (template == null) return "";

        for (String val : values) {
            template = template.replaceFirst("\\{.*?\\}", val);
        }

        return template;
    }

    // endregion
    // --------------------------------------

}
