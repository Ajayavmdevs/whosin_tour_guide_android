package com.whosin.app.ui.fragment.home;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ItemSubscriptionDeatailRecyclerBinding;
import com.whosin.app.databinding.SubscriptionPlanBottomSheetBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.FeatureModel;
import com.whosin.app.service.models.InviteFriendModel;
import com.whosin.app.service.models.MemberShipModel;
import com.whosin.app.service.models.PaymentCredentialModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.comman.BetterActivityResult;
import com.whosin.app.ui.activites.home.activity.BuyPackagePlanActivity;
import com.whosin.app.ui.activites.wallet.RedeemActivity;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionPlanBottomSheet extends DialogFragment {

    private SubscriptionPlanBottomSheetBinding binding;

    public CommanCallback<InviteFriendModel> callback;
    public List<MemberShipModel> memberShipList = new ArrayList<>();
    private MemberShipModel memberShipModel;
    private FeatureDetailsAdapter<FeatureModel> adapter = new FeatureDetailsAdapter();
    protected final BetterActivityResult<Intent, ActivityResult> activityLauncher = BetterActivityResult.registerActivityForResult(this);

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);
        initUi(view);
        setListener();
        return view;
    }

    public void initUi(View view) {
        binding = SubscriptionPlanBottomSheetBinding.bind(view);
        binding.featureRecycler.setLayoutManager( new LinearLayoutManager( requireContext(), LinearLayoutManager.VERTICAL, false ) );
        binding.featureRecycler.setAdapter( adapter );
        requestMembershipPackageDetail( memberShipList.get(0).getId());

    }

    public void setListener() {
        binding.ivClose.setOnClickListener(view -> dismiss());

        binding.getNowBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), BuyPackagePlanActivity.class);
            intent.putExtra("membershipModel", new Gson().toJson(memberShipModel));
            activityLauncher.launch( intent, result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    boolean isClose = result.getData().getBooleanExtra("close",false);
                    if (isClose) {
                         dismiss();
                    }
                }
            } );
        });

        binding.layoutInfo.setOnClickListener(v -> {
            Utils.preventDoubleClick( v );
            MembershipInfoBottomSheet membershipInfoBottomSheet = new MembershipInfoBottomSheet();
            membershipInfoBottomSheet.model = memberShipModel;
            membershipInfoBottomSheet.show(getChildFragmentManager(), "1");
        });

    }

    public int getLayoutRes() {
        return R.layout.subscription_plan_bottom_sheet;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogThemeNoFloating);
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
            FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            ViewGroup.LayoutParams layoutParam = bottomSheet.getLayoutParams();
            layoutParam.height = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParam.width = ViewGroup.LayoutParams.MATCH_PARENT;
            bottomSheet.setLayoutParams(layoutParam);
            if (bottomSheet != null) {
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        return dialog;
    }

    // endregion
    // --------------------------------------
    // region private
    // --------------------------------------

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestMembershipPackageDetail(String packageId) {
        Graphics.showProgress(getActivity());
        DataService.shared(getActivity()).requestMembershipPackageDetail(packageId, new RestCallback<ContainerModel<MemberShipModel>>(this) {
            @Override
            public void result(ContainerModel<MemberShipModel> model, String error) {
                Graphics.hideProgress(getActivity());
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.getData() != null) {
                    memberShipModel = model.getData();
                    setDetail();
                }
            }
        });
    }

    private void setDetail() {
        if (memberShipModel != null) {
            adapter.updateData(memberShipModel.getFeatures());
            binding.tvHeaderTitle.setText(memberShipModel.getTitle());
            binding.tvTitle.setText(memberShipModel.getDescription());
            binding.tvSubTitle.setText(memberShipModel.getDescription());
            binding.tvDiscount.setText(memberShipModel.getDiscountText());
            binding.tvPackageTitle.setText(memberShipModel.getTitle());
            binding.tvPrice.setText("AED " + memberShipModel.getActualPrice() + " / " + memberShipModel.getTime());
        }
    }

    // --------------------------------------
    // region Adapter
    // --------------------------------------

    public class FeatureDetailsAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder( UiUtils.getViewBy( parent, R.layout.item_subscription_deatail_recycler ) );
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            FeatureModel model = (FeatureModel) getItem( position );
            Graphics.loadImage(model.getIcon(),viewHolder.binding.imgFeature);
            viewHolder.binding.tvTitle.setText( model.getFeature() );

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private ItemSubscriptionDeatailRecyclerBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super( itemView );
                binding = ItemSubscriptionDeatailRecyclerBinding.bind( itemView );
            }
        }
    }


}