package com.whosin.app.ui.fragment.PromoterCmApply;

import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.whosin.app.R;
import com.whosin.app.comman.Utils;
import com.whosin.app.databinding.FragmentPromoterCmSoicalInfoFragmnetBinding;
import com.whosin.app.service.manager.PromoterCmApplyManager;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.ui.fragment.comman.BaseFragment;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class PromoterCmSoicalInfoFragmnet extends BaseFragment {

    private FragmentPromoterCmSoicalInfoFragmnetBinding binding;

    private PromoterCmApplyManager manager = PromoterCmApplyManager.shared;

    private UserDetailModel userDetailModel = manager.userDetailModel;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    public void initUi(View view) {

        binding = FragmentPromoterCmSoicalInfoFragmnetBinding.bind(view);


        applyTranslations();


        if (manager.isEditProfile) {
            if (userDetailModel == null) {
                return;
            }

            binding.layoutInstagram.setText(userDetailModel.getInstagram());
            binding.layoutTiktok.setText(userDetailModel.getTiktok());
            binding.layoutYoutube.setText(userDetailModel.getYoutube());
            binding.layoutFacebook.setText(userDetailModel.getFacebook());

        }else {

            UserDetailModel model = SessionManager.shared.getUser();
            if (model == null) {
                return;
            }

            binding.layoutInstagram.setText(model.getInstagram());
            binding.layoutTiktok.setText(model.getTiktok());
            binding.layoutYoutube.setText(model.getYoutube());
            binding.layoutFacebook.setText(model.getFacebook());
        }
    }

    @Override
    public void setListeners() {

    }

    @Override
    public void populateData(boolean getDataFromServer) {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_promoter_cm_soical_info_fragmnet;
    }

    public boolean isDataValid() {

        if (TextUtils.isEmpty(binding.layoutInstagram.getText())) {
            Toast.makeText(requireActivity(), "Please enter instagram handle", Toast.LENGTH_SHORT).show();
            return false;
        }


//        String instagramUrlText = binding.layoutInstagram.getText().toString();
//
//        try {
//            URL instagramUrl = new URL(instagramUrlText);
//            if (!Utils.validateInstagramProfileUrl(instagramUrl)) {
//                Toast.makeText(requireActivity(), "Please enter a valid Instagram profile link", Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        } catch (MalformedURLException e) {
//            Toast.makeText(requireActivity(), "Please enter a valid Instagram profile link", Toast.LENGTH_SHORT).show();
//            return false;
//        }


        manager.object.addProperty("facebook", binding.layoutFacebook.getText());
        manager.object.addProperty("instagram", binding.layoutInstagram.getText());
        manager.object.addProperty("youtube", binding.layoutYoutube.getText());
        manager.object.addProperty("tiktok", binding.layoutTiktok.getText());

        return true;
    }

    @Override
    protected Map<View, String> getTranslationMap() {
        Map<View, String> map = new HashMap<>();
        map.put(binding.tvTitleSocial, "social_accounts");
        map.put(binding.description, "instagram_note");

        binding.layoutInstagram.setHintText(getValue("profile_link_message"));
        binding.layoutTiktok.setHintText(getValue("add_your_tiktok_account_optional"));
        binding.layoutYoutube.setHintText(getValue("add_your_youtube_channel_optional"));
        binding.layoutFacebook.setHintText(getValue("add_your_facebook_account_optional"));


        return map;
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------


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