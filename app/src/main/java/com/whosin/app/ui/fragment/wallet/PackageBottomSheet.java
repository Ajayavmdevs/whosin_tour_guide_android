package com.whosin.app.ui.fragment.wallet;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ItemBottomSheetPackageBinding;
import com.whosin.app.databinding.PackageBottomSheetBinding;
import com.whosin.app.service.models.MyWalletModel;
import com.whosin.app.service.models.PackageModel;
import com.whosin.app.ui.activites.comman.BetterActivityResult;
import com.whosin.app.ui.activites.home.Chat.PreviewChatWallpaperActivity;
import com.whosin.app.ui.activites.wallet.RedeemActivity;


public class PackageBottomSheet extends DialogFragment {

    private PackageBottomSheetBinding binding;
    private final PackageAdapter<PackageModel> packageAdapter = new PackageAdapter<>();
    protected final BetterActivityResult<Intent, ActivityResult> activityLauncher = BetterActivityResult.registerActivityForResult(this);
    public CommanCallback<Boolean> callback;
    public MyWalletModel myWalletModel;
    public String type = "";



    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);
        initUi(view);
        setListener();
        return view;

    }

    public void initUi(View view) {
        binding = PackageBottomSheetBinding.bind(view);

        binding.packageRecycler.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
        binding.packageRecycler.setAdapter(packageAdapter);

        if (myWalletModel.getType().equals("event") && !myWalletModel.getEvent().getPackages().isEmpty()) {
            packageAdapter.updateData(myWalletModel.getEvent().getPackages());
        } else if (myWalletModel.getType().equals("offer") && !myWalletModel.getOffer().getPackages().isEmpty()) {
            packageAdapter.updateData(myWalletModel.getOffer().getPackages());
        }

    }

    public void setListener() {
        Glide.with(requireActivity()).load(R.drawable.icon_close_btn).into(binding.ivClose);
        binding.ivClose.setOnClickListener(view -> dismiss());
    }

    public int getLayoutRes() {
        return R.layout.package_bottom_sheet;
    }


    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogThemeNoFloating);
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    private class PackageAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_bottom_sheet_package));
        }

        @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            PackageModel model = (PackageModel) getItem(position);
            viewHolder.mBinding.tvName.setText(Utils.notNullString(model.getTitle()));
            if (!TextUtils.isEmpty(model.getDescription())) {
                viewHolder.mBinding.tvDescription.setText(Utils.notNullString(model.getDescription()));
                viewHolder.mBinding.tvDescription.setVisibility(View.VISIBLE);
            } else {
                viewHolder.mBinding.tvDescription.setVisibility(View.GONE);
            }
            String modifiedString = model.getDiscount().contains("%") ? model.getDiscount() : model.getDiscount() + "%";
            if (model.getDiscount().equals("0")) {
                viewHolder.mBinding.tvDiscount.setVisibility(View.GONE);
            } else {
                viewHolder.mBinding.tvDiscount.setVisibility(View.VISIBLE);
                viewHolder.mBinding.tvDiscount.setText(Utils.notNullString(modifiedString));
            }
            viewHolder.mBinding.tvDiscountPrice.setText("AED " + Utils.notNullString(model.getAmount()));

            viewHolder.mBinding.tvQty.setText(String.valueOf(model.getRemainingQty()));

            viewHolder.itemView.setOnClickListener(v -> {
                if (type.equals("redeem")) {
                    //startActivity(new Intent(getActivity(), RedeemActivity.class).putExtra("itemList", new Gson().toJson(myWalletModel)).putExtra("packageModel", new Gson().toJson(model)));
                    Intent intent = new Intent(getActivity(), RedeemActivity.class);
                    intent.putExtra("itemList",new Gson().toJson(myWalletModel));
                    intent.putExtra("packageModel", new Gson().toJson(model));
                    activityLauncher.launch( intent, result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            boolean isClose = result.getData().getBooleanExtra("close",false);
                            if (isClose) {
                                 dismiss();
                                if (callback != null) {
                                    callback.onReceive(true);
                                }
                            }
                        }
                    } );
                }
//                else {
//                    SendGiftBottomSheet sendGiftBottomSheet = new SendGiftBottomSheet();
//                    sendGiftBottomSheet.myWalletModel = myWalletModel;
//                    sendGiftBottomSheet.packageModel = model;
//                    sendGiftBottomSheet.callback = data -> {
//                        dismiss();
//                        if (callback != null) {
//                            callback.onReceive(true);
//                        }
//                    };
//                    sendGiftBottomSheet.show(getChildFragmentManager(), "1");
//                }
            });
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemBottomSheetPackageBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemBottomSheetPackageBinding.bind(itemView);
            }
        }

    }

}