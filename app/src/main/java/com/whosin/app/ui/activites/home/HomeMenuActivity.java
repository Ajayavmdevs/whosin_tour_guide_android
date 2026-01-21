package com.whosin.app.ui.activites.home;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.whosin.app.R;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Preferences;
import com.whosin.app.comman.Utils;
import com.whosin.app.databinding.ActivityHomeMenuBinding;
import com.whosin.app.service.manager.CheckUserSession;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.manager.TranslationManager;
import com.whosin.app.ui.activites.Profile.FollowingActivity;
import com.whosin.app.ui.activites.Profile.FollowresActivity;
import com.whosin.app.ui.activites.auth.AuthenticationActivity;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.activites.home.privacy.PrivacyPolicyActivity;
import com.whosin.app.ui.activites.offers.ClaimHistoryActivity;
import com.whosin.app.ui.activites.setting.SettingActivity;
import com.whosin.app.ui.activites.venue.ui.SubscriptionPlanActivity;
import com.whosin.app.ui.activites.wallet.WalletActivity;

import java.util.HashMap;
import java.util.Map;

public class HomeMenuActivity extends BaseActivity {

    private ActivityHomeMenuBinding binding;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    protected void initUi() {

        applyTranslations();

        binding.avmdevsLlcTv.setClickable(true);

        setupUserData();

    }

    @Override
    protected void setListeners() {

        binding.ilLogout.setOnClickListener( v -> Graphics.showAlertDialogWithOkCancel( activity, getString(R.string.app_name), getValue("logout_confirmation"), aBoolean -> {
            if (aBoolean) {
                //SessionManager.shared.clearSessionData( activity );
                showProgress();
                SessionManager.shared.logout(activity, (success, error) -> {
                    hideProgress();
                    if (!Utils.isNullOrEmpty(error)) {
                        Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    startActivity( new Intent( activity, AuthenticationActivity.class ).addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK ).addFlags( Intent.FLAG_ACTIVITY_NEW_TASK ) );
                    finish();
                });

            }
        } ) );


        binding.ilWallet.setOnClickListener( v -> startActivity( new Intent( activity, WalletActivity.class ) ));

        Glide.with( activity ).load( R.drawable.icon_left_back_arrow ).into( binding.ivClose );
        binding.ivClose.setOnClickListener( view -> onBackPressed());

        binding.ilSettings.setOnClickListener( view -> startActivity( new Intent( activity, SettingActivity.class ) ));

        binding.ilClaimHistory.setOnClickListener( view -> startActivity( new Intent( activity, ClaimHistoryActivity.class ) ));

        binding.ilFollowing.setOnClickListener( view -> startActivity( new Intent( activity, FollowingActivity.class ).putExtra( "id", SessionManager.shared.getUser().getId() ) ));

        binding.ilFollowers.setOnClickListener( view -> startActivity( new Intent( activity, FollowresActivity.class ).putExtra( "id", SessionManager.shared.getUser().getId() ) ) );

        binding.ilContact.setOnClickListener( v -> startActivity( new Intent( activity, ContactUsActivity.class ) ));

        binding.ilPrivacyPolicy.setOnClickListener( v -> startActivity( new Intent( activity, PrivacyPolicyActivity.class ).putExtra( "type", "Privacy Policy" ) ));

        binding.roundTerms.setOnClickListener( view -> startActivity( new Intent( activity, PrivacyPolicyActivity.class ).putExtra( "type", "Terms & Condition" ) ));

        binding.ilFriend.setOnClickListener( view -> Utils.openShareDialog( activity ));

        binding.ilBundle.setVisibility(SessionManager.shared.getUser().isMembershipActive() ? View.VISIBLE : View.GONE);

        binding.ilBundle.setOnClickListener( view -> startActivity( new Intent( activity, SubscriptionPlanActivity.class )));

        binding.avmdevsLlcTv.setOnClickListener(v -> {
            String url = "https://avmdevs.com/";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });

    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityHomeMenuBinding.inflate( getLayoutInflater() );
        return binding.getRoot();
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckUserSession.checkSessionAndProceed(activity, this::requestUserDetail);
    }

    @Override
    protected Map<View, String> getTranslationMap() {
        Map<View, String> map = new HashMap<>();
        map.put(binding.tvSettingTitle, "settings");
        map.put(binding.tvClaimHistoryTitle, "claim_history");
        map.put(binding.tvWalletTitle, "wallet");
        map.put(binding.tvMySubscriptionTitle, "my_subscription");
        map.put(binding.tvInviteYourFriendTitle, "invite_a_friend");
        map.put(binding.tvContactUsTitle, "contact_us");
        map.put(binding.tvFollowersTitle, "followers");
        map.put(binding.tvFollowingTitle, "following");
        map.put(binding.tvPrivacyPolicyTitle, "privacy_policy");
        map.put(binding.tvTermsConditionTitle, "terms_condition");
        map.put(binding.tvLogoutTitle, "logout");
        return map;
    }



    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void setupUserData() {
        binding.tvName.setText(String.format(TranslationManager.shared.get("hey") + ", " + SessionManager.shared.getUser().getFirstName()));
        Graphics.loadImageWithFirstLetter( SessionManager.shared.getUser().getImage(), binding.imageProfile, SessionManager.shared.getUser().getFullName() );
        binding.vipImage.setVisibility(!SessionManager.shared.getUser().isVip() ? View.GONE : View.VISIBLE);
        binding.tvFollowers.setText( String.valueOf( SessionManager.shared.getUser().getFollower() ) );
        binding.tvFollowing.setText( String.valueOf( SessionManager.shared.getUser().getFollowing() ) );

    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestUserDetail() {
        SessionManager.shared.getCurrentUserProfile( this, (success, error) -> {
            if (!Utils.isNullOrEmpty( error )) {
                Toast.makeText( activity, error, Toast.LENGTH_SHORT ).show();
                return;
            }
            setupUserData();
        });
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    // endregion
    // --------------------------------------

}