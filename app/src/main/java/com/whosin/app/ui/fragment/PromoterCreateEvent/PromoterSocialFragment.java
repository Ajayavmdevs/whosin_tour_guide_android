package com.whosin.app.ui.fragment.PromoterCreateEvent;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.FragmentPromoterSocialBinding;
import com.whosin.app.databinding.ItemEventSocialAccountBinding;
import com.whosin.app.service.manager.PromoterProfileManager;
import com.whosin.app.service.models.PromoterEventModel;
import com.whosin.app.service.models.SocialAccountsToMentionModel;
import com.whosin.app.ui.fragment.comman.BaseFragment;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PromoterSocialFragment extends BaseFragment {

    private FragmentPromoterSocialBinding binding;

    private final PromoterProfileManager promoterManager = PromoterProfileManager.shared;

    private final JsonObject promoterEventObject = promoterManager.promoterEventObject;

    private final PromoterEventModel promoterEventModel = promoterManager.promoterEventModel;

    private List<SocialAccountsToMentionModel> socialAccountList = new ArrayList<>();

    private SocialItemListAdapter<SocialAccountsToMentionModel> adapter = new SocialItemListAdapter<>();


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    @Override
    public void initUi(View view) {

        binding = FragmentPromoterSocialBinding.bind(view);



        binding.addSocialItemRecycleview.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
        binding.addSocialItemRecycleview.setAdapter(adapter);

        boolean check = promoterManager.isEventEdit || promoterManager.isEventSaveToDraft || promoterManager.isEventRepost;

        if (promoterEventModel != null && check) {
            if (promoterEventModel.getSocialAccountsToMention() != null && !promoterEventModel.getSocialAccountsToMention().isEmpty()) {
                socialAccountList.addAll(promoterEventModel.getSocialAccountsToMention());
            } else {
                SocialAccountsToMentionModel model = new SocialAccountsToMentionModel();
                model.setPlatform("instagram");
                socialAccountList.add(model);
            }
        } else {
            SocialAccountsToMentionModel model = new SocialAccountsToMentionModel();
            model.setPlatform("instagram");
            socialAccountList.add(model);
        }

        binding.title.setText(getValue("social_accounts_to_mention"));
        binding.tvAddMoreOptions.setText(getValue("add_mores_options"));

        adapter.updateData(socialAccountList);

    }

    @Override
    public void setListeners() {

        binding.addMoreLayout.setOnClickListener(v -> {
            selectSocialAccount();
        });

    }

    @Override
    public void populateData(boolean getDataFromServer) {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_promoter_social;
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void selectSocialAccount() {
        Utils.hideKeyboard(requireActivity());
        SocialAccountsToMentionModel model = new SocialAccountsToMentionModel();
        ArrayList<String> data = new ArrayList<>();
        data.add("Instagram");
        data.add("Tiktok");
        data.add("Facebook");
        data.add("Google");
        data.add("Youtube");
        data.add("Snapchat");
        data.add("Website");
        data.add("Whatsapp");
        data.add("Email");
        data.add("Whosin");

        Graphics.showActionSheet(requireActivity(), "Select platform", data, (data1, position) -> {
            switch (position) {
                case 0:
                    model.setPlatform("instagram");
                    break;
                case 1:
                    model.setPlatform("tiktok");
                    break;
                case 2:
                    model.setPlatform("facebook");
                    break;
                case 3:
                    model.setPlatform("google");
                    break;
                case 4:
                    model.setPlatform("youtube");
                    break;
                case 5:
                    model.setPlatform("snapchat");
                    break;
                case 6:
                    model.setPlatform("website");
                    break;
                case 7:
                    model.setPlatform("whatsapp");
                    break;
                case 8:
                    model.setPlatform("email");
                    break;
                case 9:
                    model.setPlatform("whosin");
                    break;
            }

            socialAccountList.add(model);
            adapter.updateData(socialAccountList);

        });
    }


    // endregion
    // --------------------------------------
    // region Public
    // --------------------------------------


    public boolean isDataValid() {

        if (socialAccountList.isEmpty()) {
            Toast.makeText(requireActivity(), getValue("add_soical_account"), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (adapter.getData() != null && !adapter.getData().isEmpty()) {
            for (SocialAccountsToMentionModel model : adapter.getData()) {
                if (Utils.isNullOrEmpty(model.getAccount())) {
                    Toast.makeText(context, getValue("enter_account"), Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (model.getPlatform().equals("whosin")) {
                    if (!validateUrl(model.getAccount())) {
                        Graphics.showAlertDialogWithOkButton(requireActivity(), getString(R.string.app_name), setValue("vaild_url_social_tagging",getString(R.string.app_name)));
                        return false;
                    }

                }
//                if (model.getPlatform().equals("instagram")){
//                    if (!validateUrl(model.getAccount())){
//                        Graphics.showAlertDialogWithOkButton(requireActivity(),"WHOS'IN", "Please enter valid instagram profile link");
//                        return false;
//                    }
//
//                }


            }
        }


        JsonArray socialAccountArray = new JsonArray();
        if (adapter.getData() != null && !adapter.getData().isEmpty()) {
            for (SocialAccountsToMentionModel model : adapter.getData()) {
                JsonObject SocialAccountsToMentionModel = new JsonObject();
                SocialAccountsToMentionModel.addProperty("platform", model.getPlatform());
                SocialAccountsToMentionModel.addProperty("account", model.getAccount());
                SocialAccountsToMentionModel.addProperty("title", model.getTitle());
                socialAccountArray.add(SocialAccountsToMentionModel);
            }
        }

        promoterEventObject.add("socialAccountsToMention", socialAccountArray);

        return true;
    }


    public void saveToDraft() {
        if (!socialAccountList.isEmpty()) {
            JsonArray socialAccountArray = new JsonArray();
            if (adapter.getData() != null && !adapter.getData().isEmpty()) {
                for (SocialAccountsToMentionModel model : adapter.getData()) {
                    JsonObject SocialAccountsToMentionModel = new JsonObject();
                    if (!Utils.isNullOrEmpty(model.getPlatform()) && !Utils.isNullOrEmpty(model.getAccount()) && !Utils.isNullOrEmpty(model.getTitle())) {
                        SocialAccountsToMentionModel.addProperty("platform", model.getPlatform());
                        SocialAccountsToMentionModel.addProperty("account", model.getAccount());
                        SocialAccountsToMentionModel.addProperty("title", model.getTitle());
                        socialAccountArray.add(SocialAccountsToMentionModel);
                    }
                }
            }
            promoterEventObject.add("socialAccountsToMention", socialAccountArray);
        }
    }

    public static boolean validateUrl(String urlString) {
        if (urlString == null || urlString.isEmpty()) {
            return false;
        }

        try {
            URL url = new URL(urlString);

            String scheme = url.getProtocol();
            String host = url.getHost();

            if (scheme != null && host != null &&
                    (scheme.equals("http") || scheme.equals("https") || scheme.equals("ftp") || scheme.equals("file"))) {
                return true;
            } else {
                return false;
            }
        } catch (MalformedURLException e) {
            return false;
        }
    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------


    // --------------------------------------
    // region Adapter
    // --------------------------------------


    private class SocialItemListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_event_social_account));
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

            ViewHolder viewHolder = (ViewHolder) holder;
            SocialAccountsToMentionModel model = (SocialAccountsToMentionModel) getItem(position);

            viewHolder.binding.accountTitle.setText(getValue("enter_title_optional"));

            if (viewHolder.textWatcher != null) {
                viewHolder.binding.socialEditText.removeTextChangedListener(viewHolder.textWatcher);
            }

            if (viewHolder.titleTextWatcher != null) {
                viewHolder.binding.accountTitle.removeTextChangedListener(viewHolder.titleTextWatcher);
            }

            if (!Utils.isNullOrEmpty(model.getAccount())) {
                viewHolder.binding.socialEditText.setText(model.getAccount());
            } else {
                viewHolder.binding.socialEditText.setText("");
            }

            if (!Utils.isNullOrEmpty(model.getTitle())){
                viewHolder.binding.accountTitle.setText(model.getTitle());
            } else {
                viewHolder.binding.accountTitle.setText("");
            }

            viewHolder.binding.btnRemove.setVisibility(View.VISIBLE);

            if (!Utils.isNullOrEmpty(model.getPlatform())) {
                Drawable drawable = Utils.getPlatformIcon(model.getPlatform());
                viewHolder.binding.socialEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null);

                if (model.getPlatform().equalsIgnoreCase("Whosin")) {
                    viewHolder.binding.socialEditText.setHint(model.getPlatform());
                } else {
                    viewHolder.binding.socialEditText.setHint(getValue("enter_your_social_account"));
                }
            } else {
                viewHolder.binding.socialEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
                viewHolder.binding.socialEditText.setHint(getValue("enter_your_social_account"));
            }


            viewHolder.textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() != 0) {
                        model.setAccount(s.toString());
                    } else {
                        model.setAccount("");
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            };

            viewHolder.titleTextWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() != 0) {
                        model.setTitle(s.toString());
                    } else {
                        model.setTitle("");
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            };


            viewHolder.binding.socialEditText.addTextChangedListener(viewHolder.textWatcher);
            viewHolder.binding.accountTitle.addTextChangedListener(viewHolder.titleTextWatcher);

            viewHolder.binding.btnRemove.setOnClickListener(view -> {
                Graphics.showAlertDialogWithOkCancel(requireActivity(), requireActivity().getString(R.string.app_name)
                        , getValue("are_sure_you_want_remove"), getValue("yes"), getValue("no"), aBoolean -> {
                            if (aBoolean) {
                                socialAccountList.remove(position);
                                adapter.updateData(socialAccountList);
                            }
                        });

            });

        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemEventSocialAccountBinding binding;

            private TextWatcher textWatcher;

            private TextWatcher titleTextWatcher;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemEventSocialAccountBinding.bind(itemView);
            }
        }
    }


    // endregion
    // --------------------------------------
}