package com.whosin.app.ui.fragment.PromoterCmApply;

import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Toast;

import com.whosin.app.R;
import com.whosin.app.comman.Utils;
import com.whosin.app.databinding.FragmentPromoterCmUserBioBinding;
import com.whosin.app.service.manager.PromoterCmApplyManager;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.ui.fragment.comman.BaseFragment;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class PromoterCmUserBioFragment extends BaseFragment {

    private FragmentPromoterCmUserBioBinding binding;

    private PromoterCmApplyManager manager = PromoterCmApplyManager.shared;

    private UserDetailModel userDetailModel = manager.userDetailModel;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    public void initUi(View view) {
        binding = FragmentPromoterCmUserBioBinding.bind(view);

        applyTranslations();

        binding.tvPrivacyPolicy.setText(
                Html.fromHtml(getValue("disclaimer_policy"), Html.FROM_HTML_MODE_LEGACY)
        );
        binding.tvPrivacyPolicy.setMovementMethod(LinkMovementMethod.getInstance());


        if (manager.isEditProfile) {
            if (userDetailModel == null) {
                return;
            }

            binding.editGetBio.setText(userDetailModel.getBio());

            binding.checkbox.setVisibility(View.GONE);
            binding.tvPrivacyPolicy.setVisibility(View.GONE);

        }else {
            String bioText = SessionManager.shared.getUser().getBio();
            if (!TextUtils.isEmpty(bioText)) binding.editGetBio.setText(bioText);
        }
    }

    @Override
    public void setListeners() {

        binding.tvPrivacyPolicy.setOnClickListener(v -> {
            String url = "https://www.whosin.me/privacy-policy/";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });

    }

    @Override
    public void populateData(boolean getDataFromServer) {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_promoter_cm_user_bio;
    }

    @Override
    protected Map<View, String> getTranslationMap() {
        Map<View, String> map = new HashMap<>();
        map.put(binding.title, "What_do_you_love_to_do");
        map.put(binding.editGetBio, "bio_info");
        binding.checkbox.setText(getValue("confirm_alert_information"));
        return map;
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    public boolean isDataValid() {


        if (Utils.isNullOrEmpty(binding.editGetBio.getText().toString().trim())) {
            Toast.makeText(requireActivity(), getValue("bit_about_interests"), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!manager.isEditProfile && !binding.checkbox.isChecked()) {
            Toast.makeText(requireActivity(), getValue("accept_conformtion"), Toast.LENGTH_SHORT).show();
            return false;
        }

        manager.object.addProperty("bio", binding.editGetBio.getText().toString().trim());

        return true;
    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------


    // --------------------------------------
    // region Adapter
    // --------------------------------------


    // endregion
    // --------------------------------------
}