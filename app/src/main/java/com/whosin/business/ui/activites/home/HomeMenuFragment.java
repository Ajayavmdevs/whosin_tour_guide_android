package com.whosin.business.ui.activites.home;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.whosin.business.R;
import com.whosin.business.comman.Graphics;
import com.whosin.business.comman.Utils;
import com.whosin.business.databinding.ActivityHomeMenuBinding;
import com.whosin.business.service.manager.CheckUserSession;
import com.whosin.business.service.manager.SessionManager;
import com.whosin.business.service.manager.TranslationManager;
import com.whosin.business.ui.activites.Profile.FollowingActivity;
import com.whosin.business.ui.activites.Profile.FollowresActivity;
import com.whosin.business.ui.activites.auth.AuthenticationActivity;
import com.whosin.business.ui.activites.home.privacy.PrivacyPolicyActivity;
import com.whosin.business.ui.activites.setting.BankDetailsActivity;
import com.whosin.business.ui.activites.setting.SettingActivity;
import com.whosin.business.ui.activites.setting.TransactionHistoryActivity;
import com.whosin.business.ui.activites.wallet.WalletActivity;
import com.whosin.business.ui.fragment.comman.BaseFragment;

import java.util.HashMap;
import java.util.Map;

public class HomeMenuFragment extends BaseFragment {

    private ActivityHomeMenuBinding binding;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    public void initUi(View view) {

        binding = ActivityHomeMenuBinding.bind( view );

        applyTranslations();

        binding.avmdevsLlcTv.setClickable(true);

        setupUserData();
    }

    @Override
    public void setListeners() {

        binding.ilLogout.setOnClickListener( v -> Graphics.showAlertDialogWithOkCancel( requireActivity(), getString(R.string.app_name), getValue("logout_confirmation"), aBoolean -> {
            if (aBoolean) {
                //SessionManager.shared.clearSessionData( activity );
                showProgress();
                SessionManager.shared.logout(requireActivity(), (success, error) -> {
                    hideProgress();
                    if (!Utils.isNullOrEmpty(error)) {
                        Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    startActivity( new Intent( requireActivity(), AuthenticationActivity.class ).addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK ).addFlags( Intent.FLAG_ACTIVITY_NEW_TASK ) );
                });

            }
        } ) );


        binding.ilWallet.setOnClickListener( v -> startActivity( new Intent( requireActivity(), WalletActivity.class ) ));

        Glide.with( requireActivity() ).load( R.drawable.icon_left_back_arrow ).into( binding.ivClose );
//        binding.ivClose.setOnClickListener( view -> onBackPressed());

        binding.ilSettings.setOnClickListener( view -> startActivity( new Intent( requireActivity(), SettingActivity.class ) ));

        binding.ilClaimHistory.setOnClickListener( view -> startActivity( new Intent( requireActivity(), TransactionHistoryActivity.class ) ));

        binding.ivBankDetailView.setOnClickListener( view -> startActivity( new Intent( requireActivity(), BankDetailsActivity.class ) ));

        binding.ilFollowing.setOnClickListener( view -> startActivity( new Intent( requireActivity(), FollowingActivity.class ).putExtra( "id", SessionManager.shared.getUser().getId() ) ));

        binding.ilFollowers.setOnClickListener( view -> startActivity( new Intent( requireActivity(), FollowresActivity.class ).putExtra( "id", SessionManager.shared.getUser().getId() ) ) );

        binding.ilContact.setOnClickListener( v -> startActivity( new Intent( requireActivity(), ContactUsActivity.class ) ));

        binding.ilPrivacyPolicy.setOnClickListener( v -> startActivity( new Intent( requireActivity(), PrivacyPolicyActivity.class ).putExtra( "type", "Privacy Policy" ) ));

        binding.roundTerms.setOnClickListener( view -> startActivity( new Intent( requireActivity(), PrivacyPolicyActivity.class ).putExtra( "type", "Terms & Condition" ) ));

        binding.ilFriend.setOnClickListener( view -> Utils.openShareDialog( requireActivity() ));

        binding.ilBundle.setVisibility(SessionManager.shared.getUser().isMembershipActive() ? View.VISIBLE : View.GONE);

        binding.avmdevsLlcTv.setOnClickListener(v -> {
            String url = "https://avmdevs.com/";
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
        return R.layout.activity_home_menu; }

//    @Override
//    protected View getLayoutView() {
//        binding = ActivityHomeMenuBinding.inflate( getLayoutInflater() );
//        return binding.getRoot();
//    }

    @Override
    public void onResume() {
        super.onResume();
        CheckUserSession.checkSessionAndProceed(requireActivity(), this::requestUserDetail);
    }

    @Override
    protected Map<View, String> getTranslationMap() {
        Map<View, String> map = new HashMap<>();
        map.put(binding.tvSettingTitle, "settings");
        map.put(binding.tvClaimHistoryTitle, "Transaction History");
        map.put(binding.tvBankDetailTitle, "Bank Details");
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
        SessionManager.shared.getCurrentUserProfile( requireActivity(), (success, error) -> {
            if (!Utils.isNullOrEmpty( error )) {
                Toast.makeText( requireActivity(), error, Toast.LENGTH_SHORT ).show();
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